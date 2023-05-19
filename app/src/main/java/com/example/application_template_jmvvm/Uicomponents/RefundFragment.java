package com.example.application_template_jmvvm.Uicomponents;

import static com.token.uicomponents.CustomInput.EditTextInputType.Amount;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.printservice.PrintService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.application_template_jmvvm.Entity.CardReadType;
import com.example.application_template_jmvvm.Entity.ICCCard;
import com.example.application_template_jmvvm.Entity.MSRCard;
import com.example.application_template_jmvvm.Entity.PaymentTypes;
import com.example.application_template_jmvvm.Entity.ResponseCode;
import com.example.application_template_jmvvm.Entity.SlipType;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionCol;
import com.example.application_template_jmvvm.Helpers.PrintHelpers.PrintHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.Responses.TransactionResponse;
import com.example.application_template_jmvvm.Services.TransactionService;
import com.example.application_template_jmvvm.Viewmodels.SaleViewModel;
import com.google.gson.Gson;
import com.token.uicomponents.CustomInput.CustomInputFormat;
import com.token.uicomponents.CustomInput.EditTextInputType;
import com.token.uicomponents.CustomInput.InputListFragment;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.infodialog.InfoDialog;
import com.tokeninc.cardservicebinding.CardServiceBinding;
import com.tokeninc.cardservicebinding.CardServiceListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RefundFragment extends Fragment implements CardServiceListener{

    private boolean isCardServiceConnected;
    private CardServiceListener cardServiceListener;
    private CardServiceBinding cardServiceBinding;
    private TransactionService transactionService = new TransactionService();
    int cardReadType = 0;
    int amount;
    String uuid;
    private CustomInputFormat inputTranDate;
    private CustomInputFormat inputOrgAmount;
    private CustomInputFormat inputRetAmount;
    private CustomInputFormat inputRefNo;
    private CustomInputFormat inputAuthCode;

    private ListMenuFragment instFragment;
    List<CustomInputFormat> inputList = new ArrayList<>();
    private Bundle bundle;
    private Intent intent;
    private ICCCard card;
    private MSRCard msrCard;
    private MainActivity main;

    public RefundFragment(MainActivity mainActivity) {
        this.main = mainActivity;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardServiceListener = this;
        uuid = "4234324234";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_refund, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMenu();
    }

    private void showMenu(){
        List<IListMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.matched_refund), iListMenuItem -> {
            showMatchedReturnFragment();
        }));
        menuItems.add(new MenuItem(getString(R.string.installment_refund), iListMenuItem -> {

        }));
        menuItems.add(new MenuItem(getString(R.string.cash_refund), iListMenuItem -> {

        }));
        menuItems.add(new MenuItem(getString(R.string.loyalty_refund), iListMenuItem -> {

        }));
        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.refund), true, R.drawable.token_logo_png);
        main.replaceFragment(R.id.container,mListMenuFragment,false);
    }

    private void showMatchedReturnFragment() {
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
            if (isCardServiceConnected){
                readCard();
            }
            else {
                cardServiceBinding = new CardServiceBinding(main, cardServiceListener);
            }
        });
        main.replaceFragment(R.id.container, fragment, true);
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
        try {
            JSONObject json = new JSONObject(cardData);
            int type = json.getInt("mCardReadType");

            if (type == CardReadType.CLCard.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
            }
            if (type == CardReadType.ICC.value) {
                ICCCard card = new Gson().fromJson(cardData, ICCCard.class);
                this.card = card;
                showInfoDialog();
            } else if (type == CardReadType.ICC2MSR.value || type == CardReadType.MSR.value || type == CardReadType.KeyIn.value) {
                MSRCard card = new Gson().fromJson(cardData, MSRCard.class);
                this.msrCard = card;
                cardServiceBinding.getOnlinePIN(amount, card.getCardNumber(), 0x0A01, 0, 4, 8, 30);
                showInfoDialog();
            }
            showInfoDialog();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPinReceived(String s) {

    }

    @Override
    public void onICCTakeOut() {
        insertRefund();
    }

    private void showInfoDialog() {
        InfoDialog dialog = main.showInfoDialog(InfoDialog.InfoType.Progress, getString(R.string.connecting), false);
        new Handler().postDelayed(() -> {
            dialog.update(InfoDialog.InfoType.Confirmed, getString(R.string.trans_successful) +"\n" +getString(R.string.confirmation_code) +" "+inputList.get(3).getText());
            new Handler().postDelayed(() -> {
                dialog.update(InfoDialog.InfoType.Progress, getString(R.string.printing_the_receipt));
                new Handler().postDelayed(() -> {
                    dialog.dismiss();
                    if (card != null)
                        onICCTakeOut();
                    else {
                        insertRefund();
                    }
                }, 2000);
            }, 2000);
        }, 2000);
    }

    private void insertRefund(){
        ContentValues values = new ContentValues();
        values.put(TransactionCol.col_uuid.name(), uuid);
        values.put(TransactionCol.col_bCardReadType.name(), card.getmCardReadType());
        values.put(TransactionCol.col_bTransCode.name(), 55);
        values.put(TransactionCol.col_ulAmount.name(),Integer.parseInt(inputList.get(0).getText()));
        values.put(TransactionCol.col_ulAmount2.name(), Integer.parseInt(inputList.get(1).getText()));
        values.put(TransactionCol.col_baPAN.name(), card.getmCardNumber());
        values.put(TransactionCol.col_baExpDate.name(), card.getmExpireDate());
        values.put(TransactionCol.col_baDate.name(), card.getDateTime().substring(0,8));
        values.put(TransactionCol.col_baTime.name(), card.getDateTime().substring(8));
        values.put(TransactionCol.col_baTrack2.name(), card.getmTrack2Data());
        values.put(TransactionCol.col_baCustomName.name(), card.getmTrack1CustomerName());
        values.put(TransactionCol.col_baRspCode.name(), 3);
        values.put(TransactionCol.col_bInstCnt.name(), 10);
        values.put(TransactionCol.col_ulInstAmount.name(), card.getmTranAmount1());
        values.put(TransactionCol.col_baTranDate.name(), card.getDateTime());
        values.put(TransactionCol.col_baTranDate2.name(), inputList.get(4).getText());
        values.put(TransactionCol.col_baHostLogKey.name(), "1020304050");
        values.put(TransactionCol.col_authCode.name(), inputList.get(3).getText());
        values.put(TransactionCol.col_aid.name(), card.getAID2());
        values.put(TransactionCol.col_aidLabel.name(), card.getAIDLabel());
        values.put(TransactionCol.col_baCVM.name(), card.getCVM());
        values.put(TransactionCol.col_SID.name(), card.getSID());
        TransactionResponse transactionResponse = transactionService.doInBackground(main,getContext(),values);
        finishRefund(transactionResponse);
    }

    private void finishRefund(TransactionResponse transactionResponse) {
        ResponseCode responseCode = transactionResponse.getOnlineTransactionResponse().getmResponseCode();
        Log.d("TransactionResponse/Refund", "responseCode:" + responseCode + " ContentVals: " + transactionResponse.getContentValues());
        intent = new Intent();
        bundle = new Bundle();
        bundle.putInt("ResponseCode", responseCode.ordinal());
        PrintHelper.PrintSuccess();
        intent.putExtras(bundle);
        main.setResult(Activity.RESULT_OK, intent);
        main.finish();
    }

    public void readCard() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("forceOnline", 1);
            obj.put("zeroAmount", 0);
            obj.put("fallback", 1);
            obj.put("cardReadTypes",6);
            obj.put("qrPay", 1);

            cardServiceBinding.getCard(amount, 40, obj.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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