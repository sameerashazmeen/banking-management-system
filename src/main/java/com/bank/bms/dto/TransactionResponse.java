package com.bank.bms.dto;

import com.bank.bms.entity.BankTransaction;
import com.bank.bms.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(Long id, TransactionType type, String fromAccount, String toAccount,
                                  BigDecimal amount, String description, LocalDateTime createdAt) {
    public static TransactionResponse from(BankTransaction t) {
        return new TransactionResponse(t.getId(), t.getType(), t.getFromAccount(), t.getToAccount(),
                t.getAmount(), t.getDescription(), t.getCreatedAt());
    }
}
