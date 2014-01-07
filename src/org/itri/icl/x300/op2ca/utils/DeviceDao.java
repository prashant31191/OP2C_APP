package org.itri.icl.x300.op2ca.utils;

import java.sql.SQLException;

import org.itri.icl.x300.op2ca.data.Device;

import com.j256.ormlite.android.extras.AndroidBaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

public class DeviceDao extends AndroidBaseDaoImpl<Device, Long> {
	public DeviceDao(ConnectionSource conn, Class<Device> dataClass)
			throws SQLException {
		super(conn, dataClass);
	}

}
