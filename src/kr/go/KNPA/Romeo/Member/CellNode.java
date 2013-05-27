package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import kr.go.KNPA.Romeo.Util.IndexPath;

public class CellNode {
	private static final int CN_NULL = -777;
	public static final int CN_USER = 1;
	public static final int CN_DEPARTMENT = 2;
	
	public static final int NCHECK = 0;
	public static final int FCHECK = 1;
	public static final int HCHECK = 2;
	
	
	
	private int 				_type 				= 	CN_NULL;
	private String				_idx				=	null;
	//private	IndexPath 			_parentIndexPath 	= 	null;
	private	int 				_status				= 	0;
	private	boolean				_isRoot 			= 	false;
	private	boolean 			_isUnfolded 		= 	false;
	private int					_index				=	CN_NULL;
	private	CellNode 			_parent 			= 	null;
	private	ArrayList<CellNode>	_children 			= 	null;

	private static CellNode				_rootNode			= 	null;
	
	public CellNode() {
		
	}
	
	public static void init(CellNode rootNode) {
		for(int i=0; i<rootNode.children().size(); i++) {
			rootNode.children().get(i).init();
		}
	}
	
	private void init() {
		this._status = CellNode.NCHECK;
		this._isUnfolded = true;
		
		if(this.children() != null) {
			for(int i=0; i<this.children().size(); i++) {
				this.children().get(i).init();
			}
		}
	}
	
	public static CellNode rootNode() {
		if(_rootNode == null)
			_rootNode = new CellNode().isRoot(true).isUnfolded(true).parent((CellNode)null);
		return _rootNode;
	}
	
	public int type() {
		return _type;
	}
	public CellNode type(int type) {
		this._type = type;
		return this;
	}
	
	public String idx() {
		return _idx;
	}
	public CellNode idx(String idx) {
		this._idx = idx;
		return this;
	}
	
	public int status() {
		return _status;
	}
	public CellNode status(int status) {
		this._status = status;
		return this;
	}
	
	public boolean isRoot() {
		return _isRoot;
	}
	public CellNode isRoot(boolean isRoot) {
		this._isRoot = isRoot;
		return this;
	}
	
	public boolean isUnfolded() {
		return _isUnfolded;
	}
	public CellNode isUnfolded(boolean isUnfolded) {
		this._isUnfolded = isUnfolded;
		return this;
	}
	
	public int index() {
		return _index;
	}
	public CellNode index(int index) {
		this._index = index;
		return this;
	}
	
	public IndexPath indexPath() {
		IndexPath path = null;
		if(this.parent() != null && this.parent().isRoot() == false )
			path = this.parent().indexPath();
		
		if(path == null ) {//|| path.length() == 0) {
			path = IndexPath.indexPathWithIndex(this.index());
		} else {
			path = path.clone().addIndex(this.index());
		}
		
		return path;
	}
	
	public CellNode parent() {
		return _parent;
	}
	
	public CellNode parent(CellNode parent) {
		this._parent = parent;
		return this;
	}
	
	public IndexPath parentIndexPath() {
		return parent().indexPath();
	}
	
	public ArrayList<CellNode> children() {
		if(_children == null)
			_children = new ArrayList<CellNode>();
		return _children;
	}
	
	public CellNode children(ArrayList<CellNode> children) {
		this._children = children;
		return this;
	}
	
	public CellNode append(CellNode child) {
		if(_children == null) 
			_children = new ArrayList<CellNode>();
		_children.add(child);
		return this;
	}
	
	public CellNode appendTo(CellNode parent) {
		parent.append(this);
		return parent;
	}
	
	public static class Condition {
		private int 				_type 				= 	CN_NULL;
		private String				_idx				=	null;
		private	int 				_status				= 	CN_NULL;
		private	boolean				_isRoot 			= 	false;
		private IndexPath			_indexPath			=	null;
		private	boolean 			_isUnfolded 		= 	false;
		private boolean				_hasChildren			=	false;
		
