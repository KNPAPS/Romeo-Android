package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.Util.IndexPath;

public class CellNode extends ArrayList<CellNode> {

	private static final long serialVersionUID = 498188955518204141L;
	public static final int CELLNODE_NULL = -777;
	public static final int CELLNODE_USER = 1;
	public static final int CELLNODE_DEPARTMENT = 2;
	public boolean isRoot = false;
	
	public int type = CELLNODE_NULL;
	private boolean _unfolded;
	private int _index = -1;
	private IndexPath _parentIndexPath = null;
	
	
	public static class Builder {
		private int _type = CELLNODE_NULL;
		private boolean _unfolded;
		private int _index = -1;
		private IndexPath _parentIndexPath = null;
		
		public Builder type(int type) {
			this._type = type;
			return this;
		}
		public Builder unfolded(boolean unfolded) {
			this._unfolded = unfolded;
			return this;
		}
		public Builder index(int index) {
			this._index = index;
			return this;
		}
		public Builder parentIndexPath(IndexPath parentIndexPath) {
			this._parentIndexPath = parentIndexPath;
			return this;
		}
		public CellNode build() {
			CellNode node = new CellNode();
			node.type = this._type;
			node._unfolded = this._unfolded;
			node._index = this._index;
			node._parentIndexPath = this._parentIndexPath;
			return node;
			
		}
	}
	
	public CellNode copy() {
		CellNode newCellNode =  new CellNode.Builder().type(this.type)
									 .parentIndexPath(this._parentIndexPath)
									 .unfolded(this._unfolded)
									 .index(this._index)
									 .build();
		newCellNode.addAll(this);
		
		return newCellNode;
	}
	
	public static CellNode nodeAtIndexPath(CellNode rootNode, IndexPath path) {
		CellNode cn = rootNode;
		IndexPath.Iterator itr = new IndexPath.Iterator(path);
		
		while(itr.hasNextIndex()) {
			cn = cn.get(itr.nextIndex());
		}
		return cn;
	}

	
	public int countIncludeFolded() {
		int result = 0;
		for(int i=0; i< this.size(); i++) {
			result += this.get(i).countIncludeFolded();
		}
		return (result+1);
	}
	
	public int count() {
		int result = 0;
		
		if(isUnfolded() == true) {		  	// UnFolded
			for(int i=0; i< this.size(); i++) {
				result += this.get(i).count();
			}
		} else if(isUnfolded() == false) { 	// Folded
		//	result += 1;
		}
		
		
		if(isRoot != true) return result+1;
		return result;
	}
	
	public boolean isUnfolded() {
		return _unfolded;
	}
	
	public void setUnfolded(boolean unfolded) {
		_unfolded = unfolded;
	}
	
	public IndexPath getIndexPath() {
		int idx = _index;
		
		if(_parentIndexPath == null) {
			return IndexPath.indexPathWithIndex(_index);
		} else {
			return _parentIndexPath.indexPathByAddingIndex(idx);
		}
	}
	
	public void setParentIndexPath(IndexPath path) {
		_parentIndexPath = path;
	}
	public IndexPath getParentIndexPath() {
		return _parentIndexPath;
	}
	public void setIndex(int index) {
		_index = index;
	}
	
}