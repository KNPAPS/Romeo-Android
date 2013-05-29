package kr.go.KNPA.Romeo.Library;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.go.KNPA.Romeo.Util.CollectionFactory;

public class SocialEvilManualBook extends ImageBookFragment {

	@Override
	protected String getBasePath() {
		return "socialevil";
	}

	@Override
	protected String getFullTitle() {
		return "4대 사회악 근절 전담부대 매뉴얼";
	}

	@Override
	protected String getShortTitle() {
		return "4대악 근절";
	}

	@Override
	List<Map<String, String>> initGroupData() {
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"순찰 요령, 불심검문 등 일반활동 사항") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"성폭력 사건 대응") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"학교폭력 사건 대응") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"가정폭력 사건 대응") );
		groupData.add( CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_GROUP_TITLE,	"불량식품 사건 대응") );
		
		return groupData;
	}

	@Override
	List<List<Map<String, String>>> initChildData() {
List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        
        List<Map<String, String>> listItem = null;
        
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"1. 순찰의 기본자세"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"2. 순찰 중 수행업무"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"3. 대인 검문검색"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"4. 대인 검문시 용의점 선별 방법"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"5. 대인 검문시 유의사항"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"6. 대인 불심검문 요령 및 사례"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"7. 외국인 불심검문 및 신원확인 요령"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"8. 범죄혐의자에 대한 신원확인"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"9. 단속과정에서 신분확인을 거부하는 경우"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"10. 차량 검문검색"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"11. 대차 검문시 유의점 선별방법"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"12. 공무집행을 하는 경찰관에게 모욕하는 경우"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"13. 공무집행을 하는 경찰관에게 상해,폭행하는 경우"));
		childData.add(listItem);
		
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"1. 성폭력의 정의"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"2. 성폭력 관련 법률의 이해"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"3. 성폭력 사건 처리절차"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"4. 성폭력 사건 관련 Q&A"));
		childData.add(listItem);
		
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"1. 학교폭력 사건 대응"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"2. 학교폭력 처리 요령"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"3. 학교폭력 사건 처리 절차"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"4. 피해학생 지원 제도"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"5. 청소년 음주,흡연 등 발견시"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"6. 학교폭력 관련 Q&A"));
		childData.add(listItem);
		
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"1. 가정폭력에 대한 이해"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"2. 가정폭력 처리 요령"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"3. 가정폭력 Q&A"));
		
		childData.add(listItem);
		
		listItem = new ArrayList<Map<String, String>>();
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"1. 불량식품의 정의"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"2. 4대 사회악, '불량식품'관련 추진정책 관련 용어 검토"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"3. 대표적 불량식품 유형"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"4. 식품안전 업무 특성"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"5. 단속 지침"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"6. 현장 단속시 유의사항"));
		listItem.add(CollectionFactory.hashMapWithKeysAndStrings(ImageBookContentsListAdapter.KEY_CHILD_TITLE,		"7. 부정,불량식품 현장 식별 요령"));
		childData.add(listItem);
		
		return childData;
	}

}
