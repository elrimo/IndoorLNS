package com.github.elrimo.indoorlns.adapters;

import java.util.List;

import com.github.elrimo.indoorlns.R;
import com.github.elrimo.indoorlns.beans.RowItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<RowItem> {
	
	private Context context;

	public CustomArrayAdapter(Context context, int resource, List<RowItem> objects) {
		super(context, resource, objects);
		this.context = context;
	}
	
	private class ViewHolder {
		ImageView icon;
		TextView text;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		RowItem item = getItem(position);
		
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.item_icon);
			holder.text = (TextView) convertView.findViewById(R.id.item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.icon.setImageResource(item.getIcon());
		holder.text.setText(item.getText());
		
		return convertView;
	}
	
	

}
