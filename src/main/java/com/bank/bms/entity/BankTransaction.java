package com.bank.bms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_tx_from", columnList = "fromAccount"),
        @Index(name = "idx_tx_to", columnList = "toAccount")
})
public class BankTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    /** null for deposits */
    private String fromAccount;
    /** null for withdrawals */
    private String toAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public BankTransaction() {}

    public BankTransaction(TransactionType type, String fromAccount, String toAccount,
                           BigDecimal amount, String description) {
        this.type = type;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.description = description;
    }

    public Long getId() { return id; }
    public TransactionType getType() { return type; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
