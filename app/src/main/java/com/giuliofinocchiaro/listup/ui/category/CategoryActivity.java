package com.giuliofinocchiaro.listup.ui.category;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.data.Constants;
import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.Category;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.data.model.Product;
import com.giuliofinocchiaro.listup.data.model.ProductSelected;
import com.giuliofinocchiaro.listup.ui.product.ProductActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    private int id_list;
    private CategoryViewModel categoryViewModel;
    private LinearLayout linearLayoutCategories;
    private LinearLayout ll_containers;
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
        ll_containers = findViewById(R.id.ll_productsSelected_container);

        TextView list_name = findViewById(R.id.lv_name_list_category);
        list_name.setText(list.getTitle());

        ImageView btn = findViewById(R.id.btn_list_settings);
        if (list.getOwner().getUserId() == categoryViewModel.getUser().getUserId()){
            btn.setOnClickListener(v -> {
                showCodeDialog(list.getCode());
            });
        } else btn.setVisibility(View.GONE);

        linearLayoutCategories = findViewById(R.id.ll_category_container);
        categoryViewModel.getMutableLiveDataCategories().observe(this, this::populateCategories);
        categoryViewModel.loadCategories();
        categoryViewModel.getMutableLiveDataProductsSelected().observe(this, this::displayProducts);
        categoryViewModel.loadProductsSelected();
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
                Intent i = new Intent(this, ProductActivity.class);
                i.putExtra("id_list", list.getId());
                i.putExtra("id_category", cat.getId());
                startActivity(i);
            });

            linearLayoutCategories.addView(card);
        }
    }

    private void displayProducts(ArrayList<ProductSelected> products) {
        if (products.isEmpty()) findViewById(R.id.ll_products_in_lista).setVisibility(View.GONE);
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
                mcv.setCheckable(true);
                mcv.setChecked(true);
                if (list.isCanEdit()){
                    int finalJ = j;
                    mcv.setOnClickListener(v -> {
                        boolean newChecked = !mcv.isChecked();
                        if (!newChecked) {
                            categoryViewModel.removePorduct(products.get(finalJ));
                        }
                        mcv.setChecked(newChecked);

                    });
                }

                row.addView(card);
            }
            ll_containers.addView(row);
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

    @SuppressLint("SetTextI18n")
    private void showCodeDialog(String codeText) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_show_code, null);

        TextView tvCode   = dialogView.findViewById(R.id.tv_code);
        Button btnCopy    = dialogView.findViewById(R.id.btn_copy);
        Button btnClose   = dialogView.findViewById(R.id.btn_close);

        tvCode.setText(Constants.urlAPI+"lists/sharedList.php?code="+codeText);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Codice sorgente")
                .setView(dialogView);

        AlertDialog dialog = builder.show();

        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("codice", tvCode.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Codice copiato negli appunti", Toast.LENGTH_SHORT).show();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    protected void onResume() {
        super.onResume();
        categoryViewModel.loadProductsSelected();
    }
}