package com.example.savebite;

import android.os.Handler;
import android.os.Looper;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiHelper {
    private static final String API_KEY = "AIzaSyDk4i3l-q32Z5vLovYObKKTzLhkW2H1mSY";

    private static final String URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    public interface GeminiCallback {
        void onSuccess(String jsonResult);
        void onError(String error);
    }

    public static void generateRecipe(String ingredients, GeminiCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        String prompt = "I have these ingredients: " + ingredients +
                ". Suggest 3 distinct recipes. Return ONLY a JSON array. " +
                "Each object must have these keys: 'title', 'time' (e.g. '15 mins'), 'difficulty' (e.g. 'Easy'), 'ingredients' (array of strings), and 'instructions' (full text). " +
                "Do NOT use Markdown formatting (no ```json). Just raw JSON string.";

        JSONObject jsonBody = new JSONObject();
        try {
            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);
            JSONArray partsArray = new JSONArray();
            partsArray.put(textPart);
            JSONObject content = new JSONObject();
            content.put("parts", partsArray);
            JSONArray contentsArray = new JSONArray();
            contentsArray.put(content);
            jsonBody.put("contents", contentsArray);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(URL).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String respBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(respBody);

                        String text = jsonObject.getJSONArray("candidates")
                                .getJSONObject(0).getJSONObject("content")
                                .getJSONArray("parts").getJSONObject(0).getString("text");

                        text = text.replace("```json", "").replace("```", "").trim();

                        String finalText = text;
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(finalText));
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("Error parsing JSON response."));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("API Error: " + response.code()));
                }
            }
        });
    }
}