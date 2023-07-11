package com.example.application_template_jmvvm.di;

import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.data.repository.CardRepository;
import com.example.application_template_jmvvm.data.repository.TransactionRepository;
import com.example.application_template_jmvvm.ui.posTxn.batch.BatchViewModel;
import com.example.application_template_jmvvm.ui.activation.ActivationViewModel;
import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class ViewModelModule {

    @Provides
    @ViewModelScoped
    public ActivationViewModel provideActivationViewModel(ActivationRepository repository) {
        return new ActivationViewModel(repository);
    }

    @Provides
    @ViewModelScoped
    public BatchViewModel provideBatchViewModel(BatchRepository repository) {
        return new BatchViewModel(repository);
    }

    @Provides
    @ViewModelScoped
    public TransactionViewModel provideTransactionViewModel(TransactionRepository repository) {
        return new TransactionViewModel(repository);
    }

    @Provides
    @ViewModelScoped
    public CardViewModel provideCardViewModel(CardRepository repository) {
        return new CardViewModel(repository);
    }
}
