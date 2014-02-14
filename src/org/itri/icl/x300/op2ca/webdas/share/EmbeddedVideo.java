package org.itri.icl.x300.op2ca.webdas.share;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.ui.TimerTextView;
import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.utils.CloudPlay.OpenListener;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.utils.VideoControllerListener;
import org.itri.icl.x300.op2ca.webdas.Main;
import org.linphone.CallManager;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneService;
import org.linphone.LinphoneUtils;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCall.State;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.AndroidVideoWindowImpl.VideoPreviewListener;
import org.linphone.mediastream.video.AndroidVideoWindowImpl.VideoWindowListener;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;

import conn.Http;

import data.Contacts.Contact;
import data.OPInfos.OPInfo;
import data.Resources.Resource;

import roboguice.inject.InjectView;
import schema.element.Account;
import schema.element.CData;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@Log
public class EmbeddedVideo extends OrmLiteRoboFragment<OpDB> implements VideoWindowListener, OnClickListener, OpenListener {
	
	@InjectView(R.id.videoPreview) SurfaceView mViewPre;
	@InjectView(R.id.videoCapture) GLSurfaceView mViewCap;
	@InjectView(R.id.txtTitle) TextView mTitle;
	@InjectView(R.id.txtDate) TextView mDate;
	@InjectView(R.id.btnSwitch) ImageButton mBtnSwitch;
	@InjectView(R.id.imgRecord) ImageView mImgRecord;
	
	@InjectView(R.id.lytBtnReady) LinearLayout mLytReady;
	@InjectView(R.id.lytBtnStart) LinearLayout mLytStart;
	
	@InjectView(R.id.btnStart1) ImageButton mBtnStart1;
	@InjectView(R.id.btnPause) ImageButton mBtnPause;
	
