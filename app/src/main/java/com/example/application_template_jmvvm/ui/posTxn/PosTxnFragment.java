package com.example.application_template_jmvvm.ui.posTxn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.model.response.BatchCloseResponse;
import com.example.application_template_jmvvm.domain.service.BatchCloseResponseListener;
import com.example.application_template_jmvvm.domain.service.BatchCloseService;
import com.example.application_template_jmvvm.data.model.code.BatchResult;
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

public class PosTxnFragment extends Fragment {

    private ActivationViewModel activationViewModel;
    private BatchViewModel batchViewModel;
    private CardViewModel cardViewModel;
    private TransactionViewModel transactionViewModel;
    private BatchCloseService batchCloseService  = new BatchCloseService();
    ListMenuFragment mListMenuFragment;
    private MainActivity mainActivity;

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
                                batchClose();
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

    private void batchClose() {
        batchCloseService.doInBackground(mainActivity, getContext(), transactionViewModel, batchViewModel,
                new BatchCloseResponseListener() {
                    @Override
                    public void onComplete(BatchCloseResponse response) {
                        finishBatchClose(response);
                    }
                });
    }

    private void finishBatchClose(BatchCloseResponse batchCloseResponse) {
        Log.d("finishBatch", "" + batchCloseResponse.getBatchResult());
        BatchResult responseCode = batchCloseResponse.getBatchResult();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("ResponseCode", responseCode.ordinal());
        intent.putExtras(bundle);
        mainActivity.setResult(Activity.RESULT_OK,intent);
        for (int i = 0; i <= 10; i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        mainActivity.finish();
    }

}