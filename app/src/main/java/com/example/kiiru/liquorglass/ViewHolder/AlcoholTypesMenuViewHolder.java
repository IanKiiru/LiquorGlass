package com.example.kiiru.liquorglass.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kiiru.liquorglass.Interface.ItemClickListener;
import com.example.kiiru.liquorglass.R;

/**
 * Created by Kiiru on 10/3/2017.
 */

public class AlcoholTypesMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView imageView;
    private ItemClickListener itemClickListener;

    public AlcoholTypesMenuViewHolder(View itemView){
        super(itemView);

        txtMenuName = (TextView) itemView.findViewById(R.id.alcohol_menu_name);
        imageView = (ImageView) itemView.findViewById(R.id.alcohol_menu_image);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v, getAdapterPosition(),false );

    }
}
