package org.itri.icl.x300.op2ca.utils;

public interface WebDASProcessListener {
	
	void onStart();
	void onOpen();
	void onReady();
	void onInquire();
	void onAnswer();
	void onJoin();
	void onFinish();
}
