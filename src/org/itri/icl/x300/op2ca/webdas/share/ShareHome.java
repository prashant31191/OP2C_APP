package org.itri.icl.x300.op2ca.webdas.share;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.SneakyThrows;

import org.itri.icl.x300.op2ca.Module;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.ShareAdapter;
import org.itri.icl.x300.op2ca.data.ResourceV1;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.utils.WebTaskLoader;
import org.itri.icl.x300.op2ca.webdas.Main;
import org.linphone.LinphoneManager;

import roboguice.inject.InjectView;
import schema.element.Account;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.common.base.Optional;
import com.j256.ormlite.android.extras.OrmliteListLoader;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import conn.Http;

import data.Contacts.Contact;
import data.OPInfos.OPInfo;
import data.Resources.Resource;

public class ShareHome extends OrmLiteRoboFragment<OpDB> implements LoaderCallbacks<List<OPInfo>>, OnItemClickListener, OnClickListener, OnCheckedChangeListener, OnItemSelectedListener {

	@InjectView(R.id.btnText) LinearLayout mBtnText;
	@InjectView(R.id.btnFile) LinearLayout mBtnFile;
	@InjectView(R.id.btnAudio) LinearLayout mBtnAudio;
	@InjectView(R.id.btnVideo) LinearLayout mBtnVideo;
	@InjectView(R.id.btnMgmt) LinearLayout mBtnMgmt;
	@InjectView(R.id.btnShare) LinearLayout mBtnShare;
	
	@InjectView(R.id.listView) ListView mListView;
	@InjectView(R.id.textEmpty) TextView mTextEmpty;
	@InjectView(R.id.progEmpty) ProgressBar mProgEmpty;
	@InjectView(R.id.listEmpty) LinearLayout mListEmpty;
	@Inject NotificationManager mNtfy;
	ShareAdapter mAdapter;
	ToggleButton mBtnDelt;
	TextView mTextTitle;
	ImageButton mBtnStop, mBtnBack, mBtnFrid, mBtnSort;
	TabWidget mTabWidget;
	@Inject Provider<Account> mAcc;
	@Inject Provider<Http> mHttpProvider;
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		mBtnDelt = (ToggleButton)((Main)getActivity()).getButtonToggle();
		mBtnSort = ((Main)getActivity()).getButtonNext();
		mBtnStop = ((Main)getActivity()).getButtonStop();
		mBtnBack = ((Main)getActivity()).getButtonBack();
		mTextTitle = ((Main)getActivity()).getTextTitle();
		mBtnFrid = ((Main)getActivity()).getButtonAddFriend();
		mBtnFrid.setVisibility(View.GONE);
		mBtnStop.setVisibility(View.GONE);
		mBtnBack.setVisibility(View.GONE);
		mBtnDelt.setVisibility(View.VISIBLE);
		mBtnSort.setVisibility(View.VISIBLE);
		mTabWidget = ((Main)getActivity()).getTabWidget();
		mTabWidget.setVisibility(View.VISIBLE);
		mBtnDelt.setChecked(false);
		mBtnDelt.setOnCheckedChangeListener(this);
		mTextTitle.setText("我的資源清單");
		return inflater.inflate(R.layout.op2c_share_home, c, false);
	}
	
	@Override @SneakyThrows
    public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);
		mListView.setEmptyView(mListEmpty);
		mAdapter = new ShareAdapter(getHelper());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mBtnText.setOnClickListener(this);
		mBtnFile.setOnClickListener(this);
		mBtnAudio.setOnClickListener(this);
		mBtnVideo.setOnClickListener(this);
		mBtnMgmt.setOnClickListener(this);
		mBtnShare.setOnClickListener(this);
		//getLoaderManager().initLoader(0, new Bundle(), this);
	}
	
	@Override @SneakyThrows
	public Loader<List<OPInfo>> onCreateLoader(int arg0, Bundle bundle) {
		
		QueryBuilder<OPInfo, String> queryBuilder = getHelper().infoDao().queryBuilder();
		queryBuilder.where().eq("senderID", mAcc.get().getUsername());
//		switch (bundle.getInt("order", 0)) {
//		case 0:queryBuilder.orderBy("createTime", false);break;
//		case 1:queryBuilder.orderBy("endDate", false);break;
//		case 2:queryBuilder.orderBy("like", false);break;
//		default:queryBuilder.orderBy("expireTime", false);break;
//		}
		PreparedQuery<OPInfo> preparedQuery = queryBuilder.prepare();
		return new OrmliteListLoader<OPInfo, String>(getActivity(), getHelper().infoDao(), preparedQuery);
		
		
//		return new WebTaskLoader<Optional<List<OPInfo>>>(getActivity()) {
//			public Optional<List<OPInfo>> loadInBackground() {
//				return mHttpProvider.get().optOPInfos(0L, 1000L);
//			}
//		};
//		QueryBuilder<OPInfo, String> queryBuilder = getHelper().infoDao().queryBuilder();
//		queryBuilder.where().eq("senderID", mAcc.get().getUsername());
//		switch (bundle.getInt("order", 0)) {
//		case 0:queryBuilder.orderBy("createTime", false);break;
//		case 1:queryBuilder.orderBy("endDate", false);break;
//		case 2:queryBuilder.orderBy("like", false);break;
//		default:queryBuilder.orderBy("expireTime", false);break;
//		}
//		PreparedQuery<OPInfo> preparedQuery = queryBuilder.prepare();
//		return new OrmliteListLoader<OPInfo, String>(getActivity(), getHelper().infoDao(), preparedQuery);
	}

	@Override
	public void onLoadFinished(Loader<List<OPInfo>> arg0, List<OPInfo> res) {
			mAdapter.clear();
			mAdapter.addAll(res);

	}

	@Override
	public void onLoaderReset(Loader<List<OPInfo>> arg0) {
		mAdapter.clear();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		OPInfo resource = mAdapter.getItem(pos);
		
		try {
			String _88888 = Module.getCData().lookup("url").toString().substring(4, 9);
			resource.setSenderID(_88888);
			LinphoneManager.getInstance().play(resource);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		((Main)getActivity()).next(new ShareStream(new ArrayList<Resource>(), new ArrayList<Contact>(), resource));
		
	}

	@Override
	public void onClick(View v) {
//		Intent intent;
		if (v == mBtnText) {
		} else if (v == mBtnFile) {
		} else if (v == mBtnAudio) {
		} else if (v == mBtnVideo) {//log.warning("換下一頁");
			((Main) getActivity()).next(new ShareEdit());
		} else if (v == mBtnMgmt) {
		} else {
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		Bundle bundle = new Bundle();
		bundle.putInt("order", pos);
		getLoaderManager().restartLoader(0, bundle, this);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mAdapter.setEditMode(isChecked);
		mAdapter.notifyDataSetChanged();
	}
}
