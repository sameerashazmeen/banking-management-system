package com.bank.bms.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) { super(message); }
}
