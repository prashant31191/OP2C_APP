package org.itri.icl.x300.op2ca.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.data.Device;
import org.itri.icl.x300.op2ca.data.Function;
import org.itri.icl.x300.op2ca.data.Message;
import org.itri.icl.x300.op2ca.data.Resource;
import org.itri.icl.x300.op2ca.data.TYPE;
import org.itri.icl.x300.op2ca.data.TYPE.FUNC_TYPE;
import org.itri.icl.x300.op2ca.utils.DeviceDao;
import org.itri.icl.x300.op2ca.utils.FunctionDao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.android.extras.AndroidBaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import data.Contacts.Contact;
import data.Resources;

@Log
public class OpDB extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "opdb.db";
	private static final int DATABASE_VERSION = 4;
	private Dao<Resource, Long> resDao;
	private Dao<Contact, String> ctctDao;
	private Dao<Device, Long> devDao;
	private Dao<Function, Long> funDao;
	private DeviceDao mDeviceDao;
	private FunctionDao mFunctionDao;
	private Dao<Message, Long> msgDao;
	
	public OpDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource conn) {
		try {
			TableUtils.createTable(conn, Resource.class);
			TableUtils.createTable(conn, Device.class);
			TableUtils.createTable(conn, Function.class);
//			TableUtils.createTable(conn, People.class);
//			TableUtils.createTable(conn, Phone.class);
//			TableUtils.createTable(conn, Group.class);
			TableUtils.createTable(conn, Message.class);
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
			TableUtils.dropTable(conn, Resource.class, true);
			TableUtils.dropTable(conn, Device.class, true);
			TableUtils.dropTable(conn, Function.class, true);
//			TableUtils.dropTable(conn, People.class, true);
//			TableUtils.dropTable(conn, Phone.class, true);
//			TableUtils.dropTable(conn, Group.class, true);
			TableUtils.dropTable(conn, Message.class, true);
			TableUtils.dropTable(conn, Contact.class, true);
			onCreate(db, conn);
		} catch (SQLException e) {
			log.severe(OpDB.class.getName() + " Can't drop databases");
			throw new RuntimeException(e);
		}
	}

	@SneakyThrows
	public void init() {
		Device d1 = Device.of("我的手機");
		Function f1 = Function.of(FUNC_TYPE.VIDEO, "影像裝置");
		Function f2 = Function.of(FUNC_TYPE.VIDEO, "影音裝置");
		d1 = devDao().createIfNotExists(d1);
		f1.setDevice(d1);
		f2.setDevice(d1);
		funDao().createIfNotExists(f1);
		funDao().createIfNotExists(f2);
		
		Device d2 = Device.of("虛擬裝置");
		Function f3 = Function.of(FUNC_TYPE.VIDEO, "影像裝置");
		Function f4 = Function.of(FUNC_TYPE.AUDIO, "我的mic");
		d2 = devDao().createIfNotExists(d2);
		f3.setDevice(d2);
		f4.setDevice(d2);
		funDao().createIfNotExists(f3);
		funDao().createIfNotExists(f4);
		
		
		Device d3 = Device.of("虛擬裝置2");
		Function f6 = Function.of(FUNC_TYPE.AUDIO, "你的mic");
		d3 = devDao().createIfNotExists(d3);
		f6.setDevice(d3);
		funDao().createIfNotExists(f6);
		
		
		
		
		
		resDao().createIfNotExists(Resource.of(1, "0000909111069", "SELF", "52館攝影機69", "video", "Share Content", 42, 24, 22, 44, System.currentTimeMillis() - 10000, System.currentTimeMillis()));
		resDao().createIfNotExists(Resource.of(2, "0000909111050", "SELF", "外拍50", "image", "Share Content", 12, 21, 11, 22, System.currentTimeMillis() - 10000, System.currentTimeMillis()));
		resDao().createIfNotExists(Resource.of(3, "yf25", "SELF", "小美專案修改文件yf25", "docum", "Share Content", 01, 10, 11, 00, System.currentTimeMillis() - 10000, System.currentTimeMillis()));
		resDao().createIfNotExists(Resource.of(4, "py71", "SELF", "妹妹畢業典禮py71", "image", "Share Content", 35, 53, 33, 55, System.currentTimeMillis() - 10000, System.currentTimeMillis()));
		
		
		Resource res = Resource.of(5, "0000909111055", "自己", "去年尾牙55", "audio", "Share Content", 03, 30, 00, 33, System.currentTimeMillis() - 10000, System.currentTimeMillis());
		res = resDao().createIfNotExists(res);
		
		Message m1 = Message.of("0931333405", "SELF", "Hello1", System.currentTimeMillis() - 10000L, res);
		Message m2 = Message.of("0931333405", "SELF", "Hello2", System.currentTimeMillis() - 9000L, res);
		Message m3 = Message.of("0931333405", "SELF", "Hello3", System.currentTimeMillis() - 8000L, res);
		Message m4 = Message.of("0931333405", "SELF", "Hello4", System.currentTimeMillis() - 7000L, res);
		Message m5 = Message.of("0931333405", "SELF", "Hello5", System.currentTimeMillis() - 6000L, res);
		Message m6 = Message.of("0931333405", "SELF", "Hello6", System.currentTimeMillis() - 5000L, res);
		Message m7 = Message.of("SELF", "0931333405", "Hello7", System.currentTimeMillis() - 4000L, res);
		Message m8 = Message.of("0931333405", "SELF", "Hello8", System.currentTimeMillis() - 3000L, res);
		Message m9 = Message.of("0931333405", "SELF", "Hello9", System.currentTimeMillis() - 2000L, res);
		Message m10 = Message.of("0931333405", "SELF", "Hello10", System.currentTimeMillis() - 1000L, res);
		Message m11 = Message.of("0931333405", "SELF", "Hello11", System.currentTimeMillis() - 900L, res);
		Message m12 = Message.of("0931333405", "SELF", "Hello12", System.currentTimeMillis() - 800L, res);
//		Message m13 = Message.of("0931333405", "SELF", "Hello13", System.currentTimeMillis() - 700L, res);
		
		
		
		
		msgDao().createIfNotExists(m1);
		msgDao().createIfNotExists(m2);
		msgDao().createIfNotExists(m3);
		msgDao().createIfNotExists(m4);
		msgDao().createIfNotExists(m5);
		msgDao().createIfNotExists(m6);
		msgDao().createIfNotExists(m7);
		msgDao().createIfNotExists(m8);
		msgDao().createIfNotExists(m9);
		
		msgDao().createIfNotExists(m10);
		msgDao().createIfNotExists(m11);
		msgDao().createIfNotExists(m12);
		
		resDao().createIfNotExists(Resource.of(6, "0000909111032", "自己", "桌上攝影機32", "audio", "Share Content", 03, 30, 00, 33, System.currentTimeMillis() - 10000, System.currentTimeMillis()));
	}
	@SneakyThrows
	public AndroidBaseDaoImpl<Device, Long> getDeviceDao() {
		if (mDeviceDao == null) {
			mDeviceDao = new DeviceDao(getConnectionSource(), Device.class);
		}
		return mDeviceDao;
	}
	
	@SneakyThrows
	public AndroidBaseDaoImpl<Function, Long> getFunctionDao() {
		if (mFunctionDao == null) {
			mFunctionDao = new FunctionDao(getConnectionSource(), Function.class);
		}
		return mFunctionDao;
	}
	public Dao<Function, Long> funDao() throws SQLException {
		if (funDao == null) {
			funDao = getDao(Function.class);
		}
		return funDao;
	}
	public Dao<Resource, Long> resDao() throws SQLException {
		if (resDao == null) {
			resDao = getDao(Resource.class);
		}
		return resDao;
	}

	
	public Dao<Message, Long> msgDao() throws SQLException {
		if (msgDao == null) {
			msgDao = getDao(Message.class);
		}
		return msgDao;
	}
	
	public Dao<Device, Long> devDao() throws SQLException {
		if (devDao == null) {
			devDao = getDao(Device.class);
		}
		return devDao;
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
	public void syncResource(List<Resource> resources) {
		resDao().callBatchTasks(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				return null;
			}
		});
	}
	
	
	@SneakyThrows
	public void syncContacts(Collection<Contact> contacts) {
		final long syncTime = System.currentTimeMillis();
		for(Contact contact : contacts) {
			contact.setSyncTime(syncTime);
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
		QueryBuilder<Resource, Long> qb = resDao.queryBuilder();
		qb.where().eq("type", "video");
		return resDao.query(qb.prepare());
	}

	
	public List<Resource> outbox() {
		List<Resource> outbox = Lists.newArrayList();
		return outbox;
	}
	
	
	@SneakyThrows
	public List<Device> video() {
		devDao = devDao();
		QueryBuilder<Device, Long> qb = devDao.queryBuilder();
		qb.where().eq("type", TYPE.FUNC_TYPE.VIDEO);
		return devDao.query(qb.prepare());
	}
	
	@SneakyThrows
	public List<Device> audio() {
		devDao = devDao();
		QueryBuilder<Device, Long> qb = devDao.queryBuilder();
		qb.where().eq("type", TYPE.FUNC_TYPE.AUDIO);
		return devDao.query(qb.prepare());
	}
	
	@SneakyThrows
	public List<Device> file() {
		devDao = devDao();
		QueryBuilder<Device, Long> qb = devDao.queryBuilder();
		qb.where().eq("type", TYPE.FUNC_TYPE.FILE);
		return devDao.query(qb.prepare());
	}
	
	@SneakyThrows
	public List<Device> other() {
		devDao = devDao();
		QueryBuilder<Device, Long> qb = devDao.queryBuilder();
		qb.where().eq("type", TYPE.FUNC_TYPE.OTHER);
		return devDao.query(qb.prepare());
	}
	
	@SneakyThrows
	public void markAsRead(Resource resource) {
		resDao = resDao();
		resource.setRead(true);
		log.warning("更新已讀");
		resDao.update(resource);
	}
}
