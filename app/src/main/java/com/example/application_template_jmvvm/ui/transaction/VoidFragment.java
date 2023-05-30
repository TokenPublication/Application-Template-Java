package com.example.application_template_jmvvm.ui.transaction;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.domain.entity.CardReadType;
import com.example.application_template_jmvvm.domain.entity.ICCCard;
import com.example.application_template_jmvvm.domain.entity.MSRCard;
import com.example.application_template_jmvvm.domain.helper.adapter.TransactionsRecycleAdapter;
import com.example.application_template_jmvvm.data.database.DatabaseOperations;
import com.example.application_template_jmvvm.data.database.transaction.TransactionCol;
import com.example.application_template_jmvvm.data.database.transaction.TransactionDB;
import com.example.application_template_jmvvm.domain.helper.printHelpers.PrintHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.PosTxnFragment;
import com.google.gson.Gson;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VoidFragment extends Fragment implements InfoDialogListener {

    private TransactionViewModel mViewModel;
    private InfoDialog dialog;
    int amount;
    private RecyclerView rvTransactions;
    private List<TransactionEntity> transactionList;
    private MainActivity main;

    public VoidFragment(MainActivity mainActivity) {
        this.main = mainActivity;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        mViewModel.setter(main);
        transactionList = new ArrayList<>();
        CheckTable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_void, container, false);
        if (mViewModel.getIsCardServiceConnected().getValue() == false){
            mViewModel.initializeCardServiceBinding();
        }
        mViewModel.setIsCardServiceConnected(true);
        mViewModel.readCard(amount);
        mViewModel.getCardLiveData().observe(getViewLifecycleOwner(), card -> {
            if (card != null) {
                setView(card);
            }
        });
        rvTransactions = view.findViewById(R.id.rvTransactions);
        return view;
    }

    public void CheckTable(){
        boolean empty = mViewModel.isTransactionListEmpty();
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
        transactionList = mViewModel.getTransactionsByCardNo(card.getCardNumber());
        TransactionsRecycleAdapter adapter = new TransactionsRecycleAdapter(transactionList,mViewModel,this);
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