		private boolean _hasType 		= 	false;
		private boolean	_hasIdx			=	false;
		private boolean _hasStatus 		= 	false;
		private boolean _hasIsRoot 		= 	false;
		private boolean _hasIndexPath 	= 	false;
		private	boolean	_hasIsUnfolded	= 	false;
		private boolean _hasHasChildren	=	false;
		
		public Condition() {}
		public Condition indexPath(IndexPath path) {
			this._indexPath = path;
			_hasIndexPath = true;
			return this;
		}
		
		public Condition type(int type) {
			this._type = type;
			_hasType = true;
			return this;
		}
		
		public Condition idx(String idx) {
			this._idx = idx;
			_hasIdx = true;
			return this;
		}
		
		public Condition isRoot(boolean isRoot) {
			this._isRoot = isRoot;
			_hasIsRoot = true;
			return this;
		}
		
		public Condition status (int status) {
			this._status = status;
			_hasStatus = true;
			return this;
		}
		
		public Condition isUnfolded(boolean isUnfolded) {
			this._isUnfolded = isUnfolded;
			_hasIsUnfolded = true;
			return this;
		}
		
		public Condition hasChildren(boolean hasChildren) {
			this._hasChildren = hasChildren;
			_hasHasChildren = true;
			return this;
		}
		
		public IndexPath indexPath() {
			return _indexPath;
		}
		
		public int type() {
			return _type;
		}
		
		public String idx()	{
			return _idx;
		}
		
		public int status() {
			return _status;
		}
		
		public boolean isRoot() {
			return _isRoot;
		}
		
		public boolean isUnfolded() {
			return _isUnfolded;
		}
		
		public boolean hasChildren() {
			return _hasChildren;
		}
		
		public boolean hasStatus() {
			return _hasStatus;
		}
		public boolean hasType() {
			return _hasType;
		}
		
		public boolean hasIdx() {
			return _hasIdx;
		}
		
		public boolean hasIndexPath() {
			return _hasIndexPath;
		}
		
		public boolean hasIsRoot() {
			return _hasIsRoot;
		}
		
		public boolean hasIsUnfolded() {
			return _hasIsUnfolded;
		}
		
		public boolean hasHasChildren() {
			return _hasHasChildren;
		}
		
		public void removeType() {
			_hasType = false;
			_type = CN_NULL;
		}
		
		public void removeIdx() {
			_hasIdx = false;
			_idx = null;
		}
		
		public void removeStatus() {
			_hasStatus = false;
			_status = CN_NULL;
		}
		
		public void removeIsRoot() {
			_hasIsRoot = false;
			_isRoot = false;
		}
		
		public void removeIndexPath() {
			_hasIndexPath = false;
			_indexPath = null;
		}
		
		public void removeHasChildren() {
			_hasHasChildren = false;
			_hasChildren = false;
		}
	}


	public static CellNode parent(CellNode node, Condition c) {
		return node.parent(c);
	}
	
	public static ArrayList<CellNode> parents(CellNode node, Condition c ) {
		return node.parents(c);
	}
	
	public static ArrayList<CellNode> children(CellNode node, Condition c) {
		return node.children(c);
	}
	
	public static ArrayList<CellNode> find(CellNode node, Condition c) {
		return node.find(c);
	}
	
	public CellNode parent(Condition c) {
		CellNode p = this.parent();
		while(p != null) {
			if(p.inCondition(c) == true)
				return p;
			p = p.parent();
		}
		
		return null;
	}
	
	public ArrayList<CellNode> parents(Condition c) {
		ArrayList<CellNode> parents = new ArrayList<CellNode>();
		
		CellNode p = this.parent();
		while(p != null) {
			if(p.inCondition(c) == true)
				parents.add(p);
			p = p.parent();
		}
		
		return parents;
	}
	
