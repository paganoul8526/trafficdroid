package it.localhost.trafficdroid.gui;

import it.localhost.trafficdroid.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TDPreferenceActivity extends PreferenceActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
	}
}