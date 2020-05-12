package com.example.googlemapapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<String> campRegion;
	private ArrayList<String> campName;
	private ArrayList<String> campStatus;


	public CustomAdapter(Context c, ArrayList<String> d, ArrayList<String> t, ArrayList<String> s) {
		campRegion = d;
		campName = t;
		campStatus = s;
		mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return campRegion.size();
	}

	@Override
	public Object getItem(int position) {
		return campRegion.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.campsites_listview, null);
		TextView campRegionTextView = (TextView) view.findViewById(R.id.regionTextView);
		TextView campNameTextView = (TextView) view.findViewById(R.id.nameTextView);
		TextView campStatusTextView = (TextView) view.findViewById(R.id.statusTextView);

		String campRegionOf = campRegion.get(position);
		String campNameOf = campName.get(position);
		String campStatusOf = campStatus.get(position);

		campRegionTextView.setText(campRegionOf);
		campNameTextView.setText(campNameOf);
		campStatusTextView.setText(campStatusOf);
		return view;
	}
}