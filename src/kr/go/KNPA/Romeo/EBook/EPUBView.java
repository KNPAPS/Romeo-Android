package kr.go.KNPA.Romeo.EBook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import kr.go.KNPA.Romeo.Util.WaiterView;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.service.MediatypeService;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

public class EPUBView extends WebView {

	static final String ASSET_DIR = "file://android_asset/";
	static final String BOOKS_DIR = "books/";
	private String name = null;
	
	private Handler unzipHandler = null;
	private EBookFragment controller = null; 
	
	
	public Book book = null;
	
	public EPUBView(Context context) {	super(context);	}
	public EPUBView(Context context, AttributeSet attrs) {	super(context, attrs);	}
	public EPUBView(Context context, AttributeSet attrs, int defStyle) {	super(context, attrs, defStyle);	}
	
	public void setController(EBookFragment controller) {
		this.controller = controller;
	}
	
	public void initEPUB(Book book, String name) {
		this.book = book;
		this.name = name;
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setSupportZoom(true);
		this.getSettings().setBuiltInZoomControls(true);
		//this.getSettings().set
		
		unzipHandler = new Handler();
		
		//deleteCache(this.getBookDirectory());
		
		log("isUnzipped? : " + isUnzipped());
		if(!isUnzipped()) {
			downloadResources();
		} else {
			if(EPUBView.this.controller != null)
				EPUBView.this.controller.showContentsListDialog();	
		}
	}
	
	private static boolean deleteCache(String fileName) {
		File f = new File( fileName );
		String[] list = null;
		String path = fileName;
		if(f.isDirectory()) {
			list = f.list();
			for(int i=0; i<list.length; i++)
				deleteCache(path + File.separator + list[i]);
		}
		
		if(!f.exists())
			return false;
		
		return f.delete();
	}
	
	private boolean isUnzipped() {
		WaiterView.showDialog(getContext());
		WaiterView.setTitle("데이터를 확인중입니다");
		File f = new File( this.getBookDirectory() );
		if (f.exists() && f.list().length > 0) {
			WaiterView.dismissDialog(getContext());
			return true; 
		} else {
			WaiterView.dismissDialog(getContext());
			return false; 
		}
		
		
	}
	
	
	private void unzippedEPUB() {
		WaiterView.dismissDialog(getContext());
		if(this.controller != null)
			this.controller.showContentsListDialog();
	}
	

