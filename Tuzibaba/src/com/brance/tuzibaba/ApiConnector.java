package com.brance.tuzibaba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ApiConnector {
	
	// get all from customers
	public JSONArray GetAllFromDB() {
		// URL for getting db info

		String url = "http://192.168.1.2/getAllCustomers.php";
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

				String url = "http://192.168.1.2/getCustomerDetails.php?CustomerID="+CustomerID;

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
	
	
	
}