	public ArrayList<CellNode> children(Condition c) {
		ArrayList<CellNode> children = new ArrayList<CellNode>();
		
		int nChildren = this.children().size();
		for(int i=0; i<nChildren; i++) {
			CellNode child = this.children().get(i); 
			if(child.inCondition(c))
				children.add(child);
		}
		
		return children;
	}

	public ArrayList<CellNode> find(Condition c) {
		ArrayList<CellNode> result = new ArrayList<CellNode>();
		
		if(this.children() != null) {
			for(int i=0; i< this.children().size(); i++) {
				this.children().get(i).find(c, result);
			}
		}
		return result;
	}
	
	private ArrayList<CellNode> find(Condition c, ArrayList<CellNode> array) {
		
		if(this.inCondition(c) == true)
			array.add(this);
		
		if(children() != null) {
			for(int i=0; i < this.children().size(); i++) {
				this.children().get(i).find(c, array);
			}
		}
		return array;
	}
	
	
	// 조건 추가시, 이 메서드만 건들면 된다.
	public boolean inCondition(Condition c) {
		boolean inCondition = true;
		
		if(c.hasIndexPath() && this.indexPath().equals(c.indexPath()) == false)
			inCondition = false;
		
		if(c.hasIsRoot() && (this.isRoot() != c.isRoot()) )
			inCondition = false;
		
		if(c.hasIsUnfolded() && (this.isUnfolded() != c.isUnfolded()) )
			inCondition = false;
		
		if(c.hasStatus() && this.status() != c.status())
			inCondition = false;
		
		if(c.hasType() && this.type() != c.type())
				inCondition = false;
		
		if(c.hasHasChildren() && (        ( (this.children()!=null)&&(this.children().size()>0) ) != c.hasChildren()           ) )
				inCondition = false;
		
		return inCondition;
	}
	
	public static CellNode nodeAtIndexPath(CellNode rootNode, IndexPath path) {

		CellNode cn = rootNode;
		IndexPath.Iterator itr = new IndexPath.Iterator(path);
		
		while(itr.hasNextIndex()) {
			cn = cn.children().get(itr.nextIndex());
		}
		return cn;

	}


	public int countIncludeFolded() {
		int result = 0;
		for(int i=0; i< this.children().size(); i++) {
			result += this.children().get(i).countIncludeFolded();
		}
		return (result+1);
	}

	public int count() {
		int result = 0;
		
		if(isUnfolded() == true) {		  	// UnFolded
			for(int i=0; i< this.children().size(); i++) {
				result += this.children().get(i).count();
			}
		} else if(isUnfolded() == false) { 	// Folded
		//	result += 1;
		}
		
		
		if(isRoot() != true) return result+1;
		return result;
		
	}
	

	public void check() {
		switch( status() ) {
		case CellNode.FCHECK :
			this.status(NCHECK);
			detParentOfF2N();
			checkChildren(NCHECK);
			break;
		
		case CellNode.HCHECK :
			this.status(FCHECK);
			detParentOfH2F();
			checkChildren(FCHECK);
			break;
			
		case CellNode.NCHECK :
			this.status(FCHECK);
			detParentOfN2F();
			checkChildren(FCHECK);
			break;
			
		}
	}
	
	private void checkChildren(int willStatus) {
		
		for(int i=0; i< this.children().size(); i++) {
			
			CellNode child = this.children().get(i);
			child.status(willStatus);
			child.checkChildren(willStatus);
			
		}
		
	}
	
	private void detParentOfF2N() {
		CellNode parent = this.parent();
		if(parent.isRoot()) return;
		
		boolean allNCHECK = detAllSiblingsAre(NCHECK);
		
		switch(parent.status()) {
		
		case FCHECK : 	
			if(!allNCHECK) {
				parent.status(HCHECK);
				parent.detParentOfF2H();
			} else {
				parent.status(NCHECK);
				parent.detParentOfF2N();
			}
			
			break;
			
		case HCHECK :
			if(!allNCHECK) {
				parent.status(HCHECK);
			} else {
				parent.status(NCHECK);
				parent.detParentOfH2N();
			}
			break;
			
		default :
		case NCHECK :
			break;
		}
	}
	
