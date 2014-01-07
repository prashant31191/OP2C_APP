package org.itri.icl.x300.op2ca.webdas;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;

import java.util.ArrayList;
import java.util.UUID;

import javax.inject.Inject;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.data.Phone;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.dialog.CriteriaDialog;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboFragmentActivity;
import org.itri.icl.x300.op2ca.utils.TabManager;
import org.itri.icl.x300.op2ca.webdas.mgnt.MgmtHome;
import org.itri.icl.x300.op2ca.webdas.mon.SyncPeople;
import org.itri.icl.x300.op2ca.webdas.share.ShareEdit;
import org.itri.icl.x300.op2ca.webdas.share.ShareFriend;
import org.itri.icl.x300.op2ca.webdas.share.ShareHome;
import org.linphone.AccountPreferencesFragment;
import org.linphone.LinphoneLauncherActivity;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneService;
import org.linphone.SettingsFragment;

import com.google.common.collect.Lists;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ToggleButton;

@Log
@ContentView(R.layout.op2c_main)
public class Main extends OrmLiteRoboFragmentActivity<OpDB> implements OnClickListener {

	@InjectView(android.R.id.tabs) TabWidget mTabWidget;
	@InjectView(android.R.id.tabhost) TabHost mTabHost;
	@InjectView(R.id.btnToggle) ToggleButton mBtnToggle;
	@InjectView(R.id.btnBack) ImageButton mBtnBack;
	@InjectView(R.id.btnStop) ImageButton mBtnStop;
	@InjectView(R.id.btnSort) ImageButton mBtnSort;
	@InjectView(R.id.btnFriend) ImageButton mBtnFriend;
	@InjectView(R.id.txtTitle) TextView mTxtTitle;
	FragmentManager mgr;
	TabManager mTabManager;
	
