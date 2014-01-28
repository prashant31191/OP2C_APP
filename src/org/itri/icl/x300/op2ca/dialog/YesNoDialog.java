package org.itri.icl.x300.op2ca.dialog;


import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.webdas.Main;

import com.google.inject.Inject;
import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class YesNoDialog extends RoboDialogFragment implements OnClickListener {

	@Inject FragmentManager mFM;
	@Inject WindowManager mWm;
	@InjectView(R.id.btn_custom_no) Button mBtnNo;
	@InjectView(R.id.btn_custom_yes) Button mBtnYes;
	
	public YesNoDialog() {
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setStyle(R.style.YesNo, 0);
		return inflater.inflate(R.layout.op2c_dialog_yesno, c, false);
	}
	@Override
	public void onResume() {super.onResume();
		Point size = new Point();
		mWm.getDefaultDisplay().getSize(size);
		getDialog().getWindow().setLayout((int)(size.x * 0.6), LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
		mBtnYes.setOnClickListener(this);
		mBtnNo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnYes) {
			((Main)getActivity()).prev();
			
			//DeviceDialog mDeviceDialog = new DeviceDialog();
			//mDeviceDialog.show(mFM, "device");
		} 
		dismiss();
	}
}
