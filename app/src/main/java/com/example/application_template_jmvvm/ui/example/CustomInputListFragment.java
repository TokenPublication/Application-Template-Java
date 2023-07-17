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

import java.util.ArrayList;
import java.util.List;

public class CustomInputListFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_custom_input_list, container, false);
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

        inputList.add(new CustomInputFormat(getString(R.string.card_number), EditTextInputType.CreditCardNumber, null, getString(R.string.invalid_card_number), validator));
        inputList.add(new CustomInputFormat(getString(R.string.expire_date), EditTextInputType.ExpiryDate, null, null, null));
        inputList.add(new CustomInputFormat(getString(R.string.cvv), EditTextInputType.CVV, null, null, null));
        inputList.add(new CustomInputFormat(getString(R.string.date), EditTextInputType.Date, null, null, null));
        inputList.add(new CustomInputFormat(getString(R.string.time), EditTextInputType.Time, null, null, null));
        inputList.add(new CustomInputFormat(getString(R.string.number), EditTextInputType.Number, null, null, null));
        inputList.add(new CustomInputFormat(getString(R.string.amount), EditTextInputType.Amount, null, null, null));
        inputList.add(new CustomInputFormat(getString(R.string.ip), EditTextInputType.IpAddress, null, null, null));
        inputList.add(new CustomInputFormat(getString(R.string.password), EditTextInputType.Password, null, null, null));
        inputList.add(new CustomInputFormat(getString(R.string.num_password), EditTextInputType.NumberPassword, null, null, null));
        inputList.add(new CustomInputFormat(getString(R.string.new_text), EditTextInputType.Text, null, getString(R.string.invalid_text), validator2));

        inputList.get(1).setText("1234");

        InputListFragment fragment = InputListFragment.newInstance(inputList);
        mainActivity.replaceFragment(R.id.container, fragment, false);
        fragment.setActionLayout(getString(R.string.custom_input_list), true, null); // Fragment has a back button and a title
    }

}