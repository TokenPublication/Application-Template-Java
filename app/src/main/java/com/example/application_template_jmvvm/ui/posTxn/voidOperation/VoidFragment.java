package com.example.application_template_jmvvm.ui.posTxn.voidOperation;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.batch.BatchViewModel;
import com.example.application_template_jmvvm.ui.activation.ActivationViewModel;
import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.List;
import java.util.Objects;

public class VoidFragment extends Fragment implements InfoDialogListener {

    private MainActivity mainActivity;
    private ActivationViewModel activationViewModel;
    private BatchViewModel batchViewModel;
    private CardViewModel cardViewModel;
    private TransactionViewModel transactionViewModel;
    private InfoDialog infoDialog;
    private RecyclerView rvTransactions;

    public VoidFragment(MainActivity mainActivity, ActivationViewModel activationViewModel, CardViewModel cardViewModel,
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_void, container, false);
        boolean empty = transactionViewModel.isVoidListEmpty();
        if(empty) {
            infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Info, getString(R.string.no_trans_found), false);
            new Handler().postDelayed(() -> {
                infoDialog.dismiss();
                mainActivity.setResult(Activity.RESULT_CANCELED);
                mainActivity.finish();
            }, 2000);
        } else {
            mainActivity.readCard(getViewLifecycleOwner(), 0);
            cardViewModel.getCardLiveData().observe(getViewLifecycleOwner(), card -> {
                infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed, "Read Successful", false);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    setView(card);
                    infoDialog.dismiss();
                }, 2000);
            });
        }
        rvTransactions = view.findViewById(R.id.rvTransactions);
        return view;
    }

    public void setView(ICCCard card) {
        Log.d("Card Data", card.getCardNumber());
        List<TransactionEntity> transactionList = transactionViewModel.getTransactionsByCardNo(card.getCardNumber());
        TransactionsRecycleAdapter adapter = new TransactionsRecycleAdapter(transactionList, this);
        rvTransactions.setAdapter(adapter);
        rvTransactions.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    public void gibVoid(String refNo) {
        cardViewModel.getCardLiveData().observe(mainActivity, card -> {
            infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed, "Read Successful", false);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                List<TransactionEntity> transactionList = transactionViewModel.getTransactionRepository().getTransactionsByRefNo(refNo);
                TransactionEntity transaction = transactionList.get(0);
                if (transaction != null) {
                    if (Objects.equals(card.getCardNumber(), transaction.getBaPAN())) {
                        startVoid(mainActivity, transaction);
                    }
                }
                infoDialog.dismiss();
            }, 2000);
        });
    }

    public void startVoid(LifecycleOwner lifecycleOwner, TransactionEntity transactionEntity) {
        transactionViewModel.TransactionRoutine(null, null, mainActivity, transactionEntity, null, TransactionCode.VOID,
                                                activationViewModel.getActivationRepository(), batchViewModel.getBatchRepository());
        transactionViewModel.getInfoDialogLiveData().observe(lifecycleOwner, infoDialogData -> {
            if (Objects.equals(infoDialogData.getText(), "Progress")) {
                infoDialog = mainActivity.showInfoDialog(infoDialogData.getType(), infoDialogData.getText(), false);
            } else {
                infoDialog.update(infoDialogData.getType(), infoDialogData.getText());
                if (infoDialogData.getType() == InfoDialog.InfoType.Confirmed) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {}, 2000);
                }
            }
        });
        transactionViewModel.getIntentLiveData().observe(lifecycleOwner, resultIntent -> {
            mainActivity.setResult(Activity.RESULT_OK,resultIntent);
            mainActivity.finish();
        });
    }

    @Override
    public void confirmed(int i) {}

    @Override
    public void canceled(int i) {}

}