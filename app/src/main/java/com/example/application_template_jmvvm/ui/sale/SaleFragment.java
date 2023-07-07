package com.example.application_template_jmvvm.ui.sale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application_template_jmvvm.data.model.card.CardServiceResult;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.type.CardReadType;
import com.example.application_template_jmvvm.data.model.type.PaymentTypes;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.type.SlipType;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.batch.BatchViewModel;
import com.example.application_template_jmvvm.ui.activation.ActivationViewModel;
import com.example.application_template_jmvvm.utils.printHelpers.StringHelper;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.Objects;

public class SaleFragment extends Fragment implements InfoDialogListener {

    int amount;
    View view;
    String uuid;
    private ActivationViewModel activationViewModel;
    private TransactionViewModel transactionViewModel;
    private BatchViewModel batchViewModel;
    private CardViewModel cardViewModel;
    private MainActivity mainActivity;
    Spinner spinner;
    InfoDialog infoDialog;
    private boolean QRisSuccess = true;
    private boolean isCancelable = true;

    public SaleFragment(MainActivity mainActivity, ActivationViewModel activationViewModel, CardViewModel cardViewModel,
                        TransactionViewModel transactionViewModel, BatchViewModel batchViewModel) {
        this.mainActivity = mainActivity;
        this.activationViewModel = activationViewModel;
        this.cardViewModel = cardViewModel;
        this.transactionViewModel = transactionViewModel;
        this.batchViewModel = batchViewModel;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = mainActivity.getIntent();
        Bundle bundle = intent.getExtras();
        amount = bundle.getInt("Amount");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnSale).setOnClickListener(v -> {
            mainActivity.readCard(getViewLifecycleOwner(), amount);
            cardViewModel.getCardLiveData().observe(getViewLifecycleOwner(), card -> {
                if (card.getmCardReadType() != CardReadType.QrPay.getType()) {
                    mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed, "Read Successful", false);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> doSale(card, this.getViewLifecycleOwner()), 2000);
                } else {
                    QrSale(card);
                }
            });
        });

        view.findViewById(R.id.btnSuccess).setOnClickListener(v -> prepareDummyResponse(ResponseCode.SUCCESS));
        view.findViewById(R.id.btnError).setOnClickListener(v -> prepareDummyResponse(ResponseCode.ERROR));
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> prepareDummyResponse(ResponseCode.CANCELLED));
        view.findViewById(R.id.btnOffline_decline).setOnClickListener(v -> prepareDummyResponse(ResponseCode.OFFLINE_DECLINE));
        view.findViewById(R.id.btnUnable_decline).setOnClickListener(v -> prepareDummyResponse(ResponseCode.UNABLE_DECLINE));
        view.findViewById(R.id.btnOnline_decline).setOnClickListener(v -> prepareDummyResponse(ResponseCode.ONLINE_DECLINE));

        prepareSpinner(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale, container, false);
        this.view = view;
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.mainActivity, android.R.layout.simple_spinner_dropdown_item, items);
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

    public void prepareDummyResponse(ResponseCode code) {
        // Dummy response with payment type, for 1000TR device
        int paymentType = PaymentTypes.CREDITCARD.type;

        CheckBox cbMerchant = view.findViewById(R.id.cbMerchant);
        CheckBox cbCustomer = view.findViewById(R.id.cbCustomer);

        SlipType slipType = SlipType.NO_SLIP;
        if (cbMerchant.isChecked() && cbCustomer.isChecked())
            slipType = SlipType.BOTH_SLIPS;
        else if (cbMerchant.isChecked())
            slipType = SlipType.MERCHANT_SLIP;
        else if (cbCustomer.isChecked())
            slipType = SlipType.CARDHOLDER_SLIP;

        if(code == ResponseCode.SUCCESS){
            String text = spinner.getSelectedItem().toString();

            if (text.equals(String.valueOf(PaymentTypes.TRQRCREDITCARD)))
                paymentType = PaymentTypes.TRQRCREDITCARD.type;
            else if (text.equals(String.valueOf(PaymentTypes.TRQRFAST)))
                paymentType = PaymentTypes.TRQRFAST.type;
            else if(text.equals(String.valueOf(PaymentTypes.TRQRMOBILE)))
                paymentType = PaymentTypes.TRQRMOBILE.type;
            else if (text.equals(String.valueOf(PaymentTypes.TRQROTHER)))
                paymentType = PaymentTypes.TRQROTHER.type;
            else if (text.equals(String.valueOf(PaymentTypes.OTHER)))
                paymentType = PaymentTypes.OTHER.type;
        }

        onSaleResponseRetrieved(amount, code, true, slipType, "1234 **** **** 7890", "OWNER NAME", paymentType);
    }

    public void onSaleResponseRetrieved(Integer price, ResponseCode code, Boolean hasSlip, SlipType slipType, String cardNo, String ownerName, int paymentType) {
        transactionViewModel.prepareDummyResponse(activationViewModel.getActivationRepository(), batchViewModel.getBatchRepository(),
                                                mainActivity, price, code, hasSlip, slipType, cardNo, ownerName, paymentType);
        transactionViewModel.getIntentLiveData().observe(getViewLifecycleOwner(), resultIntent -> {
            if (code == ResponseCode.SUCCESS) {
                mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed, "Success", false);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    mainActivity.setResult(Activity.RESULT_OK,resultIntent);    //TODO Result gönderilecek.
                    mainActivity.finish();
                }, 2000);
            }
            mainActivity.setResult(Activity.RESULT_OK,resultIntent);
            mainActivity.finish();
        });
    }

    public void cardReaderGIB() {
        cardViewModel.getCardLiveData().observe(mainActivity, card -> {
            mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed, "Read Successful", false);
            new Handler(Looper.getMainLooper()).postDelayed(() -> doSale(card, mainActivity), 2000);
        });
    }

    public void doSale(ICCCard card, LifecycleOwner viewLifecycleOwner) {
        uuid = mainActivity.getIntent().getExtras().getString("UUID");
        transactionViewModel.TransactionRoutine(card, uuid, mainActivity, null, null, TransactionCode.SALE,
                                                activationViewModel.getActivationRepository(), batchViewModel.getBatchRepository());
        transactionViewModel.getInfoDialogLiveData().observe(viewLifecycleOwner, infoDialogData -> {
            if (Objects.equals(infoDialogData.getText(), "Progress")) {
                infoDialog = mainActivity.showInfoDialog(infoDialogData.getType(), infoDialogData.getText(), false);
            } else {
                infoDialog.update(infoDialogData.getType(), infoDialogData.getText());
                if (infoDialogData.getType() == InfoDialog.InfoType.Confirmed) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {}, 2000);
                }
            }
        });
        transactionViewModel.getIntentLiveData().observe(viewLifecycleOwner, resultIntent -> {
            mainActivity.setResult(Activity.RESULT_OK,resultIntent);
            mainActivity.finish();
        });
    }

    public void QrSale(ICCCard card) {
        InfoDialog dialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Progress, "Please Wait", true);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            cardViewModel.getCardServiceBinding().showQR("PLEASE READ THE QR CODE", StringHelper.getAmount(amount), "QR Code Test"); // Shows QR on the back screen
            dialog.setQr("QR Code Test", "Waiting For the QR Code to Read"); // Shows the same QR on Info Dialog
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (QRisSuccess) {
                    mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed,"QR " +getString(R.string.trans_successful), false);
                    isCancelable = false;
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        doSale(card, this.getViewLifecycleOwner());
                    }, 3000);
                }
            },5000);
        },2000);
        dialog.setDismissedListener(() -> {
            if (isCancelable) {
                QRisSuccess = false;
                mainActivity.setResult(Activity.RESULT_CANCELED);
                mainActivity.callbackMessage(CardServiceResult.USER_CANCELLED);
            }
        });
    }

    @Override
    public void confirmed(int i) {}

    @Override
    public void canceled(int i) {}
}