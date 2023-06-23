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

    @Query("SELECT * FROM " + DatabaseInfo.TRANSACTIONTABLE + " WHERE " + TransactionCols.col_baPAN + " = :cardNo AND " + TransactionCols.col_isVoid + " <> 1 ORDER BY " + TransactionCols.col_ulGUP_SN + " DESC")
    List<TransactionEntity> getTransactionsByCardNo(String cardNo);

    @Query("SELECT * FROM " + DatabaseInfo.TRANSACTIONTABLE + " WHERE " + TransactionCols.col_refNo + " = :refNo")
    List<TransactionEntity> getTransactionsByRefNo(String refNo);

    @Query("SELECT * FROM " + DatabaseInfo.TRANSACTIONTABLE + " ORDER BY " + TransactionCols.col_ulGUP_SN)
    List<TransactionEntity> getAllTransactions();

    @Query("UPDATE " + DatabaseInfo.TRANSACTIONTABLE + " SET " + TransactionCols.col_isVoid + " = 1, " + TransactionCols.col_baVoidDateTime + " = :date, " + TransactionCols.col_SID + " = :card_SID WHERE " + TransactionCols.col_ulGUP_SN + " = :gupSN")
    void setVoid(int gupSN, String date, String card_SID);

    @Query("SELECT COUNT(*) FROM " + DatabaseInfo.TRANSACTIONTABLE + " WHERE " + TransactionCols.col_isVoid + " = 0 ")
    int isEmptyVoid();

    @Query("SELECT COUNT(*) FROM " + DatabaseInfo.TRANSACTIONTABLE)
    int isEmpty();

    @Query("DELETE FROM " + DatabaseInfo.TRANSACTIONTABLE)
    void deleteAll();
}







