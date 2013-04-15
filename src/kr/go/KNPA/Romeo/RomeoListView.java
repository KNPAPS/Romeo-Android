package kr.go.KNPA.Romeo;

import java.lang.ref.WeakReference;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.RoomFragment;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.widget.ListView;

public abstract class RomeoListView extends ListView {

	// Database
	protected SQLiteDatabase db;
	
	// Adapter
	public CursorAdapter listAdapter;
	protected ListHandler mHandler;
	
	// Variables
	public int type = Message.NOT_SPECIFIED;
	
	// Constructor
	public RomeoListView(Context context) {
		super(context);
	}

	public RomeoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RomeoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// Initializer
	abstract public RomeoListView initWithType(int type);
	
	/*// Context 
	protected Context getContext() {
		return this.context;
	}
	*/	

	// DB에 쿼리를 날린다. 추상메소드.
	abstract protected Cursor query();
	
	// 리스트를 다시 불러온다.
	public void refresh() {
//		if(listAdapter == null) return;
// 
//		if(listAdapter instanceof CursorAdapter) {
//			// TODO : DB Loading
//			Cursor c = query();
//			
//			setListBackground(c);
//			if(c != null) {
//				listAdapter.changeCursor(c);
//			}
//		} else {
//			listAdapter.notifyDataSetChanged();
//		}
//		
//		if(this.getAdapter() instanceof SimpleSectionAdapter && this.getAdapter() != listAdapter)
//			((SimpleSectionAdapter)this.getAdapter()).notifyDataSetChanged();
		onPreExecute();
		
		if ( mHandler == null ) {
			mHandler = new ListHandler(this);
		}
		
		Thread thread = new Thread(){
			@Override
			public void run() {
				Cursor c = query();
				android.os.Message msg = mHandler.obtainMessage();
				msg.obj = c;
				msg.arg1 = (c!=null)? 1 : 0;
				mHandler.sendMessage(msg);
			}
		};
		thread.start();
	}
	
	abstract public void onPreExecute();
	
	abstract public void onPostExecute( boolean isValidCursor );
	
	// 커서로부터 행의 갯수를 세어 비었을 경우 빈 배경을 출력해준다.
	public void setListBackground(Cursor c) {
		if(c == null || c.getCount() == 0) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			options.inPreferredConfig = Config.RGB_565;
			
			Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.empty_set_background, options);
			int height = src.getHeight();
			int width = src.getWidth();
			Bitmap resized = Bitmap.createScaledBitmap(src, width/options.inSampleSize, height/options.inSampleSize, true);
			this.setBackgroundDrawable(new BitmapDrawable(getResources(), resized));
		} else {
			this.setBackgroundResource(R.color.light);
		}
	}

	public static class ListHandler extends Handler {
		private final WeakReference<RomeoListView> mReference;
		public ListHandler(RomeoListView listView) {
			this.mReference = new WeakReference<RomeoListView>(listView);
		}
		
		@Override
		public void handleMessage(android.os.Message msg) {
			RomeoListView listView = mReference.get();
			
			if ( listView != null ) {
				Cursor c = (Cursor)msg.obj;
				if(listView.listAdapter == null) return;
					 
				if(listView.listAdapter instanceof CursorAdapter) {
					listView.setListBackground( c );
					if(c != null) {
						listView.listAdapter.changeCursor(c);
					}
				} else {
					listView.listAdapter.notifyDataSetChanged();
				}
				
				if(listView.getAdapter() instanceof SimpleSectionAdapter && listView.getAdapter() != listView.listAdapter)
					((SimpleSectionAdapter)listView.getAdapter()).notifyDataSetChanged();
			}
			
			listView.onPostExecute( msg.arg1==1?true:false );
			super.handleMessage(msg);
		}
	}
	
/*	/// abstract
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		// NOTHING HERE
	}
	*/
	
}
