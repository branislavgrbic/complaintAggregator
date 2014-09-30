package com.brance.tuzibaba;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class PostComplaintActivity extends Activity {

	TextView locationAddress;
	String locationAddressString ="Waiting for location...";
	private String categorySelected;
	private EditText description;
	private EditText age;
	private String userID;
	
	private static final int SELECT_PHOTO = 100;
	private static final int SELECT_FILE = 1;
	private static final int REQUEST_CAMERA = 0;
	static String takePicturePath;
	ImageView imageView;
	ImageView buttonCamera;
	ImageView buttonGallery;
	ImageView buttonSend;
	ImageButton selectImageButton;
	double latitude; 
	double longitude;
	InputStream inputStream;
   // private static final String server = "http://192.168.1.102/";
	private static final String server = "http://178.148.116.182/";
    // GPSTracker class
    GPSTracker gps;
    GoogleMap googleMap;
    Location myCurrentLocation;
    Marker myMarker;
    Spinner spinner ;
    public String address ="";
    // Parameters from layout
    int width;
	int height;
	LinearLayout.LayoutParams lp;
	MapFragment mapf;
	AlertDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_post_complaint);
		//this.firstName = (EditText) this.findViewById(R.id.firstName);
		this.description = (EditText) this.findViewById(R.id.lastName);
		selectImageButton = (ImageButton) findViewById(R.id.selectImageButton);
		imageView = (ImageView) findViewById(R.id.imageView);
		buttonCamera = (ImageView) findViewById(R.id.buttonCamera);
		buttonGallery = (ImageView) findViewById(R.id.buttonGallery);
		buttonSend = (ImageView) findViewById(R.id.buttonSend);
		// Hide imageView until one picture is selected
		imageView.setVisibility(View.GONE);
		
		spinner = (Spinner) findViewById(R.id.categorySpiner);
		this.locationAddress= (TextView) findViewById(R.id.locationAddress); 
		mapf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		// Get size of screen 
		height = getResources().getDisplayMetrics().heightPixels;
		width = getResources().getDisplayMetrics().widthPixels;
		lp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
		try
		{
			// Populate spinner with categories
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.category, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			    @Override
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			    	//categorySelected = spinner.getSelectedItem().toString();
			    	categorySelected = Integer.toString(position);
			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	categorySelected = "";
			    }
			});
			/*
			// Adjust GPS
			gps = new GPSTracker(this);
		    
			// check if GPS enabled     
			if(gps.canGetLocation())
			{
				// Save current location
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude +"\n" , Toast.LENGTH_LONG).show();    
			}
			else
			{
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
			    gps.showSettingsAlert();
			}
			
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
				Point point = new Point();
			
			}*/
			// Draw map should be done in background thread to increase loading speed 
			drawMap();
			new LoadGpsAsync().execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	// Adds the complaint into database, and gets response = ID
	public void AddIntoList(View view)
	{
		try
		{
			new AddToDatabase(categorySelected, description.getText().toString(),
					this.latitude, this.longitude)
						.execute(new ApiConnector());
			Toast.makeText(getApplicationContext(), "Uploading...", Toast.LENGTH_LONG).show();  
			buttonSend.setEnabled(false);
		} catch (Exception e )
		{
			Toast.makeText(getApplicationContext(), "Error1", Toast.LENGTH_LONG).show();  
			buttonSend.setEnabled(true);
		}
	}
	
	
	/* ***********************************************************************
	 * Here we get all the details user inserted, and put them in database.
	 * As a result we get Unique ID which is used for naming the picture user uploaded.
	 * After the picture is uploaded we can put path into database.
	 * 
	 ************************************************************************* */
	private class AddToDatabase extends AsyncTask<ApiConnector, Long, JSONArray>
    {
		private String categorySelected;
		private String description;
		private String myAddress;
		private String userID;
		private String imagePath;
		private double latitude;
		private double longitude;
    	public AddToDatabase(String t_category,String t_description, double t_latitude, double t_longitude)
    	{
    		this.categorySelected = t_category;
    		this.description = t_description;
    		
    		this.latitude = t_latitude;
    		this.longitude = t_longitude;
    		
    	}
		@Override
		protected JSONArray doInBackground(ApiConnector... params) {
			
			// It is executed on Background thread
			
			HttpResponse response = params[0].addToDB(this.categorySelected, this.description,this.latitude,this.longitude);
			try {
				userID = convertResponseToString(response);
				
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected void onPostExecute(JSONArray jsonArray) {
			
			// Executed on MainThread
		//	uploadImage();
			uploadImage();
			
			//Toast toast = Toast.makeText(getApplicationContext(), "Successfully added to server", Toast.LENGTH_LONG);
			//toast.show();
		}
    	
		// Uploads image to server - if user doesnt select any image noimage.png will be sent
		public void uploadImage()
		{
			
			 Bitmap bitmap = null;
			 if (takePicturePath == null)
			 {
				 // if image is null, send default img
				 
				 bitmap  = BitmapFactory.decodeResource(getResources(),R.drawable.noimage);    
		     }
			 else
			 {
				 bitmap = BitmapFactory.decodeFile(takePicturePath);  
		         
			 }
			 ByteArrayOutputStream stream = new ByteArrayOutputStream();
			 
			 // PNG Format can not be compressed
	         bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to which format you want.

	         byte [] byte_arr = stream.toByteArray();
	         String image_str = Base64.encodeToString(byte_arr,0 );
	         final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

	         nameValuePairs.add(new BasicNameValuePair("image",image_str));

	         Thread t = new Thread(new Runnable() {

	         @Override
	         public void run() {
	               try{
	                      HttpClient httpclient = new DefaultHttpClient();
	                      HttpPost httppost = new HttpPost(server+"test.php?ID=" + userID);
	                      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	                      HttpResponse response = httpclient.execute(httppost);
	                      final String the_string_response = convertResponseToString(response);
	                      imagePath = the_string_response;
	                      updateDB();
	                    /*  runOnUiThread(new Runnable() {

	                             @Override
	                             public void run() {
	                                 Toast.makeText(getApplicationContext(), "Response " + the_string_response, Toast.LENGTH_LONG).show();                          
	                             }
	                         });
	                     */
	                  }catch(final Exception e){
	                       runOnUiThread(new Runnable() {

	                         @Override
	                         public void run() {
	                             Toast.makeText(getApplicationContext(), "E2:Doslo je do greske, pokusajte kasnije." , Toast.LENGTH_LONG).show();                              
	                         }
	                     });
	                        System.out.println("Error in http connection "+e.toString());
	                  }  
	         }
	     });
	      t.start();
		}
	  
		// Converts server response to string
		
		 public String convertResponseToString(HttpResponse response)  throws IllegalStateException, IOException{

	         String res = "";
	         StringBuffer buffer = new StringBuffer();
	         inputStream = response.getEntity().getContent();
	         final int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..
	         /*
	         runOnUiThread(new Runnable() {

	        @Override
	        public void run() {
	            Toast.makeText(getApplicationContext(), "Uploading...", Toast.LENGTH_LONG).show();                     
	        }
	    });*/

	         if (contentLength < 0){
	         }
	         else{
	                byte[] data = new byte[512];
	                int len = 0;
	                try
	                {
	                    while (-1 != (len = inputStream.read(data)) )
	                    {
	                        buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
	                    }
	                }
	                catch (IOException e)
	                {
	                    e.printStackTrace();
	                }
	                try
	                {
	                    inputStream.close(); // closing the stream…..
	                }
	                catch (IOException e)
	                {
	                    e.printStackTrace();
	                }
	                res = buffer.toString();     // converting stringbuffer to string…..
	                final String res1 = res;
	               // this.imagePath = res;
	                /*
	                runOnUiThread(new Runnable() {	                	              
	                	
	                @Override
	                public void run() {
	                   //Toast.makeText(getApplicationContext(), "Result : "+ res1, Toast.LENGTH_LONG).show();
	                }
	            });*/
	                //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
	         }
	         // When we get imagePath we need to update our Database for this entry;	
	         
	         
	         
	         return res;
	    }	  
		 
		// Updates database on server with details of image (we needed ID response from server for this) 
		public void updateDB()
		{
			try
			{
				new UpdateFilePathDB( userID,  imagePath)
							.execute(new ApiConnector());
				
				
				// Showing Alert Message
      
				
				runOnUiThread(new Runnable() {	                	              
                	
	                @Override
	                public void run() {
	                   Toast.makeText(getApplicationContext(), "Successfully uploaded!", Toast.LENGTH_LONG).show();
	                   // Maybe this should be outside of runOnUiThread
	                   goToCustomerDetailsActivity();
	                	
	                	
	                }
	            });
				
	            
			} catch (Exception e )
			{
				e.printStackTrace();	
			}
		} 
		//
		 
    }
	
	/* *********************************************************************************************************************
	 * This part of code selects image from Galery or from phone Camera, then stores it in imageView for demonstration,
	 * After that path of the image should be sent to server for uploading.
	 ********************************************************************************************************************* */


			
	//	Select img from gallery, or make new photo 
	public void selectImage(View view) {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}
    	
	// Select Photo from gallery
	public void galleryPhoto(View view)
	{
		Intent intent = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(
				Intent.createChooser(intent, "Select File"),
				SELECT_FILE);
	}
	// Make photo from Camera
	public void cameraPhoto(View view)
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = new File(android.os.Environment
				.getExternalStorageDirectory(), "temp.jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		startActivityForResult(intent, REQUEST_CAMERA);
	}
	
	// When returned from selecting photo (camera or gallery)
	
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
				if (requestCode == REQUEST_CAMERA) 
				{
					File f = new File(Environment.getExternalStorageDirectory()
							.toString());
					for (File temp : f.listFiles()) {
						if (temp.getName().equals("temp.jpg")) {
							f = temp;
							break;
						}
					}
					try {
						
						Bitmap bm;
						BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

						bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
								btmapOptions);
						
		
						//Save bitmap in fullsize on sd-card
						String path = android.os.Environment
								.getExternalStorageDirectory()
								+ File.separator
								+ "Pictures" + File.separator +"Client"  + File.separator; //+ "default";
					//	f.delete();
						OutputStream fOut = null;
						File file = new File(path, String.valueOf(System
								.currentTimeMillis()) + ".jpg");
						takePicturePath = file.getAbsolutePath();
						
						//resize bitmap and set it in imageView
						bm = resizeBitmap(bm);
						
						
						try {
							fOut = new FileOutputStream(file);
							bm.compress(Bitmap.CompressFormat.JPEG, 65, fOut);
							// Change displayed image size 
				            /*
							 * Here we calculate the size of image so it is displayed correctly on all devices
							 */
				            imageView.getLayoutParams().height = bm.getHeight()* width / bm.getWidth() ;
				            imageView.getLayoutParams().width = width - lp.leftMargin - lp.rightMargin;
				            selectImageButton.setVisibility(View.GONE);
				            imageView.setVisibility(View.VISIBLE);
							imageView.setImageBitmap(bm);
							fOut.flush();
							fOut.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (requestCode == SELECT_FILE) {
				
					 Uri selectedImage = data.getData();
			            
			            InputStream imageStream = null;
						try {
							imageStream = getContentResolver().openInputStream(selectedImage);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
			            Bitmap img2  = null;
			            try {
			            	img2 = decodeUri(selectedImage);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            
			            // Change displayed image size 
			            /*
						 * Here we calculate the size of image so it is displayed correctly on all devices
						 */
			            imageView.getLayoutParams().height = yourSelectedImage.getHeight()* width / yourSelectedImage.getWidth() ;
			            imageView.getLayoutParams().width = width - lp.leftMargin - lp.rightMargin;        
			            selectImageButton.setVisibility(View.GONE);
			            imageView.setVisibility(View.VISIBLE);
			            imageView.setImageBitmap(img2);
			            takePicturePath = getRealPathFromURI (this,selectedImage);
			            String tempPath = takePicturePath;
			            // draw map again
			           // drawMap();
				}
			}
		}
		
		// Resizes bitmap to prevent large images and out of memory error
		public Bitmap resizeBitmap(Bitmap bm)
		{
			//Image resize  
			final int maxSize = 800;
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
		
		private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

	        // Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

	        // The new size we want to scale to
	        final int REQUIRED_SIZE = 100;

	        // Find the correct scale value. It should be the power of 2.
	        int width_tmp = o.outWidth, height_tmp = o.outHeight;
	        int scale = 1;
	        while (true) {
	            if (width_tmp / 2 < REQUIRED_SIZE
	               || height_tmp / 2 < REQUIRED_SIZE) {
	                break;
	            }
	            width_tmp /= 2;
	            height_tmp /= 2;
	            scale *= 2;
	        }

	        // Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

	    }
		
		public String getRealPathFromURI(Context context, Uri contentUri) {
			  Cursor cursor = null;
			  try { 
			    String[] proj = { MediaStore.Images.Media.DATA };
			    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			    cursor.moveToFirst();
			    return cursor.getString(column_index);
			  } finally {
			    if (cursor != null) {
			      cursor.close();
			    }
			  }
			}

		
		/* *************************************************************************************************
		 *  Methods for using GoogleMaps API in application. Checks if maps are available, sets map on 
		 *  layout, and finds our location
		 ***************************************************************************************************/
		private void drawMap()
		{

			// Adjust GPS
			gps = new GPSTracker(this);
		    
			// check if GPS enabled     
			if(gps.canGetLocation())
			{
				// Save current location
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				//Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude +"\n" , Toast.LENGTH_LONG).show();    
			}
			else
			{
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
			    gps.showSettingsAlert();
			}
			
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
				Point point = new Point();
			
			}
			
			try 
			{
				//getAddress();
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

		    LatLng latlong = new LatLng(location.getLatitude(),
		            location.getLongitude());

		   googleMap.setMyLocationEnabled(true);

		    CameraPosition cp = new CameraPosition.Builder().target(latlong)
		            .zoom(15).build();

		    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
		    // getAddress();
		    new LoadGpsAsync().execute();
		}
		
		// Get address based on location
		
		public void getAddress()
		{
		    Geocoder geo = new Geocoder(PostComplaintActivity.this.getApplicationContext(), Locale.getDefault());
		    List<Address> addresses = null;
			try 
			{
				addresses = geo.getFromLocation(latitude, longitude, 1);
			    if (addresses.isEmpty()) {
			    	this.locationAddress.setText("Waiting for Location");
			    }
			    else {
			        if (addresses.size() > 0) {
			           // this.locationAddress.setText(addresses.get(0).getThoroughfare()+ "," + addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getCountryName());
			        	locationAddressString = addresses.get(0).getThoroughfare()+ "," + addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getCountryName();
			        	//Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
			            address = addresses.get(0).getFeatureName() + "," + addresses.get(0).getLocality() +"," + addresses.get(0).getCountryName();
			            
			        }
			    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/* *************************************************************************************************
		 *  Load GPS map in background task
		 ***************************************************************************************************/
		public class LoadGpsAsync extends AsyncTask<String, Void, String> {
		    /** The system calls this to perform work in a worker thread and
		      * delivers it the parameters given to AsyncTask.execute() */
			
			@Override
			
			protected String doInBackground(String... params) {
				System.out.println("Do in background");
				getAddress();
				return "";		
			}
		    
		    /** The system calls this to perform work in the UI thread and delivers
		      * the result from doInBackground() */
			@Override
			protected void onPostExecute(String result) {
		    	//getAddress();
		    	//setMap();
		    	// SetMap and location on map
				locationAddress.setText(locationAddressString);			  	
			}
		}
		
		/* *************************************************************************************************
		 *  Override resume and on pause
		 ***************************************************************************************************/
		
		@Override
		protected void onResume() {
			super.onResume();
		}
		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			gps.stopUsingGPS();
		}
		
		// When user posts complaint, go to on CustomerDetailsActivity which is called from listView onChild click
		public void goToCustomerDetailsActivity()
		{
			// Send Customer ID
			
			
			LayoutInflater inflater = getLayoutInflater();
			dialog = new AlertDialog.Builder(PostComplaintActivity.this) 					
			.setView(inflater.inflate(R.layout.alert_dialog,null))
			.setTitle("Prijava poslata")	
			.setPositiveButton("U redu", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					dialog.cancel();													
				}						
			}).show();
			buttonCamera.setVisibility(View.GONE);
			buttonGallery.setVisibility(View.GONE);
			buttonSend.setVisibility(View.GONE);
		}
		
}

