package com.j256.ormlite.android.extras;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class OrmliteRawListLoader<T, ID, S> extends AsyncTaskLoader<List<S>> {
	private RuntimeExceptionDao<T, ID> mDao = null;
//	private PreparedQuery<T> mQuery = null;
	private List<S> mData = null;
	private RawRowMapper<S> mMapper;
	private String mRawQuery;

	public OrmliteRawListLoader(Context context, RuntimeExceptionDao<T, ID> dao,
				String rawQuery, 
				RawRowMapper<S> mapper) {
		super(context);
		mDao = dao;
//		mQuery = query;
		mMapper = mapper;
		mRawQuery = rawQuery;
	}

	@Override
	public List<S> loadInBackground() {
		List<S> result = null;

		try {
			result = mDao.queryRaw(mRawQuery, mMapper).getResults();
//			if (mQuery != null) {
//				result = mDao.query(mQuery);
//			} else {
//				result = mDao.queryForAll();
//			}

		} catch (SQLException e) {
			result = Collections.emptyList();
		}

		return result;
	}

	@Override
	public void deliverResult(List<S> datas) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
			// don't need the result.
			if (datas != null) {
				onReleaseResources(datas);
			}
		}

		List<S> oldDatas = mData;
		mData = datas;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(datas);
		}

		if (oldDatas != null && !oldDatas.isEmpty()) {
			onReleaseResources(oldDatas);
		}
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		if (mData != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mData);
		} else {
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(List<S> datas) {
		super.onCanceled(datas);

		// At this point we can release the resources associated with 'apps'
		// if needed.
		onReleaseResources(datas);
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with 'apps'
		// if needed.
		if (mData != null) {
			onReleaseResources(mData);
			mData = null;
		}
	}

	/**
	 * Helper function to take care of releasing resources associated with an
	 * actively loaded data set.
	 */
	protected void onReleaseResources(List<S> datas) {
		// For a simple List<> there is nothing to do. For something
		// like a Cursor, we would close it here.
	}
}
