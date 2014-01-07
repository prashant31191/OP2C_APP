package org.itri.icl.x300.op2ca.webdas.share;

import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;

import lombok.SneakyThrows;

import org.itri.icl.x300.op2ca.Module;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.DeviceAdapter;
import org.itri.icl.x300.op2ca.adapter.FunctionAdapter;
import org.itri.icl.x300.op2ca.data.Device;
import org.itri.icl.x300.op2ca.data.Function;
import org.itri.icl.x300.op2ca.data.TYPE.FUNC_TYPE;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.webdas.Main;
import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneChatRoom;

import provider.ObjectProvider;
import provider.Operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

import roboguice.inject.InjectView;
import schema.element.CData;
import schema.element.ENUM.STEP.SHARE_RTPAV;
import schema.operation.ShareRtpAv;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class ShareDevice extends OrmLiteRoboFragment<OpDB> implements TextWatcher, OnClickListener, OnItemClickListener {

	@InjectView(R.id.editText) EditText mEditText;
	@InjectView(R.id.treeView) ListView mListView;
	@InjectView(R.id.textEmpty) TextView mTextEmpty;
	@InjectView(R.id.progEmpty) ProgressBar mProgEmpty;
	@InjectView(R.id.listEmpty) LinearLayout mListEmpty;
	@InjectView(R.id.btnConfirm) Button mBtnConfirm;
	@InjectView(R.id.btnReset) Button mBtnReset;
	DeviceAdapter mAdapter;
	int previousGroup = -1;
	ToggleButton mBtnDelt;
	TextView mTextTitle;
	ImageButton mBtnStop, mBtnBack, mBtnSort, mBtnFrnd;
	
	
	public ShareDevice(Bundle... bundle) {
		if (bundle != null && !bundle[0].containsKey("device")) {
			bundle[0].putParcelableArrayList("device", new ArrayList<Function>());
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

		mAdapter = new DeviceAdapter(getHelper(), FUNC_TYPE.VIDEO, getArguments().<Device>getParcelableArrayList("device"));
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mEditText.addTextChangedListener(this);
		mBtnReset.setOnClickListener(this);
		mBtnConfirm.setOnClickListener(this);
		
		mListView.setTextFilterEnabled(true);
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
	public void afterTextChanged(Editable s) {
		mAdapter.getFilter().filter(s.toString());
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override
	public void onClick(View v) {
		if (v == mBtnStop) {
			((Main)getActivity()).prev();
		} else if (v == mBtnReset) {
			mAdapter.clearChecked();
		} else if (v == mBtnConfirm) {
			ArrayList<Device> checked = mAdapter.readChecked();
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("device", checked);
			
			//FIXME
			LinphoneChatRoom room = LinphoneManager.getLc().getOrCreateChatRoom("sip:cloudplay@140.96.116.226");
			Injector injector = Guice.createInjector(new Module());
	        Operations factory = injector.getInstance(Operations.class);
	        ShareRtpAv share_av = factory.rtpAv(SHARE_RTPAV.start, new CData());
	        share_av.receivers("10e");
	        ObjectMapper om = new ObjectProvider().getContext(this.getClass());
	        try {
	        	String msg = om.writeValueAsString(share_av);
	        	room.sendMessage(msg);
	        } catch (Exception e) {
	        	
	        }
			((Main)getActivity()).prev(bundle);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		Device device = mAdapter.getItem(pos);
		mAdapter.checkItem(device);
		mAdapter.notifyDataSetChanged();
	}
}
