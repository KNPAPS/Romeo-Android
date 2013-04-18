package kr.go.KNPA.Romeo.Chat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ChatFragment로 진입하면 보게되는 ListView이다. 방 목록을 포함하고 있다.
 */
public class RoomListView extends RomeoListView implements OnItemClickListener {

	private RoomListHandler roomListHandler;
	private Context mContext;
	/** 
	 * @name Constructors
	 * @{
	 */
	public RoomListView(Context context){
		this(context, null);
		mContext = context;
	}
	public RoomListView(Context context, AttributeSet attrs){	
		this(context, attrs, 0);
		mContext = context;
	}
	public RoomListView(Context context, AttributeSet attrs, int defStyle){	
		super(context, attrs, defStyle);
		mContext = context;
	}
	/** @} */
	
	/** 
	 * @name Database Managemant
	 * *{
	 */
	@Override
	protected Cursor query() {
		return DBProcManager.sharedManager(getContext()).chat().getRoomList(this.subType);	
	}
	/** @} */
	
	/**
	 * @name initialize
	 * @{
	 */
	@Override
	public RoomListView initWithType (int subType) {
		roomListHandler = new RoomListHandler(this);
		
		this.subType = subType;
		
		Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
			@Override
			public String getSectionTitleForItem(Cursor c) {	// TODO
				boolean checked = (c.getInt(c.getColumnIndex( DBProcManager.ChatProcManager.COLUMN_ROOM_NUM_NEW_CHAT )) > 0 ? false : true );
				return (checked ? getContext().getString(R.string.checkedChat)  : getContext().getString(R.string.unCheckedChat));
			}
		};
		
		listAdapter = new RoomListAdapter(getContext(), null, false, this.subType);
		SimpleSectionAdapter<Cursor> sectionAdapter
			= new SimpleSectionAdapter<Cursor>(getContext(), listAdapter, R.layout.section_header, R.id.cell_title, sectionizer);
		this.setAdapter(sectionAdapter);
		this.setOnItemClickListener(this);
		
		return this;
	}
	/** @} */
	
	/**
	 * @name Click Listener
	 *  @{
	 */
	
	/**
	 * 해당 방으로 입장
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long l_position) {

		new Thread() {
			@SuppressWarnings("rawtypes")
			public void run() {
				Message startMsg = roomListHandler.obtainMessage();
				startMsg.what = RoomListHandler.SHOW_WAITER_DIALOG;
				roomListHandler.sendMessage(startMsg);
				
				// 추가 정보
				ListAdapter adapter = listAdapter;
				if(getAdapter() instanceof SimpleSectionAdapter)
					adapter= ((SimpleSectionAdapter)getAdapter());
				Cursor c = (Cursor)adapter.getItem(position);
				
				String roomCode = c.getString(c.getColumnIndex(DBProcManager.ChatProcManager.COLUMN_ROOM_IDX));
				
				Room room = new Room(getContext(), roomCode);

				Message endMsg = roomListHandler.obtainMessage();
				endMsg.what = RoomListHandler.DISMISS_WAITER_DIALOG;
				roomListHandler.sendMessage(endMsg);
				
				Message msg = roomListHandler.obtainMessage();
				msg.what = RoomListHandler.ENTER_ROOM;
				msg.obj = room;
				roomListHandler.sendMessage(msg);
			};
		}.start();
	}
	
	/** @} */
	@Override
	public void onPreExecute() {
	}
	@Override
	public void onPostExecute(boolean isValidCursor) {
	}

	
	private static class RoomListHandler extends Handler {
		
		private final WeakReference<RoomListView> mReference;
			public RoomListHandler(RoomListView RoomListView) {
			this.mReference = new WeakReference<RoomListView>(RoomListView);
		}
		
		private static final int ENTER_ROOM = 1;
		private static final int SHOW_WAITER_DIALOG = 2;
		private static final int DISMISS_WAITER_DIALOG = 3;
		@Override
		public void handleMessage(Message msg) {
			RoomListView roomListView = mReference.get();
			if ( roomListView != null ) {
				
				switch( msg.what ) {
				case ENTER_ROOM:
					RoomFragment fragment = new RoomFragment((Room)msg.obj);
					MainActivity.sharedActivity().pushContent(fragment);
					break;
				case SHOW_WAITER_DIALOG:
					WaiterView.showDialog( MainActivity.sharedActivity() );
					break;
				case DISMISS_WAITER_DIALOG:
					WaiterView.dismissDialog( MainActivity.sharedActivity() );
					break;
				}
				
			}
			super.handleMessage(msg);
		}
	}

}
