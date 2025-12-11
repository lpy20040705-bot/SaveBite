package com.example.savebite;

import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable {
    private String title;
    private String time;
    private String difficulty;
    private List<String> ingredients;
    private String instructions;

    public Recipe(String title, String time, String difficulty, List<String> ingredients, String instructions) {
        this.title = title;
        this.time = time;
        this.difficulty = difficulty;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getTitle() { return title; }
    public String getTime() { return time; }
    public String getDifficulty() { return difficulty; }
    public List<String> getIngredients() { return ingredients; }
    public String getInstructions() { return instructions; }
}