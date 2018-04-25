package de.lrapp.gliderretrieveskylines;

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

public class MainActivity extends AppCompatActivity implements ApiCallback{

    // UI
    private TextView txtv_debug;
    private EditText et_pilotId;
    private TextView txtv_pilotName;
    private TextView txtv_nearestApt;
    private TextView txtv_nearestAptDis;
    private TextView txtv_pos;
    private TextView txtv_alt;
    private TextView txtv_elev;
    private TextView txtv_hag;
    private TextView txtv_pilotId;
    // API
    ApiCallback apiCallback;
    // placeholder strings
    String meters;
    String kilometers;
    String position;
    // current locale
    Locale cur_locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiCallback = this;
        cur_locale = getResources().getConfiguration().locale;
        initUi();
    }

    /**
     * initialize UI
     */
    private void initUi() {
        txtv_debug = (TextView) findViewById(R.id.txtv_debug);
        et_pilotId = (EditText) findViewById(R.id.et_trackingId);
        txtv_pilotName = (TextView) findViewById(R.id.txtv_pilotName);
        txtv_nearestApt = (TextView) findViewById(R.id.txtv_nearestApt);
        txtv_nearestAptDis = (TextView) findViewById(R.id.txtv_nearestAptDist);
        txtv_pos = (TextView) findViewById(R.id.txtv_pos);
        txtv_alt = (TextView) findViewById(R.id.txtv_alt);
        txtv_elev = (TextView) findViewById(R.id.txtv_elev);
        txtv_hag = (TextView) findViewById(R.id.txtv_hag);
        txtv_pilotId = (TextView) findViewById(R.id.txtv_pilotId);

        meters = getResources().getString(R.string.meters);
        kilometers = getResources().getString(R.string.kilometers);
        position = getResources().getString(R.string.position);
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
            txtv_debug.setText(data.toString());
            try {
                txtv_pilotName.setText(data.getJSONObject("pilot").getString("name"));
                txtv_pilotId.setText(format(cur_locale, "%d", data.getJSONObject("pilot").getInt("id")));
                txtv_nearestApt.setText(data.getJSONObject("nearestAirport").getString("name"));
                txtv_nearestAptDis.setText(format(kilometers, data.getDouble("nearestAirportDistance")/1000));
                txtv_pos.setText(format(position, data.getJSONArray("location").getDouble(1),
                        data.getJSONArray("location").getDouble(0)));
                txtv_alt.setText(format(meters, data.getInt("altitude")));
                txtv_elev.setText(format(meters, data.getInt("elevation")));
                txtv_hag.setText(format(meters, data.getInt("altitude") - data.getInt("elevation")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_pilotIdNF), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * work with data from callback, triggered when callback is called by ApiRequest's async task
     * @param data pilot's track data as JSONObject
     */
    public void callback(JSONObject data) {
        Log.i("MainActivity:", "callback!");
        updateUI(data);
    }
}
