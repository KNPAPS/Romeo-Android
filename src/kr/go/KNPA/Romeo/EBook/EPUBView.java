package kr.go.KNPA.Romeo.EBook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class EPUBView extends WebView {

	//private static final String ASSET_DIR = "file:"+File.separator+File.separator+"android_asset"+File.separator;
	private static final String BOOKS_DIR = "books/";
	private String name = null;
	
	public Book book = null;
	
	public EPUBView(Context context) {	super(context);	}
	public EPUBView(Context context, AttributeSet attrs) {	super(context, attrs);	}
	public EPUBView(Context context, AttributeSet attrs, int defStyle) {	super(context, attrs, defStyle);	}
	
	public void initEPUB(String name) {
		this.name = name;
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setSupportZoom(true);
		//this.getSettings().setBuiltInZoomControls(true);
		
		AssetManager am = getContext().getAssets();
		
		try {
			InputStream epubIS = am.open(BOOKS_DIR + name + ".epub");
			this.book = (new EpubReader()).readEpub(epubIS);
		} catch(IOException e) {
			log(e.getMessage());
		}
		
		downloadResources();	
	}
	
	public TableOfContents getTableOfContents() {
		return this.book.getTableOfContents();
	}
	
	public void loadEPUB(Resource res) {
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

	private void downloadResources() {
		makeDirectoryForBookCache();
		
		Resources resources = book.getResources();
		Collection<Resource> resList = resources.getAll();
		Iterator<Resource> itr = resList.iterator();
		
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
		}
	}
	
	private boolean makeDirectoryForBookCache() {
		
		Log.d("check Downlod path", getBookDirectory());
		
		String imagesPath = getBookDirectory() + "Images" + File.separator;
		String stylesPath = getBookDirectory() + "Styles" + File.separator;
		String textPath = getBookDirectory() + "Text" + File.separator;
		
		boolean images = false;
		File imagesDir = new File(imagesPath);
		if(imagesDir.exists()) {
			images = (imagesDir.list().length > 0)? true : false;
		} else {
			imagesDir.mkdirs();
			images = false;
		}
		
		boolean styles = false;
		File stylesDir = new File(stylesPath);
		if(stylesDir.exists()) {
			styles = (stylesDir.list().length > 0)? true : false;
		} else {
			stylesDir.mkdirs();
			styles = false;
		}


		File textDir = new File(textPath);
		if(!textDir.exists()) 
			textDir.mkdirs();

		return images && styles;
	}
	
	private boolean isFileExists(String filePath) {
		File file = new File(getBookDirectory() + filePath);
		boolean exists = file.exists() && (file.length() > 0);
		if(exists) {
			if(file.canRead())
				return true;
			else {
				file.delete();
				return false;
			}
		} else 
			return false;
	}
	
	private void writeFile(Resource res) throws FileNotFoundException, IOException {
		String filePath = res.getHref();
		File resFile = new File( getBookDirectory() + filePath );
        
		//log(resFile.getAbsolutePath());
		
		
		if( isFileExists(res.getHref()) && resFile.length() == res.getData().length ) {
			log("file " +  res.getHref() +  " Exsists");
		} else {
			//log("file " +  res.getHref() +  " NOT Exsists. write.");
			resFile.delete();
			resFile.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(resFile);
	        fos.write(res.getData());
	        fos.close();
		}
		
		
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
			this.root = epubView.getTableOfContents();	
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
				epubView.loadEPUB(((TOCReference)getGroup(groupPosition)).getResource());
				dialog.dismiss();
				return true;
			}
		}

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			epubView.loadEPUB( ((TOCReference)getChild(groupPosition, childPosition)).getResource() );
			dialog.dismiss();
			return true;
		}


		protected boolean hasChildren(int groupPosition) {
			return getChildrenCount(groupPosition) > 0;
		}
	}
}
