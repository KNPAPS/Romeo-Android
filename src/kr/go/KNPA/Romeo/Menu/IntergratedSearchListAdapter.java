package kr.go.KNPA.Romeo.Menu;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.DB.DocuDAO;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Member.Department;
import kr.go.KNPA.Romeo.Member.MemberDetailActivity;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Survey.Survey;
import kr.go.KNPA.Romeo.Util.Formatter;
import kr.go.KNPA.Romeo.Util.ImageManager;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

class IntergratedSearchListAdatper extends BaseAdapter implements OnItemClickListener {
	private String keyword = null;
	private ArrayList<User> users;
	private ArrayList<Document> documents;
	private ArrayList<Survey> surveys;
	private Context context;
	
	public IntergratedSearchListAdatper(Context context, String keyword, ArrayList<User> users, ArrayList<Document> documents, ArrayList<Survey> surveys) {
		this.context = context;
		this.keyword = keyword;
		if(users == null)
			users = new ArrayList<User>();
		this.users = users;
		if(documents == null)
			documents = new ArrayList<Document>();
		this.documents = documents;
		if(surveys == null) 
			surveys = new ArrayList<Survey>();
		this.surveys = surveys;
	}

	static void searchResult(final Context context, String keyword, final ListView searchList)
	{
		// 각 섹션별로 정보 얻어오기
		// 얻어온 정보들을 ArrayList로 만들기

		ArrayList<User> resUsers = searchInMembers(context, keyword);
		ArrayList<Document> resDocs = searchInDocuments(context, keyword);

		// SimpleSectionList

		BaseAdapter listAdapter = new IntergratedSearchListAdatper(context, keyword, resUsers, resDocs, null);

		Sectionizer<Object> sectionizer = new Sectionizer<Object>() {

			@Override
			public String getSectionTitleForItem(Object obj)
			{
				String sectionTitle = "";
				if (obj instanceof User)
				{
					sectionTitle = "사용자";
				}
				else if (obj instanceof Document)
				{
					sectionTitle = context.getString(R.string.document);
				}
				else if (obj instanceof Survey)
				{
					sectionTitle = context.getString(R.string.survey);
				}
				else
				{
					sectionTitle = "기타";
				}
				return sectionTitle;
			}

		};

		final SimpleSectionAdapter<Object> resultAdapter = new SimpleSectionAdapter<Object>(context, listAdapter, R.layout.section_header, R.id.title, sectionizer);
		
		Handler handler = new Handler(Looper.getMainLooper());
		
		handler.post(new Runnable() {

			@Override
			public void run()
			{
				searchList.setAdapter(resultAdapter);
				WaiterView.dismissDialog(context);
			}
		});
		
		
	}
	
	private static ArrayList<User> searchInMembers(Context context, String keyword)
	{
		// // 유저
		Payload request = new Payload().setEvent(Event.Search.user()).setData(new Data().add(0, KEY.SEARCH.QUERY, keyword));
		Data resData = new Connection().async(false).requestPayload(request).request().getResponsePayload().getData();
		ArrayList<User> resUsers = new ArrayList<User>();
		for (int i = 0; i < resData.size(); i++)
		{
			HashMap<String, Object> _user = resData.get(i);

			Department department = new Department((String) _user.get(KEY.DEPT.IDX), (String) _user.get(KEY.DEPT.NAME), (String) _user.get(KEY.DEPT.FULL_NAME), (String) _user.get(KEY.DEPT.PARENT_IDX));
			User user = new User((String) _user.get(KEY.USER.IDX), (String) _user.get(KEY.USER.NAME), (Integer) _user.get(KEY.USER.RANK), (String) _user.get(KEY.USER.ROLE), department);
			resUsers.add(user);
		}
		return resUsers;
	}

