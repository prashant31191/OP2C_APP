package org.itri.icl.x300.op2ca.webdas.share;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.adapter.FriendAdapter;
import org.itri.icl.x300.op2ca.adapter.ShareAdapter;
import org.itri.icl.x300.op2ca.data.ext.ContactArg;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragment;
import org.itri.icl.x300.op2ca.webdas.Main;

import data.Contacts.Contact;

import roboguice.inject.InjectView;
import android.app.Service;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ToggleButton;

@Log
public class ShareFriend extends OrmLiteRoboFragment<OpDB> implements LoaderCallbacks<List<Contact>>, OnClickListener, OnItemClickListener, TextWatcher {

	@InjectView(R.id.editText) EditText mEditText;
	@InjectView(R.id.btnReset) Button mBtnReset;
	@InjectView(R.id.btnConfirm) Button mBtnConfirm;
	@InjectView(R.id.btnGroup) Button mBtnGroup;
	@InjectView(R.id.listView) ListView mListView;
	@InjectView(R.id.textEmpty) TextView mTextEmpty;
	@InjectView(R.id.progEmpty) ProgressBar mProgEmpty;
	@InjectView(R.id.listEmpty) LinearLayout mListEmpty;
	@Inject InputMethodManager mIMMgr;
	FriendAdapter mAdapter;
	ToggleButton mBtnDelt;
	TextView mTextTitle;
	ImageButton mBtnStop, mBtnBack, mBtnSort;
	ImageButton mBtnFriend;
	TabWidget mTabWidget;
	
	
	public ShareFriend(Bundle... bundle) {
		if (bundle != null && !bundle[0].containsKey("people")) {
			bundle[0].putParcelableArrayList("people", new ArrayList<ContactArg>());
		}
		setArguments(bundle[0]);
	}
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		mBtnDelt = (ToggleButton)((Main)getActivity()).getButtonToggle();
		mBtnSort = ((Main)getActivity()).getButtonNext();
		mBtnStop = ((Main)getActivity()).getButtonStop();
		mBtnBack = ((Main)getActivity()).getButtonBack();
		mTextTitle = ((Main)getActivity()).getTextTitle();
		mTabWidget = ((Main)getActivity()).getTabWidget();
		mTabWidget.setVisibility(View.GONE);
		mBtnDelt.setVisibility(View.GONE);
		mBtnSort.setVisibility(View.GONE);
		mBtnBack.setVisibility(View.INVISIBLE);
		mBtnStop.setVisibility(View.VISIBLE);
		mBtnFriend = ((Main)getActivity()).getButtonAddFriend();
		mBtnFriend.setVisibility(View.INVISIBLE);
		mBtnStop.setOnClickListener(this);
		mTextTitle.setText("好友名單");
		return inflater.inflate(R.layout.op2c_share_friend, c, false);
	}
	
	@Override @SneakyThrows
    public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);
		mListView.setEmptyView(mListEmpty);
		mListView.setItemsCanFocus(false);
		mListView.setOnItemClickListener(this);
		mAdapter = new FriendAdapter(getHelper(), getArguments().<ContactArg>getParcelableArrayList("people"));
		mListView.setAdapter(mAdapter);
		mBtnReset.setOnClickListener(this);
		mBtnConfirm.setOnClickListener(this);
		mBtnGroup.setOnClickListener(this);
		mEditText.addTextChangedListener(this);
		mEditText.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            mIMMgr.showSoftInput(mEditText, 0);
	        }
	    });
		mListView.setTextFilterEnabled(true);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnStop) {
			((Main)getActivity()).prev(getArguments());
			mAdapter.clearChecked();
		} else if (v == mBtnGroup) {
//			mAdapter.writeChecked();
//			mAdapter.clearChecked();
		} else if (v == mBtnConfirm) {
			ArrayList<ContactArg> checked = mAdapter.readChecked();
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("people", checked);
			((Main)getActivity()).prev(bundle);
		} else if (v == mBtnReset) {
			mAdapter.clearChecked();
			
		}
	}

	@Override
	public Loader<List<Contact>> onCreateLoader(int arg0, Bundle arg1) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<List<Contact>> arg0, List<Contact> arg1) {
		
	}

	@Override
	public void onLoaderReset(Loader<List<Contact>> arg0) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> view, View arg1, int pos, long arg3) {
		Contact phone = mAdapter.getItem(pos);
        mAdapter.write(phone);
        mAdapter.notifyDataSetChanged();
	}
	@Override
	public void afterTextChanged(Editable s) {
		mAdapter.getFilter().filter(s.toString());
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
