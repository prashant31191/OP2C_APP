package org.itri.icl.x300.op2ca.webdas.mgnt;

import java.util.List;

import lombok.SneakyThrows;

import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.ShareAdapter;
import org.itri.icl.x300.op2ca.data.Message;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.webdas.Main;
import org.itri.icl.x300.op2ca.webdas.share.ShareEdit;

import data.Resources.Resource;

import roboguice.inject.InjectView;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MgmtHome extends OrmLiteRoboFragment<OpDB> implements OnClickListener {
	@InjectView(R.id.btn_mgmt_people) Button mBtnPeople;
	@InjectView(R.id.btn_mgmt_state) Button mBtnState;
	@InjectView(R.id.btn_mgmt_device) Button mBtnDevice;
	@InjectView(R.id.btn_mgmt_prefs) Button mBtnPrefs;
	
	ShareAdapter mAdapter;
	ToggleButton mBtnDelt;
	TextView mTextTitle;
	ImageButton mBtnStop, mBtnBack, mBtnFrid, mBtnSort;
	TabWidget mTabWidget;
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
		mBtnDelt.setVisibility(View.GONE);
		mBtnSort.setVisibility(View.GONE);
		mTabWidget = ((Main)getActivity()).getTabWidget();
		mTabWidget.setVisibility(View.VISIBLE);
		mTextTitle.setText("設定");
		return inflater.inflate(R.layout.op2c_mgmt_main0, c, false);
	}
	
	@Override @SneakyThrows
    public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);
		mBtnPeople.setOnClickListener(this);
		mBtnDevice.setOnClickListener(this);
		mBtnState.setOnClickListener(this);
		mBtnPrefs.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnPeople) {
			
		} else if (v == mBtnDevice) {
			((Main)getActivity()).next(new MgmtDevice());
		} else if (v == mBtnState) {
			
		} else if (v == mBtnPrefs) {
			
		}
		
	}

}
