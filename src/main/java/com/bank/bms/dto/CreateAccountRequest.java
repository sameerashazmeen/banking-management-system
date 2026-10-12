package com.bank.bms.dto;

import com.bank.bms.entity.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank String holderName,
        @NotNull AccountType accountType,
        @DecimalMin(value = "0.00") BigDecimal initialDeposit) {}
