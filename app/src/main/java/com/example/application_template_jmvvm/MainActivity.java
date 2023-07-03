package com.example.application_template_jmvvm;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.application_template_jmvvm.data.model.card.CardServiceResult;
import com.example.application_template_jmvvm.ui.posTxn.PosTxnFragment;
import com.example.application_template_jmvvm.ui.posTxn.batch.BatchViewModel;
import com.example.application_template_jmvvm.ui.activation.ActivationViewModel;
import com.example.application_template_jmvvm.ui.activation.SettingsFragment;
import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;
import com.example.application_template_jmvvm.ui.sale.SaleFragment;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;
import com.tokeninc.cardservicebinding.CardServiceBinding;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements InfoDialogListener {

    public CardServiceBinding cardServiceBinding;
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
        /*
        Firstly, added TR1000 and TR400 configurations to build.gradle file. After that,
        related to Build Variant (400TRDebug or 1000TRDebug) the manifest file created with apk
        and the appname in manifest file will be 1000TR or 400TR.
        */
        if (BuildConfig.FLAVOR.equals("TR1000")) {
            Log.v("TR1000 APP","Application Template for 1000TR");
        }
        if(BuildConfig.FLAVOR.equals("TR400")) {
            Log.v("TR400 APP","Application Template for  400TR");
        }
        fragmentManager = getSupportFragmentManager();

        activationViewModel = new ViewModelProvider(this).get(ActivationViewModel.class);
        batchViewModel = new ViewModelProvider(this).get(BatchViewModel.class);
        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        actionControl(getIntent().getAction());
    }

    private void actionControl(@Nullable String action){
        if (Objects.equals(action, getString(R.string.Sale_Action))){
            SaleFragment saleTxnFragment = new SaleFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
            replaceFragment(R.id.container, saleTxnFragment, false);
        }

        else if (Objects.equals(action, getString(R.string.PosTxn_Action))){
            PosTxnFragment posTxnFragment = new PosTxnFragment(this, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
            replaceFragment(R.id.container, posTxnFragment, false);
        }

        else if (Objects.equals(action, getString(R.string.Settings_Action))){
            SettingsFragment settingsFragment = new SettingsFragment(this, activationViewModel);
            replaceFragment(R.id.container, settingsFragment, false);
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
                    cardViewModel.getCardServiceResultLiveData().observe(lifecycleOwner, cardServiceResult -> {
                        if (cardServiceResult.resultCode() == CardServiceResult.USER_CANCELLED.resultCode()) {
                            Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show();
                        }
                        if (cardServiceResult.resultCode() == CardServiceResult.ERROR_TIMEOUT.resultCode()) {
                            Toast.makeText(this,"Error Timeout", Toast.LENGTH_LONG).show();
                        }
                        if (cardServiceResult.resultCode() == CardServiceResult.ERROR.resultCode()) {
                            Toast.makeText(this,"Error", Toast.LENGTH_LONG).show();
                        }
                        finish();
                    });
                }, 2000);
            }
        });
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
            int setConfigResult = cardServiceBinding.setEMVConfiguration(total.toString());
            Toast.makeText(getApplicationContext(), "setEMVConfiguration res=" + setConfigResult, Toast.LENGTH_SHORT).show();
            Log.d("emv_config", "setEMVConfiguration: " + setConfigResult);
        }
        catch (Exception e) {
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
            int setCLConfigResult = cardServiceBinding.setEMVCLConfiguration(totalCL.toString());
            Toast.makeText(getApplicationContext(), "setEMVCLConfiguration res=" + setCLConfigResult, Toast.LENGTH_SHORT).show();
            Log.d("emv_config", "setEMVCLConfiguration: " + setCLConfigResult);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }
}
