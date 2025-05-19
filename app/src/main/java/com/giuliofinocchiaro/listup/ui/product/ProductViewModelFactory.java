package com.giuliofinocchiaro.listup.ui.product;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProductViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int idList;
    private int idCategory;

    public ProductViewModelFactory(Application application, int idList, int idCategory) {
        this.application = application;
        this.idList = idList;
        this.idCategory = idCategory;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductViewModel.class)) {
            //noinspection unchecked
            return (T) new ProductViewModel(application, idList, idCategory);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
