/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.adt.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.ua.dlsi.im3.core.adt.IADT;
import es.ua.dlsi.im3.core.IDGenerator;

/**
 * @author david
 * @param <LabelType>
 */
public class Tree<LabelType extends ITreeLabel> implements ITree<LabelType>, Comparable<Tree<LabelType>>, IADT {
	private long ID;
	/**
	 * @return the nextNodeNumber
	 */
	protected static final int getNextNodeNumber() {
		return nextNodeNumber++;
	}

	/**
	 * Next node number
	 */
	private static int nextNodeNumber = 0;

	/**
	 * @return the nodeNumber
	 */
	@Override
	public final int getNodeNumber() {
		return nodeNumber;
	}

	/**
	 * Unique GraphNode number
	 */
	private final int nodeNumber;
	/**
	 * EMPTY string
	 */
	//private static final String EMPTY = "";
	/**
	 * Inner node label prefix
	 */
	private static final String INNER = "I";
	/**
	 * Key of the tree
	 */
	private LabelType label;

	/**
	 * Parent tree
	 */
	protected Tree<LabelType> parent;
	/**
	 * Where the label comes from
	 */
	protected Tree<LabelType> propagatedFrom = null;

	protected Tree<LabelType> leftSibling;

	/**
	 * Calculated with preorderTraversal
	 */
	int preorderIndex = -1;
	/**
	 * Calculated with postOrderTraversal
	 */
	int postorderIndex = -1;
	/**
	 * Calculated with inOrderTraversal
	 */
	int inorderIndex = -1;

	//FRACTIONS IView view;

	/**
	 * @return the postorderIndex
	 */
	public final int getPostorderIndex() {
		return postorderIndex;
	}

	/**
	 * @return the preorderIndex
	 * @throws TreeException
	 */
	public final int getPreorderIndex() throws TreeException {
		if (preorderIndex == -1) {
			throw new TreeException("preorder not calculated, use before method preorderTraversal");
		}
		return preorderIndex;
	}

	public boolean isPreorderCalculated() {
		return preorderIndex != -1;
	}

	/**
	 * @return the preorderIndex
	 * @throws TreeException
	 */
	public final int getInorderIndex() throws TreeException {
		if (inorderIndex == -1) {
			throw new TreeException("inorderIndex not calculated, use before method inorderTraversal");
		}
		return inorderIndex;
	}

	/**
	 * @return the leftSibling
	 */
	public final Tree<LabelType> getLeftSibling() {
		return leftSibling;
	}

	/**
	 * @param aleftSibling
	 *            the leftSibling to set
	 */
	public final void setLeftSibling(Tree<LabelType> aleftSibling) {
		this.leftSibling = aleftSibling;
	}

	/**
	 * Children of the tree
	 */
	private ArrayList<Tree<LabelType>> children;

	/**
	 * Level of this tree. The root will have level 0
	 */
	private int level;
	/**
	 * Height from bottom of this tree. The leaves will have height 0
	 */
	private int heightFromBottom = -1;
	/**
	 * Contains the size of the tree, keep in mind that this tempo is a temporal
	 * one, and each time a new node is added, this tempo should be calculated
	 */
	private int size = -1;
	/**
	 * The index of the subtree into the whole tree, using a postorder
	 */
	private int index;
	/**
	 * Used to recursive compute the index
	 */
	private static int currentIndex;

	/**
	 * It creates a tree with the given label
	 *
	 * @param label
	 */
	public Tree(LabelType label) {
		this.nodeNumber = getNextNodeNumber();
		this.label = label;
		children = new ArrayList<>();
		ID = IDGenerator.getID();
	}
	
	public long getID() {
		return ID;
	}

	/**
	 * Gets the number of children
	 *
	 * @return Number of children
	 */
	@Override
	public int getNumChildren() {
		return children.size();
	}

	/**
	 * Selector
	 *
	 * @return Key of the tree
	 */
	@Override
	public LabelType getLabel() {
		return label;
	}

	/**
	 * Selector
	 *
	 * @return Level of the tree. Root will have level 0
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Setter
	 *
	 * @param l
	 *            Label of the tree
	 */
	public void setLabel(LabelType l) {
		this.label = l;
	}

	/**
	 * Adds a child to a tree
	 *
	 * @param achild
	 *            The child to be added
	 * @return The position where the child is inserted starting from 0
	 * @throws TreeException
	 *             Exception when tree is empty
	 */
	public int addChild(Tree<LabelType> achild) throws TreeException {
		if (achild == this) {
			throw new TreeException("Cannot add a tree as a child of itself");
		}

		Tree<LabelType> t = achild;
		children.add(t);
		t.parent = this;
		achild.setLevel(level + 1);
		//System.out.println("Level: " + level + ", child: " + achild.level + "____" + this);
		return children.size() - 1;
	}

