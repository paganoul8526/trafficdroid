package it.localhost.trafficdroid.activity;

import it.localhost.trafficdroid.R;
import it.localhost.trafficdroid.common.Utility;
import it.localhost.trafficdroid.fragment.QuizDialogFragment;
import it.localhost.trafficdroid.fragment.WebviewDialogFragment;
import it.localhost.trafficdroid.service.TdListener;
import it.localhost.trafficdroid.service.TdService;
import it.localhost.trafficdroid.tabFragment.BolloFragment;
import it.localhost.trafficdroid.tabFragment.MainFragment;
import it.localhost.trafficdroid.tabFragment.PatenteFragment;
import it.localhost.trafficdroid.tabFragment.PedaggioFragment;
import it.localhost.trafficdroid.tabFragment.PreferencesFragment;
import it.localhost.trafficdroid.tabFragment.VideoFragment;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.android.vending.billing.IInAppBillingService;
import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class MainActivity extends Activity { // NO_UCD
	private static final String ALCOL_URL = "http://voti.kataweb.it/etilometro/index.php";
	private static final String IN_APP_BILLING_SERVICE = "com.android.vending.billing.InAppBillingService.BIND";
	public static final String EVENT_CAT_WEBCAM = "Webcam";
	public static final String EVENT_CAT_BADNEWS = "BadNews";
	public static final String EVENT_CAT_IAB = "InAppBilling";
	public static final String EVENT_CAT_GRAPH = "Graph";
	public static final String EVENT_ACTION_REQUEST = "Request";
	public static final String EVENT_ACTION_OPEN = "Open";
	public static final String EVENT_ACTION_NONE = "None";
	public static final String EVENT_ACTION_LAUNCHPURCHASEFLOW = "LaunchPurchaseFlow";
	private static final String ITEM_TYPE_INAPP = "inapp";
	public static final String SKU_AD_FREE = "ad_free";
	public static final String SKU_QUIZ_FREE = "quiz_free";
	public static final String SKU_INTERSTITIAL_FREE = "interstitial_free";
	private static final String RESPONSE_CODE = "RESPONSE_CODE";
	private static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
	private static final String RESPONSE_INAPP_PURCHASE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
	private static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
	private static final String RESPONSE_INAPP_DATA_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
	private static final String RESPONSE_INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";
	private static final String KEY_FACTORY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	private static final String RSA_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgg7ckVCx3779Q4Dq99wMFYlwS4+jmbrTtBjLzG2cL4xoz6pZhLct4vIL2sfhA588Vfp4vRDHaIN3lpiCOGIxWRxI3krOoF+n1G/F9kUdiGaK4hYMPYa41MPbG6wc9tWJgcGe0PdYExCmeIvFiQrc4HU63J9zN+C1HRqw1t91YC2vzyZFxNLoIp3kcoz6rBCopm4GA01ZPZrP5RTR2hiJLrDVJl5mzuDrl7yoMq6OQ1SasVaWkgN7yTDyh9Df9hv5FsE8haVFddSJfTEh4BFZcFSW+17xgeImNtCgDtQ/GuTG3FIOiOotugIa1OjKC4z5zbZFl8Zz+cz8fFOxzfLkTQIDAQAB";
	private IInAppBillingService inAppBillingService;
	private ServiceConnection serviceConnection;
	private BroadcastReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// android.os.StrictMode.setThreadPolicy(new
		// android.os.StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
		// android.os.StrictMode.setVmPolicy(new
		// android.os.StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
		serviceConnection = new TdServiceConnection();
		bindService(new Intent(IN_APP_BILLING_SERVICE), serviceConnection, Context.BIND_AUTO_CREATE);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(false);
		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.addTab(bar.newTab().setText(R.string.app_name).setTabListener(new MainFragment()));
		bar.addTab(bar.newTab().setText(R.string.anasTv).setTabListener(new VideoFragment()));
		bar.addTab(bar.newTab().setText(R.string.pedaggio).setTabListener(new PedaggioFragment()));
		bar.addTab(bar.newTab().setText(R.string.patente).setTabListener(new PatenteFragment()));
		bar.addTab(bar.newTab().setText(R.string.bollo).setTabListener(new BolloFragment()));
		bar.addTab(bar.newTab().setText(R.string.settings).setTabListener(new PreferencesFragment()));
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TdService.beginUpdate);
		intentFilter.addAction(TdService.endUpdate);
		receiver = new UpdateReceiver();
		registerReceiver(receiver, intentFilter);
		WakefulIntentService.scheduleAlarms(new TdListener(), this, false);
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(TdService.notificationId);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_option, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (Utility.isAdFree(this))
			menu.removeItem(R.id.menuAdFree);
		if (Utility.isInterstitialFree(this))
			menu.removeItem(R.id.menuInterstitialFree);
		return true;
	}

	@Override
	public void onBackPressed() {
		if (getActionBar().getSelectedNavigationIndex() == 0)
			super.onBackPressed();
		else
			getActionBar().setSelectedNavigationItem(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuAlcol:
				new WebviewDialogFragment().show(getFragmentManager(), ALCOL_URL, null);
				return true;
			case R.id.menuQuiz:
				new QuizDialogFragment().show(getFragmentManager(), getString(R.string.patenteQuiz));
				return true;
			case R.id.menuRefresh:
				if (!Utility.getProviderTraffic(this).equals(getString(R.string.providerTrafficDefault)))
					new TdListener().sendWakefulWork(this);
				return true;
			case R.id.menuAdFree:
				launchPurchaseFlow(SKU_AD_FREE);
				return true;
			case R.id.menuInterstitialFree:
				launchPurchaseFlow(SKU_INTERSTITIAL_FREE);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private final class UpdateReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(TdService.beginUpdate))
				setProgressBarIndeterminateVisibility(true);
			else if (intent.getAction().equals(TdService.endUpdate))
				setProgressBarIndeterminateVisibility(false);
		}
	}

	public void launchPurchaseFlow(String sku) {
		EasyTracker.getInstance(this).send(MapBuilder.createEvent(EVENT_CAT_IAB, EVENT_ACTION_LAUNCHPURCHASEFLOW, sku, (long) 0).build());
		try {
			Bundle buyIntentBundle = inAppBillingService.getBuyIntent(3, getPackageName(), sku, ITEM_TYPE_INAPP, "");
			if (buyIntentBundle.getInt(RESPONSE_CODE) == 0)
				startIntentSenderForResult(((PendingIntent) buyIntentBundle.getParcelable(RESPONSE_BUY_INTENT)).getIntentSender(), 101010, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class TdServiceConnection implements ServiceConnection {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			inAppBillingService = IInAppBillingService.Stub.asInterface(service);
			try {
				int response = inAppBillingService.isBillingSupported(3, getPackageName(), ITEM_TYPE_INAPP);
				if (response == 0)
					new RetrievePurchasesService().execute();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public class RetrievePurchasesService extends AsyncTask<Void, Void, ArrayList<String>> {
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ArrayList<String> out = new ArrayList<String>();
			String continueToken = null;
			try {
				Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
				sig.initVerify(KeyFactory.getInstance(KEY_FACTORY_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.decode(RSA_PUBKEY, Base64.DEFAULT))));
				do {
					Bundle ownedItems = inAppBillingService.getPurchases(3, getPackageName(), ITEM_TYPE_INAPP, continueToken);
					if (ownedItems.getInt(RESPONSE_CODE) == 0) {
						ArrayList<String> skuList = ownedItems.getStringArrayList(RESPONSE_INAPP_PURCHASE_ITEM_LIST);
						ArrayList<String> purchaseDataList = ownedItems.getStringArrayList(RESPONSE_INAPP_PURCHASE_DATA_LIST);
						ArrayList<String> signatureList = ownedItems.getStringArrayList(RESPONSE_INAPP_DATA_SIGNATURE_LIST);
						for (int i = 0; i < skuList.size(); ++i) {
							sig.update(purchaseDataList.get(i).getBytes());
							if (sig.verify(Base64.decode(signatureList.get(i), Base64.DEFAULT)))
								out.add(skuList.get(i));
						}
					}
					continueToken = ownedItems.getString(RESPONSE_INAPP_CONTINUATION_TOKEN);
				} while (continueToken != null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return out;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			super.onPostExecute(result);
			Utility.setAdFree(MainActivity.this, result.contains(SKU_AD_FREE) ? true : false);
			Utility.setInterstitialFree(MainActivity.this, result.contains(SKU_INTERSTITIAL_FREE) ? true : false);
			View ad = findViewById(R.id.ad);
			if (ad != null)
				ad.setVisibility(result.contains(SKU_AD_FREE) ? View.GONE : View.VISIBLE);
			if (!result.contains(SKU_QUIZ_FREE) && !Utility.getProviderTraffic(MainActivity.this).equals(getString(R.string.providerTrafficDefault)))
				new QuizDialogFragment().show(getFragmentManager(), getString(R.string.patenteQuiz));
			invalidateOptionsMenu();
		}
	}
}