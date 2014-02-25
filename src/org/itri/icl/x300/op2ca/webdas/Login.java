package org.itri.icl.x300.op2ca.webdas;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.AccountProvider;
import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.Module;
import org.itri.icl.x300.op2ca.R;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
//import org.linphone.mediastream.Log;

import com.google.inject.Guice;
import com.google.inject.Injector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import schema.element.Account;

@Log
@ContentView(R.layout.op2c_login)
public class Login extends RoboFragmentActivity implements OnClickListener, TextWatcher, OnItemSelectedListener {
	@InjectView(R.id.btnLogin) Button mBtnLogin;
	@InjectView(R.id.edtUsername) EditText mEdtUsername;
	@InjectView(R.id.edtPassword) EditText mEdtPassword;
	@InjectView(R.id.server) Spinner mmBtnServ;
	ArrayAdapter<String> mAdapter;
	@Inject SharedPreferences mPrefs;
	String[] proxy = {"61.221.50.9:5168", "54.199.199.47:5168"};
	public void onCreate(Bundle state) { 
		super.onCreate(state);
		mEdtUsername.addTextChangedListener(this);
		mEdtPassword.setEnabled(false);
		mBtnLogin.setOnClickListener(this);
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, proxy);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mmBtnServ.setAdapter(mAdapter);
		mmBtnServ.setOnItemSelectedListener(this);
	}
	
	@Override
	public void onClick(View v) {
		
		LinphoneManager.getLc().setDeviceRotation(270); //TODO
		LinphonePreferences mNewPrefs = LinphonePreferences.instance();
		LinphoneCore lc = LinphoneManager.getLc();
		lc.clearProxyConfigs();
		lc.clearAuthInfos();
		mNewPrefs.removePreviousVersionAuthInfoRemoval();
		mNewPrefs.setNewAccountUsername(mEdtUsername.getText().toString());
		mNewPrefs.setNewAccountPassword(mEdtPassword.getText().toString());
		mNewPrefs.setNewAccountUserId(mEdtUsername.getText().toString());
		mNewPrefs.setNewAccountDomain(mPrefs.getString("op2c_proxy", "61.221.50.9:5168"));
		try {
			mNewPrefs.saveNewAccount();
			//TODO
			//mNewPrefs.setAccountProxy(0, "isip.dlinkddns.com:9090");
//			mNewPrefs.setAccountProxy(0, acc.getDomain());
			//mNewPrefs.setAccountOutboundProxyEnabled(0, true);
			mNewPrefs.setDebugEnabled(true);
			mNewPrefs.setEchoCancellation(false);
			mNewPrefs.setFrontCamAsDefault(false);
			//Log.e("firewall = " + mNewPrefs.getAccountCount() + " " + lc.getProxyConfigList().length + " " + mNewPrefs.getAccountProxy(0) + " " + mNewPrefs.isVideoEnabled() + " " + mNewPrefs.getTransport());
		} catch (LinphoneCoreException e) {
			e.printStackTrace();
		}
		
		lc.setUploadBandwidth(0);
		lc.setDownloadBandwidth(0);
		if (mNewPrefs.getAccountCount() == 1) {
			log.warning("設定正確");
			setResult(Activity.RESULT_OK);
			finish();
		} else {
			App.makeToast("設定有誤");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	log.warning("按下 back");
	    	setResult(App.NO_REGISTER);
	    	finish();
	    	//super.onBackPressed();
	    	return true;
	    } else {
	    	return super.onKeyDown(keyCode, event);
	    }
	}
	
//	@Override  
//    public void onBackPressed() {  
//		setResult(App.NO_REGISTER);
//    	finish();
//    	super.onBackPressed();
//    }
	
	
	@Override
	public void afterTextChanged(Editable s) {
		String username = s.toString().trim();
		if (username.length() == 0) {
			mEdtPassword.setText("");
			mBtnLogin.setEnabled(false);
		} else if (username.length() == 10) {
			mEdtPassword.setText("12345678");
			mBtnLogin.setEnabled(true);
		} else {
			mEdtPassword.setText(username + "abc");
			mBtnLogin.setEnabled(true);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		log.warning("proxy = " + mAdapter.getItem(pos));
		
		mPrefs.edit().putString("op2c_proxy", mAdapter.getItem(pos)).commit();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

}
