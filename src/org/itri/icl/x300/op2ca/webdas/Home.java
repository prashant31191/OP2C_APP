package org.itri.icl.x300.op2ca.webdas;

import java.util.List;

import javax.inject.Inject;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.ShareAdapter;
import org.itri.icl.x300.op2ca.data.Resource;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.dialog.YesNoDialog;
import org.itri.icl.x300.op2ca.utils.OnCriteriaChangeListener;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.linphone.InCallActivity;
import org.linphone.IncomingCallActivity;
import org.linphone.KeepAliveHandler;
import org.linphone.LinphoneActivity;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneService;
import org.linphone.LinphoneUtils;
import org.linphone.LinphoneManager.AddressType;
import org.linphone.LinphoneSimpleListener.LinphoneOnCallStateChangedListener;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCore;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.AndroidVideoWindowImpl.VideoDisplayListener;
import org.linphone.mediastream.video.display.GL2JNIView;
import org.linphone.ui.AddressText;

import roboguice.inject.InjectView;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.j256.ormlite.android.extras.OrmliteListLoader;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;


@Log
public class Home extends OrmLiteRoboFragment<OpDB> implements LoaderCallbacks<List<Resource>>, OnCriteriaChangeListener, OnCheckedChangeListener, OnClickListener, OnItemClickListener, VideoDisplayListener, LinphoneOnCallStateChangedListener {
	@InjectView(R.id.listView) ListView mListView;
	@InjectView(R.id.textEmpty) TextView mTextEmpty;
	@InjectView(R.id.progEmpty) ProgressBar mProgEmpty;
	@InjectView(R.id.listEmpty) LinearLayout mListEmpty;
	@InjectView(R.id.lytVideo) RelativeLayout mVideoPanel;
	@InjectView(R.id.viewVideo) GLSurfaceView mVideoView;
	@InjectView(R.id.btnTalk) Button mBtnTalk;
	@InjectView(R.id.btnClose) Button mBtnClose;
	@InjectView(R.id.txtMesg) TextView mtxtMesg;
	
	private int mAlwaysChangingPhoneAngle = -1;
	ShareAdapter mAdapter;
	ToggleButton mBtnDelt;
	ImageButton mBtnBack, mBtnStop, mBtnFrid, mBtnSort;
	TextView mTextTitle;
	AndroidVideoWindowImpl mVideoDisplayWindow;
	
	Resource mResource;
	@Inject AlarmManager mAlarmMgr;
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		mBtnDelt = (ToggleButton)((Main)getActivity()).getButtonToggle();
		mBtnSort = ((Main)getActivity()).getButtonNext();
		mBtnBack = ((Main)getActivity()).getButtonBack();
		mBtnStop = ((Main)getActivity()).getButtonStop();
		mBtnFrid = ((Main)getActivity()).getButtonAddFriend();
		mTextTitle = ((Main)getActivity()).getTextTitle();
		mBtnStop.setVisibility(View.GONE);
		mBtnBack.setVisibility(View.GONE);
		mBtnFrid.setVisibility(View.GONE);
		mBtnSort.setVisibility(View.VISIBLE);
		mBtnDelt.setVisibility(View.VISIBLE);
		mTextTitle.setText("我的資源清單");
		mBtnDelt.setChecked(false);
		mBtnDelt.setOnCheckedChangeListener(this);
//		mBtnSort.setOnItemSelectedListener(this);
//		mBtnSort.setSelection(0);
		LinphoneManager.addListener(this);
		
		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		switch (rotation) {
		case Surface.ROTATION_0:
			rotation = 0;
			break;
		case Surface.ROTATION_90:
			rotation = 90;
			break;
		case Surface.ROTATION_180:
			rotation = 180;
			break;
		case Surface.ROTATION_270:
			rotation = 270;
			break;
		}

		LinphoneManager.getLc().setDeviceRotation(rotation);
		mAlwaysChangingPhoneAngle = rotation;
		LinphoneManager.getLc().setVideoWindow(null);
		
