package org.linphone;
/*
AccountPreferencesFragment.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/


import org.itri.icl.x300.op2ca.R;
import org.linphone.ui.PreferencesListFragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;

/**
 * @author Sylvain Berfini
 */
public class AccountPreferencesFragment extends PreferencesListFragment {
	private int n;
	private LinphonePreferences mPrefs;
	
	public AccountPreferencesFragment() {
		super(R.xml.account_preferences);
		mPrefs = LinphonePreferences.instance();
	}
	
	@Override
	public void onDestroy() {
		LinphoneManager.getLc().refreshRegisters();
		super.onDestroy();
	}
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		PreferenceScreen screen = getPreferenceScreen();
		n = 0;//getArguments().getInt("Account", 0);
		manageAccountPreferencesFields(screen);
	}
	
	OnPreferenceChangeListener usernameChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mPrefs.setAccountUsername(n, newValue.toString());
			preference.setSummary(newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener useridChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mPrefs.setAccountUserId(n, newValue.toString());
			preference.setSummary(newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener passwordChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mPrefs.setAccountPassword(n, newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener domainChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mPrefs.setAccountDomain(n, newValue.toString());
			preference.setSummary(newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener proxyChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mPrefs.setAccountProxy(n, newValue.toString());
			preference.setSummary(newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener outboundProxyChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mPrefs.setAccountOutboundProxyEnabled(n, (Boolean) newValue);
			return true;
		}		
	};
	OnPreferenceChangeListener expiresChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mPrefs.setExpires(n, newValue.toString());
			preference.setSummary(newValue.toString());
			return true;
		}		
	};
	OnPreferenceChangeListener prefixChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String value = newValue.toString();
			preference.setSummary(value);
			mPrefs.setPrefix(n, value);
			return true;
		}
	};
	OnPreferenceChangeListener escapeChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			boolean value = (Boolean) newValue;
			mPrefs.setReplacePlusByZeroZero(n, value);
			return true;
		}
	};
	OnPreferenceChangeListener disableChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mPrefs.setAccountEnabled(n, (Boolean) newValue);
			return true;
		}		
	};
	OnPreferenceChangeListener deleteChangedListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			return true;
		}		
	};
	
	private void manageAccountPreferencesFields(PreferenceScreen parent) {
		boolean isDefaultAccount = mPrefs.getDefaultAccountIndex() == n;
		
    	PreferenceCategory account = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.pref_sipaccount_key));
    	EditTextPreference username = (EditTextPreference) account.getPreference(0);
    	username.setText(mPrefs.getAccountUsername(n));
    	username.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	username.setOnPreferenceChangeListener(usernameChangedListener);
    	username.setSummary(username.getText());
    	
    	EditTextPreference userid = (EditTextPreference) account.getPreference(1);
    	userid.setText(mPrefs.getAccountUserId(n));
    	userid.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	userid.setOnPreferenceChangeListener(useridChangedListener);
    	userid.setSummary(userid.getText());
    	
    	EditTextPreference password = (EditTextPreference) account.getPreference(2);
    	password.setText(mPrefs.getAccountPassword(n));
    	password.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    	password.setOnPreferenceChangeListener(passwordChangedListener);
    	
    	EditTextPreference domain = (EditTextPreference) account.getPreference(3);
    	domain.setText(mPrefs.getAccountDomain(n));
    	domain.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	domain.setOnPreferenceChangeListener(domainChangedListener);
    	domain.setSummary(domain.getText());
		
    	PreferenceCategory advanced = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.pref_advanced_key));
    	EditTextPreference proxy = (EditTextPreference) advanced.getPreference(0);
    	proxy.setText(mPrefs.getAccountProxy(n));
    	proxy.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    	proxy.setOnPreferenceChangeListener(proxyChangedListener);
    	proxy.setSummary("".equals(proxy.getText()) || (proxy.getText() == null) ? getString(R.string.pref_help_proxy) : proxy.getText());
    	
    	CheckBoxPreference outboundProxy = (CheckBoxPreference) advanced.getPreference(1);
    	outboundProxy.setChecked(mPrefs.isAccountOutboundProxySet(n));
    	outboundProxy.setOnPreferenceChangeListener(outboundProxyChangedListener);
    	
    	EditTextPreference expires = (EditTextPreference) advanced.getPreference(2);
    	expires.setText(mPrefs.getExpires(n));
    	expires.setOnPreferenceChangeListener(expiresChangedListener);
    	expires.setSummary(mPrefs.getExpires(n));

    	EditTextPreference prefix = (EditTextPreference) advanced.getPreference(3);
    	String prefixValue = mPrefs.getPrefix(n);
    	prefix.setSummary(prefixValue);
    	prefix.setText(prefixValue);
    	prefix.setOnPreferenceChangeListener(prefixChangedListener);
    	
    	CheckBoxPreference escape = (CheckBoxPreference) advanced.getPreference(4);
		escape.setChecked(mPrefs.getReplacePlusByZeroZero(n));
		escape.setOnPreferenceChangeListener(escapeChangedListener);
    	
    	PreferenceCategory manage = (PreferenceCategory) getPreferenceScreen().findPreference(getString(R.string.pref_manage_key));
    	final CheckBoxPreference disable = (CheckBoxPreference) manage.getPreference(0);
    	disable.setEnabled(true);
    	disable.setChecked(!mPrefs.isAccountEnabled(n));
    	disable.setOnPreferenceChangeListener(disableChangedListener);
    	
    	CheckBoxPreference mainAccount = (CheckBoxPreference) manage.getPreference(1);
    	mainAccount.setChecked(isDefaultAccount);
    	mainAccount.setEnabled(!mainAccount.isChecked());
    	mainAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() 
    	{
			public boolean onPreferenceClick(Preference preference) {
				mPrefs.setDefaultAccount(n);
				disable.setEnabled(false);
				disable.setChecked(false);
				preference.setEnabled(false);
				return true;
			}
		});

    	final Preference delete = manage.getPreference(2);
    	delete.setEnabled(true);
    	delete.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        public boolean onPreferenceClick(Preference preference) {
	        	mPrefs.deleteAccount(n);
	        	LinphoneActivity.instance().displaySettings();
	        	return true;
	        }
        });
	}
}
