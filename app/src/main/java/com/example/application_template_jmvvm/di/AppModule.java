package com.example.application_template_jmvvm.di;

import android.app.Application;

import com.example.application_template_jmvvm.data.database.AppTempDB;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.data.repository.CardRepository;
import com.example.application_template_jmvvm.data.repository.TransactionRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public AppTempDB provideDatabase(Application app) {
        return AppTempDB.getDatabase(app);
    }

    @Provides
    @Singleton
    public ActivationRepository provideActivationRepository(AppTempDB database) {
        return new ActivationRepository(database.activationDao());
    }

    @Provides
    @Singleton
    public BatchRepository provideBatchRepository(AppTempDB database) {
        return new BatchRepository(database.batchDao());
    }

    @Provides
    @Singleton
    public TransactionRepository provideTransactionRepository(AppTempDB database) {
        return new TransactionRepository(database.transactionDao());
    }

    @Provides
    @Singleton
    public CardRepository provideCardRepository() {
        return new CardRepository();
    }
}
