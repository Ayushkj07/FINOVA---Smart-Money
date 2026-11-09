package com.finova.dto.ai;

import java.time.Instant;
import java.util.List;

public class AiChatResponse {


    private String response;
    private List<String> suggestions;
    private String modelUsed;
    private Instant timestamp = Instant.now();
    private String disclaimer;


    public AiChatResponse() {}

    public AiChatResponse(String response, List<String> suggestions, String modelUsed, Instant timestamp, String disclaimer) {
        this.response = response;
        this.suggestions = suggestions;
        this.modelUsed = modelUsed;
        this.timestamp = timestamp;
        this.disclaimer = disclaimer;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<String> getSuggestions() {
        return this.suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getModelUsed() {
        return this.modelUsed;
    }

    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDisclaimer() {
        return this.disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public static AiChatResponseBuilder builder() {
        return new AiChatResponseBuilder();
    }

    public static class AiChatResponseBuilder {
        private String response;
        private List<String> suggestions;
        private String modelUsed;
        private Instant timestamp;
        private String disclaimer;

        public AiChatResponseBuilder() {}

        public AiChatResponseBuilder response(String response) {
            this.response = response;
            return this;
        }

        public AiChatResponseBuilder suggestions(List<String> suggestions) {
            this.suggestions = suggestions;
            return this;
        }

        public AiChatResponseBuilder modelUsed(String modelUsed) {
            this.modelUsed = modelUsed;
            return this;
        }

        public AiChatResponseBuilder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public AiChatResponseBuilder disclaimer(String disclaimer) {
            this.disclaimer = disclaimer;
            return this;
        }

        public AiChatResponse build() {
            AiChatResponse instance = new AiChatResponse();
            instance.setResponse(this.response);
            instance.setSuggestions(this.suggestions);
            instance.setModelUsed(this.modelUsed);
            instance.setTimestamp(this.timestamp);
            instance.setDisclaimer(this.disclaimer);
            return instance;
        }
    }
}
