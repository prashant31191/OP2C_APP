package org.itri.icl.x300.op2ca.utils;

import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;

import data.Comments;
import data.Contacts;
import data.Loves;
import data.OPInfos;
import data.Resources;

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
	
	Optional<List<Comments.Comment>> listComments(String opID, Long startTime, Long afterN);
	Optional<List<OPInfos.OPInfo>> listOPInfos(Long startTime, Long afterN);
	Optional<List<Contacts.Contact>> listContacts();
	Optional<List<Loves.Love>> listLoves(String opID);
	Optional<List<Resources.Resource>> listResources();
	Optional<List<Resources.Resource>> listResources(String type);
	
	void save();
	
	
	
	
}
