package ua.com.glady.uacc;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import ua.com.glady.uacc.guis.BackwardCalcUi;
import ua.com.glady.uacc.guis.BusDataUi;
import ua.com.glady.uacc.guis.CarDataUi;
import ua.com.glady.uacc.guis.MotorcycleDataUi;
import ua.com.glady.uacc.guis.PreferencesDialog;
import ua.com.glady.uacc.guis.TruckDataUi;
import ua.com.glady.uacc.guis.VehicleDataUi;
import ua.com.glady.uacc.main_menu.IMenuItemSelectedListener;
import ua.com.glady.uacc.main_menu.MainMenu;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.INotifyEvent;
import ua.com.glady.uacc.model.types.VehicleType;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.model.vehicle.Bus;
import ua.com.glady.uacc.model.vehicle.Car;
import ua.com.glady.uacc.model.vehicle.Motorcycle;
import ua.com.glady.uacc.model.vehicle.Truck;
import ua.com.glady.uacc.tools.ToolsView;

import static android.widget.RelativeLayout.LayoutParams;
import static ua.com.glady.uacc.tools.ToolsRes.getRawResAsString;

/**
 * Primary application activity
 *
 * Created by Slava on 19.03.2015.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private class VehiclePage{
        String caption;
        String description;
        VehicleDataUi dataUI;
    }

    private VehiclePage[] vehiclePages;

    // Active vehicle UI mode (constant above)
    private int activePageIndex;

    private BackwardCalcUi backwardCalcUi;

    private WebView webResult;
    private ViewGroup panVehicleData;

    // defines mode
    private boolean isForwardCalculation;

    private Resources res;

    // singleton object
    private ExcisesRegistry excisesRegistry;

    private static final String STATE_ACTIVE_INDEX = "activeIndex";
    private static final String STATE_FORWARD_MODE = "isForwardMode";

    @Override
   protected void onResume(){
       super.onResume();
       updateView();
   }

    /**
     * Initializes excises list, it wil be used in fragments and vehicle objects
     */
    private void createExcises(){
        excisesRegistry = ExcisesRegistry.getInstance();
        String excisesSource = getRawResAsString(getResources(), R.raw.excise_list);
        try {
            excisesRegistry.makeExcisesRegistry(excisesSource);
        } catch (JSONException e) {
            Toast.makeText(this.getApplicationContext(), R.string.errExciseListCorrupted, Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    private VehicleType getCurrentVehicleType(){
        return VehicleType.values()[activePageIndex];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

        // be default we show cars
        activePageIndex = 0;

        isForwardCalculation = true;

        if (savedInstanceState != null) {
            activePageIndex = savedInstanceState.getInt(STATE_ACTIVE_INDEX, 0);
            isForwardCalculation = savedInstanceState.getBoolean(STATE_FORWARD_MODE, true);
        }

        // Remember that onCreate called on orientation switch as well.
        res = this.getResources();
        createExcises();

        backwardCalcUi = new BackwardCalcUi(this, getCurrentVehicleType());

        initializeVehiclePages();

        ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);
        scroll.requestFocus();

        webResult = (WebView) findViewById(R.id.webResult);
        panVehicleData = (ViewGroup) findViewById(R.id.panVehicleData);

        updateView();
    }

    /**
     * In this activity we 'emulate' regular tab UI, since some of the data on the screen is
     * not so clearly different as tab assumed.
     *
     * So here we initialize these pages
     */
    private void initializeVehiclePages() {

        vehiclePages = new VehiclePage[4];

        // Initializing cars
        VehiclePage pageCar = new VehiclePage();
        pageCar.caption = getString(R.string.Car);
        pageCar.description = getString(R.string.CarDescription);
        pageCar.dataUI =  new CarDataUi(this, excisesRegistry);
        vehiclePages[0] = pageCar;

        // Initializing buses
        VehiclePage pageBus = new VehiclePage();
        pageBus.caption = getString(R.string.Bus);
        pageBus.description = getString(R.string.BusDescription);
        pageBus.dataUI =  new BusDataUi(this, excisesRegistry);
        vehiclePages[1] = pageBus;

        // Initializing trucks
        VehiclePage pageTruck = new VehiclePage();
        pageTruck.caption = getString(R.string.Truck);
        pageTruck.description = getString(R.string.TruckDescription);
        pageTruck.dataUI =  new TruckDataUi(this, excisesRegistry);
        vehiclePages[2] = pageTruck;

        // Initializing motorcycles
        VehiclePage pageMotorcycle = new VehiclePage();
        pageMotorcycle.caption = getString(R.string.Motorcycle);
        pageMotorcycle.description = getString(R.string.MotorcycleDescription);
        pageMotorcycle.dataUI =  new MotorcycleDataUi(this, excisesRegistry);
        vehiclePages[3] = pageMotorcycle;
    }


    /**
     * Invalidates main UI to set up active vehicle details
     */
    private void updateView(){

        TextView tvVehicleType = (TextView) this.findViewById(R.id.tvVehicleType);
        tvVehicleType.setText(vehiclePages[activePageIndex].caption);

        TextView tvCalculationDescription = (TextView) this.findViewById(R.id.tvCalculationDescription);
        if (this.isForwardCalculation)
            tvCalculationDescription.setText(R.string.ForwardCalcTitle);
        else
            tvCalculationDescription.setText(R.string.BackwardCalcTitle);

        showActiveFragment();

        webResult.setVisibility(View.GONE);

        ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);
        scroll.scrollTo(0, 0);

        if (!this.isForwardCalculation)
            updateBcPreferences();
    }

    /**
     * Switches UI to active vehicle type
     */
    private void showActiveFragment() {

        panVehicleData.removeAllViews();

        if (this.isForwardCalculation) {
            panVehicleData.addView(vehiclePages[activePageIndex].dataUI);
        } else {
            panVehicleData.addView(this.backwardCalcUi);
            backwardCalcUi.setVehicleType(VehicleType.values()[activePageIndex]);
        }
    }


    /**
     * @return concrete Vehicle object (subtype depends on active UI)
     */
    private AVehicle getActiveVehicle() {
        if (isForwardCalculation)
            return vehiclePages[activePageIndex].dataUI.getVehicle();
        else switch (activePageIndex) {
            case 0:
                return new Car(this, excisesRegistry);
            case 1:
                return new Bus(this, excisesRegistry);
            case 2:
                return new Truck(this, excisesRegistry);
            case 3:
                return new Motorcycle(this, excisesRegistry);
            default:
                throw new IllegalArgumentException(getString(R.string.errLayoutNotAssigned));
        }
    }

    /**
     * Shows help html
     */
    private void showHelp(){
        String title = this.res.getString(R.string.Info);
        String html = getRawResAsString(res, R.raw.help);
        ToolsView.showPopupWebView(this, title, html);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            showHelp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateBcPreferences(){
        backwardCalcUi.updatePreferencesText();
    }

    IMenuItemSelectedListener onMenuItemSelected = new IMenuItemSelectedListener(){

        @Override
        public void menuItemSelected(int position) {

            if ((position >= 0) && (position <= 3)){
                activePageIndex = position;
                updateView();
            }

            if (position == 4) {
                showHelp();
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btCalculate){
            calculateResult();
            return;
        }
        if (v.getId() == R.id.btSwitchMode){
            isForwardCalculation = !isForwardCalculation;
            updateView();
            return;
        }
        if ((v.getId() == R.id.btVehicleType) || (v.getId() == R.id.llVehicleTitle) ||
                (v.getId() == R.id.tvVehicleType) || (v.getId() == R.id.tvCalculationDescription)){
            MainMenu menu = new MainMenu();
            menu.show(this, onMenuItemSelected);
        }
    }

    /**
     * Runs calculation (forward or backward) and loads result to webView
     */
    private void calculateResult() {
        String result;

        if (isForwardCalculation)
            result = getForwardCalcResult();
        else
            result = getBackwardCalcResult();

        // There is a very well-known bug in WebView: it doesn't reduce height
        // if content was reloaded. So if previous page was long enough - it will left some
        // empty space on the bottom  after reload
        // This wasn't fixed in 4.4.4, and the problem is well known and described (see SO,
        // https://code.google.com/p/android/issues/detail?id=18726 ) and so on
        // Few methods were tested here and one only made good result - webView destroyed
        // before show and created on new content. In this case everything worked fine on
        // smart and tablet with acceptable performance
        LinearLayout panScroll = (LinearLayout) this.findViewById(R.id.panScroll);
        panScroll.removeView(webResult);

        webResult = new WebView(this);
        webResult.setVisibility(View.VISIBLE);
        webResult.loadData(result, "text/html; charset=UTF-8", null);
        webResult.reload();

        WebSettings settings = webResult.getSettings();
        // to correct display of special chars (EURO, cubed, so on)
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultFontSize(14);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        lp.setMargins(4, 12, 4, 0);
        panScroll.addView(webResult, lp);
    }

    /**
     * @return html-string with backward calculation result
     */
    private String getBackwardCalcResult() {
        int finalPrice = backwardCalcUi.getPrice();

        if (finalPrice <= 0){
            return getResources().getString(R.string.errFinalPriceUndefined);
        }

        String htmlTemplate = getRawResAsString(getResources(), R.raw.reverse_calc_template);
        AVehicle vehicle = getActiveVehicle();
        vehicle.makeBcOutput(finalPrice);
        return vehicle.getBackwardCalcHtml(htmlTemplate);
    }

    /**
     * @return html-string with forward calculation result
     */
    private String getForwardCalcResult() {
        String htmlTemplate = getRawResAsString(getResources(), R.raw.direct_calc_template);
        AVehicle vehicle = getActiveVehicle();
        if (vehicle.getBasicPrice() <= 0)
            return res.getString(R.string.errPriceCannotBeZero);
        return vehicle.getForwardCalcHtml(htmlTemplate);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(STATE_ACTIVE_INDEX, activePageIndex);
        savedInstanceState.putBoolean(STATE_FORWARD_MODE, isForwardCalculation);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}