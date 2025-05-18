package com.giuliofinocchiaro.listup.ui.product;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.data.model.Product;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {
    private int id_list;
    private ProductViewModel productViewModel;
    private int id_category;
    private RecyclerView rvProducts;
    private ProductAdapter adapter;


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

        ProductViewModelFactory productViewModelFactory = new ProductViewModelFactory(getApplication(), id_list, id_category);
        productViewModel = new ViewModelProvider(this, productViewModelFactory).get(ProductViewModel.class);

        if (productViewModel.getList() == null || productViewModel.getCategory() == null) {
            finish();
            return;
        }

        rvProducts = findViewById(R.id.rv_products);
        GridLayoutManager glm = new GridLayoutManager(this, 3);
        rvProducts.setLayoutManager(glm);
        adapter = new ProductAdapter();
        rvProducts.setAdapter(adapter);

        // 2) osserva i prodotti
        productViewModel.getMutableLiveDataProducts().observe(this, this::displayProducts);
        productViewModel.loadProduct();
    }

    public void displayProducts(ArrayList<Product> products) {
        adapter.setProducts(products);
    }
}