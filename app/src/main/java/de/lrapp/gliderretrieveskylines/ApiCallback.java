package de.lrapp.gliderretrieveskylines;

import org.json.JSONObject;

public interface ApiCallback {
    /**
     * Called by the call-Method when request finished.
     * @param result Data returned by the API as String
     */
    void callback(JSONObject result);
}
