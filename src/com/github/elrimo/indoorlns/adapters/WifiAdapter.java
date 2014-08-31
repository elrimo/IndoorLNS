package com.github.elrimo.indoorlns.adapters;

import java.util.List;

import com.github.elrimo.indoorlns.R;
import com.github.elrimo.indoorlns.beans.WifiItem;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WifiAdapter extends BaseAdapter {

	   private List<WifiItem> listeWifiItem;
	   private LayoutInflater layoutInflater;

	   public WifiAdapter(Context context, List<WifiItem> objects) {

	           listeWifiItem = objects;
	           layoutInflater = LayoutInflater.from(context);
	   }

	   public int getCount() {
	       return listeWifiItem.size();
	   }

	   public Object getItem(int position) {
	       return listeWifiItem.get(position);
	   }

	   public long getItemId(int position) {
	       return position;
	   }

	   private class ViewWifiHolder {
	       TextView tvApName;
	       TextView tvAdresseMac;
	       TextView ForceSignal;
	     
	   }

	   public View getView(int position, View convertView, ViewGroup parent) {
	       ViewWifiHolder viewHolder;

	       if(convertView == null) {
	           viewHolder = new ViewWifiHolder();

	           convertView = layoutInflater.inflate(R.layout.item_wifi, null);

	           viewHolder.tvApName = (TextView) convertView.findViewById(R.id.tvWifiName);
	           viewHolder.tvAdresseMac = (TextView) convertView.findViewById(R.id.tvWifiMac);
	           viewHolder.ForceSignal = (TextView) convertView.findViewById(R.id.ForceSignal);
	          
	           convertView.setTag(viewHolder);
	       } else {
	           viewHolder = (ViewWifiHolder)convertView.getTag();
	       }

	       // On affecte les valeurs
	       viewHolder.tvApName.setText(listeWifiItem.get(position).getAPName());
	       viewHolder.tvAdresseMac.setText(listeWifiItem.get(position).getAdresseMac());
	     
	       // On change la couleur en fonction de la force du signal
	       if(listeWifiItem.get(position).getForceSignal() <= -80) {
	           viewHolder.ForceSignal.setBackgroundColor(Color.GREEN);
	       } else if(listeWifiItem.get(position).getForceSignal() <= -50) {
	           viewHolder.ForceSignal.setBackgroundColor(Color.YELLOW);
	       } else {
	           viewHolder.ForceSignal.setBackgroundColor(Color.RED);
	       }

	       return convertView;
	   }

	}