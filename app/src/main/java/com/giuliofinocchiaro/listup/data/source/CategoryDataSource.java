package com.giuliofinocchiaro.listup.data.source;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.giuliofinocchiaro.listup.data.Constants;
import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.Category;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.data.model.User;
import com.giuliofinocchiaro.listup.data.source.lists.ListDataSource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryDataSource {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void getCategories(CategoryCallback callback) {
        executor.execute(() -> {
            try {
                Log.d("List", "Dentro");
                URL url = new URL(Constants.urlAPI + "categories/getCategories.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();
                try (Scanner scanner = new Scanner(
                        responseCode == 200 ? conn.getInputStream() : conn.getErrorStream()
                )) {
                    String response = scanner.useDelimiter("\\A").next();
                    JSONObject jsonResponse = new JSONObject(response);

                    Log.d("List", jsonResponse.toString());

                    if (jsonResponse.optBoolean("status", false)) {
                        // Ottengo direttamente l'array
                        JSONArray jsonArray = jsonResponse.getJSONArray("message");

                        ArrayList<Category> categories = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject category = jsonArray.optJSONObject(i);
                            if (category == null) continue;

                            categories.add(new Category(
                                    category.optInt("id"),
                                    category.optString("name"),
                                    category.optString("icon")
                            ));
                        }

                        Log.d("Category", "Category: " + categories);

                        mainHandler.post(() ->
                                callback.onSuccess(new Result.Success<>(categories))
                        );
                    } else {
                        String errorMessage = jsonResponse.optString("message", "Errore sconosciuto");
                        mainHandler.post(() ->
                                callback.onError(new Result.Error(new Exception(errorMessage)))
                        );
                    }
                }
            } catch (Exception e) {
                mainHandler.post(() ->
                        callback.onError(new Result.Error(new IOException("Error get lists in", e)))
                );
            }
        });
    }

    public interface CategoryCallback{
        void onSuccess(Result.Success<ArrayList<Category>> result);
        void onError(Result.Error error);
    }
}
