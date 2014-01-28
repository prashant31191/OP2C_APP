package org.itri.icl.x300.op2ca.webdas.share;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.Module;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.MessageAdapter;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.utils.VideoControllerListener;
import org.itri.icl.x300.op2ca.webdas.Main;
import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.j256.ormlite.android.extras.OrmliteListLoader;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import data.Comments.Comment;
import data.Contacts.Contact;
import data.OPInfos.OPInfo;
import data.Resources.Resource;

@Log
public class ShareStream extends OrmLiteRoboFragment<OpDB> implements LoaderCallbacks<List<Comment>>, OnClickListener, OnItemClickListener, VideoControllerListener {
	@InjectView(R.id.btnEmoji) Button mNtnEmoji;
	@InjectView(R.id.btnLikes) Button mBtnLikes;
	@InjectView(R.id.btnSubmit) Button mBtnSubmit;
	@InjectView(R.id.btnOldMsg) Button mBtnOldMsg;
	@InjectView(R.id.listMesg) ListView mListView;
	@InjectView(R.id.editMsg) EditText mEditMsg;
	
	@InjectView(R.id.listEmpty) LinearLayout mListEmpty;
	
	@Inject InputMethodManager mIMM;
	MessageAdapter mAdapter;
	ToggleButton mBtnDelt;
	TextView mTextTitle;
	ImageButton mBtnStop, mBtnBack, mBtnSort;
	ImageButton mBtnFriend;
	TabWidget mTabWidget;
	FragmentManager mMgr;
	EmbeddedVideo mShareVideo;
	OPInfo mResource;
	List<Resource> mDevice;
	List<Contact> mPeople;
	@Inject Provider<CloudPlay> mPlay;
	String mScene = "local";
	public ShareStream() {
		log.warning("為什麼會呼叫呢？");
		
	}
	public ShareStream(List<Resource> device, List<Contact> people, String scene) {
		mScene = scene;
		mDevice = device;
		mPeople = people;
	}
	
	public ShareStream(List<Resource> device, List<Contact> people, OPInfo info) {
		mResource = info;
		mDevice = device;
		mPeople = people;
		
		
//		Bundle bundle = new Bundle();
//		bundle.putParcelableArrayList("people", new ArrayList<Phone>());
//		bundle.putParcelableArrayList("device", new ArrayList<Function>());
//		setArguments(bundle);
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
		mBtnStop.setVisibility(View.GONE);
		mBtnFriend.setVisibility(View.INVISIBLE);
		mTabWidget = ((Main)getActivity()).getTabWidget();
		mTabWidget.setVisibility(View.GONE);
		mBtnBack.setOnClickListener(this);
		mBtnFriend.setOnClickListener(this);
//		String title = getArguments().<Function>getParcelableArrayList("function").get(0).getText();
		mTextTitle.setText("標題");
		return inflater.inflate(R.layout.op2c_share_stream, c, false);
	}
	
	@Override 
    public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);
		mIMM.hideSoftInputFromWindow(mEditMsg.getWindowToken(), 0);
		mAdapter = new MessageAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setEmptyView(mListEmpty);
		mListView.setOnItemClickListener(this);
		mBtnSubmit.setOnClickListener(this);
		
		
		
		//		mLytMembers.setData(getArguments().<Phone>getParcelableArrayList("people"));
////		Function fun = getHelper().getFunction();
//		if (getArguments() != null && getArguments().containsKey("people")) {
//			
//		}
//		if (getArguments() != null && !getArguments().getParcelableArrayList("device").isEmpty()) {
//			log.warning("create share Video");
		mShareVideo = new EmbeddedVideo(mDevice, mPeople, mScene, true); //目前只有Video 先這樣寫
		mShareVideo.setVideoControllerListener(this);
		mMgr.beginTransaction().add(R.id.share_embedded, mShareVideo, "share").commit();
			
//		}
		
//		mEditMsg.setEnabled(getArguments().getParcelableArrayList("device").isEmpty() ? false : true);
		
		mEditMsg.setOnClickListener(this);
//		mEditMsg.addTextChangedListener(this);
		mEditMsg.setText("");
		if (mResource != null)
		getLoaderManager().initLoader(0, null, this);
	}
	
	
	@Override
	public void onPause() { super.onPause();
		if (mScene.equals("remote")) 
			hangUp();
	}
	
	@Override
	public void onClick(View v) {
		if(v == mBtnBack) {
			((Main)getActivity()).prev();
		}
		
	}
	
	@Override @SneakyThrows
	public Loader<List<Comment>> onCreateLoader(int arg0, Bundle arg1) {
		QueryBuilder<Comment, Long> qb = getHelper().cmntDao().queryBuilder();
		qb.where().eq("opInfo_id", mResource.getOpID());
		qb.orderBy("time", false);
		PreparedQuery<Comment> preparedQuery = qb.prepare();
		return new OrmliteListLoader<Comment, Long>(getActivity(), getHelper().cmntDao(), preparedQuery);
	}
	@Override
	public void onLoadFinished(Loader<List<Comment>> arg0, List<Comment> res) {
		mAdapter.clear();
		mAdapter.addAll(res);
	}
	@Override
	public void onLoaderReset(Loader<List<Comment>> arg0) {
		mAdapter.clear();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}
	@Override
	public void onVideoPlay() {
		try {
			String _88888 = Module.getCData().lookup("url").toString().substring(4, 9);
			mTextTitle.setText("標題" + _88888);
			mResource.setSenderID(_88888);
			LinphoneManager.getInstance().play(mResource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onVideoStop() {
		log.warning("scene = " + mScene);
		mPlay.get().finish(mScene);
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
