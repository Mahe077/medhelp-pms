package com.medhelp.pms.modules.inventory_module.domain.entities;

import com.medhelp.pms.modules.auth_module.domain.entities.User;
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
@Table(name = "inventory_transactions", schema = "inventory_schema")
public class InventoryTransaction {
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
    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType;

    @NotNull
    @Column(name = "quantity_change", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityChange;

    @NotNull
    @Column(name = "quantity_before", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityBefore;

    @NotNull
    @Column(name = "quantity_after", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityAfter;

    @Size(max = 50)
    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "reason", length = Integer.MAX_VALUE)
    private String reason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}