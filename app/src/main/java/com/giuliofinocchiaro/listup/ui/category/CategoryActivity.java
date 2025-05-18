package com.giuliofinocchiaro.listup.ui.category;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.data.Constants;
import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.Category;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.ui.product.ProductActivity;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    private int id_list;
    private CategoryViewModel categoryViewModel;
    private LinearLayout linearLayoutCategories;
    private ListShop list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        Intent intent = getIntent();
        id_list = intent.getIntExtra("id_list", -1);

        if (id_list == -1) {
            finish();
            return;
        }

        CategoryViewModelFactory categoryViewModelFactory = new CategoryViewModelFactory(getApplication(), id_list);
        categoryViewModel = new ViewModelProvider(this, categoryViewModelFactory).get(CategoryViewModel.class);

        if (categoryViewModel.getList() == null){
            finish();
            return;
        }

        list = categoryViewModel.getList();

        TextView list_name = findViewById(R.id.lv_name_list_category);
        list_name.setText(list.getTitle());

        linearLayoutCategories = findViewById(R.id.ll_category_container);
        categoryViewModel.loadCategories();
        categoryViewModel.getMutableLiveDataCategories().observe(this, this::populateCategories);
    }

    private void populateCategories(ArrayList<Category> categories) {
        if (!list.isCanEdit()){
            findViewById(R.id.ll_category).setVisibility(View.GONE);
            return;
        }
        findViewById(R.id.ll_category).setVisibility(View.VISIBLE);
        linearLayoutCategories.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Category cat : categories) {
            View card = inflater.inflate(R.layout.item_category, linearLayoutCategories, false);
            ImageView iv = card.findViewById(R.id.img_category_icon);
            TextView tv = card.findViewById(R.id.tv_category);

            loadImage(iv, cat.getIcon());
            tv.setText(cat.getName());


            card.setOnClickListener(v -> {
                Intent data = new Intent(this, ProductActivity.class);
                data.putExtra("id_category", cat.getId());
                data.putExtra("id_list", list.getId());
                startActivity(data);
            });

            linearLayoutCategories.addView(card);
        }
    }

    private void loadImage(ImageView iv, String URL){
        String fullURL = Constants.urlCDN + URL + "?t=" + System.currentTimeMillis();

        if (URL == null || URL.isEmpty()){
            iv.setImageResource(R.mipmap.ic_launcher);
            return;
        }
        Picasso.get().load(fullURL).into(iv, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                iv.setImageResource(R.mipmap.ic_launcher);
            }
        });
    }
}