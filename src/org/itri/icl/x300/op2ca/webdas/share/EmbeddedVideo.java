package org.itri.icl.x300.op2ca.webdas.share;

import java.util.List;

import javax.inject.Inject;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.data.Device;
import org.itri.icl.x300.op2ca.data.ext.ContactArg;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.utils.CloudPlay.OpenListener;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.webdas.Main;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneService;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.AndroidVideoWindowImpl.VideoPreviewListener;

import roboguice.inject.InjectView;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

@Log
public class EmbeddedVideo extends OrmLiteRoboFragment<OpDB> implements VideoPreviewListener, OnClickListener, OpenListener {
	
//	@InjectView(R.id.videoCapture) SurfaceView mViewCap; //GLSurfaceView
	@InjectView(R.id.videoCapture) GLSurfaceView mViewCap;
	@InjectView(R.id.txtTitle) TextView mTitle;
	@InjectView(R.id.txtDate) TextView mDate;
	@InjectView(R.id.btnSwitch) ImageButton mBtnSwitch;
	@InjectView(R.id.imgRecord) ImageView mImgRecord;
	@InjectView(R.id.btnRecord) ImageButton mBtnRecord;
	@InjectView(R.id.btnCancel) ImageButton mBtnCancel;
	
	AndroidVideoWindowImpl mVideoPreviewWindow;
	
	List<Device> mDevice;
	@Inject CloudPlay mPlay;
	List<ContactArg> mPeople;
	public EmbeddedVideo(List<Device> device, List<ContactArg> people) {
		mDevice = device;
		mPeople = people;
		
	}
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		
		return inflater.inflate(R.layout.op2c_share_video, c, false);
	}
	
	@Override
    public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);
		mVideoPreviewWindow = new AndroidVideoWindowImpl(mViewCap, null);
		mVideoPreviewWindow.setListener(this);
		mVideoPreviewWindow.init();
		mBtnSwitch.setOnClickListener(this);
		mBtnRecord.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
	}
	
	public Button getShareButton() {
		return null;
//		return mBtnShare;
	}
	
	public Button getCancelButton() {
		return null;
//		return mBtnCancel;
	}

	@Override
	public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
		mViewCap = (GLSurfaceView)surface;
		LinphoneManager.getLc().setPreviewWindow(mViewCap);
		
	}

	@Override
	public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw) {
		LinphoneManager.getLc().setPreviewWindow(null);
	}
	
	public void setTitle(String title) {
		String typedText = title.toString().trim();
		mTitle.setText(typedText.isEmpty() ? "無主旨" : typedText);
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnSwitch) {
			App.makeToast(mDevice.get(0).next().getText());
		} else if (v == mBtnCancel) {
			hangUp();
			getFragmentManager().beginTransaction().remove(this).commit();
			mDevice.clear();
		} else if (v == mBtnRecord) {
			if (mPeople.isEmpty()) {
				App.makeToast("尚未選定聯絡人");
				return;
			} else {
				mPlay.ready();
				((Main)getActivity()).replace(new ShareStream());
			}
//			LinphoneChatRoom room = LinphoneManager.getLc().getOrCreateChatRoom("sip:cloudplay@140.96.116.226");
////			Injector injector = Guice.createInjector(new Module());
////	        Operations factory = injector.getInstance(Operations.class);
//	        ShareRtpAv share_av = mOp.rtpAv(SHARE_RTPAV.ready, new CData());
//	        share_av.receivers("10e");
//	        ObjectMapper om = new ObjectProvider().getContext(this.getClass());
//	        try {
//	        	String msg = om.writeValueAsString(share_av);
//	        	room.sendMessage(msg);
//	        } catch (Exception e) {
//	        	
//	        }
		} 
//		if (v == mBtnCancel) {
////			mLytPreview.setVisibility(View.INVISIBLE);
//		} else if (v == mBtnShare) {
////			LinphoneManager.getInstance().newOutgoingPlay(res);
////			((Main)getActivity()).prev();
//		}
	}
	
	private void hangUp() {
		LinphoneCore lc = LinphoneManager.getLc();
		LinphoneCall currentCall = lc.getCurrentCall();
		LinphoneManager.getLc().setVideoWindow(null);
		if (currentCall != null) {
			lc.terminateCall(currentCall);
		} else if (lc.isInConference()) {
			lc.terminateConference();
		} else {
			lc.terminateAllCalls();
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
	public void onOpen() {
		
	}
}
