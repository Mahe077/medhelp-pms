package com.medhelp.pms.modules.billing_module.domain.entities;

import com.medhelp.pms.modules.auth_module.domain.entities.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "pricing_rules", schema = "billing_schema")
public class PricingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Size(max = 50)
    @NotNull
    @Column(name = "rule_type", nullable = false, length = 50)
    private String ruleType;

    @NotNull
    @Column(name = "conditions", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> conditions;

    @Size(max = 50)
    @NotNull
    @Column(name = "pricing_method", nullable = false, length = 50)
    private String pricingMethod;

    @NotNull
    @Column(name = "pricing_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal pricingValue;

    @ColumnDefault("0")
    @Column(name = "priority")
    private Integer priority;

    @NotNull
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

}