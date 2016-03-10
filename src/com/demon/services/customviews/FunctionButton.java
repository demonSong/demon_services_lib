package com.demon.services.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.demon.services.bitmap.utils.BitmapTool;
import com.google.android.gms.R;

public class FunctionButton extends View implements OnTouchListener{

	// functionButton
	private String mTitle;
	private Bitmap mIcon;
	private int mRectSize;
	private Integer mIconId;

	// 绘画
	private Rect mIconRect;
	private Rect mTextBound;
	private Paint mTextPaint;
	// 默认字体大小 为12sp
	private int mTextSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());

	public FunctionButton(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("ClickableViewAccessibility") 
	public FunctionButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

		//初始化监听器
		setOnTouchListener(this);
		
		// 初始化 所有 被定义的属性
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.FunctionButton, 0, 0);

		/**
		 * cycle for getting all attributions
		 */
		try {

			mTitle = a.getString(R.styleable.FunctionButton_fb_title);
			mIconId = a.getResourceId(R.styleable.FunctionButton_fb_icon, 0);
			mRectSize = (int) a.getDimension(
					R.styleable.FunctionButton_fb_rect, 25);

		} finally {
			a.recycle();
		}
		// 如果 未传入 文字信息
		if (mTitle == null) {
			throw new NullPointerException("functionButton 未设置任何title信息");
		}

		// 初始化 bitmap的信息
		mIcon = BitmapFactory.decodeResource(getResources(), mIconId);
		// 根据实际传入大小改变图片大小

		// 初始化文字信息
		mTextBound = new Rect();
		mTextPaint = new Paint();
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0Xffffffff);
		mTextPaint.getTextBounds(mTitle, 0, mTitle.length(), mTextBound);
	}

	public FunctionButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	// 画图内容 需要一定的padding
	private static final int SINGLE_PADDING = 10;

	@SuppressLint("DrawAllocation") @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int fbWidth = Math.max(mRectSize,mTextBound.width());

		int fbHeight = mRectSize + mTextBound.height();

		setMeasuredDimension(fbWidth + SINGLE_PADDING * 2, fbHeight
				+ SINGLE_PADDING * 2);
		mIconRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
	}

	// 画图所需要的画笔

	@SuppressLint("DrawAllocation") @Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Paint mPaint = new Paint();
		mPaint.setColor(Color.GRAY);
		mPaint.setAlpha(150);
		canvas.drawRect(mIconRect, mPaint);
		
		// draw functionIcon
		canvas.drawBitmap(setupTargetBitmap(pressed), (getMeasuredWidth()-mRectSize)/2, SINGLE_PADDING, null);
		// draw Title
		canvas.drawText(mTitle, (getMeasuredWidth()-mTextBound.width())/2, SINGLE_PADDING + mRectSize
				+ mTextBound.height(), mTextPaint);

	}
	
	
	private boolean pressed =false;
	
	public void setPressed(boolean pressed){
		this.pressed =pressed;
		invalidateView();
	}
	
	/**
	 * 在内存中绘制可变色的Icon
	 */
	private Bitmap setupTargetBitmap(boolean pressed)
	{
	
		Rect mRect= new Rect(0,0,mRectSize,mRectSize);
		
		if(!pressed){
			Bitmap newBitmap = Bitmap.createBitmap(mRectSize, mRectSize,
					Config.ARGB_8888);
			Canvas mCanvas = new Canvas(newBitmap);
			Paint mPaint = new Paint();
			//图片信息为白色
			mPaint.setColor(0xFFffffff);
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mCanvas.drawRect(mRect, mPaint);
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
			mPaint.setAlpha(255);
			mCanvas.drawBitmap(BitmapTool.createScaleBitmap(mIcon, mRectSize, mRectSize), null, mRect, mPaint);
			
			return newBitmap;
		}else{
			Bitmap newBitmap = Bitmap.createBitmap(mRectSize, mRectSize,
					Config.ARGB_8888);
			Canvas mCanvas = new Canvas(newBitmap);
			Paint mPaint = new Paint();
			//图片信息为蓝色
			mPaint.setColor(0xFF0099FF);
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mCanvas.drawRect(mRect, mPaint);
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
			mPaint.setAlpha(255);
			mCanvas.drawBitmap(BitmapTool.createScaleBitmap(mIcon, mRectSize, mRectSize), null, mRect, mPaint);
			
			return newBitmap;
		}
		
	}
	
	/**
	 * 重绘
	 */
	private void invalidateView()
	{
		if (Looper.getMainLooper() == Looper.myLooper())
		{
			invalidate();
		} else
		{
			postInvalidate();
		}
	}

	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			//按键被按下后
			setPressed(true);
			break;
		case MotionEvent.ACTION_UP:
			setPressed(false);
			break;
		}
		return true;
	}
}
