package com.example.application_template_jmvvm.ui.transaction;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.response.TransactionResponse;
import com.example.application_template_jmvvm.data.service.TransactionResponseListener;
import com.example.application_template_jmvvm.data.service.TransactionService;
import com.example.application_template_jmvvm.domain.entity.ICCCard;
import com.example.application_template_jmvvm.domain.entity.TransactionCode;
import com.example.application_template_jmvvm.domain.helper.adapter.TransactionsRecycleAdapter;
import com.example.application_template_jmvvm.domain.helper.contentValHelper;
import com.example.application_template_jmvvm.domain.helper.printHelpers.PrintHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.ArrayList;
import java.util.List;

public class VoidFragment extends Fragment implements InfoDialogListener {

    private MainActivity main;
    private CardViewModel cardViewModel;
    private TransactionViewModel transactionViewModel;
    private BatchViewModel batchViewModel;
    private InfoDialog dialog;
    int amount;
    private RecyclerView rvTransactions;
    private List<TransactionEntity> transactionList = new ArrayList<>();
    private TransactionService transactionService = new TransactionService();

    public VoidFragment(MainActivity mainActivity, CardViewModel cardViewModel, TransactionViewModel transactionViewModel, BatchViewModel batchViewModel) {
        this.main = mainActivity;
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
            dialog = main.showInfoDialog(InfoDialog.InfoType.Info, getString(R.string.no_trans_found), false);
            new Handler().postDelayed(() -> {
                dialog.dismiss();
                main.setResult(Activity.RESULT_CANCELED);
                main.finish();
            }, 2000);
        }
        else{
            if (!cardViewModel.getIsCardServiceConnected()){
                cardViewModel.initializeCardServiceBinding(main);
                cardViewModel.setIsCardServiceConnected(true);
            }
            cardViewModel.readCard(amount);
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
        TransactionsRecycleAdapter adapter = new TransactionsRecycleAdapter(transactionList, transactionViewModel,this);
        rvTransactions.setAdapter(adapter);
        rvTransactions.setLayoutManager(new LinearLayoutManager(main));
        ViewGroup parent = (ViewGroup) rvTransactions.getParent();
        parent.addView(rvTransactions);
    }

    public void startVoid(TransactionEntity transactionEntity) {
        ContentValues values = contentValHelper.contentValCreator(transactionEntity);
        transactionService.doInBackground(main, getContext(), values, TransactionCode.VOID, transactionViewModel,
                batchViewModel, new TransactionResponseListener() {
                    @Override
                    public void onComplete(TransactionResponse response) {
                        finishVoid(response);
                    }
                });
    }

    public void finishVoid(TransactionResponse response) {      //TODO Slip ayarlanacak.
        Log.d("RspCode:",response.getOnlineTransactionResponse().getmResponseCode().toString());
        PrintHelper.PrintError();
        main.setResult(Activity.RESULT_OK);
        main.finish();
    }

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }


}