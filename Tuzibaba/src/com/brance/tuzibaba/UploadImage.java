package com.brance.tuzibaba;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

public class UploadImage extends Activity {
    InputStream inputStream;
   // private static final String server = "http://192.168.1.102/";
	private static final String server = "http://178.148.116.182/";
        @Override
    public void onCreate(Bundle icicle) {
            super.onCreate(icicle);
            setContentView(R.layout.upload_activity);
            System.out.println("UPLOAD IMAGE CLASS!");
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);    
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
            byte [] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr,0 );
            final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("image",image_str));

             Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                  try{
                         HttpClient httpclient = new DefaultHttpClient();
                         HttpPost httppost = new HttpPost(server+"test.php");
                         httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                         HttpResponse response = httpclient.execute(httppost);
                         final String the_string_response = convertResponseToString(response);
                         runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(UploadImage.this, "Successfully uploaded!" , Toast.LENGTH_LONG).show();                          
                                }
                            });

                     }catch(final Exception e){
                          runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(UploadImage.this, "E1:Doslo je do greske, pokusajte kasnije." , Toast.LENGTH_LONG).show();                              
                            }
                        });
                           System.out.println("Error in http connection "+e.toString());
                     }  
            }
        });
         t.start();
        }

        public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{

             String res = "";
             StringBuffer buffer = new StringBuffer();
             inputStream = response.getEntity().getContent();
             final int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..
              runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(UploadImage.this, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();                     
            }
        });

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
                    runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                       Toast.makeText(UploadImage.this, "Result : " +res1, Toast.LENGTH_LONG).show();
                    }
                });
                    //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
             }
             return res;
        }
}
