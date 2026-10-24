package com.example.smartpantrymanager.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;

import java.text.NumberFormat;
import java.util.List;



public class PantryItemAdapter
       extends RecyclerView.Adapter<PantryItemAdapter.PantryItemViewHolder> {

    private List<PantryItem> pantryItems; //Stores all the pantry items that the adapter must display

    public PantryItemAdapter(List<PantryItem> pantryItems){
        this.pantryItems = pantryItems;
    }

    //This method creates the visual ingredient cards
    @NonNull //Tells the method that it must not return null
    @Override
    public PantryItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryItemViewHolder(view);
    }

    //This method places real ingredient information into a card.
    //onCreatViewHolder() creates the empty card, while onBindViewHolder() fills that card
    @Override
    public void onBindViewHolder(
            @NonNull PantryItemViewHolder holder,
            int position //Identifies which ingredient RecyclerView is currently displaying
    ){
        PantryItem pantryItem = pantryItems.get(position); //Getting the position and storing it in an object pantryItem

        holder.textIngredientName.setText(pantryItem.getName()); //Displaying the name of the ingredient

        String formattedQuantity =
                NumberFormat.getNumberInstance().format(
                        pantryItem.getQuantity()
                );

        String quantityAndUnit =
                formattedQuantity + " " + pantryItem.getUnit(); //"2.5" + "Litres" = 2.5 Litres

        holder.textQuantityUnit.setText(quantityAndUnit);

        String expiryDate = pantryItem.getExpiryDate();

        if (expiryDate == null || expiryDate.isEmpty()){
            holder.textExpiryDate.setText("No expiry date");
        }else {
            holder.textExpiryDate.setText("Expires: " + expiryDate);
        }
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    public void updateItems(List<PantryItem> newPantryItems){
        pantryItems = newPantryItems;
        notifyDataSetChanged();
    }

    public static class PantryItemViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView textIngredientName;
        private final TextView textQuantityUnit;
        private final TextView textExpiryDate;

        public PantryItemViewHolder(@NonNull View itemView){
            super(itemView);
            //Variables under, represent the three TextViews inside item_pantry.xml

            textIngredientName = itemView.findViewById(R.id.textIngredientName);

            textQuantityUnit = itemView.findViewById(R.id.textQuantityUnit);

            textExpiryDate = itemView.findViewById(R.id.textExpiryDate);

        }
    }
}
