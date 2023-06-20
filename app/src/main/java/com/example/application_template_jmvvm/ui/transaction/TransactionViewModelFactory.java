package com.example.application_template_jmvvm.ui.transaction;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application_template_jmvvm.data.repository.TransactionRepository;

public class TransactionViewModelFactory implements ViewModelProvider.Factory {
    private TransactionRepository transactionRepository;

    public TransactionViewModelFactory(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TransactionViewModel.class)) {
            return (T) new TransactionViewModel(transactionRepository);
        }
        return null;
    }
}
