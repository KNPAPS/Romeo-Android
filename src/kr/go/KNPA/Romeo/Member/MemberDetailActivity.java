package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberDetailActivity extends Activity implements OnClickListener {

	static final int NOT_SPECIFIED = -777;
	Button background;
	Button  close;
	Button favorite;
	Button goDocument;
	Button goSurvey;
	Button goCommand;
	Button goMeeting;
	public MemberDetailActivity() {
	}

 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		Intent intent = getIntent();
		// intent.getStringExtra("KEY");
		
		Bundle b = intent.getExtras();
		
		
		boolean fromFavorite = b.getBoolean("fromFavorite");
		boolean isGroup = false;
		if(fromFavorite == true) {
			isGroup = b.getBoolean("isGroup");	
		}
		
		String title = b.getString("title");
		long TS = b.getLong("TS");
		
		long[] idxs = b.getLongArray("idxs");
		long idx = b.getLong("idx");
		if(idxs == null) {
			idxs = new long[1];
			idxs[0] = idx;
		}
		
		if((idxs.length==1 && idxs[0] == 0L) || idx == NOT_SPECIFIED) {
			finish();
		} //else {
		
		// User 정보를 얻어온다.
		ArrayList<User> users = User.getUsersWithIndexes(idxs);
		//}
		
        //배경투명처리
		/*
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		 */

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		layoutParams.dimAmount = 0.7f;

		getWindow().setAttributes(layoutParams);
		
		setContentView(R.layout.member_detail_activity);

		/*
		 *  VERSION > 13
		 *  Point bound = new Point();
		 *  getWindowManager().getDefaultDisplay().getSize(bound);
		 *  width = bound.x;
		 *  height = bound.y;
		 */
		int statusBarHeight = (int) Math.ceil(25 * this.getResources().getDisplayMetrics().density);
		ViewGroup vg = (ViewGroup) findViewById(R.id.memberDetailActivityLayout);
		ViewGroup.LayoutParams lp = vg.getLayoutParams();
		lp.width = getWindowManager().getDefaultDisplay().getWidth();
		lp.height = getWindowManager().getDefaultDisplay().getHeight() - statusBarHeight;
		vg.setLayoutParams(lp);
//		getWindow().setAttributes(layoutParams);

		
		// Bind Click Events
		background = (Button)findViewById(R.id.backgroundButton);
		close = (Button)findViewById(R.id.close);
		favorite = (Button)findViewById(R.id.favorite);
		goMeeting = (Button)findViewById(R.id.goMeeting);
		goCommand = (Button)findViewById(R.id.goCommand);
		goDocument = (Button)findViewById(R.id.goDocument);
		goSurvey = (Button)findViewById(R.id.goSurvey);
		
		background.setOnClickListener(this);
		close.setOnClickListener(this);
		favorite.setOnClickListener(this);
		goMeeting.setOnClickListener(this);
		goCommand.setOnClickListener(this);
		goDocument.setOnClickListener(this);
		goSurvey.setOnClickListener(this);
		
		
		
		ImageView userPicIV = (ImageView)findViewById(R.id.user_pic);
		TextView departmentTV = (TextView)findViewById(R.id.department);
		TextView rankTV = (TextView)findViewById(R.id.rank);
		TextView nameTV = (TextView)findViewById(R.id.name);
		
		if(isGroup) {
			nameTV.setText(title);
		} else {
			User user = users.get(0); 
			departmentTV.setText(user.getDepartmentFull());
			rankTV.setText(User.RANK[user.rank]);
			nameTV.setText(user.name);
			// TODO : userPic Setting
		}
	}

	@Override
	public void onClick(View view) {
		final long TD = 300;
		Button btn = (Button)view;
		if(background == btn) {
			finish();
		} else if (close == btn) {
			finish();
		} else if (favorite == btn) {
			final android.widget.Toast t = android.widget.Toast.makeText(this, "Favorite", android.widget.Toast.LENGTH_SHORT);
			t.show();
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					t.cancel();
				}
			};
			timer.schedule(task, TD);
		} else if (goDocument == btn) {
			final android.widget.Toast t = android.widget.Toast.makeText(this, "goDocument", android.widget.Toast.LENGTH_SHORT);
			t.show();
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					t.cancel();
				}
			};
			timer.schedule(task, TD);
		} else if (goSurvey == btn) {
			final android.widget.Toast t = android.widget.Toast.makeText(this, "goSurvey", android.widget.Toast.LENGTH_SHORT);
			t.show();
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					t.cancel();
				}
			};
			timer.schedule(task, TD);
		} else if (goMeeting == btn) {
			final android.widget.Toast t = android.widget.Toast.makeText(this, "goMeeting", android.widget.Toast.LENGTH_SHORT);
			t.show();
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					t.cancel();
				}
			};
			timer.schedule(task, TD);
		} else if (goCommand == btn) {
			final android.widget.Toast t = android.widget.Toast.makeText(this, "goCommand", android.widget.Toast.LENGTH_SHORT);
			t.show();
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					t.cancel();
				}
			};
			timer.schedule(task, TD);
		}
	}
 
}
