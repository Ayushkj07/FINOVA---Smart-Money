package com.finova.dto.ai;

import jakarta.validation.constraints.NotBlank;
public class AiChatRequest {



    @NotBlank(message = "Message prompt cannot be empty")
    private String message;

    private String conversationContext;


    public AiChatRequest() {}

    public AiChatRequest(String message, String conversationContext) {
        this.message = message;
        this.conversationContext = conversationContext;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConversationContext() {
        return this.conversationContext;
    }

    public void setConversationContext(String conversationContext) {
        this.conversationContext = conversationContext;
    }

    public static AiChatRequestBuilder builder() {
        return new AiChatRequestBuilder();
    }

    public static class AiChatRequestBuilder {
        private String message;
        private String conversationContext;

        public AiChatRequestBuilder() {}

        public AiChatRequestBuilder message(String message) {
            this.message = message;
            return this;
        }

        public AiChatRequestBuilder conversationContext(String conversationContext) {
            this.conversationContext = conversationContext;
            return this;
        }

        public AiChatRequest build() {
            AiChatRequest instance = new AiChatRequest();
            instance.setMessage(this.message);
            instance.setConversationContext(this.conversationContext);
            return instance;
        }
    }
}
