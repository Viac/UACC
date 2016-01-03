package ua.com.glady.uacc.model.types;

/**
 * Basic data structure to keep
 *
 * Created by Slava on 02.01.2016.
 */
public class VehicleSpecificImpost {

    public String header = ""; // e.g. "Compensation impost"
    public String subHeader = ""; // e.g. "15% of basic price"
    public String description = ""; // e.g. "Established 01-JAN-2016 by regulatory act #861-VII"
    public double value = 0.0; // defines % value (15.0 = 15% of basic price)

}
