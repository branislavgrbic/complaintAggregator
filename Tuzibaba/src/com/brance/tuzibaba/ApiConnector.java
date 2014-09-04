package com.brance.tuzibaba;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
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

		String url = "http://192.168.1.2:8080/getAllCustomers.php";

		// get HttpResponse Object from url
		// Get HttpEntity from Http Response Object

		HttpEntity httpEntity = null;

		try {
			
			DefaultHttpClient httpClient = new DefaultHttpClient(); //Default HttpClient
			HttpGet httpGet = new HttpGet(url);
			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			
			httpEntity = httpResponse.getEntity();
			
		} catch (ClientProtocolException e) {

			// Signals error in http protocol
			e.printStackTrace();

			// Log Errors Here

		} catch (IOException e) {
			
			// TODO: handle exception
			e.printStackTrace();
			
		}

	
		// Convert HttpEntity into JSON Array
		JSONArray jsonArray = null;
		
		if (httpEntity != null)
		{
			try 
			{
				String entityResoponse = EntityUtils.toString(httpEntity);
				
				Log.e("Entity Response : " , entityResoponse);
				
				//jsonArray = new JSONArray(entityResoponse);
				jsonArray = new JSONArray(entityResoponse);
				
				
				
			} catch (JSONException e)
			{
				e.printStackTrace();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
			
		return jsonArray;

	}
	
	
	
}
