package ua.com.glady.uacc;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONException;

import ua.com.glady.uacc.fragments.ABaseFragmentUI;
import ua.com.glady.uacc.fragments.BackwardCalcUi;
import ua.com.glady.uacc.fragments.BikeDetailsUI;
import ua.com.glady.uacc.fragments.BusDetailsUI;
import ua.com.glady.uacc.fragments.CarDetailsUI;
import ua.com.glady.uacc.fragments.TruckDetailsUI;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.calculators.BcPreferences;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.model.vehicle.Bus;
import ua.com.glady.uacc.model.vehicle.Car;
import ua.com.glady.uacc.model.vehicle.Motorcycle;
import ua.com.glady.uacc.model.vehicle.Truck;
import ua.com.glady.uacc.tools.ToolsView;

import static android.widget.RelativeLayout.LayoutParams;
import static ua.com.glady.uacc.tools.ToolsRes.getRawResAsString;

/**
 * Whole application runs on single activity. Some fragments used to provide additional
 * inputs.
 *
 * Created by Slava on 19.03.2015.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    // UIs with forward calculation details, all of them is subclasses for aBaseFragmentUI
    private CarDetailsUI carDetailsUI;
    private BusDetailsUI busDetailsUI;
    private TruckDetailsUI truckDetailsUI;
    private BikeDetailsUI motorcycleDetailsUI;

    // Object instanced with concrete subclass
    private ABaseFragmentUI aBaseFragmentUI;

    private ImageButton btCar;
    private ImageButton btBus;
    private ImageButton btTruck;
    private ImageButton btMotorcycle;

    private BackwardCalcUi backwardCalcUi;

    // Used to provide icon IDs for the buttons
    private final int[][] buttons = {
            {R.id.btCar, R.id.btBus, R.id.btTruck, R.id.btMotorcycle},
            {R.drawable.car_gray, R.drawable.bus_gray, R.drawable.truck_gray, R.drawable.motorcycle_gray},
            {R.drawable.car_yellow, R.drawable.bus_yellow, R.drawable.truck_yellow, R.drawable.motorcycle_yellow},
            {R.drawable.car_blue, R.drawable.bus_blue, R.drawable.truck_blue, R.drawable.motorcycle_blue}};

    private WebView webResult;

    // index of currently activated vehicle
    private int activeIndex;
    // defines mode
    private boolean isForwardCalculation;

    private SharedPreferences sPref;
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

    /**
     * Extracted method, all details UIs used own bundles to get layout.
     * @param destination concrete instance of ABaseFragmentUI
     * @param layoutId resource id
     */
    void assignFragmentLayoutId(ABaseFragmentUI destination, int layoutId){
        Bundle args = new Bundle();
        args.putInt(Constants.LAYOUT_ID_TAG, layoutId);
        destination.setArguments(args);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        activeIndex = 0;
        isForwardCalculation = true;

        if (savedInstanceState != null) {
            activeIndex = savedInstanceState.getInt(STATE_ACTIVE_INDEX, 0);
            isForwardCalculation = savedInstanceState.getBoolean(STATE_FORWARD_MODE, true);
        }

        // Remember that onCreate called on orientation switch as well.
        sPref = this.getPreferences(MODE_PRIVATE);
        res = this.getResources();
        createExcises();

        btCar = (ImageButton) findViewById(R.id.btCar);
        btBus = (ImageButton) findViewById(R.id.btBus);
        btTruck = (ImageButton) findViewById(R.id.btTruck);
        btMotorcycle = (ImageButton) findViewById(R.id.btMotorcycle);

        carDetailsUI = new CarDetailsUI();
        assignFragmentLayoutId(carDetailsUI, R.layout.car_details);

        busDetailsUI = new BusDetailsUI();
        assignFragmentLayoutId(busDetailsUI, R.layout.bus_details);

        truckDetailsUI = new TruckDetailsUI();
        assignFragmentLayoutId(truckDetailsUI, R.layout.truck_details);

        motorcycleDetailsUI = new BikeDetailsUI();
        assignFragmentLayoutId(motorcycleDetailsUI, R.layout.bike_details);

        backwardCalcUi = new BackwardCalcUi();

        ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);
        scroll.requestFocus();

        webResult = (WebView) findViewById(R.id.webResult);

        switchBaseFragment();
    }

    /**
     * Makes all buttons style 'inactive'
     */
    private void clearButtonsHighlight(){
        btCar.setBackgroundResource(R.drawable.car_gray);
        btBus.setBackgroundResource(R.drawable.bus_gray);
        btTruck.setBackgroundResource(R.drawable.truck_gray);
        btMotorcycle.setBackgroundResource(R.drawable.motorcycle_gray);
    }

    /**
     * Invalidates main UI to set up active vehicle details
     */
    private void updateView(){
        clearButtonsHighlight();
        ImageButton activeButton = (ImageButton) findViewById(buttons[0][activeIndex]);
        if (isForwardCalculation)
            activeButton.setBackgroundResource(buttons[2][activeIndex]);
        else
            activeButton.setBackgroundResource(buttons[3][activeIndex]);
        showActiveFragment();
        webResult.setVisibility(View.INVISIBLE);

        ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);
        scroll.scrollTo(0, 0);
    }

    /**
     * Switches UI to active vehicle type
     */
    private void showActiveFragment() {
        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction ftr = fm.beginTransaction();

        if (this.isForwardCalculation) {
            switchBaseFragment();
            ftr.replace(R.id.lDetails, aBaseFragmentUI);
        } else {
            BcPreferences bcPreferences = getActiveVehicle().getBcPreferences();
            if (!backwardCalcUi.isVisible()) {
                Bundle args = new Bundle();
                args.putInt("minVolume", bcPreferences.minVolume);
                args.putInt("maxVolume", bcPreferences.maxVolume);
                args.putInt("stepVolume", bcPreferences.stepVolume);
                args.putString("minVolumeKey", bcPreferences.minVolumeKey);
                args.putString("maxVolumeKey", bcPreferences.maxVolumeKey);
                args.putString("stepVolumeKey", bcPreferences.stepVolumeKey);
                backwardCalcUi.setArguments(args);
                ftr.replace(R.id.lDetails, backwardCalcUi);
            }
            else {
                backwardCalcUi.setBcPreferences(bcPreferences);
                backwardCalcUi.invalidatePreferencesInfo();
            }
        }
        ftr.commit();
    }

    /**
     * Assigns baseFragment object depending on current active index
     */
    private void switchBaseFragment() {
        switch (activeIndex) {
            case 0:
                aBaseFragmentUI = carDetailsUI;
                break;
            case 1:
                aBaseFragmentUI = busDetailsUI;
                break;
            case 2:
                aBaseFragmentUI = truckDetailsUI;
                break;
            case 3:
                aBaseFragmentUI = motorcycleDetailsUI;
                break;
            default:
                throw new IllegalArgumentException(getString(R.string.errLayoutNotAssigned));
        }
    }

    /**
     * @return concrete Vehicle object (subtype depends on active UI)
     */
    private AVehicle getActiveVehicle() {
        if (isForwardCalculation)
            return aBaseFragmentUI.getVehicle();
        else switch (activeIndex) {
            case 0:
                return new Car(sPref, res, excisesRegistry);
            case 1:
                return new Bus(sPref, res, excisesRegistry);
            case 2:
                return new Truck(sPref, res, excisesRegistry);
            case 3:
                return new Motorcycle(sPref, res, excisesRegistry);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btCalculate){
            calculateResult();
            return;
        }
        if (v.getId() == R.id.btSwitchMode){
            // Duplicates touch on certain button to switch calculation mode
            // between forward and backward calculation
            onClick(findViewById(buttons[0][activeIndex]));
            return;
        }
        for (int i = 0; i < buttons[0].length; i++){
            if (v.getId() == buttons[0][i]){
                if (activeIndex == i)
                    isForwardCalculation = !isForwardCalculation;
                else
                    activeIndex = i;
            }
        }
        updateView();
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
        LinearLayout panMainBase = (LinearLayout) this.findViewById(R.id.panMainBase);
        panMainBase.removeView(webResult);

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
        panMainBase.addView(webResult, lp);
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
        savedInstanceState.putInt(STATE_ACTIVE_INDEX, activeIndex);
        savedInstanceState.putBoolean(STATE_FORWARD_MODE, isForwardCalculation);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}