package org.itri.icl.x300.op2ca.webdas.mon;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboIntentService;

import schema.element.Account;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import conn.Http;

import data.Capability;
import data.Contacts.Contact;
import data.Resources.Resource;


import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.*;

@Log
public class SyncPeople extends OrmLiteRoboIntentService<OpDB> {

	@Inject Provider<Http> mHttpProvider;
	@Inject Provider<Account> mAcctProvider;
	public SyncPeople() {
		super("sync_people");
	}
	//取得Android DB的聯絡人，同步到WebDAS App DB.
	@Override
	protected void onHandleIntent(Intent intent) {
		log.warning("同步聯絡人...");
		Cursor cursor = getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI, null, "", null, "");
		int displayNameIndex = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
		int lookupIndex = cursor.getColumnIndex(CommonDataKinds.Phone.LOOKUP_KEY);
		int contactIndex = cursor.getColumnIndex(CommonDataKinds.Phone.CONTACT_ID);
		int numberIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER);
		Set<Contact> ctcts = Sets.newHashSet();
		Long time = System.currentTimeMillis();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			
			String lookupKey = cursor.getString(lookupIndex);
			log.warning("lookup key = " + lookupKey);
			String displayName = cursor.getString(displayNameIndex);
			String number = cursor.getString(numberIndex);
			
			ctcts.add(Contact.of(number, displayName, lookupKey, time));
		}
		getHelper().syncContacts(ctcts);
		//Http http = mHttpProvider.get();
		//Account acct = mAcctProvider.get();
		//http.save(Lists.newArrayList(ctcts));
		//http.save(Resource.of(acct.getUsername()+"@device.com", "我的手機", "online", Sets.newHashSet(Capability.of("audio", "聲音裝置"), Capability.of("video", "影像裝置"))));
		//http.save(Resource.of("10b@device.com", "遠端攝影機", "online", Sets.newHashSet(Capability.of("video", "影像裝置"))));
		
	}

}
