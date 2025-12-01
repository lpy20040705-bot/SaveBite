package com.example.savebite;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {
    private List<PantryItem> items;
    private Context context;
    private OnItemClickListener listener;
    private DatabaseHelper db;

    public interface OnItemClickListener {
        void onDeleteClick(int id);
        void onStatusChange(); 
    }

    public PantryAdapter(Context context, List<PantryItem> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.qty.setText(item.getQuantity());

        String status = item.getStatus();
        holder.details.setText(status.toUpperCase() + " • " + item.getExpiryDate());

        
        int colorRes;
        if (status.equals("consumed")) {
            colorRes = R.color.accent_green; 
            holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); 
            holder.name.setTextColor(Color.GRAY);
        } else {
            holder.name.setPaintFlags(holder.name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); 
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.black));

            if (status.equals("expired")) colorRes = R.color.status_expired;
            else if (status.equals("warning")) colorRes = R.color.status_warning;
            else colorRes = R.color.status_good;
        }
        holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(context, colorRes));

        
        holder.cbConsumed.setOnCheckedChangeListener(null);
        holder.cbConsumed.setChecked(item.getIsConsumed() == 1);

        
        holder.cbConsumed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int newStatus = isChecked ? 1 : 0;
            item.setIsConsumed(newStatus);
            db.updateItemStatus(item.getId(), newStatus); 

            
            notifyItemChanged(holder.getAdapterPosition());

            
            if (listener != null) listener.onStatusChange();
        });

        holder.deleteBtn.setOnClickListener(v -> listener.onDeleteClick(item.getId()));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, details, qty;
        View statusIndicator;
        ImageView deleteBtn;
        CheckBox cbConsumed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemName);
            details = itemView.findViewById(R.id.itemDetails);
            qty = itemView.findViewById(R.id.itemQty);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            deleteBtn = itemView.findViewById(R.id.btnDelete);
            cbConsumed = itemView.findViewById(R.id.cbConsumed);
        }
    }
}
