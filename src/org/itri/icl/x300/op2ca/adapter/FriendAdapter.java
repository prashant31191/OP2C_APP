package org.itri.icl.x300.op2ca.adapter;

import static android.graphics.BitmapFactory.decodeResource;
import static android.graphics.BitmapFactory.decodeStream;
import static android.net.Uri.withAppendedPath;
import static android.provider.ContactsContract.Contacts.CONTENT_LOOKUP_URI;
import static android.provider.ContactsContract.Contacts.openContactPhotoInputStream;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.data.ext.ContactArg;
import org.itri.icl.x300.op2ca.db.OpDB;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import data.Contacts.Contact;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

@Log
public class FriendAdapter extends ArrayAdapter<Contact> {
	OpDB mOpDB;
	private Set<Contact> mChecked = Sets.newHashSet();
	private Set<Contact> mOrginal = Sets.newHashSet();
	public FriendAdapter(OpDB opDB, ArrayList<ContactArg> checked) {
		super(App.getCtx(), R.layout.op2c_item_contact, R.id.text1, opDB.contacts());
		Collection<Contact> transed = Collections2.transform(checked, new Function<ContactArg, Contact>() {
			@Override
			public Contact apply(ContactArg arg0) {
				return Contact.of(arg0.getUserID(), arg0.getDisplayName(), arg0.getLookuoKey());
			}
		});
		mChecked.addAll(transed);
		mOrginal.addAll(transed);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		Contact phone = getItem(position);
		TextView item = (TextView)view.findViewById(R.id.text1);
		ImageView photoView = (ImageView) view.findViewById(R.id.photo);
		TextView number = (TextView) view.findViewById(R.id.phone);
		CheckedTextView group = (CheckedTextView)view.findViewById(R.id.group);
		group.setChecked(mChecked.contains(phone) ? true : false);
		number.setText(phone.getUserID() + "@domain.org");
//		People people = phone.getPeople();
		String contactId = phone.getLookupKey();
		Uri contactUri = withAppendedPath(CONTENT_LOOKUP_URI, contactId);
		InputStream photoIs = openContactPhotoInputStream(App.getCtx().getContentResolver(), contactUri);
		photoView.setImageBitmap(photoIs != null ? decodeStream(photoIs) : decodeResource(App.getCtx().getResources(), R.drawable.webdas_person01_picture_2x));
		item.setText(phone.getDisplayName());
		item.setTypeface(null, Typeface.BOLD);
		return view;
	}
	public void write(Contact phone) {
		if (mChecked.contains(phone)) {
			log.warning("contact 清除 " + phone.getDisplayName());
			mChecked.remove(phone);
		} else {
			log.warning("contact 加入 " + phone.getDisplayName());
			mChecked.add(phone);
		}
	}
	public void clearChecked() {
		mChecked.clear();
		notifyDataSetChanged();
	}
	
	public ArrayList<ContactArg> readChecked() {
		ArrayList<ContactArg> checked = Lists.newArrayList();
		for(int i = 0; i < getCount(); i++) {
			Contact phone = getItem(i);
			if (mChecked.contains(phone)) {
				checked.add(new ContactArg(phone.getUserID(), phone.getDisplayName(), phone.getLookupKey()));
			}
		}
		return checked;
	}
}