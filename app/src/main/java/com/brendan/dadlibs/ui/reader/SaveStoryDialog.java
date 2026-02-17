package com.brendan.dadlibs.ui.reader;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.data.entity.SavedStory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.Consumer;

public class SaveStoryDialog {

    private final Context context;
    private Consumer<SavedStory> onSaveCallback;

    public SaveStoryDialog(Context context){
        this.context = context;
    }

    public void show(SavedStory story, Consumer<SavedStory> onSaveCallback){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_save_story, null);

        TextInputLayout titleLayout = dialogView.findViewById(R.id.story_title_layout);
        TextInputEditText titleInput = dialogView.findViewById(R.id.story_title_input);

        titleInput.setText(story.name);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Save Story")
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                Editable title = titleInput.getText();
                boolean shouldDismiss = true;
                if (title == null || title.toString().trim().isEmpty()) {
                    titleLayout.setErrorEnabled(true);
                    titleLayout.setError("Please save your story with a title!");
                    shouldDismiss = false;
                } else {
                    titleLayout.setErrorEnabled(false);
                }
                if (shouldDismiss) {
                    onSaveCallback.accept(new SavedStory(
                            title.toString(), story.templateId, story.text,
                            System.currentTimeMillis() / 1000L, 5.0f));
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }
}

