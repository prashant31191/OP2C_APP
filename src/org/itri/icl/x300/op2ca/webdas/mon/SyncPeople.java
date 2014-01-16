package org.itri.icl.x300.op2ca.webdas.mon;

import java.util.Map;
import java.util.Set;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboIntentService;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import data.Contacts.Contact;


import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.*;
import android.provider.ContactsContract.Contacts;

@Log
public class SyncPeople extends OrmLiteRoboIntentService<OpDB> {

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
//		Map<String, People> hm = Maps.newHashMap();
		Set<Contact> ctcts = Sets.newHashSet();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String lookupKey = cursor.getString(lookupIndex);
			String displayName = cursor.getString(displayNameIndex);
			String number = cursor.getString(numberIndex);
			ctcts.add(Contact.of(number, displayName, lookupKey));
//			if(hm.containsKey(cursor.getString(lookupIndex))) {
//				People people = hm.get(lookupKey);
//				people.add(Phone.of(cursor.getString(numberIndex)));
//			} else {
//				People people = People.of(lookupKey, cursor.getLong(contactIndex), 
//													 cursor.getString(displayNameIndex));
//				people.add(Phone.of(cursor.getString(numberIndex)));
//				hm.put(lookupKey, people);
//			}
			//log.warning(cursor.getString(lookupIndex) + " " + cursor.getLong(idIndex) + " " + cursor.getString(numberIndex) + " - " + cursor.getString(displayNameIndex));
		}
//		getHelper().sync(hm.values());
		getHelper().syncContacts(ctcts);
	}

}