	private void downloadResources() {
		WaiterView.showDialog(getContext());
		WaiterView.setTitle("첫 사용 준비중입니다");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				// baseURL이 되는 Text 폴더를 만들도록 한다.
				// html 파일들은 리소스 쓰기 중 취급되지 않으므로 생성되지 않는다.
				String textPath = getBookDirectory() + "Text" + File.separator;
				File textDir = new File(textPath);
				if(!textDir.exists()) 
					textDir.mkdirs();
				
				Collection<Resource> resources = book.getResources().getAll();
				Iterator<Resource> itr = resources.iterator();
				
				int totalCnt = resources.size();
				int accumedCnt = 0;
				// 모든 리소스를 순회하며 필요한 파일만 쓰도록 한다.
				while (itr.hasNext()) {
					Resource res = itr.next();
					if (
						(res.getMediaType() == MediatypeService.JPG) || 
						(res.getMediaType() == MediatypeService.PNG) || 
						(res.getMediaType() == MediatypeService.GIF) || 
						(res.getMediaType() == MediatypeService.CSS)	)  {
					        
						try {
							writeFile(res);
						} catch (Exception e) {
							log(e.getMessage());
						}
				   	 
					}
					
					accumedCnt++;
					final int percent = 100*accumedCnt/totalCnt;
					unzipHandler.post(new Runnable() {
						@Override
						public void run() {
							WaiterView.setProgress(percent);
						}
					}); 
					
					
				}	// while End
				
				// 필요한 파일들을 다 썼으면, 마무리를 위해 함수를 호출한다.
				unzipHandler.post(new Runnable() {
					
					@Override
					public void run() {
						unzippedEPUB();
					}
				});
			}
		}).start();
		
	}
	
	private void writeFile(Resource res) throws FileNotFoundException, IOException {
		
		String targetDir = getBookDirectory();
		String path = targetDir + res.getHref();
		
		File resFile = new File( path );
		
		if (path.lastIndexOf('/') != -1) {
		    File d = new File(path.substring(0, path.lastIndexOf('/')));
		    d.mkdirs();
		}
		
		if( resFile.exists() && ( !resFile.canRead() || resFile.length() != res.getData().length ) ) 
			resFile.delete();
		
		if(resFile.exists() == false) {
			resFile.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(resFile);
	        fos.write(res.getData());
	        fos.close();
		} else {
			log("file " +  res.getHref() +  " Exsists");
		}
			
		
	}
	
	public void loadEPUBPage(Resource res) {
		String line = "";
		String linez = "";
		try {
			InputStream is = res.getInputStream();
			StringBuffer sb = new StringBuffer();
			BufferedReader reader = new BufferedReader( new InputStreamReader(is));
			
			try {
				while ( (line = reader.readLine()) != null ) {
					linez = sb.append(line + "\n").toString();
					//log(line);
				}
			} catch(IOException e) {
				
			}
			
			
		} catch (IOException e) {
			
		}
		
		this.loadDataWithBaseURL("content://kr.go.KNPA.Romeo.EBook" + this.getBookDirectory() + "Text/", linez, "text/html", "utf-8", null);
	}
	
	@Override
	public void loadDataWithBaseURL(String baseUrl, String data,
			String mimeType, String encoding, String historyUrl) {
		super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
	}

	public String getBookDirectory() {
		return getContext().getFilesDir().getAbsolutePath() + File.separator + BOOKS_DIR +  this.name + File.separator;
	}
	
	private void log(String str) {
		Log.e("EBook", str);
	}
	
	public static class ContentListAdapter implements ExpandableListAdapter, OnGroupClickListener, OnChildClickListener {
		EPUBView epubView = null;
		TableOfContents root = null;
		RomeoDialog dialog = null;
		Context context = null;
		
		public ContentListAdapter(Context context, EPUBView epubView) {
			this.epubView = epubView; 
			this.root = epubView.book.getTableOfContents();	
			//this.dialog = dialog;	
			this.context = context;
		}
		
		public void setDialog(RomeoDialog dialog) {
			this.dialog = dialog;
		}
		
		@Override	public boolean areAllItemsEnabled() {	return true;	}
		@Override	public long getGroupId(int groupPosition) {	return ((TOCReference)getGroup(groupPosition)).getResourceId().hashCode();	}
		@Override	public long getChildId(int groupPosition, int childPosition) {	return ((TOCReference)getGroup(groupPosition)).getChildren().get(childPosition).hashCode();	}
		@Override	public boolean hasStableIds() {		return false;	}
		@Override	public boolean isChildSelectable(int groupPosition, int childPosition) {	return true;	}
		@Override	public boolean isEmpty() {	return false;	}
		@Override	public int getGroupCount() {	return root.getTocReferences().size();	}
		@Override	public int getChildrenCount(int groupPosition) {	return root.getTocReferences().get(groupPosition).getChildren().size();	}

		

		@Override
		public Object getGroup(int groupPosition) {
			return root.getTocReferences().get(groupPosition);
		}
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return root.getTocReferences().get(groupPosition).getChildren().get(childPosition);
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TOCReference group = (TOCReference)getGroup(groupPosition);
			convertView  = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
										.inflate(R.layout.dialog_menu_cell, parent, false);
			
			TextView titleTV = (TextView)convertView.findViewById(R.id.title);
			titleTV.setText(group.getTitle());
			return convertView;
		}
		
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			
			TOCReference child = (TOCReference)getChild(groupPosition, childPosition);
			convertView  = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
										.inflate(R.layout.dialog_menu_cell, parent, false);
			
			TextView titleTV = (TextView)convertView.findViewById(R.id.title);
			titleTV.setText(child.getTitle());
			return convertView;
		}

		
		@Override	public void onGroupCollapsed(int groupPosition) {}
		@Override	public void onGroupExpanded(int groupPosition) {}
		@Override	public void registerDataSetObserver(DataSetObserver observer) {}
		@Override	public void unregisterDataSetObserver(DataSetObserver observer) {}
		@Override	public long getCombinedChildId(long groupId, long childId) {	return ((groupId + childId)+"").hashCode(); }
		@Override	public long getCombinedGroupId(long groupId) {	return ((groupId)+"").hashCode();}

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
			if( hasChildren(groupPosition) ) {
				return false;
			} else {
				epubView.loadEPUBPage(((TOCReference)getGroup(groupPosition)).getResource());
				dialog.dismiss();
				return true;
			}
		}

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			epubView.loadEPUBPage( ((TOCReference)getChild(groupPosition, childPosition)).getResource() );
			dialog.dismiss();
			return true;
		}


		protected boolean hasChildren(int groupPosition) {
			return getChildrenCount(groupPosition) > 0;
		}
	}
}
