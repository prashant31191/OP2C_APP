package org.itri.icl.x300.op2ca.adapter;

import static android.graphics.BitmapFactory.decodeResource;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lombok.SneakyThrows;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.data.Resource;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneSimpleListener.OnResourceStateChangedListener;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareAdapter extends ArrayAdapter<Resource> implements OnClickListener, OnResourceStateChangedListener {
	Calendar mCale = Calendar.getInstance();
	boolean editable = false;
	Resource mResource;
//	Dao<Resource, Long> mDao;
	OpDB mOpDB;
	SimpleDateFormat mFmt = new SimpleDateFormat("MM/dd, hh:mm");
	public ShareAdapter() {
		super(App.getCtx(), R.layout.op2c_item_share, R.id.text_title);
	}
	public ShareAdapter(OpDB opDB) {
		super(App.getCtx(), R.layout.op2c_item_share, R.id.text_title);
//		this.mDao = dao;
		mOpDB = opDB;
		LinphoneManager.addListener(this);
	}
	
	public void setEditMode(boolean editable) {
		this.editable = editable;
	}
	
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		Resource item = getItem(position);		
//		if (item.active()) {
//			mResource = item;
//		}
		ImageView imgView = (ImageView) view.findViewById(R.id.pict_media);
		int imgID;
		if ("video".equalsIgnoreCase(item.getShareType())) {
			imgID = R.drawable.webdas_item_videoctx;
		} else if ("doc".equalsIgnoreCase(item.getShareType())) {
			imgID = R.drawable.webdas_item_document;
		} else if ("audio".equalsIgnoreCase(item.getShareType())) {
			imgID = R.drawable.webdas_item_audioctx;
		} else if ("comm".equalsIgnoreCase(item.getShareType())) {
			imgID = R.drawable.webdas_item_intercom;;
		} else if ("chat".equalsIgnoreCase(item.getShareType())) {
			imgID = R.drawable.webdas_item_chatroom;
		} else if ("res".equalsIgnoreCase(item.getShareType())) {
			imgID = R.drawable.webdas_item_resource;
		} else {
			imgID = R.drawable.webdas_item_resource;
		}
		imgView.setImageBitmap(decodeResource(App.getCtx().getResources(), imgID)); 
		
		TextView txtTitle = (TextView) view.findViewById(R.id.text_title);
		txtTitle.setText(item.getDescription());
		
		TextView txtStartDate = (TextView) view.findViewById(R.id.text_clock);
		mCale.setTimeInMillis(item.getCreateTime());
		txtStartDate.setText(mFmt.format(mCale.getTimeInMillis()));
		
		TextView txtEndDate = (TextView) view.findViewById(R.id.text_countdown);
		mCale.setTimeInMillis(item.getExpireTime());
		txtEndDate.setText(mFmt.format(mCale.getTimeInMillis()));
		
		TextView txtLikeCount = (TextView) view.findViewById(R.id.text_like_count);
		txtLikeCount.setText(item.getNum_likes() + "");
		
		TextView txtRead = (TextView) view.findViewById(R.id.txtRead);
		txtRead.setText(item.isRead() ? "已讀" : "未讀");
		txtRead.setTextColor(item.isRead() ? Color.rgb('c', 'c', 'c') : Color.BLUE);
		
		ImageView imgLive = (ImageView) view.findViewById(R.id.pict_live);
		imgLive.setImageResource(item.isRead()? R.drawable.webdas_live_org : R.drawable.webdas_live_red);
		
		TextView txtOwner = (TextView) view.findViewById(R.id.text_owner);
		if (item.getState() == State.OutgoingProgress) {
			txtOwner.setText("連線中");
		} else if (item.getState() == State.StreamsRunning) {
			txtOwner.setText("播放中");
		} else {
			txtOwner.setText("");
		}
		
		Button btnRemove = (Button) view.findViewById(R.id.bton_remove);
		btnRemove.setVisibility(editable ? View.VISIBLE : View.GONE);
		btnRemove.setOnClickListener(this);
		btnRemove.setTag(item);
		return view;
	}

	@Override @SneakyThrows
	public void onClick(View v) {
		Resource resource = (Resource)v.getTag();
		if (mOpDB.resDao().delete(resource) > 0) 
			remove(resource);
	}

	@Override
	public void onResourceStateChanged(LinphoneCall call, final State state, final Resource resoure, String message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (state == State.StreamsRunning) {
					//TODO mOpDB.markAsRead(resoure);
					notifyDataSetChanged();
				} else if (state == State.OutgoingProgress || 
						   state == State.CallEnd || state == State.Error || state == State.CallReleased) {
					notifyDataSetChanged();
				}
			}
		});
		resoure.setState(state);
	}
}
