package com.example.application_template_jmvvm;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.model.type.CardReadType;
import com.example.application_template_jmvvm.ui.service.ServiceViewModel;
import com.example.application_template_jmvvm.ui.posTxn.PosTxnFragment;
import com.example.application_template_jmvvm.ui.posTxn.batch.BatchViewModel;
import com.example.application_template_jmvvm.ui.activation.ActivationViewModel;
import com.example.application_template_jmvvm.ui.activation.SettingsFragment;
import com.example.application_template_jmvvm.ui.posTxn.refund.RefundFragment;
import com.example.application_template_jmvvm.ui.posTxn.voidOperation.VoidFragment;
import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;
import com.example.application_template_jmvvm.ui.sale.SaleFragment;
import com.example.application_template_jmvvm.ui.trigger.TriggerFragment;
import com.example.application_template_jmvvm.ui.trigger.TriggerViewModel;
import com.example.application_template_jmvvm.utils.ExtraContentInfo;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;
import com.tokeninc.libtokenkms.TokenKMS;

import org.json.JSONObject;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * This is the Main Activity class, all operations are run here because this
 * application is designed as a single-activity architecture
 * It's @AndroidEntryPoint because, we get ViewModel inside of class,
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements InfoDialogListener {
    public ActivationViewModel activationViewModel;
    private CardViewModel cardViewModel;
    public BatchViewModel batchViewModel;
    private TransactionViewModel transactionViewModel;
    private TriggerViewModel triggerViewModel;
    private ServiceViewModel serviceViewModel;
    private InfoDialog infoDialog;
    private TokenKMS tokenKMS;

    /**
     * This function is overwritten to continue the activity where it was left when
     * the configuration is changed (i.e screen rotation)
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        startActivity();
    }

    /**
     * This is the onCreate method for create the viewModels and build config.
     * Also, it binds the card service for after.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity();
    }

    public void startActivity() {
        buildConfigs();

        activationViewModel = new ViewModelProvider(this).get(ActivationViewModel.class);
        batchViewModel = new ViewModelProvider(this).get(BatchViewModel.class);
        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        triggerViewModel = new ViewModelProvider(this).get(TriggerViewModel.class);
        serviceViewModel = new ViewModelProvider(this).get(ServiceViewModel.class);

        infoDialog = showInfoDialog(InfoDialog.InfoType.Connecting, getString(R.string.connecting), false);
        serviceViewModel.ServiceRoutine(this);
        serviceViewModel.getInfoDialogLiveData().observe(this, infoDialogData -> infoDialog.update(infoDialogData.getType(), infoDialogData.getText()));
        serviceViewModel.getIsConnectedLiveData().observe(this, isConnected -> {
            infoDialog.dismiss();
            actionControl(getIntent().getAction());
            initializeCardService(this);
        });
    }

    /**
     * Firstly, added TR1000 and TR400 configurations to build.gradle file. After that,
     * related to Build Variant (400TRDebug or 1000TRDebug) the manifest file created with apk
     * and the app name in manifest file will be 1000TR or 400TR.
    */
    private void buildConfigs() {
        if (BuildConfig.FLAVOR.equals("TR1000")) {
            Log.v("TR1000 APP", "Application Template for 1000TR");
        }
        if (BuildConfig.FLAVOR.equals("TR400")) {
            Log.v("TR400 APP", "Application Template for 400TR");
        }
    }

    /**
     * This function for call some operations or change the view with respect to action of the current intent
     */
    private void actionControl(@Nullable String action) {
        if (Objects.equals(action, getString(R.string.Sale_Action))) {
            saleActionReceived();
        }

        else if (Objects.equals(action, getString(R.string.BatchClose_Action))) {
            if (transactionViewModel.isTransactionListEmpty()) {
                showInfoDialog(InfoDialog.InfoType.Warning, getString(R.string.no_trans_found), false);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    setResult(Activity.RESULT_OK);
                    finish();
                }, 2000);
            } else {
                PosTxnFragment posTxnFragment = new PosTxnFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
                posTxnFragment.doBatchClose(this, true);
            }
        }

        else if (Objects.equals(action, getString(R.string.PosTxn_Action))) {
            PosTxnFragment posTxnFragment = new PosTxnFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
            replaceFragment(R.id.container, posTxnFragment, false);
        }

        else if (Objects.equals(action, getString(R.string.Settings_Action))) {
            SettingsFragment settingsFragment = new SettingsFragment(this, activationViewModel, cardViewModel);
            replaceFragment(R.id.container, settingsFragment, false);
        }

        else if (Objects.equals(action, getString(R.string.Refund_Action))) {
            refundActionReceived();
        }

        else if (Objects.equals(action, getString(R.string.Parameter_Action))) {
            TriggerFragment triggerFragment = new TriggerFragment(this, triggerViewModel);
            replaceFragment(R.id.container, triggerFragment, false);
        }

        else {
            PosTxnFragment posTxnFragment = new PosTxnFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
            replaceFragment(R.id.container, posTxnFragment, false);
        }
    }

    /**
     * This method for bind the card service. Also it has a 30 seconds timeout for handle onFailure
     * at card service bind.
     * @param lifecycleOwner for observe the cardServiceConnect liveData from CardViewModel.
     */
    public void initializeCardService(LifecycleOwner lifecycleOwner) {
        final boolean[] isCancelled = {false};
        CountDownTimer timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                isCancelled[0] = true;
                infoDialog.update(InfoDialog.InfoType.Declined, getString(R.string.card_service_error));
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (infoDialog != null) {
                        infoDialog.dismiss();
                        finish();
                    }
                }, 2000);
            }
        };
        timer.start();
        cardViewModel.initializeCardServiceBinding(this);
        cardViewModel.getMessageLiveData().observe(this, message -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());

        cardViewModel.getIsCardServiceConnect().observe(lifecycleOwner, isConnected -> {
            if (isConnected && !isCancelled[0]) {
                timer.cancel();
            }
        });
    }

    /**
     * This method for read card. But it has the null check for card service binding.
     * If it is null, we showed card service error to user.
     * Also, we observe the card service result and response message live data for handle error,
     * cancel cases etc.
     */
    public void readCard(LifecycleOwner lifecycleOwner, int amount, TransactionCode transactionCode) {
        if (cardViewModel.getCardServiceBinding() != null) {
            cardViewModel.readCard(amount, transactionCode);
            cardViewModel.getResponseMessageLiveData().observe(lifecycleOwner, responseCode -> responseMessage(responseCode, ""));
        } else {
            initializeCardService(lifecycleOwner);
            readCard(lifecycleOwner, amount, transactionCode);
        }
    }

    /**
     * This function is called when action == "SALE". Action could be "SALE" in 3 different scenarios
     * 1- When the customer clicks on credit card in pgw and then selects Application Template as a Banking Application
     * ( If the device has only Application Template as a Banking Application, pgw automatically directs user to Application Template when clicking credit card)
     * 2- When GiB sends a sale request
     * 3- When the card is read by payment gateway and the Application Template is the only issuer of this card, in this situation
     * payment gateway automatically directs the sale to Application Template and sale action received.
     */
    private void saleActionReceived() {
        SaleFragment saleTxnFragment = new SaleFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
        SharedPreferences sharedPreferences = getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        boolean isEnabled = sharedPreferences.getBoolean("demo_mode", false);
        Bundle bundle = getIntent().getExtras();
        String cardData = bundle != null ? bundle.getString("CardData") : null;
        int amount = getIntent().getExtras().getInt("Amount");

        if (cardData != null && !cardData.equals(" ")) {
            if (isEnabled) {
                saleTxnFragment.cardReader(this, amount, false);
            } else {
                replaceFragment(R.id.container, saleTxnFragment, false);
            }
        }

        if (getIntent().getExtras() != null) {
            int cardReadType = getIntent().getExtras().getInt("CardReadType");
            if (cardReadType == CardReadType.ICC.getType()) {
                cardViewModel.setGIB(true);
                saleTxnFragment.cardReader(this, amount, true);
            } else {
                if (isEnabled) {
                    saleTxnFragment.cardReader(this, amount, false);
                } else {
                    replaceFragment(R.id.container, saleTxnFragment, false);
                }
            }
        } else {
            if (isEnabled) {
                saleTxnFragment.cardReader(this, amount, false);
            } else {
                replaceFragment(R.id.container, saleTxnFragment, false);
            }
        }
    }

    /**
     * This function only calls whenever Refund Action is received.
     * If there is no RefundInfo on current intent, it will show info dialog with No Refund Intent for 2 seconds.
     * else -> it transforms refundInfo to JSON object to parse it easily. Then get ReferenceNo and BatchNo
     * Then it compares intent batch number with current Batch Number from database, if they are equal then start Void operation
     * else start refund operation.
     */
    private void refundActionReceived() {
        if (getIntent().getExtras() == null || getIntent().getExtras().getString("RefundInfo") == null) {
            responseMessage(ResponseCode.ERROR, getString(R.string.refund_info_not_found));
        } else {
            try {
                String refundData = new JSONObject(getIntent().getExtras().getString("RefundInfo")).toString();
                String refNo = new JSONObject(refundData).getString("RefNo");
                int amount = Integer.parseInt(new JSONObject(refundData).getString("Amount"));
                if (new JSONObject(refundData).getInt("BatchNo") == batchViewModel.getBatchNo()) { //void
                    readCard(this, amount, TransactionCode.VOID);
                    VoidFragment voidFragment = new VoidFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
                    voidFragment.gibVoid(refNo, true);
                } else {
                    Bundle refundBundle = new Bundle();
                    refundBundle.putInt(ExtraContentInfo.orgAmount, amount);
                    refundBundle.putInt(ExtraContentInfo.refAmount, amount);
                    refundBundle.putString(ExtraContentInfo.refNo, refNo);
                    refundBundle.putString(ExtraContentInfo.authCode, new JSONObject(refundData).getString("AuthCode"));
                    refundBundle.putString(ExtraContentInfo.tranDate, new JSONObject(refundData).getString("TranDate"));
                    RefundFragment refundFragment = new RefundFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
                    refundFragment.cardReader(this, refundBundle, true);
                }
            } catch (Exception e) {
                responseMessage(ResponseCode.ERROR, getString(R.string.refund_info_not_found));
            }
        }
    }

    /**
     * It takes @param responseCode and message and control it with switch case.
     * Related to it's value, the info dialog shows in screen and activity will
     * finish with intent contains response code. Also with message parameter, the
     * error messages can seen at screen.
     */
    public void responseMessage(ResponseCode responseCode, String message) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        switch (responseCode) {
            case ERROR:
                if (!Objects.equals(message, "")) {
                    showInfoDialog(InfoDialog.InfoType.Error, getString(R.string.error) + ": "+ message, true);
                } else {
                    showInfoDialog(InfoDialog.InfoType.Error, getString(R.string.error), true);
                }
                break;
            case CANCELLED:
                showInfoDialog(InfoDialog.InfoType.Warning, getString(R.string.cancelled), true);
                break;
            case OFFLINE_DECLINE:
                showInfoDialog(InfoDialog.InfoType.Warning, getString(R.string.offline_decline), true);
            case UNABLE_DECLINE:
            case ONLINE_DECLINE:
                showInfoDialog(InfoDialog.InfoType.Declined, getString(R.string.declined), true);
                break;
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            bundle.putInt("ResponseCode", responseCode.ordinal());
            intent.putExtras(bundle);
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        },2000);
    }

    /**
     * Method for get TokenKMS object out of this file.
     */
    public TokenKMS getTokenKMS() {
        return tokenKMS;
    }

    /**
     * Shows a dialog to the user which asks for a confirmation.
     * Dialog will be dismissed automatically when user taps on to confirm/cancel button.
     */
    public InfoDialog showConfirmationDialog(InfoDialog.InfoType type, String title, String info, InfoDialog.InfoDialogButtons buttons, int arg, InfoDialogListener listener) {
        InfoDialog dialog = InfoDialog.newInstance(type, title, info, buttons, arg, listener);
        dialog.show(getSupportFragmentManager(), "");
        return dialog;
    }

    /**
     * This is for showing infoDialog
     */
    public InfoDialog showInfoDialog(InfoDialog.InfoType type, String text, boolean isCancelable) {
        InfoDialog fragment = InfoDialog.newInstance(type, text, isCancelable);
        fragment.show(getSupportFragmentManager(), "");
        return fragment;
    }

    /**
     * This is for replacing the fragment or adding to the backstack.
     * It's name is replaceFragment but it can be use for add the fragment to the backstack
     * with @param addToBackStack
     */
    public void replaceFragment(@IdRes Integer resourceId, Fragment fragment, Boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(resourceId, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void confirmed(int i) { }

    @Override
    public void canceled(int i) { }
}
