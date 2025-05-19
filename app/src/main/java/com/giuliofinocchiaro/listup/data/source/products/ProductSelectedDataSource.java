package com.giuliofinocchiaro.listup.data.source.products;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.giuliofinocchiaro.listup.data.Constants;
import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.Product;
import com.giuliofinocchiaro.listup.data.model.ProductSelected;
import com.giuliofinocchiaro.listup.data.model.User;
import com.giuliofinocchiaro.listup.data.repository.ListRepository;
import com.giuliofinocchiaro.listup.data.repository.ProductRepository;
import com.giuliofinocchiaro.listup.data.source.auth.UserDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductSelectedDataSource {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void getProducts(
            ProductRepository productRepository,
            ListRepository listRepository,
            int listId,
            int userId,
            ProductSelectedCallback callback
    ) {
        executor.execute(() -> {
            try {
                URL url = new URL(Constants.urlAPI + "product_in_list/getProductsInList.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Costruisco il JSON di richiesta
                JSONObject payload = new JSONObject();
                payload.put("list_id", listId);
                payload.put("user_id", userId);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                try (Scanner scanner = new Scanner(
                        code == 200 ? conn.getInputStream() : conn.getErrorStream()
                )) {
                    String resp = scanner.useDelimiter("\\A").next();
                    JSONObject root = new JSONObject(resp);

                    if (!root.optBoolean("status", false)) {
                        String msg = root.optString("message", "Errore sconosciuto");
                        mainHandler.post(() ->
                                callback.onError(new Result.Error(new Exception(msg)))
                        );
                        return;
                    }

                    JSONArray array = root.getJSONArray("message");
                    ArrayList<ProductSelected> results = new ArrayList<>();

                    // se non ci sono prodotti, rispondi subito
                    if (array.length() == 0) {
                        mainHandler.post(() ->
                                callback.onSuccess(new Result.Success<>(results))
                        );
                        return;
                    }

                    // contatore per callback unica
                    final int total = array.length();

                    for (int i = 0; i < total; i++) {
                        JSONObject obj = array.getJSONObject(i);

                        int psId       = obj.optInt("id");
                        int prodId     = obj.optInt("product_id");
                        int optId      = obj.optInt("option_id");
                        double qty     = obj.optDouble("quantity");
                        String desc    = obj.optString("description");
                        boolean urgent = obj.optBoolean("is_urgent", false);
                        boolean worth  = obj.optBoolean("is_worth_it", false);
                        boolean offer  = obj.optBoolean("is_on_offer", false);
                        boolean checked= obj.optBoolean("checked", false);

                        int ownerId = obj.optInt("user_id");
                        Product p = productRepository.getProductById(prodId);
                        ProductSelected ps = new ProductSelected(
                                p, psId,
                                listRepository.getListById(listId),
                                new User(-1, "", "", "", ""),
                                String.valueOf(optId),
                                qty, desc,
                                urgent, worth, offer, checked
                        );
                        results.add(ps);
                    }
                    mainHandler.post(() ->
                            callback.onSuccess(new Result.Success<>(results))
                    );
                }
            } catch (Exception e) {
                mainHandler.post(() ->
                        callback.onError(new Result.Error(new IOException("Error fetching products", e)))
                );
            }
        });
    }

    public void removeProduct(
            int listId,
            int userId,
            int product_id,
            ProductRemoveSelectedCallback callback
    ) {
        executor.execute(() -> {
            try {
                URL url = new URL(Constants.urlAPI + "product_in_list/postRemoveProductInList.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Costruisco il JSON di richiesta
                JSONObject payload = new JSONObject();
                payload.put("list_id", listId);
                payload.put("user_id", userId);
                payload.put("product_id", product_id);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                try (Scanner scanner = new Scanner(
                        code == 200 ? conn.getInputStream() : conn.getErrorStream()
                )) {
                    String resp = scanner.useDelimiter("\\A").next();
                    JSONObject root = new JSONObject(resp);

                    if (!root.optBoolean("status", false)) {
                        String msg = root.optString("message", "Errore sconosciuto");
                        mainHandler.post(() ->
                                callback.onError(new Result.Error(new Exception(msg)))
                        );
                        return;
                    }
                    mainHandler.post(() ->
                            callback.onSuccess(new Result.Success<>(true))
                    );
                }
            } catch (Exception e) {
                mainHandler.post(() ->
                        callback.onError(new Result.Error(new IOException("Error fetching products", e)))
                );
            }
        });
    }

        /**
         * Aggiunge un prodotto a una lista chiamando:
         * POST product_in_list/addProductInList.php
         *
         * @param listId   id della lista
         * @param userId   id dell’utente che sta facendo l’operazione
         * @param product  dati del prodotto da inserire (ProductInList)
         * @param callback risultato dell’operazione
         */
        public void addProductToList(int listId, int userId, ProductSelected product, AddProductCallback callback) {
            executor.execute(() -> {
                try {
                    URL url = new URL(Constants.urlAPI + "product_in_list/postProductInList.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);

                    // costruisco il payload JSON
                    JSONObject payload = new JSONObject();
                    payload.put("list_id", listId);
                    payload.put("user_id", userId);

                    JSONObject prodJson = new JSONObject();
                    prodJson.put("id", product.getId());
                    prodJson.put("description", product.getDescription());
                    prodJson.put("option_id", Integer.parseInt(product.getOption()));
                    prodJson.put("quantity", product.getQuantity());
                    prodJson.put("is_urgent", product.isIs_urgent());
                    prodJson.put("is_on_offer", product.isIs_on_offer());
                    prodJson.put("is_worth_it", product.isIs_worth_it());
                    // il server ignora “checked” in ingresso, ma lo includiamo per sicurezza
                    prodJson.put("checked", product.isIs_checked());

                    payload.put("product", prodJson);

                    // invio
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(body);
                        os.flush();
                    }

                    int code = conn.getResponseCode();
                    String resp = new java.util.Scanner(
                            code == 200 ? conn.getInputStream() : conn.getErrorStream()
                    ).useDelimiter("\\A").next();

                    JSONObject root = new JSONObject(resp);
                    if (root.optBoolean("status", false)) {
                        String msg = root.optString("message", "OK");
                        mainHandler.post(() -> callback.onSuccess(msg));
                    } else {
                        String err = root.optString("message", "Errore sconosciuto");
                        mainHandler.post(() -> callback.onError(new Exception(err)));
                    }

                } catch (Exception e) {
                    mainHandler.post(() -> callback.onError(e));
                }
            });
    }
    public interface AddProductCallback {
        void onSuccess(String message);
        void onError(Exception e);
    }


    public interface ProductSelectedCallback {
        void onSuccess(Result.Success<ArrayList<ProductSelected>> result);
        void onError(Result.Error error);
    }

    public interface ProductRemoveSelectedCallback {
        void onSuccess(Result.Success<Boolean> result);
        void onError(Result.Error error);
    }
}
