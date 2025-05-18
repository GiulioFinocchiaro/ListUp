package com.giuliofinocchiaro.listup.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.ui.category.CategoryActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private HomeViewModel homeViewModel;
    private LinearLayout ll_mylists;
    private LinearLayout ll_guestLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ll_mylists = findViewById(R.id.ll_myLists_home);
        ll_guestLists = findViewById(R.id.ll_guestList_home);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getListMine().observe(this, lists -> {
            displayLists(ll_mylists, lists);
        });
        homeViewModel.loadListsForOwner();

        homeViewModel.getListGuest().observe(this, lists -> {
            displayLists(ll_guestLists, lists);
        });
        homeViewModel.loadListsForGuest();
    }

    @SuppressLint("SetTextI18n")
    private void displayLists(LinearLayout ll, ArrayList<ListShop> lists){
        ll.removeAllViews();
        // se vuoto, non mostrarmi il contenitore
        ll.setVisibility((lists == null || lists.isEmpty()) ? View.GONE : View.VISIBLE);

        if (lists != null) {
            LayoutInflater inflater = LayoutInflater.from(this);

            for (ListShop list : lists) {
                // infliamo la nostra card
                View itemView = inflater.inflate(R.layout.item_shopping_list, ll, false);

                TextView tvName = itemView.findViewById(R.id.tv_list);
                TextView tvOwner = itemView.findViewById(R.id.list_tv_proprietario);

                tvName.setText(list.getTitle());
                String proprietario = (list.getOwner().getUserId() == homeViewModel.getUserLogged().getUserId()) ? "Tu": list.getOwner().getUsername();
                tvOwner.setText("Proprietario: "+ proprietario);
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, CategoryActivity.class);
                    intent.putExtra("id_list", list.getId());
                    startActivity(intent);
                });
                ll.addView(itemView);
            }
        }
    }
}