package com.engineering.canoq.parkhere;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    TextView loadingText = (TextView)findViewById(R.id.loading_text_view);
    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;

    Location mLastLocation;
    Double mLLat;
    Double mLLng;

    Double mLat;
    Double mLng;

    MarkerOptions marker;

    String ans;

    BitmapDescriptor pin;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });


        //Set-up Fab: on click
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingText.setVisibility(View.VISIBLE);

                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask()
                {
                    @Override
                    public void run() {
                        FetchJSONTaskPrimer spotTask = new FetchJSONTaskPrimer();
                        spotTask.execute();
                    }}, 0, 3000);
            }
        });

        //Set-up Fab: estética
        Drawable icon = getResources().getDrawable(R.mipmap.opera_glass);
        fab.setImageDrawable(icon);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        MapsInitializer.initialize(getApplicationContext());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        mGoogleApiClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ParkHere Map",
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.engineering.canoq.parkhere/http/host/path")
        );

        if (mMap!= null)mMap.clear();

        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.engineering.canoq.parkhere/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            LatLng current = new LatLng(-33.424405, -70.60997);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null){
            mLLat = mLastLocation.getLatitude();
            mLLng = mLastLocation.getLongitude();
        }

        // Add a marker in Sydney and move the camera
        //LatLng current = new LatLng(mLLat, mLLng);
        LatLng current = new LatLng(-33.424405, -70.60997);
        mMap.addMarker(new MarkerOptions().position(current).title("Ubicacion Actual"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

        mMap.clear();
        marker = new MarkerOptions().position(latLng);
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        mLat = latLng.latitude;
        mLng = latLng.longitude;

        fab.setVisibility(View.VISIBLE);



    }

    //Buscará datos en Server
    public class FetchJSONTaskPrimer extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchJSONTaskPrimer.class.getSimpleName();

        //Busca los valores del API y los devuelve como String ResultStr
        private String getDataFromJson(String JsonStr) throws JSONException {

            final String SERVER_SPOTS = "spots";
            JSONObject JsonData = new JSONObject(JsonStr);

            JSONArray JsonSpots = JsonData.getJSONArray(SERVER_SPOTS);

            ans = "";
            int N = JsonSpots.length();
            for (int i = 0; i < N; i++) {

                JSONObject Spot = JsonSpots.getJSONObject(i);
                if (Spot.getInt("status") == 0) {
                    Double lat = Spot.getDouble("lat");
                    Double lon = Spot.getDouble("lon");
                    ans += lat.toString() + "!" + lon.toString() + "/";
                }
            }

            return ans;
        }

        //Background para que el UI no laggee
        @Override
        protected String doInBackground(String... params) {

            //CONEXION CON API
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            //Crea URL (!)(!)(!) CREAR VARIABLE PARA CUANTOS DIAS DE DATA
            String JsonStr = null;
            String URL_ORE = "http://www.parkhere.me/get_spots";

            try {
                Uri builtUri = Uri.parse(URL_ORE).buildUpon()
                        .build();
                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                JsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                //Después de tener la conexión con el API, crea el resultStr y lo
                //devuelve
                return getDataFromJson(JsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                mMap.clear();
                String[] tuples = result.split("/");
                for (String tupleStr : tuples) {
                    if (tupleStr.contains("!")) {

                        pin = BitmapDescriptorFactory.fromResource(R.mipmap.pin);

                        String[] tuple = tupleStr.split("!");
                        LatLng flag1 = new LatLng(Double.valueOf(tuple[0]), Double.valueOf(tuple[1]));
                        mMap.addMarker(new MarkerOptions().position(flag1).title("Estacionamientos!")
                                .icon(pin));
                        loadingText.setVisibility(View.INVISIBLE);

                    }
                }

            }
        }

    }
}