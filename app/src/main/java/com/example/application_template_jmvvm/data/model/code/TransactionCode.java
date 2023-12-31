package com.example.application_template_jmvvm.data.model.code;

/**
 * This is enum class for holding types of Transaction.
 */
public enum TransactionCode {
    SALE(1),
    INSTALLMENT_SALE(2),
    VOID(3),
    MATCHED_REFUND(4),
    CASH_REFUND(5),
    INSTALLMENT_REFUND(6);

    private final int type;

    TransactionCode(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
