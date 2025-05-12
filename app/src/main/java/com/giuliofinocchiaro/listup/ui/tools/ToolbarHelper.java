package com.giuliofinocchiaro.listup.ui.tools;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.giuliofinocchiaro.listup.R;

public class ToolbarHelper {

    public interface ToolbarListener {
        void onBackClicked();
    }

    public static void setupToolbar(Toolbar toolbar, String title, final AppCompatActivity activity) {

        // Find the title TextView
        TextView titleTextView = toolbar.findViewById(R.id.toolbar_titleAc);
        if (titleTextView != null) {
            titleTextView.setText(title);
        }

    }

    public static void setTitle(Toolbar toolbar, String title) {
        TextView titleTextView = toolbar.findViewById(R.id.toolbar_title);
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }
}