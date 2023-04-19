package com.example.application_template_jmvvm.Viewmodels;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.Uicomponents.MainActivity;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;

public class SettingsViewModel extends ViewModel {

    public void replaceFragment(MainActivity mainActivity, Fragment mFragment, Boolean addToBackStack) {
        new Thread(() -> mainActivity.replaceFragment(R.id.container, mFragment, addToBackStack)).start();
    }
}