package com.example.application_template_jmvvm.ui.transaction;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import dagger.hilt.android.AndroidEntryPoint;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.application_template_jmvvm.data.service.TransactionResponseListener;
import com.example.application_template_jmvvm.domain.entity.ICCCard;
import com.example.application_template_jmvvm.domain.entity.MSRCard;
import com.example.application_template_jmvvm.domain.entity.PaymentTypes;
import com.example.application_template_jmvvm.domain.entity.ResponseCode;
import com.example.application_template_jmvvm.domain.entity.SlipType;
import com.example.application_template_jmvvm.data.database.activation.ActivationDB;
import com.example.application_template_jmvvm.data.database.transaction.TransactionCol;
import com.example.application_template_jmvvm.domain.entity.TransactionCode;
import com.example.application_template_jmvvm.domain.helper.printHelpers.PrintHelper;
import com.example.application_template_jmvvm.domain.helper.StringHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.response.TransactionResponse;
import com.example.application_template_jmvvm.data.service.TransactionService;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;

import java.util.Locale;

@AndroidEntryPoint
public class SaleFragment extends Fragment{

    private TransactionService transactionService = new TransactionService();
    int amount;
    String uuid;
    private Bundle bundle;
    private Intent intent;
    private TransactionViewModel transactionViewModel;
    private BatchViewModel batchViewModel;
    private MainActivity main;
    Spinner spinner;

    public SaleFragment(MainActivity mainActivity, TransactionViewModel transactionViewModel, BatchViewModel batchViewModel) {
        this.main = mainActivity;
        this.transactionViewModel = transactionViewModel;
        this.batchViewModel = batchViewModel;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = main.getIntent();
        bundle = intent.getExtras();
        amount = bundle.getInt("Amount");
        uuid = intent.getExtras().getString("UUID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale, container, false);
        view.findViewById(R.id.btnSale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //TODO service için boolean observer kaldırılacak.
                transactionViewModel.getIsCardServiceConnected().observe(getViewLifecycleOwner(), isConnected -> {
                    if (isConnected) {
                        transactionViewModel.readCard(amount);
                    } else {
                        transactionViewModel.initializeCardServiceBinding();
                    }
                });
                transactionViewModel.setIsCardServiceConnected(true);
                transactionViewModel.getCardLiveData().observe(getViewLifecycleOwner(), card -> {
                    if (card != null) {
                        doSale(card);
                    }
                });
            }
        });

        view.findViewById(R.id.btnSuccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Response Code", ResponseCode.SUCCESS.name());
            }
        });

        view.findViewById(R.id.btnError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Response Code", ResponseCode.ERROR.name());
            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Response Code", ResponseCode.CANCELLED.name());
            }
        });

        view.findViewById(R.id.btnOffline_decline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Response Code", ResponseCode.OFFLINE_DECLINE.name());
            }
        });

        view.findViewById(R.id.btnUnable_decline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Response Code", ResponseCode.UNABLE_DECLINE.name());
            }
        });

        view.findViewById(R.id.btnOnline_decline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Response Code", ResponseCode.ONLINE_DECLINE.name());
            }
        });

        prepareSpinner(view);           //TODO Response Code ayarlanacak.
        return view;
    }

    private void prepareSpinner(View view) {
        spinner = view.findViewById(R.id.spinner);
        String[] items = new String[]{
                String.valueOf(PaymentTypes.CREDITCARD),
                String.valueOf(PaymentTypes.TRQRCREDITCARD),
                String.valueOf(PaymentTypes.TRQRFAST),
                String.valueOf(PaymentTypes.TRQRMOBILE),
                String.valueOf(PaymentTypes.TRQROTHER),
                String.valueOf(PaymentTypes.OTHER)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.main, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(listener);            //TODO PaymentType finishe eklenecek.
    }

    private final AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void doSale(ICCCard card) {
        ContentValues values = transactionViewModel.getCardModel().prepareContentValues(card, uuid);  //CardViewModel implemente edilecek.
        transactionService.doInBackground(main, getContext(), values, TransactionCode.SALE, transactionViewModel,
                batchViewModel, new TransactionResponseListener() {
                    @Override
                    public void onComplete(TransactionResponse response) {
                        finishSale(response);
                    }
                });
    }

    private void finishSale(TransactionResponse transactionResponse) {
        //TODO Response code ve transaction code ayarlanacak. Transaction code enum olmayacak.Activation ve Transaction viewmodel açılacak.,
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        SlipType slipType = SlipType.BOTH_SLIPS;
        String cardNo = (String) transactionResponse.getContentValues().get(TransactionCol.col_baPAN.name());
        bundle.putInt("ResponseCode", transactionResponse.getOnlineTransactionResponse().getmResponseCode().ordinal()); // #1 Response Code
        bundle.putString("CardOwner", "OWNER NAME"); // Optional
        bundle.putString("CardNumber", cardNo); // Optional, Card No can be masked
        bundle.putInt("PaymentStatus", 0); // #2 Payment Status
        bundle.putInt("Amount", (Integer) transactionResponse.getContentValues().get(TransactionCol.col_ulAmount.name())); // #3 Amount
        //bundle.putInt("BatchNo", databaseHelper.getBatchNo());
        bundle.putString("CardNo", StringHelper.MaskTheCardNo((String) transactionResponse.getContentValues().get(TransactionCol.col_baPAN.name()))); //#5 Card No "MASKED"
        bundle.putString("MID", ActivationDB.getInstance(getContext()).getMerchantId()); //#6 Merchant ID
        bundle.putString("TID", ActivationDB.getInstance(getContext()).getTerminalId()); //#7 Terminal ID
        //bundle.putInt("TxnNo", databaseHelper.getTxNo());
        bundle.putInt("SlipType", slipType.value);
        //bundle.putString("RefNo", String.valueOf(databaseHelper.getSaleID()));
        //bundle.putInt("PaymentType", paymentType);

        if (slipType == SlipType.CARDHOLDER_SLIP || slipType == SlipType.BOTH_SLIPS) {      //TODO AppTemp cannot cast hatası incelenecek.
            //bundle.putString("customerSlipData", SalePrintHelper.getFormattedText(getSampleReceipt(cardNo, "OWNER NAME"), SlipType.CARDHOLDER_SLIP, main, 1, 2));
        }
        if (slipType == SlipType.MERCHANT_SLIP || slipType == SlipType.BOTH_SLIPS) {
            //bundle.putString("merchantSlipData", SalePrintHelper.getFormattedText(getSampleReceipt(cardNo, "OWNER NAME"), SlipType.MERCHANT_SLIP, main, 1, 2));
        }
        bundle.putString("ApprovalCode", getApprovalCode());
        resultIntent.putExtras(bundle);
        main.setResult(Activity.RESULT_OK, resultIntent);
        main.finish();
        for (int i = 0; i <= 10; i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        PrintHelper.PrintSuccess();
        PrintHelper.PrintError();
    }

    private String getApprovalCode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        int approvalCode = sharedPref.getInt("ApprovalCode", 0);
        sharedPref.edit().putInt("ApprovalCode", ++approvalCode).apply();
        return String.format(Locale.ENGLISH, "%06d", approvalCode);
    }
}