package ua.com.glady.uacc.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.INotifyEvent;
import ua.com.glady.uacc.tools.ToolsView;

/**
 * This class provides UI to setup reverse calculation preferences.
 *
 * It was decided to use small same preferences fragment instead of Android existing
 * Preferences Activity in order to simplify UI for end-users.
 *
 * Created by vgl on 19.03.2015.
 */
public class BcPreferencesDialog extends DialogFragment  {

    private View view;

    String minEngineKey;
    String maxEngineKey;
    String stepEngineKey;

    int minEngineDefaultValue;
    int maxEngineDefaultValue;
    int stepEngineDefaultValue;

    private EditText edMinEngine = null;
    private EditText edMaxEngine = null;
    private EditText edStepEngine = null;

    INotifyEvent onSave;

    @Override
    public void onStart() {
        super.onStart();

        edMinEngine.setText(String.valueOf(minEngineDefaultValue));
        edMaxEngine.setText(String.valueOf(maxEngineDefaultValue));
        edStepEngine.setText(String.valueOf(stepEngineDefaultValue));
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        // creates popup dialog based on "preferences" layout
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.erpTitle));
        builder.setMessage(getResources().getString(R.string.erpMessage));


        LayoutInflater inflater = getActivity().getLayoutInflater();

        // null is ok here
        // @see <a href="http://possiblemobile.com/2013/05/layout-inflation-as-intended/">why</a>
        // "Every Rule Has An Exception". So this is why do we need @SuppressLint("InflateParams")
        view = inflater.inflate(R.layout.preferences, null);

        builder.setView(view);

        builder.setPositiveButton(getResources().getString(R.string.erpApply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                applyChanges();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        edMinEngine = (EditText) view.findViewById(R.id.edPrefMinEngine);
        edMaxEngine = (EditText) view.findViewById(R.id.edPrefMaxEngine);
        edStepEngine = (EditText) view.findViewById(R.id.edPrefStepEngine);

        return builder.create();
    }

    /**
     * React on confirm action. Puts UI data to RAM, verifies them and informs about action.
     */
    void applyChanges(){
        // UI to RAM

        String msg;
        int guiValue;

        guiValue = ToolsView.getInt(view, R.id.edPrefMinEngine, Constants.UNDEFINED);
        if (guiValue != Constants.UNDEFINED) {
            if (guiValue > Constants.ENGINE_MAX_BOUND) {
                msg = getResources().getString(R.string.erpWarningEngineVolumeTooLarge);
                Toast.makeText(view.getContext(), msg, Toast.LENGTH_LONG).show();
                return;
            }
            minEngineDefaultValue = guiValue;
        }

        guiValue = ToolsView.getInt(view, R.id.edPrefMaxEngine, Constants.UNDEFINED);
        if (guiValue != Constants.UNDEFINED) {
            if (guiValue > Constants.ENGINE_MAX_BOUND) {
                msg = getResources().getString(R.string.erpWarningEngineVolumeTooLarge);
                Toast.makeText(view.getContext(), msg, Toast.LENGTH_LONG).show();
                return;
            }
            maxEngineDefaultValue = guiValue;
        }

        // UI data sanity check
        if (minEngineDefaultValue > maxEngineDefaultValue) {
            msg = getResources().getString(R.string.erpWarningWrongRange);
            Toast.makeText(view.getContext(), msg, Toast.LENGTH_LONG).show();
            return;
        }

        guiValue = ToolsView.getInt(view, R.id.edPrefStepEngine, Constants.UNDEFINED);
        if (guiValue != Constants.UNDEFINED) {

            if (guiValue == 0) {
                msg = getResources().getString(R.string.erpWarningWrongRange);
                Toast.makeText(view.getContext(), msg, Toast.LENGTH_LONG).show();
                return;
            }

            int itemsCount = (maxEngineDefaultValue - minEngineDefaultValue) / guiValue;
            if (itemsCount > 100) {
                msg = getResources().getString(R.string.erpWarningStepTooSmall);
                Toast.makeText(view.getContext(), msg, Toast.LENGTH_LONG).show();
                return;
            }

            stepEngineDefaultValue = guiValue;
        }

        // informing caller that we did it
        if (onSave != null)
            onSave.onEvent();
    }

}