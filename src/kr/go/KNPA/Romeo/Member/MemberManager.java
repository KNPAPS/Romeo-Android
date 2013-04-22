package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.HashMap;

import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.StatusCode;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import android.os.Handler;


public class MemberManager {
	public static int USER_IDX = 0;
	public static int USER_NAME = 1;
	public static int USER_DEPARTMENT = 2;
	public static int USER_RANK = 3;
	public static int USER_PIC = 4;
	public static int USER_OBJECT = 5;
	public static int USER_ALL = 6;
	
	private static MemberManager _instance = null;
	
	
	private static HashMap<String, User> cachedUsers = null;	//! 캐시된 사람들 목록
	private static HashMap<String, Department> cachedDepartment = null;
	
	// Apply Singleton Type
	public static MemberManager sharedManager() {
		if(_instance == null) {
			_instance = new MemberManager();
		}
		
		if ( cachedUsers == null ) {
			cachedUsers = new HashMap<String, User>();
		}
		
		if ( cachedDepartment == null ) {
			cachedDepartment = new HashMap<String, Department>();
		}
		
		return _instance;
	}
	
	
	/**
	 * 다른 멤버의 정보 저장
	 * @param member Member 객체
	 */
	public void cacheUser(User user) {
		if(cachedUsers == null)
			cachedUsers = new HashMap<String, User>();
		cachedUsers.put(user.idx, user);
	}
	
	public void cacheDepartment(Department department) {
		if(cachedDepartment == null)
			cachedDepartment = new HashMap<String, Department>();
		cachedDepartment.put(department.idx, department);
	}
	
	
	public User getUser(final String idx) {
		User user = cachedUsers.get(idx);
		if ( user != null ) {
			return user;
		} else {
			Thread t = new Thread(){
				public void run() {
					Payload request = new Payload().setEvent(Event.User.getUserInfo())
							.setData( new Data().add(0, KEY.USER.IDX, idx) );
					Connection conn = new Connection().requestPayload(request).async(false).request();
					Payload response = conn.getResponsePayload();
					
					if ( response.getStatusCode() == StatusCode.SUCCESS ) {
						HashMap<String,Object> hm = response.getData().get(0);
						
						Department dep = new Department(
													(String)hm.get(KEY.DEPT.IDX), 
													(String)hm.get(KEY.DEPT.NAME), 
													(String)hm.get(KEY.DEPT.FULL_NAME), 
													null, 
													Department.NOT_SPECIFIED
												);
						
						User userFromServer = new User.Builder()
											.idx((String)hm.get(KEY.USER.IDX))
											.name((String)hm.get(KEY.USER.NAME))
											.rank(Integer.parseInt((String)hm.get(KEY.USER.RANK)))
											.role((String)hm.get(KEY.USER.ROLE))
											.department(dep)
											.build();

						cacheUser(userFromServer);
					}
				}
			};

			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			User u = cachedUsers.get(idx);
			return u;
		}
	}
	
	public ArrayList<User> getUsers(ArrayList<String> idxs) {
		
		ArrayList<User> users = new ArrayList<User>();
		Data data = new Data();
		for(int i=0; i< idxs.size(); i++) {
			User cachedUser = cachedUsers.get(idxs.get(i));
			if ( cachedUser != null ) {
				users.add(cachedUser);
			} else {
				data.add(i, KEY.USER.IDX, idxs.get(i));
			}
		}
		
		//새로 서버에서 가져올 사람이 없으면 걍 리턴
		if ( data.size() == 0 ) {
			return users;
		}
		
		//캐시되어있지 않은 사람들은 서버에서 정보를 가져옴
		Payload request = new Payload().setEvent(Event.User.getUserInfo())
								.setData( data );
		
		Connection conn = new Connection().requestPayload(request).async(false).request();
		Payload response = conn.getResponsePayload();
		
		if ( response.getStatusCode() == StatusCode.SUCCESS ) {
			Data responseData = response.getData();
			
			for(int i=0; i<responseData.size(); i++) {
				HashMap<String,Object> hm = response.getData().get(i);
				
				Department dep = new Department(
													(String)hm.get(KEY.DEPT.IDX), 
													(String)hm.get(KEY.DEPT.NAME), 
													(String)hm.get(KEY.DEPT.FULL_NAME), 
													null, 
													Long.parseLong(hm.get(KEY.DEPT.SEQUENCE)==null?"0":(String)hm.get(KEY.DEPT.SEQUENCE))
												);
				
				User user = new User.Builder()
									.idx((String)hm.get(KEY.USER.IDX))
									.name((String)hm.get(KEY.USER.NAME))
									.rank(Integer.parseInt((String)hm.get(KEY.USER.RANK)))
									.role((String)hm.get(KEY.USER.ROLE))
									.department(dep)
									.build();

				cacheUser(user);
				users.add(user);
			}
				
			return users;
		} else {
			return null;
		}
	}
	
	/**
	 * 해당 hash를 가진 사람에 대한 자료 cache에서 삭제
	 * @param hash
	 * @return
	 */
	public void removeCachedMember(String hash) {
		cachedUsers.remove(hash);
	}
	
	
	
