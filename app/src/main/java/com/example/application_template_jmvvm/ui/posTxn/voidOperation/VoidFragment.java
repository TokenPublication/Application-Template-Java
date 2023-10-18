package com.example.application_template_jmvvm.ui.posTxn.voidOperation;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application_template_jmvvm.data.database.transaction.Transaction;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
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
    int amount = 0;

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

    /**
     * Firstly, it controls the transaction list for getting non-void operations. If it is empty,
     * showed No Transaction infoDialog.
     * Else, run the readCard operation and get cardData with observer. After that, with cardData
     * prepare transactionList and call setView() function for show them in screen.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_void, container, false);
        mainActivity.readCard(getViewLifecycleOwner(), amount, TransactionCode.VOID);
        cardViewModel.getCardLiveData().observe(getViewLifecycleOwner(), card -> {
            List<Transaction> transactionList = transactionViewModel.getTransactionsByCardNo(card.getCardNumber());
            if (transactionList.size() == 0) {
                showNoTransaction();
            } else {
                setView(transactionList);
            }
        });
        rvTransactions = view.findViewById(R.id.rvTransactions);
        return view;
    }

    /**
     * This method for show No Transaction info dialog and finish the activity with result cancelled.
     */
    public void showNoTransaction() {
        infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Info, getString(R.string.no_trans_found), false);
        new Handler().postDelayed(() -> {
            infoDialog.dismiss();
            mainActivity.setResult(Activity.RESULT_CANCELED);
            mainActivity.finish();
        }, 2000);
    }

    /**
     * This method for set recyclerView with setAdapter function related to our transactions.
     */
    public void setView(List<Transaction> transactionList) {
        TransactionsRecycleAdapter adapter = new TransactionsRecycleAdapter(transactionList, this, null);
        rvTransactions.setAdapter(adapter);
        rvTransactions.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    /**
     * This function is called after card reading, it finds the corresponding transaction by reference number and void it
     * if the reading card and transaction's card numbers are matching.
     */
    public void gibVoid(String refNo, Boolean isGIB) {
        cardViewModel.getCardLiveData().observe(mainActivity, card -> {
            List<Transaction> transactionList = transactionViewModel.getTransactionsByRefNo(refNo);
            Transaction transaction = transactionList.get(0);
            if (transaction != null) {
                if (Objects.equals(card.getCardNumber(), transaction.getBaPAN())) {
                    doVoid(mainActivity, transaction, isGIB);
                }
            } else {
                mainActivity.responseMessage(ResponseCode.ERROR, getString(R.string.trans_not_found), null);
            }
        });
    }

    /**
     * It starts void operation in parallel with TransactionRoutine method. It updates UI related to
     * TransactionViewModel. Finally, if it is GIB void, we send a result with intent. Else, we only
     * finish the activity.
     */
    public void doVoid(LifecycleOwner lifecycleOwner, Transaction transaction, Boolean isGIB) {
        transactionViewModel.TransactionRoutine(null, mainActivity, transaction, null, TransactionCode.VOID,
                                                activationViewModel.getActivationRepository(), batchViewModel.getBatchRepository(), isGIB);
        transactionViewModel.getInfoDialogLiveData().observe(lifecycleOwner, infoDialogData -> {
            if (Objects.equals(infoDialogData.getText(), mainActivity.getApplicationContext().getString(R.string.connecting))) {
                infoDialog = mainActivity.showInfoDialog(infoDialogData.getType(), infoDialogData.getText(), false);
            } else {
                infoDialog.update(infoDialogData.getType(), infoDialogData.getText());
            }
        });
        transactionViewModel.getIntentLiveData().observe(lifecycleOwner, resultIntent -> {
            if (resultIntent != null) {
                mainActivity.setResult(Activity.RESULT_OK, resultIntent);
            }
            mainActivity.finish();
        });
    }

    @Override
    public void confirmed(int i) { }

    @Override
    public void canceled(int i) { }
}
