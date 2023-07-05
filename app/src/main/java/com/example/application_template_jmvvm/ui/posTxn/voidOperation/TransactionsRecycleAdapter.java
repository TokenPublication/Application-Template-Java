package com.example.application_template_jmvvm.ui.posTxn.voidOperation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.utils.printHelpers.StringHelper;
import com.example.application_template_jmvvm.R;

import java.util.List;

public class TransactionsRecycleAdapter extends RecyclerView.Adapter<TransactionsRecycleAdapter.MyHolder> {

    private List<TransactionEntity> transactionList;
    private VoidFragment voidFragment;

    public TransactionsRecycleAdapter(List<TransactionEntity> transactionList, VoidFragment voidFragment) {
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
        TransactionEntity transaction = transactionList.get(position);
        holder.card_no.setText(StringHelper.MaskTheCardNo(transaction.getBaPAN()));
        holder.process_time.setText(transaction.getBaTranDate());
        holder.sale_amount.setText(StringHelper.getAmount(transaction.getUlAmount()));
        holder.approval_code.setText(transaction.getAuthCode());
        holder.serial_no.setText(String.valueOf(transaction.getUlGUP_SN()));
        holder.itemView.setOnClickListener(v -> voidFragment.startVoid(transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
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
