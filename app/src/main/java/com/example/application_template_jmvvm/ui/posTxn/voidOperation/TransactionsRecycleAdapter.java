package com.example.application_template_jmvvm.ui.posTxn.voidOperation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application_template_jmvvm.data.database.transaction.Transaction;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.ui.posTxn.slip.SlipFragment;
import com.example.application_template_jmvvm.utils.printHelpers.DateUtil;
import com.example.application_template_jmvvm.utils.printHelpers.StringHelper;
import com.example.application_template_jmvvm.R;

import java.util.List;

/**
 * This class for show Transactions with recyclerView.
 */
public class TransactionsRecycleAdapter extends RecyclerView.Adapter<TransactionsRecycleAdapter.MyHolder> {
    private List<Transaction> transactionList;
    private VoidFragment voidFragment;
    private SlipFragment slipFragment;

    public TransactionsRecycleAdapter(List<Transaction> transactionList, VoidFragment voidFragment, SlipFragment slipFragment) {
        this.transactionList = transactionList;
        this.voidFragment = voidFragment;
        this.slipFragment = slipFragment;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.card_no.setText(StringHelper.MaskTheCardNo(transaction.getBaPAN()));
        String date = DateUtil.getFormattedDate(transaction.getBaTranDate().substring(0, 8)) + " " + DateUtil.getFormattedTime(transaction.getBaTranDate().substring(8));
        holder.process_time.setText(date);
        if (transaction.getbTransCode() != TransactionCode.SALE.getType() && transaction.getbTransCode() != TransactionCode.INSTALLMENT_SALE.getType()) {
            holder.sale_amount.setText(StringHelper.getAmount(transaction.getUlAmount2()));
        } else {
            holder.sale_amount.setText(StringHelper.getAmount(transaction.getUlAmount()));
        }
        holder.approval_code.setText(transaction.getAuthCode());
        holder.serial_no.setText(String.valueOf(transaction.getUlGUP_SN()));
        holder.itemView.setOnClickListener(v -> {
            if (voidFragment != null) {
                voidFragment.doVoid(voidFragment.getViewLifecycleOwner(), transaction, false);
            } else {
                slipFragment.prepareSlip(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        TextView card_no, process_time, sale_amount, approval_code, serial_no;

        public MyHolder(View itemView) {
            super(itemView);

            card_no = itemView.findViewById(R.id.textCardNo);
            process_time = itemView.findViewById(R.id.textDate);
            sale_amount = itemView.findViewById(R.id.textAmount);
            approval_code = itemView.findViewById(R.id.textApprovalCode);
            serial_no = itemView.findViewById(R.id.tvSN);
        }
    }
}
