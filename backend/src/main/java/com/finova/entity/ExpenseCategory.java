package com.finova.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "expense_categories")
public class ExpenseCategory {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String icon = "DollarSign";

    @Column(name = "color_hex", length = 20)
    private String colorHex = "#9B87F5";

    @Column(name = "is_system")
    private Boolean isSystem = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();


    public ExpenseCategory() {}

    public ExpenseCategory(Long id, String name, String icon, String colorHex, Boolean isSystem, User user, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.colorHex = colorHex;
        this.isSystem = isSystem;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColorHex() {
        return this.colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public Boolean getIsSystem() {
        return this.isSystem;
    }

    public Boolean isIsSystem() {
        return this.isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static ExpenseCategoryBuilder builder() {
        return new ExpenseCategoryBuilder();
    }

    public static class ExpenseCategoryBuilder {
        private Long id;
        private String name;
        private String icon;
        private String colorHex;
        private Boolean isSystem;
        private User user;
        private Instant createdAt;

        public ExpenseCategoryBuilder() {}

        public ExpenseCategoryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ExpenseCategoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ExpenseCategoryBuilder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public ExpenseCategoryBuilder colorHex(String colorHex) {
            this.colorHex = colorHex;
            return this;
        }

        public ExpenseCategoryBuilder isSystem(Boolean isSystem) {
            this.isSystem = isSystem;
            return this;
        }

        public ExpenseCategoryBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ExpenseCategoryBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExpenseCategory build() {
            ExpenseCategory instance = new ExpenseCategory();
            instance.setId(this.id);
            instance.setName(this.name);
            instance.setIcon(this.icon);
            instance.setColorHex(this.colorHex);
            instance.setIsSystem(this.isSystem);
            instance.setUser(this.user);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
