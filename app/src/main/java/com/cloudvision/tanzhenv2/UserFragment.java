package com.cloudvision.tanzhenv2;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UserFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.activity_user, container, false);
	}
	
	@Override
	public void onResume() {

	    super.onResume();
	    getView().setFocusableInTouchMode(true);
	    getView().requestFocus();
	    getView().setOnKeyListener(new View.OnKeyListener() {
	        @Override
	        public boolean onKey(View v, int keyCode, KeyEvent event) {

	            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

	                // handle back button
	            	getFragmentManager().popBackStack();
	                return true;
	            }
	            return false;
	        }
	    });
	}
}
