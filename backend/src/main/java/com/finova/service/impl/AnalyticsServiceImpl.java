package com.finova.service.impl;

import com.finova.entity.Budget;
import com.finova.entity.Expense;
import com.finova.entity.Income;
import com.finova.entity.User;
import com.finova.repository.*;
import com.finova.service.AnalyticsService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getFinancialSummaryReport(Long userId, String period, LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        LocalDate start = startDate != null ? startDate : today.withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : today.withDayOfMonth(today.lengthOfMonth());

        if (period != null) {
            if ("WEEKLY".equalsIgnoreCase(period)) {
                start = today.minusDays(today.getDayOfWeek().getValue() - 1);
                end = today;
            } else if ("QUARTERLY".equalsIgnoreCase(period)) {
                start = today.minusMonths(3).withDayOfMonth(1);
                end = today;
            } else if ("YEARLY".equalsIgnoreCase(period)) {
                start = today.withDayOfYear(1);
                end = today;
            }
        }

        BigDecimal totalIncome = incomeRepository.sumAmountByUserIdAndDateBetween(userId, start, end);
        BigDecimal totalExpenses = expenseRepository.sumAmountByUserIdAndDateBetween(userId, start, end);
        BigDecimal netSavings = totalIncome.subtract(totalExpenses).max(BigDecimal.ZERO);
        BigDecimal totalSavings = savingsGoalRepository.sumTotalSavingsByUserId(userId);
        BigDecimal outstandingDebt = debtRepository.sumTotalBorrowedDebtByUserId(userId);

        long daysBetween = Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1);
        BigDecimal avgDailySpending = totalExpenses.divide(BigDecimal.valueOf(daysBetween), 2, RoundingMode.HALF_UP);

        double savingsRate = 0.0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = netSavings.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        List<Object[]> topCategories = expenseRepository.sumAmountGroupByCategoryAndDateBetween(userId, start, end);
        String highestCategory = "N/A";
        BigDecimal highestCategoryAmount = BigDecimal.ZERO;
        for (Object[] cat : topCategories) {
            BigDecimal amt = (BigDecimal) cat[1];
            if (amt.compareTo(highestCategoryAmount) > 0) {
                highestCategoryAmount = amt;
                highestCategory = (String) cat[0];
            }
        }

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("period", period != null ? period : "CUSTOM");
        report.put("startDate", start.toString());
        report.put("endDate", end.toString());
        report.put("totalIncome", totalIncome);
        report.put("totalExpenses", totalExpenses);
        report.put("netSavings", netSavings);
        report.put("savingsRate", Math.round(savingsRate * 10.0) / 10.0);
        report.put("averageDailySpending", avgDailySpending);
        report.put("highestSpendingCategory", highestCategory);
        report.put("highestCategoryAmount", highestCategoryAmount);
        report.put("accumulatedTotalSavings", totalSavings);
        report.put("outstandingDebt", outstandingDebt);
        report.put("categoryBreakdown", topCategories);

        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportCsvTransactions(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusMonths(3);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end);
        List<Income> incomes = incomeRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end);

        StringBuilder csv = new StringBuilder();
        csv.append("Date,Type,Title,Category,Payment Method,Amount,Description\n");

        for (Income inc : incomes) {
            csv.append(String.format("%s,INCOME,\"%s\",\"%s\",%s,%.2f,\"%s\"\n",
                    inc.getDate(),
                    escapeCsv(inc.getTitle()),
                    escapeCsv(inc.getCategory()),
                    inc.getPaymentMethod(),
                    inc.getAmount(),
                    escapeCsv(inc.getDescription())));
        }

        for (Expense exp : expenses) {
            csv.append(String.format("%s,EXPENSE,\"%s\",\"%s\",%s,-%.2f,\"%s\"\n",
                    exp.getDate(),
                    escapeCsv(exp.getTitle()),
                    escapeCsv(exp.getCategoryName()),
                    exp.getPaymentMethod(),
                    exp.getAmount(),
                    escapeCsv(exp.getDescription())));
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportExcelReport(Long userId, String period) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = today.withDayOfMonth(today.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end);
        List<Income> incomes = incomeRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Summary Sheet
            Sheet summarySheet = workbook.createSheet("Financial Summary");
            org.apache.poi.ss.usermodel.Row headerRow = summarySheet.createRow(0);
            headerRow.createCell(0).setCellValue("Metric");
            headerRow.createCell(1).setCellValue("Value");

            BigDecimal totalIncome = incomeRepository.sumAmountByUserIdAndDateBetween(userId, start, end);
            BigDecimal totalExpense = expenseRepository.sumAmountByUserIdAndDateBetween(userId, start, end);
            BigDecimal net = totalIncome.subtract(totalExpense);

            Object[][] summaryData = {
                    {"Report Period", start + " to " + end},
                    {"Total Inflow", totalIncome.doubleValue()},
                    {"Total Outflow", totalExpense.doubleValue()},
                    {"Net Cash Flow", net.doubleValue()},
                    {"Total Savings Reserve", savingsGoalRepository.sumTotalSavingsByUserId(userId).doubleValue()},
                    {"Total Outstanding Debt", debtRepository.sumTotalBorrowedDebtByUserId(userId).doubleValue()}
            };

            int rowIdx = 1;
            for (Object[] r : summaryData) {
                org.apache.poi.ss.usermodel.Row row = summarySheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(String.valueOf(r[0]));
                if (r[1] instanceof Number) {
                    row.createCell(1).setCellValue(((Number) r[1]).doubleValue());
                } else {
                    row.createCell(1).setCellValue(String.valueOf(r[1]));
                }
            }

            // Transactions Sheet
            Sheet txSheet = workbook.createSheet("Transactions");
            org.apache.poi.ss.usermodel.Row txHeader = txSheet.createRow(0);
            String[] headers = {"Date", "Type", "Title", "Category", "Amount", "Payment Method"};
            for (int i = 0; i < headers.length; i++) {
                txHeader.createCell(i).setCellValue(headers[i]);
            }

            int txRowIdx = 1;
            for (Income inc : incomes) {
                org.apache.poi.ss.usermodel.Row r = txSheet.createRow(txRowIdx++);
                r.createCell(0).setCellValue(inc.getDate().toString());
                r.createCell(1).setCellValue("INCOME");
                r.createCell(2).setCellValue(inc.getTitle());
                r.createCell(3).setCellValue(inc.getCategory());
                r.createCell(4).setCellValue(inc.getAmount().doubleValue());
                r.createCell(5).setCellValue(inc.getPaymentMethod().name());
            }

            for (Expense exp : expenses) {
                org.apache.poi.ss.usermodel.Row r = txSheet.createRow(txRowIdx++);
                r.createCell(0).setCellValue(exp.getDate().toString());
                r.createCell(1).setCellValue("EXPENSE");
                r.createCell(2).setCellValue(exp.getTitle());
                r.createCell(3).setCellValue(exp.getCategoryName());
                r.createCell(4).setCellValue(-exp.getAmount().doubleValue());
                r.createCell(5).setCellValue(exp.getPaymentMethod().name());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate Excel report: ", e);
            return new byte[0];
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportPdfReport(Long userId, String period) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = today.withDayOfMonth(today.lengthOfMonth());

        User user = userRepository.findById(userId).orElse(null);
        String studentName = user != null ? user.getFullName() : "Student";
        String currency = (user != null && user.getProfile() != null && user.getProfile().getCurrency() != null)
                ? user.getProfile().getCurrency() : "INR";

        BigDecimal totalIncome = incomeRepository.sumAmountByUserIdAndDateBetween(userId, start, end);
        BigDecimal totalExpense = expenseRepository.sumAmountByUserIdAndDateBetween(userId, start, end);
        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // FINOVA Brand Header
            com.lowagie.text.Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(155, 135, 245));
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(41, 39, 53));
            com.lowagie.text.Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(133, 131, 146));
            com.lowagie.text.Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(41, 39, 53));

            Paragraph header = new Paragraph("FINOVA", brandFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph tagline = new Paragraph("Smart Money. Smarter Future. — Student Financial Statement", regularFont);
            tagline.setAlignment(Element.ALIGN_CENTER);
            tagline.setSpacingAfter(20);
            document.add(tagline);

            // Student Meta
            document.add(new Paragraph(String.format("Student: %s  |  Period: %s to %s", studentName, start, end), boldFont));
            document.add(new Paragraph("Generated On: " + LocalDate.now() + "  |  Currency: " + currency, regularFont));
            document.add(new Paragraph(" "));

            // Financial Summary Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(20);

            addTableCell(table, "Total Monthly Inflow", true);
            addTableCell(table, currency + " " + totalIncome.setScale(2, RoundingMode.HALF_UP), false);

            addTableCell(table, "Total Monthly Outflow", true);
            addTableCell(table, currency + " " + totalExpense.setScale(2, RoundingMode.HALF_UP), false);

            addTableCell(table, "Net Period Savings", true);
            addTableCell(table, currency + " " + netSavings.setScale(2, RoundingMode.HALF_UP), false);

            addTableCell(table, "Savings Reserve Accumulation", true);
            addTableCell(table, currency + " " + savingsGoalRepository.sumTotalSavingsByUserId(userId), false);

            addTableCell(table, "Active Borrowed Balance", true);
            addTableCell(table, currency + " " + debtRepository.sumTotalBorrowedDebtByUserId(userId), false);

            document.add(table);

            // Expense Breakdown Table
            Paragraph expSection = new Paragraph("Recent Major Expenses", titleFont);
            expSection.setSpacingAfter(10);
            document.add(expSection);

            PdfPTable expTable = new PdfPTable(4);
            expTable.setWidthPercentage(100);
            expTable.setWidths(new float[]{2, 4, 3, 3});

            addTableHeadCell(expTable, "Date");
            addTableHeadCell(expTable, "Title");
            addTableHeadCell(expTable, "Category");
            addTableHeadCell(expTable, "Amount");

            List<Expense> expenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end);
            int count = 0;
            for (Expense e : expenses) {
                if (count++ >= 15) break; // top 15 in statement
                addTableCell(expTable, e.getDate().toString(), false);
                addTableCell(expTable, e.getTitle(), false);
                addTableCell(expTable, e.getCategoryName(), false);
                addTableCell(expTable, currency + " " + e.getAmount(), false);
            }
            document.add(expTable);

            // Footer note
            Paragraph footer = new Paragraph("\nConfidential student financial record issued by FINOVA Personal Finance Platform.", regularFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF report: ", e);
            return new byte[0];
        }
    }

    private void addTableCell(PdfPTable table, String text, boolean isBold) {
        com.lowagie.text.Font font = isBold ?
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(41, 39, 53)) :
                FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(133, 131, 146));
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setBorderColor(new Color(238, 238, 242));
        table.addCell(cell);
    }

    private void addTableHeadCell(PdfPTable table, String text) {
        com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(155, 135, 245));
        cell.setPadding(8);
        cell.setBorderColor(new Color(155, 135, 245));
        table.addCell(cell);
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace("\"", "\"\"");
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    public AnalyticsServiceImpl(IncomeRepository incomeRepository, ExpenseRepository expenseRepository, BudgetRepository budgetRepository, SavingsGoalRepository savingsGoalRepository, DebtRepository debtRepository, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
    }

}
