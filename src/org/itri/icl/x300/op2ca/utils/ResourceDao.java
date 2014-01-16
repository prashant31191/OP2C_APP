package org.itri.icl.x300.op2ca.utils;

import java.sql.SQLException;

import com.j256.ormlite.android.extras.AndroidBaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import data.Resources.Resource;

public class ResourceDao extends AndroidBaseDaoImpl<Resource, String> {
	public ResourceDao(ConnectionSource conn, Class<Resource> dataClass)
			throws SQLException {
		super(conn, dataClass);
	}

}
