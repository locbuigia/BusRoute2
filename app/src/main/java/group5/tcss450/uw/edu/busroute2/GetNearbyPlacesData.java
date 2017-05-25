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
     * Google maps object.
     */

    private GoogleMap mMap;

    /*
     * url for the Google Places.
     */

    private String url;

    /*
     * Latitude of the gas station.
     */

    private double lat;

    /*
     * Longitude of the gas station.
     */

    private double lng;

    /*
     * Name of the gas station.
     */

    private String name;

    /*
     * Address of the gas station.
     */

    private String vicinity;

    /*
     * Rating of the gas station.
     */

    private double rating;

    /*
     * List of the data that has been parsed.
     */

    public static List<BusParser> mList;

    /*
     * Hashmap of the location markers.
     */

    public HashMap<Marker, Integer> mPubMarkerMap = new HashMap<>();

    /*
     * Uses the url to get data from Google Places.
     */

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
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

    /*
     * Shows the nearby gas stations.
     */

//    private void showNearbyPlaces(List<DataParser> nearbyPlacesList) {
//        mList = new ArrayList<>();
//        for (int i = 0; i < nearbyPlacesList.size(); i++) {
//            Log.d("onPostExecute","Entered into showing locations");
//            MarkerOptions markerOptions = new MarkerOptions();
//            DataParser googlePlace = nearbyPlacesList.get(i);
//            lat = Double.parseDouble(googlePlace.getLat());
//            lng = Double.parseDouble(googlePlace.getLng());
//            name = googlePlace.getName();
//            vicinity = googlePlace.getVicinity();
//            rating = googlePlace.getRating();
//            DataParser dataParser = new DataParser(name, vicinity,
//                    googlePlace.getLat(), googlePlace.getLng(), rating);
//            mList.add(dataParser);
//            Log.d("mList:", " " + mList.get(i).getName());
//            markerOptions.position(new LatLng(lat,lng));
//            markerOptions.title(dataParser.getName());
//            Marker mark = mMap.addMarker(markerOptions);
//            mPubMarkerMap.put(mark, i);
//        }
//    }

    private void showBusRoutes(List<BusParser> busRoutesList) {
        mList = new ArrayList<>();
        for (int i = 0; i < busRoutesList.size(); i++) {
            Log.d("onPostExecute","Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            BusParser busRoute = busRoutesList.get(i);
            name = busRoute.getBus();
            mList.add(busRoute);
            Log.d("mList:", " " + mList.get(i).getBus());
            markerOptions.position(new LatLng(lat,lng));
            markerOptions.title(busRoute.getBus());
            Marker mark = mMap.addMarker(markerOptions);
            mPubMarkerMap.put(mark, i);
        }
    }
}