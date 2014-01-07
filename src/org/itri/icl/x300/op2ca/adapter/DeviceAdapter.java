package org.itri.icl.x300.op2ca.adapter;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.SneakyThrows;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.data.Device;
import org.itri.icl.x300.op2ca.data.Function;
import org.itri.icl.x300.op2ca.data.TYPE.FUNC_TYPE;
import org.itri.icl.x300.op2ca.db.OpDB;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.j256.ormlite.android.extras.AndroidBaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

public class DeviceAdapter extends ArrayAdapter<Device> {
	AndroidBaseDaoImpl<Device, Long> mDevDao;
	SimpleEntry<Device, Boolean> entry;
	FUNC_TYPE mType;
	SelectArg mSelectArg;
	@SneakyThrows
	public DeviceAdapter(OpDB opDB, FUNC_TYPE type, List<Device> device) throws SQLException {
		super(App.getCtx(), R.layout.op2c_item_group, R.id.txtDevice, new ArrayList<Device>());
		mDevDao = opDB.getDeviceDao();
//		mSelectArg = new SelectArg();
		QueryBuilder<Function, Long> qb = opDB.getFunctionDao().queryBuilder();
		mType = type;
		qb.where().eq("type", mType);
		addAll(mDevDao.query(mDevDao.queryBuilder().join(qb).distinct().prepare()));
		for (Device dev : device) {
			entry = new SimpleEntry<Device, Boolean>(dev, true);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		Device device = getItem(position);
		
		// Function function =
		// mDevDao.queryForId(cursor.getLong(cursor.getColumnIndex("_id")));
		// Device device = function.getDevice();
		Collection<Function> coll = Collections2.filter(device.getFunction(), new Predicate<Function>() {
			@Override
			public boolean apply(Function arg0) {
				return arg0.getType().toString().contains(mType.toString());
			}
		});
		device.setFunction(coll);
		TextView txtDevice = (TextView) view.findViewById(R.id.txtDevice);

		txtDevice.setText(device.getText() + "(" + Joiner.on(", ").skipNulls().join(coll) + ")");
		if (entry != null && entry.getValue() && entry.getKey().equals(device)) {
			txtDevice.setTypeface(null, Typeface.BOLD);
		} else {
			txtDevice.setTypeface(null, Typeface.NORMAL);
		}

		RadioButton rdoDevice = (RadioButton) view.findViewById(R.id.rdoDevice);
		if (entry != null && entry.getValue() && entry.getKey().equals(device)) {
			rdoDevice.setChecked(true);
		} else {
			rdoDevice.setChecked(false);
		}
		rdoDevice.setEnabled(false);
		return view;
	}

	/**
	 * @param id
	 */
	@SneakyThrows
	public void checkItem(Device device) {
		if (entry != null && entry.getValue() && entry.getKey().equals(device)) {
			entry.setValue(false);
		} else {
			entry = new SimpleEntry<Device, Boolean>(device, true);
		}
	}

	// @SneakyThrows
	// public void writeChecked() {
	// UpdateBuilder<Function, Long> ub = mFunctionDao.updateBuilder();
	// ub.updateColumnValue("checked", false);
	// ub.where().eq("checked", true);
	// mFunctionDao.update(ub.prepare());
	// if (entry != null) {
	// mFunctionDao.update(mFunChecked);
	// }
	// }

	public ArrayList<Device> readChecked() {
		return (entry != null && entry.getValue()) ? Lists.newArrayList(entry.getKey()) : new ArrayList<Device>();
	}

	public void clearChecked() {
		entry = null;
		notifyDataSetChanged();
	}
}
