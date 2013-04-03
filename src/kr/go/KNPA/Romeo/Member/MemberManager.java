package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Config.EventEnum;
import kr.go.KNPA.Romeo.Config.StatusCodeEnum;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;

/**
 * 멤버 객체에 대한 작업을 수행\n
 * 다른 사용자들의 정보를 캐싱하여 내부에 저장해놓거나 사용자에 대한 정보를 가져오는데 활용함 
 * @author 최영우
 * @since 2013.4.2
 */
public class MemberManager{
	
	//! 캐시된 사람들 목록
	private static HashMap<String,Member> cachedMemberMap = null;
	
	/**
	 * 다른 멤버의 정보 저장
	 * @param member Member 객체
	 */
	public static void cacheMember(Member member) {
		cachedMemberMap.put(member.getMemberHash(), member);
	}
	
	/**
	 * hash 값으로 캐시된 유저 검색.\n
	 * 만약 서버에도 없다면 null 리턴
	 * @param hash 유저 해쉬
	 * @param fetchFromServer 만약 자료가 캐시되지 않았다면 서버에서 가져오는지 여부 
	 * @return
	 */
	public static Member getMember(String hash, boolean fetchFromServer) {
		Member user = cachedMemberMap.get(hash);
		if ( user != null ) {
			return user;
		} else {
			Payload reqPl = (new Payload(EventEnum.USER_GET_USER_INFO))
								.setData( (new Data()).add(0,Data.KEY_USER_HASH,hash) );
			Connection conn = new Connection.Builder(reqPl.toJson()).build();
			conn.request();
			Payload respl = new Payload(conn.getResponsePayload());
			
			//성공적으로 가져왔으면 DATA에서 정보를 꺼내 새 member 객체 생성 후 리턴
			if ( respl.getStatusCode() == StatusCodeEnum.SUCCESS ) {
				HashMap<String,Object> hm = respl.getData().get(0);
				
				Member member = new Member(hm.get(Data.KEY_USER_HASH).toString());
				member.setDeptFullName(hm.get(Data.KEY_DEPT_FULL_NAME).toString());
				member.setDeptName(hm.get(Data.KEY_DEPT_NAME).toString());
				member.setMemberName(hm.get(Data.KEY_USER_NAME).toString());
				member.setRankIdx( (Integer) hm.get(Data.KEY_USER_RANK));
				member.setMemberRole(hm.get(Data.KEY_USER_ROLE).toString());
				
				return member;
			} else {
				return null;
			}
		}
	}
	
	/**
	 * user hash의 배열을 받아서 해당 hash들에 대한 사용자 정보를 가져온다\n
	 * 만약 해당 hash에 대한 사용자가 없으면 arraylist에서 해당 index의 값은 null
	 * @param hashes 사용자 해쉬 값들
	 * @param fetchFromServer 캐시된 게 없으면 서버에서 정보를 가져오는지
	 * @return
	 */
	public static ArrayList<Member> getMembers(String[] hashes, boolean fetchFromServer) {
		ArrayList<Member> members = new ArrayList<Member>(hashes.length);
		for(int i=0; i<hashes.length; i++) {
			members.add(getMember(hashes[i],fetchFromServer));
		}		
		return members;
	}
	
	
	/**
	 * 해당 hash를 가진 사람에 대한 자료 cache에서 삭제
	 * @param hash
	 * @return
	 */
	public static void removeCachedMember(String hash) {
		cachedMemberMap.remove(hash);
	}
}
