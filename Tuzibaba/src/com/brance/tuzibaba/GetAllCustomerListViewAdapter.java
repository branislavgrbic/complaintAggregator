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
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GetAllCustomerListViewAdapter extends BaseAdapter {

	private JSONArray dataArray;
	int coun=0;
	private Activity activity;
	private static LayoutInflater inflater = null;
	//private static final String baseUrlForImage = "http://192.168.1.102/images/";
	private static final String baseUrlForImage = "http://178.148.116.182/images/";
	//private static final String server = "http://192.168.1.102/";
	private static final String server = "http://178.148.116.182/";
	public String imageNameTmp;
	String[] categoryArray;
	int[] imageID;

	
	
	public GetAllCustomerListViewAdapter(JSONArray jsonArray,Activity a, String[] tmp, int[] arg_imageID)
	{
		this.activity = a;
		this.dataArray = jsonArray;
		imageID = arg_imageID;
		inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		categoryArray = tmp;
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
	public View getView(int position,  View convertView, ViewGroup parent) {
		
		// set up convert view, if it is null 
		coun ++;
		//System.out.println("Getview" + coun);
		
		final ListCell cell;
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.get_all_customer_cell, null);
			cell = new ListCell();
			
			cell.category = (TextView) convertView.findViewById(R.id.category);
			cell.description = (TextView) convertView.findViewById(R.id.description);
			cell.image = (ImageView) convertView.findViewById(R.id.customer_mobile);
			cell.outter = (LinearLayout) convertView.findViewById(R.id.outterLayout);
			cell.image.setTag(position);
			cell.datum = (TextView) convertView.findViewById(R.id.datum);
			cell.statusImage = (ImageView) convertView.findViewById(R.id.statusImage);
			cell.statusTextChange = (TextView) convertView.findViewById(R.id.statusTextChange);
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
			cell.category.setText(jsonObject.getString("first"));
			try
			{
				
				if (Integer.parseInt(jsonObject.getString("first")) == 0)
				{
					cell.category.setText("Ostalo");
				} else cell.category.setText(categoryArray[Integer.parseInt(jsonObject.getString("first"))]);
			}
			catch (Exception e )
			{
				cell.category.setText(categoryArray[7]);
			}
		//	cell.description.setText(jsonObject.getString("last"));
			cell.status = jsonObject.getInt("status");
			cell.str_category = jsonObject.getString("first");
			cell.myPosition = position;
			cell.datum.setText(jsonObject.getString("date"));
			
			// System.out.println(position + " " + jsonObject.getString("first") +" "+ jsonObject.getString("last") + " status:" + jsonObject.getInt("status"));
			// Temp Image is no image
			
		//	cell.image.setImageResource(imageID);
			
			// Surround with different color depending on status
			switch (cell.status)
			{
			// Red, not solved
			case 0: //cell.outter.setBackgroundColor(Color.parseColor("#ff0000"));
					cell.statusImage.setImageResource(imageID[7]);
					cell.statusTextChange.setText("U obradi");
					break;
			// Orange, needs approval
			case 1: //cell.outter.setBackgroundColor(Color.parseColor("#e19528"));
					cell.statusImage.setImageResource(imageID[8]);
					cell.statusTextChange.setText("Potrebna verifikacija");
					break;
			// Green, solved! Yeeeey!
			case 2: //cell.outter.setBackgroundColor(Color.parseColor("#1f9b0f"));
					cell.statusImage.setImageResource(imageID[9]);
					cell.statusTextChange.setText("Popravljeno!");
					break;
			}
			
			// For Scenario 1 (in userdatabase we have name of the image
			
			// url for image folder
			
			switch (Integer.parseInt(cell.str_category))
			{
			case 1:
				cell.image.setImageResource(imageID[1]);
				break;
			case 2:
				cell.image.setImageResource(imageID[2]);
				break;
			case 3:
				cell.image.setImageResource(imageID[3]);
				break;
			case 4:
				cell.image.setImageResource(imageID[4]);
				break;
			case 5:
				cell.image.setImageResource(imageID[5]);
				break;
			case 6:
				cell.image.setImageResource(imageID[6]);
				break;
			default:
				cell.image.setImageResource(imageID[0]);
				break;
			}
			
			
			
			/* Enable this for async image loading, disabled now

			// name of image that we are downloading
			String nameOfImage = jsonObject.getString("imageName");

			// full url of image
			String urlForImageInServer = baseUrlForImage + nameOfImage;
			
			// new async task for downloading images in background
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
					
					if(result != null) 
					{
						cell.image.setImageBitmap(resizeBitmap2(result));								
				    }				
					
					
				}
				
				
			}
			// Scenario 1
			.execute(urlForImageInServer);
			*/
			
		} catch (JSONException e) 
		{
			
			e.printStackTrace();
		}
		return convertView;
		
	
	

	}
	
	
	private class ListCell
	{
		private TextView category;
		private TextView description;
		private ImageView image;
		private TextView datum;
		private ImageView statusImage;
		private TextView statusTextChange;
		private LinearLayout outter;
		private int status;
		private double latitude;
		private double longitude;
		int myPosition;
		AsyncTask task;
		String str_category; 
		
	}
	
	public Bitmap resizeBitmap(Bitmap bm)
	{
		//Image resize  
		final int maxSize = 960;
		int outWidth;
		int outHeight;
		int inWidth = bm.getWidth();
		int inHeight = bm.getHeight();
		if(inWidth > inHeight){
		    outWidth = maxSize;
		    outHeight = (inHeight * maxSize) / inWidth; 
		} else {
		    outHeight = maxSize;
		    outWidth = (inWidth * maxSize) / inHeight; 
		}

		bm = Bitmap.createScaledBitmap(bm, outWidth, outHeight, false);
		return bm;
	}
	public Bitmap resizeBitmap2(Bitmap bm)
	{
		//Image resize  
		final int maxSize = 90;
		int outWidth;
		int outHeight;
		int inWidth = bm.getWidth();
		int inHeight = bm.getHeight();
		if(inWidth > inHeight){
		    outWidth = maxSize;
		    outHeight = (inHeight * maxSize) / inWidth; 
		} else {
		    outHeight = maxSize;
		    outWidth = (inWidth * maxSize) / inHeight; 
		}

		bm = Bitmap.createScaledBitmap(bm, outWidth, outHeight, false);
		return bm;
	}
	
	
	
}