	private static ArrayList<Document> searchInDocuments(Context context, String keyword)
	{
		// // 문서
		Cursor cDoc = DAO.document(context).search(keyword);
		ArrayList<Document> resDocs = new ArrayList<Document>();
		cDoc.moveToFirst();
		while (cDoc.getCount() > 0 && !cDoc.isAfterLast())
		{
			Document doc = new Document(cDoc.getString(cDoc.getColumnIndex(DocuDAO.COLUMN_DOC_IDX)), cDoc.getInt(cDoc.getColumnIndex(DocuDAO.COLUMN_DOC_TYPE)),
					cDoc.getString(cDoc.getColumnIndex(DocuDAO.COLUMN_DOC_TITLE)), cDoc.getString(cDoc.getColumnIndex(DocuDAO.COLUMN_DOC_CONTENT)), cDoc.getString(cDoc
							.getColumnIndex(DocuDAO.COLUMN_SENDER_IDX)), null, (cDoc.getInt(cDoc.getColumnIndex(DocuDAO.COLUMN_DOC_TYPE)) != Document.TYPE_DEPARTED) ? true
							: false, cDoc.getLong(cDoc.getColumnIndex(DocuDAO.COLUMN_CREATED_TS)), (cDoc.getInt(cDoc.getColumnIndex(DocuDAO.COLUMN_IS_CHECKED)) == 1) ? true
							: false, cDoc.getLong(cDoc.getColumnIndex(DocuDAO.COLUMN_CHECKED_TS)), null, null,
					(cDoc.getInt(cDoc.getColumnIndex(DocuDAO.COLUMN_IS_FAVORITE)) == 1) ? true : false);
			resDocs.add(doc);
			cDoc.moveToNext();
		}
		return resDocs;
	}
	
	@Override
	public int getCount() {
		return users.size() + documents.size() + surveys.size();
	}

	@Override
	public Object getItem(int pos) {
		if(pos < users.size()) {
			return users.get(pos);
		} else if((pos -= users.size()) < documents.size()) {
			return documents.get(pos);
		} else if((pos -= documents.size()) < surveys.size()) {
			return surveys.get(pos);
		} else {
			return new Object();
		}
	}

	@Override
	public long getItemId(int pos) {
		return getItem(pos).hashCode();
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		Object item = getItem(pos);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(item instanceof User) {
			User user = (User)item;
			View view = inflater.inflate(R.layout.member_favorite_user_cell, parent, false);
			ImageView  userPicIV = (ImageView) view.findViewById(R.id.user_pic);
			new ImageManager().loadToImageView(ImageManager.PROFILE_SIZE_SMALL, user.idx, userPicIV);
			TextView deptTV = (TextView)view.findViewById(R.id.department);
			deptTV.setText(user.department.nameFull);
			TextView nameTV = (TextView)view.findViewById(R.id.name);
			nameTV.setText(user.name);
			TextView rankTV = (TextView)view.findViewById(R.id.rank);
			rankTV.setText(User.RANK[user.rank]);
			TextView roleTV = (TextView)view.findViewById(R.id.role);
			roleTV.setText(user.role);
			
			return view;
			
		} else if(item instanceof Document) {
			
			final Document doc = (Document)item;
			View view = null;
			if(doc.favorite == true) {
				view = inflater.inflate(R.layout.document_list_cell_favorite, parent, false);
			} else {
				switch(doc.subType()) {
				
					case Document.TYPE_DEPARTED: 
						view = inflater.inflate(R.layout.document_list_cell_departed, parent, false); break;
					
					case Document.TYPE_RECEIVED: 
					default : 
						view = inflater.inflate(R.layout.document_list_cell_received, parent, false); break;
				}
			}
			
			TextView titleTV = (TextView)view.findViewById(R.id.title);
			titleTV.setText(doc.title);
			final TextView senderTV = (TextView)view.findViewById(R.id.sender);
			
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					senderTV.setText( (String)msg.obj );
				}
			};
			
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					final User sender = User.getUserWithIdx(doc.senderIdx);
					Message m = handler.obtainMessage();
					m.obj = sender.department.nameFull + " " + User.RANK[sender.rank] +" "+ sender.name;
//					context.runOnUiThread(new Runnable() {
//						
//						@Override
//						public void run() {
//							senderTV.setText(sender.department.nameFull + " " + User.RANK[sender.rank] +" "+ sender.name );
//						}
//					});
				}
			}).start();
			
			TextView arrivalDTTV = (TextView)view.findViewById(R.id.arrivalDT);
			arrivalDTTV.setText(Formatter.timeStampToRecentString(doc.TS));
			
			return view;
			
		} else if(item instanceof Survey) {
			
			Survey survey = (Survey)item;
			View view = inflater.inflate(R.layout.member_favorite_user_cell, parent, false);
			return view;
			
		} else {
			
			return new View(context);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long pos_l) {
		Object item = getItem(pos);
		if(item instanceof User) {
			Intent intent = new Intent(this.context, MemberDetailActivity.class);

			Bundle b = new Bundle();
			b.putString(MemberDetailActivity.KEY_IDX, ((User)getItem(pos)).idx );
			b.putInt(MemberDetailActivity.KEY_IDX_TYPE, MemberDetailActivity.IDX_TYPE_USER);
			intent.putExtras(b);	
			
			this.context.startActivity(intent);
		}
		
	}
	
	
}
