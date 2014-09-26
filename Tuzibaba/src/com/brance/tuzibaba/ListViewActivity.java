package com.brance.tuzibaba;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ListViewActivity extends Activity {

	private static final String server = "http://178.148.116.182/";
	ListView getAllCustomerListView;
	private JSONArray jsonArray;
	private ImageButton button;
	boolean handlerDone = false;
	boolean errorLoading = false;
	int width;
	int height;
	ImageButton splashImage;
	ImageView unavailable;
	RelativeLayout.LayoutParams lp;
	int[] imageID;
	boolean startedAlready = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.activity_list_view);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
        button = (ImageButton) findViewById(R.id.postavi);
        this.getAllCustomerListView = (ListView) this.findViewById(R.id.getAllCustomerListView);
        height = getResources().getDisplayMetrics().heightPixels;
		width = getResources().getDisplayMetrics().widthPixels;
		splashImage = (ImageButton) findViewById(R.id.splashImage);
		unavailable = (ImageView) findViewById(R.id.unavailable);
		lp = (RelativeLayout.LayoutParams) splashImage.getLayoutParams();
		splashImage.getLayoutParams().height = height;
		splashImage.getLayoutParams().width = width;
		splashImage.setImageResource(R.drawable.splash);
		imageID = new int[10];
		// If application is started from scratch show splash screen, if its resumed or 
		// screen is rotated, do nothing
		if (!startedAlready)
		{
			System.out.println("started alreadu :" + startedAlready);
			splashImage.setVisibility(View.VISIBLE);
			startedAlready = true;
			
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
			        @Override
			        public void run() {
			        	splashImage.setVisibility(View.GONE);
			        	getAllCustomerListView.setVisibility(View.VISIBLE);
			        	button.setVisibility(View.VISIBLE);
			        	System.out.println("HANDLER DONE");
			           // finish();
			        	/*
			        	boolean reachable = false;
						try {
							reachable = InetAddress.getByName("http://178.148.116.182").isReachable(4000);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (reachable) 
						{
							System.out.println("REACHABLE");
							new GetAllFromDbTask().execute(new ApiConnector());
							
						} 
						else
						{
							System.out.println("UnREACHABLE");
							unavailable.setVisibility(View.VISIBLE);
						}
						*/
			        	if (errorLoading)
			        	{
			        		System.out.println("No data from database");
							unavailable.setVisibility(View.VISIBLE);
							splashImage.setVisibility(View.GONE);
				        	getAllCustomerListView.setVisibility(View.GONE);
				        	button.setVisibility(View.VISIBLE);
				        	errorLoading = false;
				        	handlerDone = true;
			        	}
			            
			        }
			        
			        
			    }, 3000);
			
			
		}
		else
		{
			splashImage.setVisibility(View.GONE);
        	getAllCustomerListView.setVisibility(View.VISIBLE);
        	button.setVisibility(View.VISIBLE);
		}
		try
		{
			//new CheckServer().execute(new ApiConnector());
			unavailable.setVisibility(View.VISIBLE);
			new GetAllFromDbTask().execute(new ApiConnector());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
        
        
        this.getAllCustomerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				try
				{
					//System.out.println("OnItemClicK:" + position);
					
					// Get the customer which was clicked
					JSONObject customerCliked = jsonArray.getJSONObject(position);
					
					// Send Customer ID
					Intent showDetails = new Intent(getApplicationContext(), CustomerDetailsActivity.class);
					showDetails.putExtra("CustomerID", customerCliked.getInt("ID"));
					
					startActivity(showDetails);
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
        	
        });
    } 

    public void setListAdapter(JSONArray jsonArray,String[] tmp)
    {
    	this.jsonArray = jsonArray;
    	imageID[0] = getResources().getIdentifier("ostalo", "drawable", getPackageName());
    	imageID[1] = getResources().getIdentifier("rupa2", "drawable", getPackageName());
    	imageID[2] = getResources().getIdentifier("smece2", "drawable", getPackageName());
    	imageID[3] = getResources().getIdentifier("grafiti2", "drawable", getPackageName());
    	imageID[4] = getResources().getIdentifier("rasveta2", "drawable", getPackageName());
    	imageID[5] = getResources().getIdentifier("parking", "drawable", getPackageName());
    	imageID[6] = getResources().getIdentifier("slivnik2", "drawable", getPackageName());
    	imageID[7] = getResources().getIdentifier("red", "drawable", getPackageName());
    	imageID[8] = getResources().getIdentifier("orange", "drawable", getPackageName());
    	imageID[9] = getResources().getIdentifier("green", "drawable", getPackageName());
    	this.getAllCustomerListView.setAdapter(new GetAllCustomerListViewAdapter(jsonArray, this,tmp, imageID ));
    }
    
    // GetAllCustomerTask
    private class GetAllFromDbTask extends AsyncTask<ApiConnector, Long, JSONArray>
    {
    	
		@Override
		protected JSONArray doInBackground(ApiConnector... params) {
			
			// It is executed on Background thread
			
			return params[0].GetAllFromDB();
			
		}
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected void onPostExecute(JSONArray jsonArray) {
			unavailable.setVisibility(View.GONE);
			// Executed on MainThread
			if (jsonArray!= null)
			{
				String[] tmp = getResources().getStringArray(R.array.category);
				setListAdapter(jsonArray,tmp);
				if (jsonArray.length() == 0)
				{
					if (handlerDone)
					{
						System.out.println("No data from database");
						unavailable.setVisibility(View.VISIBLE);
						splashImage.setVisibility(View.GONE);
			        	getAllCustomerListView.setVisibility(View.GONE);
			        	button.setVisibility(View.VISIBLE);
					} 
				}
			}
			else 
			{
				// No connection to dataBase and splash screen is finished
				if (handlerDone)
				{
					System.out.println("No data from database");
					unavailable.setVisibility(View.VISIBLE);
					splashImage.setVisibility(View.GONE);
		        	getAllCustomerListView.setVisibility(View.GONE);
		        	button.setVisibility(View.VISIBLE);
				} 
				// else errorLoading should be shown in handler 
				else errorLoading = true;
			}
		}
    	
    }
    
    // On Splash Screen click turn it off
    
    public void dismissSplashScreen (View view)
    {
    	splashImage.setVisibility(View.GONE);
    	getAllCustomerListView.setVisibility(View.VISIBLE);
    	button.setVisibility(View.VISIBLE);
    	System.out.println("UserDissmised");
    	
    }
    
    
    private class AddToDatabase extends AsyncTask<ApiConnector, Long, JSONArray>
    {
    	
		@Override
		protected JSONArray doInBackground(ApiConnector... params) {
			
			// It is executed on Background thread
			
			 params[0].AddToDB("","",0,0);
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected void onPostExecute(JSONArray jsonArray) {
			
			// Executed on MainThread
			
			 new GetAllFromDbTask().execute(new ApiConnector());
		}
    	
    }
  
    public void addIntoList(View view)
    {
    	//new AddToDatabase().execute(new ApiConnector());
    }
    
    public void PostComplaint(View view)
    {
    	// Send Customer ID
		Intent showDetails = new Intent(getApplicationContext(), PostComplaintActivity.class);	
		startActivity(showDetails);
    }
    
    // Check Server
    private class CheckServer extends AsyncTask<ApiConnector, Long, Integer>
    {
    	
		@Override
		protected Integer doInBackground(ApiConnector... params) {
			
			// It is executed on Background thread
			
			int result = params[0].CheckServer();
			return result;
		}
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected void onPostExecute(Integer num) {
			
			// Executed on MainThread		
			if (num == 0)
			{
				unavailable.setVisibility(View.VISIBLE);
			}
		}
    	
    }
  
    
}