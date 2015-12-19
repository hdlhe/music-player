package com.example.musicplayer;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayListFragment extends Fragment {
	private static final String TAG = "PlayListFragment";
	private static final boolean DEBUG = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(DEBUG){
			Log.i(TAG, "onCreate");
		}
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frame_play_list, null, false);
		return view;
	}
	
	@Override
	public void onPause() {
		if(DEBUG){
			Log.i(TAG, "onPause");
		}
		super.onPause();
	}
}
