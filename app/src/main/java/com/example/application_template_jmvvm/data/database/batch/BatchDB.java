package com.example.application_template_jmvvm.data.database.batch;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.application_template_jmvvm.data.database.DatabaseInfo;

@Entity(tableName = DatabaseInfo.BATCH_TABLE)
public class BatchDB {

    @ColumnInfo(name = BatchCol.col_ulSTN)
    private int col_ulSTN;

    @PrimaryKey
    @ColumnInfo(name = BatchCol.col_batchNo)
    private int col_batchNo = 1;

    @ColumnInfo(name = BatchCol.col_ulGUP_SN)
    private int col_ulGUP_SN = 1;

    @ColumnInfo(name = BatchCol.col_previous_batch_slip)
    private String col_previous_batch_slip;

    public int getCol_ulSTN() {
        return col_ulSTN;
    }

    public void setCol_ulSTN(int col_ulSTN) {
        this.col_ulSTN = col_ulSTN;
    }

    public int getCol_batchNo() {
        return col_batchNo;
    }

    public void setCol_batchNo(int col_batchNo) {
        this.col_batchNo = col_batchNo;
    }

    public int getCol_ulGUP_SN() {
        return col_ulGUP_SN;
    }

    public void setCol_ulGUP_SN(int col_ulGUP_SN) {
        this.col_ulGUP_SN = col_ulGUP_SN;
    }

    public String getCol_previous_batch_slip() {
        return col_previous_batch_slip;
    }

    public void setCol_previous_batch_slip(String col_previous_batch_slip) {
        this.col_previous_batch_slip = col_previous_batch_slip;
    }
}
