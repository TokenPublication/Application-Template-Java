package com.example.application_template_jmvvm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.application_template_jmvvm.data.database.AppTempDB;
import com.example.application_template_jmvvm.data.database.transaction.Transaction;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.model.type.SlipType;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.utils.objects.SampleReceipt;
import com.example.application_template_jmvvm.utils.printHelpers.TransactionPrintHelper;

import java.util.List;

public class CheckSaleReceiver extends BroadcastReceiver {

    /**
     * This class for receive the UUID from successful transaction performed via
     * battery run out flow. It takes UUID from @param intent and control the transaction
     * is successful or not. If it's successful, it creates intent again and send to PGW
     * for print it. When the sending intent, used sendBroadcast function for communicate
     * with PGW.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("UUID")) {
            Log.d("UUID", intent.getExtras().getString("UUID"));
            String uuid = intent.getExtras().getString("UUID");
            AppTempDB db = AppTempDB.getDatabase(context);
            ActivationRepository activationRepository = new ActivationRepository(db.activationDao());
            BatchRepository batchRepository = new BatchRepository(db.batchDao());
            TransactionPrintHelper transactionPrintHelper = new TransactionPrintHelper();
            List<Transaction> transactionList = db.transactionDao().getTransactionsByUUID(uuid);
            Transaction transaction;
            if (transactionList == null || transactionList.isEmpty()) {
                transaction = null;
            } else {
                transaction = transactionList.get(0);
            }
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            if (transaction != null) {
                bundle.putInt("ResponseCode", ResponseCode.SUCCESS.ordinal());
                bundle.putInt("PaymentStatus", 0);
                bundle.putInt("Amount", transaction.getUlAmount());
                SampleReceipt receipt = new SampleReceipt(transaction, activationRepository, batchRepository, null);
                bundle.putString("customerSlipData", transactionPrintHelper.getFormattedText(receipt, transaction, TransactionCode.SALE, SlipType.CARDHOLDER_SLIP, context, null, null, false));
                bundle.putString("merchantSlipData", transactionPrintHelper.getFormattedText(receipt, transaction, TransactionCode.SALE, SlipType.MERCHANT_SLIP, context, null, null, false));
                bundle.putInt("BatchNo", transaction.getBatchNo());
                bundle.putInt("TxnNo", transaction.getUlGUP_SN());
                bundle.putInt("SlipType", SlipType.BOTH_SLIPS.value);
                bundle.putBoolean("IsSlip", true);
            } else {
                bundle.putInt("ResponseCode", ResponseCode.ERROR.ordinal());
            }
            resultIntent.putExtras(bundle);
            resultIntent.setAction("check_sale_result");
            resultIntent.setPackage("com.tokeninc.sardis.paymentgateway");
            Log.d("intent_control", resultIntent.toString());
            context.sendBroadcast(resultIntent);
        }
    }
}
