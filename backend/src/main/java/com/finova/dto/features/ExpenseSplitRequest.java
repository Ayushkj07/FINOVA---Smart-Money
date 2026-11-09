package com.finova.dto.features;

import com.finova.entity.enums.SplitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class ExpenseSplitRequest {

    public static class MemberSplitDto {
        @NotBlank(message = "Member name is required")
        private String name;
        private String email;
        private BigDecimal customAmount;

        public MemberSplitDto() {}
        public MemberSplitDto(String name, String email, BigDecimal customAmount) {
            this.name = name;
            this.email = email;
            this.customAmount = customAmount;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public BigDecimal getCustomAmount() { return customAmount; }
        public void setCustomAmount(BigDecimal customAmount) { this.customAmount = customAmount; }
    }

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;

    private SplitType splitType = SplitType.EQUAL;

    @NotEmpty(message = "At least one participant is required")
    private List<MemberSplitDto> members;

    public ExpenseSplitRequest() {}
    public ExpenseSplitRequest(String title, BigDecimal totalAmount, SplitType splitType, List<MemberSplitDto> members) {
        this.title = title;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.members = members;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public SplitType getSplitType() { return splitType; }
    public void setSplitType(SplitType splitType) { this.splitType = splitType; }
    public List<MemberSplitDto> getMembers() { return members; }
    public void setMembers(List<MemberSplitDto> members) { this.members = members; }
}
