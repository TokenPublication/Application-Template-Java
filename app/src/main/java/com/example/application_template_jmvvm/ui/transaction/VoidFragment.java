package com.example.application_template_jmvvm.ui.transaction;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.response.TransactionResponse;
import com.example.application_template_jmvvm.domain.service.TransactionResponseListener;
import com.example.application_template_jmvvm.domain.service.TransactionService;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.domain.adapter.TransactionsRecycleAdapter;
import com.example.application_template_jmvvm.domain.contentValHelper;
import com.example.application_template_jmvvm.domain.printHelpers.PrintHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.ArrayList;
import java.util.List;

public class VoidFragment extends Fragment implements InfoDialogListener {

    private MainActivity mainActivity;
    private CardViewModel cardViewModel;
    private TransactionViewModel transactionViewModel;
    private BatchViewModel batchViewModel;
    private InfoDialog dialog;
    int amount;
    private RecyclerView rvTransactions;
    private List<TransactionEntity> transactionList = new ArrayList<>();
    private TransactionService transactionService = new TransactionService();

    public VoidFragment(MainActivity mainActivity, CardViewModel cardViewModel, TransactionViewModel transactionViewModel, BatchViewModel batchViewModel) {
        this.mainActivity = mainActivity;
        this.cardViewModel = cardViewModel;
        this.transactionViewModel = transactionViewModel;
        this.batchViewModel = batchViewModel;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_void, container, false);
        boolean empty = transactionViewModel.isTransactionListEmpty();
        if(empty){
            dialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Info, getString(R.string.no_trans_found), false);
            new Handler().postDelayed(() -> {
                dialog.dismiss();
                mainActivity.setResult(Activity.RESULT_CANCELED);
                mainActivity.finish();
            }, 2000);
        }
        else{
            final boolean[] isCancelled = {false};
            dialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Processing, "Processing", false);
            CountDownTimer timer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    isCancelled[0] = true;
                    dialog.update(InfoDialog.InfoType.Declined, "Connect Failed");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (dialog != null) {
                            dialog.dismiss();   //TODO Backpressed
                            mainActivity.finish();
                        }
                    }, 2000);
                }
            };
            timer.start();
            cardViewModel.initializeCardServiceBinding(mainActivity);

            cardViewModel.getIsCardServiceConnect().observe(getViewLifecycleOwner(), isConnected -> {
                if (isConnected && !isCancelled[0]) {
                    timer.cancel();
                    dialog.update(InfoDialog.InfoType.Confirmed, "Connected to Service");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        cardViewModel.readCard(amount);
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            // Do something after the delay
                            dialog.dismiss();
                        }, 1000);
                    }, 2000);
                }
            });
            cardViewModel.getCardLiveData().observe(getViewLifecycleOwner(), card -> {
                if (card != null) {
                    setView(card);
                }
            });
        }
        rvTransactions = view.findViewById(R.id.rvTransactions);
        return view;
    }

    public void setView(ICCCard card) {
        Log.d("Card Data", card.getCardNumber());
        transactionList = transactionViewModel.getTransactionsByCardNo(card.getCardNumber());
        TransactionsRecycleAdapter adapter = new TransactionsRecycleAdapter(transactionList, transactionViewModel, this);
        rvTransactions.setAdapter(adapter);
        rvTransactions.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    public void startVoid(TransactionEntity transactionEntity) {
        ContentValues values = contentValHelper.contentValCreator(transactionEntity);
        transactionService.doInBackground(values, TransactionCode.VOID, transactionViewModel,
                batchViewModel.getBatchRepository(), new TransactionResponseListener() {
                    @Override
                    public void onComplete(TransactionResponse response) {
                        finishVoid(response);
                    }
                });
    }

    public void finishVoid(TransactionResponse response) {      //TODO Slip ayarlanacak.
        Log.d("RspCode:",response.getOnlineTransactionResponse().getmResponseCode().toString());
        PrintHelper.PrintError();
        mainActivity.setResult(Activity.RESULT_OK);
        mainActivity.finish();
    }

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }


}