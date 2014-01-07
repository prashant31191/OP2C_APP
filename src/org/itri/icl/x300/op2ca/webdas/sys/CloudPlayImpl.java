package org.itri.icl.x300.op2ca.webdas.sys;

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
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CloudPlayImpl implements CloudPlay {
	@Inject Provider<Account> mAcc;
	LinphoneChatRoom mChatRoom;
	ObjectMapper mWriter;
	@Inject Operations mFactory;
	Injector injector = Guice.createInjector(new Module());
	Set<OpenListener> mOpenListener = Sets.newHashSet();
	Set<FindListener> mFindListener = Sets.newHashSet();
	Set<JoinListener> mJoinListener = Sets.newHashSet();
	public CloudPlayImpl() {
		mChatRoom = LinphoneManager.getLc().getOrCreateChatRoom("sip:cloudplay@" + mAcc.get().getDomain());
		mWriter = new ObjectProvider().getContext(this.getClass());
		mFactory = injector.getInstance(Operations.class);
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
}
