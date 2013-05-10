package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Util.ImageManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserListAdapter extends BaseAdapter {

	private ArrayList<User> mArray;
	private Context mContext;
	
	public UserListAdapter( Context context ) {
		this.mContext = context;
		this.mArray = new ArrayList<User>(); 
	}
	
	public UserListAdapter( Context context, ArrayList<User> users ) {
		this.mContext = context; 
		this.mArray = users;
	}
	
	@Override
	public int getCount() {
		return mArray.size();
	}

	@Override
	public Object getItem(int position) {
		if ( position >= mArray.size() || position < 0 ) {
			return mArray.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if ( position>=mArray.size() || position <0 ) {
			throw new IllegalStateException("couldn't access to index " + position);
		}
		
		LayoutInflater inflater = LayoutInflater.from(mContext);

		View v = null;
		TextView departmentTV = null;
		TextView nameTV = null;
		TextView rankTV = null;
		TextView roleTV = null;
		ImageView userPIC = null;
		if ( convertView == null ) {
			v = inflater.inflate(R.layout.user_list_cell, parent, false);
			
		} else {
			v = convertView;			
		}
		
		userPIC = (ImageView)v.findViewById(R.id.user_pic);
		departmentTV = (TextView)v.findViewById(R.id.department);
		nameTV = (TextView)v.findViewById(R.id.name);
		rankTV = (TextView)v.findViewById(R.id.rank);
		roleTV = (TextView)v.findViewById(R.id.role);
		
		ImageManager im = new ImageManager();
		im.loadToImageView(ImageManager.PROFILE_SIZE_SMALL, mArray.get(position).idx, userPIC);
		departmentTV.setText(mArray.get(position).department.nameFull);
		nameTV.setText(mArray.get(position).name);
		rankTV.setText(Constants.POLICE_RANK[mArray.get(position).rank]);
		roleTV.setText(mArray.get(position).role);
		userPIC.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putString(MemberDetailActivity.KEY_IDX, mArray.get(position).idx);
				b.putInt(MemberDetailActivity.KEY_IDX_TYPE, MemberDetailActivity.IDX_TYPE_USER );
				Intent intent = new Intent(mContext, MemberDetailActivity.class);
				intent.putExtras(b);
				mContext.startActivity(intent);
			}
		});
		return v;
	}


}
