package com.brendan.dadlibs.ui.importexport;

import android.app.Application;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.brendan.dadlibs.BuildConfig;
import com.brendan.dadlibs.db.AppDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ImportExportViewModel extends AndroidViewModel {

    private AppDatabase appDb;
    private final Application application;

    private final String DB_FILE = AppDatabase.DB_FILENAME;
    private final String WAL_FILE = DB_FILE + "-wal";
    private final String SHM_FILE = DB_FILE + "-shm";

    public ImportExportViewModel(@NonNull Application application) {
        super(application);
        appDb = AppDatabase.getDatabase(application);
        this.application = application;
    }

    public void exportDatabaseToUri(@NonNull Uri uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                appDb.close();

                File dbDir = application.getDatabasePath(DB_FILE).getParentFile();
                File mainDb = new File(dbDir, DB_FILE);
                File wal = new File(dbDir, WAL_FILE);
                File shm = new File(dbDir, SHM_FILE);

                try (OutputStream os = application.getContentResolver().openOutputStream(uri);
                     ZipOutputStream zos = new ZipOutputStream(os)) {

                    zipFile(zos, mainDb, DB_FILE);

                    if (wal.exists()) zipFile(zos, wal, WAL_FILE);
                    if (shm.exists()) zipFile(zos, shm, SHM_FILE);
                    ZipEntry meta = new ZipEntry("metadata.json");
                    zos.putNextEntry(meta);
                    zos.write(
                            (String.format(
                                    Locale.US,
                                    "{\"schemaVersion\": %d, \"appVersion\": \"%s\"}",
                                    appDb.version(),
                                    BuildConfig.VERSION_NAME)
                            ).getBytes(StandardCharsets.UTF_8));
                    zos.closeEntry();
                }

            } catch (Exception e) {
                Log.e("Backup", "Export failed", e);
            } finally {
                // Re-open Room
                appDb = AppDatabase.getDatabase(application);
            }
        });
    }
    public void importDatabaseFromUri(
            @NonNull Uri uri, Runnable invalidDbHandler, Runnable errorHandler,
            BiConsumer<String, String> futureVersionHandler, Runnable successHandler) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                validateDbImport(uri);
                appDb.close();

                File dbDir = application.getDatabasePath(DB_FILE).getParentFile();
                new File(dbDir, DB_FILE).delete();
                new File(dbDir, WAL_FILE).delete();
                new File(dbDir, SHM_FILE).delete();

                try (InputStream is = application.getContentResolver().openInputStream(uri);
                     ZipInputStream zis = new ZipInputStream(is)) {

                    ZipEntry entry;
                    byte[] buffer = new byte[8192];

                    while ((entry = zis.getNextEntry()) != null) {
                        File outFile = new File(dbDir, entry.getName());
                        try (FileOutputStream fos = new FileOutputStream(outFile)) {
                            int len;
                            while ((len = zis.read(buffer)) != -1)
                                fos.write(buffer, 0, len);
                        }
                        zis.closeEntry();
                    }
                }
                new Handler(Looper.getMainLooper()).post(successHandler);

            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(errorHandler);
            } catch (InvalidBackupException e) {
                new Handler(Looper.getMainLooper()).post(invalidDbHandler);
            } catch (FutureVersionBackupException e) {
                new Handler(Looper.getMainLooper()).post(() -> futureVersionHandler.accept(e.currentVersion, e.backupVersion));
            }
        });
    }


    private void validateDbImport(@NonNull Uri uri) throws InvalidBackupException, FutureVersionBackupException {
        Set<String> allowedEntries = new HashSet<>(Arrays.asList(
                DB_FILE,
                WAL_FILE,
                SHM_FILE,
                "metadata.json"));

        boolean hasDb = false;

        try (InputStream is = application.getContentResolver().openInputStream(uri);
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            JSONObject metadata = null;

            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();

                if (name.equals(DB_FILE))
                    hasDb = true;

                if (!allowedEntries.contains(name))
                    throw new InvalidBackupException("Backup contains unexpected file: " + name);

                if (name.equals("metadata.json")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    String jsonStr = baos.toString("UTF-8");
                    try {
                        metadata = new JSONObject(jsonStr);
                    } catch (JSONException e) {
                        throw new InvalidBackupException("metadata.json is not valid JSON");
                    }
                }
                zis.closeEntry();
            }
            if (!hasDb)
                throw new InvalidBackupException("Backup does not contain the main database file");

            if (metadata != null) {
                int schemaVersion = metadata.optInt("schemaVersion", -1);
                String appVersion = metadata.optString("appVersion", "(unknown)");
                if (schemaVersion == -1) {
                    throw new InvalidBackupException("metadata.json missing schemaVersion");
                }
                if (schemaVersion > appDb.version()) {
                    throw new FutureVersionBackupException(
                            String.format("Imported schema version %d is greater than current schema version %d", schemaVersion, appDb.version()),
                            BuildConfig.VERSION_NAME,
                            appVersion
                    );
                }
            }

        } catch (IOException e) {
            throw new InvalidBackupException("Failed to read backup file", e);
        }
    }

    private void zipFile(ZipOutputStream zos, File file, String name) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, len);
            }
        }
        zos.closeEntry();
    }
}
