package ua.com.glady.uacc.main_menu;

/**
 * Implemented adapter gor the main menu
 *
 * Created by Slava on 14.04.2015.
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ua.com.glady.uacc.R;

public class MenuAdapter extends BaseAdapter implements View.OnClickListener {

    ArrayList<MainMenuItem> items;
    Context context;
    LayoutInflater lInflater;
    IMenuItemSelectedListener listener;

    MenuAdapter(Context context, ArrayList<MainMenuItem> items, IMenuItemSelectedListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // using existing views, creating new
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.main_menu_item, parent, false);
        }

        MainMenuItem item = this.getMenuItem(position);

        TextView tvTitle = (TextView) view.findViewById(R.id.tvMainMenuItemHeader);
        tvTitle.setText(item.title);
        tvTitle.setTag(position);
        tvTitle.setOnClickListener(this);

        TextView tvText = (TextView) view.findViewById(R.id.tvMainMenuItemText);
        if (item.text.isEmpty()) {
            tvText.setVisibility(View.GONE);
        }
        else {
            tvText.setText(item.text);
            tvText.setTag(position);
            tvText.setOnClickListener(this);
        }

        if (item.image != -1) {
            ImageView img = (ImageView) view.findViewById(R.id.imMainMenuItemImage);
            img.setImageResource(item.image);
            img.setTag(position);
            img.setOnClickListener(this);
        }

        view.setTag(position);
        view.setOnClickListener(this);

        return view;
    }

    private MainMenuItem getMenuItem(int position) {
        return (MainMenuItem) this.getItem(position);
    }

    @Override
    public void onClick(View v) {
        listener.menuItemSelected((Integer) v.getTag());
    }
}