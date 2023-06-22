package com.example.application_template_jmvvm.ui.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.token.uicomponents.CustomInput.CustomInputFormat;
import com.token.uicomponents.CustomInput.EditTextInputType;
import com.token.uicomponents.CustomInput.InputListFragment;
import com.token.uicomponents.CustomInput.InputValidator;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;

import java.util.ArrayList;
import java.util.List;

public class CustomInputListFragment extends Fragment {

    List<IListMenuItem> menuItems = new ArrayList<>();
    private MainActivity mainActivity;

    public CustomInputListFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_input_list, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InputValidator validator = input -> input.getText().length() == 19;

        InputValidator validator2 = input -> input.getText().length() == 10;

        List<CustomInputFormat> inputList = new ArrayList<>();
        CustomInputFormat customInputFormat = new CustomInputFormat("Text",
                EditTextInputType.Text, 8, null,
                null);
        customInputFormat.setText("00000016");

        inputList.add(customInputFormat);

        inputList.add(new CustomInputFormat("Card Number", EditTextInputType.CreditCardNumber, null, "Invalid card number!", validator));
        inputList.add(new CustomInputFormat("Expire Date", EditTextInputType.ExpiryDate, null, null, null));
        inputList.add(new CustomInputFormat("CVV", EditTextInputType.CVV, null, null, null));
        inputList.add(new CustomInputFormat("Date", EditTextInputType.Date, null, null, null));
        inputList.add(new CustomInputFormat("Time", EditTextInputType.Time, null, null, null));
        inputList.add(new CustomInputFormat("Number", EditTextInputType.Number, null, null, null));
        inputList.add(new CustomInputFormat("Amount", EditTextInputType.Amount, null, null, null));
        inputList.add(new CustomInputFormat("IP", EditTextInputType.IpAddress, null, null, null));
        inputList.add(new CustomInputFormat("Password", EditTextInputType.Password, null, null, null));
        inputList.add(new CustomInputFormat("Password (Num)", EditTextInputType.NumberPassword, null, null, null));
        inputList.add(new CustomInputFormat("New Text", EditTextInputType.Text, null, "Max text size 10", validator2));

        inputList.get(1).setText("1234");

        InputListFragment fragment = InputListFragment.newInstance(inputList);
        mainActivity.replaceFragment(R.id.container, fragment, false);
        fragment.setActionLayout("Custom Input List", true, null); // Fragment has a back button and a title
    }

}