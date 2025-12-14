package com.brendan.dadlibs.ui.home;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brendan.dadlibs.R;
import com.brendan.dadlibs.engine.DadLibEngine;
import com.brendan.dadlibs.entity.Template;

import java.util.ArrayList;
import java.util.List;

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

    public interface OnTemplateClickListener {
        void onClick(Template template);
    }

    private List<Template> templates;
    private final OnTemplateClickListener templateClickListener;
    private final OnTemplateClickListener menuClickListener;

    public TemplateAdapter(OnTemplateClickListener templateClickListener, OnTemplateClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
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
        holder.preview.setText(DadLibEngine.getPreview(template));

        holder.itemView.setOnClickListener(v -> templateClickListener.onClick(template));
        holder.menuButton.setOnClickListener(v -> menuClickListener.onClick(template));
    }

    @Override
    public int getItemCount() {
        if (templates != null)
            return templates.size();
        else
            return 0;
    }

    public void setTemplates(List<Template> templates){
        this.templates = templates;
    }

}
