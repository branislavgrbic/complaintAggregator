package com.brance.tuzibaba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

public class ApiConnector {
	
	//private static final String server = "http://192.168.1.102/";
	private static final String server = "http://178.148.115.115/";
	InputStream inputStream;
	// get all from customers
	public JSONArray GetAllFromDB() {
		// URL for getting db info

		String url = server + "getAllCustomers.php";
		//String url = "example.com","username","password","my_db";
		//String url = "http://10.0.2.2:8080/getAllCustomers.php";
		// get HttpResponse Object from url
		// Get HttpEntity from Http Response Object

		HttpEntity httpEntity = null;
		InputStream is = null;
        String result = "";
		try {
			
			DefaultHttpClient httpClient = new DefaultHttpClient(); //Default HttpClient
			HttpGet httpGet = new HttpGet(url);
			//HttpPost httpGet = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			
			httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		} catch (ClientProtocolException e) {

			// Signals error in http protocol
			e.printStackTrace();
			System.err.println("error in http protocol");
			// Log Errors Here

		} catch (IOException e) {
			System.err.println("error IOException e");
			// TODO: handle exception
			e.printStackTrace();
			
		}
		// Convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

	
		// Convert HttpEntity into JSON Array
		JSONArray jsonArray = null;
		
		if (httpEntity != null)
		{
			try 
			{
				//String entityResoponse = EntityUtils.toString(httpEntity);
				
			//	Log.e("Entity Response : " , entityResoponse);
				
				//jsonArray = new JSONArray(entityResoponse);
				//JSONObject json = new JSONObject(entityResoponse);
				jsonArray = new JSONArray(result);
				
				
				
			} catch (JSONException e)
			{
				System.err.println("error JSONException1");
				e.printStackTrace();
			} catch (Exception e)
			{
				System.err.println("error globas");
				e.printStackTrace();
				
			}
		}
			
		return jsonArray;

	}
	
	public JSONArray GetCustomerDetails(int CustomerID)
	{
		// URL for getting db info

				String url = server + "getCustomerDetails.php?CustomerID="+CustomerID;

				//String url = "example.com","username","password","my_db";
				//String url = "http://10.0.2.2:8080/getAllCustomers.php";
				// get HttpResponse Object from url
				// Get HttpEntity from Http Response Object

				HttpEntity httpEntity = null;
				InputStream is = null;
		        String result = "";
				try {
					
					DefaultHttpClient httpClient = new DefaultHttpClient(); //Default HttpClient
					HttpGet httpGet = new HttpGet(url);
					//HttpPost httpGet = new HttpPost(url);
					HttpResponse httpResponse = httpClient.execute(httpGet);
					
					httpEntity = httpResponse.getEntity();
					is = httpEntity.getContent();
				} catch (ClientProtocolException e) {

					// Signals error in http protocol
					e.printStackTrace();
					System.err.println("error in http protocol");
					return null;
					// Log Errors Here

				} catch (IOException e) {
					System.err.println("error IOException e");
					// TODO: handle exception
					e.printStackTrace();
					return null;
					
				}
				// Convert response to string
		        try {
		            BufferedReader reader = new BufferedReader(new InputStreamReader(
		                    is, "iso-8859-1"), 8);
		            StringBuilder sb = new StringBuilder();
		            String line = null;
		            while ((line = reader.readLine()) != null) {
		                sb.append(line + "\n");
		            }
		            is.close();
		            result = sb.toString();
		        } catch (Exception e) {
		            Log.e("log_tag", "Error converting result " + e.toString());
		            return null;
		        }

			
				// Convert HttpEntity into JSON Array
				JSONArray jsonArray = null;
				
				if (httpEntity != null)
				{
					try 
					{
						//String entityResoponse = EntityUtils.toString(httpEntity);
						
					//	Log.e("Entity Response : " , entityResoponse);
						
						//jsonArray = new JSONArray(entityResoponse);
						//JSONObject json = new JSONObject(entityResoponse);
						jsonArray = new JSONArray(result);
						
						
						
					} catch (JSONException e)
					{
						System.err.println("error JSONException1");
						e.printStackTrace();
						return null;
					} catch (Exception e)
					{
						System.err.println("error globas");
						e.printStackTrace();
						return null;
						
					}
				}
					
				return jsonArray;
		}
	
	
	
