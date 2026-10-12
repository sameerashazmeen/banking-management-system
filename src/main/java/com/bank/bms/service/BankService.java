package com.bank.bms.service;

import com.bank.bms.dto.*;
import com.bank.bms.entity.*;
import com.bank.bms.exception.BadRequestException;
import com.bank.bms.exception.InsufficientFundsException;
import com.bank.bms.exception.ResourceNotFoundException;
import com.bank.bms.repository.AccountRepository;
import com.bank.bms.repository.TransactionRepository;
import com.bank.bms.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;

@Service
public class BankService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final AccountRepository accounts;
    private final TransactionRepository transactions;
    private final UserRepository users;

    public BankService(AccountRepository accounts, TransactionRepository transactions, UserRepository users) {
        this.accounts = accounts;
        this.transactions = transactions;
        this.users = users;
    }

    // ---------------- accounts ----------------

    @Transactional
    public AccountResponse createAccount(Authentication auth, CreateAccountRequest req) {
        AppUser owner = users.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account acc = new Account();
        acc.setAccountNumber(generateAccountNumber());
        acc.setHolderName(req.holderName());
        acc.setAccountType(req.accountType());
        acc.setOwner(owner);
        BigDecimal initial = req.initialDeposit() == null ? BigDecimal.ZERO.setScale(2) : scale(req.initialDeposit());
        acc.setBalance(initial);
        accounts.save(acc);

        if (initial.signum() > 0) {
            transactions.save(new BankTransaction(TransactionType.DEPOSIT, null,
                    acc.getAccountNumber(), initial, "Initial deposit"));
        }
        return AccountResponse.from(acc);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> myAccounts(Authentication auth) {
        return accounts.findAllByOwnerUsername(auth.getName()).stream().map(AccountResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Authentication auth, String accountNumber) {
        Account acc = accounts.findWithOwner(accountNumber)
                .orElseThrow(() -> notFound(accountNumber));
        assertAccess(auth, acc);
        return AccountResponse.from(acc);
    }

    // ---------------- money movement ----------------

    @Transactional
    public AccountResponse deposit(Authentication auth, String accountNumber, AmountRequest req) {
        Account acc = accounts.findForUpdate(accountNumber).orElseThrow(() -> notFound(accountNumber));
        assertAccess(auth, acc);
        BigDecimal amount = scale(req.amount());
        acc.setBalance(acc.getBalance().add(amount));
        transactions.save(new BankTransaction(TransactionType.DEPOSIT, null, accountNumber, amount,
                orDefault(req.description(), "Cash deposit")));
        return AccountResponse.from(acc);
    }

    @Transactional
    public AccountResponse withdraw(Authentication auth, String accountNumber, AmountRequest req) {
        Account acc = accounts.findForUpdate(accountNumber).orElseThrow(() -> notFound(accountNumber));
        assertAccess(auth, acc);
        BigDecimal amount = scale(req.amount());
        debit(acc, amount);
        transactions.save(new BankTransaction(TransactionType.WITHDRAWAL, accountNumber, null, amount,
                orDefault(req.description(), "Cash withdrawal")));
        return AccountResponse.from(acc);
    }

    /**
     * Atomic transfer. Both rows are locked in a fixed (alphabetical) order so two opposite
     * transfers between the same pair of accounts cannot deadlock.
     */
    @Transactional
    public TransactionResponse transfer(Authentication auth, TransferRequest req) {
        if (req.fromAccount().equals(req.toAccount())) {
            throw new BadRequestException("Source and destination accounts must be different");
        }
        String first = req.fromAccount().compareTo(req.toAccount()) < 0 ? req.fromAccount() : req.toAccount();
        String second = first.equals(req.fromAccount()) ? req.toAccount() : req.fromAccount();

        Account a1 = accounts.findForUpdate(first).orElseThrow(() -> notFound(first));
        Account a2 = accounts.findForUpdate(second).orElseThrow(() -> notFound(second));
        Account from = a1.getAccountNumber().equals(req.fromAccount()) ? a1 : a2;
        Account to = (from == a1) ? a2 : a1;

        assertAccess(auth, from);   // you may only send money out of your own account

        BigDecimal amount = scale(req.amount());
        debit(from, amount);
        to.setBalance(to.getBalance().add(amount));

        BankTransaction tx = transactions.save(new BankTransaction(TransactionType.TRANSFER,
                from.getAccountNumber(), to.getAccountNumber(), amount,
                orDefault(req.description(), "Fund transfer")));
        return TransactionResponse.from(tx);
    }

    // ---------------- history ----------------

    @Transactional(readOnly = true)
    public List<TransactionResponse> history(Authentication auth, String accountNumber) {
        Account acc = accounts.findWithOwner(accountNumber).orElseThrow(() -> notFound(accountNumber));
        assertAccess(auth, acc);
        return transactions.findHistory(accountNumber).stream().map(TransactionResponse::from).toList();
    }

    // ---------------- helpers ----------------

    private void debit(Account acc, BigDecimal amount) {
        if (acc.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account " + acc.getAccountNumber());
        }
        acc.setBalance(acc.getBalance().subtract(amount));
    }

    private void assertAccess(Authentication auth, Account acc) {
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !acc.getOwner().getUsername().equals(auth.getName())) {
            throw new AccessDeniedException("Not your account");
        }
    }

    private String generateAccountNumber() {
        String n;
        do {
            StringBuilder sb = new StringBuilder("BK");
            for (int i = 0; i < 10; i++) sb.append(RANDOM.nextInt(10));
            n = sb.toString();
        } while (accounts.existsByAccountNumber(n));
        return n;
    }

    private static BigDecimal scale(BigDecimal v) { return v.setScale(2, RoundingMode.HALF_UP); }
    private static String orDefault(String s, String d) { return (s == null || s.isBlank()) ? d : s; }
    private static ResourceNotFoundException notFound(String no) {
        return new ResourceNotFoundException("Account not found: " + no);
    }
}
