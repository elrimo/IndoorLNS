package com.github.elrimo.indoorlns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.ogc.WMSLayer;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.github.elrimo.indoorlns.beans.LocalizationCallback;
import com.github.elrimo.indoorlns.beans.WifiItem;
import com.google.gson.Gson;
import com.savagelook.android.UrlJsonAsyncTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class LocalizationFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private LocationManager myLocationManager;
	private String PROVIDER = LocationManager.NETWORK_PROVIDER;
	private final static String RSSI_URL = "http://192.168.1.4:81/localisation/rssi/fingerprintlocalization";
	private WifiManager wifiManager;
	private List<WifiItem> listeWifiItem;
	private static final String ENCODING_UTF_8 = "UTF-8";
	private static final Gson gson = new Gson();

	private static final String ARG_SECTION_NUMBER = "section_number";

	private MapView mapView;

	private ProgressDialog progressDialog;

	private TimerTask mTimerTask;
	private final Handler handler = new Handler();
	private Timer t = new Timer();
	private GraphicsLayer graphicsLayer;

	private Point location;
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static LocalizationFragment newInstance(int sectionNumber) {
		LocalizationFragment fragment = new LocalizationFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public LocalizationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		mapView = (MapView) rootView.findViewById(R.id.section_view);
		
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage(getActivity().getString(R.string.loading));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		// Load the localization map
		progressDialog.show();
		// Config our map view
		configMapView(mapView);
		mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
			private static final long serialVersionUID = -5753742033861061658L;
			@Override
			public void onStatusChanged(Object arg0, STATUS arg1) {
				// Hide the progress dialog
				progressDialog.hide();
				myLocationManager = (LocationManager) getActivity().getSystemService(
						getActivity().LOCATION_SERVICE);
				// get last known location, if available
				if (wifiManager != null)
					wifiManager.startScan();
				// On récupère le service WiFi d'Android
				wifiManager = (WifiManager) getActivity().getSystemService(
						Context.WIFI_SERVICE);
				mTimerTask = new TimerTask() {
				public void run() {

					handler.post(new Runnable() {
						public void run() {
							location=getLocation();
						}
					});
				}
			};
			t.schedule(mTimerTask, 90000, 80000);
			showLocation(location);

			}
		});				

		return rootView;
	}

	private LocationListener myLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			//showLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mapView != null) {
			// cwv.handleDestroy();
			// mapView.destroyDrawingCache();
		}
		if (mTimerTask != null)
			mTimerTask.cancel();
		;
	}

	@Override
	public void onPause() {
		if (mTimerTask != null)
			mTimerTask.cancel();
		super.onPause();
	}

	private void configMapView(final MapView mapView) {
		mapView.setAllowRotationByPinch(true);

		WMSLayer wmsLayer = new WMSLayer(
				"http://192.168.1.4:8090/geoserver/workspace/wms?service=WMS&request=GetMap"
						+ "&layers=indoormap&bbox=-100.0,-100.0,5000.0,2500.0"
						+ "&width=647&height=330&srs=EPSG:2001&format=image/png",
				SpatialReference.create(2001));
		mapView.addLayer(wmsLayer);

	}

	public void showLocation(Point location) {
		// Hide the progress dialog
		if(location==null){
			Toast toast = Toast.makeText(getActivity(),
					"Can not localize user",
					Toast.LENGTH_LONG);
			
		}
		progressDialog.hide();
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.loc6);
		PictureMarkerSymbol pictureMarker = new PictureMarkerSymbol(drawable);
		graphicsLayer = new GraphicsLayer();
		// create a graphic with the geometry and marker symbol
		Graphic pointGraphic = new Graphic(location, pictureMarker);
		// add the graphic to the graphics layer
		graphicsLayer.addGraphic(pointGraphic);
		mapView.zoomToResolution(location, 2);
		mapView.addLayer(graphicsLayer);
	}

	public Point getLocation() {

		// Gestion de la liste des AP WiFi (voir
		// tuto sur les adapters et les //listviews)
		listeWifiItem = new ArrayList<WifiItem>();
		if (wifiManager != null) {
			// On vérifie que le WiFi est allumé
			if (wifiManager.isWifiEnabled()) {
				// On récupère les scans
				List<ScanResult> listeScan = wifiManager.getScanResults();
				// On vide notre liste
				listeWifiItem.clear();
				// Pour chaque scan
				for (ScanResult scanResult : listeScan) {
					WifiItem item = new WifiItem();
					item.setAdresseMac(scanResult.BSSID);
					item.setAPName(scanResult.SSID);
					item.setForceSignal(scanResult.level);
					Log.d("FormationWifi", scanResult.SSID + " LEVEL "
							+ scanResult.level);
					listeWifiItem.add(item);
					new LocalizationTask(getActivity(),
							new LocalizationCallback() {

								@Override
								public void onFailure() {
									// update UI with notice
									// that no results were
									// found
									Toast toast = Toast.makeText(getActivity(),
											"No result found.",
											Toast.LENGTH_LONG);
									toast.show();
									location=null;

								}

								@Override
								public void onSuccess() {
									location=new Point(470, 1560);

								}
							}).execute(RSSI_URL);
				}
			}
		}
		return location;

	}

	private class LocalizationTask extends UrlJsonAsyncTask {
		private LocalizationCallback callback;

		public LocalizationTask(Context context, LocalizationCallback callback) {
			super(context);
			this.callback = callback;
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urls[0]);
			String response = null;
			JSONObject json = new JSONObject();
			try {
				try {
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");
					StringEntity se = new StringEntity(
							gson.toJson(listeWifiItem), ENCODING_UTF_8);
					post.setEntity(se);
					post.setHeader("Accept", "application/json");
					post.setHeader("Content-Type", "application/json");
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = client.execute(post, responseHandler);
					json = new JSONObject(response);
				} catch (HttpResponseException e) {
					e.printStackTrace();
					Log.e("ClientProtocol", "" + e);
					json.put("info", "Error. Retry!");
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("IO", "" + e);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSON", "" + e);
			}
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				// on SUccess
				callback.onSuccess();
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
				// on failure
				callback.onFailure();
			} finally {
				super.onPostExecute(json);
			}
		}

	}

}