	@SuppressWarnings("unchecked")
	public void addChildBefore(Tree<? extends LabelType> insertBeforeChild,
			Tree<? extends LabelType> childToInsert) throws TreeException {		
		Tree<LabelType> t = (Tree<LabelType>) childToInsert;
		for (int i=0; i<children.size(); i++) {
			if (children.get(i) == insertBeforeChild) {
				children.add(i, t);
				childToInsert.setLevel(level+1);
				//System.out.println("Level: " + level + ", child: " + childToInsert.level + "---" + this);
				return;
			}
		}
		throw new TreeException("Cannot find reference child");
	}

	@SuppressWarnings("unchecked")
	public void addChildAfter(Tree<? extends LabelType> insertBeforeChild,
			Tree<? extends LabelType> childToInsert) throws TreeException {		
		Tree<LabelType> t = (Tree<LabelType>) childToInsert;
		for (int i=0; i<children.size(); i++) {
			if (children.get(i) == insertBeforeChild) {
				children.add(i+1, t);
				childToInsert.setLevel(level+1);
				//System.out.println("Level: " + level + ", child: " + childToInsert.level + "...."+this);
				return;
			}
		}
		throw new TreeException("Cannot find reference child");
	}
	
	/**
	 * This method leaves the tree empty
	 */
	public void clear() {
		children.clear();
	}

	/**
	 * True when does not have children
	 *
	 * @return boolean
	 */
	@Override
	public boolean isLeaf() {
		return children.isEmpty();
	}

	/**
	 * Returns the ith child
	 *
	 * @param i
	 *            Starting from 0
	 * @return Tree
	 */
	public Tree<LabelType> getChild(int i) {
		return children.get(i);
	}

	/**
	 * This deletes all children of this tree
	 */
	public void removeChildren() {
		children.clear();
		//// GC children.removeAllElements();
	}

	/**
	 * @return Returns the parent.
	 */
	public Tree<LabelType> getParent() {
		return parent;
	}

	/**
	 * @return 
	 */
	@Override
	public Iterator<Tree<LabelType>> getChildrenIterator() {
		return children.iterator();
	}

	/**
	 * @return True if it has not parent
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * Return number of nodes
	 *
	 * @return
	 */
	public int computeSizeAndIndex() {
		if (isRoot()) {
			currentIndex = 1; // init the index
			computeHeightFromBottom();
		}
		size = 1;
		for (Iterator<Tree<LabelType>> iter = this.getChildrenIterator(); iter.hasNext();) {
			Tree<LabelType> child = iter.next();
			size += child.computeSizeAndIndex();
		}
		this.index = currentIndex;
		currentIndex++;

		return size;
	}

	private void computeHeightFromBottom() {
		if (this.isLeaf()) {
			this.heightFromBottom = 0;
		} else {
			int max = 0;
			for (int i = 0; i < this.getNumChildren(); i++) {
				Tree<LabelType> child = this.getChild(i);
				child.computeHeightFromBottom();
				max = Math.max(max, child.heightFromBottom);
			}
			this.heightFromBottom = max + 1;
		}

	}

	/**
	 * Return the size that has been calculated using the computeSize The
	 * computeSize must be calculated first
	 *
	 * @return
	 * @throws TreeException
	 */
	public int getPrecomputedSize() throws TreeException {
		if (size == -1) {
			throw new TreeException("Call computeSize first before getPrecomuptedSize");
		}
		return size;
	}

	/**
	 * For debugging purposes, print a preorder traversal of the tree. It prints
	 * the tree with escape sequences
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(this.getLabel().getStringLabel());
		for (int i = 0; i < this.getNumChildren(); i++) {
			sb.append(this.getChild(i).toString());
		}
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Returns the string version of the root label
	 *
	 * @param differenciateInnerNodes
	 *            If true, the inner nodes are printed as I<x>, where <x> is the
	 *            node rank
	 * @return
	 */
	public String getRootLabel(boolean differenciateInnerNodes) {
		if (differenciateInnerNodes && !this.isLeaf()) {
			StringBuilder sb = new StringBuilder();
			sb.append(INNER);
			sb.append(this.getNumChildren());
			sb.append(':');
			sb.append(this.getLabel().getStringLabel());
			return sb.toString();
		} else {
			return this.getLabel().getStringLabel();
		}
	}

