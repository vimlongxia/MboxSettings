package com.mbx.settingsmbox;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class FocusMoveAnimationListener implements AnimationListener {

	private String TAG = "FocusMoveAnimationListener";
	private Context mContext = null;
	private ViewGroup mRootView = null;

	private ImageView mCursorView = null;

	private ViewGroup mPreFocusView = null;

	int width;
	int higth;
	int toX;
	int toY;

	public FocusMoveAnimationListener(Context context) {
		mContext = context;
	}

	public FocusMoveAnimationListener(Context context, ViewGroup parentView) {
		mContext = context;
		mRootView = parentView;

	}

	public void setCursorView(ImageView cursor) {
		mCursorView = cursor;
	}

	public void setPrefocusView(ViewGroup preFocusView) {
		mPreFocusView = preFocusView;
	}

	public void setCursorSize(int w, int h, int to_x, int to_y) {
		width = w;
		higth = h;
		toX = to_x;
		toY = to_y;
	}

	@Override
	public void onAnimationEnd(Animation animation) {

		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
		//lp.width = width;
		//lp.height = higth;
		//lp.x = toX;
		//lp.y = toY;
		//mCursorView.setLayoutParams(lp);
		//mCursorView.setVisibility(View.VISIBLE);
		mRootView.getChildAt(0).setScaleX(1.3f);
		mRootView.getChildAt(0).setScaleY(1.1f);
		((TextView) mRootView.getChildAt(1)).setTextSize(28);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (mPreFocusView != null) {

			mPreFocusView.getChildAt(0).setScaleX(1f);
			mPreFocusView.getChildAt(0).setScaleY(1f);
			((TextView) mPreFocusView.getChildAt(1)).setTextSize(25);
		}

	}

}
