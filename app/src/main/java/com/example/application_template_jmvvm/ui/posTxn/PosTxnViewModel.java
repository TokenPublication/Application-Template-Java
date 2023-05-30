package com.example.application_template_jmvvm.ui.posTxn;

import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;

public class PosTxnViewModel extends ViewModel {

    public void replaceFragment(MainActivity mainActivity, ListMenuFragment mListMenuFragment) {
        new Thread(() -> mainActivity.replaceFragment(R.id.container, mListMenuFragment, false)).start();
    }

}