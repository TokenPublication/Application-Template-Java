package com.example.application_template_jmvvm.ui.posTxn;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.example.application_template_jmvvm.data.response.BatchCloseResponse;
import com.example.application_template_jmvvm.data.response.TransactionResponse;
import com.example.application_template_jmvvm.data.service.BatchCloseResponseListener;
import com.example.application_template_jmvvm.data.service.BatchCloseService;
import com.example.application_template_jmvvm.data.service.TransactionResponseListener;
import com.example.application_template_jmvvm.data.service.TransactionService;
import com.example.application_template_jmvvm.domain.entity.BatchResult;
import com.example.application_template_jmvvm.domain.entity.TransactionCode;
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

    private TransactionViewModel transactionViewModel;
    private BatchCloseService batchCloseService  = new BatchCloseService();
    private BatchViewModel batchViewModel;
    ListMenuFragment mListMenuFragment;
    private MainActivity main;

    public PosTxnFragment(MainActivity mainActivity, TransactionViewModel transactionViewModel, BatchViewModel batchViewModel) {
        this.main = mainActivity;
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
            RefundFragment RefundFragment = new RefundFragment(this.main, transactionViewModel, batchViewModel);
            main.replaceFragment(R.id.container,RefundFragment,true);
        }));
        menuItems.add(new MenuItem(getString(R.string.void_transaction), iListMenuItem -> {
            VoidFragment VoidFragment = new VoidFragment(this.main, transactionViewModel, batchViewModel);    //TODO backstack ayarlanacak.
            main.replaceFragment(R.id.container,VoidFragment,false);
        }));
        menuItems.add(new MenuItem(getString(R.string.batch_close), iListMenuItem -> {
            if (transactionViewModel.isTransactionListEmpty()) {
                InfoDialog infoDialog = main.showInfoDialog(InfoDialog.InfoType.Warning,
                        "No Transaction", false);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (infoDialog != null) {
                        infoDialog.dismiss();
                    }
                }, 2000);
            }
            else {
                main.showConfirmationDialog(InfoDialog.InfoType.Info, "Batch Close",
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
            ExampleFragment ExampleFragment = new ExampleFragment(this.main);
            main.replaceFragment(R.id.container,ExampleFragment,true);
        }));

        mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.pos_operations), true, R.drawable.token_logo_png);
        main.replaceFragment(R.id.container,mListMenuFragment,false);
    }

    private void batchClose() {
        batchCloseService.doInBackground(main, getContext(), transactionViewModel, batchViewModel,
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
        main.setResult(Activity.RESULT_OK,intent);
        for (int i = 0; i <= 10; i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        main.finish();
    }

}