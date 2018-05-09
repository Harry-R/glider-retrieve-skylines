package de.lrapp.gliderretrieveskylines;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static java.lang.String.*;

public class MainActivity extends AppCompatActivity implements ApiCallback {

    // UI
    private EditText et_pilotId;
    private TextView txtv_pilotName;
    private TextView txtv_nearestApt;
    private TextView txtv_pos;
    private TextView txtv_hag;
    private TextView txtv_distMe;
    // API
    ApiCallback apiCallback;
    // placeholder strings
    String meters;
    String kilometers;
    String position;
    // current locale
    Locale cur_locale;
    // location manager & listener
    LocationManager locationManager;
    MyLocationListener locationListener;
    // state machine
    StateMachine stateMachine;
    private Boolean firstUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiCallback = this;
        cur_locale = getResources().getConfiguration().locale;
        initLocationListener();
        initUi();
    }


    @Override
    protected void onStop(){
        super.onStop();

        // save pilotId settings
        SharedPreferences settings = getSharedPreferences("sharedPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("pilotId", Integer.parseInt(et_pilotId.getText().toString()));
        editor.apply();
    }


    /**
     * Initialize the location listener, request location permission, if not granted
     */
    private void initLocationListener() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    /**
     * Overrides AppCompat.onRequestPermissionsResult, see their documentation for details
     * @param requestCode Code to identify the request
     * @param permissions permission as String
     * @param grantResults int array with grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            // access fine location
            case 1: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission granted
                } else {
                    // permission denied
                    Toast.makeText(MainActivity.this, "Permission denied to read your Location", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * initialize UI
     */
    private void initUi() {
        et_pilotId = (EditText) findViewById(R.id.et_trackingId);
        txtv_pilotName = (TextView) findViewById(R.id.txtv_pilotName);
        txtv_nearestApt = (TextView) findViewById(R.id.txtv_nearestApt);
        txtv_pos = (TextView) findViewById(R.id.txtv_pos);
        txtv_hag = (TextView) findViewById(R.id.txtv_hag);
        txtv_distMe = (TextView) findViewById(R.id.txtv_distMe);

        meters = getResources().getString(R.string.meters);
        kilometers = getResources().getString(R.string.kilometers);
        position = getResources().getString(R.string.position);

        // restore pilotId from settings
        SharedPreferences settings = getSharedPreferences("sharedPrefs", 0);
        int pilotId = settings.getInt("pilotId", 0);
        et_pilotId.setText(pilotId + "");
    }

    /**
     * on click listener for update button
     * @param view view the function was called from
     */
    public void clickBtnUpdate(View view) {
        int pilotID = Integer.parseInt(et_pilotId.getText().toString());
        ApiRequest.startUpdater(pilotID, apiCallback);
    }

    /**
     * Update UI with tracking data, use strings with placeholder
     * @param data pilot's tracking data
     */
    private void updateUI(JSONObject data) {
        if(data != null) {
            try {
                txtv_pilotName.setText(format(cur_locale, "%s (%d)", data.getJSONObject("pilot").getString("name"), data.getJSONObject("pilot").getInt("id")));
                txtv_nearestApt.setText(format(cur_locale, "%s (%.01f km)", data.getJSONObject("nearestAirport").getString("name"), data.getDouble("nearestAirportDistance")/1000));
                txtv_pos.setText(format(position, data.getJSONArray("location").getDouble(1),
                        data.getJSONArray("location").getDouble(0)));
                txtv_hag.setText(format(meters, data.getInt("altitude") - data.getInt("elevation")));
                txtv_distMe.setText(calcDistance(data.getJSONArray("location").getDouble(1),
                        data.getJSONArray("location").getDouble(0)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_pilotIdNF), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Calculates distance between the retriever's and the pilot's location and formats as string
     * @param lat pilot's Latitude
     * @param lon pilot's longitude
     * @return If there is GPS: distance formatted as string, else: "Searching fo GPS..."
     */
    private String calcDistance(double lat, double lon) {
        Location myLocation = locationListener.getLocation();
        Location pilotLocation = new Location("");
        pilotLocation.setLatitude(lat);
        pilotLocation.setLongitude(lon);
        if (myLocation != null) {
            Float distance = myLocation.distanceTo(pilotLocation)/1000;
            return format(Locale.US, "%.01f km", distance);
        }
        return "Searching for GPS...";
    }

    /**
     * work with data from callback, triggered when callback is called by ApiRequest's async task
     * @param data pilot's track data as JSONObject
     */
    public void callback(JSONObject data) {
        // calculate height above GND for state machine
        int height = 0;
        try {
             height = data.getInt("altitude") - data.getInt("elevation");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // needs the state machine to be initialized?
        if (firstUpdate) {
            stateMachine = new StateMachine(height, this);
            firstUpdate = false;
        }
        // perform a state machine operation
        stateMachine.run(height);
        // update the UI
        updateUI(data);
    }
}
