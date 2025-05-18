package com.giuliofinocchiaro.listup.ui.category;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.Category;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.data.repository.CategoryRepository;
import com.giuliofinocchiaro.listup.data.repository.ListRepository;
import com.giuliofinocchiaro.listup.data.source.CategoryDataSource;
import com.giuliofinocchiaro.listup.data.source.lists.ListDataSource;

import java.util.ArrayList;

public class CategoryViewModel extends AndroidViewModel{
    private ListRepository listRepository;
    private CategoryRepository categoryRepository;
    private ListShop list;
    private MutableLiveData<ArrayList<Category>> mutableLiveDataCategories = new MutableLiveData<>();

    public CategoryViewModel(@NonNull Application application, int id_list) {
        super(application);
        this.listRepository = ListRepository.getInstance(new ListDataSource(), application);
        list = listRepository.getListById(id_list);
        this.categoryRepository = CategoryRepository.getInstance(new CategoryDataSource(), application);
    }

    public ListShop getList() {
        return list;
    }

    public void loadCategories() {
        categoryRepository.loadCategories(new CategoryDataSource.CategoryCallback() {
            @Override
            public void onSuccess(Result.Success<ArrayList<Category>> result) {
                mutableLiveDataCategories.postValue(result.getData());
                Log.d("Category", "CAtegories: " + result.getData());
            }

            @Override
            public void onError(Result.Error error) {
                Log.e("Category", "Error loading categories", error.getError());
                Toast.makeText(
                        getApplication(),
                        "Errore caricamento Categorie: " + error.getError().getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    public MutableLiveData<ArrayList<Category>> getMutableLiveDataCategories() {
        return mutableLiveDataCategories;
    }
}
