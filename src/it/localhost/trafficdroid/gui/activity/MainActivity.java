package it.localhost.trafficdroid.gui.activity;

import it.localhost.trafficdroid.R;
import it.localhost.trafficdroid.common.Const;
import it.localhost.trafficdroid.common.TdException;
import it.localhost.trafficdroid.dao.TrafficDAO;
import it.localhost.trafficdroid.dto.DLCTaskDTO;
import it.localhost.trafficdroid.dto.StreetDTO;
import it.localhost.trafficdroid.gui.ZoneListAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity {
	private DLCTaskDTO dlctask;
	private ListView zoneView;
	private TextView leftTextView;
	private TextView rightTextView;
	private TextView centerTextView;
	private Spinner spinner;
	private ArrayAdapter<StreetDTO> arrayAdapter;
	private SharedPreferences sharedPreferences;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Const.BEGIN_UPDATE)) {
				setProgressBarIndeterminateVisibility(true);
			} else if (intent.getAction().equals(Const.END_UPDATE)) {
				setProgressBarIndeterminateVisibility(false);
				refreshgui(); // nuovi dati da visualizzare
			}
		}
	};
	private IntentFilter intentFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("ACT", "onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		leftTextView = (TextView) findViewById(R.id.left);
		rightTextView = (TextView) findViewById(R.id.right);
		centerTextView = (TextView) findViewById(R.id.center);
		zoneView = (ListView) findViewById(R.id.zonelist);
		spinner = (Spinner) findViewById(R.id.spinner);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				viewStreet();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		intentFilter = new IntentFilter();
		intentFilter.addAction(Const.BEGIN_UPDATE);
		intentFilter.addAction(Const.END_UPDATE);
	}

	@Override
	public void onResume() {
		Log.e("ACT", "onResume");
		super.onResume();
		String url = sharedPreferences.getString(getResources().getString(R.string.urlKey), Const.emptyString);
		if (url.equals(Const.emptyString) || url.equals(getResources().getString(R.string.urlDefaultValue)))
			new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.warning)).setPositiveButton(getResources().getString(R.string.ok), null)
					.setMessage(getResources().getString(R.string.badConf)).show();
		else
			refreshgui();
		registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	private void refreshgui() {
		Log.e("ACT", "refreshgui");
		try {
			dlctask = TrafficDAO.retrieveData(this);
			arrayAdapter = new ArrayAdapter<StreetDTO>(MainActivity.this, android.R.layout.simple_spinner_item, dlctask.getStreets());
			arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(arrayAdapter);
			viewStreet();
		} catch (TdException e) {
			e.printStackTrace();
		}
	}
	
	public void viewStreet() {
		if (dlctask.getStreets().size() > 0) {
			zoneView.setAdapter(new ZoneListAdapter(this, dlctask.getStreets().get(spinner.getSelectedItemPosition()).getZones()));
			leftTextView.setText(dlctask.getStreets().get(spinner.getSelectedItemPosition()).getDirectionLeft());
			rightTextView.setText(dlctask.getStreets().get(spinner.getSelectedItemPosition()).getDirectionRight());
			if (dlctask.getTrafficTime() != null) {
				// centerTextView.setText(DateFormat.getTimeFormat(this).format(dlctask.getTrafficTime()));
				centerTextView.setText(new java.text.SimpleDateFormat("H:mm:ss").format(dlctask.getTrafficTime()));
			}
		} else {
			zoneView.setAdapter(null);
			leftTextView.setText(null);
			rightTextView.setText(null);
			centerTextView.setText(null);
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem m1 = menu.add(0, Const.menuSettings, Menu.NONE, R.string.settings);
		MenuItem m2 = menu.add(0, Const.menuRefresh, Menu.NONE, R.string.refresh);
		m1.setIcon(android.R.drawable.ic_menu_preferences);
		m2.setIcon(android.R.drawable.ic_menu_rotate);
		m1.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
				return true;
			}
		});
		m2.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				sendBroadcast(Const.doUpdateIntent);
				return true;
			}
		});
		return true;
	}
}