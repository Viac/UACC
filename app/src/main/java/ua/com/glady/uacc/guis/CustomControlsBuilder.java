package ua.com.glady.uacc.guis;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.tools.FlatListPicker;
import ua.com.glady.uacc.tools.NumberEdit;

/**
 * This class contains static wrappers for the custom controls to create them in the same way
 * each time
 * <p/>
 * Created by Slava on 09.04.2015.
 */
public class CustomControlsBuilder {

    /**
     * Creates flat list
     *
     * @param context control context
     * @param items list of the items in the list
     * @param title list title
     * @return flat list
     */
    public static FlatListPicker createFlatListPicker(Context context, String[] items,
                                                      String title) {
        int[] headerMargins = {0, 8, 0, 16};
        int[] margins = {0, 8, 0, 8};

        FlatListPicker.Builder b = new FlatListPicker.Builder(context, items).setHeader(title);
        b.setHeaderAppearance(R.style.flat_list_header);
        b.setItemAppearance(R.style.flat_list_item);
        b.setActiveItemAppearance(R.style.flat_list_active_item);
        b.setOrientation(FlatListPicker.Orientation.Horizontal);
        b.setHeaderMargins(headerMargins);
        b.setMargins(margins);
        return b.build();
    }

    /**
     * Creates number edit
     *
     * @param context control context
     * @param title label on the edit
     * @param defaultValue what will be shown just after create
     * @param minValue min available value for the edit
     * @param maxValue max available value for the edit
     * @return new number edit
     */
    public static NumberEdit createNumberEdit(Context context, String title, int defaultValue, int minValue, int maxValue) {
        NumberEdit ed = new NumberEdit(context, title, defaultValue);
        ed.setTitleAppearance(R.style.flat_list_header);
        ed.setEditAppearance(R.style.flat_list_active_item);
        ed.setOrientation(LinearLayout.VERTICAL);
        ed.setMinMaxLimit(minValue, maxValue);
        ed.setRemoveUnderline(true);
        return ed.getUi();
    }

    /**
     * Creates vertical linear layout, used to save code amount for regular operations
     *
     * @param context control context
     * @return linear layout
     */
    public static LinearLayout createVerticalLayout(Context context) {
        LinearLayout result = new LinearLayout(context);
        result.setOrientation(LinearLayout.VERTICAL);
        result.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return result;
    }

    /**
     * Overloaded method that creates vertical linear layout with margins
     * used to save code amount for regular operations
     *
     * @param context control context
     * @return linear layout
     */
    public static LinearLayout createVerticalLayout(Context context,
                                                    int marginLeft, int marginTop,
                                                    int marginRight, int marginBottom) {

        LinearLayout result = createVerticalLayout(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        result.setLayoutParams(layoutParams);

        return result;
    }

    /**
     * Creates horizontal linear layout, used to save code amount for regular operations
     *
     * @param context control context
     * @return linear layout
     */
    public static LinearLayout createHorizontalLayout(Context context) {
        LinearLayout result = new LinearLayout(context);
        result.setOrientation(LinearLayout.HORIZONTAL);
        result.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return result;
    }

    /**
     * Overloaded method that creates horizontal linear layout with margins
     * used to save code amount for regular operations
     *
     * @param context control context
     * @return linear layout
     */
    public static LinearLayout createHorizontalLayout(Context context,
                                                    int marginLeft, int marginTop,
                                                    int marginRight, int marginBottom) {

        LinearLayout result = createHorizontalLayout(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        result.setLayoutParams(layoutParams);

        return result;
    }

}