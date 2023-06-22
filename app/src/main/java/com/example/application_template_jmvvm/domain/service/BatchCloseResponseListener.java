package com.example.application_template_jmvvm.domain.service;

import com.example.application_template_jmvvm.data.model.response.BatchCloseResponse;

public interface BatchCloseResponseListener {
    void onComplete(BatchCloseResponse response);
}
