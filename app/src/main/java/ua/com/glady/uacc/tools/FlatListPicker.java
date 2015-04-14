package ua.com.glady.uacc.tools;


import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a control that provides selection from the list
 *
 * Created by Slava on 09.04.2015.
 */
public class FlatListPicker extends LinearLayout implements View.OnClickListener {

    public interface OnChange{
        public void doOnChange(FlatListPicker source);
    }

    public enum Orientation { Horizontal, Vertical };

    private List<TextView> items;

    private final Context context;
    private final String header;
    private final String[] source;
    private Orientation orientation;

    private int headerAppearance;
    private int itemAppearance;
    private int activeItemAppearance;

    private int[] headerMargins;
    private int[] itemMargins;
    private int[] margins;

    private OnChange onChange;

    private int index = -1;

    private LinearLayout baseLayout;
    private LinearLayout headerLayout;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        invalidateItems();
    }

    private void invalidateItems() {
        for (int i = 0; i < items.size(); i++){
            if (i == index)
                highlightItem(items.get(i));
            else
                lowlightItem(items.get(i));
        }
    }

    private void highlightItem(TextView tv) {
        if (this.activeItemAppearance != -1)
            tv.setTextAppearance(context, activeItemAppearance);
        else
            // default highlight
            tv.setTypeface(null, Typeface.BOLD);
    }

    private void lowlightItem(TextView tv) {
        if (this.itemAppearance != -1)
            tv.setTextAppearance(context, itemAppearance);
        else
            // default highlight
            tv.setTypeface(null, Typeface.NORMAL);
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < items.size(); i++){
            if (v == items.get(i)){
                setIndex(i);
                if (onChange != null)
                    onChange.doOnChange(this);
                return;
            }
        }

        if ((v == this)  || (v == baseLayout) || (v == headerLayout)){
            int i = this.getIndex();
            if (i < (items.size() - 1))
                i++;
            else
                i = 0;
            setIndex(i);
        }
    }

    private FlatListPicker(Builder builder) {
        super(builder.context);

        this.context = builder.context;
        this.header = builder.header;
        this.source = builder.source;

        createUsingBuilder(builder);

        items = new ArrayList<>();

        index = -1;

        baseLayout = new LinearLayout(context);
        baseLayout.setOrientation(LinearLayout.VERTICAL);
        baseLayout.setOnClickListener(this);
        LayoutParams baseLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        if ((margins != null) && (margins.length == 4))
            baseLayoutParams.setMargins(margins[0], margins[1], margins[2], margins[3]);
        baseLayout.setLayoutParams(baseLayoutParams);

        this.addView(baseLayout);

        if (!header.isEmpty()){
            createHeader(baseLayout);
        }

        if (source.length > 0) {
            createItems(baseLayout);
        }
    }

    private void createItems(LinearLayout baseLayout) {
        LinearLayout itemsLayout = new LinearLayout(context);
        itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        LayoutParams tv_lp;
        if (orientation == Orientation.Vertical) {
            itemsLayout.setOrientation(VERTICAL);
            tv_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        else {
            itemsLayout.setOrientation(HORIZONTAL);
            tv_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
        }

        for (String s: source){
            TextView tvItem = new TextView(context);
            if ((itemMargins != null) && (itemMargins.length == 4))
                tv_lp.setMargins(itemMargins[0], itemMargins[1], itemMargins[2], itemMargins[3]);
            tvItem.setLayoutParams(tv_lp);
            tvItem.setText(s);
            tvItem.setOnClickListener(this);
            items.add(tvItem);
            itemsLayout.addView(tvItem);
        }

        baseLayout.addView(itemsLayout);
        setIndex(0);
    }

    private void createHeader(LinearLayout baseLayout) {
        headerLayout = new LinearLayout(context);
        headerLayout.setOrientation(VERTICAL);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if ((headerMargins != null) && (headerMargins.length == 4))
            lp.setMargins(headerMargins[0], headerMargins[1], headerMargins[2], headerMargins[3]);

        headerLayout.setLayoutParams(lp);

        TextView tvHeader = new TextView(context);
        tvHeader.setText(header);
        if (headerAppearance != -1)
            tvHeader.setTextAppearance(context, headerAppearance);
        headerLayout.addView(tvHeader);

        baseLayout.setOnClickListener(this);
        baseLayout.addView(headerLayout);
    }

    private void createUsingBuilder(Builder builder) {

        this.orientation = builder.orientation;

        this.headerAppearance = builder.headerAppearance;
        this.itemAppearance = builder.itemAppearance;
        this.activeItemAppearance = builder.activeItemAppearance;

        this.headerMargins = builder.headerMargins;
        this.itemMargins = builder.itemMargins;
        this.margins = builder.margins;

        this.onChange = builder.onChange;
    }

    public static class Builder {

        // Required parameters
        private final Context context;
        private final String[] source;

        // Optional parameters
        private String header = "";
        private Orientation orientation = Orientation.Vertical;

        private int headerAppearance = -1;
        private int itemAppearance = -1;
        private int activeItemAppearance = -1;

        private int[] headerMargins;
        private int[] itemMargins;
        private int[] margins;

        private OnChange onChange;

        public Builder(Context context, String[] source) {
            this.context = context;
            this.source = source;
            this.headerMargins = null;
            this.itemMargins = null;
            this.onChange = null;
        }

        public Builder setHeader(String header) {
            this.header = header;
            return this;
        }

        public Builder setOrientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setHeaderAppearance(int headerAppearance) {
            this.headerAppearance = headerAppearance;
            return this;
        }

        public Builder setActiveItemAppearance(int activeItemAppearance) {
            this.activeItemAppearance = activeItemAppearance;
            return this;
        }

        public Builder setItemAppearance(int itemAppearance) {
            this.itemAppearance = itemAppearance;
            return this;
        }

        public Builder setHeaderMargins(int[] headerMargins) {
            this.headerMargins = headerMargins;
            return this;
        }

        public Builder setItemMargins(int[] itemMargins) {
            this.itemMargins = itemMargins;
            return this;
        }

        public Builder setMargins(int[] margins) {
            this.margins = margins;
            return this;
        }

        public Builder setOnChange(OnChange onChange) {
            this.onChange = onChange;
            return this;
        }


        public FlatListPicker build() {
            return new FlatListPicker(this);
        }
    }

}
