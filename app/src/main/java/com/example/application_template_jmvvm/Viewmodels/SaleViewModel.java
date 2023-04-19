package com.example.application_template_jmvvm.Viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SaleViewModel extends ViewModel {
    private MutableLiveData<String> mActionName = new MutableLiveData<>();

    public LiveData<String> getActionName() {
        return mActionName;
    }

    public void setActionName(String actionName) {
        mActionName.setValue(actionName);
    }

}