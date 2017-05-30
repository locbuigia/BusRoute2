package group5.tcss450.uw.edu.busroute2;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class that gets the gas stations that are nearby the given location.
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String>{

    /*
     * String of the data from Google Places.
     */

    private String googlePlacesData;

    /*
     * url for the Google Places.
     */

    private String url;

    /*
     * List of the data that has been parsed.
     */

    public static List<BusParser> mList;

    public static List<String> mBusList ;

    /*
     * Uses the url to get data from Google Places.
     */

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            url = (String) params[0];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    /*
     * Parses the result from the data.
     */

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<BusParser> nearbyPlacesList;
        BusParser busParser = new BusParser();
        nearbyPlacesList =  busParser.parse(result);
        showBusRoutes(nearbyPlacesList);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void showBusRoutes(List<BusParser> busRoutesList) {
        mList = new ArrayList<>();
        mBusList = new ArrayList<>();
        for (int i = 0; i < busRoutesList.size(); i++) {
            BusParser busRoute = busRoutesList.get(i);
            mList.add(busRoute);
            mBusList.add(busRoute.getBus());
            Log.d("Bus Routes ", mBusList.get(i));
        }
    }
}