package com.medhelp.pms.modules.inventory_module.domain.entities;

import com.medhelp.pms.modules.billing_module.domain.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "stock_adjustments", schema = "inventory_schema")
public class StockAdjustment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id")
    private InventoryItem inventoryItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private InventoryBatch batch;

    @Size(max = 50)
    @NotNull
    @Column(name = "adjustment_type", nullable = false, length = 50)
    private String adjustmentType;

    @NotNull
    @Column(name = "quantity_expected", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityExpected;

    @NotNull
    @Column(name = "quantity_actual", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityActual;

    @NotNull
    @Column(name = "quantity_difference", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityDifference;

    @NotNull
    @Column(name = "reason", nullable = false, length = Integer.MAX_VALUE)
    private String reason;

    @ColumnDefault("true")
    @Column(name = "requires_approval")
    private Boolean requiresApproval;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @NotNull
    @Column(name = "adjustment_date", nullable = false)
    private LocalDate adjustmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

}