package group5.tcss450.uw.edu.busroute2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String Key_Search = "name";

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;

    private double longitude, latitude;

    private Marker mCurrLocationMarker;

    private static final String TAG = "LocationsActivity";
    private static final String KEY_DB = "DB";

    private LocationRequest mLocationRequest;

    String place;
    String name;
    public static int caseToParse;


    private TextView mTextView;

    private EditText mEditText;
    private ArrayList<String> listOfWords = new ArrayList<>();
    private String mString;

    /**
     * url to connect to our API.
     */
    private static final String mURL = "https://westus.api.cognitive.microsoft.com/linguistics/v1.0/analyze";
    /**
     * keys for connect to external database.
     */
    private static  final String mKey = "b80390842a2449528662a1a01e98d032";
    private static final String mKey1 = "2807d9ffb29043c7a13e7bcf706a81a2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        place = getIntent().getStringExtra("type");
        name = getIntent().getStringExtra("name");

        mTextView = (TextView) findViewById(R.id.Text_View);
        mEditText = (EditText) findViewById(R.id.search_field);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 4000);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCfUf81B45d045Yf-9PiCtlF7RXQN9tr7I");
        caseToParse = 1;
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        Location lastLocation = new Location("Last Location");
        lastLocation.setLatitude(latitude);
        lastLocation.setLongitude(longitude);
        if (location.distanceTo(lastLocation) > 1000) {
            //Place current location marker
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            LatLng latlng = new LatLng(latitude, longitude);
            Log.d("lat:" , "" + latitude);
            Log.d("lng:" , "" + longitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }

        if (place != null) {
            mMap.clear();
            Log.d("onClick", "Button is Clicked");
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            String url = getUrl(latitude, longitude, place);
            Object[] DataTransfer = new Object[2];
            DataTransfer[0] = mMap;
            DataTransfer[1] = url;
            Log.d("onClick", url);
            final GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(DataTransfer);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    DataParser dp;
                    dp = getNearbyPlacesData.mList.get(getNearbyPlacesData.mPubMarkerMap.get(marker));
                    Intent intent = new Intent(getApplication(), DetailActivity.class);

                    Location desti = new Location("Destination");
                    desti.setLatitude(Double.parseDouble(dp.getLat()));
                    desti.setLongitude(Double.parseDouble(dp.getLng()));

                    intent.putExtra("name", dp.getName());
                    intent.putExtra("vicinity", dp.getVicinity());
                    intent.putExtra("rating", dp.getRating());

                    startActivity(intent);
                }
            });
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /*
     * Requests the user for permission to use location.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // popup permission requestion for location.
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private String getUrlText(double latitude, double longitude, String query) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        googlePlacesUrl.append("query=" + removeSpace(query));
        googlePlacesUrl.append("&key=" + "AIzaSyCfUf81B45d045Yf-9PiCtlF7RXQN9tr7I");
        googlePlacesUrl.append("&location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 400);
        Log.d("getUrl", googlePlacesUrl.toString());
        caseToParse = 2;
        return (googlePlacesUrl.toString());
    }

    private String removeSpace(String str) {
        String returnStr;
        return returnStr = str.replace(" ", "_");
    }

    public void Search(View v) {
        listOfWords.clear();
        mMap.clear();
        AsyncTask<String, Void, String> task = null;
        mString = mEditText.getText().toString();
        switch (v.getId()) {
            case R.id.button:
                task = new PostWebServiceTask();
                task.execute(mURL);
                break;
            default:
                throw new IllegalStateException("Not implemented");
        }
        Log.d("onClick", "Button is Clicked");
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
    }

    private void display(String theLocation) {
        String url = getUrlText(latitude, longitude, theLocation);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        final GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                DataParser dp;
                dp = getNearbyPlacesData.mList.get(getNearbyPlacesData.mPubMarkerMap.get(marker));
                Intent intent = new Intent(getApplication(), DetailActivity.class);

                intent.putExtra("name", dp.getName());
                intent.putExtra("vicinity", dp.getVicinity());
                intent.putExtra("rating", dp.getRating());

                startActivity(intent);
            }
        });
    }

    private class PostWebServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 1) {
                throw new IllegalArgumentException("Two String arguments required.");
            }
            HttpClient httpclient = HttpClients.createDefault();

            //modified example code from microsoft
            try
            {

                HttpEntity entity;

                URIBuilder builder = new URIBuilder(mURL);

                URI uri = builder.build();
                HttpPost request = new HttpPost(uri);
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Ocp-Apim-Subscription-Key", mKey);

                String sent = mString;



                //Request Body
                StringEntity reqEntity = new StringEntity("{\"language\" : \"en\",\n" +
                        "\t\"analyzerIds\" : [\"4fa79af1-f22c-408d-98bb-b7d7aeef7f04\", \"22a6b758-420f-4745-8a3c-46835a67c0d2\"],\n" +
                        "\t\"text\" :" + "\""+ sent  +"\"}" );        //  \"I want to get pizza\" }");
                request.setEntity(reqEntity);




                HttpResponse response = httpclient.execute(request);
                entity = response.getEntity();
                String result =  new String(EntityUtils.toString(entity));

                //debug purposes.
                if (entity != null)
                {
                    Log.d("entity not null", result);
                }

                return result;
            }
            catch (Exception e)
            {

                String result = "Unable to connect, Reason: " + e.getMessage();
                return e.getMessage();
            }
        }
        @Override
        protected void onPostExecute(String result) {

            News news = null;

            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            News[] newses = new News[0];
            try {
                JSONArray value = new JSONArray(result);
                newses = new News[value.length()];
                for (int i = 0; i < value.length()-1; i++){
                    JSONObject oneNews = (JSONObject) value.get(i);
                    newses[i] = new News(oneNews);
                }

            } catch (JSONException e){
                e.printStackTrace();
            }

            final News[] tempNewses = newses;
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i< tempNewses.length - 1; i++) {
                sb.append(tempNewses[i].getResult());
            }

            Log.d("result", sb.toString());
            Log.d("The String", mString);
            StringTokenizer parseString = new StringTokenizer(mString, " ");
            while (parseString.hasMoreTokens()) {
                listOfWords.add(parseString.nextToken());
            }
            String tag = sb.toString().substring(2, sb.toString().length()-2);
            String[] tags = tag.split(",");
            ArrayList<Integer> size = new ArrayList();
            String word = "";
            for (int i = 0; i < tags.length; i++) {
                if (tags[i].equals("\"NN\"" )|| tags[i].equals("\"NNP\"") || tags[i].equals("\"NNS\"" )|| tags[i].equals("\"NNPS\""))
                {
                    Log.d("Index ", i + "");
                    Log.d("Word ", listOfWords.get(i));
                    word = word + " " +  listOfWords.get(i);
                    size.add(i);
                }
            }

            StringBuilder mFinal = new StringBuilder();
            for(int i = 0; i< size.size();i++ ) {
                mFinal.append(listOfWords.get(size.get(i)));
            }
            // mTextView.setText(mFinal.toString());
            mTextView.setText(sb.toString());
            Log.d("Final Word is ", word);
            display(word);
        }
    }
}
