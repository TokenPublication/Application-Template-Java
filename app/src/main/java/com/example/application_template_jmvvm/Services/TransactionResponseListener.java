package com.example.application_template_jmvvm.Services;

import com.example.application_template_jmvvm.Responses.TransactionResponse;

public interface TransactionResponseListener {
    void onComplete(TransactionResponse response);
}
