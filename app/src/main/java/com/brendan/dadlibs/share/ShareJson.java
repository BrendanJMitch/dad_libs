package com.brendan.dadlibs.share;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class ShareJson {

    private static final Moshi moshi = new Moshi.Builder().build();
    private static final JsonAdapter<SharePayload> adapter = moshi.adapter(SharePayload.class);

    private ShareJson() {}

    public static String toJson(SharePayload payload) {
        return adapter.toJson(payload);
    }

    public static SharePayload fromJson(String json) throws IOException {
        return adapter.fromJson(json);
    }
}
