package org.itri.icl.x300.op2ca;

import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.webdas.sys.CloudPlayImpl;

import provider.Operations;
import schema.element.Account;
import schema.operation.ShareRtpAv;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import conn.Http;

public class Module extends AbstractModule {

	@Override
	protected void configure() {

		bind(Account.class).toProvider(AccountProvider.class);
		
		bind(Http.class).toProvider(HttpProvider.class);
		
		bind(CloudPlay.class).to(CloudPlayImpl.class);
		
		install(new FactoryModuleBuilder().implement(ShareRtpAv.class,
				ShareRtpAv.class).build(Operations.class));

	}

}
