package com.example.application_template_jmvvm.Viewmodels;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.Uicomponents.SaleFragment;
import com.example.application_template_jmvvm.Uicomponents.MainActivity;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;
import com.token.uicomponents.ListMenuFragment.ListMenuFragment;

import java.util.ArrayList;
import java.util.List;

public class PosTxnViewModel extends ViewModel {

    public void replaceFragment(MainActivity mainActivity, ListMenuFragment mListMenuFragment) {
        new Thread(() -> mainActivity.replaceFragment(R.id.container, mListMenuFragment, false)).start();
    }

}