package com.bank.bms.controller;

import com.bank.bms.dto.TransactionResponse;
import com.bank.bms.dto.TransferRequest;
import com.bank.bms.service.BankService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final BankService bank;

    public TransferController(BankService bank) { this.bank = bank; }

    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(Authentication auth, @Valid @RequestBody TransferRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bank.transfer(auth, req));
    }
}
