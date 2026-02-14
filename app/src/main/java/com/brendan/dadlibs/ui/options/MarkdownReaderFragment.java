package com.brendan.dadlibs.ui.options;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.brendan.dadlibs.R;

import org.commonmark.node.Heading;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;

public class MarkdownReaderFragment extends Fragment {

    private int documentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            documentId = getArguments().getInt("document_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_markdown_reader, container, false);
        TextView textView = view.findViewById(R.id.content);
        String markdown = readRawResource(documentId);

        int color;
        TypedValue tv = new TypedValue();
        if (requireContext().getTheme().resolveAttribute(
                android.R.attr.textColor, tv, true)){
            color = tv.data;
        } else {
            color = Color.BLACK;
        }

        Markwon markwon = Markwon.builder(requireContext())
                .usePlugin(TablePlugin.create(requireContext()))
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin(){
                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.headingBreakHeight(0)
                                .bulletWidth(15);
                    }})
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder.appendFactory(
                                Heading.class,
                                (configuration, props) -> new ForegroundColorSpan(color));
                    }})
                .build();
        markwon.setMarkdown(textView, markdown);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    private String readRawResource(int resId) {
        InputStream is = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            reader.close();
        } catch (Exception e) {
            return "Failed to load document.";
        }

        return sb.toString();
    }
}
