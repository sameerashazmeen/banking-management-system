package com.bank.bms.service;

import com.bank.bms.dto.RegisterRequest;
import com.bank.bms.entity.AppUser;
import com.bank.bms.entity.Role;
import com.bank.bms.exception.BadRequestException;
import com.bank.bms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public void register(RegisterRequest req) {
        if (users.existsByUsername(req.username())) {
            throw new BadRequestException("Username already taken");
        }
        AppUser u = new AppUser();
        u.setUsername(req.username());
        u.setPassword(encoder.encode(req.password()));
        u.setRole(Role.USER);
        users.save(u);
    }
}
