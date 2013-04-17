package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class UserListActivity extends Activity {

	public static final String KEY_USERS_IDX = "idxs";
	public static final String KEY_TITLE = "title";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.user_list_activity, null);
		setContentView(view);
		
		
		ListView lv = (ListView)view.findViewById(R.id.listView);
		ArrayList<String> userIdxs = getIntent().getExtras().getStringArrayList(KEY_USERS_IDX);
		String title = getIntent().getExtras().getString(KEY_TITLE);
		ArrayList<User> users = User.getUsersWithIdxs(userIdxs);
		UserListAdapter adapter = new UserListAdapter(this, users);
		lv.setAdapter(adapter);
		
		Button lbb = (Button)view.findViewById(R.id.left_bar_button);
		Button rbb = (Button)view.findViewById(R.id.right_bar_button);
		
		rbb.setVisibility(View.INVISIBLE);
		lbb.setText("취소");
		
		TextView titleView = (TextView)view.findViewById(R.id.title);
		titleView.setText(title);
		
		lbb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
