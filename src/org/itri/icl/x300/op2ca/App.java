package org.itri.icl.x300.op2ca;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import roboguice.RoboGuice;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.Toast;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

import data.Contacts.Contact;

public class App extends Application {
	
	
	
	public static final int NO_REGISTER = 100;
	
	public static final int LOAD_DB = 0;
	public static final int LOAD_WEB = 1;
	private static Context context;
	Set<Contact> mCacheFriends = Sets.newHashSet();
	private static Cache<String, String> mNameCache = CacheBuilder.newBuilder()
			.expireAfterWrite(60, TimeUnit.MINUTES)
			.build();
	public void onCreate() {
		super.onCreate();
		RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
				RoboGuice.newDefaultRoboModule(this), new Module());
		context = getApplicationContext();
	}

	public static Context getCtx() {
		return context;
	}
	
	public static void makeToast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	@SneakyThrows
	public static String getNameCache(final String number) {
		return mNameCache.get(number, new Callable<String>() {
			public String call() throws Exception {
				//log.warning("lookupDisplayName()");
				return lookupDisplayName(number);  
			}
		});
	}
	
	/**
	 * 查詢顯示名稱
	 * @param phoneNumber
	 * @return displayName
	 */
	private static String lookupDisplayName(String phoneNumber) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor c = context.getContentResolver().query(uri, null, null, null, null);
		String displayName;
		try {
			if (c.getCount() == 0 || c == null)
				return phoneNumber;
			else 
				c.moveToNext();
			displayName = c.getString(c.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		} finally {
			c.close();
		}
		return displayName;
	}
}
