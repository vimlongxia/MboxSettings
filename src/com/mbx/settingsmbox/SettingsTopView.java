package com.mbx.settingsmbox;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mbx.settingsmbox.SettingsMboxActivity;


public class SettingsTopView extends RelativeLayout {
	private String TAG = "SettingsTopView";

	public static boolean isHasFocus = false;

	public static boolean isNeedAnimation = true;

	private Context mContext = null;

	public static ImageView mCursorView = null;
	private FocusMoveAnimationListener animationListener;

	private int fromRealX = -1;
	private int fromRealY = -1;
	private int toRealX = -1;
	private int toRealY = -1;

	private int cursorWidth = 300;
	private int cursorHeight = 178;

	private int offset_x = 170;
	private int offset_y = 0;

	public static LinearLayout settingsContentLayout_01 = null;
	public static LinearLayout settingsContentLayout_02 = null;
	public static LinearLayout settingsContentLayout_03 = null;
	public static LinearLayout settingsContentLayout_04 = null;

	public static SettingsTopView preFocusView = null;

	private static float SCALE_RATE = 1.05f;

	public SettingsTopView(Context context) {
		super(context);
		mContext = context;
		animationListener = new FocusMoveAnimationListener(mContext, this);

	}

	public SettingsTopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		animationListener = new FocusMoveAnimationListener(mContext, this);
	}

	public SettingsTopView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		animationListener = new FocusMoveAnimationListener(mContext, this);
	}

	public static void setCursorView(ImageView cursor) {
		mCursorView = cursor;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {

		if (gainFocus) {
			Rect mCurfocusedRect = new Rect();
			this.getGlobalVisibleRect(mCurfocusedRect);
			int curWidth = mCurfocusedRect.width();
			int curHeight = mCurfocusedRect.height();
			int curLeft = mCurfocusedRect.left;
			int curTop = mCurfocusedRect.top +1;
			mCursorView.setVisibility(View.VISIBLE);
			AbsoluteLayout.LayoutParams parmas = new AbsoluteLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
			parmas.width = cursorWidth;
			parmas.height = cursorHeight;
			parmas.x = curLeft - (cursorWidth - curWidth) / 2;
			parmas.y = curTop - (cursorHeight - curHeight) / 2;
			mCursorView.setLayoutParams(parmas);
			this.getChildAt(0).setScaleX(1.3f);
			this.getChildAt(0).setScaleY(1.1f);
			((TextView) this.getChildAt(1)).setTextSize(28);

			if (preFocusView != null) {
				if (preFocusView.getId() == this.getId()) {
					return;
				}

				((SettingsMboxActivity)mContext).setViewVisable(this);

				Rect mPrefocusedRect = new Rect();
				preFocusView.getGlobalVisibleRect(mPrefocusedRect);
				int preWidth = mPrefocusedRect.width();
				int preHeight = mPrefocusedRect.height();
				int preLeft = mPrefocusedRect.left;
				int preRight = mPrefocusedRect.right;
				int preTop = mPrefocusedRect.top;

				int move_x = curLeft - preLeft;
				int move_y = curTop - preTop;

				float move_from_x = 0f;
				float move_to_x = 0f;
				if (direction == View.FOCUS_RIGHT) {
					move_from_x = 0f - preWidth + 25;
					move_to_x = move_x - curWidth + 25;

				} else if (direction == View.FOCUS_LEFT) {
					move_from_x = 0f + preWidth;
					move_to_x = move_x + curWidth + 20;
				}
				TranslateAnimation translateAnimation = new TranslateAnimation(
						move_from_x - 20, move_to_x - 20, 0f, move_y);

				translateAnimation.setFillAfter(true);

				translateAnimation.setDuration(200);

				translateAnimation.setAnimationListener(animationListener);

				ScaleAnimation scaleAnimation = new ScaleAnimation(0.95f, 1f,
						0.95f, 1f);

				scaleAnimation.setDuration(200);

				AnimationSet mAnimationSet = new AnimationSet(true);
				mAnimationSet.addAnimation(scaleAnimation);
				mAnimationSet.addAnimation(translateAnimation);

				// animationListener.setCursorSize(cursorWidth,
				// cursorHeight,newLeft, newTop);
				// animationListener.setCursorView(mCursorView);

				animationListener.setPrefocusView(preFocusView);
				mCursorView.startAnimation(mAnimationSet);

				// this.getChildAt(0).setBackgroundResource(R.drawable.shadow2);
			} else {

				Log.d(TAG, "==== first focus view ");
				this.getChildAt(0).setScaleX(1.3f);
				this.getChildAt(0).setScaleY(1.1f);
				((TextView) this.getChildAt(1)).setTextSize(28);

			}

			preFocusView = this;

		} else {
			Log.d(TAG, "===== gainFocus false");
			mCursorView.setVisibility(View.GONE);
		}

		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

}
