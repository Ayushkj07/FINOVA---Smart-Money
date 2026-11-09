package com.finova.dto.features;

import java.math.BigDecimal;
import java.util.List;

public class ExpensePredictorResponse {

    public static class CategoryPrediction {
        private String category;
        private BigDecimal historicalAverage;
        private BigDecimal predictedAmount;
        private Double trendPercentage;
        private String reason;

        public CategoryPrediction() {}
        public CategoryPrediction(String category, BigDecimal historicalAverage, BigDecimal predictedAmount, Double trendPercentage, String reason) {
            this.category = category;
            this.historicalAverage = historicalAverage;
            this.predictedAmount = predictedAmount;
            this.trendPercentage = trendPercentage;
            this.reason = reason;
        }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public BigDecimal getHistoricalAverage() { return historicalAverage; }
        public void setHistoricalAverage(BigDecimal historicalAverage) { this.historicalAverage = historicalAverage; }
        public BigDecimal getPredictedAmount() { return predictedAmount; }
        public void setPredictedAmount(BigDecimal predictedAmount) { this.predictedAmount = predictedAmount; }
        public Double getTrendPercentage() { return trendPercentage; }
        public void setTrendPercentage(Double trendPercentage) { this.trendPercentage = trendPercentage; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public static CategoryPredictionBuilder builder() { return new CategoryPredictionBuilder(); }
        public static class CategoryPredictionBuilder {
            private String category;
            private BigDecimal historicalAverage;
            private BigDecimal predictedAmount;
            private Double trendPercentage;
            private String reason;

            public CategoryPredictionBuilder category(String category) { this.category = category; return this; }
            public CategoryPredictionBuilder historicalAverage(BigDecimal historicalAverage) { this.historicalAverage = historicalAverage; return this; }
            public CategoryPredictionBuilder predictedAmount(BigDecimal predictedAmount) { this.predictedAmount = predictedAmount; return this; }
            public CategoryPredictionBuilder trendPercentage(Double trendPercentage) { this.trendPercentage = trendPercentage; return this; }
            public CategoryPredictionBuilder reason(String reason) { this.reason = reason; return this; }
            public CategoryPrediction build() { return new CategoryPrediction(category, historicalAverage, predictedAmount, trendPercentage, reason); }
        }
    }

    private BigDecimal totalPredictedExpense;
    private BigDecimal currentMonthSpending;
    private Double projectedIncreasePercentage;
    private List<CategoryPrediction> categoryPredictions;
    private String methodologyNote;
    private String disclaimer;

    public ExpensePredictorResponse() {}
    public ExpensePredictorResponse(BigDecimal totalPredictedExpense, BigDecimal currentMonthSpending, Double projectedIncreasePercentage, List<CategoryPrediction> categoryPredictions, String methodologyNote, String disclaimer) {
        this.totalPredictedExpense = totalPredictedExpense;
        this.currentMonthSpending = currentMonthSpending;
        this.projectedIncreasePercentage = projectedIncreasePercentage;
        this.categoryPredictions = categoryPredictions;
        this.methodologyNote = methodologyNote;
        this.disclaimer = disclaimer;
    }

    public BigDecimal getTotalPredictedExpense() { return totalPredictedExpense; }
    public void setTotalPredictedExpense(BigDecimal totalPredictedExpense) { this.totalPredictedExpense = totalPredictedExpense; }
    public BigDecimal getCurrentMonthSpending() { return currentMonthSpending; }
    public void setCurrentMonthSpending(BigDecimal currentMonthSpending) { this.currentMonthSpending = currentMonthSpending; }
    public Double getProjectedIncreasePercentage() { return projectedIncreasePercentage; }
    public void setProjectedIncreasePercentage(Double projectedIncreasePercentage) { this.projectedIncreasePercentage = projectedIncreasePercentage; }
    public List<CategoryPrediction> getCategoryPredictions() { return categoryPredictions; }
    public void setCategoryPredictions(List<CategoryPrediction> categoryPredictions) { this.categoryPredictions = categoryPredictions; }
    public String getMethodologyNote() { return methodologyNote; }
    public void setMethodologyNote(String methodologyNote) { this.methodologyNote = methodologyNote; }
    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }

    public static ExpensePredictorResponseBuilder builder() { return new ExpensePredictorResponseBuilder(); }
    public static class ExpensePredictorResponseBuilder {
        private BigDecimal totalPredictedExpense;
        private BigDecimal currentMonthSpending;
        private Double projectedIncreasePercentage;
        private List<CategoryPrediction> categoryPredictions;
        private String methodologyNote;
        private String disclaimer;

        public ExpensePredictorResponseBuilder totalPredictedExpense(BigDecimal totalPredictedExpense) { this.totalPredictedExpense = totalPredictedExpense; return this; }
        public ExpensePredictorResponseBuilder currentMonthSpending(BigDecimal currentMonthSpending) { this.currentMonthSpending = currentMonthSpending; return this; }
        public ExpensePredictorResponseBuilder projectedIncreasePercentage(Double projectedIncreasePercentage) { this.projectedIncreasePercentage = projectedIncreasePercentage; return this; }
        public ExpensePredictorResponseBuilder categoryPredictions(List<CategoryPrediction> categoryPredictions) { this.categoryPredictions = categoryPredictions; return this; }
        public ExpensePredictorResponseBuilder methodologyNote(String methodologyNote) { this.methodologyNote = methodologyNote; return this; }
        public ExpensePredictorResponseBuilder disclaimer(String disclaimer) { this.disclaimer = disclaimer; return this; }
        public ExpensePredictorResponse build() { return new ExpensePredictorResponse(totalPredictedExpense, currentMonthSpending, projectedIncreasePercentage, categoryPredictions, methodologyNote, disclaimer); }
    }
}
