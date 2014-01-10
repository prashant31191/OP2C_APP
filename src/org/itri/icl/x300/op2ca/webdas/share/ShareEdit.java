package org.itri.icl.x300.op2ca.webdas.share;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.ShareAdapter;
import org.itri.icl.x300.op2ca.data.Device;
import org.itri.icl.x300.op2ca.data.Function;
import org.itri.icl.x300.op2ca.data.ext.ContactArg;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.ui.FriendListView;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.webdas.Main;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
public class ShareEdit extends OrmLiteRoboFragment<OpDB> implements LoaderCallbacks<List<Device>>, OnClickListener, TextWatcher {

	@InjectView(R.id.btnEmoji) Button mNtnEmoji;
	@InjectView(R.id.btnResource) Button mBtnDevice;
	@InjectView(R.id.btnSubmit) Button mBtnSubmit;
	@InjectView(R.id.editMsg) EditText mEditMsg;
	@InjectView(R.id.lytMembers) FriendListView mLytMembers;
	
	@Inject InputMethodManager mIMM;
	
	ShareAdapter mAdapter;
	ToggleButton mBtnDelt;
	TextView mTextTitle;
	ImageButton mBtnStop, mBtnBack, mBtnSort;
	ImageButton mBtnFriend;
	TabWidget mTabWidget;
	FragmentManager mMgr;
	EmbeddedVideo mShareVideo;
	
	
	public ShareEdit() {
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("people", new ArrayList<ContactArg>());
		bundle.putParcelableArrayList("device", new ArrayList<Function>());
		setArguments(bundle);
		
	}
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		log.warning("onCreateView = " + state);
		mMgr = getChildFragmentManager();
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
		mLytMembers.setData(getArguments().<ContactArg>getParcelableArrayList("people"));
////		Function fun = getHelper().getFunction();
//		if (getArguments() != null && getArguments().containsKey("people")) {
//			
//		}
		if (getArguments() != null && !getArguments().getParcelableArrayList("device").isEmpty()) {
			log.warning("create share Video");
			mShareVideo =  new EmbeddedVideo(getArguments().<Device>getParcelableArrayList("device"),
											 getArguments().<ContactArg>getParcelableArrayList("people")); //目前只有Video 先這樣寫
			mMgr.beginTransaction().add(R.id.share_embedded, mShareVideo, "share").commit();
		}
		
		mEditMsg.setEnabled(getArguments().getParcelableArrayList("device").isEmpty() ? false : true);
		
		mEditMsg.setOnClickListener(this);
		mEditMsg.addTextChangedListener(this);
		mEditMsg.setText("");
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onPause() { super.onPause();
		mIMM.hideSoftInputFromWindow(mEditMsg.getWindowToken(), 0);
		if (mShareVideo != null) {
			log.warning("刪除");
			mMgr.beginTransaction().remove(mShareVideo).commit();
		}
	}
	@Override
	public Loader<List<Device>> onCreateLoader(int arg0, Bundle arg1) {
//		mTextEmpty.setText(getString(R.string.title_mgmt_no_resource, "檔案"));
		return null;
	}

	@Override
	public void onLoadFinished(Loader<List<Device>> arg0, List<Device> res) {
		if (res.isEmpty()) {
//			mTextEmpty.setText(getString(R.string.title_mgmt_no_resource, "檔案"));
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Device>> arg0) {
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnBack) {
			((Main)getActivity()).prev();
//			getHelper().clearChecked();
		} else if (v == mBtnFriend) {
			((Main)getActivity()).next(new ShareFriend(getArguments()));
		} else if (v == mBtnDevice) {
			((Main)getActivity()).next(new ShareDevice(getArguments()));
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
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
}
