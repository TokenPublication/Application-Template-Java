package com.example.application_template_jmvvm.ui.posTxn.refund;

import static com.token.uicomponents.CustomInput.EditTextInputType.Amount;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.application_template_jmvvm.utils.ExtraContentInfo;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.batch.BatchViewModel;
import com.example.application_template_jmvvm.ui.activation.ActivationViewModel;
import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;
import com.example.application_template_jmvvm.utils.objects.MenuItem;

import com.example.application_template_jmvvm.utils.printHelpers.DateUtil;
import com.token.uicomponents.CustomInput.CustomInputFormat;
import com.token.uicomponents.CustomInput.EditTextInputType;
import com.token.uicomponents.CustomInput.InputListFragment;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;
import com.token.uicomponents.ListMenuFragment.MenuItemClickListener;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * This is the fragment for the Refund actions.
 */
public class RefundFragment extends Fragment implements InfoDialogListener {
    private ActivationViewModel activationViewModel;
    private CardViewModel cardViewModel;
    private TransactionViewModel transactionViewModel;
    private BatchViewModel batchViewModel;
    private TransactionCode transactionCode;
    int amount;
    int instCount;
    private CustomInputFormat inputTranDate;
    private CustomInputFormat inputOrgAmount;
    private MainActivity mainActivity;
    private InfoDialog infoDialog;
    private InputListFragment inputListFragment;

