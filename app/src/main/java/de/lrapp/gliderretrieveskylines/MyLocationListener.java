package de.lrapp.gliderretrieveskylines;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * implements location listener interface,
 * see official LocationListener documentation for further details
 */
public class MyLocationListener implements LocationListener {

    // the retriever's GPS location
    private Location location;

    /**
     * getter for private location var
     * @return the retriever's GPS location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * is called on location change, updates location variable with new location and writes to log
     * @param loc changed location
     */
    @Override
    public void onLocationChanged(Location loc) {

        location = loc;
        Log.i(TAG, "Latitude: " + loc.getLatitude() + "\t Longitude: " + loc.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}