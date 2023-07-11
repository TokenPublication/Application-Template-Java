package com.example.application_template_jmvvm.ui.activation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.utils.objects.MenuItem;
import com.token.uicomponents.CustomInput.CustomInputFormat;
import com.token.uicomponents.CustomInput.EditTextInputType;
import com.token.uicomponents.CustomInput.InputListFragment;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private MainActivity mainActivity;
    private ActivationViewModel activationViewModel;
    private String terminalId, merchantId, ipNo, portNo;

    public SettingsFragment(MainActivity mainActivity, ActivationViewModel activationViewModel) {
        this.mainActivity = mainActivity;
        this.activationViewModel = activationViewModel;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMenu();
    }

    private void showMenu() {
        List<IListMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.setup), iListMenuItem -> addTidMidFragment()));
        menuItems.add(new MenuItem(getString(R.string.host_settings), iListMenuItem -> addIpFragment()));
        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.settings), true, R.drawable.token_logo_png);
        mainActivity.replaceFragment(R.id.container, mListMenuFragment,false);
    }

    private void addTidMidFragment() {
        List<CustomInputFormat> inputList = new ArrayList<>();
        inputList.add(new CustomInputFormat(mainActivity.getString(R.string.merchant_no), EditTextInputType.Number, 10, mainActivity.getString(R.string.invalid_merchant_no), input -> input.getText().length() == 10));

        inputList.add(new CustomInputFormat(mainActivity.getString(R.string.terminal_no), EditTextInputType.Text, 8, mainActivity.getString(R.string.invalid_terminal_no), input -> input.getText().length() == 8));

        inputList.get(0).setText(activationViewModel.getMerchantId());
        inputList.get(1).setText(activationViewModel.getTerminalId());

        InputListFragment TidMidFragment = InputListFragment.newInstance(inputList, mainActivity.getString(R.string.save), list -> {
            merchantId = inputList.get(0).getText();
            terminalId = inputList.get(1).getText();

            activationViewModel.updateActivation(terminalId, merchantId, activationViewModel.getHostIP());
            mainActivity.getSupportFragmentManager().popBackStack();
        });
        mainActivity.replaceFragment(R.id.container, TidMidFragment,true);
    }

    private void addIpFragment() {
        List<CustomInputFormat> inputList = new ArrayList<>();
        inputList.add(new CustomInputFormat("IP", EditTextInputType.IpAddress, null, mainActivity.getString(R.string.invalid_ip), customInputFormat -> {
            String text = customInputFormat.getText();
            boolean isValid = StringUtils.countMatches(text, ".") == 3 && text.split("\\.").length == 4;
            if (isValid) {
                String[] array = text.split("\\.");
                int index = 0;
                while (isValid && index < array.length) {
                    isValid = StringUtils.isNumeric(array[0]);
                    index++;
                }
            }
            return isValid;
        }));
        inputList.add(new CustomInputFormat("Port", EditTextInputType.Number, 4, mainActivity.getString(R.string.invalid_port), customInputFormat -> customInputFormat.getText().length() >= 2 && Integer.parseInt(customInputFormat.getText()) > 0));

        inputList.get(0).setText(activationViewModel.getHostIP());
        inputList.get(1).setText(activationViewModel.getHostPort());

        InputListFragment IpFragment = InputListFragment.newInstance(inputList, mainActivity.getString(R.string.save), list -> {
            ipNo = inputList.get(0).getText();
            portNo = inputList.get(1).getText();

            activationViewModel.updateConnection(ipNo, portNo, activationViewModel.getHostIP());
            mainActivity.getSupportFragmentManager().popBackStack();
        });
        mainActivity.replaceFragment(R.id.container,IpFragment,true);
    }
}