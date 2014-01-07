package org.itri.icl.x300.op2ca.utils;

import org.itri.icl.x300.op2ca.db.OpDB;

import roboguice.service.RoboIntentService;
import roboguice.service.RoboService;
import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

public abstract class OrmLiteRoboIntentService<H extends OrmLiteSqliteOpenHelper>
		extends RoboIntentService {

	public OrmLiteRoboIntentService(String name) {
		super(name);
	}

	private volatile H helper;
	private volatile boolean created = false;
	private volatile boolean destroyed = false;

	/**
	 * Get a helper for this action.
	 */
	public H getHelper() {
		if (helper == null) {
			if (!created) {
				throw new IllegalStateException(
						"A call has not been made to onCreate() yet so the helper is null");
			} else if (destroyed) {
				throw new IllegalStateException(
						"A call to onDestroy has already been made and the helper cannot be used after that point");
			} else {
				throw new IllegalStateException(
						"Helper is null for some unknown reason");
			}
		} else {
			return helper;
		}
	}

	/**
	 * Get a connection source for this action.
	 */
	public ConnectionSource getConnectionSource() {
		return getHelper().getConnectionSource();
	}

	@Override
	public void onCreate() {
		if (helper == null) {
			helper = getHelperInternal(this);
			created = true;
		}
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseHelper(helper);
		destroyed = true;
	}

	protected H getHelperInternal(Context context) {
		@SuppressWarnings({ "unchecked", "deprecation" })
		H newHelper = (H) OpenHelperManager.getHelper(context, OpDB.class);
		return newHelper;
	}

	protected void releaseHelper(H helper) {
		OpenHelperManager.releaseHelper();
		this.helper = null;
	}

}
