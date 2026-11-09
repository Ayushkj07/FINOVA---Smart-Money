package com.finova.entity;

import com.finova.entity.enums.RoleType;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "roles")
public class Role {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private RoleType name;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();


    public Role() {}

    public Role(Long id, RoleType name, String description, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleType getName() {
        return this.name;
    }

    public void setName(RoleType name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static RoleBuilder builder() {
        return new RoleBuilder();
    }

    public static class RoleBuilder {
        private Long id;
        private RoleType name;
        private String description;
        private Instant createdAt;

        public RoleBuilder() {}

        public RoleBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoleBuilder name(RoleType name) {
            this.name = name;
            return this;
        }

        public RoleBuilder description(String description) {
            this.description = description;
            return this;
        }

        public RoleBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Role build() {
            Role instance = new Role();
            instance.setId(this.id);
            instance.setName(this.name);
            instance.setDescription(this.description);
            instance.setCreatedAt(this.createdAt);
            return instance;
        }
    }
}
