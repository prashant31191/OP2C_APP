package org.itri.icl.x300.op2ca;

import javax.inject.Inject;
import javax.inject.Provider;

import conn.Http;

import schema.element.Account;

public class HttpProvider implements Provider<Http> {

	@Inject AccountProvider acc;
    @Override
    public Http get() {
    	Http http = new Http();
    	Account account = acc.get();
    	http.setBase("http://54.199.185.4:8080");
    	http.setAuth(account.getUsername(), account.getPassword());
    	http.log();
    	return http;
    }
}
 