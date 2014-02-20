/**
 * 
 */
package uk.co.craigwarren.megaman;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/**
 * @author craig
 * 
 */
public class MegaManAnimated extends Drawable{

	private static final String TAG = MegaManAnimated.class.getSimpleName();
	private Bitmap bitmap; // the animation sequence
	private Rect sourceRect; // the rectangle to be drawn from the animation
								// bitmap
	private Rect destRect;
	private Rect futureDestRect;
	private int frameNr; // number of frames in animation
	private int currentFrame; // the current frame
	private long frameTicker; // the time of the last frame update
	private int framePeriod; // milliseconds between each frame (1000/fps)
	private int spriteWidth; // the width of the sprite to calculate the cut out
								// rectangle
	private int spriteHeight; // the height of the sprite
	private boolean active;
	private boolean backwards;
	private boolean animating;
	private float scale;
	
	private int destHeight;
	private int destWidth;
	
	Handler mAnimationHandler;
	Runnable mAnimationRunnable;
	
	public MegaManAnimated(Bitmap bitmap, int fps,
			int frameCount) {
		scale = 3.0f;
		this.bitmap = bitmap;
		currentFrame = 0;
		frameNr = frameCount;
		spriteWidth = bitmap.getWidth() / frameCount;
		spriteHeight = bitmap.getHeight();
		destHeight = Math.round(spriteHeight * scale);
		destWidth = Math.round(spriteWidth * scale);
		sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
		destRect = new Rect(0, 0, destWidth, destHeight);
		futureDestRect = new Rect(destRect);
		framePeriod = 1000 / fps;
		frameTicker = 0l;
		backwards = false;
		animating = false;

		mAnimationHandler = new Handler();
		mAnimationRunnable = new Runnable() {

			@Override
			public void run() {
				if (active) {
					boolean finished = update(SystemClock.uptimeMillis());
					if (!finished) {
						mAnimationHandler.postDelayed(mAnimationRunnable, 20);
					}
				}
			}
		};
	}
	
	private boolean update(long gameTime) {
		boolean reachedEnd = false;
		if (gameTime > frameTicker + framePeriod) {
			frameTicker = gameTime;
			// increment the frame
			if(backwards) {
				currentFrame--;
			} else {
				currentFrame++;
			}
			if (currentFrame >= frameNr) {
				currentFrame = frameNr -1;
				backwards = true;
				reachedEnd = true;
				animating = false;
			}
			if(currentFrame < 0) {
				currentFrame = 0;
				backwards = false;
				destRect = new Rect(futureDestRect);
			}
			Log.d(TAG, "Now on animation frame "+ currentFrame);
			invalidateSelf();
			return reachedEnd;
		}
		// define the rectangle to cut out sprite
		this.sourceRect.left = currentFrame * spriteWidth;
		this.sourceRect.right = this.sourceRect.left + spriteWidth;
		return reachedEnd;
	}
	
	public void start() {
		active = true;
		animating = true;
		mAnimationHandler.post(mAnimationRunnable);
	}
	
	public void stop() {
		active = false;
		animating = false;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
		
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		
	}
	
	public void touched(int x, int y) {
		if(animating || !active) {
			Log.d(TAG, "Ignoring touch because we are animating");
		} else {
			Log.d(TAG, "Received touch event "+x+","+y);
			Log.d(TAG, "destWidth = "+destWidth);
			Log.d(TAG, "destHeight = "+destHeight);
			futureDestRect.set(x - (destWidth/2), y - (destHeight), x + (destWidth/2), y);
			Log.d(TAG,"Teleport to "+futureDestRect);
			animating = true;
			mAnimationHandler.post(mAnimationRunnable);
		}
	}



}