	/**
	 * @param differenciateInnerNodes
	 *            If true, the inner nodes are printed as I<x>, where <x> is the
	 *            node rank
	 * @return
	 */
	public String toString(boolean differenciateInnerNodes) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(this.getRootLabel(differenciateInnerNodes));
		for (int i = 0; i < this.getNumChildren(); i++) {
			sb.append(this.getChild(i).toString(differenciateInnerNodes));
		}
		sb.append(')');
		return sb.toString();
	}

	/**
	 * @return A string in the form <rootlabel>(<child 1>,...<child N>)
	 */
	public String toFunctionalString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getRootLabel(false));
		if (this.getNumChildren() > 0) {
			sb.append('(');
			for (int i = 0; i < this.getNumChildren(); i++) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append(this.getChild(i).toFunctionalString());
			}
			sb.append(')');
		}
		return sb.toString();
	}

	/**
	 * To use it the computeSizeAndIndex must be called first
	 *
	 * @return Returns the index starting from 1
	 */
	public final int getIndex() {
		return index;
	}

	/**
	 * @param arg
	 * @see es.ua.dlsi.im.pr.Prototype#equals(java.lang.Object)
	 */
	public boolean equals(Tree<LabelType> arg) {
		Tree<LabelType> other = arg;
		if (!this.label.equals(other.label)) {
			return false;
		} else if (this.getNumChildren() != other.getNumChildren()) {
			return false;
		} else {
			int nchild = this.getNumChildren();
			for (int i = 0; i < nchild; i++) {
				if (!this.getChild(i).equals(other.getChild(i))) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Get the maximum depth
	 *
	 * @return
	 */
	public int getDepth() {
		int max = 0;
		int nchild = this.getNumChildren();
		for (int i = 0; i < nchild; i++) {
			int d = this.getChild(i).getDepth();
			if (d > max) {
				max = d;
			}
		}
		return max + 1;
	}

	/**
	 * Remove the i-th child
	 *
	 * @param i
	 */
	protected void removeChild(int i) {
		this.children.remove(i); // URGENT Actualizar profundidad guardada ....
	}

	/**
	 *
	 * @param levels
	 *            Number of levels to leave
	 */
	public void prune(int levels) {
		// TODO Auto-generated method stub
		prune(levels, 0);
	}

	private void prune(int levels, int currentLevel) {
		if (currentLevel < levels) {
			for (int i = 0; i < this.getNumChildren(); i++) {
				this.getChild(i).prune(levels, currentLevel + 1);
			}
		} else {
			children.clear();
		}

	}

	@Override
	public Tree<LabelType> getPropagatedFrom() {
		return propagatedFrom;
	}

	public void setPropagatedFrom(Tree<LabelType> st) {
		this.propagatedFrom = st;
	}

	public void linkSiblings() {
		ArrayList<ArrayList<Tree<LabelType>>> vvs = new ArrayList<>();
		linkSiblingsBuildArrayList(vvs, 0, this); // first build a vector of
													// list of tree for each
													// level

		// now put the links
		for (int lvl = 0; lvl < vvs.size(); lvl++) {
			ArrayList<Tree<LabelType>> vlevel=vvs.get(lvl);
			for (int j = 1; j < vlevel.size(); j++) {
				Tree<LabelType> pt = vlevel.get(j - 1);
				Tree<LabelType> t = vlevel.get(j);
				t.setLeftSibling(pt);
				// //System.out.println("Linking " + t.toString() +
				// "["+t.hashCode() + "]"+ " to " + pt.toString() +
				// "["+pt.hashCode() + "]");
			}
		}
	}

	private void linkSiblingsBuildArrayList(ArrayList<ArrayList<Tree<LabelType>>> vvs, int level, Tree<LabelType> st) {
		if (st != null) {
			if (vvs.size() <= level) {
				vvs.add(level, new ArrayList<Tree<LabelType>>());
			}
			vvs.get(level).add(st);
			for (int i = 0; i < st.getNumChildren(); i++) {
				Tree<LabelType> child = st.getChild(i);
				linkSiblingsBuildArrayList(vvs, level + 1, child);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Tree<LabelType> clone() {
		Tree<LabelType> t = this.createTree();
		t.index = index;
		if (label != null) {
			t.label = (LabelType) label.clone();
		}
		t.setLevel(level);
		t.size = size;
		for (int i = 0; i < this.getNumChildren(); i++) {
			Tree<LabelType> child = this.getChild(i);
			Tree<LabelType> c = child.clone();
			c.parent = t;
			t.children.add(c);
			if (this.propagatedFrom == child) {
				t.setPropagatedFrom(c);
			}
		}

		return t;
	}

	/**
	 * This should be used to mantain all levels updated
	 * @param level
	 */
	private void setLevel(int level) {
		for (int i = 0; i < this.getNumChildren(); i++) {
			Tree<LabelType> child = this.getChild(i);
			child.setLevel(level+1);
		}
		this.level = level;
	}

	protected Tree<LabelType> createTree() {
		return new Tree<LabelType>(null);
	}

	/**
	 * Possition of the child in the vector of children of its parent
	 *
	 * @return 0 if it is root
	 * @throws es.ua.dlsi.im3.core.adt.tree.TreeException
	 */
	public int getChildPossition() throws TreeException {
		if (this.isRoot()) {
			return 0;
		} else {
			for (int i = 0; i < parent.getNumChildren(); i++) {
				if (this == parent.getChild(i)) {
					return i;
				}
			}
		}

		throw new TreeException("Tree not child of its parent");
	}

	public void constructTreeLeavesList(ArrayList<Tree<LabelType>> leaves) {
		if (this.isLeaf()) {
			leaves.add(this);
		} else {
			for (int i = 0; i < this.getNumChildren(); i++) {
				this.getChild(i).constructTreeLeavesList(leaves);
			}
		}
	}

	/*
	 * public void levelOrderTraversal(ArrayList<Tree> result) { int depth =
	 * this.getDepth(); for (int d=0; d<depth; d++) { this.levelorderAux(d,
	 * result); } }
	 * 
	 * private void levelorderAux(int d, ArrayList<Tree> result) { if
	 * (!this.isEmpty()) { if (d == this.getLevel()) { result.add(this); } else
	 * { for (int i=0; i<this.getNumChildren(); i++) {
	 * this.getChild(i).levelorderAux(d, result); } } } }
	 */
	public void levelOrderTraversal(ArrayList<Tree<LabelType>> result) {
		ArrayList<ArrayList<Tree<LabelType>>> vlevels = new ArrayList<>();
		levelorderAux(vlevels);
		for (Iterator<ArrayList<Tree<LabelType>>> iterator = vlevels.iterator(); iterator.hasNext();) {
			ArrayList<Tree<LabelType>> vector = iterator.next();
			result.addAll(vector);
		}
	}

	private void levelorderAux(ArrayList<ArrayList<Tree<LabelType>>> vlevels) {
		int lvl = this.getLevel();
		if (vlevels.size() <= lvl) {
			vlevels.add(new ArrayList<Tree<LabelType>>());
			// vlevels.add(new ArrayList<>());
		}
		vlevels.get(lvl).add(this);
		int n = this.getNumChildren();
		for (int i = 0; i < n; i++) {
			this.getChild(i).levelorderAux(vlevels);
		}
	}

	public void preorderTraversal(ArrayList<Tree<LabelType>> result) {
		// 20091119 if (!this.isEmpty())
		{
			this.preorderIndex = result.size();
			result.add(this);
			for (int i = 0; i < this.getNumChildren(); i++) {
				this.getChild(i).preorderTraversal(result);
			}
		}
	}

	public void postorderTraversal(ArrayList<Tree<LabelType>> result) {
		// 20091119 if (!this.isEmpty())
		{
			for (int i = 0; i < this.getNumChildren(); i++) {
				this.getChild(i).postorderTraversal(result);
			}
			this.postorderIndex = result.size();
			result.add(this);
		}
	}

	public void inorderTraversal(ArrayList<Tree<LabelType>> result) {
		if (this.isLeaf()) {
			// //System.out.println(this.getLabel().toString());
			this.inorderIndex = result.size();
			result.add(this);
		} else {
			int nc = this.getNumChildren();
			for (int i = 0; i < nc; i++) {
				this.getChild(i).inorderTraversal(result);
				if (nc == 1 || i == (nc / 2) - 1) {
					// //System.out.println(this.getLabel().toString());
					this.inorderIndex = result.size();
					result.add(this);
				}
			}
		}
		// result.add(this);
	}

	public int size() throws TreeException {
		return getPrecomputedSize();
	}

	@Override
	public int compareTo(Tree<LabelType> o) {
		return this.hashCode() - o.hashCode();
	}

	/**
	 * Height from bottom of this tree. The leaves will have height 0
	 *
	 * @return
	 * @throws TreeException
	 */
	public int getHeightFromBottom() throws TreeException {
		if (heightFromBottom == -1) {
			throw new TreeException("Call computeSize first before getHeightFromBottom");
		}

		return this.heightFromBottom;
	}

	public void destroy() {
		this.leftSibling = null;
		this.parent = null;
		this.propagatedFrom = null;
		this.removeChildren();
		this.children = null;
	}

	public int getMaxRank() {
		if (this.isLeaf()) {
			return 0;
		} else {
			int n = this.children.size();
			int max = n;
			for (int i = 0; i < n; i++) {
				max = Math.max(max, this.children.get(i).getMaxRank());
			}
			return max;
		}
	}

	/*FRACTIONS public IView getView() {
		return view;
	}

	public void setView(IView view) {
		this.view = view;
	}*/

	public List<Tree<LabelType>> getChildren() {
		return children;
	}

}
