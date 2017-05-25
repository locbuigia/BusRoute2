package group5.tcss450.uw.edu.busroute2;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jnbui on 5/25/2017.
 */

public class BusParser implements Serializable{
     /*
     * Gas station name
     */

    private String mBusRoute;

    /*
     * The address the station.
     */

    private String mVicinity;

    /*
     * The latitude and longitude of the station.
     */

    private String mLat, mLng;

    /*
     * The rating given to the gas station.
     */

    private double mRating;


    /*
     * Constructor of the for the data.
     * @param theName The name of the station.
     * @param theVicinity The address from the station.
     * @param theLat The latitude of the station.
     * @param theLng The longitude of the station.
     * @param theRating The rating of the station.
     * @param thePriceLevel The price level of the station.
     */

    public BusParser(String theBus) {
        mBusRoute = theBus;

    }

    /*
     * Empty public constructor that takes no fields.
     */

    public BusParser() {
    }

    /*
     * Pares the json string and returns a list of data fields.
     */

    public List<BusParser> parse(String jsonData) {
        ArrayList<BusParser> list = new ArrayList<>();
        JSONArray jsonArray;
        JSONObject jsonObject;

        try {
            Log.d("Places", "parse");
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes");

            int length = jsonArray.length();
            Log.d("Length", Integer.toString(length));
            if (jsonArray != null) {
                for (int i = 0; i < length; i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    JSONArray legs = object.getJSONArray("legs");
                    JSONObject temp1 = legs.getJSONObject(0);
                    JSONArray steps = temp1.getJSONArray("steps");
                    for (int j = 1; j < steps.length(); j+=2) {
                        JSONObject transit_detail = steps.getJSONObject(j-1);
                        JSONObject temp = transit_detail.getJSONObject("transit_details");
                        JSONObject line = temp.getJSONObject("line");
                        Log.d("Temp1 ", line.toString());
//                        String busRoute = line.getString("short_name");
                    }


//                    BusParser bus = new BusParser(busRoute);
//
//                    list.add(bus);
                }
            }
        } catch (JSONException e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
        }
        return list;
    }

    public String getBus() {
        return mBusRoute;
    }
}
