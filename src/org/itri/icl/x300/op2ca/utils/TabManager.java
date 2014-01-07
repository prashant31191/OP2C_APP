package org.itri.icl.x300.op2ca.utils;

import java.util.HashMap;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.R;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@Log
public class TabManager implements TabHost.OnTabChangeListener {
	private final FragmentActivity mActivity;
	private final TabHost mTabHost;
	private final int mContainerId;
	private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
	TabInfo mLastTab;
	Fragment mCurFrg;
	int tabCount = 0;

	public void add(String text, int imgId, Class<?> clazz) {
		TabSpec spec1 = mTabHost.newTabSpec("tab" + (tabCount++)).setIndicator(
				newIndicator(text, imgId));
		addTab(spec1, clazz, null);
	}

	public void add(String text, int imgId, Class<?> clazz, Bundle bundle) {
		TabSpec spec1 = mTabHost.newTabSpec("tab" + (tabCount++)).setIndicator(
				newIndicator(text, imgId));
		addTab(spec1, clazz, bundle);
	}

	public static void expandTouchArea(final View bigView,
			final View smallView, final int extraPadding) {
		bigView.post(new Runnable() {
			@Override
			public void run() {
				Rect rect = new Rect();
				smallView.getHitRect(rect);
				rect.top -= extraPadding;
				rect.left -= extraPadding;
				rect.right += extraPadding;
				rect.bottom += extraPadding;
				bigView.setTouchDelegate(new TouchDelegate(rect, smallView));
			}
		});
	}

	private View newIndicator(CharSequence tabTitle, int imgId) {
		final View tabIndicator;
		if (R.drawable.tab_03 == imgId) {
			tabIndicator = LayoutInflater.from(mTabHost.getContext()).inflate(
					R.layout.op2c_tab_indicator2, mTabHost.getTabWidget(),
					false);
		} else {
			tabIndicator = LayoutInflater.from(mTabHost.getContext()).inflate(
					R.layout.tab_indicator, mTabHost.getTabWidget(), false);
		}
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		title.setText(tabTitle);
		final ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(imgId);
		// View tabIndicator =
		// LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.tab_indicator,
		// mTabHost.getTabWidget(), false);
		// TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		// title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		// title.setText(tabTitle);
		// ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);

		// final View parent = mTabHost.getTabWidget(); //delegate是想擴大觸控範圍的元件
		// parent.post( new Runnable() {
		// public void run() {
		// final Rect r = new Rect();
		// tabIndicator.getHitRect(r);
		//
		// final Rect r2 = new Rect();
		// parent.getHitRect(r2);
		// log.warning("top=" + r2.top + ", bottom=" + r2.bottom + ", left=" +
		// r2.left + ", right=" + r2.right);
		//
		//
		// log.warning("top=" + r.top + ", bottom=" + r.bottom + ", left=" +
		// r.left + ", right=" + r.right);
		// r.top = r2.top + 80; //觸控範圍往上增加
		//
		// // r.bottom -= 30;
		// // r.left += 30;
		// // r.right -= 30;
		// // r.bottom += 10; //觸控範圍往下增加
		// // r.left -= 10; //觸控範圍往左增加
		// // r.right += 10; //觸控範圍往右增加
		// tabIndicator.setTouchDelegate(new TouchDelegate(r , parent));
		// }
		// });

		return tabIndicator;
	}

	static final class TabInfo {
		private final String tag;
		private final Class<?> clss;
		private final Bundle args;
		private Fragment fragment;

		TabInfo(String _tag, Class<?> _class, Bundle _args) {
			tag = _tag;
			clss = _class;
			args = _args;
		}
	}

	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			// v.setBackgroundResource(R.drawable.bg);
			v.setMinimumWidth(100);
			v.setMinimumHeight(100);
			return v;
		}
	}

	public TabManager(FragmentActivity activity, TabHost tabHost,
			int containerId) {
		mActivity = activity;
		mTabHost = tabHost;
		mContainerId = containerId;
		mTabHost.setOnTabChangedListener(this);
	}

	public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
		tabSpec.setContent(new DummyTabFactory(mActivity));
		String tag = tabSpec.getTag();

		TabInfo info = new TabInfo(tag, clss, args);

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state. If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		info.fragment = mActivity.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (info.fragment != null && !info.fragment.isDetached()) {
			FragmentTransaction ft = mActivity.getSupportFragmentManager()
					.beginTransaction();
			ft.detach(info.fragment);
			ft.commit();
		}

		mTabs.put(tag, info);
		mTabHost.addTab(tabSpec);
	}

	public Fragment getCurrentFragment() {
		return mCurFrg;
	}

	// @Override
	public void onTabChanged(String tabId) {
		log.warning(tabId + " tab ID");
		TabInfo newTab = mTabs.get(tabId);
		if (mLastTab != newTab) {
			FragmentTransaction ft = mActivity.getSupportFragmentManager()
					.beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(mActivity,
							newTab.clss.getName(), newTab.args);
					ft.add(mContainerId, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}
			mLastTab = newTab;
			mCurFrg = mLastTab.fragment;
			ft.commit();
			mActivity.getSupportFragmentManager().executePendingTransactions();
		}
	}

}