		return inflater.inflate(R.layout.op2c_home, c, false);
	}
	
	@Override @SneakyThrows
    public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);
		mListView.setEmptyView(mListEmpty);
		mAdapter = new ShareAdapter(getHelper());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mBtnClose.setOnClickListener(this);
		
		mBtnTalk.setOnClickListener(this);
		
		mVideoDisplayWindow = new AndroidVideoWindowImpl(mVideoView, null);
		mVideoDisplayWindow.setListener(this);
		mVideoDisplayWindow.init();
        getLoaderManager().initLoader(0, new Bundle(), this);
	}
	@Override @SneakyThrows
	public Loader<List<Resource>> onCreateLoader(int arg0, Bundle bundle) {
		QueryBuilder<Resource, Long> queryBuilder = getHelper().resDao().queryBuilder();
		switch (bundle.getInt("order", 0)) {
		case 1:
			queryBuilder.orderBy("createTime", false);break;
		case 2:
			queryBuilder.orderBy("like", false);break;
		default:
			queryBuilder.orderBy("expireTime", false);break;
		}
		PreparedQuery<Resource> preparedQuery = queryBuilder.prepare();
		return new OrmliteListLoader<Resource, Long>(getActivity(), getHelper().resDao(), preparedQuery);
	}

	@Override
	public void onLoadFinished(Loader<List<Resource>> arg0, List<Resource> res) {
		mAdapter.clear();
		mAdapter.addAll(res);
	}

	@Override
	public void onLoaderReset(Loader<List<Resource>> arg0) {}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mAdapter.setEditMode(isChecked);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnTalk) {
			
		} else if (v == mBtnClose) {
			mVideoPanel.setVisibility(View.GONE);
			hangUp();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		//hangUp();//先將目前通話掛斷
		
//		YesNoDialog mYesNoDialog = new YesNoDialog();
//		mYesNoDialog.show(getChildFragmentManager(), "yesNo");
		
		//TODO
		startOrientationSensor();
		//answer();
		
		
		//TODO
		Resource res = (Resource) arg0.getItemAtPosition(pos);
		res.setSender("88888");
		LinphoneManager.getInstance().newOutgoingPlay(res);
		
		
//		res.setSender("0000909111055");
//		res.setSender("10d");
		
		
//		AddressType address2 = new AddressText(getActivity(), null);
//		address2.setDisplayedName("88888");
//		address2.setText("88888");
//		LinphoneManager.getInstance().newOutgoingCall(address2);
		mListView.smoothScrollToPositionFromTop(pos, 0);
		
		
		
//		LinphoneManager.getInstance().routeAudioToSpeaker();
	}
	
	/* VideoWindowListener */
	@Override
	public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
		mVideoView = (GLSurfaceView) surface;
		LinphoneManager.getLc().setVideoWindow(vw);
	}

	@Override
	public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw) {
		if (LinphoneManager.getLc() != null) {
			LinphoneManager.getLc().setVideoWindow(null);
		}
	}
	
	/* copy from linphone's VideoCallFragment.java*/
	@Override
	public void onResume() {super.onResume();
		if (mVideoView != null) {
			((GLSurfaceView) mVideoView).onResume();
		}
//		
		log.warning("ready to set video window");
		if (mVideoDisplayWindow != null && LinphoneService.isReady()) {
			log.warning("set video window");
			synchronized (mVideoDisplayWindow) {
				LinphoneManager.getLc().setVideoWindow(mVideoDisplayWindow);
			}
		}
	}

	@Override
	public void onPause() {	
		if (mVideoDisplayWindow != null) {
			synchronized (mVideoDisplayWindow) {
				/*
				 * this call will destroy native opengl renderer which is used by
				 * androidVideoWindowImpl
				 */
				log.warning("停止...");
				LinphoneManager.getLc().setVideoWindow(null);
			}
		}
		if (mVideoView != null) {
			((GLSurfaceView) mVideoView).onPause();
		}
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		LinphoneManager.removeListener(this);
		if (mVideoView != null) {
			mVideoView = null;
		}
		if (mVideoDisplayWindow != null) { 
			// Prevent linphone from crashing if correspondent hang up while you are rotating
			mVideoDisplayWindow.release();
			mVideoDisplayWindow = null;
		}
		
		
		if (mOrientationHelper != null) {
			mOrientationHelper.disable();
			mOrientationHelper = null;
		}
		super.onDestroy();
	}
	
	private class LocalOrientationEventListener extends OrientationEventListener {
		public LocalOrientationEventListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(final int o) {
			if (o == OrientationEventListener.ORIENTATION_UNKNOWN) {
				return;
			}

			int degrees = 270;
			if (o < 45 || o > 315)
				degrees = 0;
			else if (o < 135)
				degrees = 90;
			else if (o < 225)
				degrees = 180;

			if (mAlwaysChangingPhoneAngle == degrees) {
				return;
			}
			mAlwaysChangingPhoneAngle = degrees;

			log.warning("Phone orientation changed to  " + degrees);
			int rotation = (360 - degrees) % 360;
			LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
			if (lc != null) {
				lc.setDeviceRotation(rotation);
				LinphoneCall currentCall = lc.getCurrentCall();
				if (currentCall != null && currentCall.cameraEnabled() && currentCall.getCurrentParamsCopy().getVideoEnabled()) {
					lc.updateCall(currentCall, null);
				}
			}
		}
	}
	OrientationEventListener mOrientationHelper;
	private synchronized void startOrientationSensor() {
		if (mOrientationHelper == null) {
			mOrientationHelper = new LocalOrientationEventListener(getActivity());
		}
		mOrientationHelper.enable();
	}
	@Override //LinphoneOnCallStateChangedListener
	public void onCallStateChanged(final LinphoneCall call, State state, String message) {
		if (state == State.IncomingReceived) {
			log.warning("接收...");
			new Handler(Looper.getMainLooper()).post(new Runnable() {
	            public void run() { 
	            	mVideoPanel.setVisibility(View.VISIBLE);
	            }});
//			startActivity(new Intent(getActivity(), IncomingCallActivity.class));
		} else if (state == State.StreamsRunning) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
	            public void run() { 
	            	mVideoPanel.setVisibility(View.VISIBLE);
	            }});
		} else if (state == State.OutgoingProgress || state == State.OutgoingEarlyMedia || state == State.OutgoingRinging) {
			log.warning("撥出前... " + message + " "+ call.getCurrentParamsCopy().getVideoEnabled());
		} else if (state == State.OutgoingInit) {
			log.warning("撥出... " + message + " " + call.getCurrentParamsCopy().getVideoEnabled());
//			startOrientationSensor();
//			LinphoneManager.getInstance().routeAudioToSpeaker();
//			log.warning("cid = " + call.getVideoStats());
//			LinphoneManager.getInstance().addVideo();
////			new Handler(Looper.getMainLooper()).post(new Runnable() {
////				@Override
////				public void run() {
//					LinphoneManager.getInstance().addVideo();
//					LinphoneManager.getInstance().routeAudioToSpeaker();
//					LinphoneCallParams params = call.getCurrentParamsCopy();
//					params.setVideoEnabled(true);
//					LinphoneManager.getLc().updateCall(call, params);
//				}
//			});
//        	
			
			if (call.getCurrentParamsCopy().getVideoEnabled()) {
//				LinphoneCallParams params = call.getCurrentParamsCopy();
//	    		params.setVideoEnabled(true);
//	    			
//	    			LinphoneManager.getLc().enableVideo(false, true);
	    		 
//	    		try {
//	    			LinphoneManager.getLc().acceptCallUpdate(call, params);
//	    		} catch (LinphoneCoreException e) {
//	    			e.printStackTrace();
//	    		}
				log.warning("撥出 video codec... ");// + call.getCurrentParamsCopy().getUsedVideoCodec().getMime());
				//startVideoActivity(call);
			} else {
				//startIncallActivity(call);
			}
//			LinphoneActivity.instance().startVideoActivity(call);
			
//			Intent intent = new Intent(getActivity(), InCallActivity.class);
//			intent.putExtra("VideoEnabled", true);
//			startOrientationSensor();
//			startActivity(intent);
			
		} else if (state == State.CallEnd || state == State.Error || state == State.CallReleased) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
	            public void run() { 
	            	mVideoPanel.setVisibility(View.GONE);
	            }});
			// Convert LinphoneCore message for internalization
			if (message != null && message.equals("Call declined.")) { 
				log.warning(getString(R.string.error_call_declined));
			} else if (message != null && message.equals("User not found.")) {
				log.warning(getString(R.string.error_user_not_found));
			} else if (message != null && message.equals("Incompatible media parameters.")) {
				log.warning(getString(R.string.error_incompatible_media));
			}
