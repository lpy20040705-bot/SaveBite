package com.example.savebite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RecipesFragment extends Fragment {
    private Button btnGenerate;
    private RecyclerView recyclerView;
    private TextView tvLoading;
    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        btnGenerate = view.findViewById(R.id.btnGenerate);
        recyclerView = view.findViewById(R.id.recyclerViewRecipes);
        tvLoading = view.findViewById(R.id.tvLoading);

        db = new DatabaseHelper(getContext());
        recipeList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeAdapter(getContext(), recipeList);
        recyclerView.setAdapter(adapter);

        btnGenerate.setOnClickListener(v -> generateRecipes());

        return view;
    }

    private void generateRecipes() {
        // 核心修改：调用 getAvailableItems() 来获取未消耗的食材
        List<PantryItem> items = db.getAvailableItems();

        if (items.isEmpty()) {
            Toast.makeText(getContext(), "No available ingredients! Add fresh items or unmark consumed items.", Toast.LENGTH_LONG).show();
            return;
        }

        StringBuilder ingredients = new StringBuilder();
        for (PantryItem item : items) {
            ingredients.append(item.getName()).append(", ");
        }

        tvLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        btnGenerate.setEnabled(false);
        btnGenerate.setText("Thinking...");

        // 假设 GeminiHelper 和 Recipe/PantryItem Model 已在项目中定义
        GeminiHelper.generateRecipe(ingredients.toString(), new GeminiHelper.GeminiCallback() {
            @Override
            public void onSuccess(String jsonResult) {
                try {
                    recipeList.clear();
                    JSONArray jsonArray = new JSONArray(jsonResult);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String title = obj.optString("title", "Recipe");
                        String time = obj.optString("time", "15 mins");
                        String diff = obj.optString("difficulty", "Medium");
                        String instructions = obj.optString("instructions", "No details.");

                        List<String> ingredientList = new ArrayList<>();
                        JSONArray ingArr = obj.optJSONArray("ingredients");
                        if (ingArr != null) {
                            for(int j=0; j<ingArr.length(); j++) {
                                ingredientList.add(ingArr.getString(j));
                            }
                        }

                        recipeList.add(new Recipe(title, time, diff, ingredientList, instructions));
                    }

                    adapter.notifyDataSetChanged();

                    tvLoading.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    btnGenerate.setEnabled(true);
                    btnGenerate.setText("Generate New Ideas");

                } catch (Exception e) {
                    onError("Failed to parse recipes.");
                }
            }

            @Override
            public void onError(String error) {
                tvLoading.setText("Error: " + error);
                btnGenerate.setEnabled(true);
                btnGenerate.setText("Try Again");
            }
        });
    }
}