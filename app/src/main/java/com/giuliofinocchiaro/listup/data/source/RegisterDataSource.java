package com.giuliofinocchiaro.listup.data.source;

import android.os.Handler;
import android.os.Looper;

import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.Constants;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class RegisterDataSource {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    public void register(String username, String password, String email, String name, String surname, RegisterCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(Constants.urlAPI + "auth/register.php");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);
                jsonObject.put("password", password);
                jsonObject.put("email", email);
                jsonObject.put("name", name);
                jsonObject.put("surname", surname);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                try (Scanner scanner = new Scanner(responseCode == 200 ? conn.getInputStream() : conn.getErrorStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.optBoolean("success", false)) {
                        mainHandler.post(() -> callback.onSuccess(new Result.Success<>(true)));
                    } else {
                        mainHandler.post(() -> callback.onError(new Result.Error(new IOException(jsonResponse.optString("message", "Unknown Error")))));
                    }
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(new Result.Error(new IOException("Error registering", e))));
            }
        });
    }

    public interface RegisterCallback {
        void onSuccess(Result.Success<Boolean> result);
        void onError(Result.Error error);
    }
}
