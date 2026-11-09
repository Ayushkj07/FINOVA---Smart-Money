package com.finova.dto.ai;

import java.math.BigDecimal;
import java.util.List;

public class AiInsightsResponse {

    public static class InsightCard {
        private String type;
        private String title;
        private String description;
        private String severity;
        private String icon;
        private String actionText;

        public InsightCard() {}
        public InsightCard(String type, String title, String description, String severity, String icon, String actionText) {
            this.type = type;
            this.title = title;
            this.description = description;
            this.severity = severity;
            this.icon = icon;
            this.actionText = actionText;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getActionText() { return actionText; }
        public void setActionText(String actionText) { this.actionText = actionText; }

        public static InsightCardBuilder builder() { return new InsightCardBuilder(); }
        public static class InsightCardBuilder {
            private String type;
            private String title;
            private String description;
            private String severity;
            private String icon;
            private String actionText;

            public InsightCardBuilder type(String type) { this.type = type; return this; }
            public InsightCardBuilder title(String title) { this.title = title; return this; }
            public InsightCardBuilder description(String description) { this.description = description; return this; }
            public InsightCardBuilder severity(String severity) { this.severity = severity; return this; }
            public InsightCardBuilder icon(String icon) { this.icon = icon; return this; }
            public InsightCardBuilder actionText(String actionText) { this.actionText = actionText; return this; }
            public InsightCard build() { return new InsightCard(type, title, description, severity, icon, actionText); }
        }
    }

    private String monthlySummary;
    private List<InsightCard> insights;
    private List<String> topSavingOpportunities;
    private BigDecimal predictedNextMonthSpending;
    private String disclaimer;

    public AiInsightsResponse() {}
    public AiInsightsResponse(String monthlySummary, List<InsightCard> insights, List<String> topSavingOpportunities, BigDecimal predictedNextMonthSpending, String disclaimer) {
        this.monthlySummary = monthlySummary;
        this.insights = insights;
        this.topSavingOpportunities = topSavingOpportunities;
        this.predictedNextMonthSpending = predictedNextMonthSpending;
        this.disclaimer = disclaimer;
    }

    public String getMonthlySummary() { return monthlySummary; }
    public void setMonthlySummary(String monthlySummary) { this.monthlySummary = monthlySummary; }
    public List<InsightCard> getInsights() { return insights; }
    public void setInsights(List<InsightCard> insights) { this.insights = insights; }
    public List<String> getTopSavingOpportunities() { return topSavingOpportunities; }
    public void setTopSavingOpportunities(List<String> topSavingOpportunities) { this.topSavingOpportunities = topSavingOpportunities; }
    public BigDecimal getPredictedNextMonthSpending() { return predictedNextMonthSpending; }
    public void setPredictedNextMonthSpending(BigDecimal predictedNextMonthSpending) { this.predictedNextMonthSpending = predictedNextMonthSpending; }
    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }

    public static AiInsightsResponseBuilder builder() { return new AiInsightsResponseBuilder(); }
    public static class AiInsightsResponseBuilder {
        private String monthlySummary;
        private List<InsightCard> insights;
        private List<String> topSavingOpportunities;
        private BigDecimal predictedNextMonthSpending;
        private String disclaimer;

        public AiInsightsResponseBuilder monthlySummary(String monthlySummary) { this.monthlySummary = monthlySummary; return this; }
        public AiInsightsResponseBuilder insights(List<InsightCard> insights) { this.insights = insights; return this; }
        public AiInsightsResponseBuilder topSavingOpportunities(List<String> topSavingOpportunities) { this.topSavingOpportunities = topSavingOpportunities; return this; }
        public AiInsightsResponseBuilder predictedNextMonthSpending(BigDecimal predictedNextMonthSpending) { this.predictedNextMonthSpending = predictedNextMonthSpending; return this; }
        public AiInsightsResponseBuilder disclaimer(String disclaimer) { this.disclaimer = disclaimer; return this; }
        public AiInsightsResponse build() { return new AiInsightsResponse(monthlySummary, insights, topSavingOpportunities, predictedNextMonthSpending, disclaimer); }
    }
}
