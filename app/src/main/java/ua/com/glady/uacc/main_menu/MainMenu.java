package ua.com.glady.uacc.main_menu;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import ua.com.glady.uacc.R;

/**
 * Provides access to application main menu
 *
 * Shortly - shows main menu (available vehicle types + info + preferences)
 * and returns selected position in listener
 *
 * Created by Slava on 14.04.2015.
 */
public class MainMenu implements IMenuItemSelectedListener {

    AlertDialog dialog = null;

    IMenuItemSelectedListener listener;

    public void show(Context context, IMenuItemSelectedListener listener) {

        this.listener = listener;

        ArrayList<MainMenuItem> li = new ArrayList<>();

        li.add(new MainMenuItem(context.getString(R.string.Car), context.getString(R.string.CarDescription), R.drawable.car_gray));
        li.add(new MainMenuItem(context.getString(R.string.Bus), context.getString(R.string.BusDescription), R.drawable.bus_gray));
        li.add(new MainMenuItem(context.getString(R.string.Truck), context.getString(R.string.TruckDescription), R.drawable.truck_gray));
        li.add(new MainMenuItem(context.getString(R.string.Motorcycle), context.getString(R.string.MotorcycleDescription), R.drawable.motorcycle_gray));

        li.add(new MainMenuItem(context.getString(R.string.Info), "", android.R.drawable.ic_menu_help));

        MenuAdapter a = new MenuAdapter(context, li, this);

        ListView lvMain = new ListView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lvMain.setLayoutParams(lp);
        lvMain.setAdapter(a);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(lvMain);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void menuItemSelected(int position) {
        listener.menuItemSelected(position);
        dialog.dismiss();
    }
}
