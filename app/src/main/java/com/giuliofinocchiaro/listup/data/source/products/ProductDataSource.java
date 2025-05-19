package com.giuliofinocchiaro.listup.data.source.products;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.giuliofinocchiaro.listup.data.Constants;
import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.Product;
import com.giuliofinocchiaro.listup.data.repository.CategoryRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductDataSource {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void getProducts(CategoryRepository categoryRepository, ProductDataSource.ProductCallback callback) {
        executor.execute(() -> {
            try {
                Log.d("List", "Dentro");
                URL url = new URL(Constants.urlAPI + "products/getProducts.php");
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

                    Log.d("Product", jsonResponse.toString());

                    if (jsonResponse.optBoolean("status", false)) {
                        // Ottengo direttamente l'array
                        JSONArray jsonArray = jsonResponse.getJSONArray("message");

                        ArrayList<Product> prodcuts = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject product = jsonArray.optJSONObject(i);
                            if (product == null) continue;

                            prodcuts.add(new Product(
                                    product.optInt("id"),
                                    product.optString("name"),
                                    categoryRepository.getCategoryById(product.optInt("category")),
                                    product.optString("unit"),
                                    product.optInt("is_available") == 1,
                                    product.optString("icon")
                            ));
                        }

                        Log.d("Product", "Product: " + prodcuts);

                        mainHandler.post(() ->
                                callback.onSuccess(new Result.Success<>(prodcuts))
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

    public interface ProductCallback{
        void onSuccess(Result.Success<ArrayList<Product>> result);
        void onError(Result.Error error);
    }
}
