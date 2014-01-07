package org.itri.icl.x300.op2ca.ui;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.data.Phone;
import org.itri.icl.x300.op2ca.db.OpDB;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@Log
public class FriendListView extends FlowLayout {
	public FriendListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	    setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

	}
	List<Phone> mChecked; 
	public void setData(List<Phone> checked) {
		mChecked = checked;
		for (Phone phone : mChecked) {
			FriendItem mFriendItem = new FriendItem(getContext(), phone);
			
			addView(mFriendItem);
		}
	}
	
	public class FriendItem extends LinearLayout implements OnClickListener {
		public FriendItem(Context context, Phone phone) {
			super(context);
			View view = View.inflate(context, R.layout.op2c_item_friend, this); //重要
			TextView textName = (TextView) view.findViewById(R.id.textName);
			String displayName = phone.getPeople().getDisplayName();
			textName.setText(displayName);
			textName.setTypeface(null, Typeface.BOLD);
			ImageView btnDelete = (ImageView) view.findViewById(R.id.btnDelete);
			btnDelete.setTag(phone);
			btnDelete.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			mChecked.remove((Phone)v.getTag());
//			mOpDB.clearChecked((Phone)v.getTag());
			FriendListView.this.removeView(this);
		}
	}
}
