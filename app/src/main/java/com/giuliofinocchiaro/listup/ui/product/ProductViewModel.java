package com.giuliofinocchiaro.listup.ui.product;

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
import com.giuliofinocchiaro.listup.data.source.products.ProductDataSource;
import com.giuliofinocchiaro.listup.data.source.lists.ListDataSource;
import com.giuliofinocchiaro.listup.data.source.products.ProductSelectedDataSource;

import java.util.ArrayList;

public class ProductViewModel extends AndroidViewModel {
    private ListRepository listRepository;
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private ListShop list;
    private Category category;
    private MutableLiveData<ArrayList<Product>> mutableLiveDataProducts = new MutableLiveData<>();
    private Context context;

    public ProductViewModel(@NonNull Application application, int id_list, int id_category) {
        super(application);
        this.context = application;
        this.listRepository = ListRepository.getInstance(new ListDataSource(), application);
        list = listRepository.getListById(id_list);
        this.categoryRepository = CategoryRepository.getInstance(new CategoryDataSource(), application);
        category = categoryRepository.getCategoryById(id_category);
        this.productRepository = ProductRepository.getInstance(new ProductDataSource(), application);
    }

    public ListShop getList() {
        return list;
    }

    public Category getCategory() {
        return category;
    }

    public MutableLiveData<ArrayList<Product>> getMutableLiveDataProducts() {
        return mutableLiveDataProducts;
    }

    public void loadProduct(){
        productRepository.loadProducts(new ProductDataSource.ProductCallback() {
            @Override
            public void onSuccess(Result.Success<ArrayList<Product>> result) {
                mutableLiveDataProducts.postValue(productRepository.getProductsByCategory(category.getId()));
                Log.d("Product", "Product: " + result.getData());
            }

            @Override
            public void onError(Result.Error error) {
                Log.e("Product", "Error loading Product", error.getError());
                Toast.makeText(
                        getApplication(),
                        "Errore caricamento Product: " + error.getError().getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    public void addProductSelected(ProductSelected product){
        new ProductSelectedDataSource().addProductToList(list.getId(), LoginRepository.getInstance(new LoginDataSource(), context).getUser().getUserId(), product, new ProductSelectedDataSource.AddProductCallback() {
            @Override
            public void onSuccess(String message) {

            }

            @Override
            public void onError(Exception e) {
                // mostra toast o log
            }
        });
    }

    public User getUser() {
        return LoginRepository.getInstance(new LoginDataSource(), context).getUser();
    }
}
