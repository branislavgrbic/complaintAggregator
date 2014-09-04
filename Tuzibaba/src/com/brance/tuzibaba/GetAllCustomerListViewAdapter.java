package com.brance.tuzibaba;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.provider.Telephony.Sms.Conversations;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GetAllCustomerListViewAdapter extends BaseAdapter {

	private JSONArray dataArray;
	private Activity activity;
	private static LayoutInflater inflater = null;
	
	
	public GetAllCustomerListViewAdapter(JSONArray jsonArray,Activity a)
	{
		this.activity = a;
		this.dataArray = jsonArray;
		
		inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	@Override
	public int getCount() {
		
		return this.dataArray.length();
	}

	@Override
	public Object getItem(int position) {
		
		return position;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// set up convert view, if it is null 
		ListCell cell;
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.get_all_customer_cell, null);
			cell = new ListCell();
			
			cell.firstName = (TextView) convertView.findViewById(R.id.customer_name);
			cell.lastName = (TextView) convertView.findViewById(R.id.customer_surname);
			cell.image = (ImageView) convertView.findViewById(R.id.customer_mobile);
			
			convertView.setTag(cell);
		}
		else
		{
			cell = (ListCell) convertView.getTag();
		}
		
		// chage the data of cell
		try 
		{
			JSONObject jsonObject = this.dataArray.getJSONObject(position);
			cell.firstName.setText(jsonObject.getString("first"));
			cell.lastName.setText(jsonObject.getString("last"));
			
			cell.image.setImageResource(R.drawable.ic_launcher);
			//add here code for image location
			
			
		} catch (JSONException e) 
		{
			
			e.printStackTrace();
		}
		
		
		
		return convertView;
	}
	
	private class ListCell
	{
		private TextView firstName;
		private TextView lastName;
		private ImageView image;
		
		
	}

}
