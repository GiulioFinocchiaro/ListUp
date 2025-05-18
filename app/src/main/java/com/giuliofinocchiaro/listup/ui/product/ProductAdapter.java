package com.giuliofinocchiaro.listup.ui.product;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.data.Constants;
import com.giuliofinocchiaro.listup.data.model.Product;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.Holder> {
    private final ArrayList<Product> items = new ArrayList<>();
    private final Set<Integer> selectedPositions = new HashSet<>();

    @SuppressLint("NotifyDataSetChanged")
    void setProducts(ArrayList<Product> list) {
        items.clear();
        items.addAll(list);
        selectedPositions.clear();
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder h, int pos) {
        Product p = items.get(pos);
        h.tvName.setText(p.getName());
        loadImage(h.ivIcon, p.getIcon());

        // stato selezionato
        h.card.setChecked(selectedPositions.contains(pos));

        h.card.setOnClickListener(v -> {
            boolean was = selectedPositions.contains(pos);
            if (was) selectedPositions.remove(pos);
            else selectedPositions.add(pos);
            h.card.setChecked(!was);
            // qui puoi anche notificare ViewModel o salvare la selezione
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView ivIcon;
        TextView tvName;

        Holder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_product);
            ivIcon = itemView.findViewById(R.id.iv_product_icon);
            tvName = itemView.findViewById(R.id.tv_product_name);
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
