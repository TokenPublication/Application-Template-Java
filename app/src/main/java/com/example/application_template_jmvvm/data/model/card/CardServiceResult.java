package com.example.application_template_jmvvm.data.model.card;

public enum CardServiceResult {
    SUCCESS(0),
    USER_CANCELLED(1),
    ERROR(2),
    ERROR_MSR_TRACK_IS_EMPTY(3),
    ERROR_JSON_PARSE(4),
    ERROR_UNSUPPORTED_ENCODING(5),
    ERROR_TIMEOUT(6),
    ERROR_FALLBACK_AUTH(7);

    private final int value;

    CardServiceResult(int value){
        this.value = value;
    }

    public int resultCode(){
        return value;
    }
}
