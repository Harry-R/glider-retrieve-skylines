package de.lrapp.skylinestrackingtesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ApiCallback{

    // UI
    private TextView txtv_debug;
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
    }

    /**
     * on click listener for update button
     * @param view view the function was called from
     */
    public void clickBtnUpdate(View view) {
        ApiRequest.liveTrackData(42, apiCallback);
    }

    /**
     * work with data from callback, triggered wenn callback is called by ApiRequest's async task
     * @param data
     */
    public void callback(String data) {
        Log.i("MAinActivity:", "callback!");
        txtv_debug.setText(data);
    }
}
