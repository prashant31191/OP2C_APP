package org.itri.icl.x300.op2ca.utils;

import org.itri.icl.x300.op2ca.db.OpDB;

import roboguice.activity.RoboFragmentActivity;
import android.content.Context;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

public class OrmLiteRoboFragmentActivity<H extends OrmLiteSqliteOpenHelper>
		extends RoboFragmentActivity {

	private volatile H helper;
	private volatile boolean created = false;
	private volatile boolean destroyed = false;

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
	public void onCreate(Bundle savedInstanceState) {
		if (helper == null) {
			helper = getHelperInternal(this);
			created = true;
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseHelper(helper);
		destroyed = true;
	}

	protected H getHelperInternal(Context context) {
		@SuppressWarnings("unchecked")
		H newHelper = (H) OpenHelperManager.getHelper(context, OpDB.class);
		return newHelper;
	}

	protected void releaseHelper(H helper) {
		OpenHelperManager.releaseHelper();
		helper = null;
	}

}
