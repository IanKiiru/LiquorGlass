package com.example.kiiru.liquorglass.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kiiru.liquorglass.Interface.ItemClickListener;
import com.example.kiiru.liquorglass.R;

/**
 * Created by Kiiru on 10/8/2017.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtOrderId, txtOrderStatus, txtOrderAddress, txtOrderPhone, customerName;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        customerName = (TextView) itemView.findViewById(R.id.customer_name);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}
