package it.localhost.trafficdroid.activity;

import it.localhost.trafficdroid.common.Const;
import android.app.Activity;
import android.os.Bundle;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public abstract class AbstractActivity extends Activity {
	private static GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker.startNewSession(Const.anlyticsId, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		tracker.trackPageView(this.getClass().getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		tracker.dispatch();
	}

	@Override
	public void onStop() {
		super.onStop();
		tracker.stopSession();
	}
}