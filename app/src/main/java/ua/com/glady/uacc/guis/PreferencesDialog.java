package ua.com.glady.uacc.guis;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.INotifyEvent;
import ua.com.glady.uacc.model.calculators.UaccPreferences;
import ua.com.glady.uacc.model.types.VehicleType;
import ua.com.glady.uacc.tools.NumberEdit;
import ua.com.glady.uacc.tools.ToolsView;

/**
 * This class provides UI to setup reverse calculation preferences.
 * <p/>
 * It was decided to use small preferences dialog for the one vehicle type instead of Android
 * existing Preferences Activity in order to simplify UI for end-users.
 * <p/>
 * Created by vgl on 19.03.2015.
 */
public class PreferencesDialog {

    Dialog dialog;
    Context context;

    NumberEdit edLow;
    NumberEdit edHigh;
    NumberEdit edStep;
    NumberEdit edDefault;

    UaccPreferences uaccPreferences;
    UaccPreferences.VehiclePreferences vp;

    public void setOnSave(INotifyEvent onSave) {
        this.onSave = onSave;
    }

    private INotifyEvent onSave;

    public PreferencesDialog(Context context, VehicleType vehicleType) {
        this.context = context;

        uaccPreferences = new UaccPreferences(context, vehicleType);
        vp = uaccPreferences.getVehiclePreferences();
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.erpTitle);
        builder.setMessage(R.string.erpMessage);

        LinearLayout ll = CustomControlsBuilder.createVerticalLayout(context, 16, 0, 16, 0);

        edLow = CustomControlsBuilder.createNumberEdit(context, context.getString(R.string.erpMin), vp.lowVolume, 0, Constants.ENGINE_MAX_BOUND);
        edLow.setEditAppearance(R.style.flat_list_item);
        ll.addView(edLow);
        ToolsView.addSeparatorLine(context, ll, 2, context.getResources().getColor(R.color.aqua_gray), 0, 16, 0, 8);

        edHigh = CustomControlsBuilder.createNumberEdit(context, context.getString(R.string.erpMax), vp.highVolume, 0, Constants.ENGINE_MAX_BOUND);
        edHigh.setEditAppearance(R.style.flat_list_item);
        ll.addView(edHigh);
        ToolsView.addSeparatorLine(context, ll, 2, context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 8);

        edStep = CustomControlsBuilder.createNumberEdit(context, context.getString(R.string.erpStep), vp.stepVolume, 1, 1000);
        edStep.setEditAppearance(R.style.flat_list_item);
        ll.addView(edStep);
        ToolsView.addSeparatorLine(context, ll, 2, context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 8);

        edDefault = CustomControlsBuilder.createNumberEdit(context, context.getString(R.string.erpDefault), vp.defaultVolume, 0, Constants.ENGINE_MAX_BOUND);
        edDefault.setEditAppearance(R.style.flat_list_item);
        ll.addView(edDefault);

        builder.setView(ll);

        builder.setPositiveButton(R.string.erpApply, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                applyChanges();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        dialog = builder.create();
        dialog.show();
    }


    /**
     * React on confirm action. Puts UI data to RAM, verifies them and informs about action.
     */
    void applyChanges() {

        // UI to RAM

        String msg;

        int low = edLow.getValue();
        int high = edHigh.getValue();
        int step = edStep.getValue();
        int defaultValue = edDefault.getValue();

        // UI data sanity check
        if (low > high) {
            Toast.makeText(context, R.string.erpWarningWrongRange, Toast.LENGTH_LONG).show();
            return;
        }

        int itemsCount = (high - low) / step;
        if (itemsCount > 100) {
            Toast.makeText(context, R.string.erpWarningStepTooSmall, Toast.LENGTH_LONG).show();
            return;
        }

        uaccPreferences.setVehiclePreferences(low, high, step, defaultValue);

        // informing caller that we did it
        if (onSave != null)
            onSave.onEvent();
    }


}