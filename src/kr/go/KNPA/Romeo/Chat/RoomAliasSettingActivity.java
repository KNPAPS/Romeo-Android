package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RoomAliasSettingActivity extends Activity {
	public static final int		REQUEST_CODE		= 237;
	public static final String	KEY_ROOM_CODE		= "roomCode";
	public static final String	KEY_ROOM_PREV_ALIAS	= "prevAlias";
	public static final String	KEY_ROOM_TITLE		= "title";

	public static final String	KEY_NEW_ALIAS		= "newAlias";
	private RoomModel			mModel;
	private static Handler		mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mHandler = new Handler();

		setContentView(R.layout.activity_room_alias_setting);

		((TextView) findViewById(R.id.title)).setText(R.string.title_activity_room_title_setting);

		Button lbb = ((Button) findViewById(R.id.left_bar_button));
		lbb.setText(R.string.cancel);
		lbb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		((Button) findViewById(R.id.right_bar_button)).setVisibility(View.INVISIBLE);

		Bundle b = getIntent().getExtras();
		final String roomCode = b.getString(KEY_ROOM_CODE);
		if (roomCode == null)
		{
			setResult(RESULT_CANCELED);
			finish();
		}

		final Button submitBtn = (Button) findViewById(R.id.room_alias_submit);
		final EditText aliasET = (EditText) findViewById(R.id.room_alias);
		final TextView chatterTV = (TextView) findViewById(R.id.chatterListTV);

		new Thread() {
			public void run()
			{
				super.run();
				mModel = new RoomModel(RoomAliasSettingActivity.this, new Room(roomCode));
				mModel.init();
				final String roomTitle = mModel.getRoom().getTitle();
				final String prevAlias = mModel.getRoom().getAlias() == null || mModel.getRoom().getAlias().trim().equals("") ? roomTitle : mModel.getRoom().getAlias();

				ArrayList<Chatter> chatters = mModel.getRoom().chatters;

				String chatterNames = "";

				for (int i = 0; i < chatters.size(); i++)
				{
					chatterNames += Constants.POLICE_RANK[chatters.get(i).rank] + " " + chatters.get(i).name + "님, ";
				}

				final String chatterText = chatterNames.substring(0, chatterNames.length() - 2) + "이 대화에 참여하고 있습니다.";

				mHandler.post(new Runnable() {
					public void run()
					{
						chatterTV.setText(chatterText);
						aliasET.setHint(roomTitle);
						aliasET.setText(prevAlias);
						aliasET.setSelection(prevAlias.length());
						submitBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v)
							{
								String newAlias = aliasET.getText().toString();

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
				});

			}
		}.start();
	}
}
