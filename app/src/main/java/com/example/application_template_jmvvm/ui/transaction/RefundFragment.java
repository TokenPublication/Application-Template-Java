package com.example.application_template_jmvvm.ui.transaction;

import static com.token.uicomponents.CustomInput.EditTextInputType.Amount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application_template_jmvvm.domain.extraContentInfo;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.ResponseCode;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.domain.printHelpers.PrintHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.model.response.TransactionResponse;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.ui.posTxn.BatchViewModel;
import com.example.application_template_jmvvm.ui.settings.ActivationViewModel;
import com.example.application_template_jmvvm.ui.utils.MenuItem;

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
import java.util.Objects;

public class RefundFragment extends Fragment implements InfoDialogListener {

    private ActivationViewModel activationViewModel;
    private CardViewModel cardViewModel;
    private TransactionViewModel transactionViewModel;
    private BatchViewModel batchViewModel;
    private TransactionCode transactionCode;
    int amount;
    int instCount;
    String uuid;
    private CustomInputFormat inputTranDate;
    private CustomInputFormat inputOrgAmount;
    private CustomInputFormat inputRetAmount;
    private CustomInputFormat inputRefNo;
    private CustomInputFormat inputAuthCode;
    private Bundle bundle;
    private Intent intent;
    private MainActivity mainActivity;
    private InfoDialog infoDialog;

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
            showMatchedReturnFragment(TransactionCode.MATCHED_REFUND);
        }));
        menuItems.add(new MenuItem(getString(R.string.installment_refund), iListMenuItem -> {
            showInstallmentRefundFragment();
        }));
        menuItems.add(new MenuItem(getString(R.string.cash_refund), iListMenuItem -> {
            showReturnFragment();
        }));
        menuItems.add(new MenuItem(getString(R.string.loyalty_refund), iListMenuItem -> {

        }));
        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.refund), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, mListMenuFragment,false);
    }

    private void showMatchedReturnFragment(TransactionCode transactionCode) {
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
            if (transactionCode == TransactionCode.INSTALLMENT_REFUND) {
                this.transactionCode = TransactionCode.INSTALLMENT_REFUND;
            } else {
                this.transactionCode = transactionCode;
            }
            transactionViewModel.setIsButtonClickedLiveData(true);
        });

        mainActivity.replaceFragment(R.id.container, fragment, true);
        fragment.getViewLifecycleOwnerLiveData().observe(mainActivity, lifecycleOwner -> {
            if (lifecycleOwner != null) {
                transactionViewModel.getIsButtonClickedLiveData().observe(fragment.getViewLifecycleOwner(), isClicked -> {
                    if (isClicked) {
                        cardReader(fragment, inputList);
                    }
                });
            }
        });
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
            transactionViewModel.setIsButtonClickedLiveData(true);
        });

        mainActivity.replaceFragment(R.id.container, fragment, true);
        fragment.getViewLifecycleOwnerLiveData().observe(mainActivity, lifecycleOwner -> {
            if (lifecycleOwner != null) {
                transactionViewModel.getIsButtonClickedLiveData().observe(fragment.getViewLifecycleOwner(), isClicked -> {
                    if (isClicked) {
                        cardReader(fragment, inputList);
                    }
                });
            }
        });
    }

    private void showInstallmentRefundFragment() {
        MenuItemClickListener<MenuItem> listener = menuItem -> {
            String itemName = menuItem.getName().toString();
            String[] itemNameSplit = itemName.split(" ");
            instCount = Integer.parseInt(itemNameSplit[0]);
            showMatchedReturnFragment(TransactionCode.INSTALLMENT_REFUND);
        };

        int maxInst = 12;
        List<IListMenuItem> menuItems = new ArrayList<>();
        for (int i = 2; i <= maxInst; i++) {
            MenuItem menuItem = new MenuItem(i +" " +getString(R.string.installment), listener);
            menuItems.add(menuItem);
        }

        ListMenuFragment instFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.installment_refund), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, instFragment, true);
    }

    private void cardReader(InputListFragment fragment, List<CustomInputFormat> inputList) {
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
                        infoDialog.dismiss();
                        mainActivity.finish();
                    }
                }, 2000);
            }
        };
        timer.start();
        cardViewModel.initializeCardServiceBinding(mainActivity);

        fragment.getViewLifecycleOwnerLiveData().observe(mainActivity, lifecycleOwner -> {
            if (lifecycleOwner != null) {
                cardViewModel.getIsCardServiceConnect().observe(lifecycleOwner, isConnected -> {
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
            }
        });

        fragment.getViewLifecycleOwnerLiveData().observe(mainActivity, lifecycleOwner -> {
            if (lifecycleOwner != null) {
                cardViewModel.getCardLiveData().observe(lifecycleOwner, card -> {
                    if (card != null) {
                        mainActivity.showInfoDialog(InfoDialog.InfoType.Confirmed, "Read Successful", false);
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            doRefund(card, transactionCode, inputList, fragment);
                            infoDialog.dismiss();
                        }, 2000);
                    }
                });
            }
        });
    }

    public void doRefund(ICCCard card, TransactionCode transactionCode, List<CustomInputFormat> inputList, Fragment fragment) {
        Bundle refundInfo = bundleCreator(transactionCode, inputList);
        transactionViewModel.TransactionRoutine(card, uuid, mainActivity, this, null, refundInfo, transactionCode,
                                                    activationViewModel.getActivationRepository(), batchViewModel.getBatchRepository());
        transactionViewModel.getShowDialogLiveData().observe(fragment.getViewLifecycleOwner(), text -> {
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
        transactionViewModel.getIntentLiveData().observe(fragment.getViewLifecycleOwner(), resultIntent -> {
            mainActivity.setResult(Activity.RESULT_OK,resultIntent);
            mainActivity.finish();
        });
    }

    public Bundle bundleCreator(TransactionCode transactionCode, List<CustomInputFormat> inputList) {
        Bundle bundle = new Bundle();
        switch (transactionCode) {
            case MATCHED_REFUND:
                bundle.putInt(extraContentInfo.orgAmount, Integer.parseInt(inputList.get(0).getText()));
                bundle.putInt(extraContentInfo.refAmount, Integer.parseInt(inputList.get(1).getText()));
                bundle.putString(extraContentInfo.refNo, inputList.get(2).getText());
                bundle.putString(extraContentInfo.authCode, inputList.get(3).getText());
                bundle.putString(extraContentInfo.tranDate, inputList.get(4).getText());
                break;
            case CASH_REFUND:
                bundle.putInt(extraContentInfo.refAmount, Integer.parseInt(inputList.get(0).getText()));
                break;
            case INSTALLMENT_REFUND:
                bundle.putInt(extraContentInfo.orgAmount, Integer.parseInt(inputList.get(0).getText()));
                bundle.putInt(extraContentInfo.refAmount, Integer.parseInt(inputList.get(1).getText()));
                bundle.putString(extraContentInfo.refNo, inputList.get(2).getText());
                bundle.putString(extraContentInfo.authCode, inputList.get(3).getText());
                bundle.putString(extraContentInfo.tranDate, inputList.get(4).getText());
                bundle.putInt(extraContentInfo.instCount, instCount);
                break;
            default:
                break;
        }
        return bundle;
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

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }
}