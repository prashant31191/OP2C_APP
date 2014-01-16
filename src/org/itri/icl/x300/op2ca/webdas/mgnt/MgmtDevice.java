package org.itri.icl.x300.op2ca.webdas.mgnt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.SneakyThrows;

import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.FunctionAdapter;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.utils.WebTaskLoader;
import org.itri.icl.x300.op2ca.webdas.Main;

import com.google.common.base.Optional;

import data.Resources.Resource;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class MgmtDevice extends OrmLiteRoboFragment<OpDB> implements OnGroupExpandListener, OnChildClickListener, OnClickListener, TextWatcher, LoaderCallbacks<List<Resource>> {

	@InjectView(R.id.editText) EditText mEditText;
	@InjectView(R.id.treeView) ExpandableListView mTreeView;
	@InjectView(R.id.textEmpty) TextView mTextEmpty;
	@InjectView(R.id.progEmpty) ProgressBar mProgEmpty;
	@InjectView(R.id.listEmpty) LinearLayout mListEmpty;
	FunctionAdapter mAdapter;
	int previousGroup = -1;
	ToggleButton mBtnDelt;
	TextView mTextTitle;
	TabWidget mTabWidget;
	ImageButton mBtnStop, mBtnBack, mBtnSort;
	@Inject Provider<CloudPlay> mCloudPlay;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		mBtnDelt = (ToggleButton)((Main)getActivity()).getButtonToggle();
		mBtnSort = ((Main)getActivity()).getButtonNext();
		mBtnStop = ((Main)getActivity()).getButtonStop();
		mBtnBack = ((Main)getActivity()).getButtonBack();
		mTextTitle = ((Main)getActivity()).getTextTitle();
		mTabWidget = ((Main)getActivity()).getTabWidget();
		mBtnDelt.setVisibility(View.GONE);
		mBtnSort.setVisibility(View.VISIBLE);
		mBtnBack.setVisibility(View.VISIBLE);
		mBtnStop.setVisibility(View.GONE);
		mTabWidget.setVisibility(View.GONE);
		mBtnBack.setOnClickListener(this);
		mTextTitle.setText("數位資源管理");
		return inflater.inflate(R.layout.op2c_mgmt_device, c, false);
	}

	@Override
	@SneakyThrows
	public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);	        
		mTreeView.setEmptyView(mListEmpty);
		mTreeView.setOnChildClickListener(this);
//		mAdapter = new FunctionAdapter(getHelper().getFunctionDao());
//		mTreeView.setAdapter(mAdapter);
		mTreeView.setOnGroupExpandListener(this);
		mEditText.addTextChangedListener(this);
		mTreeView.setTextFilterEnabled(true);
//		mBtnReset.setOnClickListener(this);
//		mBtnConfirm.setOnClickListener(this);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		return false;
	}

	@Override
	public void onGroupExpand(int groupPosition) {
		if(groupPosition != previousGroup)
			mTreeView.collapseGroup(previousGroup);
        previousGroup = groupPosition;		
	}

	@Override
	public void onClick(View v) {
		((Main)getActivity()).prev();
	}

	@Override
	public void afterTextChanged(Editable s) {}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	@Override
	public Loader<List<Resource>> onCreateLoader(int arg0, Bundle arg1) {
//		if (arg0 == 0) {
			return new WebTaskLoader<List<Resource>>(getActivity()) {
				public List<Resource> loadInBackground() {
					Optional<List<Resource>> opt = mCloudPlay.get().listResources();
					return opt.isPresent() ? opt.get() : new ArrayList<Resource>();
				}
			};
//		} else {
//			
//		}

	}

	@Override
	public void onLoadFinished(Loader<List<Resource>> loader, List<Resource> result) {
		//getHelper().syncResource(result);
	}

	@Override
	public void onLoaderReset(Loader<List<Resource>> loader) {
		
	}
	
//	@Subscribe
//	public void onMainEvent() {
//		
//	}
}