package com.example.application_template_jmvvm.domain.entity;

public enum BatchResult {
    SUCCESS(0),
    ERROR(1);

    private final int resultCode;

    BatchResult(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }
}
