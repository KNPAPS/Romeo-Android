package kr.go.KNPA.Romeo.search;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Constants;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.ImageManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberSearchTextViewAdapter extends ArrayAdapter<User> implements Filterable {
	private ArrayList<User>		mFullData;
	private ArrayList<User>		mData;
	private ArrayList<String>	mExcludeIdxs;

	public MemberSearchTextViewAdapter(Context context, ArrayList<User> users)
	{
		super(context, 0);
		mFullData = users;
		mData = new ArrayList<User>();
		mData.addAll(mFullData);
	}

	public void setExcludeIdxs(ArrayList<String> e)
	{
		mExcludeIdxs = e;
	}

	public void addExcludeIdxs(String idx)
	{
		mExcludeIdxs.add(idx);
	}

	public void removeExcludeIdxs(String idx)
	{
		mExcludeIdxs.remove(idx);
	}

	@Override
	public int getCount()
	{
		return mData.size();
	}

	@Override
	public User getItem(int index)
	{
		return mData.get(index);
	}

	@Override
	public Filter getFilter()
	{
		Filter f = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				FilterResults filterResults = new FilterResults();
				if (constraint != null)
				{
					ArrayList<User> filteredData = new ArrayList<User>();
					for (int i = 0; i < mFullData.size(); i++)
					{
						User u = mFullData.get(i);
						if (u.name.contains(constraint))
						{
							if (mExcludeIdxs == null || !mExcludeIdxs.contains(u.idx))
							{
								filteredData.add(u);
							}
						}

						u = null;
					}

					mData = filteredData;
					filterResults.values = filteredData;
					filterResults.count = filteredData.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence contraint, FilterResults results)
			{
				if (results != null && results.count > 0)
				{
					notifyDataSetChanged();
				}
				else
				{
					notifyDataSetInvalidated();
				}
			}

			@Override
			public CharSequence convertResultToString(Object resultValue)
			{
				return ((User) resultValue).name;
			}
		};
		return f;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{

		if (position >= mData.size() || position < 0)
		{
			throw new IllegalStateException("couldn't access to index " + position);
		}

		LayoutInflater inflater = LayoutInflater.from(getContext());

		View v = null;
		if (convertView == null || !mData.get(position).idx.equals(convertView.getTag()))
		{
			TextView departmentTV = null;
			TextView nameTV = null;
			TextView rankTV = null;
			TextView roleTV = null;
			ImageView userPIC = null;

			v = inflater.inflate(R.layout.user_list_cell, parent, false);

			userPIC = (ImageView) v.findViewById(R.id.user_pic);
			departmentTV = (TextView) v.findViewById(R.id.department);
			nameTV = (TextView) v.findViewById(R.id.name);
			rankTV = (TextView) v.findViewById(R.id.rank);
			roleTV = (TextView) v.findViewById(R.id.role);

			ImageManager im = new ImageManager();
			im.loadToImageView(ImageManager.PROFILE_SIZE_SMALL, mData.get(position).idx, userPIC);
			departmentTV.setText(mData.get(position).department.nameFull);
			nameTV.setText(mData.get(position).name);
			rankTV.setText(Constants.POLICE_RANK[mData.get(position).rank]);
			roleTV.setText(mData.get(position).role);
		}
		else
		{
			v = convertView;
		}

		return v;
	}
}