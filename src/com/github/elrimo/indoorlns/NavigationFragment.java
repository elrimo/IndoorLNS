package com.github.elrimo.indoorlns;

import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.ogc.WMSLayer;
import com.esri.core.geometry.SpatialReference;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NavigationFragment extends Fragment {

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private ListView placesListView;

	private static final String ARG_SECTION_NUMBER = "section_number";


	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static NavigationFragment newInstance(int sectionNumber) {
		NavigationFragment fragment = new NavigationFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public NavigationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nav, container,
				false);

		placesListView = (ListView) rootView
				.findViewById(R.id.places_list_view);
		// Create and populate a List of destination names.
		// Create and populate a List of planet names.
		String[] destination = new String[] { "Ascensseur", "Bureau PDG",
				"escalier", "Réception", "Salle de reunion", "Open Space",
				"R&D", "Mousala", "WCF", "WCH" , "Bureau 101", "Bureau 102", "Bureau 103",
				"Bureau 104", "Bureau 105", "Bureau 106",
				"Bureau 107",  "Bureau 108", "Bureau 109", "Bureau 110",
				"Bureau 111" };
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
	              android.R.layout.simple_list_item_1, android.R.id.text1, destination);
	    
	    
	            // Assign adapter to ListView
		 placesListView.setAdapter(adapter); 
	            
	            // ListView Item Click Listener
		 placesListView.setOnItemClickListener(new OnItemClickListener() {
	 
	                  @Override
	                  public void onItemClick(AdapterView<?> parent, View view,
	                     int position, long id) {
	                    
	                   // ListView Clicked item index
	                   int itemPosition     = position;
	                   
	                   // ListView Clicked item value
	                   String  itemValue    = (String) placesListView.getItemAtPosition(position);
	                      
	                    // Show Alert 
	                    Toast.makeText(getActivity(),
	                      "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
	                      .show();
	                 
	                  }

					
	    
	             }); 
	    

		


		if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {

		}
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void loadNavMapView(final MapView mapView) {
		mapView.setAllowRotationByPinch(true);

		WMSLayer wmsLayer = new WMSLayer(
				"http://192.168.1.4:8090/geoserver/workspace/wms?service=WMS&request=GetMap"
						+ "&layers=indoormap&bbox=-100.0,-100.0,5000.0,2500.0"
						+ "&width=647&height=330&srs=EPSG:2001&format=image/png",
				SpatialReference.create(2001));
		mapView.addLayer(wmsLayer);

		mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
			private static final long serialVersionUID = -5753742033861061658L;

			@Override
			public void onStatusChanged(Object arg0, STATUS arg1) {
				// Hide the progress dialog

				// Une fois map de localisation est chargé, le timer s'éxecute

			}
		});

	}

}