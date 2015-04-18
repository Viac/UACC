package ua.com.glady.uacc.guis;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.INotifyEvent;
import ua.com.glady.uacc.model.calculators.UaccPreferences;
import ua.com.glady.uacc.model.types.VehicleType;
import ua.com.glady.uacc.tools.NumberEdit;
import ua.com.glady.uacc.tools.ToolsView;

/**
 * Implements UI for the backward calculation
 *
 * Created by Slava on 09.04.2015.
 */
public class BackwardCalcUi extends LinearLayout {

    private final Context context;

    private VehicleType vehicleType;

    TextView tvPreferences;
    ImageButton btEditPreferences;
    NumberEdit ed;

    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if ((v == tvPreferences) || (v == btEditPreferences)) {
                PreferencesDialog preferences = new PreferencesDialog(context, vehicleType);
                preferences.setOnSave(new INotifyEvent() {
                    @Override
                    public void onEvent() {
                        updatePreferencesText();
                    }
                });
                preferences.show();
            }
        }
    };

    public BackwardCalcUi(Context context, VehicleType vehicleType) {
        super(context);
        this.context = context;

        LinearLayout llBase = new LinearLayout(context);
        llBase.setOrientation(VERTICAL);
        this.addView(llBase);

        TextView tvInfo = new TextView(context);
        tvInfo.setTextAppearance(context, R.style.flat_list_item);
        tvInfo.setText(R.string.BackwardCalcInfo);
        LinearLayout.LayoutParams tvInfoParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tvInfoParams.setMargins(0, 16, 0, 0);
        llBase.addView(tvInfo, tvInfoParams);

        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 8);

        LinearLayout llPreferences = CustomControlsBuilder.createHorizontalLayout(context);
        llPreferences.setPadding(0, 8, 0, 0);

        tvPreferences = new TextView(context);

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tvParams.weight = 1;
        tvParams.width = 0;
        tvPreferences.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
        tvPreferences.setTextAppearance(context, R.style.flat_list_item);
        tvPreferences.setClickable(true);
        tvPreferences.setOnClickListener(onClick);
        llPreferences.addView(tvPreferences, tvParams);

        btEditPreferences = new ImageButton(context);
        btEditPreferences.setImageResource(android.R.drawable.ic_menu_edit);
        LinearLayout.LayoutParams btParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tvParams.weight = 0.3f;
        btEditPreferences.setClickable(true);
        btEditPreferences.setOnClickListener(onClick);
        llPreferences.addView(btEditPreferences, btParams);

        llBase.addView(llPreferences);

        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 8);

        ed = CustomControlsBuilder.createNumberEdit(
                context,
                context.getString(R.string.BackwardCalcEditPrompt),
                Constants.DEFAULT_PRICE,
                Constants.PRICE_MIN_BOUND,
                Constants.PRICE_MAX_BOUND);
        llBase.addView(ed);

        setVehicleType(vehicleType);
    }

    /**
     * @return UI final price value
     */
    public int getPrice() {
        return ed.getValue();
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
        updatePreferencesText();
    }

    public void updatePreferencesText() {
        String template = getResources().getString(R.string.BackwardCalcPreferencesTemplate);

        UaccPreferences uaccPreferences = new UaccPreferences(context, vehicleType);

        String uiText = String.format(template,
                uaccPreferences.getVehiclePreferences().lowVolume,
                uaccPreferences.getVehiclePreferences().highVolume,
                uaccPreferences.getVehiclePreferences().stepVolume);
        tvPreferences.setText(uiText);
    }
}
