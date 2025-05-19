package com.giuliofinocchiaro.listup.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.data.model.ListShop;
import com.giuliofinocchiaro.listup.ui.category.CategoryActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

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
        Button btn = findViewById(R.id.btn_aggiungi_lista);
        btn.setOnClickListener(v -> showAddDialog());
    }

    @SuppressLint("SetTextI18n")
    private void displayLists(LinearLayout ll, ArrayList<ListShop> lists){
        ll.removeAllViews();
        ll.setVisibility((lists == null || lists.isEmpty()) ? View.GONE : View.VISIBLE);

        if (lists != null) {
            LayoutInflater inflater = LayoutInflater.from(this);

            for (ListShop list : lists) {
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

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_product, null);

        TextInputEditText etTitle = dialogView.findViewById(R.id.et_nome_lista);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Aggiungi Lista")
                .setView(dialogView)
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("OK", (dialog, which) -> {
                    String title = etTitle.getText() != null
                            ? etTitle.getText().toString().trim()
                            : "";

                    if (!title.isEmpty()) {
                        homeViewModel.addListWithTitle(title);
                        homeViewModel.loadListsForGuest();
                        homeViewModel.loadListsForOwner();
                    }
                    dialog.dismiss();
                })
                .show();
    }
}