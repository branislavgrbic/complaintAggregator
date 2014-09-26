package com.brance.tuzibaba;

import java.lang.ref.WeakReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class CopyOfListViewActivity extends Activity {
	
	ListView getAllCustomerListView;
	private JSONArray jsonArray;
	private ImageButton button;
	boolean handlerDone = false;
	boolean errorLoading = false;
	int width;
	int height;
	ImageView splashImage;
	ImageView unavailable;
	RelativeLayout.LayoutParams lp;
	private Bitmap mPlaceHolderBitmap;
	
	// Making Disk and memory Cache for list View
	private DiskLruCache mDiskLruCache;
	private final Object mDiskCacheLock = new Object();
	private boolean mDiskCacheStarting = true;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	private static final String DISK_CACHE_SUBDIR = "thumbnails";
	
	private LruCache<String, Bitmap> mMemoryCache;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.activity_list_view);
        button = (ImageButton) findViewById(R.id.postavi);
        this.getAllCustomerListView = (ListView) this.findViewById(R.id.getAllCustomerListView);
        height = getResources().getDisplayMetrics().heightPixels;
		width = getResources().getDisplayMetrics().widthPixels;
		splashImage = (ImageView) findViewById(R.id.splashImage);
		unavailable = (ImageView) findViewById(R.id.unavailable);
		lp = (RelativeLayout.LayoutParams) splashImage.getLayoutParams();
		splashImage.getLayoutParams().height = height;
		splashImage.getLayoutParams().width = width;
		splashImage.setImageResource(R.drawable.splash);
		setLoadingImage();
		
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };

		
		
		 Handler handler = new Handler();
		    handler.postDelayed(new Runnable() {
		        @Override
		        public void run() {
		        	splashImage.setVisibility(View.GONE);
		        	getAllCustomerListView.setVisibility(View.VISIBLE);
		        	button.setVisibility(View.VISIBLE);
		        	System.out.println("HANDLER DONE");
		           // finish();
		        	if (errorLoading)
		        	{
		        		System.out.println("No data from database");
						unavailable.setVisibility(View.VISIBLE);
						splashImage.setVisibility(View.GONE);
			        	getAllCustomerListView.setVisibility(View.GONE);
			        	button.setVisibility(View.VISIBLE);
			        	errorLoading = false;
			        	handlerDone = true;
		        	}
		            
		        }
		    }, 3000);  
		
		try
		{
			new GetAllFromDbTask().execute(new ApiConnector());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
        
        
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
    
    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param bitmap
     */
    public void setLoadingImage(Bitmap bitmap) {
    	mPlaceHolderBitmap = bitmap;
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId
     */
    public void setLoadingImage() {
    	mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("unavailable" , "drawable", getPackageName())); 
    }
    
    public void loadBitmap(int resId, ImageView imageView) {
        if (cancelPotentialWork(resId, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }
    
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
    	   if (imageView != null) {
    	       final Drawable drawable = imageView.getDrawable();
    	       if (drawable instanceof AsyncDrawable) {
    	           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
    	           return asyncDrawable.getBitmapWorkerTask();
    	       }
    	    }
    	    return null;
    	}
    
    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
    
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
    
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromResource(getResources(), data, 100, 100);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    
    
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
    
    static class AsyncDrawable extends BitmapDrawable {
    	
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
    /*
    //Memory cache
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
    
    public void loadBitmap(int resId, ImageView imageView) {
        final String imageKey = String.valueOf(resId);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
        	imageView.setImageBitmap(bitmap);
        } else {
        	imageView.setImageResource(R.drawable.splash);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(resId);
        }
    }
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromResource(getResources(), data, 100, 100);
        }
        
        public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        }
        
        public int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

		// Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    //Cache methods 
	 // Creates a unique sub directory of the designated app cache directory. Tries to use external
	 // but if not mounted, falls back on internal storage.
	 public static File getDiskCacheDir(Context context, String uniqueName) {
	     // Check if media is mounted or storage is built-in, if so, try and use external cache dir
	     // otherwise use internal cache dir
	     final String cachePath =
	             Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
	                     !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
	                             context.getCacheDir().getPath();
	
	     return new File(cachePath + File.separator + uniqueName);
	 }
    
	 class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
		    @Override
		    protected Void doInBackground(File... params) {
		        synchronized (mDiskCacheLock) {
		            File cacheDir = params[0];
		            mDiskLruCache = DiskLruCache.open(cacheDir, DISK_CACHE_SIZE);
		            mDiskCacheStarting = false; // Finished initialization
		            mDiskCacheLock.notifyAll(); // Wake any waiting threads
		        }
		        return null;
		    }
		}

		
		public void addBitmapToCache(String key, Bitmap bitmap) {
		    // Add to memory cache as before
		    if (getBitmapFromMemCache(key) == null) {
		        mMemoryCache.put(key, bitmap);
		    }

		    // Also add to disk cache
		    synchronized (mDiskCacheLock) {
		        if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
		            mDiskLruCache.put(key, bitmap);
		        }
		    }
		}

		public Bitmap getBitmapFromDiskCache(String key) {
		    synchronized (mDiskCacheLock) {
		        // Wait while disk cache is started from background thread
		        while (mDiskCacheStarting) {
		            try {
		                mDiskCacheLock.wait();
		            } catch (InterruptedException e) {}
		        }
		        if (mDiskLruCache != null) {
		            return mDiskLruCache.get(key);
		        }
		    }
		    return null;
		}
		*/
    //
    
    

    public void setListAdapter(JSONArray jsonArray,String[] tmp)
    {
    	this.jsonArray = jsonArray;
    	//this.getAllCustomerListView.setAdapter(new GetAllCustomerListViewAdapter(jsonArray, this,tmp,getResources().getIdentifier("noimagetumb", "drawable", getPackageName()) ));
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
			if (jsonArray!= null)
			{
				String[] tmp = getResources().getStringArray(R.array.category);
				setListAdapter(jsonArray,tmp);
			}
			else 
			{
				// No connection to dataBase and splash screen is finished
				if (handlerDone)
				{
					System.out.println("No data from database");
					unavailable.setVisibility(View.VISIBLE);
					splashImage.setVisibility(View.GONE);
		        	getAllCustomerListView.setVisibility(View.GONE);
		        	button.setVisibility(View.VISIBLE);
				} 
				// else errorLoading should be shown in handler 
				else errorLoading = true;
			}
		}
    	
    }
    
    private class AddToDatabase extends AsyncTask<ApiConnector, Long, JSONArray>
    {
    	
		@Override
		protected JSONArray doInBackground(ApiConnector... params) {
			
			// It is executed on Background thread
			
			 params[0].AddToDB("","",0,0);
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected void onPostExecute(JSONArray jsonArray) {
			
			// Executed on MainThread
			
			 new GetAllFromDbTask().execute(new ApiConnector());
		}
    	
    }
  
    public void addIntoList(View view)
    {
    	//new AddToDatabase().execute(new ApiConnector());
    }
    
    public void PostComplaint(View view)
    {
    	// Send Customer ID
		Intent showDetails = new Intent(getApplicationContext(), PostComplaintActivity.class);	
		startActivity(showDetails);
    }
    
}