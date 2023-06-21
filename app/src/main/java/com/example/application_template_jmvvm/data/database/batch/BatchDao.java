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
    long insertBatch(BatchDB batch);

    @Query("UPDATE " + DatabaseInfo.BATCHTABLE + " SET " + BatchCol.col_ulGUP_SN + " = :groupSn + 1 WHERE " + BatchCol.col_ulGUP_SN + " = :groupSn")
    void updateGUPSN(int groupSn);

    @Query("UPDATE " + DatabaseInfo.BATCHTABLE + " SET " + BatchCol.col_ulGUP_SN + " = " + BatchCol.col_ulGUP_SN + " + 1 WHERE ROWID = (SELECT ROWID FROM " + DatabaseInfo.BATCHTABLE + " LIMIT 1)")
    void incrementGUPSN();

    @Query("UPDATE " + DatabaseInfo.BATCHTABLE + " SET " + BatchCol.col_ulGUP_SN + " = 1, " + BatchCol.col_batchNo + " = :batchNo + 1 WHERE " + BatchCol.col_batchNo + " = :batchNo")
    void updateBatchNo(int batchNo);

    @Query("UPDATE " + DatabaseInfo.BATCHTABLE + " SET " + BatchCol.col_previous_batch_slip + " = :batchSlip WHERE " + BatchCol.col_batchNo + " = :batchNo")
    void updateBatchSlip(String batchSlip, int batchNo);

    @Query("SELECT * FROM " + DatabaseInfo.BATCHTABLE)
    List<BatchDB> getAllBatch();

    @Query("SELECT " + BatchCol.col_ulGUP_SN + " FROM " + DatabaseInfo.BATCHTABLE + " LIMIT 1")
    int getGUPSN();

    @Query("SELECT " + BatchCol.col_batchNo + " FROM " + DatabaseInfo.BATCHTABLE + " LIMIT 1")
    int getBatchNo();

    @Query("SELECT " + BatchCol.col_previous_batch_slip + " FROM " + DatabaseInfo.BATCHTABLE + " LIMIT 1")
    String getBatchPreviousSlip();

    @Query("DELETE FROM " + DatabaseInfo.BATCHTABLE)
    void deleteAll();

}
