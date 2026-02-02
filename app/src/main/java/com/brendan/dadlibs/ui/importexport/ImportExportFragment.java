package com.brendan.dadlibs.ui.importexport;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.brendan.dadlibs.R;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImportExportFragment extends PreferenceFragmentCompat {


    private ActivityResultLauncher<String> exportDbLauncher;
    private ActivityResultLauncher<String[]> importDbLauncher;

    private ImportExportViewModel viewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ImportExportViewModel.class);
        exportDbLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/zip"),
                uri -> {
                    if (uri != null)
                        viewModel.exportDatabaseToUri(uri);
                });
        importDbLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        viewModel.importDatabaseFromUri(
                                uri, this::handleInvalidDb, this::handleError, this::handleFutureVersion, this::handleSuccessfulImport);
                    }
                }
        );
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.import_export_preferences, rootKey);

        Preference importPref = findPreference("import_database");
        if (importPref != null) {
            importPref.setOnPreferenceClickListener(pref -> {
                promptImportDatabase();
                return true;
            });
        }

        Preference exportPref = findPreference("export_database");
        if (exportPref != null) {
            exportPref.setOnPreferenceClickListener(pref -> {
                String dateString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                        .format(new Date());
                exportDbLauncher.launch(String.format("dadlibs_%s.zip", dateString));
                return true;
            });
        }
    }

    private void promptImportDatabase(){
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Data Loss Warning")
                .setMessage("You are about to discard all your templates, word lists, and " +
                        "saved stories, and replace them with imported ones. You will NOT be able " +
                        "to get them back!\n" +
                        "Are you sure you want to proceed?")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.proceed_import_database, null)
                .create();
        dialog.setOnShowListener(
                dlg -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                        v -> importDbLauncher.launch(new String[] {"application/zip"})));

        dialog.show();
    }

    private void handleInvalidDb(){
        Toast.makeText(requireContext(), "Invalid database. Did you choose the correct file?", Toast.LENGTH_LONG).show();
    }

    private void handleError(){
        Toast.makeText(requireContext(), "An error occurred importing the database. Please try again.", Toast.LENGTH_LONG).show();
    }

    private void handleFutureVersion(String currentVersion, String importVersion){

    }

    private void handleSuccessfulImport(){
        ProcessPhoenix.triggerRebirth(requireActivity().getApplicationContext());
    }

}
