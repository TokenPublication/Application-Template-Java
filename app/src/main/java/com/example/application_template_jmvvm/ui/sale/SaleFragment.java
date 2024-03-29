package com.example.application_template_jmvvm.ui.sale;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.example.application_template_jmvvm.utils.ExtraContentInfo;
import com.example.application_template_jmvvm.utils.objects.MenuItem;
import com.example.application_template_jmvvm.utils.printHelpers.StringHelper;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.ListMenuFragment.MenuItemClickListener;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This Class is for Sale operations, It has dummy sale layout, which is the only view that we created.
 * Other ui elements come from ui library.
 */

public class SaleFragment extends Fragment implements InfoDialogListener {
    int amount;
    String uuid;
    String ZNO;
    String receiptNo;
    int instCount = 0;
    private ActivationViewModel activationViewModel;
    private TransactionViewModel transactionViewModel;
    private BatchViewModel batchViewModel;
    private CardViewModel cardViewModel;
    private MainActivity mainActivity;
    private ListMenuFragment listMenuFragment;
    private ListMenuFragment instFragment;
    Spinner spinner;
    InfoDialog infoDialog;
    private TransactionCode transactionCode;
    private boolean QRisSuccess = true;
    private boolean isCancelable = true;
    private boolean isApprove = false;

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
        amount = mainActivity.getIntent().getExtras().getInt("Amount");
    }

    /**
     * This override method for handle onClickListeners for buttons and amount showed in screen
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnSale).setOnClickListener(v -> cardReader(getViewLifecycleOwner(), amount, false));
        view.findViewById(R.id.btnSuccess).setOnClickListener(v -> prepareDummyResponse(view, ResponseCode.SUCCESS));
        view.findViewById(R.id.btnError).setOnClickListener(v -> prepareDummyResponse(view, ResponseCode.ERROR));
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> prepareDummyResponse(view, ResponseCode.CANCELLED));
        view.findViewById(R.id.btnOffline_decline).setOnClickListener(v -> prepareDummyResponse(view, ResponseCode.OFFLINE_DECLINE));
        view.findViewById(R.id.btnUnable_decline).setOnClickListener(v -> prepareDummyResponse(view, ResponseCode.UNABLE_DECLINE));
        view.findViewById(R.id.btnOnline_decline).setOnClickListener(v -> prepareDummyResponse(view, ResponseCode.ONLINE_DECLINE));

        TextView tvAmount = view.findViewById(R.id.tvAmount);
        tvAmount.setText(StringHelper.getAmount(amount));

        prepareSpinner(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sale, container, false);
    }

    /**
     * This method is for preparing Spinner on layout, Spinner contains 6 Payment Methods
     */
    private void prepareSpinner(View view) {
        spinner = view.findViewById(R.id.spinner);
        String[] items = new String[] {
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
        public void onNothingSelected(AdapterView<?> parent) { }
    };

    /**
     * This method for getting choices of user for payment and slip After that, it calls onSaleResponseRetrieved
     * method with this choices and dummy data for dummy sale.
     */
    public void prepareDummyResponse(View view, ResponseCode code) {
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

        if (code == ResponseCode.SUCCESS) {
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

    /**
     * This method for call readCard method in mainActivity and observe the card data for operations
     * like doSale or prepareSaleMenu
     * Flow: if card read type is QR -> QrSale
     * if card read type is ICC(read card part) -> PrepareSaleMenu
     * else (card read type is different or ICC(continue_emv) part) -> doSale.
     */
    public void cardReader(LifecycleOwner lifecycleOwner, int amount, boolean isGIB) {
        mainActivity.readCard(lifecycleOwner, amount, TransactionCode.SALE);
        cardViewModel.getCardLiveData().observe(lifecycleOwner, card -> {
            if (card != null) {
                if (card.getmCardReadType() == CardReadType.QrPay.getType()) {
                    QrSale();
                } else if (card.getmCardReadType() == CardReadType.ICC.getType() && !isApprove) {
                    isApprove = true;
                    cardViewModel.setCardLiveData(null);
                    prepareSaleMenu(card, isGIB);
                } else {
                    doSale(card, lifecycleOwner);
                }
            }
        });
    }

    /**
     * Flow: Clicking Sale Button > Read Card > On Card Data Received > (if card is ICC) -> here
     * It is a sale menu, if user click sale it calls cardReader() method for call the read card again.
     */
    private void prepareSaleMenu(ICCCard card, boolean isGIB) {
        boolean isInstallmentAllowed = false;
        boolean isLoyaltyAllowed = false;
        boolean isCampaignAllowed = false;
        if (!isGIB) {
            /*
              * TODO Developer, Check from BIN table, isInstallmentAllowed etc.
             */
            Log.d("Card Number", card.getCardNumber());
            isInstallmentAllowed = true;
            isLoyaltyAllowed = true;
            isCampaignAllowed = true;
        }

        List<IListMenuItem> menuItems = new ArrayList<>();

        menuItems.add(new MenuItem(mainActivity.getApplicationContext().getString(R.string.sale), iListMenuItem -> cardReader(listMenuFragment.getViewLifecycleOwner(), amount, isGIB)));
        if (isInstallmentAllowed) {
            menuItems.add(new MenuItem(mainActivity.getString(R.string.installment_sale), iListMenuItem -> showInstallments()));
        }
        if (isLoyaltyAllowed) {
            menuItems.add(new MenuItem(mainActivity.getString(R.string.loyalty_sale), iListMenuItem -> { }));
        }
        if (isCampaignAllowed) {
            menuItems.add(new MenuItem(mainActivity.getString(R.string.campaign_sale), iListMenuItem -> { }));
        }

        listMenuFragment = ListMenuFragment.newInstance(menuItems, mainActivity.getApplicationContext().getString(R.string.sale_type), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, listMenuFragment, false);
    }

    /**
     * This method for show installment count choice screen. After user selects instCount, it calls cardReader method for read card.
     */
    private void showInstallments() {
        MenuItemClickListener<MenuItem> listener = menuItem -> {
            String itemName = menuItem.getName();
            String[] itemNameSplit = itemName.split(" ");
            //TODO Developer, Check instCount from Parameter DB.
            instCount = Integer.parseInt(itemNameSplit[0]);
            cardReader(instFragment.getViewLifecycleOwner(), amount, false);
        };

        int maxInst = 18;
        List<IListMenuItem> menuItems = new ArrayList<>();
        for (int i = 2; i <= maxInst; i++) {
            MenuItem menuItem = new MenuItem(i + " " + mainActivity.getApplicationContext().getString(R.string.installment), listener);
            menuItems.add(menuItem);
        }

        instFragment = ListMenuFragment.newInstance(menuItems, mainActivity.getApplicationContext().getString(R.string.installment_sale), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, instFragment, true);
    }

    /**
     * This method for only dummy sale that did in Sale UI (success, error, declined)
     */
    public void onSaleResponseRetrieved(Integer price, ResponseCode code, Boolean hasSlip, SlipType slipType, String cardNo, String ownerName, int paymentType) {
        transactionViewModel.prepareDummyResponse(activationViewModel.getActivationRepository(), batchViewModel.getBatchRepository(),
                                                mainActivity, price, code, hasSlip, slipType, cardNo, ownerName, paymentType);
        transactionViewModel.getIntentLiveData().observe(getViewLifecycleOwner(), resultIntent -> {
            if (code == ResponseCode.SUCCESS) {
                mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed, getString(R.string.trans_successful), false);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    mainActivity.setResult(Activity.RESULT_OK, resultIntent);
                    mainActivity.finish();
                }, 2000);
            } else {
                mainActivity.responseMessage(code, "", resultIntent);
            }
        });
    }

    /**
     * Flow: Clicking Sale Button > Read Card > On Card Data Received > (if card is Contactless) -> here
     * If card is ICC -> Prepare Sale Menu > Click Sale > here
     * It calls transactionRoutine method in Transaction ViewModel for perform transaction.
     * Also, it updates the UI with UI statements at ViewModel. Finally, it gets intent data from viewModel
     * and finish the activity with this intent.
     */
    public void doSale(ICCCard card, LifecycleOwner viewLifecycleOwner) {
        Bundle bundle = getInfo();
        transactionViewModel.TransactionRoutine(card, mainActivity, null, bundle, transactionCode,
                activationViewModel.getActivationRepository(), batchViewModel.getBatchRepository(), null);
        transactionViewModel.getInfoDialogLiveData().observe(viewLifecycleOwner, infoDialogData -> {
            if (Objects.equals(infoDialogData.getText(), mainActivity.getString(R.string.connecting))) {
                infoDialog = mainActivity.showInfoDialog(infoDialogData.getType(), infoDialogData.getText(), false);
            } else {
                infoDialog.update(infoDialogData.getType(), infoDialogData.getText());
            }
        });
        transactionViewModel.getIntentLiveData().observe(viewLifecycleOwner, resultIntent -> {
            mainActivity.setResult(Activity.RESULT_OK, resultIntent);
            mainActivity.finish();
        });
    }

    /**
     * This method for getting info like uuid, zno coming from PGW and create a bundle related to this
     * data. Also, it updates transactionCode for sale operation related to instCount.
     */
    private Bundle getInfo() {
        Bundle bundle = new Bundle();
        uuid = mainActivity.getIntent().getExtras().getString("UUID");
        if (uuid != null) {
            Log.i("UUID of Sale, SaleFragment", uuid);
        }
        ZNO = mainActivity.getIntent().getExtras().getString("ZNO");
        receiptNo = mainActivity.getIntent().getExtras().getString("ReceiptNo");

        bundle.putString("UUID", uuid);
        if (ZNO != null && receiptNo != null) {
            bundle.putString("ZNO", ZNO);
            bundle.putString("ReceiptNo", receiptNo);
        }
        if (instCount > 0) {
            transactionCode = TransactionCode.INSTALLMENT_SALE;
            bundle.putInt(ExtraContentInfo.instCount, instCount);
        } else {
            transactionCode = TransactionCode.SALE;
        }
        return bundle;
    }

    /**
     * This method for shows the QR and perform dummy QR sale. Only updates the UI, not performing any sale operation.
     */
    public void QrSale() {
        InfoDialog dialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Progress, getString(R.string.please_wait), true);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            cardViewModel.getCardServiceBinding().showQR(getString(R.string.please_read_qr), StringHelper.getAmount(amount), "QR Code Test"); // Shows QR on the back screen
            dialog.setQr("QR Code Test", getString(R.string.waiting_qr_read)); // Shows the same QR on Info Dialog
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (QRisSuccess) {
                    mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed, "QR " + getString(R.string.trans_successful), false);
                    isCancelable = false;
                    new Handler(Looper.getMainLooper()).postDelayed(() -> mainActivity.finish(), 3000);
                }
            },5000);
        },2000);
        dialog.setDismissedListener(() -> {
            if (isCancelable) {
                QRisSuccess = false;
                mainActivity.responseMessage(ResponseCode.CANCELLED, "", null);
            }
        });
    }

    @Override
    public void confirmed(int i) { }

    @Override
    public void canceled(int i) { }
}
