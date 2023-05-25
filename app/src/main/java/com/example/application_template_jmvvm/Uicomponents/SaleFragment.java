package com.example.application_template_jmvvm.Uicomponents;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelStoreOwner;

import android.preference.PreferenceManager;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.application_template_jmvvm.Entity.CardReadType;
import com.example.application_template_jmvvm.Entity.ICCCard;
import com.example.application_template_jmvvm.Entity.ICard;
import com.example.application_template_jmvvm.Entity.MSRCard;
import com.example.application_template_jmvvm.Entity.PaymentTypes;
import com.example.application_template_jmvvm.Entity.ResponseCode;
import com.example.application_template_jmvvm.Entity.SampleReceipt;
import com.example.application_template_jmvvm.Entity.SlipType;
import com.example.application_template_jmvvm.Helpers.DataBase.DatabaseHelper;
import com.example.application_template_jmvvm.Helpers.DataBase.activation.ActivationCol;
import com.example.application_template_jmvvm.Helpers.DataBase.activation.ActivationDB;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionCol;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionDB;
import com.example.application_template_jmvvm.Helpers.PrintHelpers.PrintHelper;
import com.example.application_template_jmvvm.Helpers.PrintHelpers.SalePrintHelper;
import com.example.application_template_jmvvm.Helpers.StringHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.Responses.TransactionResponse;
import com.example.application_template_jmvvm.Services.TransactionResponseListener;
import com.example.application_template_jmvvm.Services.TransactionService;
import com.example.application_template_jmvvm.Viewmodels.SaleViewModel;
import com.google.gson.Gson;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.infodialog.InfoDialog;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;

import org.json.JSONObject;

import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SaleFragment extends Fragment implements CardServiceListener {

    private boolean isCardServiceConnected;
    private CardServiceListener cardServiceListener;
    private CardServiceBinding cardServiceBinding;
    private TransactionService transactionService = new TransactionService();
    int cardReadType = 0;
    int amount;
    String uuid;
    private Bundle bundle;
    private Intent intent;
    private ICCCard card;
    private MSRCard msrCard;
    private SaleViewModel mViewModel;
    private MainActivity main;
    Spinner spinner;

    public SaleFragment(MainActivity mainActivity) {
        this.main = mainActivity;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(SaleViewModel.class);
        mViewModel.setMainActivity(main);
        cardServiceListener = this;
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
            public void onClick(View v) {
                if (isCardServiceConnected) {
                    readCard();
                } else {
                    cardServiceBinding = new CardServiceBinding(main, cardServiceListener);
                }
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.setActionName(getString(R.string.Sale_Action));
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    @Override
    public void onCardServiceConnected() {
        Log.d("Connected to Card Service", "");
        //TODO: Config files
        //main.setConfig();
        //main.setCLConfig();
        isCardServiceConnected = true;
        readCard();
    }

    public void readCard() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("forceOnline", 1);
            obj.put("zeroAmount", 0);
            obj.put("fallback", 1);
            obj.put("cardReadTypes", 6);
            obj.put("qrPay", 1);

            cardServiceBinding.getCard(amount, 40, obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCardDataReceived(String cardData) {
        Log.d("Card Data", cardData);
        try {
            JSONObject json = new JSONObject(cardData);
            int type = json.getInt("mCardReadType");
            if (type == CardReadType.CLCard.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
            } else if (type == CardReadType.ICC.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
            } else if (type == CardReadType.ICC2MSR.value || type == CardReadType.MSR.value || type == CardReadType.KeyIn.value) {
                MSRCard card = new Gson().fromJson(cardData, MSRCard.class);
                this.msrCard = card;
                cardServiceBinding.getOnlinePIN(amount, card.getCardNumber(), 0x0A01, 0, 4, 8, 30);
            }
            doSale(card);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSale(ICCCard card) {
        mViewModel.performSaleTransaction(card,transactionService, getContext(), uuid);
        mViewModel.getTransactionResponseLiveData().observe(getViewLifecycleOwner(), new Observer<TransactionResponse>() {
            @Override
            public void onChanged(TransactionResponse transactionResponse) {
                finishSale(transactionResponse);
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
        bundle.putInt("Amount2", (Integer) transactionResponse.getContentValues().get(TransactionCol.col_ulAmount2.name()));
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
            final int progress = i;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        PrintHelper.PrintSuccess();
        PrintHelper.PrintError();
    }

    private SampleReceipt getSampleReceipt(String cardNo, String ownerName) {
        SampleReceipt receipt = new SampleReceipt();
        receipt.setMerchantName("TOKEN FINTECH");
        receipt.setMerchantID(ActivationDB.getInstance(getContext()).getMerchantId());
        receipt.setPosID(ActivationDB.getInstance(getContext()).getTerminalId());
        receipt.setCardNo(StringHelper.maskCardNumber(cardNo));
        receipt.setFullName(ownerName);
        receipt.setAmount(StringHelper.getAmount(amount));
        //receipt.setGroupNo(String.valueOf(databaseHelper.getBatchNo()));
        receipt.setAid("A0000000000031010");
        //receipt.setSerialNo(String.valueOf(databaseHelper.getSaleID()));
        //receipt.setApprovalCode(StringHelper.GenerateApprovalCode(String.valueOf(databaseHelper.getBatchNo()), String.valueOf(databaseHelper.getTxNo()), String.valueOf(databaseHelper.getSaleID())));
        return receipt;
    }

    private String getApprovalCode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        int approvalCode = sharedPref.getInt("ApprovalCode", 0);
        sharedPref.edit().putInt("ApprovalCode", ++approvalCode).apply();
        return String.format(Locale.ENGLISH, "%06d", approvalCode);
    }

    @Override
    public void onPinReceived(String s) {

    }

    @Override
    public void onICCTakeOut() {

    }

}