package com.example.application_template_jmvvm.Helpers.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.application_template_jmvvm.Helpers.DataBase.DataModel;
import com.example.application_template_jmvvm.Helpers.StringHelper;
import com.example.application_template_jmvvm.R;

import java.util.List;

public class TransactionsRecycleAdapter extends RecyclerView.Adapter<TransactionsRecycleAdapter.MyHolder> {
    List<DataModel> dataModelArrayList;

    public TransactionsRecycleAdapter(List<DataModel> dataModelArrayList) {
        this.dataModelArrayList = dataModelArrayList;
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

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        DataModel dataModel=dataModelArrayList.get(position);
        holder.card_no.setText(StringHelper.maskCardNumber(dataModel.getCard_no()));
        holder.process_time.setText(dataModel.getProcess_time());
        holder.sale_amount.setText(StringHelper.getAmount(Integer.parseInt(dataModel.getSale_amount())));
        holder.approval_code.setText(dataModel.getApproval_code());
        holder.serial_no.setText(dataModel.getSerial_no());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }
}
