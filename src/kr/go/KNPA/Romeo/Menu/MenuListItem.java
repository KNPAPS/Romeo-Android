package kr.go.KNPA.Romeo.Menu;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuListItem extends HashMap<String, String>{
	// 단순 텍스트가 아닌 다양한 정보를 셀에 넣기 위하여 MenuListAdapter에 사용될 Object를 정의한다.
	private static final long	serialVersionUID	= -6641836254004025590L;
	private final static String KEY_TITLE = "title";
	private final static String KEY_ICON_IMAGE = "iconImage";
	private final static String KEY_CODE = "code";

//	private String	title;		// 이름
//	private int		iconImage;	// 아이콘
//	private String	code;

	private ArrayList<MenuListItem> children;
	
	public MenuListItem(int title, int iconImage, String code) {
		// 생성자
//		this.title = title;
//		this.iconImage = iconImg;
//		this.code = code;
		this.put(KEY_TITLE, ""+title);
		this.put(KEY_ICON_IMAGE, ""+iconImage);
		this.put(KEY_CODE, code);
	}
	
	public int section() {
		return this.title();
	}
	public int title() {
		return Integer.parseInt(this.get(KEY_TITLE));
	}
	public int iconImage() {
		return Integer.parseInt(this.get(KEY_ICON_IMAGE));
	}
	public String code() {
		return this.get(KEY_CODE);
	}
	
	public ArrayList<MenuListItem> children() {
		return children;
	}

	public void children(ArrayList<MenuListItem> children) {
		this.children = children;
	}
	
	public void addChild(MenuListItem child) {
		if(children == null)
			children = new ArrayList<MenuListItem>();
		children.add(child);
	}

	public String toString() {
		String title = this.get(KEY_TITLE);
		String iconImage  = this.get(KEY_ICON_IMAGE);
		String code = this.get(KEY_CODE);
		
		title = title != null && title.length() > 0 ? title : "null";  
		iconImage = iconImage != null && iconImage.length() > 0 ? iconImage : "null";
		code = code != null && code.length() > 0 ? code : "null";
		
		return super.toString() + "\n" + "title : " + title + ", iconImage : " + iconImage + ", code : " + code;
	}


}