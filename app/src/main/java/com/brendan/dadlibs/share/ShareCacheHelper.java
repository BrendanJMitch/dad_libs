package com.brendan.dadlibs.share;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ShareCacheHelper {

    public static File writeShareFile(Context context, String filename, String json) throws IOException {
        if (!filename.toLowerCase().endsWith(".json"))
            filename = filename + ".json";
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, filename);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        }
        return file;
    }

}
