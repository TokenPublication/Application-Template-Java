package com.example.application_template_jmvvm.ui.posTxn;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.repository.BatchRepository;
import com.example.application_template_jmvvm.ui.transaction.TransactionViewModel;

public class BatchViewModelFactory implements ViewModelProvider.Factory {
    private BatchRepository batchRepository;
    public BatchViewModelFactory(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BatchViewModel.class)) {
            return (T) new BatchViewModel(batchRepository);
        }
        return null;
    }
}
