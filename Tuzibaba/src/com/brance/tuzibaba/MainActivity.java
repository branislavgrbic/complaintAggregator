package com.brance.tuzibaba;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ListView getAllCustomerListView;
	private JSONArray jsonArray;
	private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_main);
        this.button = (Button) this.findViewById(R.id.button1);
        this.getAllCustomerListView = (ListView) this.findViewById(R.id.getAllCustomerListView);
        
        new GetAllFromDbTask().execute(new ApiConnector());
        
        this.getAllCustomerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				try
				{
					System.out.println("OnItemClicK:" + position);
					
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

   
    public void setListAdapter(JSONArray jsonArray)
    {
    	this.jsonArray = jsonArray;
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
    
  
    private void addIntoList(View view)
    {
    	
    }
    
    
    
}
