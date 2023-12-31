package com.example.application_template_jmvvm.data.model.type;

/**
 * This is enum class for holding types of cards.
 */
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

    public int getType() {
        return value;
    }
}
