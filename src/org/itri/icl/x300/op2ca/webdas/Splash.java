package org.itri.icl.x300.op2ca.webdas;

import javax.inject.Inject;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.webdas.mon.SyncPeople;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneManager.EcCalibrationListener;
import org.linphone.LinphoneService;
import org.linphone.core.LinphoneCore.EcCalibratorStatus;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

@Log
@ContentView(R.layout.op2c_splash)
public class Splash extends RoboActivity implements EcCalibrationListener {

	private Handler mHandler = new Handler();
	private ServiceWaitThread mThread;
	@Inject
	SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (LinphoneService.isReady()) {
			onServiceReady();
		} else {
			// start linphone as background
			// startService(new Intent(ACTION_MAIN).setClass(this,
			// LinphoneService.class));
			startService(new Intent(this, LinphoneService.class));
			startService(new Intent(this, SyncPeople.class));
			mThread = new ServiceWaitThread();
			mThread.start();
		}
	}

	protected void onServiceReady() {
		mHandler.postDelayed(new Runnable() {
				@Override
				@SneakyThrows
				public void run() {
					LinphoneManager.getInstance().startEcCalibration(Splash.this);
					startActivity(new Intent(Splash.this, Main.class));
					finish();
				}
			}, 1000);
		
	}

	private class ServiceWaitThread extends Thread {
		public void run() {
			while (!LinphoneService.isReady()) {
				try {
					sleep(30);
				} catch (InterruptedException e) {
					throw new RuntimeException(
							"waiting thread sleep() has been interrupted");
				}
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					onServiceReady();
				}
			});
			mThread = null;
		}
	}

	@Override
	public void onEcCalibrationStatus(EcCalibratorStatus status, int delayMs) {
		log.warning("ec = " + status.value());
	}
}
