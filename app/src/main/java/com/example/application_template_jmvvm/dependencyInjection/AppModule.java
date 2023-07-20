package com.example.application_template_jmvvm.dependencyInjection;

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

/**
 * This is a Module for Hilt, Hilt has some modules to define some values. If you define those values there, you
 * don't need to define them everytime to call those. The only thing to do is @Inject method before you call those values
 * If you Inject them in constructor you need to @Inject constructor, else if in class first you should
 * annotate that class with @AndroidEntryPoint then you specify its class and add @Inject annotation to head of that.
 * Because there are different lifecycles in Android, in @InstallIn parameter you specify
 * to get more details you can visit https://developer.android.com/training/dependency-injection/hilt-android
 * lifecycle of values that those class defines
 * This is SingletonComponent because those values should live as application does
 */
@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    /**
     * It returns a AppTempDB instance
     * @Provide is for providing dependency which class it returns.
     * To do that without errors, You should specify only one provide method for each class
     * otherwise you should named them and call them with their names.
     * @Singleton is for making it single instance of this. If it doesn't exist, everytime we call this it creates new instance
     * @param app is Application, as I mentioned in AppTemp class, it comes from there and returns ActivityContext
     */
    @Provides
    @Singleton
    public AppTempDB provideDatabase(Application app) {
        return AppTempDB.getDatabase(app);
    }

    /**
     * It returns Repository instance
     * @param database is comes from provideDatabase method automatically
     */
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
