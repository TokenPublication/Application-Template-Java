package com.example.application_template_jmvvm.Uicomponents;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.Handler;

import com.example.application_template_jmvvm.Helpers.DataBase.DatabaseHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.Viewmodels.PosTxnViewModel;
import com.example.application_template_jmvvm.Viewmodels.SaleViewModel;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.infodialog.InfoDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    DatabaseHelper databaseHelper;
    private List<IListMenuItem> menuItems = new ArrayList<>();
    private PosTxnViewModel posTxnViewModel;
    private SaleViewModel saleViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        posTxnViewModel = new ViewModelProvider(this).get(PosTxnViewModel.class);

        actionControl(getIntent().getAction());
    }

    /*public void replaceFragment(Fragment fragment) {
        new Thread(() -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }).start();
    }*/

    protected InfoDialog showInfoDialog(InfoDialog.InfoType type, String text, boolean isCancelable) {
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
            SaleFragment saleTxnFragment = new SaleFragment(this);
            replaceFragment(R.id.container, saleTxnFragment, true);
        }

        else if (Objects.equals(action, getString(R.string.PosTxn_Action))){
            PosTxnFragment posTxnFragment = new PosTxnFragment(this);
            replaceFragment(R.id.container, posTxnFragment, false);
        }

        else if (Objects.equals(action, getString(R.string.Settings_Action))){
            SettingsFragment settingsFragment = new SettingsFragment(this,getApplicationContext());
            replaceFragment(R.id.container, settingsFragment, false);
        }

        else {
            PosTxnFragment posTxnFragment = new PosTxnFragment(this);
            replaceFragment(R.id.container, posTxnFragment, false);
        }
    }
}
