package org.itri.icl.x300.op2ca.webdas;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.Module;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.ShareAdapter;
import org.itri.icl.x300.op2ca.data.ResourceV1;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.dialog.YesNoDialog;
import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.utils.CloudPlay.JoinListener;
import org.itri.icl.x300.op2ca.utils.OnCriteriaChangeListener;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.utils.WebTaskLoader;
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
import org.linphone.mediastream.video.AndroidVideoWindowImpl.VideoWindowListener;
import org.linphone.mediastream.video.capture.AndroidVideoApi5JniWrapper;
import org.linphone.mediastream.video.display.GL2JNIView;
import org.linphone.ui.AddressText;

import roboguice.inject.InjectView;
import schema.element.Account;
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

import com.google.common.base.Optional;
import com.j256.ormlite.android.extras.OrmliteListLoader;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.async.ITypeListener;
import com.sun.jersey.api.client.async.TypeListener;

import data.OPInfos.OPInfo;
import data.Resources.Resource;


@Log
public class Home extends OrmLiteRoboFragment<OpDB> implements JoinListener, LoaderCallbacks<List<OPInfo>>, OnCriteriaChangeListener, OnCheckedChangeListener, OnClickListener, OnItemClickListener, VideoWindowListener, LinphoneOnCallStateChangedListener {
	@InjectView(R.id.listView) ListView mListView;
	@InjectView(R.id.textEmpty) TextView mTextEmpty;
	@InjectView(R.id.progEmpty) ProgressBar mProgEmpty;
	@InjectView(R.id.listEmpty) LinearLayout mListEmpty;
	@InjectView(R.id.lytVideo) RelativeLayout mVideoPanel;
	@InjectView(R.id.viewVideo) GLSurfaceView mVideoView;
	
	@InjectView(R.id.prevVideo) SurfaceView mViewPre;
	@InjectView(R.id.btnTalk) Button mBtnTalk;
	@InjectView(R.id.btnClose) Button mBtnClose;
	@InjectView(R.id.txtMesg) TextView mtxtMesg;
	
	private int mAlwaysChangingPhoneAngle = -1;
	ShareAdapter mAdapter;
	ToggleButton mBtnDelt;
	ImageButton mBtnBack, mBtnStop, mBtnFrid, mBtnSort;
	TextView mTextTitle;
	AndroidVideoWindowImpl mVideoDisplayWindow;
	@Inject Provider<Account> mAcc;
	@Inject Provider<CloudPlay> mCloudPlay;
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
		mCloudPlay.get().addJoinListener(this);
		android.util.Log.e("chatroom",mCloudPlay + " " + mCloudPlay.get() + "join 2 listener size = " + mCloudPlay.get().getJoinListener().size());
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
		mVideoDisplayWindow = new AndroidVideoWindowImpl(mVideoView, mViewPre);
		
