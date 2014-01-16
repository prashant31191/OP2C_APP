package org.itri.icl.x300.op2ca;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.utils.CloudPlay.FindListener;
import org.itri.icl.x300.op2ca.utils.CloudPlay.JoinListener;
import org.itri.icl.x300.op2ca.utils.CloudPlay.OpenListener;
import org.itri.icl.x300.op2ca.webdas.sys.CloudPlayImpl;
import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCore;

import com.google.common.collect.Sets;

import android.util.Log;

import provider.Operations;
import schema.element.Account;

import conn.Http;

public class PlayProvider implements Provider<CloudPlay> {

	@Inject Provider<Account> pAcct;
	@Inject Provider<Http> pHttp;
	@Inject Operations mFact;
	static Set<OpenListener> mOpenListener = new HashSet<OpenListener>();
	static Set<FindListener> mFindListener = new HashSet<FindListener>();
	static Set<JoinListener> mJoinListener = new HashSet<JoinListener>();
	
	@Override
	public CloudPlay get() {
		//Log.v("abcde", mOpenListener.hashCode() + " " + mFindListener.hashCode() + " " + mJoinListener.hashCode());
		return new CloudPlayImpl(mOpenListener, mFindListener, mJoinListener, pHttp, pAcct, mFact);
	}
}
