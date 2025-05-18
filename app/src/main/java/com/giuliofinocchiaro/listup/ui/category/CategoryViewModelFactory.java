package com.giuliofinocchiaro.listup.ui.category;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CategoryViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int idList;

    public CategoryViewModelFactory(Application application, int idList) {
        this.application = application;
        this.idList = idList;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
            //noinspection unchecked
            return (T) new CategoryViewModel(application, idList);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
