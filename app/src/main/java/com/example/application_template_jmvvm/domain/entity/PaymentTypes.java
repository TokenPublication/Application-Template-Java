package com.example.application_template_jmvvm.domain.entity;

public enum PaymentTypes {
    CREDITCARD(3),
    TRQRCREDITCARD(23),
    TRQRFAST(24),
    TRQRMOBILE(25),
    TRQROTHER(26),
    OTHER(27);

    public final int type;
    PaymentTypes(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
