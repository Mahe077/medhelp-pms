package com.medhelp.pms.modules.inventory_module.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "purchase_order_items", schema = "inventory_schema")
public class PurchaseOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id")
    private InventoryItem inventoryItem;

    @Size(max = 11)
    @NotNull
    @Column(name = "ndc_code", nullable = false, length = 11)
    private String ndcCode;

    @Size(max = 255)
    @NotNull
    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    @NotNull
    @Column(name = "quantity_ordered", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityOrdered;

    @ColumnDefault("0")
    @Column(name = "quantity_received", precision = 10, scale = 2)
    private BigDecimal quantityReceived;

    @NotNull
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 4)
    private BigDecimal unitPrice;

    @NotNull
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

}