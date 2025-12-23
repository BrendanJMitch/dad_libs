package com.brendan.dadlibs.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.entity.Template;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}