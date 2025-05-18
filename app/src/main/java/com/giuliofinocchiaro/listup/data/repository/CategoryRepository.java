package com.giuliofinocchiaro.listup.data.repository;

import android.content.Context;

import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.Category;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.data.source.CategoryDataSource;
import com.giuliofinocchiaro.listup.data.source.lists.ListDataSource;

import java.util.ArrayList;

public class CategoryRepository {
    private static CategoryRepository instance;
    private final CategoryDataSource categoryDataSource;
    private ArrayList<Category> categories = new ArrayList<>();
    private final Context context;

    public CategoryRepository(CategoryDataSource categoryDataSource, Context context) {
        this.categoryDataSource = categoryDataSource;
        this.context = context;
    }

    public static synchronized CategoryRepository getInstance(CategoryDataSource categoryDataSource, Context context) {
        if (instance == null) {
            instance = new CategoryRepository(categoryDataSource, context);
        }
        return instance;
    }

    public void loadCategories(CategoryDataSource.CategoryCallback callback) {
        categoryDataSource.getCategories(new CategoryDataSource.CategoryCallback() {
            @Override
            public void onSuccess(Result.Success<ArrayList<Category>> result) {
                categories = result.getData();
                callback.onSuccess(new Result.Success<>(categories));
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }

    public Category getCategoryById(int id){
        for (Category category : categories){
            if (category.getId() == id) return category;
        }

        return null;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }
}
