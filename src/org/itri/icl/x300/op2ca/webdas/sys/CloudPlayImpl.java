package org.itri.icl.x300.op2ca.webdas.sys;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.SneakyThrows;

import org.itri.icl.x300.op2ca.Module;
import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.itri.icl.x300.op2ca.utils.CloudPlay.FindListener;
import org.itri.icl.x300.op2ca.utils.CloudPlay.JoinListener;
import org.itri.icl.x300.op2ca.utils.CloudPlay.OpenListener;
import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;

import provider.ObjectProvider;
import provider.Operations;
import schema.element.Account;
import schema.element.CData;
import schema.element.ENUM.STEP.SHARE_RTPAV;
import schema.operation.ShareRtpAv;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.async.TypeListener;

import conn.Http;
import data.Comments;
import data.Comments.Comment;
import data.Contacts;
import data.Contacts.Contact;
import data.Loves;
import data.Loves.Love;
import data.OPInfos;
import data.OPInfos.OPInfo;
import data.Resources;
import data.Resources.Resource;

public class CloudPlayImpl implements CloudPlay {
	LinphoneChatRoom mChatRoom;
	ObjectMapper mWriter;
	Provider<Http> mHttp;
	Provider<Account> mAcct;
	Set<OPInfo> mOPInfo = Sets.newHashSet();
	Operations mFactory;
	static Set<OpenListener> mOpenListener;
	static Set<FindListener> mFindListener;
	static Set<JoinListener> mJoinListener;
	public CloudPlayImpl(Set<OpenListener> openListener, Set<FindListener> findListener, Set<JoinListener> joinListener, Provider<Http> http, Provider<Account> accu, Operations factory) {
		Log.wtf("chatroom", "a new play.....");
		try {
			LinphoneCore lc = LinphoneManager.getLc();
			if (lc != null)
				mChatRoom = lc.getOrCreateChatRoom("sip:cloudplay@" + accu.get().getDomain());
		} catch (Exception e) {
			e.printStackTrace();
		}
		mOpenListener = openListener;
		mFindListener = findListener;
		mJoinListener = joinListener;
		mWriter = new ObjectProvider().getContext(this.getClass());
		mHttp = http;
		mAcct = accu;
		
		Log.wtf("chatroom", "base setting");
//		mHttp = http.get();
//		mHttp.setBase("http://140.96.116.38:8080");
//		mHttp.setAuth(accu.get().getUsername(), accu.get().getPassword());
//		mHttp.log();
		mFactory = factory;
	}
	public void setCData(CData cdata) {
		this.cdata = cdata;
	}
	

//	private void init() {
//		mHttp.setBase("http://140.96.116.38:8080");
//		mHttp.setAuth(mAcct.get().getUsername(), mAcct.get().getPassword());
//		mHttp.log();
//	}
	public Optional<List<Comments.Comment>> listComments(String opID, Long startTime, Long afterN) {
//		init();
		return mHttp.get().optComments(opID, startTime, afterN);
	}
	
	public Optional<List<OPInfos.OPInfo>> listOPInfos(Long startTime, Long afterN) {
		return mHttp.get().optOPInfos(startTime, afterN);
	}
	
	@Override
	public Future<List<OPInfos.OPInfo>> asyncListOPInfo(TypeListener<List<OPInfos.OPInfo>> tl, Long startTime, Long afterN) {
		return mHttp.get().asyncListOPInfos(tl, startTime, afterN);
	}
	
	public Optional<List<Contacts.Contact>> listContacts() {
		return mHttp.get().optContacts();
	}
	
	public Optional<List<Loves.Love>> listLoves(String opID) {
		return mHttp.get().optLoves(opID);
	}
	
	public Optional<List<Resources.Resource>> listResources() {
		return mHttp.get().optResources();
	}
	
	public Optional<List<Resources.Resource>> listResources(String type) {
		return mHttp.get().optResources(type);
	}
	
	public void save(List<Contact> contacts) {
		mHttp.get().save(contacts);
	}
	
	public void save(OPInfo opInfo) {
		mHttp.get().save(opInfo);
	}
	
	public void save(Resource resource) {
		mHttp.get().save(resource);
	}
	public void save(String opID, List<Comment> comments) {
		mHttp.get().save(opID, comments);
	}
	public void save(String opID, Love love) {
		mHttp.get().save(opID, love);
	}

	@Override @SneakyThrows
	public void start(String scene) {
		ShareRtpAv share_av2 = mFactory.rtpAv(SHARE_RTPAV.start, new CData(), scene);
        share_av2.receivers("10d");
        getChatRoom().sendMessage(mWriter.writeValueAsString(share_av2));
	}
	
	private LinphoneChatRoom getChatRoom() {
		if (mChatRoom == null)
			mChatRoom = LinphoneManager.getLc().getOrCreateChatRoom("sip:cloudplay@" + mAcct.get().getDomain());
		return mChatRoom;
	}

	@Override @SneakyThrows
	public void open(String scene) {
		ShareRtpAv share_av2 = mFactory.rtpAv(SHARE_RTPAV.open, new CData(), scene);
        share_av2.receivers("10d");
        getChatRoom().sendMessage(mWriter.writeValueAsString(share_av2));
	}

	CData cdata;
	@Override @SneakyThrows
	public void ready(String scene) {
		ShareRtpAv share_av2 = mFactory.rtpAv(SHARE_RTPAV.ready, Module.getCData(), scene);
        share_av2.receivers("10d");
        getChatRoom().sendMessage(mWriter.writeValueAsString(share_av2));
	}

	@Override @SneakyThrows
	public void invite(String scene) {
		// TODO Auto-generated method stub
		
	}

	@Override @SneakyThrows
	public void answer(String scene) {
        ShareRtpAv share_av2 = mFactory.rtpAv(SHARE_RTPAV.answer, Module.getCData(), scene);
        share_av2.receivers("10d");
        getChatRoom().sendMessage(mWriter.writeValueAsString(share_av2));
	}
	
	@Override @SneakyThrows
	public void finish(String scene) {
		ShareRtpAv share_av2 = mFactory.rtpAv(SHARE_RTPAV.finish, Module.getCData(), scene);
        share_av2.receivers("10d");
        getChatRoom().sendMessage(mWriter.writeValueAsString(share_av2));
        Module.setCData(new CData());
	}

	@Override
	public void addOpenListener(OpenListener listener) {
		mOpenListener.add(listener);
	}

	@Override
	public void addFindListener(FindListener listener) {
		mFindListener.add(listener);
	}

	@Override
	public void addJoinListener(JoinListener listener) {
		mJoinListener.add(listener);
	}



	@Override
	public Set<OpenListener> getOpenListener() {
		return mOpenListener;
	}

	@Override
	public Set<FindListener> getFindListener() {
		return mFindListener;
	}

	@Override
	public Set<JoinListener> getJoinListener() {
		return mJoinListener;
	}

}
