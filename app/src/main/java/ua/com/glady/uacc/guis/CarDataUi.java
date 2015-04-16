package ua.com.glady.uacc.guis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.types.VehicleType;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.model.vehicle.Car;
import ua.com.glady.uacc.tools.ToolsView;

/**
 * Implements car details UI
 *
 * Created by Slava on 09.04.2015.
 */
public class CarDataUi extends VehicleDataUi {

    private boolean isSnowGolf;
    private boolean isCaravan;

    private CheckBox cbIsSpecialDesign;

    OnClickListener onSpecialDesignClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            final String caravanCaption = context.getString(R.string.SpecialPurposeLine1) + ": " +
                    Constants.sLineBreak + context.getString(R.string.SpecialPurposeCaravan);

            final String snowGolfCaption = context.getString(R.string.SpecialPurposeLine1) + ": " +
                    Constants.sLineBreak + context.getString(R.string.SpecialPurposeSnowGolf);


            if (isSnowGolf || isCaravan){
                resetSpecialDesignCombo();
            }
            else {
                CharSequence colors[] = new CharSequence[] {
                        context.getString(R.string.SpecialPurposeCaravan),
                        context.getString(R.string.SpecialPurposeSnowGolf),
                        context.getString(R.string.Clear),
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setCancelable(false);

                builder.setTitle(context.getString(R.string.SpecialPurposePrompt));
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                isCaravan = true;
                                cbIsSpecialDesign.setTextAppearance(context, R.style.flat_list_active_item);
                                cbIsSpecialDesign.setText(caravanCaption);
                                break;
                            case 1:
                                isSnowGolf = true;
                                cbIsSpecialDesign.setTextAppearance(context, R.style.flat_list_active_item);
                                cbIsSpecialDesign.setText(snowGolfCaption);
                                break;
                            default:
                                resetSpecialDesignCombo();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }
    };

    private void resetSpecialDesignCombo(){
        final String defaultCaption = context.getString(R.string.SpecialPurposeLine1) +
                Constants.sLineBreak + context.getString(R.string.SpecialPurposeLine2);
        isSnowGolf = false;
        isCaravan = false;
        cbIsSpecialDesign.setChecked(false);
        cbIsSpecialDesign.setTextAppearance(context, R.style.flat_list_item);
        cbIsSpecialDesign.setText(defaultCaption);
    }

    public CarDataUi(Context context, ExcisesRegistry excisesRegistry) {

        super(context, excisesRegistry);

        this.addEngineList();
        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 0);

        this.addAgeList();
        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 8);

        this.addVolumeAndPrice();
        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 0, 0, 0);

        cbIsSpecialDesign = new CheckBox(context);
        cbIsSpecialDesign.setScaleX(0.75f);
        cbIsSpecialDesign.setScaleY(0.75f);

        resetSpecialDesignCombo();
        cbIsSpecialDesign.setOnClickListener(onSpecialDesignClick);

        llBase.addView(cbIsSpecialDesign);
    }

    @Override
    protected void initializeConcreteVehicle() {
        vehicleType = VehicleType.Car;

        this.availableEngines = new int[4];
        availableEngines[0] = Constants.ENG_GASOLINE;
        availableEngines[1] = Constants.ENG_DIESEL;
        availableEngines[2] = Constants.ENG_ELECTRIC;
        availableEngines[3] = Constants.ENG_OTHER;

        this.availableAges = new int[3];
        availableAges[0] = Age.AGE_0_YEARS;
        availableAges[1] = Age.AGE_NOT_EXCEED_5_YEARS;
        availableAges[2] = Age.AGE_EXCEED_5_YEARS;
    }

    @Override
    public AVehicle getVehicle() {
        Car result = new Car(context, excisesRegistry);

        if (isCaravan)
            result.setCaravan(true);

        if (isSnowGolf)
            result.setSpecialDesign(true);

        result.getEngine().setType(getEngineType());
        result.getEngine().setVolume(this.getVolume());

        result.setAge(this.getAge());

        // defining price
        result.setBasicPrice(this.getPrice());

        return result;
    }

}
