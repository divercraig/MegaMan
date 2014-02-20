package uk.co.craigwarren.megaman;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity {
	
	MegaManAnimated mAnimation;
	View mView;
	private static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mView = findViewById(R.id.main_view);
		mAnimation = new MegaManAnimated(BitmapFactory.decodeResource(getResources(), R.drawable.arrive_40_40),20,13);
		mView.setBackground(mAnimation);
		mAnimation.start();	
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mAnimation.stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "Received touch event "+event.getX()+","+event.getY());
		mAnimation.touched(Math.round(event.getX()), Math.round(event.getY()));
		return super.onTouchEvent(event);
	}
	
	

}
