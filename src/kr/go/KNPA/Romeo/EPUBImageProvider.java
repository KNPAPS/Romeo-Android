package kr.go.KNPA.Romeo;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class EPUBImageProvider extends ContentProvider {

	//public static final String AUTHORITY = "kr.go.KNPA.Romeo.test";
	//public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		
		URI fileURI = URI.create( "file://" + uri.getPath() );
		File file = new File( fileURI );

		ParcelFileDescriptor parcel = null;
		try {
		parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		} catch (FileNotFoundException e) {
		log("Error finding: " + fileURI + "\n" + e.toString() );
		}

			return parcel;
	}
	
	private void log(String str) {
		Log.d("PROVIDER", str);
	}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
