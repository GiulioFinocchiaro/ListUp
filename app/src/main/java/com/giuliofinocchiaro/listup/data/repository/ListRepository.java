package com.giuliofinocchiaro.listup.data.repository;

import android.content.Context;
import android.util.Log;

import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.data.source.auth.LoginDataSource;
import com.giuliofinocchiaro.listup.data.source.lists.ListDataSource;

import java.util.ArrayList;

public class ListRepository {
    private static ListRepository instance;
    private final ListDataSource listDataSource;
    private final LoginRepository loginRepository;
    private ArrayList<ListShop> listShopsOwner = new ArrayList<>();
    private ArrayList<ListShop> listShopsGuest = new ArrayList<>();
    private final Context context;

    public ListRepository(ListDataSource listDataSource, Context context) {
        this.listDataSource = listDataSource;
        this.loginRepository = LoginRepository.getInstance(new LoginDataSource(), context);
        this.context = context;
    }

    public static synchronized ListRepository getInstance(ListDataSource productsDataSource, Context context) {
        if (instance == null) {
            instance = new ListRepository(productsDataSource, context);
        }
        return instance;
    }

    public void getListsForOwner(ListDataSource.ListCallback callback) {
        listDataSource.getListsForUserOwner(this.loginRepository.getUser(), new ListDataSource.ListCallback() {
            @Override
            public void onSuccess(Result.Success<ArrayList<ListShop>> result) {
                listShopsOwner = result.getData();
                callback.onSuccess(new Result.Success<>(listShopsOwner));
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }

    public void getListsForGuest(ListDataSource.ListCallback callback){
        listDataSource.getListForUserGuest(this.loginRepository.getUser(), new ListDataSource.ListCallback() {
            @Override
            public void onSuccess(Result.Success<ArrayList<ListShop>> result) {
                listShopsGuest = result.getData();
                callback.onSuccess(new Result.Success<>(listShopsGuest));
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }

    public ListShop getListById(int id){
        for (ListShop listShop : this.listShopsOwner){
            if (listShop.getId() == id) return listShop;
        }
        for (ListShop listShop : this.listShopsGuest){
            if (listShop.getId() == id) return listShop;
        }
        return null;
    }

    public void addList(String title, ListDataSource.ListAddCallback callback){
        listDataSource.getAddList(title, LoginRepository.getInstance(new LoginDataSource(), context).getUser().getUserId(), new ListDataSource.ListAddCallback() {
            @Override
            public void onSuccess(Result.Success<Boolean> result) {

            }

            @Override
            public void onError(Result.Error error) {

            }
        });
    }
}
