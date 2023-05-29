package com.example.application_template_jmvvm.domain.helper.adapter;

import android.content.ContentValues;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application_template_jmvvm.data.database.transaction.TransactionCol;
import com.example.application_template_jmvvm.domain.helper.StringHelper;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.ui.transaction.VoidFragment;

import java.util.ArrayList;

public class TransactionsRecycleAdapter extends RecyclerView.Adapter<TransactionsRecycleAdapter.MyHolder> {

    private ArrayList<ContentValues> transactionList;
    private VoidFragment voidFragment;

    public TransactionsRecycleAdapter(ArrayList<ContentValues> transactionList, VoidFragment voidFragment) {
        this.transactionList = transactionList;
        this.voidFragment = voidFragment;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ContentValues transaction = transactionList.get(position);
        holder.card_no.setText(StringHelper.MaskTheCardNo(transaction.getAsString(TransactionCol.col_baPAN.name())));
        holder.process_time.setText(transaction.getAsString(TransactionCol.col_baTranDate.name()));
        holder.sale_amount.setText(StringHelper.getAmount(Integer.parseInt(transaction.getAsString(TransactionCol.col_ulAmount.name()))));
        holder.approval_code.setText(transaction.getAsString(TransactionCol.col_authCode.name()));
        holder.serial_no.setText(transaction.getAsString(TransactionCol.col_ulGUP_SN.name()));
        transaction.put(TransactionCol.col_isVoid.name(),1);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authCode = transaction.getAsString(TransactionCol.col_authCode.name());
                voidFragment.doVoid(transaction,authCode);
                Log.d("RecyclerView/onClick", "ContentVal: " + transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView card_no, process_time, sale_amount, approval_code, serial_no;

        public MyHolder(View itemView) {
            super(itemView);

            card_no = (TextView) itemView.findViewById(R.id.textCardNo);
            process_time = (TextView) itemView.findViewById(R.id.textDate);
            sale_amount = (TextView) itemView.findViewById(R.id.textAmount);
            approval_code = (TextView) itemView.findViewById(R.id.textApprovalCode);
            serial_no = (TextView) itemView.findViewById(R.id.tvSN);
        }
    }

}
