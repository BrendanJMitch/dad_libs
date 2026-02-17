package com.brendan.dadlibs.ui.home;

import android.annotation.SuppressLint;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.data.entity.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {

    public static class TemplateViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView preview;
        ImageView menuButton;

        public TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.template_title);
            preview = itemView.findViewById(R.id.template_preview);
            menuButton = itemView.findViewById(R.id.template_menu);
            preview.post(() -> {
                LinearGradient shader = new LinearGradient(
                        0, 0, 0, preview.getHeight(),
                        new int[]{
                                0xFF000000,   // fully opaque at top
                                0x00000000    // fully transparent at bottom
                        },
                        null,
                        Shader.TileMode.CLAMP
                );
                preview.getPaint().setShader(shader);
                preview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            });
        }
    }

    public interface TemplateClickListener {
        void onCardClick(Template template);
        void onMenuClick(View v, Template template);
    }

    private List<Template> templates;
    private final TemplateClickListener templateClickListener;
    private final HomeViewModel viewModel;

    public TemplateAdapter(HomeViewModel viewModel, TemplateClickListener templateClickListener) {
        this.viewModel = viewModel;
        this.templateClickListener = templateClickListener;
        this.templates = new ArrayList<>();
    }


    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_card, parent, false);
        return new TemplateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int index) {
        Template template = templates.get(index);

        holder.title.setText(template.name);
        holder.preview.setText(getPreview(template.text, holder.preview.getCurrentTextColor()));

        holder.itemView.setOnClickListener(v -> templateClickListener.onCardClick(template));
        holder.menuButton.setOnClickListener(v -> templateClickListener.onMenuClick(v, template));
    }

    @Override
    public int getItemCount() {
        if (templates != null)
            return templates.size();
        else
            return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTemplates(List<Template> templates){
        this.templates = templates;
        notifyDataSetChanged();
    }

    public void addTemplates(List<Template> templates){
        this.templates.addAll(templates);
        notifyItemRangeInserted(this.templates.size() - templates.size(), templates.size());
    }

    public void removeTemplate(Template template){
        for (int i = 0; i < templates.size(); i++) {
            if (Objects.equals(templates.get(i).id, template.id)) {
                templates.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    private CharSequence getPreview(String template, int color){
        List<Pair<Integer, Integer>> blanks = viewModel.getBlankIndices(template);
        SpannableStringBuilder builder = new SpannableStringBuilder(template);
        for (Pair<Integer, Integer> blank : blanks) {
            builder.setSpan(
                    new BlankSpan(color),
                    blank.first, blank.first + blank.second,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

}
