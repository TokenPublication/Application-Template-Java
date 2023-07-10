package com.example.application_template_jmvvm;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.application_template_jmvvm.data.model.card.CardServiceResult;
import com.example.application_template_jmvvm.data.model.type.CardReadType;
import com.example.application_template_jmvvm.ui.posTxn.PosTxnFragment;
import com.example.application_template_jmvvm.ui.posTxn.batch.BatchViewModel;
import com.example.application_template_jmvvm.ui.activation.ActivationViewModel;
import com.example.application_template_jmvvm.ui.activation.SettingsFragment;
import com.example.application_template_jmvvm.ui.posTxn.refund.RefundFragment;
import com.example.application_template_jmvvm.ui.posTxn.voidOperation.VoidFragment;
import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;
import com.example.application_template_jmvvm.ui.sale.SaleFragment;
import com.example.application_template_jmvvm.utils.ExtraContentInfo;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements InfoDialogListener {

    private FragmentManager fragmentManager;
    public ActivationViewModel activationViewModel;
    private CardViewModel cardViewModel;
    public BatchViewModel batchViewModel;
    private TransactionViewModel transactionViewModel;
    private InfoDialog infoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildConfigs();
        fragmentManager = getSupportFragmentManager();

        activationViewModel = new ViewModelProvider(this).get(ActivationViewModel.class);
        batchViewModel = new ViewModelProvider(this).get(BatchViewModel.class);
        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        actionControl(getIntent().getAction());
    }

    /**
     * Firstly, added TR1000 and TR400 configurations to build.gradle file. After that,
     * related to Build Variant (400TRDebug or 1000TRDebug) the manifest file created with apk
     * and the appname in manifest file will be 1000TR or 400TR.
    */
    private void buildConfigs() {
        if (BuildConfig.FLAVOR.equals("TR1000")) {
            Log.v("TR1000 APP", "Application Template for 1000TR");
        }
        if (BuildConfig.FLAVOR.equals("TR400")) {
            Log.v("TR400 APP", "Application Template for  400TR");
        }
    }

    private void actionControl(@Nullable String action) {
        if (Objects.equals(action, getString(R.string.Sale_Action))) {
            saleActionReceived();
        }

        else if (Objects.equals(action, getString(R.string.BatchClose_Action))) {
            if (transactionViewModel.isTransactionListEmpty()) {
                showInfoDialog(InfoDialog.InfoType.Warning, "No Transaction", false);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    setResult(Activity.RESULT_CANCELED);
                    callbackMessage(CardServiceResult.ERROR);
                }, 2000);
            } else {
                PosTxnFragment posTxnFragment = new PosTxnFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
                posTxnFragment.batchClose(this);
            }
        }

        else if (Objects.equals(action, getString(R.string.PosTxn_Action))) {
            PosTxnFragment posTxnFragment = new PosTxnFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
            replaceFragment(R.id.container, posTxnFragment, false);
        }

        else if (Objects.equals(action, getString(R.string.Settings_Action))) {
            SettingsFragment settingsFragment = new SettingsFragment(this, activationViewModel);
            replaceFragment(R.id.container, settingsFragment, false);
        }

        else if (Objects.equals(action, getString(R.string.Refund_Action))) {
            refundActionReceived();
        }

        else {
            PosTxnFragment posTxnFragment = new PosTxnFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
            replaceFragment(R.id.container, posTxnFragment, false);
        }
    }

    public void readCard(LifecycleOwner lifecycleOwner, int amount) {
        final boolean[] isCancelled = {false};
        infoDialog = showInfoDialog(InfoDialog.InfoType.Processing, "Processing", false);
        CountDownTimer timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                isCancelled[0] = true;
                infoDialog.update(InfoDialog.InfoType.Declined, "Connect Failed");
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

        cardViewModel.getIsCardServiceConnect().observe(lifecycleOwner, isConnected -> {
            if (isConnected && !isCancelled[0]) {
                timer.cancel();
                infoDialog.update(InfoDialog.InfoType.Confirmed, "Connected to Service");
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    cardViewModel.readCard(amount);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> infoDialog.dismiss(),1000);
                    cardViewModel.getCardServiceResultLiveData().observe(lifecycleOwner, this::callbackMessage);
                }, 2000);
            }
        });
    }

    private void saleActionReceived() {
        SaleFragment saleTxnFragment = new SaleFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
        Bundle bundle = getIntent().getExtras();
        String cardData = bundle != null ? bundle.getString("CardData") : null;
        int amount = getIntent().getExtras().getInt("Amount");

        if (cardData != null && !cardData.equals(" ")) {
            replaceFragment(R.id.container, saleTxnFragment, false);
        }

        if (getIntent().getExtras() != null) {
            int cardReadType = getIntent().getExtras().getInt("CardReadType");
            if (cardReadType == CardReadType.ICC.getType()) {
                cardViewModel.setGIB(true);
                readCard(this, amount);
                saleTxnFragment.cardReaderGIB();
            } else {
                replaceFragment(R.id.container, saleTxnFragment, false);
            }
        } else {
            replaceFragment(R.id.container, saleTxnFragment, false);
        }
    }

    private void refundActionReceived() {
        if (getIntent().getExtras() == null || getIntent().getExtras().getString("RefundInfo") == null) {
            callbackMessage(CardServiceResult.ERROR);
        } else {
            try {
                String refundData = new JSONObject(getIntent().getExtras().getString("RefundInfo")).toString();
                String refNo = new JSONObject(refundData).getString("RefNo");
                int amount = Integer.parseInt(new JSONObject(refundData).getString("Amount"));
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()) + " " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                if (new JSONObject(refundData).getInt("BatchNo") == batchViewModel.getBatchNo()) { //void
                    readCard(this, 0);
                    VoidFragment voidFragment = new VoidFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
                    voidFragment.gibVoid(refNo);
                } else {
                    Bundle refundBundle = new Bundle();
                    refundBundle.putInt(ExtraContentInfo.orgAmount, amount);
                    refundBundle.putInt(ExtraContentInfo.refAmount, amount);
                    refundBundle.putString(ExtraContentInfo.refNo, refNo);
                    refundBundle.putString(ExtraContentInfo.authCode, new JSONObject(refundData).getString("AuthCode"));
                    refundBundle.putString(ExtraContentInfo.tranDate, date);
                    readCard(this, amount);
                    RefundFragment refundFragment = new RefundFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
                    refundFragment.gibRefund(refundBundle);
                }
            } catch (Exception e) {
                callbackMessage(CardServiceResult.ERROR);
            }
        }
    }

    public void callbackMessage(CardServiceResult cardServiceResult) {
        switch (cardServiceResult) {
            case USER_CANCELLED:
                Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show();
                break;
            case ERROR_TIMEOUT:
                Toast.makeText(this,"Error Timeout", Toast.LENGTH_LONG).show();
                break;
            case ERROR:
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show();
                break;
        }
        finish();
    }

    public InfoDialog showConfirmationDialog(InfoDialog.InfoType type, String title, String info, InfoDialog.InfoDialogButtons buttons, int arg, InfoDialogListener listener) {
        InfoDialog dialog = InfoDialog.newInstance(type, title, info, buttons, arg, listener);
        dialog.show(getSupportFragmentManager(), "");
        return dialog;
    }

    public InfoDialog showInfoDialog(InfoDialog.InfoType type, String text, boolean isCancelable) {
        InfoDialog fragment = InfoDialog.newInstance(type, text, isCancelable);
        fragment.show(getSupportFragmentManager(), "");
        return fragment;
    }

    public InfoDialog getInfoDialog() {
        return infoDialog;
    }

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

    public void showDialog(InfoDialog infoDialog) {
        infoDialog.show(fragmentManager, "");
    }

    public void setConfig() {
        try {
            InputStream xmlStream = getApplicationContext().getAssets().open("custom_emv_config.xml");
            BufferedReader r = new BufferedReader(new InputStreamReader(xmlStream));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                Log.d("emv_config", "conf line: " + line);
                total.append(line).append('\n');
            }
            int setConfigResult = cardViewModel.getCardServiceBinding().setEMVConfiguration(total.toString());
            Toast.makeText(getApplicationContext(), "setEMVConfiguration res=" + setConfigResult, Toast.LENGTH_SHORT).show();
            Log.d("emv_config", "setEMVConfiguration: " + setConfigResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCLConfig() {
        try {
            InputStream xmlCLStream = getApplicationContext().getAssets().open("custom_emv_cl_config.xml");
            BufferedReader rCL = new BufferedReader(new InputStreamReader(xmlCLStream));
            StringBuilder totalCL = new StringBuilder();
            for (String line; (line = rCL.readLine()) != null; ) {
                Log.d("emv_config", "conf line: " + line);
                totalCL.append(line).append('\n');
            }
            int setCLConfigResult = cardViewModel.getCardServiceBinding().setEMVCLConfiguration(totalCL.toString());
            Toast.makeText(getApplicationContext(), "setEMVCLConfiguration res=" + setCLConfigResult, Toast.LENGTH_SHORT).show();
            Log.d("emv_config", "setEMVCLConfiguration: " + setCLConfigResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void confirmed(int i) {}

    @Override
    public void canceled(int i) {}
}
