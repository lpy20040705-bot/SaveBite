package com.example.savebite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private List<Recipe> recipes;
    private Context context;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.tvTitle.setText(recipe.getTitle());
        holder.tvTime.setText("⏱ " + recipe.getTime());
        holder.tvDifficulty.setText("📊 " + recipe.getDifficulty());

        holder.itemView.setOnClickListener(v -> {
            if (context instanceof FragmentActivity) {
                RecipeDetailDialog dialog = RecipeDetailDialog.newInstance(recipe);
                dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "RecipeDetail");
            }
        });
    }

    @Override
    public int getItemCount() { return recipes.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvDifficulty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
        }
    }
}