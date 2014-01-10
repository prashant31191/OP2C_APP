package org.itri.icl.x300.op2ca.webdas.share;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.MessageAdapter;
import org.itri.icl.x300.op2ca.adapter.ShareAdapter;
import org.itri.icl.x300.op2ca.data.Device;
import org.itri.icl.x300.op2ca.data.Function;
import org.itri.icl.x300.op2ca.data.Message;
import org.itri.icl.x300.op2ca.data.Phone;
import org.itri.icl.x300.op2ca.data.Resource;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.ui.FriendListView;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.webdas.Main;

import com.j256.ormlite.android.extras.OrmliteListLoader;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import roboguice.inject.InjectView;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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

@Log
public class ShareStream extends OrmLiteRoboFragment<OpDB> implements LoaderCallbacks<List<Message>>, OnClickListener, OnItemClickListener {
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
	Resource mResource;
	
	public ShareStream() {
	}
	
	public ShareStream(Resource resource) {
		mResource = resource;
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
			mShareVideo =  new EmbeddedVideo(new ArrayList<Device>(), new ArrayList<Phone>()); //目前只有Video 先這樣寫
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
	public void onClick(View v) {
		if(v == mBtnBack) {
			((Main)getActivity()).prev();
		}
		
	}
	@Override @SneakyThrows
	public Loader<List<Message>> onCreateLoader(int arg0, Bundle arg1) {
		QueryBuilder<Message, Long> qb = getHelper().msgDao().queryBuilder();
		qb.where().eq("resource_id", mResource.get_id());
		qb.orderBy("time", false);
		PreparedQuery<Message> preparedQuery = qb.prepare();
		return new OrmliteListLoader<Message, Long>(getActivity(), getHelper().msgDao(), preparedQuery);
	}
	@Override
	public void onLoadFinished(Loader<List<Message>> arg0, List<Message> res) {
		mAdapter.clear();
		mAdapter.addAll(res);
	}
	@Override
	public void onLoaderReset(Loader<List<Message>> arg0) {
		mAdapter.clear();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}
}
