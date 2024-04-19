package com.example.application_template_jmvvm.data.database.batch;

import com.example.application_template_jmvvm.data.database.DatabaseInfo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
@Dao
public interface BatchDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBatch(Batch batch);

    @Query("UPDATE " + DatabaseInfo.BATCH_TABLE + " SET " + BatchCols.col_ulSTN + " = CASE WHEN " + BatchCols.col_ulSTN + " >= 999 THEN 0 ELSE " + BatchCols.col_ulSTN + " + 1 END WHERE ROWID = (SELECT ROWID FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1)")
    void updateSTN();

    @Query("UPDATE " + DatabaseInfo.BATCH_TABLE + " SET " + BatchCols.col_ulGUP_SN + " = " + BatchCols.col_ulGUP_SN + " + 1 WHERE ROWID = (SELECT ROWID FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1)")
    void updateGUPSN();

    @Query("UPDATE " + DatabaseInfo.BATCH_TABLE + " SET " + BatchCols.col_ulGUP_SN + " = 1, " + BatchCols.col_batchNo + " = " + BatchCols.col_batchNo + "+ 1 WHERE ROWID = (SELECT ROWID FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1)")
    void updateBatchNo();

    @Query("UPDATE " + DatabaseInfo.BATCH_TABLE + " SET " + BatchCols.col_previous_batch_slip + " = :batchSlip WHERE " + BatchCols.col_batchNo + " = :batchNo")
    void updateBatchSlip(String batchSlip, int batchNo);

    @Query("SELECT * FROM " + DatabaseInfo.BATCH_TABLE)
    List<Batch> getAllBatch();

    @Query("SELECT " + BatchCols.col_ulSTN + " FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1")
    int getSTN();

    @Query("SELECT " + BatchCols.col_ulGUP_SN + " FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1")
    int getGUPSN();

    @Query("SELECT " + BatchCols.col_batchNo + " FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1")
    int getBatchNo();

    @Query("SELECT " + BatchCols.col_previous_batch_slip + " FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1")
    String getBatchPreviousSlip();
}
