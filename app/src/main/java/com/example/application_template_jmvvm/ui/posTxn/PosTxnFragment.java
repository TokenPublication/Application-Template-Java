package com.example.application_template_jmvvm.ui.posTxn;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.settings.ActivationViewModel;
import com.example.application_template_jmvvm.ui.transaction.CardViewModel;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModel;
import com.example.application_template_jmvvm.ui.utils.MenuItem;
import com.example.application_template_jmvvm.ui.example.ExampleFragment;
import com.example.application_template_jmvvm.ui.transaction.RefundFragment;
import com.example.application_template_jmvvm.ui.transaction.VoidFragment;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PosTxnFragment extends Fragment implements InfoDialogListener{

    private ActivationViewModel activationViewModel;
    private BatchViewModel batchViewModel;
    private CardViewModel cardViewModel;
    private TransactionViewModel transactionViewModel;
    private MainActivity mainActivity;
    private InfoDialog infoDialog;
    private ListMenuFragment mListMenuFragment;

    public PosTxnFragment(MainActivity mainActivity, ActivationViewModel activationViewModel, CardViewModel cardViewModel,
                          TransactionViewModel transactionViewModel, BatchViewModel batchViewModel) {
        this.mainActivity = mainActivity;
        this.activationViewModel = activationViewModel;
        this.cardViewModel = cardViewModel;
        this.transactionViewModel = transactionViewModel;
        this.batchViewModel = batchViewModel;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postxn, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<IListMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.transactions), iListMenuItem -> {
        }));
        menuItems.add(new MenuItem(getString(R.string.refund), iListMenuItem -> {
            RefundFragment RefundFragment = new RefundFragment(this.mainActivity, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
            mainActivity.replaceFragment(R.id.container,RefundFragment,true);
        }));
        menuItems.add(new MenuItem(getString(R.string.void_transaction), iListMenuItem -> {
            VoidFragment VoidFragment = new VoidFragment(this.mainActivity, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);    //TODO backstack ayarlanacak.
            mainActivity.replaceFragment(R.id.container,VoidFragment,false);
        }));
        menuItems.add(new MenuItem(getString(R.string.batch_close), iListMenuItem -> {
            if (transactionViewModel.isTransactionListEmpty()) {
                InfoDialog infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Warning,
                        "No Transaction", false);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (infoDialog != null) {
                        infoDialog.dismiss();
                    }
                }, 2000);
            }
            else {
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Info, "Batch Close",
                        "Implement Batch Close ?", InfoDialog.InfoDialogButtons.Both, 1,
                        new InfoDialogListener() {
                            @Override
                            public void confirmed(int i) {
                                batchClose(mListMenuFragment);
                            }

                            @Override
                            public void canceled(int i) {}
                    });
            }
        }));
        menuItems.add(new MenuItem(getString(R.string.examples), iListMenuItem -> {
            ExampleFragment ExampleFragment = new ExampleFragment(this.mainActivity);
            mainActivity.replaceFragment(R.id.container,ExampleFragment,true);
        }));

        mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.pos_operations), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container,mListMenuFragment,false);
    }

    private void batchClose(ListMenuFragment listMenuFragment) {
        batchViewModel.BatchCloseRoutine(mainActivity, activationViewModel.getActivationRepository(), transactionViewModel.getTransactionRepository());
        batchViewModel.getShowDialogLiveData().observe(listMenuFragment.getViewLifecycleOwner(), text -> {
            if (text != null) {
                if (Objects.equals(text, "Progress")) {
                    infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Progress, text, false);
                } else {
                    infoDialog.update(InfoDialog.InfoType.Progress, text);
                }
                if (text.contains("Confirmed")) {
                    infoDialog.update(InfoDialog.InfoType.Confirmed, text);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {}, 2000);
                }
            }
        });
        batchViewModel.getIntentLiveData().observe(listMenuFragment.getViewLifecycleOwner(), resultIntent -> {
            mainActivity.setResult(Activity.RESULT_OK,resultIntent);
            mainActivity.finish();
        });
    }

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }
}