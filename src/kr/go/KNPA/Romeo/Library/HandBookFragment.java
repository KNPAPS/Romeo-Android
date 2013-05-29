package kr.go.KNPA.Romeo.Library;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.go.KNPA.Romeo.Util.CollectionFactory;

public class HandBookFragment extends ImageBookFragment {

	@Override
	protected String getBasePath() {
		return "handbook";
	}

	@Override
	protected String getFullTitle() {
		return "집회시위 현장매뉴얼";
	}

	@Override
	protected String getShortTitle() {
		return "현장매뉴얼";
	}

	@Override
	List<Map<String, String>> initGroupData() {
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"제1장 집회시위 관리 지침") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"제2장 유형별 법규 적용") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"제3장 관련법령 요약 해설") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"제4장 집회시위 관리 지침") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"제5장 집회시위 관리 지침") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"참고") );
		
		return groupData;
	}

	@Override
	List<List<Map<String, String>>> initChildData() {
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        
        List<Map<String, String>> listItem = null;
        
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		ImageBookContentsListAdapter.NO_CHILD));
		childData.add(listItem);
		
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"A. 단순 몸싸움"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"B. 도로점거 시위"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"C. 상징물 소훼"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"D. 1인 시위"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"E. 변형된 1인 시위"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"F. 문화제,기자회견 등 빙자 불법집회"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"G. 불시 항의방문 및 시설점거 농성"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"H. 금지통고된 집회 상경 또는 집결 차단"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"I. 차량시위"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"J. 돌,쇠파이프 및 피켓 등 사용 공격"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"K. 차벽 손괴,방화,전도"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"L. 고공 시위,농성"));
		childData.add(listItem);
		
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"1. 집시법상 위반행위"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"2. 집시법률 처벌 규정 요약"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"3. 시위유형별 위반행위"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"4. 집회장소별 위반행위"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"5. 즉결심판 가능한 경미범죄 유형"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"6. 주요행위별 적용 가능 법령"));
		childData.add(listItem);
		
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		ImageBookContentsListAdapter.NO_CHILD));
		childData.add(listItem);
		
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		ImageBookContentsListAdapter.NO_CHILD));
		childData.add(listItem);
		
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"집회시위 안전관리수칙"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"분사기 운용지침"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"물포 운용지침"));
		childData.add(listItem);
		
		return childData;
	}
	
}
