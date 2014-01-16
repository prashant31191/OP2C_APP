package org.itri.icl.x300.op2ca;

import javax.inject.Singleton;

import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.webdas.sys.CloudPlayImpl;

import provider.Operations;
import schema.element.Account;
import schema.element.CData;
import schema.operation.ShareRtpAv;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import conn.Http;

public class Module extends AbstractModule {

	public static void setCData(CData cdata) {
		mCdata = cdata;
	}
	private static CData mCdata = new CData();
	
	public static CData getCData() {
		return mCdata;
	}
	@Override
	protected void configure() {
		bind(Account.class).toProvider(AccountProvider.class);
		bind(Http.class).toProvider(HttpProvider.class);
		bind(CloudPlay.class).toProvider(PlayProvider.class).in(Singleton.class);
		install(new FactoryModuleBuilder().implement(ShareRtpAv.class, ShareRtpAv.class).build(Operations.class));
	}
}
