package com.example.kiiru.liquorglass.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kiiru.liquorglass.Interface.ItemClickListener;
import com.example.kiiru.liquorglass.R;

/**
 * Created by Kiiru on 10/4/2017.
 */

public class DrinksListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtDrinkName;
    public ImageView drinksImageView;

    private ItemClickListener itemClickListener;

    public DrinksListViewHolder(View itemView) {
        super(itemView);

        txtDrinkName = (TextView) itemView.findViewById(R.id.drinks_menu_name);
        drinksImageView = (ImageView) itemView.findViewById(R.id.drinks_menu_image);

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
