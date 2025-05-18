package com.giuliofinocchiaro.listup.data.repository;

import android.content.Context;

import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.Product;
import com.giuliofinocchiaro.listup.data.source.CategoryDataSource;
import com.giuliofinocchiaro.listup.data.source.ProductDataSource;

import java.util.ArrayList;

public class ProductRepository {
    private static ProductRepository instance;
    private final ProductDataSource productDataSource;
    private ArrayList<Product> products = new ArrayList<>();
    private final Context context;

    public ProductRepository(ProductDataSource productDataSource, Context context) {
        this.productDataSource = productDataSource;
        this.context = context;
    }

    public static synchronized ProductRepository getInstance(ProductDataSource productsDataSource, Context context) {
        if (instance == null) {
            instance = new ProductRepository(productsDataSource, context);
        }
        return instance;
    }

    public void loadProducts(ProductDataSource.ProductCallback callback) {
        productDataSource.getProducts(CategoryRepository.getInstance(new CategoryDataSource(), context), new ProductDataSource.ProductCallback() {
            @Override
            public void onSuccess(Result.Success<ArrayList<Product>> result) {
                products = result.getData();
                callback.onSuccess(new Result.Success<>(products));
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }

    public ArrayList<Product> getProductsByCategory(int id){
        ArrayList<Product> productsCate = new ArrayList<>();
        for (Product product : products){
            if (product.getCategory().getId() == id) productsCate.add(product);
        }
        return productsCate;
    }
}