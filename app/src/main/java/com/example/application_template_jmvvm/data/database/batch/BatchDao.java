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
    void insertBatch(BatchDB batch);

    @Query("UPDATE " + DatabaseInfo.BATCH_TABLE + " SET " + BatchCol.col_ulSTN + " = CASE WHEN " + BatchCol.col_ulSTN + " >= 9999 THEN 0 ELSE " + BatchCol.col_ulSTN + " + 1 END WHERE ROWID = (SELECT ROWID FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1)")
    void updateSTN();

    @Query("UPDATE " + DatabaseInfo.BATCH_TABLE + " SET " + BatchCol.col_ulGUP_SN + " = " + BatchCol.col_ulGUP_SN + " + 1 WHERE ROWID = (SELECT ROWID FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1)")
    void updateGUPSN();

    @Query("UPDATE " + DatabaseInfo.BATCH_TABLE + " SET " + BatchCol.col_ulGUP_SN + " = 1, " + BatchCol.col_batchNo + " = " + BatchCol.col_batchNo + "+ 1 WHERE ROWID = (SELECT ROWID FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1)")
    void updateBatchNo();

    @Query("UPDATE " + DatabaseInfo.BATCH_TABLE + " SET " + BatchCol.col_previous_batch_slip + " = :batchSlip WHERE " + BatchCol.col_batchNo + " = :batchNo")
    void updateBatchSlip(String batchSlip, int batchNo);

    @Query("SELECT * FROM " + DatabaseInfo.BATCH_TABLE)
    List<BatchDB> getAllBatch();

    @Query("SELECT " + BatchCol.col_ulSTN + " FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1")
    int getSTN();

    @Query("SELECT " + BatchCol.col_ulGUP_SN + " FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1")
    int getGUPSN();

    @Query("SELECT " + BatchCol.col_batchNo + " FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1")
    int getBatchNo();

    @Query("SELECT " + BatchCol.col_previous_batch_slip + " FROM " + DatabaseInfo.BATCH_TABLE + " LIMIT 1")
    String getBatchPreviousSlip();

    @Query("DELETE FROM " + DatabaseInfo.BATCH_TABLE)
    void deleteAll();
}
