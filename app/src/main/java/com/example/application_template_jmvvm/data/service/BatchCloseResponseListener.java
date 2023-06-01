package com.example.application_template_jmvvm.data.service;

import com.example.application_template_jmvvm.data.response.BatchCloseResponse;

public interface BatchCloseResponseListener {
    void onComplete(BatchCloseResponse response);
}
