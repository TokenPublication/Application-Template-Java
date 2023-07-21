package com.example.application_template_jmvvm.data.model.response;

import com.example.application_template_jmvvm.data.model.code.BatchResult;

import java.text.SimpleDateFormat;

/**
 * This class is for holding data after batch close operation ends.
 */
public class BatchCloseResponse {
    private BatchResult batchResult;
    private SimpleDateFormat date;

    public BatchCloseResponse(BatchResult batchResult, SimpleDateFormat date) {
        this.batchResult = batchResult;
        this.date = date;
    }

    public BatchResult getBatchResult() {
        return batchResult;
    }

    public void setBatchResult(BatchResult batchResult) {
        this.batchResult = batchResult;
    }

    public SimpleDateFormat getDate() {
        return date;
    }

    public void setDate(SimpleDateFormat date) {
        this.date = date;
    }
}
