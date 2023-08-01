package com.example.application_template_jmvvm.ui.posTxn.slip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.ui.activation.ActivationViewModel;
import com.example.application_template_jmvvm.ui.posTxn.batch.BatchViewModel;
import com.example.application_template_jmvvm.ui.posTxn.voidOperation.TransactionsRecycleAdapter;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.List;

/**
 * This fragment for Slip Menu. It has yesterday's batch close and all transactions that did in today
 * with recyclerView.
 */
public class SlipFragment extends Fragment implements InfoDialogListener {
    private MainActivity mainActivity;
    private ActivationViewModel activationViewModel;
    private BatchViewModel batchViewModel;
    private TransactionViewModel transactionViewModel;
    private InfoDialog infoDialog;
    private RecyclerView rvTransactions;
    private TransactionCode transactionCode;

    public SlipFragment(MainActivity mainActivity, ActivationViewModel activationViewModel,
                        TransactionViewModel transactionViewModel, BatchViewModel batchViewModel) {
        this.mainActivity = mainActivity;
        this.activationViewModel = activationViewModel;
        this.transactionViewModel = transactionViewModel;
        this.batchViewModel = batchViewModel;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slip, container, false);
        rvTransactions = view.findViewById(R.id.rvTransaction);
        return view;
    }

    /**
     * This method for getting all transactions if it is not empty and setView. Then, prepare list menu
     * for batch close and transaction list.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.backButton).setOnClickListener(v -> mainActivity.getSupportFragmentManager().popBackStack());

        if (!transactionViewModel.isTransactionListEmpty()) {
            List<TransactionEntity> transactionList = transactionViewModel.getAllTransactions();
            setView(transactionList);
        }

        view.findViewById(R.id.btnBatch).setOnClickListener(v -> {
            infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Progress, mainActivity.getString(R.string.printing_the_receipt), false);
            batchViewModel.printPreviousBatchSlip(mainActivity);
            batchViewModel.getInfoDialogLiveData().observe(mainActivity, infoDialogData -> infoDialog.dismiss());
        });
    }

    /**
     * This method for set recyclerView with setAdapter function related to our transactions.
     */
    public void setView(List<TransactionEntity> transactionList) {
        TransactionsRecycleAdapter adapter = new TransactionsRecycleAdapter(transactionList, null, this);
        rvTransactions.setAdapter(adapter);
        rvTransactions.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    /**
     * This method called in adapter class with chosen transaction in transactionList showed with
     * recyclerView. Firstly, it prepares TransactionCode of the transaction for getting slip correctly.
     * After that, it shows Printing the receipt screen and call prepareSlip which runs on IO thread.
     */
    public void prepareSlip(TransactionEntity transactionEntity) {
        switch (transactionEntity.getbTransCode()) {
            case 1:
                transactionCode = TransactionCode.SALE;
                break;
            case 2:
                transactionCode = TransactionCode.INSTALLMENT_SALE;
                break;
            case 4:
                transactionCode = TransactionCode.MATCHED_REFUND;
                break;
            case 5:
                transactionCode = TransactionCode.CASH_REFUND;
                break;
            case 6:
                transactionCode = TransactionCode.INSTALLMENT_REFUND;
                break;                
        }
        if (transactionEntity.getIsVoid() == 1) {
            transactionCode = TransactionCode.VOID;
        }
        infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Progress, mainActivity.getString(R.string.printing_the_receipt), false);
        transactionViewModel.prepareSlip(activationViewModel.getActivationRepository(), batchViewModel.getBatchRepository(), mainActivity, transactionEntity, transactionCode, true);
        transactionViewModel.getInfoDialogLiveData().observe(getViewLifecycleOwner(), infoDialogData -> infoDialog.dismiss());
    }

    @Override
    public void confirmed(int i) { }

    @Override
    public void canceled(int i) { }
}
