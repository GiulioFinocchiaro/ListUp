package com.giuliofinocchiaro.listup.data.source.lists;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.giuliofinocchiaro.listup.data.Constants;
import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.data.model.User;
import com.giuliofinocchiaro.listup.data.source.auth.UserDataSource;

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

public class ListDataSource {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void getListsForUserOwner(User userLogged, ListCallback callback) {
        executor.execute(() -> {
            try {
                Log.d("List", "Dentro");
                URL url = new URL(Constants.urlAPI + "lists/getListsForUserOwner.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Costruisco il JSON
                JSONObject json = new JSONObject();
                json.put("idUser", userLogged.getUserId());

                // Invio i dati
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                }

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

                        ArrayList<ListShop> listShops = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject list = jsonArray.optJSONObject(i);
                            if (list == null) continue;

                            listShops.add(new ListShop(
                                    list.optInt("id"),
                                    userLogged,
                                    list.optString("title"),
                                    list.optString("code"),
                                    list.optInt("canEdit") == 1  // int → boolean
                            ));
                        }

                        Log.d("List", "Owner: " + listShops);

                        mainHandler.post(() ->
                                callback.onSuccess(new Result.Success<>(listShops))
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

    public void getListForUserGuest(User userLogged, ListCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(Constants.urlAPI + "lists/getListsForUserGuest.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Costruisco il JSON
                JSONObject json = new JSONObject();
                json.put("idUser", userLogged.getUserId());

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                try (Scanner scanner = new Scanner(
                        responseCode == 200 ? conn.getInputStream() : conn.getErrorStream()
                )) {
                    String response = scanner.useDelimiter("\\A").next();
                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.optBoolean("status", false)) {
                        JSONArray jsonArray = jsonResponse.getJSONArray("message");
                        ArrayList<ListShop> listShops = new ArrayList<>();

                        // Se non ci sono liste, restituisci subito lista vuota
                        if (jsonArray.length() == 0) {
                            mainHandler.post(() ->
                                    callback.onSuccess(new Result.Success<>(listShops))
                            );
                            return;
                        }

                        // Contatore per callback una sola volta
                        final int total = jsonArray.length();
                        final int[] processed = {0};

                        for (int i = 0; i < total; i++) {
                            JSONObject list = jsonArray.getJSONObject(i);
                            int ownerId = list.optInt("owner");

                            new UserDataSource().getUser(ownerId, new UserDataSource.UserCallback() {
                                @Override
                                public void onSuccess(Result.Success<User> result) {
                                    User owner = result.getData();
                                    ListShop listShop = new ListShop(
                                            list.optInt("id"),
                                            owner,
                                            list.optString("title"),
                                            list.optString("code"),
                                            list.optInt("canEdit") == 1
                                    );
                                    listShops.add(listShop);
                                    checkDone();
                                }

                                @Override
                                public void onError(Result.Error error) {
                                    // fallback
                                    ListShop listShop = new ListShop(
                                            list.optInt("id"),
                                            userLogged,
                                            list.optString("title"),
                                            list.optString("code"),
                                            list.optInt("canEdit") == 1
                                    );
                                    listShops.add(listShop);
                                    checkDone();
                                }

                                private void checkDone() {
                                    processed[0]++;
                                    if (processed[0] == total) {
                                        Log.d("List", "Guest: " + listShops);
                                        mainHandler.post(() ->
                                                callback.onSuccess(new Result.Success<>(listShops))
                                        );
                                    }
                                }
                            });
                        }
                    } else {
                        String errorMessage = jsonResponse.optString("message", "Errore sconosciuto");
                        mainHandler.post(() ->
                                callback.onError(new Result.Error(new Exception(errorMessage)))
                        );
                    }
                }
            } catch (Exception e) {
                mainHandler.post(() ->
                        callback.onError(new Result.Error(new IOException("Error get guest lists", e)))
                );
            }
        });
    }

    public interface ListCallback{
        void onSuccess(Result.Success<ArrayList<ListShop>> result);
        void onError(Result.Error error);
    }
}
