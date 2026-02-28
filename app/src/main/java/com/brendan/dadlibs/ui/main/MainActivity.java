package com.brendan.dadlibs.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.databinding.ActivityMainBinding;
import com.brendan.dadlibs.share.ImportHelper;
import com.brendan.dadlibs.share.SharePayload;
import com.brendan.dadlibs.ui.sharing.ImportDialog;
import com.google.android.material.navigation.NavigationView;
import com.squareup.moshi.JsonDataException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_word_lists, R.id.nav_saved_stories, R.id.nav_options)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        handleIncomingIntent(getIntent());
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                String mimeType = this.getContentResolver().getType(uri);
                Log.d("MIME", "Type = " + mimeType);
                try {
                    SharePayload payload = ImportHelper.getPayloadFromUri(this, uri);
                    showImportPromptDialog(payload);
                } catch (IOException e) {
                    Toast.makeText(this, getString(R.string.failed_import_text), Toast.LENGTH_LONG).show();
                } catch (JsonDataException e) {
                    Toast.makeText(this, getString(R.string.invalid_data_text), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void showImportPromptDialog(SharePayload payload) {
        new ImportDialog(this, payload).show(() ->
                ImportHelper.performImport(this, payload, this::refresh));
    }

    private void refresh(){
        Intent intent = getIntent();
        intent.setAction(null);
        intent.setData(null);
        recreate();
    }
}