    public RefundFragment(MainActivity mainActivity, ActivationViewModel activationViewModel, CardViewModel cardViewModel,
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
        View view = inflater.inflate(R.layout.fragment_refund, container, false);
        showMenu();
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * It prepares list menu item and shows it to the screen. It has refund types.
     */
    private void showMenu() {
        List<IListMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.matched_refund), iListMenuItem -> showMatchedReturnFragment(TransactionCode.MATCHED_REFUND)));
        menuItems.add(new MenuItem(getString(R.string.installment_refund), iListMenuItem -> showInstallmentRefundFragment()));
        menuItems.add(new MenuItem(getString(R.string.cash_refund), iListMenuItem -> showReturnFragment()));
        menuItems.add(new MenuItem(getString(R.string.loyalty_refund), iListMenuItem -> { }));
        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.refund), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, mListMenuFragment,false);
    }

    /**
     * This inputListFragment for show Matched Refund screen.
     */
    private void showMatchedReturnFragment(TransactionCode transactionCode) {
        List<CustomInputFormat> inputList = new ArrayList<>();
        inputOrgAmount = new CustomInputFormat(getString(R.string.original_amount), Amount, null, getString(R.string.invalid_amount),
                input -> {
                    int amount = input.getText().isEmpty() ? 0 : Integer.parseInt(input.getText());
                    return amount > 0;
                });
        inputList.add(inputOrgAmount);

        CustomInputFormat inputRetAmount = new CustomInputFormat(getString(R.string.refund_amount), Amount, null, getString(R.string.invalid_amount),
                input -> {
                    int amount = input.getText().isEmpty() ? 0 : Integer.parseInt(input.getText());
                    int original = inputOrgAmount.getText().isEmpty() ? 0 : Integer.parseInt(inputOrgAmount.getText());
                    return amount > 0 && amount <= original;
                });
        inputList.add(inputRetAmount);

        CustomInputFormat inputRefNo = new CustomInputFormat(getString(R.string.ref_no), EditTextInputType.Number, 10, getString(R.string.ref_no_invalid_ten_digits),
                customInputFormat -> !DateUtil.isCurrentDay(inputTranDate.getText()) || DateUtil.isCurrentDay(inputTranDate.getText())
                        && customInputFormat.getText().length() == 10);
        inputList.add(inputRefNo);

        CustomInputFormat inputAuthCode = new CustomInputFormat(getString(R.string.confirmation_code), EditTextInputType.Number, 6, getString(R.string.confirmation_code_invalid_six_digits),
                customInputFormat -> customInputFormat.getText().length() == 6);
        inputList.add(inputAuthCode);

        inputTranDate = new CustomInputFormat(getString(R.string.tran_date), EditTextInputType.Date, null, getString(R.string.tran_date_invalid),
                customInputFormat -> {
                    try {
                        String[] array = customInputFormat.getText().split("/");
                        String date = array[2].substring(2) + array[1] + array[0];
                        Date now = Calendar.getInstance().getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.US);
                        return Integer.parseInt(sdf.format(now)) >= Integer.parseInt(date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
        );
        inputList.add(inputTranDate);

        inputListFragment = InputListFragment.newInstance(inputList, getString(R.string.refund), list -> {
            amount = Integer.parseInt(list.get(1));
            this.transactionCode = transactionCode;
            Bundle refundInfo = bundleCreator(transactionCode, inputList);
            cardReader(inputListFragment.getViewLifecycleOwner(), refundInfo, false);
        });
        mainActivity.replaceFragment(R.id.container, inputListFragment, true);
    }

    /**
     * This inputListFragment for show Return Refund screen.
     */
    private void showReturnFragment() {
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

        inputListFragment = InputListFragment.newInstance(inputList, getString(R.string.refund), list -> {
            amount = Integer.parseInt(list.get(0));
            transactionCode = TransactionCode.CASH_REFUND;
            Bundle refundInfo = bundleCreator(transactionCode, inputList);
            cardReader(inputListFragment.getViewLifecycleOwner(), refundInfo, false);
        });

        mainActivity.replaceFragment(R.id.container, inputListFragment, true);
    }

    /**
     * This listMenuFragment for show installments. After that, showed matched refund screen for
     * getting other refund info.
     */
    private void showInstallmentRefundFragment() {
        MenuItemClickListener<MenuItem> listener = menuItem -> {
            String itemName = menuItem.getName();
            String[] itemNameSplit = itemName.split(" ");
            instCount = Integer.parseInt(itemNameSplit[0]);
            showMatchedReturnFragment(TransactionCode.INSTALLMENT_REFUND);
        };

        int maxInst = 12;
        List<IListMenuItem> menuItems = new ArrayList<>();
        for (int i = 2; i <= maxInst; i++) {
            MenuItem menuItem = new MenuItem(i + " " +getString(R.string.installment), listener);
            menuItems.add(menuItem);
        }

        ListMenuFragment instFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.installment_refund), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, instFragment, true);
    }

    /**
     * This method is for perform readCard operation. It contains two flows in it.
     * GIB Refund -> transactionCode == null, continue
     * Normal Refund -> transactionCode has a value, continue
     * Also it has fragment's lifeCycleOwner for observe cardData from readCard.
     */
    public void cardReader(LifecycleOwner lifecycleOwner, Bundle refundInfo, Boolean isGIB) {
        if (transactionCode == null) {
            transactionCode = TransactionCode.MATCHED_REFUND;
        }
        amount = refundInfo.getInt(ExtraContentInfo.refAmount);
        mainActivity.readCard(lifecycleOwner, amount, transactionCode);
        cardViewModel.getCardLiveData().observe(lifecycleOwner, card -> doRefund(card, transactionCode, refundInfo, lifecycleOwner, isGIB));
    }

    /**
     * This method for perform TransactionRoutine for insert our refund operation to Database. Also
     * it updates UI related to ViewModel's UI States. Finally, it gets intent only on GIB Refund.
     * In normal refunds, we do not need for send intent to result.
     */
    public void doRefund(ICCCard card, TransactionCode transactionCode, Bundle refundInfo, LifecycleOwner lifecycleOwner, Boolean isGIB) {
        transactionViewModel.TransactionRoutine(card, mainActivity, null, refundInfo, transactionCode,
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

    /**
     * This method for prepare bundle with refundInfo that user enters.
     */
    public Bundle bundleCreator(TransactionCode transactionCode, List<CustomInputFormat> inputList) {
        Bundle bundle = new Bundle();
        switch (transactionCode) {
            case MATCHED_REFUND:
                bundle.putInt(ExtraContentInfo.orgAmount, Integer.parseInt(inputList.get(0).getText()));
                bundle.putInt(ExtraContentInfo.refAmount, Integer.parseInt(inputList.get(1).getText()));
                bundle.putString(ExtraContentInfo.refNo, inputList.get(2).getText());
                bundle.putString(ExtraContentInfo.authCode, inputList.get(3).getText());
                bundle.putString(ExtraContentInfo.tranDate, inputList.get(4).getText());
                break;
            case CASH_REFUND:
                bundle.putInt(ExtraContentInfo.refAmount, Integer.parseInt(inputList.get(0).getText()));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                String dateTime = sdf.format(Calendar.getInstance().getTime());
                bundle.putString(ExtraContentInfo.tranDate, dateTime);
                break;
            case INSTALLMENT_REFUND:
                bundle.putInt(ExtraContentInfo.orgAmount, Integer.parseInt(inputList.get(0).getText()));
                bundle.putInt(ExtraContentInfo.refAmount, Integer.parseInt(inputList.get(1).getText()));
                bundle.putString(ExtraContentInfo.refNo, inputList.get(2).getText());
                bundle.putString(ExtraContentInfo.authCode, inputList.get(3).getText());
                bundle.putString(ExtraContentInfo.tranDate, inputList.get(4).getText());
                bundle.putInt(ExtraContentInfo.instCount, instCount);
                break;
            default:
                break;
        }
        return bundle;
    }

    @Override
    public void confirmed(int i) { }

    @Override
    public void canceled(int i) { }
}
