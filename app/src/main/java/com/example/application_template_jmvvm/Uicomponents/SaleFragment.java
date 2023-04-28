package com.example.application_template_jmvvm.Uicomponents;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelStoreOwner;

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
import com.example.application_template_jmvvm.Entity.SlipType;
import com.example.application_template_jmvvm.Helpers.DataBase.activation.ActivationCol;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionCol;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionDB;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.Services.TransactionService;
import com.example.application_template_jmvvm.Viewmodels.SaleViewModel;
import com.google.gson.Gson;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SaleFragment extends Fragment implements CardServiceListener{

    private boolean isCardServiceConnected;
    private CardServiceListener cardServiceListener;
    private CardServiceBinding cardServiceBinding;
    private TransactionService transactionService = new TransactionService();
    int cardReadType = 0;
    int amount;
    String uuid;
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
        cardServiceListener = this;

        Bundle bundle = main.getIntent().getExtras();
        amount = bundle.getInt("Amount");
        uuid = main.getIntent().getExtras().getString("UUID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale, container, false);
        view.findViewById(R.id.btnSale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCardServiceConnected){
                    readCard();
                }
                else {
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
        prepareSpinner(view);
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

    private void prepareSpinner(View view){
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
        spinner.setOnItemSelectedListener(listener);
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
        Log.d("Connected to Card Service","");
        //TODO: Config files
        //main.setConfig();
        //main.setCLConfig();
        isCardServiceConnected = true;
        readCard();
    }

    @Override
    public void onCardDataReceived(String cardData) {
        Log.d("Card Data", cardData);
        try {
            JSONObject json = new JSONObject(cardData);
            int type = json.getInt("mCardReadType");

            if (type == CardReadType.CLCard.value) {
                cardReadType = CardReadType.CLCard.value;
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
                Log.d(card.getCardNumber(),"Card Number: ");
                Log.d(String.valueOf(amount),"Amount: ");
            }
            else if (type == CardReadType.ICC.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
            }
            else if (type == CardReadType.ICC2MSR.value || type == CardReadType.MSR.value || type == CardReadType.KeyIn.value) {
                MSRCard card = new Gson().fromJson(cardData, MSRCard.class);
                this.msrCard = card;
                cardServiceBinding.getOnlinePIN(amount, card.getCardNumber(), 0x0A01, 0, 4, 8, 30);
            }
            doSale(card);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPinReceived(String s) {

    }

    @Override
    public void onICCTakeOut() {

    }

    public void readCard() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("forceOnline", 1);
            obj.put("zeroAmount", 0);
            obj.put("fallback", 1);
            obj.put("cardReadTypes",6);
            obj.put("qrPay", 1);

            cardServiceBinding.getCard(amount, 40, obj.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doSale(ICCCard card) {
        ContentValues values = new ContentValues();  //TODO RxJava content value
        values.put(TransactionCol.col_uuid.name(), uuid);
        values.put(TransactionCol.col_bCardReadType.name(), card.getmCardReadType());
        values.put(TransactionCol.col_bTransCode.name(), 55);
        values.put(TransactionCol.col_ulAmount.name(), card.getmTranAmount1());
        values.put(TransactionCol.col_ulAmount2.name(), card.getmTranAmount1());
        values.put(TransactionCol.col_baPAN.name(), card.getmCardNumber());
        values.put(TransactionCol.col_baExpDate.name(), card.getmExpireDate());
        values.put(TransactionCol.col_baDate.name(), card.getDateTime().substring(0,8));
        values.put(TransactionCol.col_baTime.name(), card.getDateTime().substring(8));
        values.put(TransactionCol.col_baTrack2.name(), card.getmTrack2Data());
        values.put(TransactionCol.col_baCustomName.name(), card.getmTrack1CustomerName());
        values.put(TransactionCol.col_baRspCode.name(), 3);
        values.put(TransactionCol.col_bInstCnt.name(), 10);
        values.put(TransactionCol.col_ulInstAmount.name(), card.getmTranAmount1());
        values.put(TransactionCol.col_baTranDate.name(), card.getDateTime());
        values.put(TransactionCol.col_baTranDate2.name(), card.getDateTime());
        values.put(TransactionCol.col_baHostLogKey.name(), "1020304050");
        values.put(TransactionCol.col_authCode.name(), "10203040");
        values.put(TransactionCol.col_aid.name(), card.getAID2());
        values.put(TransactionCol.col_aidLabel.name(), card.getAIDLabel());
        values.put(TransactionCol.col_baCVM.name(), card.getCVM());
        values.put(TransactionCol.col_SID.name(), card.getSID());
        transactionService.doInBackground(main,getContext(),values);
        TransactionDB.getInstance(getContext()).insertTransaction(values);
    }
}