	@InjectView(R.id.btnStop1) ImageButton mBtnStop1;
	@InjectView(R.id.btnStop2) ImageButton mBtnStop2;
	@InjectView(R.id.txtDate) TimerTextView mTimerView;
	AndroidVideoWindowImpl mVideoPreviewWindow;
	Resource resourceArg;
	List<Resource> mDevice;
	List<Contact> mPeople;
	@Inject Provider<CloudPlay> mPlay;
	@Inject Provider<Account> mAcct;
	@Inject Provider<Http> mHttp;
	VideoControllerListener listener;
	String mScene;
	boolean mStart = false; 
	public EmbeddedVideo(List<Resource> device, List<Contact> people, String scene, boolean start) {
		mDevice = device;
		if (!mDevice.isEmpty())
			resourceArg = mDevice.get(0);
		mPeople = people;
		mScene = scene;
		mStart = start;
	}
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		return inflater.inflate(R.layout.op2c_share_video, c, false);
	}
	
	@Override
    public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);
		mViewPre.setVisibility(View.VISIBLE);
		mViewCap.setVisibility(mScene.equalsIgnoreCase("remote") ? View.VISIBLE : View.GONE);
		mVideoPreviewWindow = new AndroidVideoWindowImpl(mViewCap, mViewPre);
		if (mStart) {
			mLytStart.setVisibility(View.VISIBLE);
			mLytReady.setVisibility(View.GONE);
		} else {
			mLytStart.setVisibility(View.GONE);
			mLytReady.setVisibility(View.VISIBLE);
		}
		//mBtnSwitch.setVisibility(resourceArg.getCapabilities().size() == 1 ? View.INVISIBLE : View.VISIBLE);
		mVideoPreviewWindow.setListener(this);
		mVideoPreviewWindow.init();
		mBtnSwitch.setOnClickListener(this);
		mBtnStart1.setOnClickListener(this);
		mBtnPause.setOnClickListener(this);
		mBtnStop1.setOnClickListener(this);
		mBtnStop2.setOnClickListener(this);
	}
	
	public Button getShareButton() {
		return null;
	}
	
	public Button getCancelButton() {
		return null;
	}

	
	public void setTitle(String title) {
		String typedText = title.toString().trim();
		mTitle.setText(typedText.isEmpty() ? "無主旨" : typedText);
	}

	public void setVideoControllerListener(VideoControllerListener listener) {
		this.listener = listener;
	}
	@Override
	public void onClick(View v) {
		if (v == mBtnSwitch) {
			switchCamera();App.makeToast(resourceArg.getCapabilities().size() + " size");
		} else if (v == mBtnStop1 && listener != null) {
			listener.onVideoStop();
			mDevice.clear();
		} else if (v == mBtnStart1 && listener != null) {
			listener.onVideoPlay();
			log.warning("寫入DB");
			OPInfo op = OPInfo.of(UUID.randomUUID().toString(), mAcct.get().getUsername(), "SELF", "立即分享", "video", "New Share Content");
			try {
				getHelper().infoDao().createIfNotExists(op);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			//mHttp.get().asyncSave(OPInfo.of(UUID.randomUUID().toString(), mAcct.get().getUsername(), "sender", "video", resourceArg.getUri(), mTitle.getText().toString()));
		} else if (v == mBtnStop2 && listener != null) {
			listener.onVideoStop();
			mTimerView.pause();
		} else if (v == mBtnPause && listener != null) {
			listener.onVideoPlay();
			
			//pauseOrResumeCall();
		}
	}
	
	
	private void pauseOrResumeCall() {
		LinphoneCore lc = LinphoneManager.getLc();
		LinphoneCall call = lc.getCurrentCall();
		if (call != null && LinphoneUtils.isCallRunning(call)) {
			if (call.isInConference()) {
				lc.removeFromConference(call);
				if (lc.getConferenceSize() <= 1) {
					lc.leaveConference();
				}
			} else {
				lc.pauseCall(call);
				//pause.setImageResource(R.drawable.pause_on);
			}
		} else {
			List<LinphoneCall> pausedCalls = LinphoneUtils.getCallsInState(lc, Arrays.asList(State.Paused));
			if (pausedCalls.size() == 1) {
				LinphoneCall callToResume = pausedCalls.get(0);
				if ((call != null && callToResume.equals(call)) || call == null) {
					lc.resumeCall(callToResume);
				}
			} else if (call != null) {
				lc.resumeCall(call);

			}
		}
	}
	
	@Override
	public void onResume() {super.onResume();	
		if (mVideoPreviewWindow != null && LinphoneService.isReady()) {
			synchronized (mVideoPreviewWindow) {
				LinphoneManager.getLc().setVideoWindow(mVideoPreviewWindow);
				mVideoPreviewWindow.requestRender();
			}
		}
	}

	@Override
	public void onPause() {	
		if (mVideoPreviewWindow != null) {
			synchronized (mVideoPreviewWindow) {
				LinphoneManager.getLc().setVideoWindow(null);
			}
		}
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		if (mViewCap != null) {
			mViewCap = null;
		}
		if (mVideoPreviewWindow != null) { 
			// Prevent linphone from crashing if correspondent hang up while you are rotating
			mVideoPreviewWindow.release();
			mVideoPreviewWindow = null;
		}
		super.onDestroy();
	}

	
	
	@Override
	public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
		LinphoneManager.getLc().setVideoWindow(vw);
		mViewCap = (GLSurfaceView) surface;
	}
	@Override
	public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw) {
		LinphoneCore lc = LinphoneManager.getLc(); 
		if (lc != null) {
			lc.setVideoWindow(null);
		}
	}
	@Override
	public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw) {
		LinphoneManager.getLc().setPreviewWindow(null);		
	}
	
	@Override
	public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
		mViewPre = surface;
		LinphoneManager.getLc().setPreviewWindow(mViewPre);
	}
	@Override
	public void onOpen() {
	}
	
	
	public void switchCamera() {
		try {
			int videoDeviceId = LinphoneManager.getLc().getVideoDevice();
			videoDeviceId = (videoDeviceId + 1) % AndroidCameraConfiguration.retrieveCameras().length;
			LinphoneManager.getLc().setVideoDevice(videoDeviceId);
			CallManager.getInstance().updateCall();
			
			// previous call will cause graph reconstruction -> regive preview
			// window
			if (mViewPre != null) {
				LinphoneManager.getLc().setPreviewWindow(mViewPre);
			}
		} catch (ArithmeticException ae) {
			//Log.e("Cannot swtich camera : no camera");
		}
	}
}
