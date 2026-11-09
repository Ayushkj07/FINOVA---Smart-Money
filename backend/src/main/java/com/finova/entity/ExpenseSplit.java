package com.finova.entity;

import com.finova.entity.enums.SplitType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expense_splits")
public class ExpenseSplit {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "split_type", length = 30)
    private SplitType splitType = SplitType.EQUAL;

    @Column(name = "is_settled")
    private Boolean isSettled = false;

    @OneToMany(mappedBy = "expenseSplit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseSplitMember> members = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();


    public ExpenseSplit() {}

    public ExpenseSplit(Long id, User creator, String title, BigDecimal totalAmount, SplitType splitType, Boolean isSettled, List<ExpenseSplitMember> members, Instant createdAt) {
        this.id = id;
        this.creator = creator;
        this.title = title;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.isSettled = isSettled;
        this.members = members;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCreator() {
        return this.creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public SplitType getSplitType() {
        return this.splitType;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
    }

    public Boolean getIsSettled() {
        return this.isSettled;
    }

    public Boolean isIsSettled() {
        return this.isSettled;
    }

    public void setIsSettled(Boolean isSettled) {
        this.isSettled = isSettled;
    }

    public List<ExpenseSplitMember> getMembers() {
        return this.members;
    }

    public void setMembers(List<ExpenseSplitMember> members) {
        this.members = members;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static ExpenseSplitBuilder builder() {
        return new ExpenseSplitBuilder();
    }

    public static class ExpenseSplitBuilder {
        private Long id;
        private User creator;
        private String title;
        private BigDecimal totalAmount;
        private SplitType splitType;
        private Boolean isSettled;
        private List<ExpenseSplitMember> members;
        private Instant createdAt;

        public ExpenseSplitBuilder() {}

        public ExpenseSplitBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ExpenseSplitBuilder creator(User creator) {
            this.creator = creator;
            return this;
        }

        public ExpenseSplitBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExpenseSplitBuilder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public ExpenseSplitBuilder splitType(SplitType splitType) {
            this.splitType = splitType;
            return this;
        }

        public ExpenseSplitBuilder isSettled(Boolean isSettled) {
            this.isSettled = isSettled;
            return this;
        }

        public ExpenseSplitBuilder members(List<ExpenseSplitMember> members) {
            this.members = members;
            return this;
        }

        public ExpenseSplitBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExpenseSplit build() {
            ExpenseSplit instance = new ExpenseSplit();
            instance.setId(this.id);
            instance.setCreator(this.creator);
            instance.setTitle(this.title);
            instance.setTotalAmount(this.totalAmount);
            instance.setSplitType(this.splitType);
            instance.setIsSettled(this.isSettled);
            instance.setMembers(this.members);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
