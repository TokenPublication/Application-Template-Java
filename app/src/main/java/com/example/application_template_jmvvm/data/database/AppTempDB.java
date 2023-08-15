package com.example.application_template_jmvvm.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.application_template_jmvvm.data.database.activation.Activation;
import com.example.application_template_jmvvm.data.database.activation.ActivationDao;
import com.example.application_template_jmvvm.data.database.batch.Batch;
import com.example.application_template_jmvvm.data.database.batch.BatchDao;
import com.example.application_template_jmvvm.data.database.transaction.TransactionDao;
import com.example.application_template_jmvvm.data.database.transaction.Transaction;

import java.util.concurrent.Executors;

@Database(entities = {Transaction.class, Batch.class, Activation.class}, version = DatabaseInfo.DATABASE_VERSION, exportSchema = false)
public abstract class AppTempDB extends RoomDatabase {

    public abstract TransactionDao transactionDao();
    public abstract BatchDao batchDao();
    public abstract ActivationDao activationDao();
    private static volatile AppTempDB INSTANCE;
    private static final Object lock = new Object();

    public static AppTempDB getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppTempDB.class, DatabaseInfo.DATABASE_NAME)
                            .allowMainThreadQueries()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        AppTempDB dbInstance = getDatabase(context);
                                        Activation activation = new Activation();
                                        activation.setColMerchantId("3785971905");
                                        activation.setColTerminalId("94820525");
                                        //TODO Developer, assigned temporary values for merchant and terminal ID for not getting null
                                        dbInstance.activationDao().insertActivation(activation);
                                        Batch batch = new Batch();
                                        dbInstance.batchDao().insertBatch(batch);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
