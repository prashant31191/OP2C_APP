package org.itri.icl.x300.op2ca.utils;

import java.sql.SQLException;

import org.itri.icl.x300.op2ca.data.Function;

import com.j256.ormlite.android.extras.AndroidBaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

public class FunctionDao extends AndroidBaseDaoImpl<Function, Long> {
	public FunctionDao(ConnectionSource conn, Class<Function> dataClass)
			throws SQLException {
		super(conn, dataClass);
	}

}
