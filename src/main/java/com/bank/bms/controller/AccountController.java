package com.bank.bms.controller;

import com.bank.bms.dto.*;
import com.bank.bms.service.BankService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final BankService bank;

    public AccountController(BankService bank) { this.bank = bank; }

    @PostMapping
    public ResponseEntity<AccountResponse> create(Authentication auth, @Valid @RequestBody CreateAccountRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bank.createAccount(auth, req));
    }

    @GetMapping
    public List<AccountResponse> mine(Authentication auth) {
        return bank.myAccounts(auth);
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse get(Authentication auth, @PathVariable String accountNumber) {
        return bank.getAccount(auth, accountNumber);
    }

    @PostMapping("/{accountNumber}/deposit")
    public AccountResponse deposit(Authentication auth, @PathVariable String accountNumber,
                                   @Valid @RequestBody AmountRequest req) {
        return bank.deposit(auth, accountNumber, req);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public AccountResponse withdraw(Authentication auth, @PathVariable String accountNumber,
                                    @Valid @RequestBody AmountRequest req) {
        return bank.withdraw(auth, accountNumber, req);
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionResponse> history(Authentication auth, @PathVariable String accountNumber) {
        return bank.history(auth, accountNumber);
    }
}