	private void detParentOfF2H() {
		CellNode parent = this.parent();
		if(parent.isRoot()) return;
		
		//boolean
		
		switch(parent.status()) {
		case FCHECK :
			parent.status(HCHECK);
			parent.detParentOfF2H();
			break;
			
		case HCHECK :
			parent.status(HCHECK);
			break;
			
		default :
		case NCHECK :
			break;
		}
	}
	
	private void detParentOfH2F() {
		CellNode parent = this.parent();
		if(parent.isRoot()) return;
		
		boolean allFCHECK = detAllSiblingsAre(FCHECK);
		
		switch(parent.status()) {
		case HCHECK :
			if(allFCHECK) {
				parent.status(FCHECK);
				parent.detParentOfH2F();
			} else {
				parent.status(HCHECK);
			}
			break;
		
		default :
		case FCHECK :
		case NCHECK :
			break;
		}
		
	}
	
	private void detParentOfH2N() {
		this.detParentOfF2N();
	}
	
	private void detParentOfN2F() {
		CellNode parent = this.parent();
		if(parent.isRoot()) return;
		
		boolean allFCHECK = detAllSiblingsAre(FCHECK);
		
		switch(parent.status()) {
		case HCHECK :
			if(allFCHECK) {
				parent.status(FCHECK);
				parent.detParentOfH2F();
			} else {
				parent.status(HCHECK);
			}
			break;
			
		case NCHECK :
			if(allFCHECK) {
				parent.status(FCHECK);
				parent.detParentOfN2F();
			} else {
				parent.status(HCHECK);
				parent.detParentOfN2H();
			}
			break;
			
		default :
		case FCHECK :
			break;
		}
	}
	
	private void detParentOfN2H() {
		CellNode parent = this.parent();
		if(parent.isRoot()) return;
		
		//boolean
		
		switch(parent.status()) {
		case NCHECK :
			parent.status(HCHECK);
			parent.detParentOfN2H();
			break;
			
		default :
		case FCHECK :
		case HCHECK :
			break;
		}
	}
	
