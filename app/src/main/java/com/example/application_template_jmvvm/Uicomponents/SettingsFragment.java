package com.example.application_template_jmvvm.Uicomponents;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.application_template_jmvvm.Helpers.DataBase.activation.ActivationDB;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.Viewmodels.SettingsViewModel;
import com.token.uicomponents.CustomInput.CustomInputFormat;
import com.token.uicomponents.CustomInput.EditTextInputType;
import com.token.uicomponents.CustomInput.InputListFragment;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;
    private MainActivity main;
    private String terminalId;

    private Context context;

    private String merchantId, ip_no, port_no;

    public SettingsFragment(MainActivity mainActivity, Context context) {
        this.main = mainActivity;
        this.context = context;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMenu();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    private void showMenu(){
        List<IListMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.setup), iListMenuItem -> {
            addTidMidFragment(context);
        }));
        menuItems.add(new MenuItem(getString(R.string.host_settings), iListMenuItem -> {
            addIpFragment(context);
        }));
        ListMenuFragment mListMenuFragment = ListMenuFragment.newInstance(menuItems, getString(R.string.settings), true, R.drawable.token_logo_png);
        mViewModel.replaceFragment(main,mListMenuFragment,false);
    }

    private void addTidMidFragment(Context context) {
        List<CustomInputFormat> inputList = new ArrayList<>();
        inputList.add(new CustomInputFormat(context.getString(R.string.merchant_no), EditTextInputType.Number, 10, context.getString(R.string.invalid_merchant_no), input -> input.getText().length() == 10));

        inputList.add(new CustomInputFormat(context.getString(R.string.terminal_no), EditTextInputType.Text, 8, context.getString(R.string.invalid_terminal_no), input -> input.getText().length() == 8));

        inputList.get(0).setText("9898878777");
        inputList.get(1).setText("90909090");

        InputListFragment TidMidFragment = InputListFragment.newInstance(inputList, context.getString(R.string.save), list -> {

            merchantId = inputList.get(0).getText();
            terminalId = inputList.get(1).getText();

            System.out.println(merchantId);
            System.out.println(terminalId);
            ActivationDB.getInstance(context).insertActivation(context, terminalId, merchantId);
            // TODO Barış Kılıç, Review.
        });
        main.replaceFragment(R.id.container,TidMidFragment,true);
    }

    private void addIpFragment(Context context) {
        List<CustomInputFormat> inputList = new ArrayList<>();
        inputList.add(new CustomInputFormat("IP", EditTextInputType.IpAddress, null, context.getString(R.string.invalid_ip), customInputFormat -> {
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
        inputList.add(new CustomInputFormat("Port", EditTextInputType.Number, 4, context.getString(R.string.invalid_port), customInputFormat -> customInputFormat.getText().length() >= 2 && Integer.parseInt(customInputFormat.getText()) > 0));
        inputList.get(0).setText("192.416.10.25");
        inputList.get(1).setText("1001");

        InputListFragment IpFragment = InputListFragment.newInstance(inputList, context.getString(R.string.save), list -> {
            ip_no = inputList.get(0).getText();
            port_no = inputList.get(1).getText();

            System.out.println(ip_no);
            System.out.println(port_no);
        });
        main.replaceFragment(R.id.container,IpFragment,true);
    }
}