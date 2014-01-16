package org.itri.icl.x300.op2ca.utils;

import java.sql.SQLException;

import com.j256.ormlite.android.extras.AndroidBaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import data.Capability;

public class CapabilityDao extends AndroidBaseDaoImpl<Capability, Long> {
	public CapabilityDao(ConnectionSource conn, Class<Capability> dataClass)
			throws SQLException {
		super(conn, dataClass);
	}

}
