package kr.go.KNPA.Romeo.EBook;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.RomeoDialog;
import kr.go.KNPA.Romeo.Util.WaiterView;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;
import android.content.Context;
import android.content.res.AssetManager;
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

	private static final String ASSET_DIR = "file:"+File.separator+File.separator+"android_asset"+File.separator;
	private static final String BOOKS_DIR = "books/";
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
	
	public void initEPUB(String name) {
		this.name = name;
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setSupportZoom(true);
		this.getSettings().setBuiltInZoomControls(true);
		
		
		AssetManager am = getContext().getAssets();
		
		try {
			InputStream epubIS = am.open(BOOKS_DIR + name + ".epub");
			this.book = (new EpubReader()).readEpub(epubIS);
		} catch(IOException e) {
			log(e.getMessage());
		}
		
		log("isUnzipped? : " + isUnzipped());
		if(!isUnzipped()) {
			unzipEPUB();
		} else {
			if(this.controller != null)
				this.controller.showContentsListDialog();	
		}
		//downloadResources();	
	}
	
	public TableOfContents getTableOfContents() {
		return this.book.getTableOfContents();
	}
	
	private boolean isUnzipped() {
		File f = new File( this.getBookDirectory() );
		if (f.exists() && f.list().length > 0) {
			return true; 
		} else {
			return false; 
		}
	}
	
	private void unzipEPUB() {
		WaiterView.showDialog(getContext());
		WaiterView.setTitle("첫 사용 준비중입니다");
		
		unzipHandler = new Handler();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				AssetManager am = getContext().getAssets();
				
				String targetDir = getBookDirectory();
				
				try {
					
					// 책 자체에 대한 디렉토리가 존재하지 않을 경우 만든다.
					File f = new File( targetDir );
					if(!f.isDirectory()) {
						f.mkdirs();
					}

					// 파일 전체의 길이를 구하기 위해 루프를 한번 돌린다.
					ZipInputStream countZIN = new ZipInputStream( am.open(BOOKS_DIR + name + ".epub") );
					long totalLength = 1;
					int totalCnt = 0;
					try {
						ZipEntry countZE = null;
						while ( ( countZE = countZIN.getNextEntry() ) != null ) {
							totalLength += countZE.getSize();
							totalCnt ++;
						}
					} finally {
						countZIN.close();
					}
					
					
					// ASSET 디렉토리에서 Epub 파일을 로드한다.
					//ZipInputStream zin = new ZipInputStream(new FileInputStream(ASSET_DIR + BOOKS_DIR + this.name + ".epub"));
					ZipInputStream zin = new ZipInputStream( am.open(BOOKS_DIR + name + ".epub") );
					ZipEntry ze = null;
					long accumedLength = 1;
					int accumedCnt = 0;
					try {
						// 모든 엔트리를 순회하며 작업을 진행한다.
						while ((ze = zin.getNextEntry()) != null) {
							String path = targetDir + ze.getName();

							// 내부 파일 구조에 따른, 디렉토리가 존재하지 않을 경우 디렉토리를 생성해준다.
							if (path.lastIndexOf('/') != -1) {
							    File d = new File(path.substring(0, path.lastIndexOf('/')));
							    d.mkdirs();
							}

							// 파일을 뽑아내어 저장한다.
							if (!ze.isDirectory()) {
								byte[] buffer = new byte[2048];
								FileOutputStream fout = new FileOutputStream(path, false);
								try {
									for (int c = zin.read(buffer); c != -1; c = zin.read(buffer)) {
										fout.write(c);
									}
									zin.closeEntry();
								} finally {
									fout.close();
								}
							}
//			             
//							if (!ze.isDirectory()) {
//								byte[] buffer = new byte[2048];
//								FileOutputStream fout = new FileOutputStream(path, false);
//								BufferedOutputStream bos = new BufferedOutputStream(fout, buffer.length);
//								
//								int c = 0;
//								int size;
//								
//								while ( (size = zin.read(buffer, 0, buffer.length)) != -1) {
//									bos.write(buffer, 0, size);
//								}
//								
//								bos.flush();
//								bos.close();
//								
//								fout.flush();
//								fout.close();
//								zin.closeEntry();
//								
//							}
			             
							
							
							
							// WaiterView에 압축해제 진행 정도를 표시해준다.
							accumedLength += ze.getSize();
							accumedCnt++;
							final long acc = accumedLength;
							final long tot = totalLength;
							log("unzipped : " + (int)( (100*acc)/tot) + "%, bytes : " + accumedLength + "/" + totalLength + ", cnt : " + accumedCnt + "/" + totalCnt );
							
							unzipHandler.post(new Runnable() {
								
								@Override
								public void run() {
									WaiterView.setProgress( (int)( (100*acc)/tot) );
									
								}
							});
							
						}	// while END
					
					} finally {
						zin.close();
					}	// while을 커버하는 try-catch END
					
				} catch (Exception e) {
					log("Unzip exception : "+ e.getMessage());
				}	// 전체 작업에 대한 try-catch END
				
				// 모든 작업을 마쳤으므로 unzzipedEPUB()을 호출하여 마무리를 해준다. 
				unzipHandler.post(new Runnable() {
					
					@Override
					public void run() {
						unzippedEPUB();
					}
				}); 
				
			}	// Runnable Object in Thread End
			
		}).start();	// Thread End
		
		
	}
	
	private void unzippedEPUB() {
		WaiterView.dismissDialog(getContext());
		if(this.controller != null)
			this.controller.showContentsListDialog();
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
