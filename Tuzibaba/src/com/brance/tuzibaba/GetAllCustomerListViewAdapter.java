package com.brance.tuzibaba;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.Telephony.Sms.Conversations;
import android.util.Log;
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
	private static final String baseUrlForImage = "http://192.168.1.2/images/";
	private static final String server = "http://192.168.1.2/";
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
		final ListCell cell;
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
			
			
			
			
			// For Scenario 1 (in userdatabase we have name of the image
			
			// url for image folder
			
	
			// name of image that we are downloading
			String nameOfImage = jsonObject.getString("imageName");

			// full url of image
			String urlForImageInServer = baseUrlForImage + nameOfImage;
			
			
			/* 
			*  
			*	Scenario 2 (using BLOB in database)
			*		
			*
			*/
			
			String idOfUser = jsonObject.getString("ID");
			
			// full url of image
			String urlForImageInDatabase = server + "getImage.php?ID=" + idOfUser;
			Log.i("URL FOR IMAGE " , urlForImageInDatabase);
			
			// new async task
			new AsyncTask<String, Void, Bitmap>()
			{

				@Override
				protected Bitmap doInBackground(String... params) {
					
					//download image
					String url = params[0];
					Bitmap icon = null;
					
					try 
					{
						InputStream in = new java.net.URL(url).openStream();
						icon = BitmapFactory.decodeStream(in);
					
						
					} catch (MalformedURLException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					return icon;
				}
				
				
				protected void onPostExecute(Bitmap result)
				{
					// assign that image to ImageView of list view cell
					cell.image.setImageBitmap(result);
					
				}
				
			}
			// Scenario 1
			//.execute(urlForImageInServer);
			// Scenario 2
			.execute(urlForImageInDatabase);
			
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
