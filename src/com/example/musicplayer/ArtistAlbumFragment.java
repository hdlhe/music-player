package com.example.musicplayer;

import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

public class ArtistAlbumFragment extends Fragment {
	private static final String TAG = "ArtistAlbumFragment";
	private static final boolean DEBUG = true;

	private ExpandableListView mListView;
	private ArtistAlbumListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (DEBUG) {
			Log.i(TAG, "onCreate");
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frame_artist_album, null, false);
		mListView = (ExpandableListView) view.findViewById(R.id.list);
		mAdapter = new ArtistAlbumListAdapter(this, getActivity(), null,
				R.layout.track_list_item_group, new String[] {}, new int[] {},
				R.layout.track_list_item_child, new String[] {}, new int[] {});
		mListView.setAdapter(mAdapter);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		getArtistCursor(mAdapter.getQueryHandler(), null);
	}

	@Override
	public void onPause() {
		if (DEBUG) {
			Log.i(TAG, "onPause");
		}
		super.onPause();
	}

	private Cursor getArtistCursor(AsyncQueryHandler async, String filter) {
		String[] cols = new String[] { MediaStore.Audio.Artists._ID,
				MediaStore.Audio.Artists.ARTIST,
				MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
				MediaStore.Audio.Artists.NUMBER_OF_TRACKS };
		Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
		if (!TextUtils.isEmpty(filter)) {
			uri = uri.buildUpon()
					.appendQueryParameter("filter", Uri.decode(filter)).build();
		}

		Cursor ret = null;
		if (async != null) {
			async.startQuery(0, null, uri, cols, null, null,
					MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
		} else {
			/* 同步查询 */
		}

		return ret;
	}

	private Cursor getAlbumCursor(AsyncQueryHandler async, String filter) {
		String[] cols = new String[] { MediaStore.Audio.Albums._ID,
				MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM,
				MediaStore.Audio.Albums.ALBUM_ART, };
		Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
		if (!TextUtils.isEmpty(filter)) {
			uri = uri.buildUpon()
					.appendQueryParameter("filter", Uri.decode(filter)).build();
		}

		Cursor ret = null;
		if (async != null) {
			async.startQuery(0, null, uri, cols, null, null,
					MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
		} else {
			/* 同步查询 */
		}

		return ret;
	}

	private void updateList(Cursor c) {

	}

	static class ArtistAlbumListAdapter extends SimpleCursorTreeAdapter {
		private static final String TAG = "ArtistAlbumListAdapter";
		private static final boolean DEBUG = true;

        private int mGroupArtistIdIdx;
        private int mGroupArtistIdx;
        private int mGroupAlbumIdx;
        private int mGroupSongIdx;

		private ArtistAlbumFragment mFragment;
		private QueryHandler mQueryHandler;

		static class ViewHolder {
			TextView line1;
			TextView line2;
			ImageView play_indicator;
			ImageView icon;
		}

		class QueryHandler extends AsyncQueryHandler {
			private static final String TAG = "QueryHandler";
			private static final boolean DEBUG = true;

			public QueryHandler(ContentResolver cr) {
				super(cr);
			}

			@Override
			protected void onQueryComplete(int token, Object cookie,
					Cursor cursor) {
				if (DEBUG) {
					Log.i(TAG, "onQueryComplete");
				}
				super.onQueryComplete(token, cookie, cursor);
				mFragment.init(cursor);
			}
		}

		public ArtistAlbumListAdapter(ArtistAlbumFragment fragment,
				Context context, Cursor cursor, int groupLayout,
				String[] groupFrom, int[] groupTo, int childLayout,
				String[] childFrom, int[] childTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo,
					childLayout, childFrom, childTo);
			mFragment = fragment;
			mQueryHandler = new QueryHandler(context.getContentResolver());
		}

		@Override
		protected Cursor getChildrenCursor(Cursor arg0) {
			return null;
		}

		@Override
		public View newGroupView(Context context, Cursor cursor,
				boolean isExpanded, ViewGroup parent) {
			View v = super.newGroupView(context, cursor, isExpanded, parent);
			ViewHolder vh = new ViewHolder();
			vh.line1 = (TextView) v.findViewById(R.id.line1);
			vh.line2 = (TextView) v.findViewById(R.id.line2);
			vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
			vh.icon = (ImageView) v.findViewById(R.id.icon);
			// vh.icon.setBackgroundDrawable(mDefaultAlbumIcon);
			vh.icon.setPadding(0, 0, 1, 0);
			v.setTag(vh);
			return v;
		}

		@Override
		public View newChildView(Context context, Cursor cursor,
				boolean isLastChild, ViewGroup parent) {
			return super.newChildView(context, cursor, isLastChild, parent);
		}

		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor,
				boolean isExpanded) {
			super.bindGroupView(view, context, cursor, isExpanded);
			ViewHolder vh = (ViewHolder) view.getTag();

            String artist = cursor.getString(mGroupArtistIdx);
            String displayartist = artist;
            boolean unknown = artist == null || artist.equals(MediaStore.UNKNOWN_STRING);
            if (unknown) {
                //displayartist = mUnknownArtist;
            }
            vh.line1.setText(displayartist);

            int numalbums = cursor.getInt(mGroupAlbumIdx);
            int numsongs = cursor.getInt(mGroupSongIdx);
            
            String songs_albums = MusicUtils.makeAlbumsLabel(context,
                    numalbums, numsongs, unknown);
            
            vh.line2.setText(songs_albums);
            
//            long currentartistid = MusicUtils.getCurrentArtistId();
//            long artistid = cursor.getLong(mGroupArtistIdIdx);
//            if (currentartistid == artistid && !isexpanded) {
//                vh.play_indicator.setImageDrawable(mNowPlayingOverlay);
//            } else {
//                vh.play_indicator.setImageDrawable(null);
//            }
		}

		@Override
		protected void bindChildView(View view, Context context, Cursor cursor,
				boolean isLastChild) {
			// TODO Auto-generated method stub
			super.bindChildView(view, context, cursor, isLastChild);
		}

		public AsyncQueryHandler getQueryHandler() {
			return mQueryHandler;
		}

		private void getColumnIndices(Cursor c) {
			if (c != null) {
                mGroupArtistIdIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
                mGroupArtistIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
                mGroupAlbumIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
                mGroupSongIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
			}
		}

		@Override
		public void changeCursor(Cursor cursor) {
			getColumnIndices(cursor);
			super.changeCursor(cursor);
		}
	}

	private void init(Cursor c) {
		if (mAdapter == null) {
			return;
		}

		mAdapter.changeCursor(c);
	}
}
