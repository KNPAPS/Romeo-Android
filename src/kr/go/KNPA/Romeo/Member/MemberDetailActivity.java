package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Chat.Room;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.MemberProcManager;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberDetailActivity extends Activity {

	static final int NOT_SPECIFIED = -777;
	static final String KEY_IDX = "idx";
	static final String KEY_IDX_TYPE = "idx_type";
	static final int IDX_TYPE_USER = 0; 
	static final int IDX_TYPE_GROUP = 1;
	private Handler mHandler; 
	private Button background;
	private Button  close;
	private Button favorite;
	private Button goDocument;
	private Button goSurvey;
	private Button goCommand;
	private Button goMeeting;
	
	private String idx;
	private int idxType;
	
	public MemberDetailActivity() {
	}

 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		// Intent로부터 정보를 얻자
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		
		idx = b.getString(KEY_IDX);
		idxType = b.getInt(KEY_IDX_TYPE);
		
		// 전달된 idx값이 없으면 돌아간다.
		if(idx == null|| idx.trim().length() < 1) {
			finish();
		}

		// 모양을 잡는다.
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		layoutParams.dimAmount = 0.7f;

		getWindow().setAttributes(layoutParams);
		setContentView(R.layout.member_detail_activity);

		int statusBarHeight = (int) Math.ceil(25 * this.getResources().getDisplayMetrics().density);
		ViewGroup vg = (ViewGroup) findViewById(R.id.memberDetailActivityLayout);
		ViewGroup.LayoutParams lp = vg.getLayoutParams();
		lp.width = getWindowManager().getDefaultDisplay().getWidth();
		lp.height = getWindowManager().getDefaultDisplay().getHeight() - statusBarHeight;
		vg.setLayoutParams(lp);

		// 주어진 idx가 즐겨찾기 되어있는지 판단한다.
		MemberProcManager mpm = DBProcManager.sharedManager(MemberDetailActivity.this).member(); 
		boolean isFavorite = mpm.isUserFavorite(idx);
		
		favorite = (Button)findViewById(R.id.favorite);
		if(isFavorite) {
			favorite.setBackgroundResource(R.drawable.star_active);
		} else {
			favorite.setBackgroundResource(R.drawable.star_gray);
		}

		// 주어진 정보를 토대로 채워넣는다.
		TextView departmentTV = (TextView)findViewById(R.id.department);
		TextView rankTV = (TextView)findViewById(R.id.rank);
		TextView nameTV = (TextView)findViewById(R.id.name);
				
		Cursor cursor_favoriteInfo = mpm.getFavoriteInfo(idx);
		String title = null;
		if ( cursor_favoriteInfo.moveToNext() ) {
			title = cursor_favoriteInfo.getString(cursor_favoriteInfo.getColumnIndex(MemberProcManager.COLUMN_FAVORITE_NAME));
		}
		if(idxType == IDX_TYPE_USER) {
			// User 정보를 얻어온다.
			User user = User.getUserWithIdx(idx); 
			
			departmentTV.setText(user.department.nameFull);
			rankTV.setText(User.RANK[user.rank]);
			nameTV.setText(user.name);
			
			ImageView userPicIV = (ImageView)findViewById(R.id.user_pic);
			new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_SMALL, idx, userPicIV);
		} else if(idxType == IDX_TYPE_GROUP) {
			
			if( title == null || title.trim().length() == 0) {
				title = "";
				Cursor cursor_favoriteUsers = mpm.getFavoriteGroupMemberList(idx);
				while(cursor_favoriteUsers.moveToNext()) {
					User user = User.getUserWithIdx(cursor_favoriteUsers.getString(cursor_favoriteUsers.getColumnIndex(MemberProcManager.COLUMN_USER_IDX)));  
					title += user.rank + " " + user.name;
					if(title.length() > 20 ) {
						title = title.substring(0, 20) + "...";
						break;
					}
				}
			}
			departmentTV.setText("");
			rankTV.setText("");
			nameTV.setText(title);
			ImageView userPicIV = (ImageView)findViewById(R.id.user_pic);
			userPicIV.setImageResource(R.drawable.user_pic_default);	
		}
		
		

		// Bind Click Events
		background = (Button)findViewById(R.id.backgroundButton);
		close = (Button)findViewById(R.id.close);
		
		goMeeting = (Button)findViewById(R.id.goMeeting);
		goCommand = (Button)findViewById(R.id.goCommand);
		//goDocument = (Button)findViewById(R.id.goDocument);
		//goSurvey = (Button)findViewById(R.id.goSurvey);
		
		// TODO
		background.setOnClickListener(finish);
		close.setOnClickListener(finish);
		
		favorite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MemberProcManager mpm = DBProcManager.sharedManager(MemberDetailActivity.this).member();
				boolean isFavorite = mpm.isUserFavorite(idx);
				mpm.setFavorite(idx, !isFavorite);
			}
		});
		
		goMeeting.setOnClickListener(goMessage);
		goCommand.setOnClickListener(goMessage);
		//goDocument.setOnClickListener(goMessage);
		//goSurvey.setOnClickListener(goMessage);
	}

 	private final OnClickListener finish = new OnClickListener() {
		@Override
		public void onClick(View v) {	finish();	}
	};
	
	private final OnClickListener goMessage = new OnClickListener() {
		
		@Override
		public void onClick(final View btn) {	
			new Thread(){
				public void run() {
					super.run();
					final int roomType = btn==goCommand ? Chat.TYPE_COMMAND : Chat.TYPE_MEETING;
					
					String roomCode = Room.find(getApplicationContext(), roomType, idx);
					Room room = null;
					if ( roomCode != null ) {
						room = new Room(getApplicationContext(), roomCode);
					} else {
						ArrayList<String> chatters = new ArrayList<String>();
						chatters.add( idx );
						chatters.add( UserInfo.getUserIdx(getApplicationContext()) );
						room = new Room(getApplicationContext(),roomType,chatters);
					}

					final Room fRoom = room;
					
					mHandler.post(new Runnable(){
						public void run() {
							MainActivity.sharedActivity().goRoomFragment(roomType, fRoom);
							
						};
					});
					
				};
			}.start();
			
			
		}
	};
 
}
