package com.example.application_template_jmvvm.data.model.type;

/**
 * This is enum class for holding types of slip.
 */
public enum SlipType {
    NO_SLIP(0),
    MERCHANT_SLIP(1),
    CARDHOLDER_SLIP(2),
    BOTH_SLIPS(3);

    public final int value;

    SlipType(int value) {
        this.value = value;
    }

    public int getType() { return value;}
}
