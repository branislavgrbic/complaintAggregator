package com.brance.tuzibaba;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ListView getAllCustomerListView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_main);
        
        this.getAllCustomerListView = (ListView) this.findViewById(R.id.getAllCustomerListView);
        
        new GetAllFromDbTask().execute(new ApiConnector());
        
    } 

   
    public void setListAdapter(JSONArray jsonArray)
    {
    	this.getAllCustomerListView.setAdapter(new GetAllCustomerListViewAdapter(jsonArray, this));
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
			
			// Executed on MainThread
			setListAdapter(jsonArray);
			
		}
    	
    }
    
}
