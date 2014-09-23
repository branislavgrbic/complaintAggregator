package com.brance.tuzibaba;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CustomerDetailsActivity extends Activity {

	//private static final String baseUrlForImage = "http://192.168.1.102/images/";
	private static final String baseUrlForImage = "http://178.148.116.182/images/";
	private TextView description;
	private TextView category;
	private TextView statusText;
	private ImageView image;
	private int CustomerID;
	private String categorySelected;
	private int status;
	//
	// GPSTracker class
    GPSTracker gps;
    GoogleMap googleMap;
    Location myCurrentLocation;
    Marker myMarker;
    Spinner spinner ;
    public String address ="";
    double latitude; 
	double longitude;
    // Parameters from layout
    int width;
	int height;
	LinearLayout.LayoutParams lp;
	FrameLayout scrollLayout;
	FrameLayout.LayoutParams fp;
	private TextView locationAddress;
	String[] categoryArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_customer_details);
		
		
		this.image = (ImageView) this.findViewById(R.id.imageView);
		this.locationAddress= (TextView) findViewById(R.id.locationAddress);
		this.statusText = (TextView) findViewById(R.id.statusState);
		this.category = (TextView) this.findViewById(R.id.category);
		this.description = (TextView) this.findViewById(R.id.description);
		scrollLayout = (FrameLayout) this.findViewById(R.id.scrollview);
		fp = (FrameLayout.LayoutParams) scrollLayout.getLayoutParams();
		// Get size of screen 
		height = getResources().getDisplayMetrics().heightPixels;
		width = getResources().getDisplayMetrics().widthPixels;
		lp = (LinearLayout.LayoutParams) this.image.getLayoutParams();
		categoryArray = getResources().getStringArray(R.array.category);
		// get Customer ID
		this.CustomerID = getIntent().getIntExtra("CustomerID", -1);
		
		
		
		if (this.CustomerID > 0)
		{
			// we have customer ID passed correctly.
			new GetCustomerDetails().execute(new ApiConnector());
			
		}
		
		
	}

	 private class GetCustomerDetails extends AsyncTask<ApiConnector, Long, JSONArray>
	    {

			@Override
			protected JSONArray doInBackground(ApiConnector... params) {
				
				// It is executed on Background thread
				
				return params[0].GetCustomerDetails(CustomerID);
				
			}
			
			@Override
			protected void onPreExecute() {
				
			}
			
			@Override
			protected void onPostExecute(JSONArray jsonArray) {
				
				// Executed on MainThread
				try
				{
					JSONObject customer = jsonArray.getJSONObject(0);		
					category.setText(categoryArray[Integer.parseInt(customer.getString("first"))]);
					description.setText(customer.getString("last"));
					latitude = customer.getDouble("latitude");
					longitude = customer.getDouble("longitude");
					status = customer.getInt("status");
					// Set status TextView
					switch (status)
					{
					case 0: statusText.setText("U obradi");
							statusText.setTextColor(Color.parseColor("#ff0000"));
							break;
					case 1: statusText.setText("Potrebna verifikacija");
							statusText.setTextColor(Color.parseColor("#e19528"));
							break;
					case 2: statusText.setText("Zavrsen!");
							statusText.setTextColor(Color.parseColor("#1f9b0f"));
							break;
					}
					drawMap();
					// Add point on given location
					myMarker = googleMap.addMarker(new MarkerOptions()
			        .position(new LatLng(latitude, longitude))
			        );
			        myMarker.remove(); 
					myMarker = googleMap.addMarker(new MarkerOptions()
					     .position(new LatLng(latitude, longitude))
							  );
					
					
					
					
					// For Scenario 1 (in userdatabase we have name of the image
					
					// url for image folder
					
			
					// name of image that we are downloading
					String nameOfImage = customer.getString("imageName");

					// full url of image
					String urlForImageInServer = baseUrlForImage + nameOfImage;
					
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
							// Change displayed image size 
				            /*
							 * Here we calculate the size of image so it is displayed correctly on all devices
							 */
							try
							{
								
								
								image.getLayoutParams().height = result.getHeight()* width / result.getWidth() ;
								image.getLayoutParams().width = (int)  (width -2*getResources().getDimension(R.dimen.activity_horizontal_margin));
								image.setScaleType(ScaleType.FIT_XY);
								image.setImageBitmap(resizeBitmap(result));
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
							
						}
						
					}.execute(urlForImageInServer);
					
					
					
					
				} catch (JSONException e) 
				{
					
					e.printStackTrace();
				}
				
					//
			//	}
				catch (Exception e )
				{
					e.printStackTrace();
				}
			}
	    	
	    }
	 public Bitmap resizeBitmap(Bitmap bm)
		{
			//Image resize  
			final int maxSize = 800;
			int outWidth;
			int outHeight;
			int inWidth = image.getLayoutParams().width;
			int inHeight = image.getLayoutParams().height;
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
	 
	 /* *************************************************************************************************
		 *  Methods for using GoogleMaps API in application. Checks if maps are available, sets map on 
		 *  layout, and finds our location
		 ***************************************************************************************************/
		private void drawMap()
		{

			// Adjust GPS
			gps = new GPSTracker(this);
		    

			
			// SetMap and location on map
			if (isGooglePlayOk())
			{
				setMap();
				googleMap.setMyLocationEnabled(true);
			    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
				Criteria criteria = new Criteria();
				String provider = service.getBestProvider(criteria, false);
				Location location = service.getLastKnownLocation(provider);
				myCurrentLocation = location;
				
			
			}
			
			try 
			{
				getAddress();
			}
			catch (Exception e) {
			    e.printStackTrace(); // getFromLocation() may sometimes fail
			}
		}
		
		private boolean isGooglePlayOk() 
		{
		    int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		    if (status == ConnectionResult.SUCCESS) {
		        return (true);
		    }

		    
		    return (false);

		}
		private void setMap() {
			try {
				if (googleMap == null) {
			    	MapFragment mapf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
			        googleMap =  mapf.getMap();
			        System.out.println("map2");
			        if (googleMap != null) {

			        }

			        googleMap.setMyLocationEnabled(true);	        
			        LocationManager la = (LocationManager) getSystemService(LOCATION_SERVICE);	        
			        String provider = la.getBestProvider(new Criteria(), true);	        
			        android.location.Location loc = la.getLastKnownLocation(provider);	 

			        if (provider != null) {
			            onLocationChanged(loc);
			        }

			      //  googleMap.setOnMapLongClickListener(onLongClickMapSettiins());
			        
			    }
			} catch (Exception e) {
				System.err.println("Crash google map woyld appear");
				setMap();
			}
		    
		}
		public void onLocationChanged(android.location.Location location) {

		    LatLng latlong = new LatLng(latitude,
		            longitude);

		    googleMap.setMyLocationEnabled(true);

		    CameraPosition cp = new CameraPosition.Builder().target(latlong)
		            .zoom(15).build();

		    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

		}
		
		// Get address based on location
		
		public void getAddress()
		{
		    Geocoder geo = new Geocoder(CustomerDetailsActivity.this.getApplicationContext(), Locale.getDefault());
		    List<Address> addresses = null;
			try 
			{
				addresses = geo.getFromLocation(latitude, longitude, 1);
			    if (addresses.isEmpty()) {
			    	this.locationAddress.setText("Waiting for Location");
			    }
			    else {
			        if (addresses.size() > 0) {
			            this.locationAddress.setText(addresses.get(0).getThoroughfare()+ "," + addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getCountryName());
			            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
			            address = addresses.get(0).getFeatureName() + "," + addresses.get(0).getLocality() +"," + addresses.get(0).getCountryName();
			            
			        }
			    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
