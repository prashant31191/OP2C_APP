package org.itri.icl.x300.op2ca.dialog;

import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.db.OpDB;
import org.itri.icl.x300.op2ca.utils.OrmLiteRoboDialogFragment;

import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;

public class CriteriaDialog extends OrmLiteRoboDialogFragment<OpDB> implements OnItemClickListener {

	@Inject FragmentManager mFM;
	@Inject WindowManager mWm;
	@InjectView(R.id.criteria_list) ListView mListView;
	public CriteriaDialog() {
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setStyle(R.style.YesNo, 0);
		return inflater.inflate(R.layout.op2c_dialog_order, c, false);
	}
	
	@Override
	public void onResume() {super.onResume();
		mListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, new String[] {"點擊率", "發佈日期"}));
		Point size = new Point();
		mWm.getDefaultDisplay().getSize(size);
		getDialog().getWindow().setLayout((int)(size.x * 0.8), LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}

}
