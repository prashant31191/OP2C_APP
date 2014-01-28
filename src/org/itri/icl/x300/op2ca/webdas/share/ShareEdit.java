package org.itri.icl.x300.op2ca.webdas.share;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.ShareAdapter;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.dialog.YesNoDialog;
import org.itri.icl.x300.op2ca.ui.FriendListView;
import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.utils.VideoControllerListener;
import org.itri.icl.x300.op2ca.webdas.Main;
import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;

import data.Contacts.Contact;
import data.Resources.Resource;

import roboguice.inject.InjectView;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ToggleButton;

@Log
public class ShareEdit extends OrmLiteRoboFragment<OpDB> implements OnClickListener, TextWatcher, VideoControllerListener {

	@InjectView(R.id.btnEmoji) Button mNtnEmoji;
	@InjectView(R.id.btnResource) Button mBtnDevice;
	@InjectView(R.id.btnSubmit) Button mBtnSubmit;
	@InjectView(R.id.editMsg) EditText mEditMsg;
	@InjectView(R.id.lytMembers) FriendListView mLytMembers;
	
	@Inject InputMethodManager mIMM;
	boolean mStartSharing = false;
	ShareAdapter mAdapter;
	ToggleButton mBtnDelt;
	TextView mTextTitle;
	ImageButton mBtnStop, mBtnBack, mBtnSort;
	ImageButton mBtnFriend;
	TabWidget mTabWidget;
	FragmentManager mMgr;
	EmbeddedVideo mShareVideo;
	String scene = "local";
	@Inject Provider<CloudPlay> mPlay;
	@Inject SharedPreferences mPrefs;
	ArrayList<Contact> mCheckedContacts;
	ArrayList<Resource> mCheckedResources;
	public ShareEdit() {
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("people", new ArrayList<Contact>());  // people -> contact
		bundle.putParcelableArrayList("device", new ArrayList<Resource>()); // device -> resource
		setArguments(bundle);
	}
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		mMgr = getChildFragmentManager();
	    mMgr.addOnBackStackChangedListener(new OnBackStackChangedListener() {
	        @Override
	        public void onBackStackChanged() {
	        	log.warning(mMgr.getBackStackEntryCount() + " stack " + getFragmentManager().getBackStackEntryCount());
	            if(mMgr.getBackStackEntryCount() == 0) 
	            	mPlay.get().finish(scene);
	        }
	    });
		mBtnDelt = (ToggleButton)((Main)getActivity()).getButtonToggle();
		mBtnSort = ((Main)getActivity()).getButtonNext();
		mBtnStop = ((Main)getActivity()).getButtonStop();
		mBtnBack = ((Main)getActivity()).getButtonBack();
		mBtnFriend = ((Main)getActivity()).getButtonAddFriend();
		mTextTitle = ((Main)getActivity()).getTextTitle();
		mBtnDelt.setVisibility(View.GONE);
		mBtnSort.setVisibility(View.GONE);
		mBtnBack.setVisibility(View.VISIBLE);
		mBtnStop.setVisibility(View.INVISIBLE);
		mBtnFriend.setVisibility(View.VISIBLE);
		mTabWidget = ((Main)getActivity()).getTabWidget();
		mTabWidget.setVisibility(View.GONE);
		mBtnBack.setOnClickListener(this);
		mBtnFriend.setOnClickListener(this);
		mTextTitle.setText("分享對象與裝置");
		return inflater.inflate(R.layout.op2c_mgmt_edit, c, false);
	}
	
	@Override 
    public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);
		mIMM.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
		mAdapter = new ShareAdapter();
		mBtnDevice.setOnClickListener(this);
		mBtnSubmit.setOnClickListener(this);
		mCheckedContacts = getArguments().<Contact>getParcelableArrayList("people");
		mCheckedResources = getArguments().<Resource>getParcelableArrayList("device");
		mLytMembers.setContacts(mCheckedContacts);

		if (getArguments() != null && !mCheckedResources.isEmpty()) {
			log.warning("create share Video");
			
			if(mCheckedResources.get(0).getDisplayName().equals("我的手機"))
				scene = "local";
			else
				scene = "remote";
			mShareVideo = new EmbeddedVideo(mCheckedResources, mCheckedContacts, scene, false); //目前只有Video 先這樣寫
			mShareVideo.setVideoControllerListener(this);
			insert(mShareVideo);
		}
		mEditMsg.setEnabled(mCheckedResources.isEmpty() ? false : true);
		mEditMsg.setOnClickListener(this);
		mEditMsg.addTextChangedListener(this);
		mEditMsg.setText("");
	}

	@Override
	public void onPause() { super.onPause();
		mIMM.hideSoftInputFromWindow(mEditMsg.getWindowToken(), 0);
		remove(mShareVideo);
		if (!mStartSharing) //不分享才掛斷
			hangUp();
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnBack && mMgr.findFragmentByTag("player") != null) {
			new YesNoDialog().show(mMgr, "YesNo");
		} else if (v == mBtnBack && mMgr.findFragmentByTag("player") == null) {
			((Main)getActivity()).prev();
		} else if (v == mBtnFriend) {
			((Main)getActivity()).next(new ShareFriend(getArguments()));
		} else if (v == mBtnDevice) {
			((Main)getActivity()).next(new ShareResource(getArguments()));
		} else if (v == mEditMsg) {
			mIMM.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
		} else if (v == mBtnSubmit) {
			mEditMsg.setText("");
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (mShareVideo != null && mShareVideo.isAdded())
			mShareVideo.setTitle(s.toString());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	
	
	private void insert(Fragment shareContent) {
		mMgr.beginTransaction().add(R.id.share_embedded, shareContent, "player").commit();
	}
	
	private void remove(Fragment shareContent) {
		if (shareContent != null) {
			mMgr.beginTransaction().remove(shareContent).commit();
			shareContent = null;
		}
	}
	@Override
	public void onVideoPlay() {
		if (mCheckedContacts.isEmpty()) {
			App.makeToast("尚未選定聯絡人");
			return;
		} 
		mStartSharing = true;
		mPlay.get().ready(scene);
		((Main)getActivity()).replace(new ShareStream(mCheckedResources, mCheckedContacts, scene));
	}
	@Override
	public void onVideoStop() {
		mPlay.get().finish(scene);
		remove(mShareVideo);	
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

	
}
