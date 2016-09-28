package ua.nikiforov.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author snikiforov
 */
public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    private static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";



    @Override
    protected Void doInBackground(String... params) {
//        checkNotNull(params, "param");
//        checkArgument(params.length == 1, "wrong params size");
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri uri = Uri.parse(FORECAST_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter("mode", "json")
                    .appendQueryParameter("q", params[0])
                    .appendQueryParameter("units", "metrics")
                    .appendQueryParameter("cnt", "7")
                    .appendQueryParameter("APPID", "158b4bc53e61603b88d2d7c203901ccf")
                    .build();
            URL url = new URL(uri.toString());
            Log.d(LOG_TAG, "URL: " + url.toString());
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            forecastJsonStr = buffer.toString();
            Log.d(LOG_TAG, "Received response from WeatherService: " + forecastJsonStr);
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
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return null;
    }
}
