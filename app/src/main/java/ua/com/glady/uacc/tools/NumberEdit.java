package ua.com.glady.uacc.tools;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Implements control "number edit with title"
 *
 * Created by Slava on 09.04.2015.
 */
public class NumberEdit extends LinearLayout {

    private final Context context;

    private TextView ed;
    private TextView tvTitle;
    private LinearLayout llBase;

    private String title;
    private int defaultValue;

    public void setRemoveUnderline(boolean removeUnderline) {
        this.removeUnderline = removeUnderline;
    }

    private boolean removeUnderline;

    public NumberEdit(Context context, String title, int defaultValue) {
        super(context);

        this.context = context;
        this.title = title;
        this.defaultValue = defaultValue;

        llBase = new LinearLayout(context);
        tvTitle = new TextView(context);
        ed = new EditText(context);

        removeUnderline = false;
    }

    public NumberEdit getUi(){
        llBase.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        llBase.setOrientation(this.getOrientation());
        this.addView(llBase);

        if (!title.isEmpty()){
            tvTitle.setText(title);
            llBase.addView(tvTitle);
        }

        ed.setInputType(InputType.TYPE_CLASS_NUMBER);
        ed.setText(String.valueOf(defaultValue));

        if (removeUnderline){
            int sdk = android.os.Build.VERSION.SDK_INT;

            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                // Of course, it's deprecated... this is why do we have this check above
                //noinspection deprecation
                ed.setBackgroundDrawable(null);
            } else {
                ed.setBackground(null);
            }
        }

        llBase.addView(ed);

        return this;
    }

    private boolean isNumber(String value){
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getValue() {
        String value = ed.getText().toString();
        if (isNumber(value)) {
            return Integer.valueOf(value);
        }
        else {
            return 0; // as default value
        }
    }

    public void setValue(int value) {
        ed.setText(String.valueOf(value));
    }

    public void setTitleAppearance(int titleAppearance) {
        tvTitle.setTextAppearance(context, titleAppearance);
    }

    public void setEditAppearance(int editAppearance) {
        ed.setTextAppearance(context, editAppearance);
    }

    /**
     * Sets up limits of value for the
     * @param min min value that control can have
     * @param max max value that control can have
     */
    public void setMinMaxLimit(int min, int max){
        ed.setFilters(new InputFilter[]{ new InputFilterMinMax(min, max)});
    }

}