	private boolean detAllSiblingsAre(int status) {
		ArrayList<CellNode> sibs = this.parent().children();
		
		boolean result = true;
		for(int i=0; i<sibs.size(); i++) {
			CellNode sib = sibs.get(i);
			if(sib.indexPath().equals(this.indexPath())) continue;
			if(sib.status() != status) {
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	/*
	private void detParentStatusChecked() {
		boolean flag = false;
		ArrayList<CellNode> sibs = this.parent().children();
		
		for(int i=0; i<sibs.size(); i++) {
			CellNode sib = sibs.get(i);
			if(sib.indexPath().equals(this.indexPath())) continue;
			if(sib.status() != NCHECK) flag = true;
		}
		
		if(flag == true) {
			if(this.isRoot() == true) return;
			Condition c = new Condition().isRoot(false);
			ArrayList<CellNode> _ps = this.parents(c);
			for(int i=0; i<_ps.size(); i++) {
				_ps.get(i).status(HCHECK);
			}
			this.status(NCHECK);
		} else {
			if(this.isRoot() == true) return;
			this.status(NCHECK);
			this.parent().detParentStatusChecked();
		}
	}
	
	private void detParentStatusUnchecked() {
		boolean flag = false;
		ArrayList<CellNode> sibs = this.parent().children();
		
		for(int i=0; i<sibs.size(); i++) {
			CellNode sib = sibs.get(i);
			if(sib.indexPath().equals(this.indexPath())) continue;
			if(sib.status() != FCHECK) flag = true;
		}
		
		if(flag == true) {
			if(this.isRoot() == true) return;
			Condition c = new Condition().isRoot(false);
			ArrayList<CellNode> _ps = this.parents(c);
			for(int i=0; i<_ps.size(); i++) {
				_ps.get(i).status(HCHECK);
			}
			this.status(FCHECK);
		} else {
			if(this.isRoot() == true) return;
			this.parent().detParentStatusUnchecked();
			this.status(FCHECK);
		}
	}
	*/
	public static ArrayList<String> collect(CellNode rootNode) { 
		
		/*
		
		HashSet<String> _result = new HashSet<String>();
		
		Condition uc = new Condition().status(FCHECK).type(CN_USER);
		ArrayList<CellNode> selectedUsers = rootNode.find(uc);
		
		for(int i=0; i<selectedUsers.size(); i++) {
			_result.add(selectedUsers.get(i).idx());
		}
		
		Condition dc = new Condition().status(FCHECK).type(CN_DEPARTMENT);
		ArrayList<CellNode> _selectedDeps = rootNode.find(dc);
		ArrayList<CellNode> selectedDeps = new ArrayList<CellNode>();
		
		for(int i=0; i<_selectedDeps.size(); i++) {
			if(_selectedDeps.get(i).children() != null && _selectedDeps.get(i).children().size() >0)
				selectedDeps.add(_selectedDeps.get(i));
		}
	
		
		for( int i=0; i<selectedDeps.size(); i++) {
			ArrayList<User> _us = MemberManager.sharedManager().getDeptMembers(selectedDeps.get(i).idx(), true);
			for(int j=0; j<_us.size(); j++) {
				_result.add(_us.get(j).idx);
			}
		}
		
		ArrayList<String> result = new ArrayList<String>();
		Iterator<String> itr = _result.iterator();
		while(itr.hasNext()) {
			result.add(itr.next());
		}
		
		*/
		
		
		/*
		ArrayList<String> result = new ArrayList<String>();
		
		Condition c = new Condition().hasChildren(false).status(FCHECK);
		ArrayList<CellNode> selectedNodes = rootNode.find(c);
		
		for(int i=0; i<selectedNodes.size(); i++) {
			CellNode node = selectedNodes.get(i);
			if(node.type() == CellNode.CN_DEPARTMENT) {
				ArrayList<User> users = MemberManager.sharedManager().getDeptMembers(node.idx(), true);
				for(int ui=0; ui<users.size(); i++) {
					boolean isExists = false;
					User user = users.get(ui);
					
					for(int j=0; j< result.size(); j++) {
						if( result.get(j).equalsIgnoreCase(user.idx) ) {
							isExists = true;
							break; // ui index를 가진 다음 for 문으로 간다.
						}
					}
					
					if(isExists != true)
						result.add(user.idx);
				}
			} else {
				boolean isExists = false;
				
				for(int j=0; j< result.size(); j++) {
					if( result.get(j).equalsIgnoreCase(node.idx()) ) {
						isExists = true;
						break; // ui index를 가진 다음 for 문으로 간다.
					}
				}
				
				if(isExists != true)
					result.add(node.idx());
				
			}
		}
		*/

		HashSet<String> _result = new HashSet<String>();

		Condition c = new Condition().hasChildren(false).status(FCHECK);
		ArrayList<CellNode> selectedNodes = rootNode.find(c);
		
		for(int ni=0; ni<selectedNodes.size(); ni++) {
			CellNode node = selectedNodes.get(ni);
			if(node.type() == CellNode.CN_DEPARTMENT) {
				ArrayList<User> users = MemberManager.sharedManager().getDeptMembers(node.idx(), true);
				for(int ui=0; ui<users.size(); ui++) {
					_result.add( users.get(ui).idx );
				}
			} else {
				_result.add(node.idx());
			}
		}
			
		
		ArrayList<String> result = new ArrayList<String>();
		Iterator<String> itr = _result.iterator();
		while(itr.hasNext()) {
			result.add(itr.next());
		}
		
		return result;
	}
	
}