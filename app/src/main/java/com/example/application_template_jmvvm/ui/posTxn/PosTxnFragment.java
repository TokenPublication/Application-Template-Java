package com.example.application_template_jmvvm.ui.posTxn;

import android.app.Activity;
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

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.batch.BatchViewModel;
import com.example.application_template_jmvvm.ui.activation.ActivationViewModel;
import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;
import com.example.application_template_jmvvm.utils.objects.MenuItem;
import com.example.application_template_jmvvm.ui.example.ExampleFragment;
import com.example.application_template_jmvvm.ui.posTxn.refund.RefundFragment;
import com.example.application_template_jmvvm.ui.posTxn.voidOperation.VoidFragment;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PosTxnFragment extends Fragment implements InfoDialogListener {

    private ActivationViewModel activationViewModel;
    private BatchViewModel batchViewModel;
    private CardViewModel cardViewModel;
    private TransactionViewModel transactionViewModel;
    private MainActivity mainActivity;
    private InfoDialog infoDialog;
    private ListMenuFragment listMenuFragment;

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
        return inflater.inflate(R.layout.fragment_postxn, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<IListMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.transactions), iListMenuItem -> { //TODO bakılacak.
        }));

        menuItems.add(new MenuItem(getString(R.string.refund), iListMenuItem -> {
            RefundFragment RefundFragment = new RefundFragment(this.mainActivity, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
            mainActivity.replaceFragment(R.id.container, RefundFragment, true);
        }));

        menuItems.add(new MenuItem(getString(R.string.void_transaction), iListMenuItem -> {
            VoidFragment VoidFragment = new VoidFragment(this.mainActivity, activationViewModel, cardViewModel, transactionViewModel, batchViewModel);
            mainActivity.replaceFragment(R.id.container, VoidFragment, false);
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
            } else {
                mainActivity.showConfirmationDialog(InfoDialog.InfoType.Info, "Batch Close",
                        "Implement Batch Close ?", InfoDialog.InfoDialogButtons.Both, 1,
                        new InfoDialogListener() {
                            @Override
                            public void confirmed(int i) {
                                batchClose(listMenuFragment.getViewLifecycleOwner());
                            }

                            @Override
                            public void canceled(int i) {}
                    });
            }
        }));

        menuItems.add(new MenuItem(getString(R.string.examples), iListMenuItem -> {
            ExampleFragment ExampleFragment = new ExampleFragment(this.mainActivity, cardViewModel);
            mainActivity.replaceFragment(R.id.container, ExampleFragment, true);
        }));

        menuItems.add(new MenuItem("Slip Tekrarı", iListMenuItem -> {
            batchViewModel.getBatchRepository().printSlip(batchViewModel.getPreviousBatchSlip(), mainActivity);
            mainActivity.finish();
        }));

        listMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.pos_operations), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, listMenuFragment, false);
    }

    public void batchClose(LifecycleOwner lifecycleOwner) {
        batchViewModel.BatchCloseRoutine(mainActivity, activationViewModel.getActivationRepository(), transactionViewModel.getTransactionRepository());
        batchViewModel.getInfoDialogLiveData().observe(lifecycleOwner, infoDialogData -> {
            if (Objects.equals(infoDialogData.getText(), "Progress")) {
                infoDialog = mainActivity.showInfoDialog(infoDialogData.getType(), infoDialogData.getText(), false);
            } else {
                infoDialog.update(infoDialogData.getType(), infoDialogData.getText());
                if (infoDialogData.getType() == InfoDialog.InfoType.Confirmed) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {}, 2000);
                }
            }
        });
        batchViewModel.getIntentLiveData().observe(lifecycleOwner, resultIntent -> {
            mainActivity.setResult(Activity.RESULT_OK,resultIntent);
            mainActivity.finish();
        });
    }

    @Override
    public void confirmed(int i) {}

    @Override
    public void canceled(int i) {}
}