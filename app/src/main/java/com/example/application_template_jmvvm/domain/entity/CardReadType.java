package com.example.application_template_jmvvm.domain.entity;

public enum CardReadType {
    NONE(0),
    ICC(1),
    MSR(2),
    ICC2MSR(3),
    KeyIn(4),
    CLCard(5),//Contactless cards
    QrPay(6);

    public final int value;
    CardReadType(int value) {
        this.value = value;
    }
}
