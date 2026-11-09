package com.finova.dto.dashboard;

import java.util.List;

public class HealthScoreResponse {


    private int overallScore; // 0 - 100
    private String grade; // e.g. "Excellent", "Good", "Fair", "Needs Attention"
    private int savingsRateScore; // 0 - 20
    private int budgetDisciplineScore; // 0 - 20
    private int spendingConsistencyScore; // 0 - 20
    private int debtManagementScore; // 0 - 20
    private int emergencySavingsScore; // 0 - 20
    private List<String> suggestions;
    private List<String> strengths;
    private String educationalDisclaimer;


    public HealthScoreResponse() {}

    public HealthScoreResponse(int overallScore, String grade, int savingsRateScore, int budgetDisciplineScore, int spendingConsistencyScore, int debtManagementScore, int emergencySavingsScore, List<String> suggestions, List<String> strengths, String educationalDisclaimer) {
        this.overallScore = overallScore;
        this.grade = grade;
        this.savingsRateScore = savingsRateScore;
        this.budgetDisciplineScore = budgetDisciplineScore;
        this.spendingConsistencyScore = spendingConsistencyScore;
        this.debtManagementScore = debtManagementScore;
        this.emergencySavingsScore = emergencySavingsScore;
        this.suggestions = suggestions;
        this.strengths = strengths;
        this.educationalDisclaimer = educationalDisclaimer;
    }

    public int getOverallScore() {
        return this.overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public String getGrade() {
        return this.grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getSavingsRateScore() {
        return this.savingsRateScore;
    }

    public void setSavingsRateScore(int savingsRateScore) {
        this.savingsRateScore = savingsRateScore;
    }

    public int getBudgetDisciplineScore() {
        return this.budgetDisciplineScore;
    }

    public void setBudgetDisciplineScore(int budgetDisciplineScore) {
        this.budgetDisciplineScore = budgetDisciplineScore;
    }

    public int getSpendingConsistencyScore() {
        return this.spendingConsistencyScore;
    }

    public void setSpendingConsistencyScore(int spendingConsistencyScore) {
        this.spendingConsistencyScore = spendingConsistencyScore;
    }

    public int getDebtManagementScore() {
        return this.debtManagementScore;
    }

    public void setDebtManagementScore(int debtManagementScore) {
        this.debtManagementScore = debtManagementScore;
    }

    public int getEmergencySavingsScore() {
        return this.emergencySavingsScore;
    }

    public void setEmergencySavingsScore(int emergencySavingsScore) {
        this.emergencySavingsScore = emergencySavingsScore;
    }

    public List<String> getSuggestions() {
        return this.suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<String> getStrengths() {
        return this.strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public String getEducationalDisclaimer() {
        return this.educationalDisclaimer;
    }

    public void setEducationalDisclaimer(String educationalDisclaimer) {
        this.educationalDisclaimer = educationalDisclaimer;
    }

    public static HealthScoreResponseBuilder builder() {
        return new HealthScoreResponseBuilder();
    }

    public static class HealthScoreResponseBuilder {
        private int overallScore;
        private String grade;
        private int savingsRateScore;
        private int budgetDisciplineScore;
        private int spendingConsistencyScore;
        private int debtManagementScore;
        private int emergencySavingsScore;
        private List<String> suggestions;
        private List<String> strengths;
        private String educationalDisclaimer;

        public HealthScoreResponseBuilder() {}

        public HealthScoreResponseBuilder overallScore(int overallScore) {
            this.overallScore = overallScore;
            return this;
        }

        public HealthScoreResponseBuilder grade(String grade) {
            this.grade = grade;
            return this;
        }

        public HealthScoreResponseBuilder savingsRateScore(int savingsRateScore) {
            this.savingsRateScore = savingsRateScore;
            return this;
        }

        public HealthScoreResponseBuilder budgetDisciplineScore(int budgetDisciplineScore) {
            this.budgetDisciplineScore = budgetDisciplineScore;
            return this;
        }

        public HealthScoreResponseBuilder spendingConsistencyScore(int spendingConsistencyScore) {
            this.spendingConsistencyScore = spendingConsistencyScore;
            return this;
        }

        public HealthScoreResponseBuilder debtManagementScore(int debtManagementScore) {
            this.debtManagementScore = debtManagementScore;
            return this;
        }

        public HealthScoreResponseBuilder emergencySavingsScore(int emergencySavingsScore) {
            this.emergencySavingsScore = emergencySavingsScore;
            return this;
        }

        public HealthScoreResponseBuilder suggestions(List<String> suggestions) {
            this.suggestions = suggestions;
            return this;
        }

        public HealthScoreResponseBuilder strengths(List<String> strengths) {
            this.strengths = strengths;
            return this;
        }

        public HealthScoreResponseBuilder educationalDisclaimer(String educationalDisclaimer) {
            this.educationalDisclaimer = educationalDisclaimer;
            return this;
        }

        public HealthScoreResponse build() {
            HealthScoreResponse instance = new HealthScoreResponse();
            instance.setOverallScore(this.overallScore);
            instance.setGrade(this.grade);
            instance.setSavingsRateScore(this.savingsRateScore);
            instance.setBudgetDisciplineScore(this.budgetDisciplineScore);
            instance.setSpendingConsistencyScore(this.spendingConsistencyScore);
            instance.setDebtManagementScore(this.debtManagementScore);
            instance.setEmergencySavingsScore(this.emergencySavingsScore);
            instance.setSuggestions(this.suggestions);
            instance.setStrengths(this.strengths);
            instance.setEducationalDisclaimer(this.educationalDisclaimer);
            return instance;
        }
    }
}
