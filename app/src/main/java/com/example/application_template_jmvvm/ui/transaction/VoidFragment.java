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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.gson.Gson;
import com.token.uicomponents.infodialog.InfoDialog;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class VoidFragment extends Fragment implements CardServiceListener{

    private boolean isCardServiceConnected;
    private CardServiceListener cardServiceListener;
    private CardServiceBinding cardServiceBinding;
    private ICCCard card;
    private MSRCard msrCard;

    int amount;
    private RecyclerView rvTransactions;
    private ArrayList<ContentValues> transactionList;

    private MainActivity main;

    public VoidFragment(MainActivity mainActivity) {
        this.main = mainActivity;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardServiceListener = this;
        transactionList = new ArrayList<>();
        CheckTable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_void, container, false);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void CheckTable(){
        TransactionDB databaseHelper = new TransactionDB(main);
        boolean empty = databaseHelper.isEmpty();
        if(empty){
            InfoDialog dialog = main.showInfoDialog(InfoDialog.InfoType.Info, getString(R.string.no_trans_found), false);
            new Handler().postDelayed(() -> {
                dialog.dismiss();
                main.setResult(Activity.RESULT_CANCELED);
            }, 2000);
        }
        else{
            readDataSQLite();
        }
    }

    public void readDataSQLite(){
        if (isCardServiceConnected) {
            readCard();
        } else {
            cardServiceBinding = new CardServiceBinding(main, cardServiceListener);
        }
    }

    @Override
    public void onCardServiceConnected() {
        Log.d("Connected to Card Service","");
        //TODO: Config files
        //main.setConfig();
        //main.setCLConfig();
        isCardServiceConnected = true;
        readCard();
    }

    @Override
    public void onCardDataReceived(String cardData) {
        Log.d("Card Data", cardData);
        try {
            JSONObject json = new JSONObject(cardData);
            int type = json.getInt("mCardReadType");
            String cardNo = json.getString("mCardNumber");
            if (type == CardReadType.CLCard.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
            } else if (type == CardReadType.ICC.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
            } else if (type == CardReadType.ICC2MSR.value || type == CardReadType.MSR.value || type == CardReadType.KeyIn.value) {
                MSRCard card = new Gson().fromJson(cardData, MSRCard.class);
                this.msrCard = card;
                cardServiceBinding.getOnlinePIN(amount, card.getCardNumber(), 0x0A01, 0, 4, 8, 30);
            }
            TransactionDB databaseHelper = new TransactionDB(main);
            transactionList = databaseHelper.getTransactionsByCardNo(cardNo);
            TransactionsRecycleAdapter adapter = new TransactionsRecycleAdapter(transactionList,this);
            rvTransactions.setAdapter(adapter);
            rvTransactions.setLayoutManager(new LinearLayoutManager(main));
            ViewGroup parent = (ViewGroup) rvTransactions.getParent();
            parent.addView(rvTransactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doVoid(ContentValues contentValues, String authCode){
        TransactionDB databaseHelper = new TransactionDB(main);
        DatabaseOperations.update(databaseHelper.getWritableDatabase(),"TRANSACTIONS",TransactionCol.col_authCode.name() + " = " + authCode,contentValues);
        finishVoid(contentValues);
    }

    @Override
    public void onPinReceived(String s) {

    }

    @Override
    public void onICCTakeOut() {

    }

    private void finishVoid(ContentValues contentValues) {      //TODO TransactionResponse eklenecek. Service kısmı düzenlenecek. Slip ayarlanacak.
        Log.d("Void Operation", " ContentVals: " + contentValues);
        PrintHelper.PrintError();
        main.setResult(Activity.RESULT_OK);
        main.finish();
    }

    public void readCard() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("forceOnline", 1);
            obj.put("zeroAmount", 0);
            obj.put("fallback", 1);
            obj.put("cardReadTypes", 6);
            obj.put("showAmount", 0);
            obj.put("qrPay", 1);

            cardServiceBinding.getCard(amount, 40, obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}