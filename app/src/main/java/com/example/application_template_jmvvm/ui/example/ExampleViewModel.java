package com.example.application_template_jmvvm.ui.example;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;

public class ExampleViewModel extends ViewModel {
    public void replaceFragment(MainActivity mainActivity, Fragment mFragment, Boolean addToBackStack) {
        new Thread(() -> mainActivity.replaceFragment(R.id.container, mFragment, addToBackStack)).start();
    }
}