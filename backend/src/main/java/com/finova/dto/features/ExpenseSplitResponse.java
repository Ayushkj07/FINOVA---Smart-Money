package com.finova.dto.features;

import com.finova.entity.enums.SplitType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class ExpenseSplitResponse {

    public static class MemberDto {
        private Long id;
        private String name;
        private String email;
        private BigDecimal owedAmount;
        private BigDecimal paidAmount;
        private Boolean isPaid;

        public MemberDto() {}
        public MemberDto(Long id, String name, String email, BigDecimal owedAmount, BigDecimal paidAmount, Boolean isPaid) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.owedAmount = owedAmount;
            this.paidAmount = paidAmount;
            this.isPaid = isPaid;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public BigDecimal getOwedAmount() { return owedAmount; }
        public void setOwedAmount(BigDecimal owedAmount) { this.owedAmount = owedAmount; }
        public BigDecimal getPaidAmount() { return paidAmount; }
        public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
        public Boolean getIsPaid() { return isPaid; }
        public void setIsPaid(Boolean isPaid) { this.isPaid = isPaid; }

        public static MemberDtoBuilder builder() { return new MemberDtoBuilder(); }
        public static class MemberDtoBuilder {
            private Long id;
            private String name;
            private String email;
            private BigDecimal owedAmount;
            private BigDecimal paidAmount;
            private Boolean isPaid;

            public MemberDtoBuilder id(Long id) { this.id = id; return this; }
            public MemberDtoBuilder name(String name) { this.name = name; return this; }
            public MemberDtoBuilder email(String email) { this.email = email; return this; }
            public MemberDtoBuilder owedAmount(BigDecimal owedAmount) { this.owedAmount = owedAmount; return this; }
            public MemberDtoBuilder paidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; return this; }
            public MemberDtoBuilder isPaid(Boolean isPaid) { this.isPaid = isPaid; return this; }
            public MemberDto build() { return new MemberDto(id, name, email, owedAmount, paidAmount, isPaid); }
        }
    }

    private Long id;
    private String title;
    private BigDecimal totalAmount;
    private SplitType splitType;
    private Boolean isSettled;
    private List<MemberDto> members;
    private Instant createdAt;

    public ExpenseSplitResponse() {}
    public ExpenseSplitResponse(Long id, String title, BigDecimal totalAmount, SplitType splitType, Boolean isSettled, List<MemberDto> members, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.isSettled = isSettled;
        this.members = members;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public SplitType getSplitType() { return splitType; }
    public void setSplitType(SplitType splitType) { this.splitType = splitType; }
    public Boolean getIsSettled() { return isSettled; }
    public void setIsSettled(Boolean isSettled) { this.isSettled = isSettled; }
    public List<MemberDto> getMembers() { return members; }
    public void setMembers(List<MemberDto> members) { this.members = members; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static ExpenseSplitResponseBuilder builder() { return new ExpenseSplitResponseBuilder(); }
    public static class ExpenseSplitResponseBuilder {
        private Long id;
        private String title;
        private BigDecimal totalAmount;
        private SplitType splitType;
        private Boolean isSettled;
        private List<MemberDto> members;
        private Instant createdAt;

        public ExpenseSplitResponseBuilder id(Long id) { this.id = id; return this; }
        public ExpenseSplitResponseBuilder title(String title) { this.title = title; return this; }
        public ExpenseSplitResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public ExpenseSplitResponseBuilder splitType(SplitType splitType) { this.splitType = splitType; return this; }
        public ExpenseSplitResponseBuilder isSettled(Boolean isSettled) { this.isSettled = isSettled; return this; }
        public ExpenseSplitResponseBuilder members(List<MemberDto> members) { this.members = members; return this; }
        public ExpenseSplitResponseBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public ExpenseSplitResponse build() { return new ExpenseSplitResponse(id, title, totalAmount, splitType, isSettled, members, createdAt); }
    }
}
