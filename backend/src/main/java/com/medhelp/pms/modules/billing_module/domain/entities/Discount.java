package com.medhelp.pms.modules.billing_module.domain.entities;

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
@Table(name = "discounts", schema = "billing_schema")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 50)
    @NotNull
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 50)
    @NotNull
    @Column(name = "discount_type", nullable = false, length = 50)
    private String discountType;

    @NotNull
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal discountValue;

    @Column(name = "max_uses")
    private Integer maxUses;

    @ColumnDefault("0")
    @Column(name = "times_used")
    private Integer timesUsed;

    @ColumnDefault("1")
    @Column(name = "max_uses_per_patient")
    private Integer maxUsesPerPatient;

    @Column(name = "minimum_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minimumPurchaseAmount;

    @Column(name = "applicable_medications")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> applicableMedications;

    @NotNull
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}