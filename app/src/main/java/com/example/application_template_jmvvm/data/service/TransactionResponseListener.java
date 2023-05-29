package com.example.application_template_jmvvm.data.service;

import com.example.application_template_jmvvm.data.response.TransactionResponse;

public interface TransactionResponseListener {
    void onComplete(TransactionResponse response);
}
