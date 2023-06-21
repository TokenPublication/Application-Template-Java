package com.example.application_template_jmvvm.ui.transaction;

import static com.token.uicomponents.CustomInput.EditTextInputType.Amount;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application_template_jmvvm.domain.service.TransactionResponseListener;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.domain.printHelpers.PrintHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.model.response.TransactionResponse;
import com.example.application_template_jmvvm.domain.service.TransactionService;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.example.application_template_jmvvm.ui.utils.MenuItem;

import com.token.uicomponents.CustomInput.CustomInputFormat;
import com.token.uicomponents.CustomInput.EditTextInputType;
import com.token.uicomponents.CustomInput.InputListFragment;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RefundFragment extends Fragment{

    private TransactionService transactionService = new TransactionService();
    private CardViewModel cardViewModel;
    private TransactionViewModel transactionViewModel;
    private BatchViewModel batchViewModel;
    private TransactionCode transactionCode;
    int amount;
    String uuid;
    private CustomInputFormat inputTranDate;
    private CustomInputFormat inputOrgAmount;
    private CustomInputFormat inputRetAmount;
    private CustomInputFormat inputRefNo;
    private CustomInputFormat inputAuthCode;
    private Bundle bundle;
    private Intent intent;
    private MainActivity mainActivity;

    public RefundFragment(MainActivity mainActivity, CardViewModel cardViewModel, TransactionViewModel transactionViewModel, BatchViewModel batchViewModel) {
        this.mainActivity = mainActivity;
        this.cardViewModel = cardViewModel;
        this.transactionViewModel = transactionViewModel;
        this.batchViewModel = batchViewModel;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uuid = "4234324234";        //TODO düzenlenecek.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_refund, container, false);
        showMenu();
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void showMenu(){
        List<IListMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.matched_refund), iListMenuItem -> {
            showMatchedReturnFragment();
        }));
        menuItems.add(new MenuItem(getString(R.string.installment_refund), iListMenuItem -> {
            //TODO will be implemented.Bastıktan sonra cash ve matched gelmeme hatası.inst amount ve cnt 0 dummy bak
        }));
        menuItems.add(new MenuItem(getString(R.string.cash_refund), iListMenuItem -> {
            showReturnFragment();
        }));
        menuItems.add(new MenuItem(getString(R.string.loyalty_refund), iListMenuItem -> {

        }));
        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.refund), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, mListMenuFragment,false);
    }

    private void showMatchedReturnFragment() {
        List<CustomInputFormat> inputList = new ArrayList<>();
        inputOrgAmount = new CustomInputFormat(getString(R.string.original_amount), Amount, null, getString(R.string.invalid_amount),
                input -> {
                    int amount = input.getText().isEmpty() ? 0 : Integer.parseInt(input.getText());
                    return amount > 0;
                });
        inputList.add(inputOrgAmount);

        inputRetAmount = new CustomInputFormat(getString(R.string.refund_amount), Amount, null, getString(R.string.invalid_amount),
                input -> {
                    int amount = input.getText().isEmpty() ? 0 : Integer.parseInt(input.getText());
                    int original = inputOrgAmount.getText().isEmpty() ? 0 : Integer.parseInt(inputOrgAmount.getText());
                    return amount > 0 && amount <= original;
                });
        inputList.add(inputRetAmount);

        inputRefNo = new CustomInputFormat(getString(R.string.ref_no), EditTextInputType.Number, 10, getString(R.string.ref_no_invalid_ten_digits),
                customInputFormat -> {
                    return !isCurrentDay(inputTranDate.getText()) || isCurrentDay(inputTranDate.getText()) && customInputFormat.getText().length() == 10;
                });
        inputList.add(inputRefNo);

        inputAuthCode = new CustomInputFormat(getString(R.string.confirmation_code), EditTextInputType.Number, 6, getString(R.string.confirmation_code_invalid_six_digits),
                customInputFormat -> customInputFormat.getText().length() == 6);
        inputList.add(inputAuthCode);

        inputTranDate = new CustomInputFormat(getString(R.string.tran_date), EditTextInputType.Date, null, getString(R.string.tran_date_invalid),
                customInputFormat -> {
                    try {
                        String[] array = customInputFormat.getText().split("/");
                        String date = array[2].substring(2) + array[1] + array[0];
                        Date now = Calendar.getInstance().getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
                        return Integer.parseInt(sdf.format(now)) >= Integer.parseInt(date);
                    } catch (Exception e) {
                    }
                    return false;
                }
        );
        inputList.add(inputTranDate);

        InputListFragment fragment = InputListFragment.newInstance(inputList, getString(R.string.refund), list -> {
            amount = Integer.parseInt(list.get(1));
            transactionCode = TransactionCode.MATCHED_REFUND;
            cardReader();
        });
        mainActivity.replaceFragment(R.id.container, fragment, true);
        cardDataObserver(fragment,inputList);
    }

    private void showReturnFragment(){
        List<CustomInputFormat> inputList = new ArrayList<>();
        inputList.add(new CustomInputFormat(getString(R.string.refund_amount), Amount, null, getString(R.string.invalid_amount), input -> {
            int ListAmount = input.getText().isEmpty() ? 0 : Integer.parseInt(input.getText());
            try {
                amount = ListAmount;
            } catch(NumberFormatException n) {
                n.printStackTrace();
            }
            return ListAmount > 0;
        }));

        InputListFragment fragment = InputListFragment.newInstance(inputList, getString(R.string.refund), list -> {
            amount = Integer.parseInt(list.get(0));
            transactionCode = TransactionCode.CASH_REFUND;
            cardReader();
        });
        mainActivity.replaceFragment(R.id.container, fragment, true);
        cardDataObserver(fragment,inputList);
    }

    private void cardReader(){
        if (!cardViewModel.getIsCardServiceConnected()){
            cardViewModel.initializeCardServiceBinding(mainActivity);
            cardViewModel.setIsCardServiceConnected(true);
        }
        cardViewModel.readCard(amount);
    }

    private void cardDataObserver(InputListFragment fragment, List<CustomInputFormat> inputList){
        fragment.getViewLifecycleOwnerLiveData().observe(mainActivity, lifecycleOwner -> {
            if (lifecycleOwner != null) {
                cardViewModel.getCardLiveData().observe(lifecycleOwner, card -> {
                    if (card != null) {
                        afterCardRead(card,transactionCode,inputList);
                    }
                });
            }
        });
    }

    public void afterCardRead(ICCCard card, TransactionCode transactionCode, List<CustomInputFormat> inputList){
        ContentValues values = transactionViewModel.prepareContents(card, uuid, transactionCode);
        values = transactionViewModel.prepareExtraContents(values, transactionCode, inputList);
        transactionService.doInBackground(values, transactionCode, transactionViewModel,
                batchViewModel.getBatchRepository(),
                new TransactionResponseListener() {
                    @Override
                    public void onComplete(TransactionResponse response) {
                        finishRefund(response);
                    }
                });
    }

    private void finishRefund(TransactionResponse transactionResponse) {
        ResponseCode responseCode = transactionResponse.getOnlineTransactionResponse().getmResponseCode();
        Log.d("TransactionResponse/Refund", "responseCode:" + responseCode + " ContentVals: " + transactionResponse.getContentValues());
        intent = new Intent();
        bundle = new Bundle();
        bundle.putInt("ResponseCode", responseCode.ordinal());
        PrintHelper.PrintSuccess(mainActivity.getApplicationContext());
        intent.putExtras(bundle);
        mainActivity.setResult(Activity.RESULT_OK, intent);
        mainActivity.finish();
    }

    private String getFormattedDate(String dateText) {
        String[] array = dateText.split("/");
        return array[0] + array[1] + array[2].substring(2);
    }

    private boolean isCurrentDay(String dateText) {
        if (dateText.isEmpty()) {
            return false;
        }
        String date = getFormattedDate(dateText);
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        return sdf.format(Calendar.getInstance().getTime()).equals(date);
    }

}