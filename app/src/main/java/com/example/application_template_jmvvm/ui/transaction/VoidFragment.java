package com.example.application_template_jmvvm.ui.transaction;

import android.app.Activity;
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
import com.example.application_template_jmvvm.domain.service.TransactionService;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.domain.adapter.TransactionsRecycleAdapter;
import com.example.application_template_jmvvm.domain.printHelpers.PrintHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.example.application_template_jmvvm.ui.settings.ActivationViewModel;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VoidFragment extends Fragment implements InfoDialogListener {

    private MainActivity mainActivity;
    private ActivationViewModel activationViewModel;
    private BatchViewModel batchViewModel;
    private CardViewModel cardViewModel;
    private TransactionViewModel transactionViewModel;
    private InfoDialog infoDialog;
    int amount;
    private RecyclerView rvTransactions;
    private List<TransactionEntity> transactionList = new ArrayList<>();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_void, container, false);
        boolean empty = transactionViewModel.isTransactionListEmpty(); //TODO 1 işlem voidse hiç yoksa boş dönüyor
        if(empty){
            infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Info, getString(R.string.no_trans_found), false);
            new Handler().postDelayed(() -> {
                infoDialog.dismiss();
                mainActivity.setResult(Activity.RESULT_CANCELED);
                mainActivity.finish();
            }, 2000);
        }
        else{
            final boolean[] isCancelled = {false};
            infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Processing, "Processing", false);
            CountDownTimer timer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    isCancelled[0] = true;
                    infoDialog.update(InfoDialog.InfoType.Declined, "Connect Failed");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (infoDialog != null) {
                            infoDialog.dismiss();   //TODO Backpressed
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
                    infoDialog.update(InfoDialog.InfoType.Confirmed, "Connected to Service");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        cardViewModel.readCard(amount);
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            infoDialog.dismiss();
                        }, 1000);
                    }, 2000);
                }
            });

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
        transactionList = transactionViewModel.getTransactionsByCardNo(card.getCardNumber());
        TransactionsRecycleAdapter adapter = new TransactionsRecycleAdapter(transactionList, transactionViewModel, this);
        rvTransactions.setAdapter(adapter);
        rvTransactions.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    public void startVoid(TransactionEntity transactionEntity) {
        transactionViewModel.TransactionRoutine(null, null, mainActivity, this, transactionEntity, null, TransactionCode.VOID,
                activationViewModel.getActivationRepository(), batchViewModel.getBatchRepository());
        transactionViewModel.getShowDialogLiveData().observe(getViewLifecycleOwner(), text -> {
            if (text != null) {
                if (Objects.equals(text, "Progress")) {
                    infoDialog = mainActivity.showInfoDialog(InfoDialog.InfoType.Progress, text, false);
                } else {
                    infoDialog.update(InfoDialog.InfoType.Progress, text);
                }
                if (text.contains("ONAY KODU")) {
                    infoDialog.update(InfoDialog.InfoType.Confirmed, text);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {}, 2000);
                }
            }
        });
        transactionViewModel.getIntentLiveData().observe(getViewLifecycleOwner(), resultIntent -> {
            mainActivity.setResult(Activity.RESULT_OK,resultIntent);
            mainActivity.finish();
        });
    }

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }
}