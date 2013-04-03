package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Config.EventEnum;
import kr.go.KNPA.Romeo.Config.StatusCodeEnum;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;

/**
 * 부서 관련 작업
 * @author 최영우
 * @since 2013.4.2
 */
public class DepartmentManager {
	/**
	 * 부서 정보 가져와서 Department 객체에 담아 반환\n
	 * parentHash까지는 가져온 데이터로부터 할당을 할 수 있지만\n
	 * Department 객체의 parentDept 멤버는 클라이언트쪽에서 따로 지정해줘야 함\n
	 * @param deptHash
	 * @return
	 */
	public static Department getDeptInfo(String deptHash) {
		//setting request payload
		Payload req = new Payload(EventEnum.USER_GET_DEPT_INFO);
		Data reqData = new Data();
		reqData.add(0,Data.KEY_DEPT_HASH,deptHash);
		
		req.setData(reqData);
		
		//connect
		Connection conn = new Connection.Builder( req.toJson() ).build();
		conn.request();
		
		//get response payload
		Payload resp = new Payload( conn.getResponsePayload() );
		
		//make new department object if response is successful
		if ( resp.getStatusCode() == StatusCodeEnum.SUCCESS ){
			
			Data respData = resp.getData();
			
			
			Department dept = new Department(deptHash);
			dept.setDeptFullName(respData.get(0, Data.KEY_DEPT_FULL_NAME).toString());
			dept.setDeptName(respData.get(0, Data.KEY_DEPT_NAME).toString());
			dept.setParentHash(respData.get(0, Data.KEY_DEPT_PARENT_HASH).toString());
			
			return dept;
		}else {
			//TODO status 코드에 따라서 상황 처리
			return null;
		}
	}
	
	/**
	 * 해당 부서의 하위 부서 목록을 가져온다
	 * @param parentHash
	 * @return
	 */
	public static ArrayList<Department> getChildDepts(Department parentDept) {
		//setting request payload
		Payload req = new Payload(EventEnum.USER_GET_CHILD_DEPTS);
		
		Data reqData = new Data();
		reqData.add(0, Data.KEY_DEPT_HASH, parentDept.getDeptHash());
		req.setData(reqData);
		
		//connect
		Connection conn = new Connection.Builder(req.toJson()).build();
		conn.request();
		
		//get response payload
		Payload resp = new Payload( conn.getResponsePayload() );
		
		//make new department object if response is successful
		if ( resp.getStatusCode() == StatusCodeEnum.SUCCESS ){
			Data respData = resp.getData();
			int n = respData.size();
			
			//결과로 리턴할 객체
			ArrayList<Department> childDepts = new ArrayList<Department>(n);
			
			
			for ( int i=0; i<n; i++ ) {
				Department dept = new Department(respData.get(i, Data.KEY_DEPT_HASH).toString());
				
				dept.setDeptFullName(respData.get(i, Data.KEY_DEPT_FULL_NAME).toString());
				dept.setDeptName(respData.get(i, Data.KEY_DEPT_NAME).toString());
				dept.setParentHash(respData.get(i, Data.KEY_DEPT_PARENT_HASH).toString());
				dept.setParentDept(parentDept);
				
				childDepts.add(dept);
			}
			
			return childDepts;
		}else {
			//TODO status 코드에 따라서 상황 처리
			return null;
		}
	}
	
	public static ArrayList<Member> getDeptMembers(Department dept, boolean doRecursive) {
		//setting request payload
		Payload req = new Payload(EventEnum.USER_GET_MEMBERS);
		
		Data reqData = new Data();
		reqData.add(0, Data.KEY_DEPT_HASH, dept.getDeptHash());
		reqData.add(0, Data.KEY_GET_MEMBER_FETCH_ALL, doRecursive==true?1:0 );
		req.setData(reqData);
		
		//connect
		Connection conn = new Connection.Builder(req.toJson()).build();
		conn.request();
		
		//get response payload
		Payload resp = new Payload( conn.getResponsePayload() );
		
		if ( resp.getStatusCode() == StatusCodeEnum.SUCCESS ) {
			Data respData = resp.getData();
			
			int i,n=respData.size();
			
			ArrayList<Member> members = new ArrayList<Member>(n);
			for ( i=0; i<n; i++ ) {
				Member m = new Member(respData.get(i,Data.KEY_USER_HASH).toString());
				//TODO recursive일 때 setDept 문제
				m.setDeptHash( dept.getDeptHash() );
				m.setDept( dept );
				m.setDeptFullName( dept.getDeptFullName() );
				m.setDeptName( dept.getDeptName() );
				m.setMemberHash( respData.get(i,Data.KEY_USER_HASH).toString() );
				m.setMemberName( respData.get(i,Data.KEY_USER_NAME).toString() );
				m.setRankIdx( (Integer)respData.get(i,Data.KEY_USER_RANK) );
				m.setMemberRole( respData.get(i,Data.KEY_USER_ROLE).toString() );
				members.add(m);
			}
			
			return members;
		} else {
			return null;
		}
	}
}
