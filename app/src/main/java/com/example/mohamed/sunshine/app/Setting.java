package com.example.mohamed.sunshine.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by mohamed on 13/10/16.
 */

public class Setting extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

Button btn;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_genral);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.locationkey)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.unitskey)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.dayskey)));



    }
    private void bindPreferenceSummaryToValue(Preference preference){

        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,getPreferenceManager().getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String valuestring=newValue.toString();
        if(preference instanceof ListPreference){
            ListPreference listPreference=(ListPreference)preference;
            int prefindx=listPreference.findIndexOfValue(valuestring);
            if(prefindx>=0){
                preference.setSummary(listPreference.getEntries()[prefindx]);
            }
            else {
                preference.setSummary(valuestring);
            }
        }


        return true;
    }
}
