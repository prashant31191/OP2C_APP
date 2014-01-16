package org.itri.icl.x300.op2ca;

import javax.inject.Provider;

import schema.element.Account;

public class AccountProvider implements Provider<Account> {

    @Override
    public Account get() {
        return new Account("10d", "10dabc", "61.221.50.9:5168");
//        return new Account("10d", "10dabc", "140.96.116.226");
//    	return new Account("0909111024", "12345678", "juiker.datamitetek.com");
    	
    }
}
 