	@Override @SneakyThrows
	public void onCreate(Bundle state) {
		super.onCreate(state);
		if (!LinphoneManager.isInstanciated()) { //已經建立帳號
			log.warning("No service running: avoid crash by starting the launcher" + getClass().getName());
			finish();
			startActivity(getIntent().setClass(this, Splash.class));
			return;
		}
		LinphonePreferences mNewPrefs = LinphonePreferences.instance();
		if (mNewPrefs.getAccountCount() == 0) { // 尚未建立過帳號
			startActivity(new Intent(this, Login.class));
		}
		
		
		mTabHost.setup();
		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
		Bundle bundle1 = new Bundle();
		bundle1.putBoolean("inbox", true);
		mTabManager.add("首頁", R.drawable.tab_01, Home.class, bundle1);
		Bundle bundle2 = new Bundle();
		bundle2.putBoolean("outbox", true);
		mTabManager.add("分享", R.drawable.tab_02, Dispatch.class, bundle2);
		
		mTabManager.add("訊息", R.drawable.tab_03, Setup.class, bundle2);
		Bundle bundle3 = new Bundle();
		bundle3.putBoolean("mgmt", true);
		mTabManager.add("設定", R.drawable.tab_04, SettingsFragment.class, bundle3); //Dispatch
		Bundle bundle4 = new Bundle();
		bundle4.putBoolean("other", true);
		mTabManager.add("其他", R.drawable.tab_05, AccountPreferencesFragment.class, bundle4);
//			int numberOfTabs = mTabHost.getTabWidget().getChildCount();
//		    for(int t=0; t<numberOfTabs; t++){
//		    	mTabHost.getTabWidget().getChildAt(t).setOnTouchListener(new View.OnTouchListener() {
//
//					@Override
//					public boolean onTouch(View v, MotionEvent event) {
//						if (event.getY() < 45) {
//							return true;
//						} else {
//							return false;
//						}}
//		        });
//		    }       
		if (state != null) {
			mTabHost.setCurrentTabByTag(state.getString("tab"));
		}
		
		mBtnSort.setOnClickListener(this);
//		mBtnSort.setAdapter(new ArrayAdapter<String>(this, R.layout.op2c_spinner_item, new String[] {"發佈日期", "有效日期", "點擊率"}) {{
//	    	setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    }});
	}
	
//	public void addNewTab(Context context, Class<?> cls, String tabName){
//		Intent intent = new Intent(this, Home.class);
//        TabSpec spec = mTabHost.newTabSpec(tabName)
//                .setIndicator(tabName, getResources().getDrawable(R.drawable.tab_01))
//                .setContent(intent);
//        mTabHost.addTab(spec);
//    }
	@Override
	public void onSaveInstanceState(Bundle outState) { super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	//先跳內部Frg在跳外部Act
	// TODO 
	@Override
	public void onBackPressed() {
		log.warning("返回");
		Fragment curFrg = mTabManager.getCurrentFragment();
//		curFrg.getChildFragmentManager().dump(arg0, arg1, arg2, arg3)
	    if (curFrg != null && curFrg.getChildFragmentManager().getBackStackEntryCount() > 0) {
	    	log.warning("返回1");
	    	curFrg.getChildFragmentManager().popBackStack();
	    } else {
	    	log.warning("返回0");
	    	super.onBackPressed();
	    }
	}
	
	public void next(Fragment frag) {
		log.warning("trans next = " + frag.getClass().getSimpleName());
		mgr = getSupportFragmentManager();
		FragmentTransaction trans = mgr.beginTransaction();
		if (frag instanceof ShareEdit) {
			trans.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		} else {
			trans.setCustomAnimations(R.anim.rl1, R.anim.rl2, R.anim.lr1, R.anim.lr2);
		}
		String tag = frag.getClass().getSimpleName();
	    trans.replace(R.id.share_embedded, frag, tag);
	    trans.setTransition(TRANSIT_FRAGMENT_OPEN);
	    trans.addToBackStack(tag);
	    trans.commitAllowingStateLoss();
	}
	
	
	public void replace(Fragment frag) {
		log.warning("trans replace = " + frag.getClass().getSimpleName());
		mgr = getSupportFragmentManager();
		mgr.popBackStack();
		FragmentTransaction trans = mgr.beginTransaction();
		trans.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		String tag = frag.getClass().getSimpleName();
	    trans.replace(R.id.share_embedded, frag, tag);
	    trans.setTransition(TRANSIT_FRAGMENT_OPEN);
	    trans.addToBackStack(tag);
	    trans.commitAllowingStateLoss();
	}
	
	public void prev(Bundle... bundle) {
		
		mgr = getSupportFragmentManager();
		if (mgr.getBackStackEntryCount() > 0) {
			BackStackEntry e = mgr.getBackStackEntryAt(mgr.getBackStackEntryCount()-1);
			Fragment frag = mgr.findFragmentByTag(e.getName());
			log.warning("trans prev = " + frag.getClass().getSimpleName());
		    Fragment poped = mgr.findFragmentByTag(ShareEdit.class.getSimpleName());
		    if (poped != null && bundle.length > 0) {
		    	poped.getArguments().putAll(bundle[0]);
		    }
			mgr.popBackStack();
        }
	}
	public Button getButtonToggle() {
		return mBtnToggle;
	}
	public ImageButton getButtonBack() {
		return mBtnBack;
	}
	public ImageButton getButtonStop() {
		return mBtnStop;
	}
	public ImageButton getButtonNext() {
		return mBtnSort;
	}
	
	public TextView getTextTitle() {
		return mTxtTitle;
	}
	
	public ImageButton getButtonAddFriend() {
		return mBtnFriend;
	}
	
	public TabWidget getTabWidget() {
		return mTabWidget;
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnSort) {
			CriteriaDialog mOrderDialog = new CriteriaDialog();
			mOrderDialog.show(getSupportFragmentManager(), "criteria");
		}
	}
	
}
