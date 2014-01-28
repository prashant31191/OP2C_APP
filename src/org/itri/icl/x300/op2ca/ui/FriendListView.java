package org.itri.icl.x300.op2ca.ui;

import java.util.List;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.R;

import data.Contacts.Contact;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@Log
public class FriendListView extends FlowLayout {
	public FriendListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	    setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

	}
	List<Contact> mChecked; 
	public void setContacts(List<Contact> checked) {
		mChecked = checked;
		for (Contact phone : mChecked) {
			FriendItem mFriendItem = new FriendItem(getContext(), phone);
			
			addView(mFriendItem);
		}
	}
	
	public class FriendItem extends LinearLayout implements OnClickListener {
		public FriendItem(Context context, Contact phone) {
			super(context);
			View view = View.inflate(context, R.layout.op2c_item_friend, this); //重要
			TextView textName = (TextView) view.findViewById(R.id.textName);
			String displayName = phone.getDisplayName();
			textName.setText(displayName);
			textName.setTypeface(null, Typeface.BOLD);
			ImageView btnDelete = (ImageView) view.findViewById(R.id.btnDelete);
			btnDelete.setTag(phone);
			btnDelete.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			mChecked.remove((Contact)v.getTag());
//			mOpDB.clearChecked((Phone)v.getTag());
			FriendListView.this.removeView(this);
		}
	}
}
