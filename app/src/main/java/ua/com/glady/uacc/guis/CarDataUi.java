package ua.com.glady.uacc.guis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.types.RussianMfParams;
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
    private RussianMfParams russianMfParams;

    private CheckBox cbIsSpecialDesign;

    private CheckBox cbMadeInRussia;

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
                CharSequence options[] = new CharSequence[] {
                        context.getString(R.string.SpecialPurposeCaravan),
                        context.getString(R.string.SpecialPurposeSnowGolf),
                        context.getString(R.string.Clear),
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setCancelable(false);

                builder.setTitle(context.getString(R.string.SpecialPurposePrompt));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                isCaravan = true;
                                cbIsSpecialDesign.setTextAppearance(context, R.style.combo_item_active);
                                cbIsSpecialDesign.setText(caravanCaption);
                                break;
                            case 1:
                                isSnowGolf = true;
                                cbIsSpecialDesign.setTextAppearance(context, R.style.combo_item_active);
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


    OnClickListener onMadeInRussiaClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (russianMfParams != RussianMfParams.rus_None) {
                resetMadeInRussiaCombo();
                return;
            }

            CharSequence options[] = new CharSequence[]{
                    context.getString(R.string.MadeInRussiaAutoVAZ),
                    context.getString(R.string.MadeInRussiaSollers),
                    context.getString(R.string.MadeInRussiaOther),
                    context.getString(R.string.MadeInRussiaUndefined),
                    context.getString(R.string.MadeInRussiaCancel)
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setCancelable(false);

            builder.setTitle(context.getString(R.string.MadeInRussiaPrompt));
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String caption;

                    switch (which) {
                        case 0:
                            russianMfParams = RussianMfParams.rus_AutoVAZ;
                            cbMadeInRussia.setTextAppearance(context, R.style.combo_item_active);
                            caption = context.getString(R.string.MadeInRussia) + ": " + context.getString(R.string.MadeInRussiaAutoVAZ);
                            cbMadeInRussia.setText(caption);
                            break;

                        case 1:
                            russianMfParams = RussianMfParams.rus_Sollers;
                            cbMadeInRussia.setTextAppearance(context, R.style.combo_item_active);
                            caption = context.getString(R.string.MadeInRussia) + ": " + context.getString(R.string.MadeInRussiaSollers);
                            cbMadeInRussia.setText(caption);
                            break;

                        case 2:
                            russianMfParams = RussianMfParams.rus_Other;
                            cbMadeInRussia.setTextAppearance(context, R.style.combo_item_active);
                            caption = context.getString(R.string.MadeInRussia) + ": " + context.getString(R.string.MadeInRussiaOther);
                            cbMadeInRussia.setText(caption);
                            break;

                        case 3:
                            russianMfParams = RussianMfParams.rus_Undefined;
                            cbMadeInRussia.setTextAppearance(context, R.style.combo_item_active);
                            caption = context.getString(R.string.MadeInRussia) + ": " + context.getString(R.string.MadeInRussiaUndefined);
                            cbMadeInRussia.setText(caption);
                            break;

                        case 4:
                            resetMadeInRussiaCombo();
                            break;

                        default:
                            resetMadeInRussiaCombo();
                            break;
                    }
                    dialog.dismiss();
                }
            });
            builder.show();
        }

    };

    private void resetSpecialDesignCombo(){
        final String defaultCaption = context.getString(R.string.SpecialPurposeLine1) +
                Constants.sLineBreak + context.getString(R.string.SpecialPurposeLine2);
        isSnowGolf = false;
        isCaravan = false;
        cbIsSpecialDesign.setChecked(false);
        cbIsSpecialDesign.setTextAppearance(context, R.style.combo_item);
        cbIsSpecialDesign.setText(defaultCaption);
    }

    private void resetMadeInRussiaCombo(){
        russianMfParams = RussianMfParams.rus_None;
        cbMadeInRussia.setChecked(false);
        cbMadeInRussia.setTextAppearance(context, R.style.combo_item);
        cbMadeInRussia.setText(context.getString(R.string.MadeInRussia));
    }

    private void prepareCheckBox(CheckBox cb){
        cb.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cb.setTextScaleX(0.8f);
        cb.setScaleX(0.75f);
        cb.setScaleY(0.75f);
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
        prepareCheckBox(cbIsSpecialDesign);
        resetSpecialDesignCombo();
        cbIsSpecialDesign.setOnClickListener(onSpecialDesignClick);
        llBase.addView(cbIsSpecialDesign);

        cbMadeInRussia = new CheckBox(context);
        prepareCheckBox(cbMadeInRussia);
        resetMadeInRussiaCombo();
        cbMadeInRussia.setOnClickListener(onMadeInRussiaClick);
        llBase.addView(cbMadeInRussia);
    }

    @Override
    protected void initializeConcreteVehicle() {
        vehicleType = VehicleType.Car;

        defaultVolume = 2000;

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
        result.setRussianMfParams(this.russianMfParams);

        // defining price
        result.setBasicPrice(this.getPrice());

        return result;
    }

}
