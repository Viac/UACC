package ua.com.glady.uacc;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import ua.com.glady.uacc.guis.BackwardCalcUi;
import ua.com.glady.uacc.guis.BusDataUi;
import ua.com.glady.uacc.guis.CarDataUi;
import ua.com.glady.uacc.guis.MotorcycleDataUi;
import ua.com.glady.uacc.guis.TruckDataUi;
import ua.com.glady.uacc.guis.VehicleDataUi;
import ua.com.glady.uacc.main_menu.IMenuItemSelectedListener;
import ua.com.glady.uacc.main_menu.MainMenu;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.calculators.UaccPreferences;
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
    private Button btCalculate;

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

        isForwardCalculation = true;

        activePageIndex = UaccPreferences.getActivePage(this, 0);

        if (savedInstanceState != null) {
            activePageIndex = savedInstanceState.getInt(STATE_ACTIVE_INDEX, 0);
            isForwardCalculation = savedInstanceState.getBoolean(STATE_FORWARD_MODE, true);
        }

        // Remember that onCreate called on orientation switch as well.
        res = this.getResources();
        createExcises();

        backwardCalcUi = new BackwardCalcUi(this, getCurrentVehicleType());
        btCalculate = (Button) this.findViewById(R.id.btCalculate);

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



    /**
     *
     * This code used to quickly get data to test application and to see the difference between
     * current state (01.12.2015) and probable state if act #3251 will be voted in parlament
     * So it's a kind of debug code, some dirty, of course.
     *
     * Example: http://viac-soft.in.ua/uacc/uacc_compare.html
     *
     *
     *
     private static double roundToScale(double number, int scale) {
     int pow = (int) Math.pow(10, scale);
     double tmp = number * pow;
     return (double) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
     }


    private String getGlobalTable(){
        StringBuilder sb = new StringBuilder();

        Car car = new Car(this, this.excisesRegistry);
        Car car3251 = new Car(this, this.excisesRegistry3251);

        car.getEngine().setType(Constants.ENG_GASOLINE);
        car3251.getEngine().setType(Constants.ENG_GASOLINE);


        double totalPrice = 0;
        double totalPrice3251 = 0;
        double percentageD = 0.0;
        long percOut = 0;

        sb.append("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"></head><body><table>");

        // Table header
        sb.append("<tr><td>Двигатель</td><td>Возраст</td>");
        for (int i = 500; i <= 15000; i+= 500) {
            sb.append("<td>").append(i).append("</td>");
        }
        sb.append("</tr>");

        for (int i = 799; i < 3200; i+=100) {
            car.getEngine().setVolume(i); // 1 volume
            car3251.getEngine().setVolume(i); // 1 volume

            sb.append("<tr><td>").append(i + 1).append("</td>");
            car.setAge(12);  // 2 age
            car3251.setAge(12);  // 2 age
            sb.append("<td>до 5 лет</td>");
            for (int j = 500; j <= 15000; j+=500){
                car.setBasicPrice(j);
                car3251.setBasicPrice(j);
                totalPrice = roundToScale((car.getTotalPrice() / 1000.0), 1);
                totalPrice3251 = roundToScale((car3251.getTotalPrice()/1000.0), 1);
                percentageD = 100.0 * totalPrice3251 / totalPrice;
                percOut = 100 - Math.round(percentageD);
                sb.append("<td>").append(totalPrice).append("→").append(totalPrice3251);
                sb.append("</br><span class=\"percentage" + String.valueOf(percOut / 10) + "\">-").append(percOut).append("%</span>").append("</td>");
            }
            sb.append("</tr>");


            sb.append("<tr><td>").append(i + 1).append("</td>");
            car.setAge(65);  // 2 age
            car3251.setAge(65);  // 2 age
            sb.append("<td>старше 5</td>");
            for (int j = 500; j <= 15000; j+=500){
                car.setBasicPrice(j);
                car3251.setBasicPrice(j);
                totalPrice = roundToScale((car.getTotalPrice()/1000.0), 1);

                totalPrice3251 = roundToScale((car3251.getTotalPrice()/1000.0), 1);
                percentageD = 100.0 * totalPrice3251 / totalPrice;
                percOut = 100 - Math.round(percentageD);
                sb.append("<td>").append(totalPrice).append("→").append(totalPrice3251);
                sb.append("</br><span class=\"percentage" + String.valueOf(percOut / 10) + "\">-").append(percOut).append("%</span>").append("</td>");
            }
            sb.append("</tr>");

        }
        sb.append("</table></body></html>");
        return sb.toString();
    }
     */

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
                (v.getId() == R.id.tvVehicleType) || (v.getId() == R.id.tvCalculationDescription)) {
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

        ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);

        // 24 is a top/bottom margins o details UI / button and so on
        int deltaY = panVehicleData.getHeight() + btCalculate.getHeight() + 24;

        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(scroll, "scrollY", 0, deltaY).setDuration(500);
        objectAnimator.start();
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
    public void onStop(){
        UaccPreferences.setActivePage(this, activePageIndex);
        super.onStop();
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