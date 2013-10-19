package it.localhost.trafficdroid.tabFragment;

import it.localhost.trafficdroid.R;
import it.localhost.trafficdroid.common.AutoFocusTextWatcher;
import it.localhost.trafficdroid.common.TdAdListener;
import it.localhost.trafficdroid.common.Utility;
import it.localhost.trafficdroid.fragment.WebviewDialogFragment;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.ads.AdRequest;
import com.google.ads.InterstitialAd;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class BolloFragment extends Fragment implements TabListener {
	private static final String bolloUrl = "https://servizi.aci.it/Bollonet/calcolo.do?LinguaSelezionata=ita&CodiceServizio=2&TipoVeicolo=";
	private static final String param1 = "&RegioneResidenza=";
	private static final String param2 = "&Targa=";
	private EditText targaA, targaB, targaC;
	private Spinner tipoVeicolo, regioneResidenza;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.bollo, null);
		targaA = (EditText) v.findViewById(R.id.targaA);
		targaB = (EditText) v.findViewById(R.id.targaB);
		targaC = (EditText) v.findViewById(R.id.targaC);
		new AutoFocusTextWatcher(targaA, 2);
		new AutoFocusTextWatcher(targaB, 3);
		new AutoFocusTextWatcher(targaC, 2);
		tipoVeicolo = (Spinner) v.findViewById(R.id.tipoVeicolo);
		regioneResidenza = (Spinner) v.findViewById(R.id.regioneResidenza);
		getActivity().setProgressBarIndeterminateVisibility(false);
		v.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String targa = targaA.getText().toString() + targaB.getText().toString() + targaC.getText().toString();
				String tipo = getResources().getStringArray(R.array.tipoVeicoloKey)[tipoVeicolo.getSelectedItemPosition()];
				String regione = getResources().getStringArray(R.array.regioneResidenzaKey)[regioneResidenza.getSelectedItemPosition()];
				new WebviewDialogFragment().show(getFragmentManager(), bolloUrl + tipo + param1 + regione + param2 + targa, null);
			}
		});
		if (!Utility.isInterstitialFree(getActivity())) {
			InterstitialAd interstitial = new InterstitialAd(getActivity(), getString(R.string.adUnitId));
			interstitial.setAdListener(new TdAdListener());
			interstitial.loadAd(new AdRequest());
		}
		EasyTracker.getInstance(getActivity()).send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, BolloFragment.class.getSimpleName()).build());
		return v;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.replace(android.R.id.content, this);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
