package com.giuliofinocchiaro.listup.data.repository;

import android.content.SharedPreferences;
import android.content.Context;

import com.giuliofinocchiaro.listup.data.Constants;
import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.LoggedInUser;
import com.giuliofinocchiaro.listup.data.source.LoginDataSource;

/**
 * Classe che gestisce l'autenticazione dell'utente e il salvataggio dei dati di sessione.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private final LoginDataSource dataSource;
    private LoggedInUser user = null;
    private final SharedPreferences sharedPreferences;

    /**
     * Costruttore privato per garantire il singleton.
     */
    private LoginRepository(LoginDataSource dataSource, Context context) {
        this.dataSource = dataSource;
        this.sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME_LOGIN, Context.MODE_PRIVATE);
        loadUserFromPreferences();
    }

    /**
     * Restituisce un'istanza singleton di LoginRepository.
     */
    public static synchronized LoginRepository getInstance(LoginDataSource dataSource, Context context) {
        if (instance == null) {
            instance = new LoginRepository(dataSource, context);
        }
        return instance;
    }

    public LoggedInUser getUser() {
        return user;
    }

    /**
     * Verifica se l'utente è attualmente loggato.
     */
    public boolean isLoggedIn() {
        return user != null;
    }

    /**
     * Effettua il logout dell'utente, cancellando i dati di sessione.
     */
    public void logout() {
        user = null;
        dataSource.logout();
    }

    /**
     * Effettua il login dell'utente in modo asincrono.
     */
    public void login(String username, String password, LoginCallback callback) {
        dataSource.login(username, password, new LoginDataSource.LoginCallback() {
            @Override
            public void onSuccess(Result.Success<LoggedInUser> result) {
                user = result.getData();
                saveUserToPreferences(user);
                callback.onSuccess(result);
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }



    /**
     * Salva l'utente nelle SharedPreferences.
     */
    private void saveUserToPreferences(LoggedInUser user) {
        sharedPreferences.edit()
                .putInt(Constants.KEY_USER_ID, user.getUserId())
                .putString(Constants.KEY_USER_NAME, user.getDisplayName())
                .apply();
    }

    /**
     * Carica i dati dell'utente loggato dalle SharedPreferences.
     */
    private void loadUserFromPreferences() {
        int userId = sharedPreferences.getInt(Constants.KEY_USER_ID, 0);
        String userName = sharedPreferences.getString(Constants.KEY_USER_NAME, null);
        if (userId != 0 && userName != null) {
            user = new LoggedInUser(userId, userName);
        }
    }

    /**
     * Interfaccia per la gestione del risultato del login.
     */
    public interface LoginCallback {
        void onSuccess(Result.Success<LoggedInUser> result);
        void onError(Result.Error error);
    }
}
