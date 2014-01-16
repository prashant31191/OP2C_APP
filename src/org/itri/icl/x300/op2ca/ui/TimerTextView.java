package org.itri.icl.x300.op2ca.ui;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import lombok.extern.java.Log;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

@Log
public class TimerTextView extends TextView {
	SimpleDateFormat mFmt = new SimpleDateFormat("MM/dd, hh:mm:ss");
	Timer mTimer;
	String mFmtText;
	public TimerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			public void run() {
				setText(System.currentTimeMillis());
			}
		}, 0, 1000); 
	}
	
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mTimer.cancel();
	}
	
	private void setText(long time) {
		mFmtText = mFmt.format(time);
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				TimerTextView.this.setText(mFmtText);
			}
			
		});
	}
	
	public void pause() {
		mTimer.cancel();
	}
}
