package com.example.savebite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RecipeDetailDialog extends DialogFragment {

    private static final String ARG_RECIPE = "recipe";

    public static RecipeDetailDialog newInstance(Recipe recipe) {
        RecipeDetailDialog fragment = new RecipeDetailDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_recipe_detail, container, false);

        Recipe recipe = (Recipe) getArguments().getSerializable(ARG_RECIPE);

        TextView tvTitle = view.findViewById(R.id.tvDetailTitle);
        TextView tvTime = view.findViewById(R.id.tvDetailTime);
        TextView tvDifficulty = view.findViewById(R.id.tvDetailDifficulty);
        TextView tvIngredients = view.findViewById(R.id.tvDetailIngredients);
        TextView tvInstructions = view.findViewById(R.id.tvDetailInstructions);
        View btnClose = view.findViewById(R.id.btnClose);

        if (recipe != null) {
            tvTitle.setText(recipe.getTitle());
            tvTime.setText("⏱ " + recipe.getTime());
            tvDifficulty.setText(recipe.getDifficulty().toUpperCase());

            StringBuilder sb = new StringBuilder();
            if (recipe.getIngredients() != null) {
                for (String ing : recipe.getIngredients()) {
                    sb.append("•  ").append(ing).append("\n");
                }
            }
            tvIngredients.setText(sb.toString());
            tvInstructions.setText(recipe.getInstructions());
        }

        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }
}