package kr.go.KNPA.Romeo.Member;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MemberListSearchAdepter extends BaseAdapter {

	private BaseAdapter listAdapter;
	
	public MemberListSearchAdepter(BaseAdapter listAdapter) {
		this.listAdapter = listAdapter;
	}
	
	@Override
	public int getCount() {
		return listAdapter.getCount();
	}

	@Override
	public Object getItem(int pos) {
		//if(pos == 0) return getItem(0);
		//else 
		//return listAdapter.getItem(pos-1);
		return listAdapter.getItem(pos-1);
	}

	@Override
	public long getItemId(int pos) {
		//if(pos == 0 ) return -777;
		//else 
			//return listAdapter.getItemId(pos-1);
		return listAdapter.getItemId(pos-1);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		//if(pos == 0) return null;
		//else 
		//return listAdapter.getView(pos-1, convertView, parent);
		return listAdapter.getView(pos-1, convertView, parent);
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		//return (isEnabled(0) && listAdapter.areAllItemsEnabled());
		return listAdapter.areAllItemsEnabled();
	}
	
	@Override
	public int getItemViewType(int position) {
		//if(position == 0) return -777;
		//else 
		//return listAdapter.getItemViewType(position-1);
		return listAdapter.getItemViewType(position-1);
	}
	
	@Override
	public boolean isEmpty() {
		return listAdapter.isEmpty();
	}
	
	@Override
	public void notifyDataSetChanged() {
		listAdapter.notifyDataSetChanged();
		super.notifyDataSetChanged();
	}
	
	@Override
	public void notifyDataSetInvalidated() {
		listAdapter.notifyDataSetInvalidated();
		super.notifyDataSetInvalidated();
	}
	
	@Override
	public boolean hasStableIds() {
		return listAdapter.hasStableIds(); 
	}

	@Override
	public int getViewTypeCount() {
		return listAdapter.getViewTypeCount();
	}
	
	
}
