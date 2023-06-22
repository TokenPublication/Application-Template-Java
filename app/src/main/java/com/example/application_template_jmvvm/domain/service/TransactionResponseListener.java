package com.example.application_template_jmvvm.domain.service;

import com.example.application_template_jmvvm.data.model.response.TransactionResponse;

public interface TransactionResponseListener {
    void onComplete(TransactionResponse response);
}
