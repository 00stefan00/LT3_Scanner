package nl.circulairtriangles.scanner.animations;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

/**
 * @author 	Daniel Zijlstra - getConnected 1
 * @version 1.0			
 * @since	2013-10-20
 */

public class ExpandAnimation extends Animation implements Animation.AnimationListener {
	
	private View view;
	private static int ANIMATION_DURATION;
	private int LastHeight;
	private int ToHeight;
	
	/**
	 * Constructor
	 * @param v
	 * @param FromHeighth
	 * @param ToHeight
	 * @param Duration
	 */
	
	public ExpandAnimation(View v, int FromHeight, int ToHeight, int Duration) {
		
		this.view = v;
		this.ToHeight = ToHeight;
		ANIMATION_DURATION = 1;
		setDuration(ANIMATION_DURATION);
		setRepeatCount(20);
		setFillAfter(false);
		setInterpolator(new AccelerateInterpolator());
		setAnimationListener(this);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub	
	}

	/**
	 * Increase layout height on each step
	 */
	
	@Override
	public void onAnimationRepeat(Animation animation) {
	
		LayoutParams lyp =  view.getLayoutParams();
		lyp.height = LastHeight += ToHeight/20;
		view.setLayoutParams(lyp);
		view.requestFocus();
	}

	/**
	 * Set the height values to 0 on animation start 
	 * and bring view to the front
	 */
	
	@Override
	public void onAnimationStart(Animation animation) {
		view.refreshDrawableState();
		LayoutParams lyp =  view.getLayoutParams();
		lyp.height = 0;
		view.setLayoutParams(lyp);
		LastHeight = 0;
		
	}
}