		mVideoDisplayWindow.setListener(this);
		mVideoDisplayWindow.init();
		mVideoPanel.setVisibility(LinphoneManager.getLc().isIncall() ? View.VISIBLE : View.GONE);
		//先秀出目前DB的資料，然後馬上呼叫webapi, 
		mCloudPlay.get().asyncListOPInfo(new TypeListener<List<OPInfo>>(new GenericType<List<OPInfo>>(){}) {
			@Override
			public void onComplete(Future<List<OPInfo>> arg0)
					throws InterruptedException {
				try{
					log.warning("info size = " + arg0.get().size());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0L, 100L);
		//getLoaderManager().initLoader(0, new Bundle(), this);
       // getLoaderManager().initLoader(1, new Bundle(), this);
	}
	
	
	
	//讀取硬碟列表 與 過濾
	@Override @SneakyThrows
	public Loader<List<OPInfo>> onCreateLoader(int arg0, Bundle bundle) {
		QueryBuilder<OPInfo, String> queryBuilder = getHelper().infoDao().queryBuilder();
		queryBuilder.where().ne("senderID", mAcc.get().getUsername());
		switch (bundle.getInt("order", 0)) {
		case 1:
			queryBuilder.orderBy("createTime", false);break;
		case 2:
			queryBuilder.orderBy("like", false);break;
		default:
			queryBuilder.orderBy("expireTime", false);break;
		}
		PreparedQuery<OPInfo> preparedQuery = queryBuilder.prepare();
		return new OrmliteListLoader<OPInfo, String>(getActivity(), getHelper().infoDao(), preparedQuery);
	}

	@Override
	public void onLoadFinished(Loader<List<OPInfo>> arg0, List<OPInfo> info) {
		mAdapter.clear();
		mAdapter.addAll(info);
	}

	@Override
	public void onLoaderReset(Loader<List<OPInfo>> arg0) {}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mAdapter.setEditMode(isChecked);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnTalk) {
			App.makeToast("敬請期待");
		} else if (v == mBtnClose) {
			mVideoPanel.setVisibility(View.GONE);
			hangUp();
			//mCloudPlay.get().finish("local");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		//hangUp();//先將目前通話掛斷
//		YesNoDialog mYesNoDialog = new YesNoDialog();
//		mYesNoDialog.show(getChildFragmentManager(), "yesNo");
		//TODO
		mViewPre.refreshDrawableState();
		mVideoView.refreshDrawableState();
		
		startOrientationSensor();
		//answer();
		//TODO
		OPInfo res = (OPInfo) arg0.getItemAtPosition(pos);
		res.setRead(true);
		getHelper().markAsRead(res);
		try {
			String _88888 = Module.getCData().lookup("url").toString().substring(4, 9);
			log.warning("cdata = " + _88888);
			res.setSenderID(_88888);
			LinphoneManager.getInstance().sendStaticImage(true);
			//AndroidVideoApi5JniWrapper.lowBandwidth = false;
			LinphoneManager.getInstance().newOutgoingPlay(res);
			mListView.smoothScrollToPositionFromTop(pos, 0);
		} catch (Exception e) {
			
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
				log.warning("停止...");
				LinphoneManager.getLc().setVideoWindow(null);
			}
		}
		if (mVideoView != null) {
			((GLSurfaceView) mVideoView).onPause();
		}
		
		//mViewPre.destroyDrawingCache();
		//mVideoView.destroyDrawingCache();
		
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
	
	boolean mVideoWidnow = false;
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
	            	
	            	final LinphoneCallParams remoteParams = mCall.getRemoteParams();
	    			if (remoteParams != null && remoteParams.getVideoEnabled() && LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests()) {
	    				mVideoDisplayWindow.requestRender();
	    			} else {
	    				mVideoDisplayWindow.requestRender();
	    			}
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
			startOrientationSensor();
			//LinphoneManager.getInstance().routeAudioToSpeaker();
			
//			new Handler(Looper.getMainLooper()).post(new Runnable() {
//			@Override
//			public void run() {
//			final LinphoneCallParams remoteParams = mCall.getRemoteParams();
//			if (remoteParams != null && remoteParams.getVideoEnabled() && LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests()) {
//				//LinphoneActivity.instance().startVideoActivity(mCall);
//				mVideoDisplayWindow.requestRender();
//			} else {
//				mVideoDisplayWindow.requestRender();
//				//LinphoneActivity.instance().startIncallActivity(mCall);
//			}
//			
//			}
//		});
//			log.warning("cid = " + call.getVideoStats());
			//LinphoneManager.getInstance().addVideo();
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
	            	mViewPre.destroyDrawingCache();
	        		mVideoView.destroyDrawingCache();
	        		//mVideoView.refreshDrawableState();
	        		
//	        		if (LinphoneManager.getLc() != null)
//	        			LinphoneManager.getLc().setVideoWindow(null);
//	        		if (mVideoDisplayWindow != null) { 
//	        			// Prevent linphone from crashing if correspondent hang up while you are rotating
//	        			mVideoDisplayWindow.release();
//	        			mVideoDisplayWindow = null;
//	        			
//	        			mVideoDisplayWindow = new AndroidVideoWindowImpl(mVideoView, mViewPre);
//	        			mVideoDisplayWindow.setListener(Home.this);
////	        			mVideoDisplayWindow.init();
//	        		}
	            }});
			mVideoWidnow = false;
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
		
		mViewPre.destroyDrawingCache();
		mVideoView.destroyDrawingCache();
		
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
	
	private void decline() {
		LinphoneManager.getLc().terminateCall(mCall);
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

	
	/* VideoWindowListener */
	@Override
	public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
		
		LinphoneManager.getLc().setVideoWindow(vw);
		mVideoView = (GLSurfaceView) surface;
	}

	@Override
	public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw) {
		if (LinphoneManager.getLc() != null) {
			LinphoneManager.getLc().setVideoWindow(null);
		}
	}
	
	
	@Override
	public void onJoin() {
		//log.warning("join add");
		final OPInfo op = OPInfo.of(UUID.randomUUID().toString(), "0000909111069", "SELF", "52館攝影機69", "video", "New Share Content");
		try {
			getHelper().markAllRead();
			getHelper().infoDao().createIfNotExists(op);
			
		} catch (Exception e) {
			
		}
		
		new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() { 
            	getLoaderManager().restartLoader(1, new Bundle(), Home.this);
            }});
		}
//	}
}
