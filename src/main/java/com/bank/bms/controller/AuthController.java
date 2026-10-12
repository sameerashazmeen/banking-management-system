package com.bank.bms.controller;

import com.bank.bms.dto.RegisterRequest;
import com.bank.bms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest req) {
        auth.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered"));
    }
}
