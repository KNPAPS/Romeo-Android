package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Chat.ChatFragment.ChatFragmentHandler;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import kr.go.KNPA.Romeo.Settings.SettingsCellMaker;
import kr.go.KNPA.Romeo.Util.UserInfo;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class RoomSettingActivity extends Activity {
	public static final int REQUEST_CODE = 101;
	public static final String KEY_ACTION = "action";
	public static final String KEY_IDXS = "idxs";
	
	ListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.settings_fragment, null);
		ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.activity_room_setting, null);
		view.addView(scrollView);
		LinearLayout root = (LinearLayout) scrollView.findViewById(R.id.roomSettingScrollViewLinearLayout);
		initNavigationBar(
				view, 
				getString(R.string.settingsTitle), 
				true, 
				false, 
				getString(R.string.cancel), 
				null,
				new OnClickListener() {	
					@Override	
					public void onClick(View v) {
						finish();	
					}	
				}, 
				null);
		
		// 알림 //
		final RelativeLayout hBasic 	= SettingsCellMaker.makeSectionHeader(inflater, view, "기본 설정");
		root.addView(hBasic);
		
		final RelativeLayout cWillNoti 	= SettingsCellMaker.makeCell(inflater, view, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_CHECKBOX);
		root.addView(cWillNoti);
		root.addView(SettingsCellMaker.makeListCellDivider(this, root));
		
		final RelativeLayout cLeaveRoom 	= SettingsCellMaker.makeCell(inflater, view, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_NONE);
		root.addView(cLeaveRoom);
		root.addView(SettingsCellMaker.makeListCellDivider(this, root));
		
		final RelativeLayout cInviteUser 	= SettingsCellMaker.makeCell(inflater, view, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_BUTTON);
		root.addView(cInviteUser);
	
		final RelativeLayout cUserList = SettingsCellMaker.makeCell(inflater, view, SettingsCellMaker.ONE_LINE, SettingsCellMaker.CONTROL_BUTTON);
		root.addView(cUserList );

		SettingsCellMaker.setTitle(cWillNoti, "알림");
		SettingsCellMaker.setOnCheckedChangeListener(
				SettingsCellMaker.getCheckBox(cWillNoti),
				new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
					}
				});
		
		SettingsCellMaker.setTitle(cLeaveRoom, "채팅방 나가기");
		
		cLeaveRoom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putInt(KEY_ACTION, RoomFragment.ACTION_LEAVE_ROOM);
				
				Intent intent = new Intent();
				intent.putExtras(b);
				
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		
		SettingsCellMaker.setTitle(cInviteUser, "그룹대화 초대하기");
		cInviteUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RoomSettingActivity.this, MemberSearch.class);
				startActivityForResult(intent, MemberSearch.REQUEST_CODE);
			}
		});
		
		SettingsCellMaker.setTitle(cUserList, "그룹대화 참여자 목록");
		
		setContentView(view);
	}
	
	protected void initNavigationBar(View parentView, String titleText, boolean lbbVisible, boolean rbbVisible, String lbbTitle, String rbbTitle, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
		
		Button lbb = (Button)parentView.findViewById(R.id.left_bar_button);
		Button rbb = (Button)parentView.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbTitle);	}
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbTitle);	}
		
		TextView titleView = (TextView)parentView.findViewById(R.id.title);
		titleView.setText(titleText);
		
		if(lbb.getVisibility() == View.VISIBLE) lbb.setOnClickListener(lbbOnClickListener);
		if(rbb.getVisibility() == View.VISIBLE) rbb.setOnClickListener(rbbOnClickListener);
	}
	
	//채팅방 초대 시 MemberSearchActivity를 실행하여 그 결과를 받아옴
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MemberSearch.REQUEST_CODE) {
			if(resultCode == MemberSearch.RESULT_OK) {
				final ArrayList<String> receiversIdxs = data.getExtras().getStringArrayList(MemberSearch.KEY_RESULT_USERS_IDX);
				
				if ( receiversIdxs.size() == 0 ) {
					return;
				}
				
				Bundle b = new Bundle();
				b.putInt(KEY_ACTION, RoomFragment.ACTION_JOIN_ROOM);
				
				b.putStringArrayList(KEY_IDXS, receiversIdxs);
				Intent intent = new Intent();
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();

			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
