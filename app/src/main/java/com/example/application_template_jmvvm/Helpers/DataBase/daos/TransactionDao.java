package com.example.application_template_jmvvm.Helpers.DataBase.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.application_template_jmvvm.Helpers.DataBase.DatabaseHelper;
import com.example.application_template_jmvvm.Helpers.DataBase.DatabaseInfo;
import com.example.application_template_jmvvm.Helpers.DataBase.cols.TransactionCols;
import com.example.application_template_jmvvm.Helpers.DataBase.entities.TransactionEntity;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionCol;
import com.example.application_template_jmvvm.Helpers.DataBase.transaction.TransactionDB;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTransaction(TransactionEntity transaction);

    @Query("SELECT * FROM " + DatabaseInfo.TRANSACTIONTABLE + " WHERE " + TransactionCols.col_baPAN + " = :cardNo AND " + TransactionCols.col_isVoid + " <> 1 ORDER BY " + TransactionCols.col_ulGUP_SN + " DESC")
    List<TransactionEntity> getTransactionsByCardNo(String cardNo);

    @Query("SELECT * FROM " + DatabaseInfo.TRANSACTIONTABLE + " WHERE " + TransactionCols.col_baHostLogKey + " = :refNo")
    List<TransactionEntity> getTransactionsByRefNo(String refNo);

    @Query("SELECT * FROM " + DatabaseInfo.TRANSACTIONTABLE + " ORDER BY " + TransactionCols.col_ulGUP_SN)
    List<TransactionEntity> getAllTransactions();

    @Query("UPDATE " + DatabaseInfo.TRANSACTIONTABLE + " SET " + TransactionCols.col_isVoid + " = 1, " + TransactionCols.col_baVoidDateTime + " = :date, " + TransactionCols.col_SID + " = :card_SID WHERE " + TransactionCols.col_ulGUP_SN + " = :gupSN")
    void setVoid(int gupSN, String date, String card_SID);

    @Query("SELECT COUNT(*) FROM " + DatabaseInfo.TRANSACTIONTABLE)
    int isEmpty();

    @Query("DELETE FROM " + DatabaseInfo.TRANSACTIONTABLE)
    void deleteAll();
}







