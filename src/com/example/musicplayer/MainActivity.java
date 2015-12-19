package com.example.musicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final boolean DEBUG = true;

	private static final String TAB_TAG_MUSIC_PERSON = "music person";
	private static final String TAB_TAG_ALBUM = "album";
	private static final String TAB_TAG_PLAY_LIST = "play list";
	private static final String TAB_TAG_PLAY_SONGS = "songs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initTableHost();
	}

	private void initTableHost() {
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		TabSpec tabSpec = tabHost.newTabSpec(TAB_TAG_MUSIC_PERSON)
				.setIndicator(getString(R.string.music_person))
				.setContent(R.id.artist_album);
		tabHost.addTab(tabSpec);
		tabSpec = tabHost.newTabSpec(TAB_TAG_ALBUM)
				.setIndicator(getString(R.string.album)).setContent(R.id.album_songs);
		tabHost.addTab(tabSpec);
		tabSpec = tabHost.newTabSpec(TAB_TAG_PLAY_LIST)
				.setIndicator(getString(R.string.play_list))
				.setContent(R.id.play_list);
		tabHost.addTab(tabSpec);
		tabSpec = tabHost.newTabSpec(TAB_TAG_PLAY_SONGS)
				.setIndicator(getString(R.string.song)).setContent(R.id.song);
		tabHost.addTab(tabSpec);

		tabHost.setOnClickListener(mOnTabClickListener);
		tabHost.setOnTabChangedListener(mOnTabChnageListener);
	}
	
	OnTabChangeListener mOnTabChnageListener = new OnTabChangeListener() {
		
		@Override
		public void onTabChanged(String tabId) {
			if (DEBUG) {
				Log.i(TAG, "tabHost->onTabChanged:[" + tabId + "]");
			}			
		}
	};
	
	OnClickListener mOnTabClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (DEBUG) {
				Log.i(TAG, "tabHost->onClick:[" + v.getTag() + "]");
			}
		}
	};
}
