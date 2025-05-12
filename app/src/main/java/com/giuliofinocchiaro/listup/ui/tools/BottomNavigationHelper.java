package com.giuliofinocchiaro.listup.ui.tools;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.ui.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHelper {

    public static void setupBottomNavigation(final Activity activity, BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                /*if (id == R.id.navigation_home && !(activity instanceof HomeActivity)) {
                    activity.startActivity(new Intent(activity, HomeActivity.class));
                    activity.overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.navigation_orders && !(activity instanceof OrdersActivity)) {
                    activity.startActivity(new Intent(activity, OrdersActivity.class));
                    activity.overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.navigation_settings && !(activity instanceof SettingsActivity)) {
                    activity.startActivity(new Intent(activity, SettingsActivity.class));
                    activity.overridePendingTransition(0, 0);
                    return true;
                }*/

                return false;
            }
        });
    }
}
