package org.itri.icl.x300.op2ca.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class WebTaskLoader<D> extends AsyncTaskLoader<D> {

	private D data;

	public WebTaskLoader(Context context) {
		super(context);
	}

	@Override
	public void deliverResult(D data) {
		if (isReset()) {
			return;
		}

		this.data = data;

		super.deliverResult(data);
	}

	@Override
	protected void onStartLoading() {
		if (data != null) {
			deliverResult(data);
		}

		if (takeContentChanged() || data == null) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		data = null;
	}
}