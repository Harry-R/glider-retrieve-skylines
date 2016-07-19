package de.lrapp.skylinestrackingtesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ApiCallback{

    // UI
    private TextView txtv_debug;
    private EditText et_pilotId;
    // API
    ApiCallback apiCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiCallback = this;
        initUi();
    }

    /**
     * initialize UI
     */
    private void initUi() {
        txtv_debug = (TextView) findViewById(R.id.txtv_debug);
        et_pilotId = (EditText) findViewById(R.id.et_trackingId);
    }

    /**
     * on click listener for update button
     * @param view view the function was called from
     */
    public void clickBtnUpdate(View view) {
        int pilotID = Integer.parseInt(et_pilotId.getText().toString());
        ApiRequest.liveTrackData(pilotID, apiCallback);
    }

    /**
     * work with data from callback, triggered wenn callback is called by ApiRequest's async task
     * @param data pilot's track data as JSONObject
     */
    public void callback(JSONObject data) {
        Log.i("MAinActivity:", "callback!");
        txtv_debug.setText(data.toString());
    }
}
