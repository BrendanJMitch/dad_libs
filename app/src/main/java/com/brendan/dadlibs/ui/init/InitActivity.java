package com.brendan.dadlibs.ui.init;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.db.AppDatabase;
import com.brendan.dadlibs.ui.main.MainActivity;

public class InitActivity extends AppCompatActivity {

    SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener initializedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();
        setContentView(R.layout.activity_init);

        transitionWhenInitComplete();
    }

    private void transitionWhenInitComplete() {
        preferences = getSharedPreferences("app", MODE_PRIVATE);
        if (preferences.getBoolean("initialized", false)) {
            launchMain();
            return;
        }
        initializedListener = (sharedPrefs, key) -> {
            if ("initialized".equals(key)
                    && sharedPrefs.getBoolean(key, false)) {
                launchMain();
            }
        };

        preferences.registerOnSharedPreferenceChangeListener(initializedListener);
        // Trigger database creation
        AppDatabase.getDatabase(this).getOpenHelper().getReadableDatabase();
    }

    private void launchMain() {
        preferences.unregisterOnSharedPreferenceChangeListener(initializedListener);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (initializedListener != null) {
            preferences.unregisterOnSharedPreferenceChangeListener(initializedListener);
        }
    }

    private void setFullscreen() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Modern immersive mode (API 30+)
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            WindowInsetsControllerCompat insetsController =
                    WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            insetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            insetsController.hide(WindowInsetsCompat.Type.systemBars());
        } else {
            // Legacy immersive mode (API 19+)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

}

