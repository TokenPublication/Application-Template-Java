package com.example.application_template_jmvvm.data.database.transaction;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.application_template_jmvvm.data.database.DatabaseInfo;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTransaction(TransactionEntity transaction);

    @Query("SELECT * FROM " + DatabaseInfo.TRANSACTION_TABLE + " WHERE " + TransactionCols.col_baPAN + " = :cardNo AND " + TransactionCols.col_isVoid + " <> 1 ORDER BY " + TransactionCols.col_ulGUP_SN + " ASC")
    List<TransactionEntity> getTransactionsByCardNo(String cardNo);

    @Query("SELECT * FROM " + DatabaseInfo.TRANSACTION_TABLE + " WHERE " + TransactionCols.col_refNo + " = :refNo")
    List<TransactionEntity> getTransactionsByRefNo(String refNo);

    @Query("SELECT * FROM " + DatabaseInfo.TRANSACTION_TABLE + " WHERE " + TransactionCols.col_uuid + " = :uuid")
    List<TransactionEntity> getTransactionsByUUID(String uuid);

    @Query("SELECT * FROM " + DatabaseInfo.TRANSACTION_TABLE + " ORDER BY " + TransactionCols.col_ulGUP_SN)
    List<TransactionEntity> getAllTransactions();

    @Query("UPDATE " + DatabaseInfo.TRANSACTION_TABLE + " SET " + TransactionCols.col_isVoid + " = 1, " + TransactionCols.col_baVoidDateTime + " = :date, " + TransactionCols.col_SID + " = :card_SID WHERE " + TransactionCols.col_ulGUP_SN + " = :gupSN")
    void setVoid(int gupSN, String date, String card_SID);

    @Query("SELECT COUNT(*) FROM " + DatabaseInfo.TRANSACTION_TABLE + " WHERE " + TransactionCols.col_isVoid + " = 0 ")
    int isEmptyVoid();

    @Query("SELECT COUNT(*) FROM " + DatabaseInfo.TRANSACTION_TABLE)
    int isEmpty();

    @Query("DELETE FROM " + DatabaseInfo.TRANSACTION_TABLE)
    void deleteAll();
}
