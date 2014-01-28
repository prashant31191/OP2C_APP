package org.itri.icl.x300.op2ca.adapter;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.SneakyThrows;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.db.OpDB;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.j256.ormlite.android.extras.AndroidBaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import data.Capability;
import data.Resources.Resource;

public class DeviceAdapter extends ArrayAdapter<Resource> {
	AndroidBaseDaoImpl<Resource, String> mDevDao;
	SimpleEntry<Resource, Boolean> entry;
	String mType;
	SelectArg mSelectArg;
	@SneakyThrows
	public DeviceAdapter(OpDB opDB, String type, List<Resource> device) throws SQLException {
		super(App.getCtx(), R.layout.op2c_item_group, R.id.txtDevice, new ArrayList<Resource>());
		mDevDao = opDB.getDeviceDao();
		QueryBuilder<Capability, Long> qb = opDB.getFunctionDao().queryBuilder();
		mType = type;
		qb.where().eq("type", mType);
		addAll(mDevDao.query(mDevDao.queryBuilder().join(qb).distinct().prepare()));
		for (Resource devArg : device) {
			Resource dev = mDevDao.queryForId(devArg.getUri());
			entry = new SimpleEntry<Resource, Boolean>(dev, true);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		Resource device = getItem(position);
		Collection<Capability> coll = Collections2.filter(device.getCapabilities(), new Predicate<Capability>() {
			@Override
			public boolean apply(Capability arg0) {
				return arg0.getType().toString().contains(mType);
			}
		});
		device.setCapabilities(coll);
		TextView txtDevice = (TextView) view.findViewById(R.id.txtDevice);

		txtDevice.setText(device.getDisplayName() + "(" + Joiner.on(", ").skipNulls().join(coll) + ")");
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
	public void checkItem(Resource device) {
		if (entry != null && entry.getValue() && entry.getKey().equals(device)) {
			entry.setValue(false);
		} else {
			entry = new SimpleEntry<Resource, Boolean>(device, true);
		}
	}


	public ArrayList<Resource> readChecked() {
		if (entry == null) return new ArrayList<Resource>();
		Resource resource = entry.getKey();
//		final ResourceArg resourceArg = new ResourceArg(resource.getUri(), resource.getDisplayName(), resource.getStatus());
//		Collection<CapabilityArg> capArg = Collections2.transform(resource.getCapabilities(), new Function<Capability, CapabilityArg>() {
//			@Override
//			public CapabilityArg apply(Capability arg0) {
//				CapabilityArg arg = new CapabilityArg(arg0.getId(), 
//													  arg0.getType(), 
//													  arg0.getText(), resourceArg);
//				return arg;
//			}
//		});
//		resource.setCapabilities(capArg);
		return (entry != null && entry.getValue()) ? Lists.newArrayList(resource) : new ArrayList<Resource>();
	}

	public void clearChecked() {
		entry = null;
		notifyDataSetChanged();
	}
}
