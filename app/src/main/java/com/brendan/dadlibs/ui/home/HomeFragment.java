package com.brendan.dadlibs.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.entity.Template;

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
            args.putLong("template_id", 1);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_homeFragment_to_editorFragment, args);
        });
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        templateAdapter = new TemplateAdapter(template -> {
            Bundle args = new Bundle();
            args.putLong("template_id", template.id);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_homeFragment_to_readerFragment, args);
        }, template -> {});
        templateRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        templateRecycler.setAdapter(templateAdapter);

        viewModel.updateTemplates(new HomeViewModel.DataLoadedCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onLoaded(List<Template> templates) {
                templateAdapter.setTemplates(templates);
                templateAdapter.notifyDataSetChanged();
            }
        });

        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}