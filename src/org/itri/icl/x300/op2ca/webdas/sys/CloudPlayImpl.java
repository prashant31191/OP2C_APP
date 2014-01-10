package org.itri.icl.x300.op2ca.webdas.sys;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.SneakyThrows;

import org.itri.icl.x300.op2ca.Module;
import org.itri.icl.x300.op2ca.utils.CloudPlay;
import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneChatRoom;

import provider.ObjectProvider;
import provider.Operations;
import schema.element.Account;
import schema.element.CData;
import schema.element.ENUM.STEP.SHARE_RTPAV;
import schema.operation.ShareRtpAv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;

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
	@Inject Operations mFactory;
	Http mHttp;
	Injector injector = Guice.createInjector(new Module());
	Set<OpenListener> mOpenListener = Sets.newHashSet();
	Set<FindListener> mFindListener = Sets.newHashSet();
	Set<JoinListener> mJoinListener = Sets.newHashSet();
	@Inject
	public CloudPlayImpl(Provider<Http> http, Provider<Account> accu) {
		mChatRoom = LinphoneManager.getLc().getOrCreateChatRoom("sip:cloudplay@" + accu.get().getDomain());
		mWriter = new ObjectProvider().getContext(this.getClass());
		mFactory = injector.getInstance(Operations.class);
		mHttp = http.get();
		mHttp.setAuth(accu.get().getUsername(), accu.get().getPassword());
		mHttp.log();
	}
	

	public Optional<List<Comments.Comment>> listComments(String opID, Long startTime, Long afterN) {
		return mHttp.optComments(opID, startTime, afterN);
	}
	
	public Optional<List<OPInfos.OPInfo>> listOPInfos(Long startTime, Long afterN) {
		return mHttp.optOPInfos(startTime, afterN);
	}
	
	public Optional<List<Contacts.Contact>> listContacts() {
		return mHttp.optContacts();
	}
	
	public Optional<List<Loves.Love>> listLoves(String opID) {
		return mHttp.optLoves(opID);
	}
	
	public Optional<List<Resources.Resource>> listResources() {
		return mHttp.optResources();
	}
	
	public Optional<List<Resources.Resource>> listResources(String type) {
		return mHttp.optResources(type);
	}
	
	public void save(List<Contact> contacts) {
		mHttp.save(contacts);
	}
	
	public void save(OPInfo opInfo) {
		mHttp.save(opInfo);
	}
	
	public void save(Resource resource) {
		mHttp.save(resource);
	}
	public void save(String opID, List<Comment> comments) {
		mHttp.save(opID, comments);
	}
	public void save(String opID, Love love) {
		mHttp.save(opID, love);
	}
	
	
	
	
	

	@Override @SneakyThrows
	public void start() {
		ShareRtpAv share_av2 = mFactory.rtpAv(SHARE_RTPAV.start, new CData());
        share_av2.receivers("10d");
        mChatRoom.sendMessage(mWriter.writeValueAsString(share_av2));
	}

	@Override @SneakyThrows
	public void open() {
		ShareRtpAv share_av2 = mFactory.rtpAv(SHARE_RTPAV.open, new CData());
        share_av2.receivers("10d");
        mChatRoom.sendMessage(mWriter.writeValueAsString(share_av2));
	}

	@Override @SneakyThrows
	public void ready() {
		ShareRtpAv share_av2 = mFactory.rtpAv(SHARE_RTPAV.ready, new CData());
        share_av2.receivers("10d");
        mChatRoom.sendMessage(mWriter.writeValueAsString(share_av2));
	}

	@Override @SneakyThrows
	public void invite() {
		// TODO Auto-generated method stub
		
	}

	@Override @SneakyThrows
	public void answer() {
        ShareRtpAv share_av2 = mFactory.rtpAv(SHARE_RTPAV.answer, new CData());
        share_av2.receivers("10d");
        mChatRoom.sendMessage(mWriter.writeValueAsString(share_av2));
	}
	
	@Override @SneakyThrows
	public void finish() {
		ShareRtpAv share_av2 = mFactory.rtpAv(SHARE_RTPAV.finish, new CData());
        share_av2.receivers("10d");
        mChatRoom.sendMessage(mWriter.writeValueAsString(share_av2));
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


	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
}