//			resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
		}
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
	public void onCriteriaChange(String criteria) {
		Bundle bundle = new Bundle();
		bundle.putString("order", criteria);
		getLoaderManager().restartLoader(0, bundle, this);
	}
	
	LinphoneCall mCall;
	private void answer() {
		log.warning("answer...");
		if (LinphoneManager.getLcIfManagerNotDestroyedOrNull() != null) {
			List<LinphoneCall> calls = LinphoneUtils.getLinphoneCalls(LinphoneManager.getLc());
			for (LinphoneCall call : calls) {
				log.warning("answer0..." + mCall);
				if (State.IncomingReceived == call.getState()) {
					mCall = call;
					log.warning("answer..." + mCall);
					
					break;
				}
			}
		}
		if (mCall == null) {
			log.severe("Couldn't find incoming call");
//			finish();
			return;
		}
		LinphoneCallParams params = LinphoneManager.getLc().createDefaultCallParameters();
		if (mCall != null && mCall.getRemoteParams() != null && mCall.getRemoteParams().getVideoEnabled() && LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests()) {
			params.setVideoEnabled(true);
			log.severe("answer  true");
		} else {
			log.severe("answer  false");
			params.setVideoEnabled(false);
		}
		log.severe("answer  mgr1");
		LinphoneManager mgr = LinphoneManager.getInstance();
		log.severe("answer  mgr2" + mgr);
		if (!mgr.acceptCallWithParams(mCall, params)) {
			log.warning("answer...acceptCallWithParams();");
			// the above method takes care of Samsung Galaxy S
			App.makeToast(getString(R.string.couldnt_accept_call));
		} else {
			log.severe("answer  mgr3");
			final LinphoneCallParams remoteParams = mCall.getRemoteParams();
			if (remoteParams != null && remoteParams.getVideoEnabled() && LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests()) {
				//LinphoneActivity.instance().startVideoActivity(mCall);
				mVideoDisplayWindow.requestRender();
			} else {
				mVideoDisplayWindow.requestRender();
				//LinphoneActivity.instance().startIncallActivity(mCall);
			}
		}
	}
	
	private void decline() {
		LinphoneManager.getLc().terminateCall(mCall);
	}
}
