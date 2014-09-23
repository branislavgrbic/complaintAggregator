package com.brance.tuzibaba;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Splash extends Activity {
    int width;
	int height;
	ImageView splashImage;
	LinearLayout.LayoutParams lp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);  
	    setContentView(R.layout.splash);
	    height = getResources().getDisplayMetrics().heightPixels;
		width = getResources().getDisplayMetrics().widthPixels;
		splashImage = (ImageView) findViewById(R.id.splashImage);
		lp = (LinearLayout.LayoutParams) splashImage.getLayoutParams();
		splashImage.getLayoutParams().height = height;
		splashImage.getLayoutParams().width = width;
        
		splashImage.setImageResource(R.drawable.splash);
        
        
	    Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	            Intent openMainActivity =  new Intent(Splash.this, ListViewActivity.class);
	            startActivity(openMainActivity);
	            finish();
	            
	        }
	    }, 3000);    
	}
	
	// Resizes bitmap to prevent large images and out of memory error
			public Bitmap resizeBitmap(Bitmap bm)
			{
				//Image resize  
				final int maxSize = 960;
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
}
