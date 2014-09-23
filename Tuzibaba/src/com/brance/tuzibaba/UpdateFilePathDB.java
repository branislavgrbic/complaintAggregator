package com.brance.tuzibaba;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.json.JSONArray;

import android.os.AsyncTask;

public class UpdateFilePathDB extends AsyncTask<ApiConnector, Long, JSONArray>
{
	private String CustomerID;
	private String filePath;
	public UpdateFilePathDB(String CustomerID, String filePath)
	{
		this.CustomerID = CustomerID;
		this.filePath = filePath;
	}
	
	@Override
	protected JSONArray doInBackground(ApiConnector... params) {
		
		// It is executed on Background thread
		
		
		try {
			params[0].UpdateImagePath(this.CustomerID, this.filePath);
		} catch (IllegalStateException e) {
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
		
		//Toast toast = Toast.makeText(getApplicationContext(), "Successfully added to server", Toast.LENGTH_LONG);
		//toast.show();
	}
}