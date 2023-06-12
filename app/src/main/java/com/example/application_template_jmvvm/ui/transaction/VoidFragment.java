package com.example.application_template_jmvvm.ui.transaction;

import android.app.Activity;
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
import com.example.application_template_jmvvm.domain.entity.ICCCard;
import com.example.application_template_jmvvm.domain.helper.adapter.TransactionsRecycleAdapter;
import com.example.application_template_jmvvm.domain.helper.printHelpers.PrintHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.ArrayList;
import java.util.List;

public class VoidFragment extends Fragment implements InfoDialogListener {

    private TransactionViewModel transactionViewModel;
    private InfoDialog dialog;
    int amount;
    private RecyclerView rvTransactions;
    private List<TransactionEntity> transactionList;
    private MainActivity main;

    public VoidFragment(MainActivity mainActivity, TransactionViewModel transactionViewModel) {
        this.main = mainActivity;
        this.transactionViewModel = transactionViewModel;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionList = new ArrayList<>();
        CheckTable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_void, container, false);
        if (transactionViewModel.getIsCardServiceConnected().getValue() == false){
            transactionViewModel.initializeCardServiceBinding();
        }
        transactionViewModel.setIsCardServiceConnected(true);
        transactionViewModel.readCard(amount);
        transactionViewModel.getCardLiveData().observe(getViewLifecycleOwner(), card -> {
            if (card != null) {
                setView(card);
            }
        });
        rvTransactions = view.findViewById(R.id.rvTransactions);
        return view;
    }

    public void CheckTable(){
        boolean empty = transactionViewModel.isTransactionListEmpty();
        if(empty){
            dialog = main.showInfoDialog(InfoDialog.InfoType.Info, getString(R.string.no_trans_found), false);
            new Handler().postDelayed(() -> {
                dialog.dismiss();
                main.setResult(Activity.RESULT_CANCELED);
            }, 2000);
        }
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

    public void finishVoid() {      //TODO TransactionResponse eklenecek.Slip ayarlanacak.
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