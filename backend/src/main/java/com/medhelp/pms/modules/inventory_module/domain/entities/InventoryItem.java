package com.medhelp.pms.modules.inventory_module.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "inventory_items", schema = "inventory_schema")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "medication_id", nullable = false)
    private UUID medicationId;

    @Size(max = 11)
    @NotNull
    @Column(name = "ndc_code", nullable = false, length = 11)
    private String ndcCode;

    @Size(max = 50)
    @Column(name = "bin_location", length = 50)
    private String binLocation;

    @ColumnDefault("false")
    @Column(name = "requires_refrigeration")
    private Boolean requiresRefrigeration;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "quantity_on_hand", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityOnHand;

    @Size(max = 20)
    @NotNull
    @Column(name = "quantity_unit", nullable = false, length = 20)
    private String quantityUnit;

    @NotNull
    @Column(name = "reorder_point", nullable = false, precision = 10, scale = 2)
    private BigDecimal reorderPoint;

    @NotNull
    @Column(name = "reorder_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal reorderQuantity;

    @Column(name = "economic_order_quantity", precision = 10, scale = 2)
    private BigDecimal economicOrderQuantity;

    @Column(name = "unit_cost", precision = 10, scale = 4)
    private BigDecimal unitCost;

    @Column(name = "average_wholesale_price", precision = 10, scale = 4)
    private BigDecimal averageWholesalePrice;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "last_counted_at")
    private LocalDateTime lastCountedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ColumnDefault("1")
    @Column(name = "version")
    private Integer version;

}