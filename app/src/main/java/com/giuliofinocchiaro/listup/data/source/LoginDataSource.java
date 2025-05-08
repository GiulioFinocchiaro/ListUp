package com.giuliofinocchiaro.listup.data.source;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.giuliofinocchiaro.mygiancarlobarmenuapplicationvera.ui.Constants;
import com.giuliofinocchiaro.mygiancarlobarmenuapplicationvera.data.Result;
import com.giuliofinocchiaro.mygiancarlobarmenuapplicationvera.data.model.LoggedInUser;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class LoginDataSource {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();  // Gestisce il login su un thread separato
    private final Handler mainHandler = new Handler(Looper.getMainLooper());  // Gestisce la risposta sul thread principale

    public void login(String username, String password, LoginCallback callback) {
        executor.execute(() -> {
            try {
                // Connessione al server per il login
                URL url = new URL(Constants.urlAPI + "auth/login.php");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Creazione della richiesta JSON
                JSONObject json = new JSONObject();
                json.put("username", username);
                json.put("password", password);

                // Invio della richiesta JSON
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                }

                // Ottenimento del codice di risposta
                int responseCode = conn.getResponseCode();

                // Lettura della risposta
                try (Scanner scanner = new Scanner(responseCode == 200 ? conn.getInputStream() : conn.getErrorStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.optBoolean("success", false)) {
                        // Login riuscito
                        int userId = jsonResponse.optInt("userId", -1);
                        String displayName = jsonResponse.optString("displayName", "Unknown");

                        LoggedInUser user = new LoggedInUser(userId, displayName);

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
        void onSuccess(Result.Success<LoggedInUser> result);  // Callback per il successo
        void onError(Result.Error error);  // Callback per l'errore
    }
}
