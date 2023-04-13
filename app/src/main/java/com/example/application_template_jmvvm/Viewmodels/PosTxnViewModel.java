package com.example.application_template_jmvvm.Viewmodels;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.Uicomponents.SaleFragment;
import com.example.application_template_jmvvm.Uicomponents.MainActivity;
import com.token.uicomponents.ListMenuFragment.IListMenuItem;

import java.util.ArrayList;
import java.util.List;

public class PosTxnViewModel extends ViewModel {
    private MutableLiveData<List<IListMenuItem>> mMenuItems;

    public PosTxnViewModel() {
        mMenuItems = new MutableLiveData<>();
    }

    public LiveData<List<IListMenuItem>> getMenuItems() {
        return mMenuItems;
    }

    public void setMenuItems(List<IListMenuItem> menuItems) {
        mMenuItems.setValue(menuItems);
    }
}