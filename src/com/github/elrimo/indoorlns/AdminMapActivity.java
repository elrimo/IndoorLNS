package com.github.elrimo.indoorlns;

import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.ogc.WMSLayer;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class AdminMapActivity extends Activity {

	MapView mMapView;
	Point p;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_map);

		WMSLayer space = new WMSLayer(
				"http://192.168.1.4:8090/geoserver/workspace/wms?service=WMS&request=GetMap"
						+ "&layers=workspace:indoorspace&bbox=-100.0,-100.0,5000.0,2500.0"
						+ "&width=647&height=330&srs=EPSG:2001&format=image/png",
				SpatialReference.create(2001));
		WMSLayer mur = new WMSLayer(
				"http://192.168.1.4:8090/geoserver/workspace/wms?service=WMS&request=GetMap"
						+ "&layers=workspace:object&bbox=-100.0,-100.0,5000.0,2500.0"
						+ "&width=647&height=330&srs=EPSG:2001&format=image/png",
				SpatialReference.create(2001));
		WMSLayer fingerprint = new WMSLayer(
				"http://192.168.1.4:8090/geoserver/workspace/wms?service=WMS&request=GetMap"
						+ "&layers=workspace:fingerprints&bbox=-100.0,-100.0,5000.0,2500.0"
						+ "&width=647&height=330&srs=EPSG:2001&format=image/png",
				SpatialReference.create(2001));

		// Retrieve the map and initial extent from XML layout
		mMapView = (MapView) findViewById(R.id.mapadmin_view);
		// Add dynamic layer to MapView
		mMapView.addLayer(space);
		mMapView.addLayer(mur);
		mMapView.addLayer(fingerprint);
		mMapView.setOnSingleTapListener(new OnSingleTapListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSingleTap(float x, float y) {
				// SpatialReference sp = SpatialReference.create(2001);
				p = mMapView.toMapPoint(x, y);
				// Point aux = (Point) GeometryEngine.project(p,
				// mapView.getSpatialReference(), sp);
				Log.d(this.getClass().getSimpleName(), "X= " + p.getX()
						+ ", Y= " + p.getY());

				AlertDialog.Builder builder = new AlertDialog.Builder(
						AdminMapActivity.this);
				builder.setCancelable(false)
						.setTitle(R.string.fingerprint_title)
						.setMessage(
								String.format(
										getString(R.string.fingerprint_msg),
										p.getX(), p.getY()))
						.setPositiveButton(R.string.calculate,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												AdminMapActivity.this,
												FingerprintActivity.class);
										intent.putExtra("position", p);
										// On démarre l'activity
										startActivity(intent);
										// On ferme l'activity en cours
										finish();
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});

				builder.create().show();
			}
		});

	}

	protected void onPause() {
		super.onPause();
		mMapView.pause();
	}

	protected void onResume() {
		super.onResume();
		mMapView.unpause();
	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.admin_map, menu);
	    return super.onCreateOptionsMenu(menu);
	   
	  } 
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	 	    // action with ID action_settings was selected
	    case R.id.action_signout:
	    	Intent intent = new Intent(AdminMapActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
	      break;
	    default:
	      break;
	    }

	    return true;
	  } 

}