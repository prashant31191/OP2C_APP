package org.itri.icl.x300.op2ca.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.data.ResourceV1;
import org.itri.icl.x300.op2ca.utils.CapabilityDao;
import org.itri.icl.x300.op2ca.utils.ResourceDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.android.extras.AndroidBaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import data.Capability;
import data.Comments.Comment;
import data.Contacts.Contact;
import data.Loves.Love;
import data.Member;
import data.OPInfos.OPInfo;
import data.Resources.Resource;

@Log
public class OpDB extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "opdb.db";
	private static final int DATABASE_VERSION = 2;
	private Dao<ResourceV1, Long> resDao;
	private Dao<Contact, String> ctctDao;
	private Dao<Resource, String> rescDao;
	private Dao<Comment, Long> cmntDao;
	
	private Dao<OPInfo, String> infoDao;
	
//	private Dao<Device, Long> devDao;
	private Dao<Capability, Long> funDao;
	
	private ResourceDao mDeviceDao;
	private CapabilityDao mFunctionDao;
//	private Dao<Message, Long> msgDao;
	
	public OpDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource conn) {
		try {
			//TableUtils.createTable(conn, ResourceV1.class);
			TableUtils.createTable(conn, Resource.class);
			TableUtils.createTable(conn, Capability.class);
			TableUtils.createTable(conn, OPInfo.class);
			TableUtils.createTable(conn, Love.class);
			TableUtils.createTable(conn, Comment.class);
			TableUtils.createTable(conn, Member.class);
			TableUtils.createTable(conn, Contact.class);
			init();
		} catch (SQLException e) {log.severe("Can't create database");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource conn, int arg2, int arg3) {
		try {log.info("onUpgrade()");
			//TableUtils.dropTable(conn, ResourceV1.class, true);
			TableUtils.dropTable(conn, Resource.class, true);
			TableUtils.dropTable(conn, OPInfo.class, true);
			TableUtils.dropTable(conn, Love.class, true);
			TableUtils.dropTable(conn, Capability.class, true);
			TableUtils.dropTable(conn, Comment.class, true);
			TableUtils.dropTable(conn, Member.class, true);
			TableUtils.dropTable(conn, Contact.class, true);
			onCreate(db, conn);
		} catch (SQLException e) {
			log.severe(OpDB.class.getName() + " Can't drop databases");
			throw new RuntimeException(e);
		}
	}

	@SneakyThrows
	public void init() {
		Resource d1 = Resource.of("0,", "我的手機", "ok", new HashSet<Capability>());
		Capability f1 = Capability.of("video", "影像裝置");
		Capability f2 = Capability.of("audio", "影音裝置");
		d1 = rescDao().createIfNotExists(d1);
		f1.setResource(d1);
		f2.setResource(d1);
		funDao().createIfNotExists(f1);
		funDao().createIfNotExists(f2);
		
		Resource d2 = Resource.of("1,", "虛擬裝置", "ok", new HashSet<Capability>());
		Capability f3 = Capability.of("video", "影像裝置");
		Capability f4 = Capability.of("audio", "我的mic");
		d2 = rescDao().createIfNotExists(d2);
		f3.setResource(d2);
		f4.setResource(d2);
		funDao().createIfNotExists(f3);
		funDao().createIfNotExists(f4);
		
		
		Resource d3 = Resource.of("2,", "手機麥克風", "ok", new HashSet<Capability>());
		Capability f6 = Capability.of("audio", "你的mic");
		d3 = rescDao().createIfNotExists(d3);
		f6.setResource(d3);
		funDao().createIfNotExists(f6);
		
		
		
		
		
		infoDao().createIfNotExists(OPInfo.of("1", "0000909111069", "SELF", "52館攝影機69", "video", "Share Content"));
		infoDao().createIfNotExists(OPInfo.of("2", "0000909111050", "SELF", "外拍50", "image", "Share Content"));
		infoDao().createIfNotExists(OPInfo.of("3", "yf25", "SELF", "小美專案修改文件yf25", "docum", "Share Content"));
		infoDao().createIfNotExists(OPInfo.of("4", "py71", "SELF", "妹妹畢業典禮py71", "image", "Share Content"));
		
		infoDao().createIfNotExists(OPInfo.of("6", "10d", "SELF", "我的分享", "video", "我的分享"));
		
		
		OPInfo res = OPInfo.of("5", "0000909111055", "自己", "去年尾牙55", "audio", "Share Content");
		res = infoDao().createIfNotExists(res);
		
		Comment m1 = Comment.of(res, "0931333405", "Hello1", System.currentTimeMillis() - 10000L);
		Comment m2 = Comment.of(res, "0931333405", "Hello2", System.currentTimeMillis() - 9000L);
		Comment m3 = Comment.of(res, "0931333405", "Hello3", System.currentTimeMillis() - 8000L);
		Comment m4 = Comment.of(res, "0931333405", "Hello4", System.currentTimeMillis() - 7000L);
		Comment m5 = Comment.of(res, "0931333405", "Hello5", System.currentTimeMillis() - 6000L);
		Comment m6 = Comment.of(res, "0931333405", "Hello6", System.currentTimeMillis() - 5000L);
		Comment m7 = Comment.of(res, "0931333405", "Hello7", System.currentTimeMillis() - 4000L);
		Comment m8 = Comment.of(res, "0931333405", "Hello8", System.currentTimeMillis() - 3000L);
		Comment m9 = Comment.of(res, "0931333405", "Hello9", System.currentTimeMillis() - 2000L);
		Comment m10 = Comment.of(res, "0931333405", "Hello10", System.currentTimeMillis() - 1000L);
		Comment m11 = Comment.of(res, "0931333405", "Hello11", System.currentTimeMillis() - 900L);
		Comment m12 = Comment.of(res, "0931333405", "Hello12", System.currentTimeMillis() - 800L);
//		Message m13 = Message.of("0931333405", "SELF", "Hello13", System.currentTimeMillis() - 700L, res);
		
		
		
		
		cmntDao().createIfNotExists(m1);
		cmntDao().createIfNotExists(m2);
		cmntDao().createIfNotExists(m3);
		cmntDao().createIfNotExists(m4);
		cmntDao().createIfNotExists(m5);
		cmntDao().createIfNotExists(m6);
		cmntDao().createIfNotExists(m7);
		cmntDao().createIfNotExists(m8);
		cmntDao().createIfNotExists(m9);
		
		cmntDao().createIfNotExists(m10);
		cmntDao().createIfNotExists(m11);
		cmntDao().createIfNotExists(m12);
		
		//resDao().createIfNotExists(ResourceV1.of(6, "0000909111032", "自己", "桌上攝影機32", "audio", "Share Content", 03, 30, 00, 33, System.currentTimeMillis() - 10000, System.currentTimeMillis()));
	}
	@SneakyThrows
	public AndroidBaseDaoImpl<Resource, String> getDeviceDao() {
		if (mDeviceDao == null) {
			mDeviceDao = new ResourceDao(getConnectionSource(), Resource.class);
		}
		return mDeviceDao;
	}
	
	@SneakyThrows
	public AndroidBaseDaoImpl<Capability, Long> getFunctionDao() {
		if (mFunctionDao == null) {
			mFunctionDao = new CapabilityDao(getConnectionSource(), Capability.class);
		}
		return mFunctionDao;
	}
	public Dao<Capability, Long> funDao() throws SQLException {
		if (funDao == null) {
			funDao = getDao(Capability.class);
		}
		return funDao;
	}
	public Dao<ResourceV1, Long> resDao() throws SQLException {
		if (resDao == null) {
			resDao = getDao(ResourceV1.class);
		}
		return resDao;
	}

	
	public Dao<Comment, Long> cmntDao() throws SQLException {
		if (cmntDao == null) {
			cmntDao = getDao(Comment.class);
		}
		return cmntDao;
	}
	
	
	public Dao<OPInfo, String> infoDao() throws SQLException {
		if (infoDao == null) {
			infoDao = getDao(OPInfo.class);
		}
		return infoDao;
	}
	
	
	public Dao<Resource, String> rescDao() throws SQLException {
		if (rescDao == null) {
			rescDao = getDao(Resource.class);
		}
		return rescDao;
	}
	
//	public Dao<People, Long> manDao() throws SQLException {
//		if (manDao == null) {
//			manDao = getDao(People.class);
//		}
//		return manDao;
//	}
	
	public Dao<Contact, String> ctctDao() throws SQLException {
		if (ctctDao == null) {
			ctctDao = getDao(Contact.class);
		}
		return ctctDao;
	}
	
//	public Dao<Phone, Long> numDao() throws SQLException {
//		if (numDao == null) {
//			numDao = getDao(Phone.class);
//		}
//		return numDao;
//	}
//	
	
//	@SneakyThrows
//	public Function getFunction() {
//		QueryBuilder<Function, Long> qb = funDao().queryBuilder();
//		return funDao().queryForFirst(qb.where().eq("checked", true).prepare());
//	}
//	@SneakyThrows
//	public List<Phone> listChecked() {
//		return numDao().queryForEq("checked", true);
//	}
//	@SneakyThrows
//	public void clearChecked() {
//		UpdateBuilder<Phone, Long> ub = numDao().updateBuilder();
//		ub.updateColumnValue("checked", false);
//		ub.where().eq("checked", true);
//		int u = numDao.update(ub.prepare());
//		log.warning("u = " + u);
//	}
	
//	@SneakyThrows
//	public void clearChecked(Phone phone) {
//		phone.setChecked(false);
//		numDao().update(phone);
//	}
	
	@SneakyThrows
	public List<Contact> contacts() {
		return ctctDao().queryForAll();
	}
	
//	@SneakyThrows
//	public List<Phone> phone() {
//		return numDao().queryForAll();
//	}
	
	@SneakyThrows
	public void syncResource(final List<Resource> resources) {
		rescDao().callBatchTasks(new Callable<Void>() {
			@Override @SneakyThrows
			public Void call() throws Exception {
				for(Resource resource : resources) {
					rescDao().createOrUpdate(resource);
				}
				return null;
			}
		});
	}
	
	
	@SneakyThrows
	public void syncContacts(Collection<Contact> contacts) {
		Long syncTime = 0L;// = System.currentTimeMillis();
		for(Contact contact : contacts) {
			syncTime = contact.getSyncTime();
			CreateOrUpdateStatus status = ctctDao().createOrUpdate(contact);
			log.warning(status.isCreated() + " " + status.isUpdated());
		}
		DeleteBuilder<Contact, String> db = ctctDao().deleteBuilder();
		db.where().ne("syncTime", syncTime);
		ctctDao().delete(db.prepare());
	}
	/**
	 * 更新完成後 將舊的資料欄位刪除(以timestamp來判斷)
	 * @param people
	 */
//	@SneakyThrows
//	public void sync(Collection<People> list) {
//		final long syncTime = System.currentTimeMillis();
//		for(People people : list) {
//			people.setSyncTime(syncTime);
//			CreateOrUpdateStatus status = manDao().createOrUpdate(people);
//			for(Phone phone: people.getPhone()) {
//				phone.setSyncTime(syncTime);
//				numDao().createOrUpdate(phone);
//			}
//			log.warning(status.isCreated() + " " + status.isUpdated());
//		}
//		DeleteBuilder<Phone, Long> pb = numDao().deleteBuilder();
//		pb.where().ne("syncTime", syncTime);
//		numDao().delete(pb.prepare());
//		
//		DeleteBuilder<People, Long> db = manDao().deleteBuilder();
//		db.where().ne("syncTime", syncTime);
//		manDao().delete(db.prepare());
//	}

//	@SneakyThrows
//	public void update(Phone phone) {
//		numDao().update(phone);
//	}
	
	
	@SneakyThrows
	public List<Resource> videoResource() {
		QueryBuilder<Resource, String> qb = rescDao.queryBuilder();
		qb.where().eq("type", "video");
		return rescDao.query(qb.prepare());
	}

	
	public List<Resource> outbox() {
		List<Resource> outbox = Lists.newArrayList();
		return outbox;
	}
	
	
	@SneakyThrows
	public List<Resource> video() {
		rescDao = rescDao();
		QueryBuilder<Resource, String> qb = rescDao.queryBuilder();
		qb.where().eq("type", "video");
		return rescDao.query(qb.prepare());
	}
	
	@SneakyThrows
	public List<Resource> audio() {
		rescDao = rescDao();
		QueryBuilder<Resource, String> qb = rescDao.queryBuilder();
		qb.where().eq("type", "audio");
		return rescDao.query(qb.prepare());
	}
	
	@SneakyThrows
	public List<Resource> file() {
		rescDao = rescDao();
		QueryBuilder<Resource, String> qb = rescDao.queryBuilder();
		qb.where().eq("type", "file");
		return rescDao.query(qb.prepare());
	}
	
	@SneakyThrows
	public List<Resource> other() {
		rescDao = rescDao();
		QueryBuilder<Resource, String> qb = rescDao.queryBuilder();
		qb.where().eq("type", "other");
		return rescDao.query(qb.prepare());
	}
	
	
	@SneakyThrows
	public void markAllRead() {
		infoDao = infoDao();
		UpdateBuilder<OPInfo, String> ub = infoDao.updateBuilder();
		ub.updateColumnValue("read", true);
		ub.where().eq("read", false);
		log.warning("更新所有為已讀");
		infoDao.update(ub.prepare());
	}
	
	
	@SneakyThrows
	public void markAsRead(OPInfo resource) {
		infoDao = infoDao();
		resource.setRead(true);
		log.warning("更新已讀");
		infoDao.update(resource);
	}
}