	/* ************************************************************************************************
	 * This callse insertInto.php which inserts User Name, Last Name, Age into database 
	 *   and returns the given id for this user
	 * Id is then used for image storing.
	 ************************************************************************************************* */
	public HttpResponse AddToDB(String temp_name, String temp_description, double temp_latitude, double temp_longitude) {
		// URL for getting db info

		String query = null;
		try {
			temp_name = URLEncoder.encode(temp_name   , HTTP.UTF_8);
			temp_description = URLEncoder.encode(temp_description   , HTTP.UTF_8);
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// String url = server + "getCustomerDetails.php?CustomerID="+CustomerID;
		String url = server + "insertInto.php?name=" +temp_name + "&surname="+ temp_description + "&latitude=" + temp_latitude  +"&longitude=" + temp_longitude  ;
		// String url = "example.com","username","password","my_db";
		// String url = "http://10.0.2.2:8080/getAllCustomers.php";
		// get HttpResponse Object from url
		// Get HttpEntity from Http Response Object

		HttpEntity httpEntity = null;
		InputStream is = null;
        String result = ""; 
        HttpResponse httpResponse = null;
		try {
			
			DefaultHttpClient httpClient = new DefaultHttpClient(); //Default HttpClient
			HttpGet httpGet = new HttpGet(url);
			//HttpPost httpGet = new HttpPost(url);
			httpResponse = httpClient.execute(httpGet);
			
			
			/*	 
			 * NOT REQIRED BECAUSE WE DONT NEED REPLY FROM PHP SCRIPT
			 * BUT LATER WE COULD INSERT ACQNOWLEDGMENT OF RECIEVED DB UPDATE
			
			httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			
			*
			*/
		} catch (ClientProtocolException e) {

			// Signals error in http protocol
			e.printStackTrace();
			System.err.println("error in http protocol");
			// Log Errors Here

		} catch (IOException e) {
			System.err.println("error IOException e");
			// TODO: handle exception
			e.printStackTrace();
			
		}
		// Convert response to string
   /*     try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

	
		// Convert HttpEntity into JSON Array
		JSONArray jsonArray = null;
		
		if (httpEntity != null)
		{
			try 
			{
				//String entityResoponse = EntityUtils.toString(httpEntity);
				
			//	Log.e("Entity Response : " , entityResoponse);
				
				//jsonArray = new JSONArray(entityResoponse);
				//JSONObject json = new JSONObject(entityResoponse);
				jsonArray = new JSONArray(result);
				
				
				
			} catch (JSONException e)
			{
				System.err.println("error JSONException1");
				e.printStackTrace();
			} catch (Exception e)
			{
				System.err.println("error globas");
				e.printStackTrace();
				
			}
		}
		*/	
		
		return (httpResponse);
	}
	
	/* ******************************************************************************
	 * 
	 * This method is used when uploading new report. First user sends all the information,
	 * and when he gets his Unique ID then we can update the information about image path
	 * 
	 ********************************************************************************/
	public void UpdateImagePath(String CustomerID, String filePath) {
		// URL for getting db info

		// String url = server + "getCustomerDetails.php?CustomerID="+CustomerID;
		String url = server + "updateImagePath.php?CustomerID=" + CustomerID + "&filePath=" + filePath ;
		// String url = "example.com","username","password","my_db";
		// String url = "http://10.0.2.2:8080/getAllCustomers.php";
		// get HttpResponse Object from url
		// Get HttpEntity from Http Response Object

		HttpEntity httpEntity = null;
		InputStream is = null;
        String result = ""; 
        HttpResponse httpResponse = null;
		try {
			
			DefaultHttpClient httpClient = new DefaultHttpClient(); //Default HttpClient
			HttpGet httpGet = new HttpGet(url);
			//HttpPost httpGet = new HttpPost(url);
			httpResponse = httpClient.execute(httpGet);
			
			
			/*	 
			 * NOT REQIRED BECAUSE WE DONT NEED REPLY FROM PHP SCRIPT
			 * BUT LATER WE COULD INSERT ACQNOWLEDGMENT OF RECIEVED DB UPDATE
			
			httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			
			*
			*/
		} catch (ClientProtocolException e) {

			// Signals error in http protocol
			e.printStackTrace();
			System.err.println("error in http protocol");
			// Log Errors Here

		} catch (IOException e) {
			System.err.println("error IOException e");
			// TODO: handle exception
			e.printStackTrace();
			
		}
	
	}
	
}