	/**
	 * 부서 정보 가져와서 Department 객체에 담아 반환\n
	 * parentHash까지는 가져온 데이터로부터 할당을 할 수 있지만\n
	 * Department 객체의 parentDept 멤버는 클라이언트쪽에서 따로 지정해줘야 함\n
	 * @param deptHash
	 * @return
	 */
	public Department getDeptartment(final String deptIdx) {
		Department department = cachedDepartment.get(deptIdx);
		if ( department != null )
			return department;
		
		Thread t = new Thread(){
			@Override
			public void run() {
				//setting request payload
				Payload request = new Payload().setEvent(Event.User.getDepartmentInfo());
				Data reqData = new Data();
				reqData.add(0,KEY.DEPT.IDX,deptIdx);
				
				request.setData(reqData);
				
				Connection conn = new Connection().async(false).requestPayload(request).request();

				Payload response = conn.getResponsePayload();
				if ( response.getStatusCode() == StatusCode.SUCCESS ){
					
					Data respData = response.getData();
					
					Department dept = new Department(
													(String)reqData.get(0, KEY.DEPT.IDX), 
													(String)reqData.get(0, KEY.DEPT.NAME), 
													(String)reqData.get(0, KEY.DEPT.FULL_NAME), 
													(String)reqData.get(0, KEY.DEPT.PARENT_IDX), 
													(String)respData.get(0, KEY.DEPT.SEQUENCE));
					cacheDepartment(dept);
				} else {
					//TODO status 코드에 따라서 상황 처리	
				}
				super.run();
			}
		};

		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Department d = cachedDepartment.get(deptIdx);
		return d;
		
	}
	
	/**
	 * 해당 부서의 하위 부서 목록을 가져온다
	 * @param parentHash
	 * @return
	 */
	private static ArrayList<Department> children;
	public ArrayList<Department> getChildDepts(final String deptIdx) {
		Thread t = new Thread(){
			@Override
			public void run() {
				//setting request payload
				Payload request = new Payload().setEvent(Event.User.getChildDepartments());
				
				Data reqData = new Data();
				
				if(deptIdx == null || deptIdx.trim().length() < 1 || deptIdx.trim().equals("")) {
					reqData.add(0,KEY.DEPT.IDX, "");
				} else {
					reqData.add(0,KEY.DEPT.IDX, deptIdx);
				}
				
				
				request.setData(reqData);
				
				Connection conn = new Connection().requestPayload(request).async(false).request();
				
				Payload response = conn.getResponsePayload();
				
				if ( response.getStatusCode() == StatusCode.SUCCESS ){
					Data respData = response.getData();
					int nDeps = respData.size();
					
					//결과로 리턴할 객체
					children = new ArrayList<Department>(nDeps);
					
					
					for ( int i=0; i<nDeps; i++ ) {
						Department dep = new Department(
															(String)respData.get(i, KEY.DEPT.IDX), 
															(String)respData.get(i, KEY.DEPT.NAME), 
															(String)respData.get(i, KEY.DEPT.FULL_NAME), 
															(String)respData.get(i, KEY.DEPT.PARENT_IDX), 
															(String)respData.get(i, KEY.DEPT.SEQUENCE)
														);

						cacheDepartment(dep);
						
						children.add(dep);
					}
					
					
				}else {
					//TODO status 코드에 따라서 상황 처리
				}
				
				super.run();
			}
		};

		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return children;
	}
	
	public ArrayList<User> getDeptMembers(String depIdx, boolean doRecursive) {
		//setting request payload
		Payload request = new Payload().setEvent(Event.User.getMembers());
		
		Data reqData = new Data();
		
		if( depIdx != null) {
			reqData.add(0, KEY.DEPT.IDX, depIdx);
			reqData.add(0, KEY.DEPT.FETCH_RECURSIVE, doRecursive==true?1:0 );
		} else {
			// Root Department
			reqData.add(0, KEY.DEPT.IDX, "");
			reqData.add(0, KEY.DEPT.FETCH_RECURSIVE, doRecursive==true?1:0 );
		}
		
		request.setData(reqData);
		
		Connection conn = new Connection().async(false).requestPayload(request).request();
		
		Payload response = conn.getResponsePayload();
		if ( response.getStatusCode() == StatusCode.SUCCESS ) {
			Data respData = response.getData();
			
			int nUsers = respData.size();
			ArrayList<User> members = new ArrayList<User>(nUsers);
			
			for (int i=0; i < nUsers; i++ ) {
				User user = null;
				if(doRecursive == false) {
					user = new User.Builder()
										.idx( (String)respData.get(i,KEY.USER.IDX) )
										.name( (String)respData.get(i,KEY.USER.NAME) )
										.rank( Integer.parseInt( (String)respData.get(i,KEY.USER.RANK)) )
										.role( (String)respData.get(i,KEY.USER.ROLE))
										.department(getDeptartment(depIdx))
										.build();
				} else {
					// recursive
					user = new User.Builder()
						.idx( (String)respData.get(i,KEY.USER.IDX) )
						.name( (String)respData.get(i,KEY.USER.NAME) )
						.rank( Integer.parseInt( (String)respData.get(i,KEY.USER.RANK)) )
						.role( (String)respData.get(i,KEY.USER.ROLE))
						//.department(dept) TODO
						.build();
				}
				if(user != null)
					members.add(user);
			}
			
			return members;
			
		} else {
			
			return null;
		}
	}
}
