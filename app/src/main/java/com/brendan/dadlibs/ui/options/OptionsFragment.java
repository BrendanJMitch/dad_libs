package com.brendan.dadlibs.ui.options;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.brendan.dadlibs.BuildConfig;
import com.brendan.dadlibs.R;
import com.brendan.dadlibs.share.ImportHelper;
import com.brendan.dadlibs.share.SharePayload;
import com.brendan.dadlibs.ui.sharing.ImportDialog;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.squareup.moshi.JsonDataException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OptionsFragment extends PreferenceFragmentCompat {

    private ActivityResultLauncher<String> exportDbLauncher;
    private ActivityResultLauncher<String[]> importDbLauncher;
    private ActivityResultLauncher<String[]> importItemsLauncher;
    private ImportExportViewModel viewModel;
    private static final String SOURCE_LOCATION = "https://github.com/BrendanJMitch/dad_libs";
    private static final String CREATOR_PHONE = "15095544135";


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
                });
        importItemsLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        importItems(uri);
                    }
                });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.options, rootKey);

        Preference importDbPref = findPreference("import_database");
        if (importDbPref != null) {
            importDbPref.setOnPreferenceClickListener(pref -> {
                promptImportDatabase();
                return true;
            });
        }

        Preference exportDbPref = findPreference("export_database");
        if (exportDbPref != null) {
            exportDbPref.setOnPreferenceClickListener(pref -> {
                String dateString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                        .format(new Date());
                exportDbLauncher.launch(String.format("dadlibs_%s.zip", dateString));
                return true;
            });
        }

        Preference importItemPref = findPreference("import_item");
        if (importItemPref != null) {
            importItemPref.setOnPreferenceClickListener(pref -> {
                importItemsLauncher.launch(new String[] {"application/octet-stream", "application/vnd.dadlibs.item+json"});
                return true;
            });
        }

        Preference versionPref = findPreference("app_version");
        if (versionPref != null) {
            versionPref.setSummary(String.format("%s\n%s", BuildConfig.VERSION_NAME, BuildConfig.COPYRIGHT));
        }

        Preference changelogPref = findPreference("changelog");
        if (changelogPref != null) {
            changelogPref.setOnPreferenceClickListener(pref -> {
                Bundle args = new Bundle();
                args.putString("document_name", "Changelog");
                args.putInt("document_id", R.raw.changelog);
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_optionsFragment_to_markdownReaderFragment, args);
                return true;
            });
        }

        Preference licensePref = findPreference("licenses");
        if (licensePref != null) {
            licensePref.setOnPreferenceClickListener(pref -> {
                Bundle args = new Bundle();
                args.putString("document_name", "License");
                args.putInt("document_id", R.raw.license);
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_optionsFragment_to_markdownReaderFragment, args);
                return true;
            });
        }

        Preference sourcePref = findPreference("source_code");
        if (sourcePref != null) {
            sourcePref.setOnPreferenceClickListener(pref -> {
                Uri uri = Uri.parse(SOURCE_LOCATION);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            });
        }

        Preference bugReportPref = findPreference("bug_report");
        if (bugReportPref !=  null) {
            bugReportPref.setOnPreferenceClickListener(pref -> {
                Uri uri = Uri.parse(String.format("smsto:+%s", CREATOR_PHONE));
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "Hey Brendan, fix your dang code.");
                startActivity(intent);
                return true;
            });
        }
    }

    private void importItems(Uri uri){
        Context context = requireContext();
        if (uri != null) {
            try {
                SharePayload payload = ImportHelper.getPayloadFromUri(context, uri);
                new ImportDialog(context, payload).show(() -> ImportHelper.performImport(context, payload));
            } catch (IOException e) {
                Toast.makeText(context, getString(R.string.failed_import_text), Toast.LENGTH_LONG).show();
            } catch (JsonDataException e) {
                Toast.makeText(context, getString(R.string.invalid_data_text), Toast.LENGTH_LONG).show();
            }
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
                        v -> {
                            importDbLauncher.launch(new String[] {"application/zip"});
                            dialog.dismiss();
                        }));

        dialog.show();
    }

    private void handleInvalidDb(){
        Toast.makeText(requireContext(), "Invalid database. Did you choose the correct file?", Toast.LENGTH_LONG).show();
    }

    private void handleError(){
        Toast.makeText(requireContext(), "An error occurred importing the database. Please try again.", Toast.LENGTH_LONG).show();
    }

    private void handleFutureVersion(String currentVersion, String importVersion){
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Future Version Detected")
                .setMessage(
                        String.format("This database file was created with DadLibs version %s. " +
                                        "You currently have version %s. Upgrade to DadLibs %s to import this file.",
                                importVersion, currentVersion, importVersion))
                .setPositiveButton(android.R.string.ok, null)
                .create();
        dialog.show();
    }

    private void handleSuccessfulImport(){
        ProcessPhoenix.triggerRebirth(requireActivity().getApplicationContext());
    }
}
