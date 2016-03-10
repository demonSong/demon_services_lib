package com.demon.services.bitmap.utils;

import java.lang.ref.WeakReference;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;


/**
 * 照片墙辅助工具
 * @author Administrator
 *
 */
public class BitmapTool {
	
	/**
	 * 分配 memoryCache
	 */
	private static LruCache<String, Bitmap> mMemoryCache;
	
	//set bitmap size
	private static final int BITMAP_SIZE=310;
	
	/** 
     * ImageLoader的实例。 
     */  
    private static BitmapTool mImageLoader;  
  
    private BitmapTool() {  
        // 获取应用程序最大可用内存  
        int maxMemory = (int) Runtime.getRuntime().maxMemory();  
        int cacheSize = maxMemory / 8;  
        // 设置图片缓存大小为程序最大可用内存的1/8  
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {  
            @Override  
            protected int sizeOf(String key, Bitmap bitmap) {  
                return bitmap.getByteCount();  
            }  
        };  
    }  
  
    /** 
     * 获取ImageLoader的实例。 
     *  
     * @return ImageLoader的实例。 
     */  
    public static BitmapTool getInstance() {  
        if (mImageLoader == null) {  
            mImageLoader = new BitmapTool();  
        }  
        return mImageLoader;  
    }

    /** 
     * 将一张图片存储到LruCache中。 
     *  
     * @param key 
     *            LruCache的键，这里传入图片的URL地址。 
     * @param bitmap 
     *            LruCache的键，这里传入从网络上下载的Bitmap对象。 
     */  
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
        if (getBitmapFromMemCache(key) == null) {  
            mMemoryCache.put(key, bitmap);  
        }  
    }
    
    /** 
     * 从LruCache中获取一张图片，如果不存在就返回null。 
     *  
     * @param key 
     *            LruCache的键，这里传入图片的URL地址。 
     * @return 对应传入键的Bitmap对象，或者null。 
     */  
    public static Bitmap getBitmapFromMemCache(String key) {  
        return mMemoryCache.get(key);  
    }
    
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	/**
	 * 
	 * @param iconWidth 拉伸后的宽度
	 * @param iconHeight 拉伸后的高度
	 * @return bitmap
	 * 
	 * 适用于 .9.png格式的图片,判断图片资源格式，抛出异常
	 */
	public static Bitmap createScaleBitmap(Bitmap bitmap,int iconWidth,int iconHeight){
		return Bitmap.createScaledBitmap(bitmap, iconWidth, iconHeight, true);
	}
	
	
	/**
	 * 
	 * @param res 
	 * @param resId 图片资源id
	 * @param reqWidth
	 * @param reqHeight
	 * @return bitmap
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		final int minLength =(reqWidth>reqHeight)?reqHeight:reqWidth;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		final Bitmap mBitmap =BitmapFactory.decodeResource(res, resId,options);
		
		return Bitmap.createScaledBitmap(mBitmap,minLength,minLength,true);
	}
	
	/**
	 * 图片加载 异步线程
	 * 这里使用静态，表示全程只需要一个异步线程？
	 * @author demon
	 * 
	 */
	 class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int data = 0;
		private Resources res;

		public BitmapWorkerTask(Resources res,ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
			this.res =res;
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			//do in background to perform good UI

			data = params[0];
			final Bitmap bitmap = BitmapTool.decodeSampledBitmapFromResource(
					res, data, BITMAP_SIZE, BITMAP_SIZE);
			BitmapTool.addBitmapToMemoryCache(String.valueOf(data), bitmap);
			return bitmap;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {

			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
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

	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
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
		// No task associated with the ImageView, or an existing task was
		// cancelled
		return true;
	}
	
	public void loadBitmap(Resources res,int resId, ImageView imageView) {

		// why here we use final
		final String imageKey = String.valueOf(resId);
		final Bitmap bitmap = BitmapTool.getBitmapFromMemCache(imageKey);

		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		}
		// In this case, aBitmapDrawable is used so that a placeholder （占位）image
		// can be displayed in the ImageView while the task completes
		// default bitmap is null
		else if (BitmapTool.cancelPotentialWork(resId, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(res,imageView);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(
					res, null, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(resId);
		}
	}
	

}
