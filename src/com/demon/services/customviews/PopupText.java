package com.demon.services.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.demon.services.LogCatTool;
import com.demon.services.LogCatTool.Type;
import com.demon.services.bitmap.utils.BitmapTool;
import com.google.android.gms.R;

public class PopupText extends View implements OnTouchListener{

	// debug
	private static final String TAG = PopupText.class.getSimpleName();
	private static final boolean d = true;

	// PopupText 所需要的参数
	private String mTitle;
	
	private Bitmap mUnPressedBitmap;
	private Bitmap mPressedBitmap;

	// 默认字体大小 12sp
	private int mTextSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
	private Rect mTextBound;
	private Paint mTextPaint;
	
	//press boolean
	private boolean pressed =false;

	public PopupText(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("ClickableViewAccessibility") public PopupText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//初始化 监听器
		
		// 初始化 所有 被定义的属性
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.PopupText, 0, 0);

		/**
		 * cycle for getting all attributions
		 */
		try {

			mTitle = a.getString(R.styleable.PopupText_pptext);

		} finally {
			a.recycle();
		}
		//如果 胃传入 文字信息
		if(mTitle == null){
			mTitle ="无任何内容";
		}
		
		// 传入的图片 png 格式 无需做任何处理
		mUnPressedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chatto_bg_normal);
		mPressedBitmap =BitmapFactory.decodeResource(getResources(), R.drawable.chatto_bg_focused);
		// 测量 传入 text 的大小，从而自定义 view 的大小
		mTextBound = new Rect();
		mTextPaint = new Paint();
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0Xff000000);
		mTextPaint.getTextBounds(mTitle, 0, mTitle.length(), mTextBound);
		setOnTouchListener(this);
	}

	public PopupText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 必须设置 视图大小 wrap_content 将显得没有作用
		// 选择 固定视图大小
		// 1、获取传入 字符的长度，用于初始化图片的长度
		// 2、获取传入图片的初始长度，用去确定字符的中心位置
		int iconWidth = 0;
		int iconHeight = 0;
		if (mTitle != null) {

			// 严谨点 需要判断 mtextBound 以及 mBitmap 各自宽和高的大小
			iconWidth = mTextBound.width() + SINGLE_PADDING_TO_LEFT * 2
					+ SPECIFICY_ERRO;
			iconHeight = mTextBound.height() + SINGLE_PADDINT_TO_BOTTOM
					+ SINGLE_PADDING_TO_TOP;
			LogCatTool.show(TAG, "图片的宽和高分别是：" + "(" + iconWidth + ","
					+ iconHeight + ")", d, Type.DEBUG);
		}
		// 组件 根据 图片的大小 自行设置大小。 即完成了wrap_content的功能
		setMeasuredDimension(iconWidth, iconHeight);

		LogCatTool.show(TAG, "重设后 自定义view的宽和高分别是：" + "(" + getMeasuredWidth()
				+ "," + getMeasuredHeight() + ")", d, Type.DEBUG);
	}

	//用来改变 文字信息 与 图片 之间的间距
	private static final int SINGLE_PADDING_TO_LEFT = 15;
	private static final int SINGLE_PADDINT_TO_BOTTOM = 8;
	private static final int SINGLE_PADDING_TO_TOP = 30;
	
	//修正图片本身 相对与组件的左误差
	private static final int SPECIFICY_ERRO = 10;

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		// 可以在同一张 画布上 展示 内容了哈，重复写canvas.drawBitmap即可
		//位选中的图片
		if(pressed)
			pressed(canvas);
		else
			unPressed(canvas);
	}
	
	private void pressed(Canvas canvas){
		canvas.drawBitmap(BitmapTool.createScaleBitmap(mPressedBitmap,
				getMeasuredWidth(), getMeasuredHeight()), 0 - SPECIFICY_ERRO,
				0, null);
		canvas.drawText(mTitle, 0 + SINGLE_PADDING_TO_LEFT + SPECIFICY_ERRO,
				SINGLE_PADDING_TO_TOP, mTextPaint);
	}
	
	private void unPressed(Canvas canvas){
		canvas.drawBitmap(BitmapTool.createScaleBitmap(mUnPressedBitmap,
				getMeasuredWidth(), getMeasuredHeight()), 0 - SPECIFICY_ERRO,
				0, null);
		canvas.drawText(mTitle, 0 + SINGLE_PADDING_TO_LEFT + SPECIFICY_ERRO,
				SINGLE_PADDING_TO_TOP, mTextPaint);
	}
	
	public void setPressed(boolean pressed){
		this.pressed  =pressed;
		invalidateView();
	}
	
	/*
	 * 
	 */
	public void setHostTitle(String mTitle){
		this.mTitle =mTitle;
		//每次 更新title后需要 重新修改下textbound
		mTextBound = new Rect();
		mTextPaint = new Paint();
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0Xff000000);
		mTextPaint.getTextBounds(mTitle, 0, mTitle.length(), mTextBound);
		invalidateView();
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
			pressed =true;
			LogCatTool.showDefault("按键被按下", d);
			invalidateView();
			break;
		case MotionEvent.ACTION_UP:
			pressed =false;
			invalidateView();
			break;
		}
		
		return true;
	}
}
