package com.brendan.dadlibs;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.brendan.dadlibs.ui.init.InitActivity;
import com.brendan.dadlibs.ui.main.MainActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(
                this,
                isInitialized() ? MainActivity.class : InitActivity.class
        );

        startActivity(intent);
        finish();
    }

    private boolean isInitialized() {
        return getSharedPreferences("app", MODE_PRIVATE)
                .getBoolean("initialized", false);
    }
}
