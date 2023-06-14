package com.example.application_template_jmvvm.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.application_template_jmvvm.data.database.activation.ActivationDao;
import com.example.application_template_jmvvm.data.database.activation.ActivationEntity;
import com.example.application_template_jmvvm.data.database.batch.BatchDB;
import com.example.application_template_jmvvm.data.database.batch.BatchDao;
import com.example.application_template_jmvvm.data.database.transaction.TransactionDao;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;

@Database(entities = {TransactionEntity.class, BatchDB.class, ActivationEntity.class}, version = DatabaseInfo.DATABASEVERSION, exportSchema = false)
public abstract class AppTempDB extends RoomDatabase {

    public abstract TransactionDao transactionDao();
    public abstract BatchDao batchDao();
    public abstract ActivationDao activationDao();
    private static volatile AppTempDB INSTANCE;

    public static AppTempDB getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppTempDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppTempDB.class, DatabaseInfo.DATABASENAME)
                                .allowMainThreadQueries()
                                .build();
                }
            }
        }
        return INSTANCE;
    }
}
