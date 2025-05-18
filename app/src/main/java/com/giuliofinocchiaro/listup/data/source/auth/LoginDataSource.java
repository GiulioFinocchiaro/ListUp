package com.giuliofinocchiaro.listup.data.source.auth;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.giuliofinocchiaro.listup.data.Constants;
import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.User;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginDataSource {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();  // Gestisce il login su un thread separato
    private final Handler mainHandler = new Handler(Looper.getMainLooper());  // Gestisce la risposta sul thread principale

    public void login(String username, String password, LoginCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(Constants.urlAPI + "auth/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                JSONObject json = new JSONObject();
                json.put("username", username);
                json.put("password", password);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                }
                int responseCode = conn.getResponseCode();

                try (Scanner scanner = new Scanner(responseCode == 200 ? conn.getInputStream() : conn.getErrorStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    JSONObject jsonResponse = new JSONObject(response);

                    Log.d("Login", jsonResponse.toString());

                    if (jsonResponse.optBoolean("status", false)) {
                        JSONObject userJson = new JSONObject(jsonResponse.optString("user"));

                        User user = new User(
                                userJson.optInt("id"),
                                userJson.optString("username"),
                                userJson.optString("email"),
                                userJson.optString("name"),
                                userJson.optString("surname")
                        );

                        // Chiamata al callback per il successo
                        mainHandler.post(() -> callback.onSuccess(new Result.Success<>(user)));
                    } else {
                        // Errore nel login
                        String errorMessage = jsonResponse.optString("message", "Errore sconosciuto");
                        // Chiamata al callback per l'errore
                        mainHandler.post(() -> callback.onError(new Result.Error(new Exception(errorMessage))));
                    }
                }
            } catch (Exception e) {
                // Chiamata al callback per l'errore
                mainHandler.post(() -> callback.onError(new Result.Error(new IOException("Error logging in", e))));
            }
        });
    }

    // Metodo di logout
    public void logout() {
        Log.d("Login", "Utente disconnesso");
    }

    // Interfaccia per i callback
    public interface LoginCallback {
        void onSuccess(Result.Success<User> result);  // Callback per il successo
        void onError(Result.Error error);  // Callback per l'errore
    }
}
