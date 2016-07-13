package de.lrapp.skylinestrackingtesting;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ApiRequest {


    /**
     * Coordinates the api request
     * @return live track data as string
     */
    static void liveTrackData(int pilotId, ApiCallback apiCallback) {
        try {
            URL url = new URL("https://skylines.aero/tracking");

            new RequestTask(url, apiCallback).execute();
            // TODO: return as json and filter for requested pilot id
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Establishes a http connection, calls readStream
     * @return see read stream
     * @param url url to connect to
     * @throws IOException
     */
    protected static String connect(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept", "application/json");
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Reads an InputStream and converts it to a String.
     * @param stream the InputStream
     * @return string from the stream
     * @throws IOException
     */
    protected static String readStream(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        Log.i("readStream:", total.toString());
        return total.toString();
    }


    private static class RequestTask extends AsyncTask<Void, Void, String> {
        private URL url;
        private ApiCallback apiCallback;
        /**
         * Constructor
         * @param url url to connect to
         */
        private RequestTask(URL url, ApiCallback apiCallback) {
            this.url = url;
            this.apiCallback = apiCallback;
        }
        @Override
        protected String doInBackground(Void... voids) {

            try {
                return connect(url);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i("result: ", result);
            if (result != null) {
               apiCallback.callback(result);

            }
        }
    }

}
