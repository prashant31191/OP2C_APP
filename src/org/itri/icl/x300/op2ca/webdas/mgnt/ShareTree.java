package org.itri.icl.x300.op2ca.webdas.mgnt;

import java.util.ArrayList;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.FunctionAdapter;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.webdas.Main;

import data.Resources.Resource;

import roboguice.inject.InjectView;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

@Log
public class ShareTree extends OrmLiteRoboFragment<OpDB> implements OnGroupExpandListener, TextWatcher, OnClickListener, OnChildClickListener {

	@InjectView(R.id.editText) EditText mEditText;
	@InjectView(R.id.treeView) ExpandableListView mTreeView;
	@InjectView(R.id.textEmpty) TextView mTextEmpty;
	@InjectView(R.id.progEmpty) ProgressBar mProgEmpty;
	@InjectView(R.id.listEmpty) LinearLayout mListEmpty;
	@InjectView(R.id.btnConfirm) Button mBtnConfirm;
	@InjectView(R.id.btnReset) Button mBtnReset;
	FunctionAdapter mAdapter;
	int previousGroup = -1;
	ToggleButton mBtnDelt;
	TextView mTextTitle;
	ImageButton mBtnStop, mBtnBack, mBtnSort;
	
	
	public ShareTree(Bundle... bundle) {
		if (bundle != null && !bundle[0].containsKey("device")) {
			bundle[0].putParcelableArrayList("device", new ArrayList<Resource>());
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
		mBtnDelt.setVisibility(View.GONE);
		mBtnSort.setVisibility(View.GONE);
		mBtnBack.setVisibility(View.INVISIBLE);
		mBtnStop.setVisibility(View.VISIBLE);
		mBtnStop.setOnClickListener(this);
		mTextTitle.setText("數位資源");
		return inflater.inflate(R.layout.op2c_mgmt_tree, c, false);
	}

	@Override
	@SneakyThrows
	public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);	        
		mTreeView.setEmptyView(mListEmpty);
		mTreeView.setOnChildClickListener(this);
		
//		mAdapter = new FunctionAdapter(getHelper(), getArguments().<ResourceArg>getParcelableArrayList("device"));
//		mTreeView.setAdapter(mAdapter);
		mTreeView.setOnGroupExpandListener(this);
		mEditText.addTextChangedListener(this);
		mTreeView.setTextFilterEnabled(true);
		mBtnReset.setOnClickListener(this);
		mBtnConfirm.setOnClickListener(this);
		
		mTreeView.setTextFilterEnabled(true);
	}
	
	// @Override @SneakyThrows
	// public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
	// AndroidBaseDaoImpl<Device, Long> devDao = getHelper().getDeviceDao();
	// QueryBuilder<Device, Long> qb = devDao.queryBuilder().groupBy("text");
	// return new OrmliteCursorLoader<Device>(getActivity(), devDao,
	// qb.prepare());
	// }
	//
	// @Override
	// public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
	// log.warning("cursor size = " + cursor.getCount());
	// mAdapter.changeCursor(cursor);
	// }
	//
	// @Override
	// public void onLoaderReset(Loader<Cursor> arg0) {
	// // TODO Auto-generated method stub
	// }

	@Override
	public void onGroupExpand(int groupPosition) {
		if(groupPosition != previousGroup)
			mTreeView.collapseGroup(previousGroup);
        previousGroup = groupPosition;
	}

	@Override
	public void afterTextChanged(Editable s) {
		mAdapter.getFilter().filter(s.toString());
	}
	
	@Override public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
	@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override
	public void onClick(View v) {
		if (v == mBtnStop) {
			((Main)getActivity()).prev();
		} else if (v == mBtnReset) {
//			mAdapter.clearChecked();
		} else if (v == mBtnConfirm) {
			//TODO
			//List<FunctionArg> checked = mAdapter.readChecked();
//			Bundle bundle = new Bundle();
//			bundle.putParcelableArrayList("device", checked);
//			((Main)getActivity()).prev(bundle);
		}
	}

//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
//		log.warning(arg1.getClass().getCanonicalName());
////		Phone phone = mAdapter.getItem(pos);
////        if (phone.isChecked()) {
////        	phone.setChecked(false);
////        } else if (!phone.isChecked()) {
////        	phone.setChecked(true);
////        }
//	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		Cursor cursor = mAdapter.getChild(groupPosition, childPosition);
//		mAdapter.checkItem(cursor.getLong(cursor.getColumnIndex("_id")));
		mAdapter.notifyDataSetChanged();
		return false;
	}
}
