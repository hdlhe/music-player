package com.example.musicplayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class MusicUtils {

	private static StringBuilder sFormatBuilder = new StringBuilder();
	private static Formatter sFormatter = new Formatter(sFormatBuilder,
			Locale.getDefault());
	private static HashMap<Long, Drawable> sArtCache = new HashMap<Long, Drawable>();

	private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
	private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
	private static final Uri sArtworkUri = Uri
			.parse("content://media/external/audio/albumart");

	public static String makeAlbumsLabel(Context context, int numalbums,
			int numsongs, boolean isUnknown) {
		// There are two formats for the albums/songs information:
		// "N Song(s)" - used for unknown artist/album
		// "N Album(s)" - used for known albums

		StringBuilder songs_albums = new StringBuilder();

		Resources r = context.getResources();
		if (isUnknown) {
			if (numsongs == 1) {
				songs_albums.append(context.getString(R.string.onesong));
			} else {
				String f = r.getQuantityText(R.plurals.Nsongs, numsongs)
						.toString();
				sFormatBuilder.setLength(0);
				sFormatter.format(f, Integer.valueOf(numsongs));
				songs_albums.append(sFormatBuilder);
			}
		} else {
			String f = r.getQuantityText(R.plurals.Nalbums, numalbums)
					.toString();
			sFormatBuilder.setLength(0);
			sFormatter.format(f, Integer.valueOf(numalbums));
			songs_albums.append(sFormatBuilder);
			songs_albums.append(context.getString(R.string.albumsongseparator));
		}
		return songs_albums.toString();
	}

	public static Cursor query(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder,
			int limit) {
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver == null) {
				return null;
			}
			if (limit > 0) {
				uri = uri.buildUpon().appendQueryParameter("limit", "" + limit)
						.build();
			}
			return resolver.query(uri, projection, selection, selectionArgs,
					sortOrder);
		} catch (UnsupportedOperationException ex) {
			return null;
		}

	}

	public static Cursor query(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		return query(context, uri, projection, selection, selectionArgs,
				sortOrder, 0);
	}

	private static Bitmap getArtworkQuick(Context context, long album_id,
			int w, int h) {
		w -= 1;
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
		if (uri != null) {
			ParcelFileDescriptor fd = null;
			try {
				fd = res.openFileDescriptor(uri, "r");
				int sampleSize = 1;

				sBitmapOptionsCache.inJustDecodeBounds = true;
				BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(),
						null, sBitmapOptionsCache);
				int nextHeight = sBitmapOptionsCache.outHeight >> 1;
				int nextWidth = sBitmapOptionsCache.outWidth >> 1;
				while (nextWidth > w && nextHeight > h) {
					sampleSize <<= 1;
					nextHeight >>= 1;
					nextWidth >>= 1;
				}

				sBitmapOptionsCache.inJustDecodeBounds = false;
				sBitmapOptionsCache.inSampleSize = sampleSize;
				Bitmap b = BitmapFactory.decodeFileDescriptor(
						fd.getFileDescriptor(), null, sBitmapOptionsCache);
				if (b != null) {
					if ((sBitmapOptionsCache.outHeight != h)
							|| (sBitmapOptionsCache.outWidth != w)) {
						Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
						if (tmp != b) {
							b.recycle();
						}
						b = tmp;
					}
				}

				return b;

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (fd != null) {
					try {
						fd.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return null;
	}

	// A really simple BitmapDrawable-like class, that doesn't do
	// scaling, dithering or filtering.
	private static class FastBitmapDrawable extends Drawable {
		private Bitmap mBitmap;

		public FastBitmapDrawable(Bitmap b) {
			mBitmap = b;
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}

		@Override
		public int getOpacity() {
			return PixelFormat.OPAQUE;
		}

		@Override
		public void setAlpha(int alpha) {
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
		}
	}

	public static Drawable getCachedArtwork(Context context, long artIndex,
			BitmapDrawable defaultArtwork) {
		Drawable d = null;
		synchronized (sArtCache) {
			d = sArtCache.get(artIndex);
		}
		if (d == null) {
			d = defaultArtwork;
			final Bitmap icon = defaultArtwork.getBitmap();
			int w = icon.getWidth();
			int h = icon.getHeight();
			Bitmap b = MusicUtils.getArtworkQuick(context, artIndex, w, h);
			if (b != null) {
				d = new FastBitmapDrawable(b);
				synchronized (sArtCache) {
					// the cache may have changed since we checked
					Drawable value = sArtCache.get(artIndex);
					if (value == null) {
						sArtCache.put(artIndex, d);
					} else {
						d = value;
					}
				}
			}
		}
		return d;
	}
}
