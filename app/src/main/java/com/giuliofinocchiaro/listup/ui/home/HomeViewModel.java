package com.giuliofinocchiaro.listup.ui.home;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.data.model.User;
import com.giuliofinocchiaro.listup.data.repository.ListRepository;
import com.giuliofinocchiaro.listup.data.repository.LoginRepository;
import com.giuliofinocchiaro.listup.data.source.auth.LoginDataSource;
import com.giuliofinocchiaro.listup.data.source.lists.ListDataSource;

import java.util.ArrayList;

public class HomeViewModel extends AndroidViewModel {
    private final LoginRepository loginRepository;
    private final ListRepository listRepository;

    private final MutableLiveData<ArrayList<ListShop>> listMineMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<ListShop>> listGuestMutableLiveData = new MutableLiveData<>();

    public HomeViewModel(Application application) {
        super(application);
        this.loginRepository = LoginRepository.getInstance(new LoginDataSource(), application);
        this.listRepository  = ListRepository.getInstance(new ListDataSource(), application);
    }

    /** Esponi i LiveData per l’osservazione da parte della UI */
    public LiveData<ArrayList<ListShop>> getListMine() {
        return listMineMutableLiveData;
    }

    public LiveData<ArrayList<ListShop>> getListGuest() {
        return listGuestMutableLiveData;
    }

    /** Carica le liste di cui sei proprietario */
    public void loadListsForOwner() {
        listRepository.getListsForOwner(new ListDataSource.ListCallback() {
            @Override
            public void onSuccess(Result.Success<ArrayList<ListShop>> result) {
                listMineMutableLiveData.postValue(result.getData());
                Log.d("Home", "Owner lists: " + result.getData());
            }

            @Override
            public void onError(Result.Error error) {
                Log.e("Home", "Error loading owner lists", error.getError());
                Toast.makeText(
                        getApplication(),
                        "Errore caricamento mie liste: " + error.getError().getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    /** Carica le liste condivise con te */
    public void loadListsForGuest() {
        listRepository.getListsForGuest(new ListDataSource.ListCallback() {
            @Override
            public void onSuccess(Result.Success<ArrayList<ListShop>> result) {
                listGuestMutableLiveData.postValue(result.getData());
                Log.d("Home", "Guest lists: " + result.getData());
            }

            @Override
            public void onError(Result.Error error) {
                Log.e("Home", "Error loading guest lists", error.getError());
                Toast.makeText(
                        getApplication(),
                        "Errore caricamento liste condivise: " + error.getError().getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    public User getUserLogged(){
        return loginRepository.getUser();
    }
}
