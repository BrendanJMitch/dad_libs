package com.brendan.dadlibs.share;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.brendan.dadlibs.data.db.AppDatabase;
import com.brendan.dadlibs.data.entity.SavedStory;
import com.brendan.dadlibs.data.entity.Template;
import com.brendan.dadlibs.data.relation.WordListWithWords;
import com.brendan.dadlibs.repository.WordListRepository;
import com.squareup.moshi.JsonDataException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ImportHelper {

    public static SharePayload getPayloadFromUri(Context context, Uri uri) throws IOException, JsonDataException{
        try (InputStream inputStream =
                     context.getContentResolver().openInputStream(uri);
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            String json = builder.toString();
            return ShareJson.fromJson(json);
        }
    }

    public static void performImport(Context context, SharePayload payload) {
        AppDatabase.executor.execute(() -> {

            AppDatabase db = AppDatabase.getDatabase(context);

            db.runInTransaction(() -> {

                if (payload.wordLists != null) {
                    for (WordListDto dto : payload.wordLists) {
                        WordListWithWords list = ShareMapping.getWordListWithWords(dto);
                        new WordListRepository(db.wordListDao(), db.wordDao(), db.inflectionDao()).insertWordListWithWords(list);
                    }
                }

                if (payload.templates != null) {
                    for (TemplateDto dto : payload.templates) {
                        Template entity = ShareMapping.getTemplateEntity(dto);
                        db.templateDao().insert(entity);
                    }
                }

                if (payload.savedStories != null) {
                    for (SavedStoryDto dto : payload.savedStories) {
                        SavedStory entity = ShareMapping.getSavedStoryEntity(dto);
                        db.savedStoryDao().insert(entity);
                    }
                }
            });

            ((AppCompatActivity) context).runOnUiThread(() ->
                    Toast.makeText(context,
                            "Import successful",
                            Toast.LENGTH_LONG).show()
            );
        });
    }
}
