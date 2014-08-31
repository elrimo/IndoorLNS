package com.github.elrimo.indoorlns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.esri.core.geometry.Point;
import com.github.elrimo.indoorlns.adapters.WifiAdapter;
import com.github.elrimo.indoorlns.beans.Fingerprint;
import com.github.elrimo.indoorlns.beans.WifiItem;
import com.google.gson.Gson;
import com.savagelook.android.UrlJsonAsyncTask;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class FingerprintActivity extends ActionBarActivity {
	private final static String RSSI_URL = "http://192.168.1.4:81/localisation/rssi/saveFingerprint";
	private static final Gson gson = new Gson();
	private static final String ENCODING_UTF_8 = "UTF-8";
	private Button boutonAnnuler;
	private ListView listeViewWifi;
	private List<WifiItem> listeWifiItem;
	private WifiAdapter wifiAdapter;
	private WifiManager wifiManager;
	private TimerTask mTimerTask;
	private final Handler handler = new Handler();
	private Timer t = new Timer();
	private Button hButtonStop;
	private Map<String, List<Integer>> lstrssi;
	private Map<String, String> lstAP;
	private List<WifiItem> fingerprints = new ArrayList<WifiItem>();
	Point position;
	private Fingerprint fgr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fingerprint);
		Bundle bundle = getIntent().getExtras();
		position = (Point) bundle.getSerializable("position");
		System.out.println(position.toString());

		lstrssi = new HashMap<String, List<Integer>>();
		lstAP = new HashMap<String, String>();
		lstrssi.clear();
		lstAP.clear();
		doTimerTask();
		
		hButtonStop = (Button) findViewById(R.id.idButtonStop);
		hButtonStop.setOnClickListener(mButtonStopListener);

		boutonAnnuler = (Button) findViewById(R.id.idAnnuler);
		boutonAnnuler.setOnClickListener(mButtonConcelListener);
		
	}

	View.OnClickListener mButtonConcelListener = new OnClickListener() {
		public void onClick(View v) {
			Intent mainPage = new Intent(FingerprintActivity.this, AdminMapActivity.class);
			startActivity(mainPage);
			finish();
		}
	};

	View.OnClickListener mButtonStopListener = new OnClickListener() {
		public void onClick(View v) {
			stopTask();

		}
	};

	public void doTimerTask() {

		mTimerTask = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {

						wifiscan();

					}
				});
			}
		};

		// public void schedule (TimerTask task, long delay, long period)
		t.schedule(mTimerTask, 1000, 3000); //

	}

	public void stopTask() {

		if (mTimerTask != null) {

			mTimerTask.cancel();

			Log.d("TIMER", "timer canceled");

			if (lstrssi.size() > 0) {
				Log.d("test", "enfin j'ai trouvé");
				System.out.println(lstrssi.size());
				calculeFingerprint();
				saveFingerprints();

			}

		}

	}

	void wifiscan() {
		listeViewWifi = (ListView) findViewById(R.id.listViewWifi);
		if (wifiManager != null)
			wifiManager.startScan();
		// On récupère le service WiFi d'Android
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

		// Gestion de la liste des AP WiFi (voir tuto sur les adapters et les
		// listviews)
		listeWifiItem = new ArrayList<WifiItem>();
		wifiAdapter = new WifiAdapter(this, listeWifiItem);
		listeViewWifi.setAdapter(wifiAdapter);

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
					List<Integer> lst = new ArrayList<>();
					if (lstAP.containsKey(item.getAdresseMac())) {

						lst = lstrssi.get(item.getAdresseMac());
						lst.add(item.getForceSignal());
						lstrssi.remove(item.getAdresseMac());
						lstrssi.put(item.getAdresseMac(), lst);

					} else {
						lstAP.put(item.getAdresseMac(), item.getAPName());
						lst.add(item.getForceSignal());
						lstrssi.put(item.getAdresseMac(), lst);
					}

				}

				// On rafraichit la liste
				wifiAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(this, "Vous devez activer votre WiFi", Toast.LENGTH_SHORT).show();;
			}
		}

	}

	void calculeFingerprint() {

		fingerprints.clear();
		for (Map.Entry<String, String> entry : lstAP.entrySet()) {
			WifiItem item = new WifiItem();
			int rss = 0;
			for (int i : lstrssi.get(entry.getKey())) {
				rss = rss + i;
			}
			rss = rss / lstrssi.get(entry.getKey()).size();
			item.setAdresseMac(entry.getKey());
			item.setAPName(entry.getValue());
			System.out.println(item.getAPName());
			item.setForceSignal(rss);
			fingerprints.add(item);
		}
		System.out.println(fingerprints.size());
		fgr = new Fingerprint();
		fgr.setAbscisse(position.getX());
		fgr.setOrdonnee(position.getY());
		fgr.setRSSi(fingerprints);

	}

	// On arrête le timer quand on met en pause l'application
	@Override
	protected void onPause() {
		if (mTimerTask != null)
			mTimerTask.cancel();

		super.onPause();
	}

	private void saveFingerprints() {
		FingerprintTask fingerprintTask = new FingerprintTask(
				FingerprintActivity.this);
		fingerprintTask.setMessageLoading(" save fingerprints...");
		fingerprintTask.execute(RSSI_URL);

	}

	private class FingerprintTask extends UrlJsonAsyncTask {
		public FingerprintTask(Context context) {
			super(context);
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
					StringEntity se = new StringEntity(gson.toJson(fgr),
							ENCODING_UTF_8);
					post.setEntity(se);
					post.setHeader("Accept", "application/json");
					post.setHeader("Content-Type", "application/json");
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = client.execute(post, responseHandler);
					json = new JSONObject(response);

				} catch (HttpResponseException e) {
					e.printStackTrace();
					Log.e("ClientProtocol", "" + e);
					json.put("info",
							"Error. Retry!");
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
				Toast.makeText(context, json.getString("info"),
						Toast.LENGTH_LONG).show();
				if (json.getBoolean("success")) {
					Intent intent = new Intent(FingerprintActivity.this,
							AdminMapActivity.class);
					startActivity(intent);
					finish();

				}

			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

}
