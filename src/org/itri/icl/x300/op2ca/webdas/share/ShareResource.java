package org.itri.icl.x300.op2ca.webdas.share;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.SneakyThrows;

import org.itri.icl.x300.op2ca.Module;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.DeviceAdapter;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.utils.WebTaskLoader;
import org.itri.icl.x300.op2ca.webdas.Main;
import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneChatRoom;

import provider.ObjectProvider;
import provider.Operations;
import roboguice.inject.InjectView;
import schema.element.Account;
import schema.element.CData;
import schema.element.ENUM.STEP.SHARE_RTPAV;
import schema.operation.ShareRtpAv;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.base.Optional;
import com.j256.ormlite.android.extras.OrmliteListLoader;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import conn.Http;

import data.Capability;
import data.Contacts.Contact;
import data.Resources;
import data.OPInfos.OPInfo;
import data.Resources.Resource;

public class ShareResource extends OrmLiteRoboFragment<OpDB> implements TextWatcher, OnClickListener, OnItemClickListener, LoaderCallbacks<List<Resource>> {

	@InjectView(R.id.editText) EditText mEditText;
	@InjectView(R.id.treeView) ListView mListView;
	@InjectView(R.id.textEmpty) TextView mTextEmpty;
	@InjectView(R.id.progEmpty) ProgressBar mProgEmpty;
	@InjectView(R.id.listEmpty) LinearLayout mListEmpty;
	@InjectView(R.id.btnConfirm) Button mBtnConfirm;
	@InjectView(R.id.btnReset) Button mBtnReset;
	@Inject Provider<Http> mHttpProvider;
	DeviceAdapter mAdapter;
	int previousGroup = -1;
	ToggleButton mBtnDelt;
	TextView mTextTitle;
	@Inject Provider<Account> mAcctProvider;
	ImageButton mBtnStop, mBtnBack, mBtnSort, mBtnFrnd;
	
	@Inject Provider<CloudPlay> mPlay;
	
	public ShareResource(Bundle... bundle) {
		if (bundle != null && !bundle[0].containsKey("device")) {
			bundle[0].putParcelableArrayList("device", new ArrayList<Capability>());
		}
		setArguments(bundle[0]);
	}
	
//	public ShareDevice() {
//		Bundle bundle = new Bundle();
//		bundle.putParcelableArrayList("device", new ArrayList<Function>());
//		setArguments(bundle);
//	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		mBtnDelt = (ToggleButton)((Main)getActivity()).getButtonToggle();
		mBtnSort = ((Main)getActivity()).getButtonNext();
		mBtnStop = ((Main)getActivity()).getButtonStop();
		mBtnBack = ((Main)getActivity()).getButtonBack();
		mTextTitle = ((Main)getActivity()).getTextTitle();
		mBtnFrnd = ((Main)getActivity()).getButtonAddFriend();
		mBtnDelt.setVisibility(View.GONE);
		mBtnSort.setVisibility(View.GONE);
		mBtnBack.setVisibility(View.GONE);
		mBtnStop.setVisibility(View.VISIBLE);
		mBtnFrnd.setVisibility(View.INVISIBLE);
		mBtnStop.setOnClickListener(this);
		mTextTitle.setText("數位資源");
		return inflater.inflate(R.layout.op2c_share_device, c, false);
	}

	@Override
	@SneakyThrows
	public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);	        
		mListView.setEmptyView(mListEmpty);
		mAdapter = new DeviceAdapter(getHelper(), "video", getArguments().<Resource>getParcelableArrayList("device"));
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mEditText.addTextChangedListener(this);
		mBtnReset.setOnClickListener(this);
		mBtnConfirm.setOnClickListener(this);
		mListView.setTextFilterEnabled(true);
		getLoaderManager().initLoader(0, null, this);
	}
	
	
	//讀取硬碟列表 與 過濾
	@Override @SneakyThrows
	public Loader<List<Resource>> onCreateLoader(int arg0, Bundle bundle) {
		QueryBuilder<Resource, String> queryBuilder = getHelper().rescDao().queryBuilder();
		//queryBuilder.where().eq("senderID", mAcctProvider.get().getUsername());
		PreparedQuery<Resource> preparedQuery = queryBuilder.prepare();
		return new OrmliteListLoader<Resource, String>(getActivity(), getHelper().rescDao(), preparedQuery);
	}

	@Override
	public void onLoadFinished(Loader<List<Resource>> arg0, List<Resource> info) {
		mAdapter.clear();
		mAdapter.addAll(info);
	}

	@Override
	public void onLoaderReset(Loader<List<Resource>> arg0) {}
//	 @Override @SneakyThrows
//	 public Loader<Optional<List<Resource>>> onCreateLoader(int arg0, Bundle arg1) {
//		 Log.w("video2", "fetch video");
//		 return new WebTaskLoader<Optional<List<Resource>>>(getActivity()) {
//			public Optional<List<Resource>> loadInBackground() {
//				return mHttpProvider.get().optResources("video");
//			}
//		 };
//	 }
//	
//	 @Override
//	 public void onLoadFinished(Loader<Optional<List<Resource>>> arg0, Optional<List<Resource>> result) {
//		 Log.w("video2", "fetch " + result.isPresent());
//		 if (result.isPresent()) {
//			 List<Resource> resource = result.get();
//			 mAdapter.addAll(resource);
//			 getHelper().syncResource(resource);
//		 } 
//	 }
//	
//	 @Override
//	 public void onLoaderReset(Loader<Optional<List<Resource>>> arg0) {
//		 mAdapter.clear();
//	 }

	@Override
	public void afterTextChanged(Editable s) {
		mAdapter.getFilter().filter(s.toString());
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override @SneakyThrows
	public void onClick(View v) {
		if (v == mBtnStop) {
			((Main)getActivity()).prev();
		} else if (v == mBtnReset) {
			mAdapter.clearChecked();
		} else if (v == mBtnConfirm) {
			ArrayList<Resource> checked = mAdapter.readChecked();
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("device", checked);
			
			String username = mAcctProvider.get().getUsername();
			if (!checked.isEmpty() && checked.get(0).getUri().contains(username)) {
				mPlay.get().start("local");
			} else {
				mPlay.get().start("remote");
			}
			
			//LinphoneChatRoom room = LinphoneManager.getLc().getOrCreateChatRoom("sip:cloudplay@61.221.50.9:5168");//140.96.116.226
			//Injector injector = Guice.createInjector(new Module());
	        //Operations factory = injector.getInstance(Operations.class);
//	        ShareRtpAv share_av;
//			if (!checked.isEmpty() && checked.get(0).getDisplayName().equals("我的手機")) {
//				share_av = factory.rtpAv(SHARE_RTPAV.start, new CData(), "local");
//			} else {
//				share_av = factory.rtpAv(SHARE_RTPAV.start, new CData(), "remote");
//			}
//			//FIXME
//			share_av.receivers("10e");
//	        ObjectMapper om = new ObjectProvider().getContext(this.getClass());
//	        String msg = om.writeValueAsString(share_av);
//	        room.sendMessage(msg);
			((Main)getActivity()).prev(bundle);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		Resource device = mAdapter.getItem(pos);
		mAdapter.checkItem(device);
		mAdapter.notifyDataSetChanged();
	}
}
