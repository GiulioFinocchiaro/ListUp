package com.giuliofinocchiaro.listup.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;           // <— import aggiunto
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.data.Constants;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.data.model.Product;
import com.giuliofinocchiaro.listup.data.model.ProductSelected;
import com.giuliofinocchiaro.listup.data.model.User;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {
    private int id_list;
    private int id_category;
    private ProductViewModel productViewModel;
    private LinearLayout ll_containers;
    private ListShop list;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);

        Intent intent = getIntent();
        id_list = intent.getIntExtra("id_list", -1);
        id_category = intent.getIntExtra("id_category", -1);
        if (id_list == -1 || id_category == -1) {
            finish();
            return;
        }

        // init ViewModel
        ProductViewModelFactory factory = new ProductViewModelFactory(getApplication(), id_list, id_category);
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);
        if (productViewModel.getList() == null || productViewModel.getCategory() == null) {
            finish();
            return;
        }

        this.ll_containers = findViewById(R.id.ll_containers_products);

        user = productViewModel.getUser();
        list = productViewModel.getList();

        productViewModel.getMutableLiveDataProducts()
                .observe(this, this::displayProducts);
        productViewModel.loadProduct();
    }

    private void displayProducts(ArrayList<Product> products) {
        ll_containers.removeAllViews();
        LayoutInflater inf = LayoutInflater.from(this);

        for (int i = 0; i < products.size(); i += 3) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rp.setMargins(0, 8, 0, 8);
            row.setLayoutParams(rp);

            for (int j = i; j < i + 3 && j < products.size(); j++) {
                View card = inf.inflate(R.layout.item_product_card, row, false);
                card.setClickable(true);
                card.setFocusable(true);
                card.setSelected(false);

                ImageView iv = card.findViewById(R.id.iv_product_icon);
                TextView tv = card.findViewById(R.id.tv_product_name);
                loadImage(iv, products.get(j).getIcon());
                tv.setText(products.get(j).getName());

                LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                );
                cardLp.setMargins(8, 0, 8, 0);
                card.setLayoutParams(cardLp);

                MaterialCardView mcv = card.findViewById(R.id.card_product);
                int finalJ = j;
                mcv.setOnClickListener(v -> {
                    boolean newChecked = !mcv.isChecked();
                    if (newChecked) {
                        productViewModel.addProductSelected(
                                new ProductSelected(
                                        products.get(finalJ),
                                        0,
                                        list,
                                        user,
                                        "1",
                                        1,
                                        "de",
                                        false,
                                        false,
                                        false,
                                        false
                                )
                        );
                    }
                    mcv.setChecked(newChecked);

                });

                row.addView(card);
            }
            ll_containers.addView(row);
        }
    }

    private void loadImage(ImageView iv, String URL){
        String fullURL = Constants.urlCDN + URL + ".png?t=" + System.currentTimeMillis();

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