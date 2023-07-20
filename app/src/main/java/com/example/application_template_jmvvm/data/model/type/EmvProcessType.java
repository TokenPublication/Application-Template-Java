package com.example.application_template_jmvvm.data.model.type;

/**
 * This is enum class for holding types of EmvProcess.
 */
public enum EmvProcessType {
    PARTIAL_EMV,     //After reading card data with requested Tags, requesting AAC at the 1st Generate AC to complete transaction and return requested Tag data
    READ_CARD,       //After app selection, read requested card data and return
    CONTINUE_EMV,    //Continue and complete full EMV Txn from Read Card Data process and returns  requested Tag data.
    FULL_EMV        //Actions based EMV Specification, such as Initiate App. Processing, Read App. Data, Off. Auth., Processing restrictions, Cardholder Verification, Term. Risk Man., First Gen AC and returns  requested Tag data.
}
