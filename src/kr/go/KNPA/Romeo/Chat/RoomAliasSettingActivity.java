package kr.go.KNPA.Romeo.Chat;

import kr.go.KNPA.Romeo.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RoomAliasSettingActivity extends Activity {
	public static final int REQUEST_CODE = 237;
	public static final String KEY_ROOM_CODE = "roomCode";
	public static final String KEY_ROOM_PREV_ALIAS = "prevAlias";
	public static final String KEY_ROOM_TITLE = "title";

	public static final String KEY_NEW_ALIAS = "newAlias";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_alias_setting);
		
		( (TextView)findViewById(R.id.title) ).setText(R.string.title_activity_room_title_setting);
		
		Button lbb = ( (Button)findViewById(R.id.left_bar_button) );
		lbb.setText(R.string.cancel);
		lbb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		( (Button)findViewById(R.id.right_bar_button) ).setVisibility(View.INVISIBLE);
		
		Bundle b = getIntent().getExtras();
		final String roomCode = b.getString(KEY_ROOM_CODE);
		if (roomCode == null)
		{
			setResult(RESULT_CANCELED);
			finish();
		}
		
		String roomTitle = b.getString(KEY_ROOM_TITLE);
		String roomPrevAlias = b.getString(KEY_ROOM_PREV_ALIAS);
		
		Button submitBtn = (Button)findViewById(R.id.room_alias_submit);
		EditText aliasET = (EditText)findViewById(R.id.room_alias);
		
		aliasET.setHint(roomTitle);
		aliasET.setText(roomPrevAlias);
		final String newAlias = aliasET.getText().toString();
		submitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				Bundle b = new Bundle();
				b.putString(KEY_NEW_ALIAS, newAlias);
				b.putString(KEY_ROOM_CODE, roomCode);
				
				Intent intent = new Intent();
				intent.putExtras(b);
				
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
}
