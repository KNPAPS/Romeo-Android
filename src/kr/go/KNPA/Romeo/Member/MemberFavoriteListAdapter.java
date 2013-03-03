package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.CollectionFactory;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

//Inner Class Favorite Member Adapter //
public class MemberFavoriteListAdapter extends CursorAdapter implements OnItemClickListener, OnClickListener {
	
	private Context context;
	public int type = User.NOT_SPECIFIED;
	
	ArrayList<HashMap<String,String>> search;
	
	public MemberFavoriteListAdapter(Context ctx, int type, Cursor c, boolean autoRequery) {
		super(ctx, c, autoRequery);
		this.type = type;
		context = ctx;
		if(type == User.TYPE_FAVORITE_SEARCH)
			search = new ArrayList<HashMap<String,String>>();
	}
	@Override
	public View newView(Context ctx, Cursor c, ViewGroup parent) {
		String idxs = c.getString(c.getColumnIndex("idxs"));
		long TS = c.getLong(c.getColumnIndex("TS"));
		String title = c.getString(c.getColumnIndex("title"));
		boolean isGroup = c.getInt(c.getColumnIndex("isGroup")) == 1 ? true : false;
		
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View v = null;
		if(this.type == User.TYPE_FAVORITE) {
			if(isGroup) { 
				v = inflater.inflate(R.layout.member_favorite_group_cell, parent,false);
				v.setTag("GROUP");
			} else {
				v = inflater.inflate(R.layout.member_favorite_user_cell, parent,false);
				v.setTag("USER");
			}
		} else if(this.type == User.TYPE_FAVORITE_SEARCH) {
			if(isGroup) { 
				v = inflater.inflate(R.layout.member_favorite_group_cell_search, parent,false);
				v.setTag("GROUP");
			} else {
				v = inflater.inflate(R.layout.member_favorite_user_cell_search, parent,false);
				v.setTag("USER");
			}
		}
		return v; 
	}
	
	@Override
	public void bindView(View v, Context ctx, Cursor c) {
		String idxs = c.getString(c.getColumnIndex("idxs"));
		long TS = c.getLong(c.getColumnIndex("TS"));
		String title = c.getString(c.getColumnIndex("title"));
		boolean isGroup = c.getInt(c.getColumnIndex("isGroup")) == 1 ? true : false;
		
		ImageView userPicIV= (ImageView)v.findViewById(R.id.userPic);
		TextView departmentTV= (TextView)v.findViewById(R.id.department);
		TextView rankTV= (TextView)v.findViewById(R.id.rank);
		TextView nameTV= (TextView)v.findViewById(R.id.name);
		TextView roleTV= (TextView)v.findViewById(R.id.role);
		Button goDetail = null;
		
		String department = "";
		String rank = "";
		String name = "";
		String role = "";
		
		departmentTV.setText(department);
		rankTV.setText(rank);
		nameTV.setText(name);
		roleTV.setText(role);
		
		if(isGroup && type==User.TYPE_FAVORITE) {
			goDetail = (Button)v.findViewById(R.id.goDetail);

			goDetail.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO
				}
			});
		}
		
		if(type == User.TYPE_FAVORITE_SEARCH) {
			Button searchButton = (Button)v.findViewById(R.id.control);
			searchButton.setOnClickListener(this);
			String tag = null;
			if(isGroup) {
				tag = "GROUP";
			} else {
				tag = "USER";
			}
			searchButton.setTag(tag + ":NOTSET");
			
		}

	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long l_position) {
		Intent intent = new Intent(context, MemberDetailActivity.class);
		Cursor c = (Cursor)getItem(position);
		String idxs = c.getString(c.getColumnIndex("idxs"));
		String[] _idxs = idxs.split(":");
		long[] indexes = new long[_idxs.length];
		
		for(int i=0; i<_idxs.length; i++) {
			indexes[i] = Long.parseLong(_idxs[i]);
		}
		
		String title = c.getString(c.getColumnIndex("title"));
		long TS = c.getLong(c.getColumnIndex("TS"));
		boolean isGroup = (c.getInt(c.getColumnIndex("isGroup")) == 1 ? true : false);
		
		Bundle b = new Bundle();
		b.putBoolean("isGroup", isGroup);
		b.putLong("TS", TS);
		b.putString("title", title);
		b.putLongArray("idxs", indexes);
		b.putBoolean("fromFavorite", true);
		intent.putExtras(b);		
		
		context.startActivity(intent);
		
	}
	@Override
	public void onClick(View v) {		
		String tag = (String)v.getTag();
		String[] tags = tag.split(":");
		boolean isGroup = (tags[0]).equalsIgnoreCase("GROUP") ? true : false;
		boolean isSet = (tags[1].equalsIgnoreCase("SET")?true:false);
		
		if(isGroup) {
			if(isSet) {
				
			} else {
				//search.add(CollectionFactory.hashMapWithKeysAndStrings("GROUP",))
			}
		} else {
			if(isSet) {
				
			} else {
				
			}
		}
		
		
		tag = tags[0]+":"+tags[1];
		v.setTag(tag);
	}
	
	
}
