package com.diplom.demo.Service;

import java.time.LocalDateTime;

public class ResetCodeData {
    private final String code;
    private final LocalDateTime expirationTime;

    public ResetCodeData(String code, LocalDateTime expirationTime) {
        this.code = code;
        this.expirationTime = expirationTime;
    }

    public String getCode() {
        return code;
    }

    public boolean isExpired() {
        return expirationTime.isBefore(LocalDateTime.now());
    }
}

