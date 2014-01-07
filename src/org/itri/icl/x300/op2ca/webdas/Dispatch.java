package org.itri.icl.x300.op2ca.webdas;


import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.itri.icl.x300.op2ca.R;
import org.itri.icl.x300.op2ca.webdas.mgnt.MgmtHome;
import org.itri.icl.x300.op2ca.webdas.share.ShareHome;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@Log
public class Dispatch extends RoboFragment {
	FragmentManager mgr;
	Fragment mFragment;
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle state) {
		return inflater.inflate(R.layout.op2c_share_main, c, false);
	}
	@Override @SneakyThrows
    public void onViewCreated(View view, Bundle state) {
		super.onViewCreated(view, state);
//		((Main) getActivity()).next(new MgmtHome());
		if(isDetached())
			setArguments(new Bundle());
		mgr = ((Main)getActivity()).getSupportFragmentManager();
		if (getArguments().containsKey("mgmt")) {
			mFragment = new MgmtHome();
		} else if (getArguments().containsKey("outbox")) {
			mFragment = new ShareHome();
		}
		mgr.beginTransaction().add(R.id.share_embedded, mFragment, "step").commit();
	}
	
	public void onPause() {
		super.onPause();
		mgr.beginTransaction().remove(mFragment).commit();
	}
//	public void onDestroyView() {
//		super.onDestroyView();
//		
//	}
}
