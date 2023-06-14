package com.example.application_template_jmvvm.ui.transaction;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.repository.TransactionRepository;

public class TransactionViewModelFactory implements ViewModelProvider.Factory {
    private MainActivity main;
    private TransactionRepository transactionRepository;
    public TransactionViewModelFactory(MainActivity main, TransactionRepository transactionRepository) {
        this.main = main;
        this.transactionRepository = transactionRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TransactionViewModel.class)) {
            return (T) new TransactionViewModel(main,transactionRepository);
        }
        return null;
    }
}
