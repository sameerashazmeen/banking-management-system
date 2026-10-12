package com.bank.bms.dto;

import com.bank.bms.entity.Account;
import com.bank.bms.entity.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(String accountNumber, String holderName, AccountType accountType,
                              BigDecimal balance, LocalDateTime createdAt) {
    public static AccountResponse from(Account a) {
        return new AccountResponse(a.getAccountNumber(), a.getHolderName(), a.getAccountType(),
                a.getBalance(), a.getCreatedAt());
    }
}
