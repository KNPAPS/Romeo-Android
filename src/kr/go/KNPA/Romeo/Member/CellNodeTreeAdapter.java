package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Util.IndexPath;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.BaseAdapter;

public abstract class CellNodeTreeAdapter extends BaseAdapter {

	protected Context context;
	protected CellNode _rootNode;
	  
	public int subType = User.NOT_SPECIFIED;
	
	public void setExeptionList(ArrayList<String> exeptionList) {}
	public void unsetExeptionList() {}
	
	protected abstract void init();
	
	// Deal with Position and IndexPath
		protected IndexPath getIndexPathFromPosition(int pos) {
			IndexPath path = null;
			
			int[] l = new int[IndexPath.MAX_LENGTH];
			for(int i=0; i<l.length; i++) {
				l[i] = -1;
			}
			
			CellNode cn = this.rootNode();
			
			int cnt = 0;
			int li = 0;
			while( true ){
				CellNode _cn = cn.children().get(li); 
				
				int _cnt = cnt + _cn.count(); 
				
				if(_cnt >= (pos+1)) { // cnt = x, _cnt = x+y (x+1 ~ x+y) => group1 : 0~x-1, group2 : x ~ x+y-1
					// target is in this element tree
					if((cnt + 1) == (pos+1) ) { // cn.size() == 0;
						// 기존 cnt에 하나만 더한 것이 pos 값과 같다면, 현재 element를 선택한 것이다.
						// child가 존재했다면 _cnt > pos 였을 것이고, child가 존재하지 않았다면 _cnt == pos 였을 것이다.
						path = _cn.indexPath();
						break;
					} else {
						// 그게 아니라면, 하위 오브젝트를 선택한 것이므로,
						// go to children
						cn = _cn;
						_cn = null;
						li = 0;
						cnt = cnt+1;
					
					}
				} else if(_cnt < (pos+1)) {
					// go to next sibling
					li++;
					cnt = _cnt;
				}
				
			}
			
			return path;
		}
		
		protected int getPositionFromIndexPath(IndexPath path) {
			int[] paths = path.getIndexes(null);
			int pos = -1;
			
			int cnt = 0;
			
			for(int li=0; li<paths.length; li++) {
				CellNode cn = this.rootNode();
				int l = paths[li];
				
				
				for(int i=0; i< li; i++) {
					int _l = paths[li];
					cn = cn.children().get(_l);
				}
				
				for(int i=0; i < paths[l]; i++) {
					cnt += cn.children().get(i).count();
				}
			}
			
			pos = cnt;
			return pos;
		}
		
		
		
		
		// Get Something with IndexPath
		
		protected int numberOfRowsOfTypeInSection(int type, IndexPath path) {
			int result = -1;
			
			CellNode node = CellNode.nodeAtIndexPath(this.rootNode(), path);
			int firstUserCellIndex = -1;
			
			for(int i=0; i< node.children().size(); i++) {
				if(node.children().get(i).type() == CellNode.CN_USER) {
					firstUserCellIndex = i;
					break;
				}
			}
			
			if(type == CellNode.CN_DEPARTMENT) {
				result = firstUserCellIndex;
			} else if(type == CellNode.CN_USER) {
				result = node.children().size() - firstUserCellIndex;
			}
			return result;
		}
		
		protected int nodeOrderInTypeWithIndexPath(int type, IndexPath path) {
			int result = -1;
			if(type == CellNode.CN_DEPARTMENT) {
				// Department 후에 Users가 나오므로, Department는 그대로 return해도 무방하다.
				IndexPath.Iterator itr = new IndexPath.Iterator(path);
				result = itr.lastIndex();
			} else if(type == CellNode.CN_USER) {
				IndexPath.Iterator itr = new IndexPath.Iterator(path);
				int lastIndex = itr.lastIndex();
				
				IndexPath parentPath = path.indexPathByRemovingLastIndex();
				CellNode parentNode = CellNode.nodeAtIndexPath(this.rootNode(), parentPath);
				int firstUserCellIndex = -1;
				for(int i=0; i< parentNode.children().size(); i++) {
					if(parentNode.children().get(i).type() == CellNode.CN_USER) {
						firstUserCellIndex = i;
						break;
					}
				}
				
				result =  lastIndex - firstUserCellIndex;
			}
			return result;
		}

		protected Object objectForRowAtIndexPath(IndexPath path) {
			Object obj = null;
			
			CellNode node = CellNode.nodeAtIndexPath(this.rootNode(), path);
			String idx = node.idx();
			int objectType = node.type();	//getNodeTypeAtIndexPath(path);
			
			if(objectType == CellNode.CN_DEPARTMENT) {
				obj = (Object)MemberManager.sharedManager().getDeptartment(idx);
			} else if (objectType == CellNode.CN_USER){
				obj = (Object)MemberManager.sharedManager().getUser(idx);
			}
					
			return obj;
		}
		
		protected abstract void getSubNodes(CellNode node);

		// ETC
		protected CellNode rootNode() 	{	return _rootNode;						}
		
		public void refresh() 		{		/* this.notifyDataSetChanged(); */	}

		
		
}
