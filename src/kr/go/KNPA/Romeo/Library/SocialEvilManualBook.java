package kr.go.KNPA.Romeo.Library;

import java.util.List;
import java.util.Map;

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
		return null;
	}

	@Override
	List<List<Map<String, String>>> initChildData() {
		return null;
	}

}
