package com.example.application_template_jmvvm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.application_template_jmvvm.data.database.AppTempDB;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.model.type.SlipType;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.utils.objects.SampleReceipt;
import com.example.application_template_jmvvm.utils.printHelpers.SalePrintHelper;

import java.util.List;

public class CheckSaleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("UUID")) {
            Log.d("UUID", intent.getExtras().getString("UUID"));
            String uuid = intent.getExtras().getString("UUID");
            AppTempDB db = AppTempDB.getDatabase(context);
            ActivationRepository activationRepository = new ActivationRepository(db.activationDao());
            BatchRepository batchRepository = new BatchRepository(db.batchDao());
            SalePrintHelper salePrintHelper = new SalePrintHelper();
            List<TransactionEntity> transactionList = db.transactionDao().getTransactionsByUUID(uuid);
            TransactionEntity transaction = transactionList.get(0);
            Intent resultIntent = new Intent();
            if (transaction != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("ResponseCode", ResponseCode.SUCCESS.ordinal());
                bundle.putInt("PaymentStatus", 0);
                bundle.putInt("Amount", transaction.getUlAmount());
                bundle.putString("customerSlipData", salePrintHelper.getFormattedText(new SampleReceipt(transaction.getBaPAN(), "OWNER NAME", transaction.getUlAmount(), activationRepository, batchRepository), transaction, TransactionCode.SALE, SlipType.CARDHOLDER_SLIP, context, 1, 2));
                bundle.putString("merchantSlipData", salePrintHelper.getFormattedText(new SampleReceipt(transaction.getBaPAN(), "OWNER NAME", transaction.getUlAmount(), activationRepository, batchRepository), transaction, TransactionCode.SALE, SlipType.MERCHANT_SLIP, context, 1, 2));
                bundle.putInt("BatchNo", transaction.getBatchNo());
                bundle.putInt("TxnNo", transaction.getUlGUP_SN());
                bundle.putInt("SlipType", SlipType.BOTH_SLIPS.value);
                bundle.putBoolean("IsSlip", true);
                resultIntent.putExtras(bundle);
            }

            resultIntent.setAction("check_sale_result");
            resultIntent.setPackage("com.tokeninc.sardis.paymentgateway");
            Log.d("intent_control", resultIntent.toString());
            context.sendBroadcast(resultIntent);
        }
    }
}
