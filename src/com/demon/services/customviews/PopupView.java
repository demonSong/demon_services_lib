package com.demon.services.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import com.demon.services.LogCatTool;
import com.demon.services.LogCatTool.Type;
import com.demon.services.bitmap.utils.BitmapTool;
import com.google.android.gms.R;

/**
 * 这样的 Popupview 是否有必要 重新 由自己的绘制 
 * 1、根据资源id 所传入的图片，系统默认 是不会进行压缩处理的，尤其是 jpg文件
 * 因此，需要自定义view 来对resid 传入的图片做压缩处理 
 * 2、由于 父组件与子视图的相对位置还不确定，因此需要做些，位置修正。
 * 3、目前该类 还存在 巨大问题、这跟传入图片的大小 还有一定的关系！
 * @author Administrator
 * 
 */
public class PopupView extends View {

	// debug
	private static final String TAG = PopupView.class.getSimpleName();
	private static final boolean d = true;

	// imageContent resource
	private Bitmap mImageContent;

	// draw assists
	private Integer mImageContentId;

	// default iconWidth and iconHeight
	// 这里 是否需要定义这样的默认值呢？
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 80;

	// initialize all the constructor
	public PopupView(Context context) {
		this(context, null);
	}

	/**
	 * 提取自定义属性
	 * 
	 * @param context
	 * @param attrs
	 * 
	 *            提取属性容易，但如何让这个自定义view默认加载background
	 */
	public PopupView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PopupView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.PopupView, 0, 0);

		/**
		 * cycle for getting all attributions
		 */
		try {

			mImageContentId = a.getResourceId(
					R.styleable.PopupView_imageContent, 0);

		} finally {
			a.recycle();
		}

	}

	public void setImageContentInPopupView(int resId) {

		mImageContent = BitmapTool.decodeSampledBitmapFromResource(
				getResources(), resId, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		invalidateView();
	}

	/**
	 * 重绘
	 */
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}

	/**
	 * 重新测量Popup View 的长宽高，根据layout_width,layout_height,padding的参数有关
	 * 
	 * 有了widthMeasureSpec以及heightMeasureSpec便可以得知所绘画图片的大小 初始化 Rect 矩阵，准备进行绘画
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int iconWidth = getMeasuredWidth() - getPaddingLeft()
				- getPaddingRight();
		int iconHeight = getMeasuredHeight() - getPaddingTop()
				- getPaddingBottom();
		LogCatTool.show(TAG, "默认icon的宽度和高度分别为：" + iconWidth + ":" + iconHeight,
				d, Type.DEBUG);
		int left = getMeasuredWidth() / 2 - iconWidth / 2;
		int top = getMeasuredHeight() / 2 - iconHeight / 2;
		
		LogCatTool.show(TAG, "icon的起始坐标：" + "("+left + "," + top+")",
				d, Type.DEBUG);
		
		mImageContent = BitmapTool.decodeSampledBitmapFromResource(
				getResources(), mImageContentId, iconWidth+100, iconHeight-38);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawBitmap(mImageContent, 0, 0, null);
	}
}
