package com.brendan.dadlibs.ui.sharing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.share.SavedStoryDto;
import com.brendan.dadlibs.share.SharePayload;
import com.brendan.dadlibs.share.TemplateDto;
import com.brendan.dadlibs.share.WordListDto;

public class ImportDialog {

    private final Context context;
    private final SharePayload payload;

    public ImportDialog(Context context, SharePayload payload) {
        this.payload = payload;
        this.context = context;
    }

    public void show(Runnable importFunction) {
        getDialog(importFunction).show();
    }

    private AlertDialog getDialog(Runnable importFunction){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_confirm_import, null);

        LinearLayout wordListsSection = view.findViewById(R.id.word_lists);
        LinearLayout templatesSection = view.findViewById(R.id.templates);
        LinearLayout savedStoriesSection = view.findViewById(R.id.saved_stories);
        int totalItems = 0;

        if (payload.wordLists != null && !payload.wordLists.isEmpty()) {
            totalItems += payload.wordLists.size();
            for (WordListDto wl : payload.wordLists) {
                TextView tv = new TextView(context);
                tv.setText(String.format(context.getString(R.string.bullet_text),  wl.name));
                tv.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1);
                tv.setPadding(0, 4, 0, 4);
                wordListsSection.addView(tv);
            }
        } else {
            wordListsSection.setVisibility(View.GONE);
        }

        if (payload.templates != null && !payload.templates.isEmpty()) {
            totalItems += payload.templates.size();
            for (TemplateDto t : payload.templates) {
                TextView tv = new TextView(context);
                tv.setText(String.format(context.getString(R.string.bullet_text), t.name));
                tv.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1);
                tv.setPadding(0, 4, 0, 4);
                templatesSection.addView(tv);
            }
        } else {
            templatesSection.setVisibility(View.GONE);
        }

        if (payload.savedStories != null && !payload.savedStories.isEmpty()) {
            totalItems += payload.savedStories.size();
            for (SavedStoryDto s : payload.savedStories) {
                TextView tv = new TextView(context);
                tv.setText(String.format(context.getString(R.string.bullet_text), s.name));
                tv.setTextAppearance(
                        com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1);
                tv.setPadding(0, 4, 0, 4);
                savedStoriesSection.addView(tv);
            }
        } else {
            savedStoriesSection.setVisibility(View.GONE);
        }

        return new AlertDialog.Builder(context)
                .setTitle("Import Data")
                .setMessage(String.format(context.getString(R.string.import_prompt),
                        totalItems,
                        totalItems > 1 ? "s" : ""))
                .setView(view)
                .setPositiveButton("Import", (dialog, which) -> importFunction.run())
                .setNegativeButton("Cancel", null)
                .create();
    }
}
