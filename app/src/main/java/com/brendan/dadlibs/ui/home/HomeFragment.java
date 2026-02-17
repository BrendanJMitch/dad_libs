package com.brendan.dadlibs.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.data.entity.Template;
import com.brendan.dadlibs.share.ShareCacheHelper;
import com.brendan.dadlibs.share.ShareJson;
import com.brendan.dadlibs.share.ShareMapping;
import com.brendan.dadlibs.share.SharePayload;
import com.brendan.dadlibs.share.TemplateDto;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment {

    private TemplateAdapter templateAdapter;
    private HomeViewModel viewModel;
    private RecyclerView templateRecycler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_home, container, false);
        templateRecycler = fragment.findViewById(R.id.template_recycler);
        fragment.findViewById(R.id.new_template_button).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("template_id", -1);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_homeFragment_to_editorFragment, args);
        });
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        templateAdapter = new TemplateAdapter(viewModel, new TemplateAdapter.TemplateClickListener() {
            @Override
            public void onCardClick(Template template) { launchStory(template); }

            @Override
            public void onMenuClick(View v, Template template) { showPopup(v, template); }
        });
        templateRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        templateRecycler.setAdapter(templateAdapter);

        viewModel.updateTemplates(templates ->
                templateAdapter.setTemplates(templates));

        return fragment;
    }

    private void showPopup(View v, Template template){
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.menu_template, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                editTemplate(template);
                return true;
            }
            if (item.getItemId() == R.id.menu_delete) {
                deleteTemplate(template);
                return true;
            }
            if (item.getItemId() == R.id.menu_copy) {
                copyTemplate(template);
                return true;
            }
            if (item.getItemId() == R.id.menu_share) {
                shareTemplate(template);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void launchStory(Template template){
        Bundle args = new Bundle();
        args.putLong("template_id", template.id);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_homeFragment_to_readerFragment, args);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void editTemplate(Template template){
        Bundle args = new Bundle();
        args.putLong("template_id", template.id);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_homeFragment_to_editorFragment, args);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void copyTemplate(Template template){
        viewModel.copyTemplate(
                template,
                String.format("%s (copy)", template.name),
                templates -> templateAdapter.addTemplates(templates));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteTemplate(Template template) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_delete, null);

        TextView messageView = dialogView.findViewById(R.id.delete_message);

        messageView.setText(String.format(getString(R.string.confirm_delete_word_list) , template.name));
        Context context = requireContext();
        new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton(context.getString(R.string.delete), (dialog, which) -> {
                    viewModel.deleteTemplate(template);
                    templateAdapter.removeTemplate(template);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void shareTemplate(Template template) {
        TemplateDto dto = ShareMapping.getTemplateDto(template);
        SharePayload payload = new SharePayload(List.of(dto), null, null);
        String json = ShareJson.toJson(payload);
        Context context = requireContext();

        File jsonFile;
        try {
            jsonFile = ShareCacheHelper.writeShareFile(context, template.name, json);
        } catch (IOException e) {
            Toast.makeText(context, "Unable to export. Try again?", Toast.LENGTH_LONG).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                jsonFile);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(
                Intent.createChooser(intent, "Share Templates"));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}