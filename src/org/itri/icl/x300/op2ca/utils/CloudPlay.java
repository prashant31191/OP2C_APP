package org.itri.icl.x300.op2ca.utils;

import java.util.Set;

public interface CloudPlay {

	void start();
	void open();
	void ready();
	void invite();
	void answer();
	void finish();
	
	void addOpenListener(OpenListener listener);
	void addFindListener(FindListener listener);
	void addJoinListener(JoinListener listener);
	
	Set<OpenListener> getOpenListener();
	Set<FindListener> getFindListener();
	Set<JoinListener> getJoinListener();
	
	
	
	public static interface OpenListener {
		void onOpen();
	}
	
	public static interface FindListener {
		void onFind();
	}
	
	public static interface JoinListener {
		void onJoin();
	}
}
