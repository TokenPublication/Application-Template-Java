package com.example.application_template_jmvvm;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.application_template_jmvvm.data.database.AppTempDB;
import com.example.application_template_jmvvm.data.database.TransactionDatabase;
import com.example.application_template_jmvvm.data.database.repository.BatchRepository;
import com.example.application_template_jmvvm.data.database.repository.TransactionRepository;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModelFactory;
import com.example.application_template_jmvvm.ui.posTxn.PosTxnFragment;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.example.application_template_jmvvm.ui.settings.SettingsFragment;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModel;
import com.example.application_template_jmvvm.ui.transaction.SaleFragment;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModelFactory;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;
import com.tokeninc.cardservicebinding.CardServiceBinding;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    public CardServiceBinding cardServiceBinding;
    private FragmentManager fragmentManager;
    private BatchViewModel batchViewModel;
    private BatchViewModelFactory batchViewModelFactory;
    private TransactionViewModel transactionViewModel;
    private TransactionViewModelFactory transactionViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TransactionDatabase.getDatabase(this);
        AppTempDB.getDatabase(this);
        fragmentManager = getSupportFragmentManager();

        transactionViewModelFactory = new TransactionViewModelFactory(this, new TransactionRepository(AppTempDB.getDatabase(getApplication()).transactionDao()));
        transactionViewModel = new ViewModelProvider(this, transactionViewModelFactory).get(TransactionViewModel.class);

        batchViewModelFactory = new BatchViewModelFactory(new BatchRepository(AppTempDB.getDatabase(getApplication()).batchDao()));
        batchViewModel = new ViewModelProvider(this, batchViewModelFactory).get(BatchViewModel.class);

        actionControl(getIntent().getAction());
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

    protected void addFragment(@IdRes Integer resourceId, Fragment fragment, Boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(resourceId, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void replaceFragment(@IdRes Integer resourceId, Fragment fragment, Boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(resourceId, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void actionControl(@Nullable String action){
        if (Objects.equals(action, getString(R.string.Sale_Action))){
            SaleFragment saleTxnFragment = new SaleFragment(this, transactionViewModel, batchViewModel);
            replaceFragment(R.id.container, saleTxnFragment, false);
        }

        else if (Objects.equals(action, getString(R.string.PosTxn_Action))){
            PosTxnFragment posTxnFragment = new PosTxnFragment(this, transactionViewModel, batchViewModel);
            replaceFragment(R.id.container, posTxnFragment, false);
        }

        else if (Objects.equals(action, getString(R.string.Settings_Action))){
            SettingsFragment settingsFragment = new SettingsFragment(this,getApplicationContext());
            replaceFragment(R.id.container, settingsFragment, false);
        }

        else {
            PosTxnFragment posTxnFragment = new PosTxnFragment(this, transactionViewModel, batchViewModel);
            replaceFragment(R.id.container, posTxnFragment, false);
        }
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

}
