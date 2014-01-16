package org.itri.icl.x300.op2ca.adapter;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.App;
import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.ResourceDao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.j256.ormlite.android.extras.AndroidBaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.ResourceCursorTreeAdapter;
import android.widget.TextView;

@Log
public class FunctionAdapter extends ResourceCursorTreeAdapter {
	
	public FunctionAdapter(Context context, Cursor cursor, int groupLayout, int childLayout) {
		super(context, cursor, groupLayout, childLayout);
		// TODO Auto-generated constructor stub
	}
//	AndroidBaseDaoImpl<Function, Long> mFunctionDao;
//	SimpleEntry<Function, Boolean> entry;
//	OpDB mOpDB;
//	@SneakyThrows
//	public FunctionAdapter(OpDB opDB, ArrayList<Function> funs) throws SQLException {
//		super(App.getCtx(), opDB.getFunctionDao().getCursor(opDB.getFunctionDao().queryBuilder().groupBy("device_id").prepare()), R.layout.op2c_item_group, R.layout.op2c_item_child);
//		mFunctionDao = opDB.getFunctionDao();
//		log.warning("adapter create");
//		for(Function fun : funs) {
//			entry = new SimpleEntry<Function, Boolean>(fun, true);
//		}
////		setFilterQueryProvider(new FilterQueryProvider() {  
////			@SneakyThrows
////			public Cursor runQuery(CharSequence constraint) {  
////				QueryBuilder<Function, Long> qb = mFunctionDao.queryBuilder();
////				qb.where().like("text", "%" + constraint.toString() + "%");
////				qb.groupBy("device_id");
////				return mFunctionDao.getCursor(qb.prepare());
////			}  
////		}); 
//	}
//	
//	
//	@Override @SneakyThrows
//	protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
//        Function function = mFunctionDao.queryForId(cursor.getLong(cursor.getColumnIndex("_id")));
//        TextView txtChild = (TextView)view.findViewById(R.id.txtChild);
//        if (entry != null && entry.getValue() && entry.getKey().equals(function)) {
//        	txtChild.setTypeface(null, Typeface.BOLD);
//        } else {
//        	txtChild.setTypeface(null, Typeface.NORMAL);
//        }
//        String text = cursor.getString(cursor.getColumnIndex("text"));
//        txtChild.setText(text);   
//	}
//
//	@Override @SneakyThrows
//	protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
//		Function function = mFunctionDao.queryForId(cursor.getLong(cursor.getColumnIndex("_id")));
//		Device device = function.getDevice();
//		TextView txtDevice = (TextView) view.findViewById(R.id.txtDevice);
//		txtDevice.setText(device.getText());
//		if (entry != null && entry.getValue() && entry.getKey().getDevice().equals(device)) {
//			txtDevice.setTypeface(null, Typeface.BOLD);
//        } else {
//        	txtDevice.setTypeface(null, Typeface.NORMAL);
//        }
//		
//		RadioButton rdoDevice = (RadioButton) view.findViewById(R.id.rdoDevice);
//		if (entry != null && entry.getValue() && entry.getKey().getDevice().equals(device)) {
//			rdoDevice.setChecked(true);
//		} else {
//			rdoDevice.setChecked(false);
//		}
//		rdoDevice.setEnabled(false);
//	}
//
//	@Override @SneakyThrows
//	protected Cursor getChildrenCursor(Cursor groupCursor) {
//		int idColumn = groupCursor.getColumnIndex("device_id");
//		for(String cname : groupCursor.getColumnNames()) {
//        	log.warning("groupCursor name = " + cname);
//        }
////		mFunctionDao.getCursor(mFunctionDao.queryBuilder().selectColumns("_id"mDeviceDao.queryForId(groupCursor.getLong(idColumn))
//		return mFunctionDao.getCursor(mFunctionDao.queryBuilder().where().eq("device_id", groupCursor.getString(idColumn)).prepare());
//	}
//	
//	public FilterQueryProvider getFilterQueryProvider() {
//		return new FilterQueryProvider() {
//			@Override @SneakyThrows
//			public Cursor runQuery(CharSequence constraint) {
//				QueryBuilder<Function, Long> qb = mFunctionDao.queryBuilder();
//				qb.where().like("text", "%" + constraint.toString() + "%");
//				qb.groupBy("device_id");
//				return mFunctionDao.getCursor(qb.prepare());
//			}
//		};
//	}
//	
//	/**
//	 * @param id
//	 */
//	@SneakyThrows
//	public void checkItem(long id) {
//		Function function = mFunctionDao.queryForId(id);
//		log.warning(function.getText());
//		if (entry != null && entry.getValue() && entry.getKey().equals(function)) {
//			entry.setValue(false);
//		} else {
//			entry = new SimpleEntry<Function, Boolean>(function, true);
//		}
//	}
//	
////	@SneakyThrows
////	public void writeChecked() {
////		UpdateBuilder<Function, Long> ub = mFunctionDao.updateBuilder();
////		ub.updateColumnValue("checked", false);
////		ub.where().eq("checked", true);
////		mFunctionDao.update(ub.prepare());
////		if (entry != null) {
////			mFunctionDao.update(mFunChecked);
////		}
////	}
//	
//	public ArrayList<Function> readChecked() {
//		return (entry != null && entry.getValue()) ? Lists.newArrayList(entry.getKey()) : new ArrayList<Function>();
//	}
//	public void clearChecked() {
//		entry = null;
//		notifyDataSetChanged();
//	}
//	

	@Override
	protected void bindChildView(View view, Context context, Cursor cursor,
			boolean isLastChild) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void bindGroupView(View view, Context context, Cursor cursor,
			boolean isExpanded) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
