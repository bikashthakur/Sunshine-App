package com.sunshine.android.sunshinetheweatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sunshine.android.sunshinetheweatherapp.data.SunshinePreferences;
import com.sunshine.android.sunshinetheweatherapp.utilities.NetworkUtils;
import com.sunshine.android.sunshinetheweatherapp.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView mWeatherDataTextView;
    TextView mErrorMessageTextView;
    ProgressBar mWeatherDataLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        mWeatherDataTextView = (TextView) findViewById(R.id.tv_weather_data);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mWeatherDataLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_weather_data);
        loadWeatherData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        if (selectedItemId == R.id.action_refresh) {
            loadWeatherData();
        }
        return true;
    }

    private void loadWeatherData() {
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        URL url = NetworkUtils.buildUrl(location);
        new FecthWeatherDataTask().execute(url);
    }

    private class FecthWeatherDataTask extends AsyncTask<URL, Void, String[]> {

        @Override
        protected void onPreExecute() {
            mWeatherDataLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(URL... urls) {
            URL url = urls[0];
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                String[] parsedJsonResponse = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, jsonResponse);
                return parsedJsonResponse;
            } catch (IOException e) {
                Log.d("doInBackground IOExp", e.getMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.d("doInBackground JSONExp", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            mWeatherDataLoadingProgressBar.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                showWeatherDataView();
                for (String data : weatherData) {
                    mWeatherDataTextView.append(data + "\n\n\n");
                }
            } else {
                showErrorMessage();
            }
        }
    }

    private void showWeatherDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mWeatherDataTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mWeatherDataTextView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }
}
