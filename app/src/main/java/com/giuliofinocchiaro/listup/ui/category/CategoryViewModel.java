package com.giuliofinocchiaro.listup.ui.category;

import android.app.Application;
import android.content.Context;
import android.media.MediaRouter;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.Category;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.data.model.Product;
import com.giuliofinocchiaro.listup.data.model.ProductSelected;
import com.giuliofinocchiaro.listup.data.model.User;
import com.giuliofinocchiaro.listup.data.repository.CategoryRepository;
import com.giuliofinocchiaro.listup.data.repository.ListRepository;
import com.giuliofinocchiaro.listup.data.repository.LoginRepository;
import com.giuliofinocchiaro.listup.data.repository.ProductRepository;
import com.giuliofinocchiaro.listup.data.repository.ProductSelectedRepository;
import com.giuliofinocchiaro.listup.data.source.CategoryDataSource;
import com.giuliofinocchiaro.listup.data.source.auth.LoginDataSource;
import com.giuliofinocchiaro.listup.data.source.lists.ListDataSource;
import com.giuliofinocchiaro.listup.data.source.products.ProductDataSource;
import com.giuliofinocchiaro.listup.data.source.products.ProductSelectedDataSource;

import java.util.ArrayList;

public class CategoryViewModel extends AndroidViewModel{
    private ListRepository listRepository;
    private CategoryRepository categoryRepository;
    private ListShop list;
    private MutableLiveData<ArrayList<Category>> mutableLiveDataCategories = new MutableLiveData<>();
    private ProductSelectedRepository productSelectedRepository;
    private MutableLiveData<ArrayList<ProductSelected>> mutableLiveDataProductsSelected = new MutableLiveData<>();
    private ProductRepository productRepository;
    private Context context;

    public CategoryViewModel(@NonNull Application application, int id_list) {
        super(application);
        this.context = application;
        this.listRepository = ListRepository.getInstance(new ListDataSource(), application);
        list = listRepository.getListById(id_list);
        this.categoryRepository = CategoryRepository.getInstance(new CategoryDataSource(), application);
        this.productSelectedRepository = ProductSelectedRepository.getInstance(new ProductSelectedDataSource(), ProductRepository.getInstance(new ProductDataSource(), application), listRepository, application);
        this.productRepository = ProductRepository.getInstance(new ProductDataSource(), application);
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

    public void loadProductsSelected(){
        productRepository.loadProducts(new ProductDataSource.ProductCallback() {
            @Override
            public void onSuccess(Result.Success<ArrayList<Product>> result) {
                productSelectedRepository.loadProducts(list.getId(), new ProductSelectedDataSource.ProductSelectedCallback() {
                    @Override
                    public void onSuccess(Result.Success<ArrayList<ProductSelected>> result) {
                        mutableLiveDataProductsSelected.postValue(result.getData());
                        Log.d("ProductSelected", result.getData().toString());
                    }

                    @Override
                    public void onError(Result.Error error) {
                        Log.e("ProductSelected", "Error loading ProductSelected", error.getError());
                        Toast.makeText(
                                getApplication(),
                                "Errore caricamento ProductSelected: " + error.getError().getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }

            @Override
            public void onError(Result.Error error) {

            }
        });
    }

    public void removePorduct(ProductSelected productSelected){
        productSelectedRepository.removeProduct(productSelected.getId_selected(), list.getId(), new ProductSelectedDataSource.ProductRemoveSelectedCallback() {
            @Override
            public void onSuccess(Result.Success<Boolean> result) {
                loadProductsSelected();
            }

            @Override
            public void onError(Result.Error error) {
                Log.e("ProductSelected", "Error loading ProductSelected", error.getError());
                Toast.makeText(
                        getApplication(),
                        "Errore caricamento ProductSelected: " + error.getError().getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    public MutableLiveData<ArrayList<ProductSelected>> getMutableLiveDataProductsSelected() {
        return mutableLiveDataProductsSelected;
    }

    public MutableLiveData<ArrayList<Category>> getMutableLiveDataCategories() {
        return mutableLiveDataCategories;
    }

    public User getUser() {
        return LoginRepository.getInstance(new LoginDataSource(), context).getUser();
    }
}
