package com.finova.dto.budget;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public class BudgetRequest {



    @NotBlank(message = "Budget name is required")
    private String name;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be valid")
    private Integer year;

    @NotNull(message = "Total budget limit is required")
    @Positive(message = "Total budget limit must be positive")
    private BigDecimal totalBudgetLimit;

    private List<BudgetCategoryDto> categories;


    public BudgetRequest() {}

    public BudgetRequest(String name, Integer month, Integer year, BigDecimal totalBudgetLimit, List<BudgetCategoryDto> categories) {
        this.name = name;
        this.month = month;
        this.year = year;
        this.totalBudgetLimit = totalBudgetLimit;
        this.categories = categories;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMonth() {
        return this.month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getTotalBudgetLimit() {
        return this.totalBudgetLimit;
    }

    public void setTotalBudgetLimit(BigDecimal totalBudgetLimit) {
        this.totalBudgetLimit = totalBudgetLimit;
    }

    public List<BudgetCategoryDto> getCategories() {
        return this.categories;
    }

    public void setCategories(List<BudgetCategoryDto> categories) {
        this.categories = categories;
    }

    public static BudgetRequestBuilder builder() {
        return new BudgetRequestBuilder();
    }

    public static class BudgetRequestBuilder {
        private String name;
        private Integer month;
        private Integer year;
        private BigDecimal totalBudgetLimit;
        private List<BudgetCategoryDto> categories;

        public BudgetRequestBuilder() {}

        public BudgetRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BudgetRequestBuilder month(Integer month) {
            this.month = month;
            return this;
        }

        public BudgetRequestBuilder year(Integer year) {
            this.year = year;
            return this;
        }

        public BudgetRequestBuilder totalBudgetLimit(BigDecimal totalBudgetLimit) {
            this.totalBudgetLimit = totalBudgetLimit;
            return this;
        }

        public BudgetRequestBuilder categories(List<BudgetCategoryDto> categories) {
            this.categories = categories;
            return this;
        }

        public BudgetRequest build() {
            BudgetRequest instance = new BudgetRequest();
            instance.setName(this.name);
            instance.setMonth(this.month);
            instance.setYear(this.year);
            instance.setTotalBudgetLimit(this.totalBudgetLimit);
            instance.setCategories(this.categories);
            return instance;
        }
    }
}
