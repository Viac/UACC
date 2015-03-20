package ua.com.glady.uacc.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ua.com.glady.uacc.R;

/**
 * Contains directory of excises defined by regulatory act.
 *
 * Designed as singleton since we need to send this data to fragments. Sure you can (and ought,
 * as one only option) to make it parcelable. But we also pass this object to vehicles,
 * where it would required to implement parcelable environment
 *
 * Created by vgl on 19.03.2015.
 */
public class ExcisesRegistry {

    private final Map<String, Double> excises;

    // singleton instance
    private static ExcisesRegistry instance;

    // This way is not fast, but it doesn't really matter for us
    public static synchronized ExcisesRegistry getInstance() {
        if (instance == null) {
            instance = new ExcisesRegistry();
        }
        return instance;
    }

    /**
     * Creates a directory of excises, later it only used to get certain excise value
     */
    private ExcisesRegistry(){
        excises = new HashMap<>();
    }

    /**
     * Reads excises registry from given json string
     * @param source json string
     * @throws JSONException if json string is invalid
     */
    public void makeExcisesRegistry(String source) throws JSONException{

        excises.clear();

        JSONObject obj = new JSONObject(source);

        JSONArray arr = obj.getJSONArray("excises");
        JSONObject json;

        for (int i = 0; i < arr.length(); i++) {
            json = arr.getJSONObject(i);

            if (! (json.has("etc") && json.has("excise") ))
                throw new JSONException(R.string.assExciseNotValid + " " + json.toString());

            excises.put(json.getString("etc"), json.getDouble("excise"));
        }
    }

    /**
     * Returns excise base for certain code, or 0 if it not exists in the registry
     * meaning "all that not in a list of what should be payed is free"
     * @param etc - code (see raw.definition_of_terms.txt)
     * @return excise base (side-effects free, i.e., if final value depends on age/volume -
     * you have to calculate it manually.
     */
    public double getExciseBase(String etc){
            if (excises.containsKey(etc))
                return excises.get(etc);
            else
                return 0.0;
    }

}