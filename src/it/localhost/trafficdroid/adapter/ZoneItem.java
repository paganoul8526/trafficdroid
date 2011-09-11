package it.localhost.trafficdroid.adapter;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import it.localhost.trafficdroid.R;
import it.localhost.trafficdroid.activity.WebViewActivity;
import it.localhost.trafficdroid.common.Const;
import it.localhost.trafficdroid.dto.ZoneDTO;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ZoneItem extends AbstractItem {
	public ZoneDTO zoneDTO;

	public ZoneItem(Context context, ZoneDTO zoneDTO) {
		super(context);
		this.zoneDTO = zoneDTO;
	}

	public int getType() {
		return Const.itemTypes[2];
	}

	public View inflateView() {
		return inflater.inflate(R.layout.main_item_zone, null, false);
	}

	public void fillView(View view) {
		super.fillView(view);
		TextView zoneNameText = (TextView) view.findViewById(R.id.zoneName);
		TextView zoneKmText = (TextView) view.findViewById(R.id.zoneKm);
		TextView leftZoneSpeedText = (TextView) view.findViewById(R.id.zoneSpeedLeft);
		TextView rightZoneSpeedText = (TextView) view.findViewById(R.id.zoneSpeedRight);
		ImageView trendLeftText = (ImageView) view.findViewById(R.id.trendLeft);
		ImageView trendRightText = (ImageView) view.findViewById(R.id.trendRight);
		zoneNameText.setText(zoneDTO.getName());
		zoneKmText.setText(zoneDTO.getKm());
		if (zoneDTO.getTrendLeft() != 0)
			trendLeftText.setImageResource(zoneDTO.getTrendLeft());
		if (zoneDTO.getTrendRight() != 0)
			trendRightText.setImageResource(zoneDTO.getTrendRight());
		if (zoneDTO.getSpeedLeft() != 0)
			leftZoneSpeedText.setText(Byte.toString(zoneDTO.getSpeedLeft()));
		else
			leftZoneSpeedText.setText("-");
		if (zoneDTO.getSpeedRight() != 0)
			rightZoneSpeedText.setText(Byte.toString(zoneDTO.getSpeedRight()));
		else
			rightZoneSpeedText.setText("-");
		leftZoneSpeedText.setTextColor(Const.colorCat[zoneDTO.getCatLeft()]);
		rightZoneSpeedText.setTextColor(Const.colorCat[zoneDTO.getCatRight()]);
		leftZoneSpeedText.setTypeface(zoneDTO.getCatLeft() == 1 ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
		rightZoneSpeedText.setTypeface(zoneDTO.getCatRight() == 1 ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
		ImageView cam = (ImageView) view.findViewById(R.id.zoneCam);
		if (zoneDTO.getId().charAt(0) == Const.webcamTrue)
			cam.setImageResource(android.R.drawable.ic_menu_camera);
		else if (zoneDTO.getId().charAt(0) == Const.webcamNone)
			cam.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
		else
			cam.setImageResource(android.R.drawable.ic_menu_add);
	}

	public void onClick() {
		String code = zoneDTO.getId();
		if (code.charAt(0) == Const.webcamNone) {
			GoogleAnalyticsTracker.getInstance().trackEvent(Const.eventCatWebcam, Const.eventActionNone, code, 0);
			new AlertDialog.Builder(context).setTitle(R.string.info).setPositiveButton(R.string.ok, null).setMessage(R.string.webcamNone).show();
		} else if (code.charAt(0) == Const.webcamTrue) {
			GoogleAnalyticsTracker.getInstance().trackEvent(Const.eventCatWebcam, Const.eventActionOpen, code, 0);
			Intent intent = new Intent(context, WebViewActivity.class);
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
			String provider = sharedPreferences.getString(context.getString(R.string.providerCamKey), context.getString(R.string.providerCamDefault));
			String url = Const.http + provider + Const.popupTelecamera + Const.decodeCam(code);
			intent.putExtra(Const.url, url);
			context.startActivity(intent);
		} else {
			GoogleAnalyticsTracker.getInstance().trackEvent(Const.eventCatWebcam, Const.eventActionRequest, code, 0);
			new AlertDialog.Builder(context).setTitle(R.string.info).setPositiveButton(R.string.ok, null).setMessage(R.string.webcamAdd).show();
		}
	}
}