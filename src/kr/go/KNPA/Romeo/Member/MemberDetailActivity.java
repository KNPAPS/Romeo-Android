package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DBManager;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.DB.DBProcManager.MemberProcManager;
import kr.go.KNPA.Romeo.Util.ImageManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
	static final String KEY_IDX = "idx";
	static final String KEY_IDX_TYPE = "idx_type";
	static final int IDX_TYPE_USER = 0; 
	static final int IDX_TYPE_GROUP = 1;
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
		Bundle b = intent.getExtras();
		
		String idx = b.getString(KEY_IDX);
		int idxType = b.getInt(KEY_IDX_TYPE);
		
		if(idx == null|| idx.trim().length() < 1) {
			finish();
		}

		if(idxType == IDX_TYPE_USER) {
			
		} else if(idxType == IDX_TYPE_GROUP) {
			
		}
		
		// User 정보를 얻어온다.
		ArrayList<User> users = User.getUsersWithIdxs(idx);


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

		
		// Bind Click Events
		background = (Button)findViewById(R.id.backgroundButton);
		close = (Button)findViewById(R.id.close);
		
		String title = null;

		// TODO : 
		MemberProcManager mpm = DBProcManager.sharedManager(MemberDetailActivity.this).member(); 
		boolean isFavorite = mpm.isUserFavorite(idx);
		
		favorite = (Button)findViewById(R.id.favorite);
		if(isFavorite) {
			favorite.setBackgroundResource(R.drawable.star_active);
		} else {
			favorite.setBackgroundResource(R.drawable.star_gray);
		}
		
		goMeeting = (Button)findViewById(R.id.goMeeting);
		goCommand = (Button)findViewById(R.id.goCommand);
		goDocument = (Button)findViewById(R.id.goDocument);
		goSurvey = (Button)findViewById(R.id.goSurvey);
		
		// TODO
		background.setOnClickListener(this);
		close.setOnClickListener(this);
		favorite.setOnClickListener(this);
		goMeeting.setOnClickListener(this);
		goCommand.setOnClickListener(this);
		goDocument.setOnClickListener(this);
		goSurvey.setOnClickListener(this);
		
		
		
		ImageView userPicIV = (ImageView)findViewById(R.id.user_pic);
		new ImageManager().loadProfileImgToImageView(idx, ImageManager.PROFILE_IMG_SIZE_MEDIUM, userPicIV);
		
		TextView departmentTV = (TextView)findViewById(R.id.department);
		TextView rankTV = (TextView)findViewById(R.id.rank);
		TextView nameTV = (TextView)findViewById(R.id.name);
		
		boolean isGroup = (_idxs.length > 1)? true : false;
		if(isGroup) {
			nameTV.setText(title);
		} else {
			User user = users.get(0); 
			departmentTV.setText(user.department.nameFull);
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
			DBManager dbManager = new DBManager(MemberDetailActivity.this);
			SQLiteDatabase db = dbManager.getReadableDatabase();
			
			Bundle b = (Bundle)view.getTag();
			String idxs = b.getString("idxs");
			String _idxs[] = idxs.split(":");
			boolean isGroup = (_idxs.length > 1)? true : false;
			
			Cursor c = null;
			String sql = "SELECT * FROM "+DBManager.TABLE_MEMBER_FAVORITE+" WHERE idxs=\""+idxs+"\";";
			c = db.rawQuery(sql, null);
			
			boolean isFavorite = (c.getCount() >0 ? true :false );
			
			if(isFavorite) {
				db.delete(DBManager.TABLE_MEMBER_FAVORITE, "idxs=?", new String[]{idxs});
				btn.setBackgroundResource(R.drawable.star_gray);
			} else {
				long currentTS = System.currentTimeMillis();
				ContentValues vals = new ContentValues();
				vals.put("TS", currentTS);
				vals.put("isGroup", isGroup);
				vals.put("idxs", idxs);
				db.insert(DBManager.TABLE_MEMBER_FAVORITE, null, vals);
				btn.setBackgroundResource(R.drawable.star_active);
			}
			
			if(c != null) c.close();
			db.close();
			dbManager.close();
			
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
