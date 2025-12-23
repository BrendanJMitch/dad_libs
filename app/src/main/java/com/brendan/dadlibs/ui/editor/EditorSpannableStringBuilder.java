package com.brendan.dadlibs.ui.editor;

import com.brendan.dadlibs.ui.spannable.ModifiedSpannableStringBuilder;

/**
 * Custom mutable {@link android.text.Spanned} which uses hacks to improve
 * performance when many spans are applied and is used as the backing data of
 * an {@link android.widget.EditText}
 *
 * @author rashad
 */
public class EditorSpannableStringBuilder extends ModifiedSpannableStringBuilder {

    /**
     * Constructor
     */
    public EditorSpannableStringBuilder(CharSequence text) {
        super(text);
    }

    @Override
    protected void sendSpanChanged(Object what, int oldStart, int oldEnd, int start, int end) {
        /*
            Hack to prevent unnecessary updates
            sendSpanChanged is called when the offset of a span within the
            Spanned changes.
            When there are many spans in the Spanned, and text is inserted,
            all the spans after the insertion point will have their offsets
            updated. This causes this method to be called excessively.
            If this class is used for the backing data of an EditText, one of
            the classes that will be notified when super is called will trigger
            a reflow of the text that the span covered. Another listener will
            cause invalidation of the display lists for the View. Neither of
            these operations are costly when called appropriately, but it adds
            up when they are called too many times.
            Because of how spans are added / modified in the writer, we don't
            need this behaviour when the following spans are changed. So we
            simply don't call through to super to avoid doing unnecessary work.
            However, for all other spans we want to continue to call through so
            that the original behaviour is preserved. This is necessary for
            spans which change the layout of the text for example. If the text
            needs to be reflowed due to a span change, the listeners should be
            notified of that.
        */
        if (what instanceof PlaceholderSpan){
            return;
        }

        super.sendSpanChanged(what, oldStart, oldEnd, start, end);
    }
}
