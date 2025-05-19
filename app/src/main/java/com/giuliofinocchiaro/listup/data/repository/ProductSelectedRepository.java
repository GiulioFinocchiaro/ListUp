package com.giuliofinocchiaro.listup.data.repository;

import android.content.Context;
import android.util.Log;

import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.Product;
import com.giuliofinocchiaro.listup.data.model.ProductSelected;
import com.giuliofinocchiaro.listup.data.source.CategoryDataSource;
import com.giuliofinocchiaro.listup.data.source.auth.LoginDataSource;
import com.giuliofinocchiaro.listup.data.source.products.ProductDataSource;
import com.giuliofinocchiaro.listup.data.source.products.ProductSelectedDataSource;

import java.util.ArrayList;

public class ProductSelectedRepository {
    private static ProductSelectedRepository instance;
    private final ProductSelectedDataSource productDataSource;
    private final ProductRepository productRepository;
    private final ListRepository listRepository;
    private final LoginRepository loginRepository;
    private ArrayList<ProductSelected> products = new ArrayList<>();
    private final Context context;

    public ProductSelectedRepository(ProductSelectedDataSource productDataSource, ProductRepository productRepository, ListRepository listRepository, Context context) {
        this.productDataSource = productDataSource;
        this.productRepository = productRepository;
        this.loginRepository = LoginRepository.getInstance(new LoginDataSource(), context);
        this.listRepository = listRepository;
        this.context = context;
    }

    public static synchronized ProductSelectedRepository getInstance(ProductSelectedDataSource productsDataSource, ProductRepository productRepository, ListRepository listRepository, Context context) {
        if (instance == null) {
            instance = new ProductSelectedRepository(productsDataSource, productRepository, listRepository, context);
        }
        return instance;
    }

    public void loadProducts(int list_id, ProductSelectedDataSource.ProductSelectedCallback callback) {
        productDataSource.getProducts(productRepository, listRepository, list_id, loginRepository.getUser().getUserId(), new ProductSelectedDataSource.ProductSelectedCallback() {
            @Override
            public void onSuccess(Result.Success<ArrayList<ProductSelected>> result) {
                Log.d("ProductSelected", result.getData().toString());
                products = result.getData();
                callback.onSuccess(new Result.Success<>(products));
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }

    public ArrayList<ProductSelected> getProducts() {
        return products;
    }

    public void removeProduct(int product_id, int list_id, ProductSelectedDataSource.ProductRemoveSelectedCallback callback) {
        productDataSource.removeProduct(list_id, this.loginRepository.getUser().getUserId(), product_id, new ProductSelectedDataSource.ProductRemoveSelectedCallback() {
            @Override
            public void onSuccess(Result.Success<Boolean> result) {
                callback.onSuccess(new Result.Success<>(true));
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }
}