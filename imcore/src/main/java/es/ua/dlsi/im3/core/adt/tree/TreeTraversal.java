/*
 * Created on 06-mar-2004
 * David Rizo Valero
 * drizo@dlsi.ua.es
 */
package es.ua.dlsi.im3.core.adt.tree;

import java.util.Iterator;


/**
 * Algorithms to traverse tree 
 * @author David Rizo Valero
 */
public final class TreeTraversal {
 	/**
 	 * Traversal in preorder
 	 * @throws TreeException
 	 */
	public static void preorderTraversal(ITree<?> tree, ITraversalVisitor visitor) throws TreeException {
		preorderTraversal(tree, visitor, 0, Integer.MAX_VALUE);
	}

 	/**
 	 * Traversal in preorder
 	 * @throws TreeException
 	 */
	public static void preorderTraversal(ITree<?> tree, ITraversalVisitor visitor, int currentLevel, int maxLevel ) throws TreeException {
		if (currentLevel <= maxLevel) {
		    visitor.startTree();
		    visitor.visitKey(tree.getLabel());
			//System.out.print(this.key.visit());
			for (Iterator<?> iter = tree.getChildrenIterator(); iter.hasNext();) {
				ITree<?> element = (ITree<?>) iter.next();
				preorderTraversal(element, visitor, currentLevel +1, maxLevel);
			}			
			visitor.endTree();
		}
	}
	
	/**
	 * Traversal in postorder
	 * @throws TreeException
	 */
	public static void postorderTraversal(ITree<?> tree, ITraversalVisitor visitor) throws TreeException {
		    visitor.startTree();
			for (Iterator<?> iter = tree.getChildrenIterator(); iter.hasNext();) {
				ITree<?> element = (ITree<?>) iter.next();
				postorderTraversal(element, visitor);
			}			
			visitor.visitKey(tree.getLabel());
			visitor.endTree();
	}
	
	/**
	 * Traversal visiting only the leaves
	 * @throws TreeException
	 */
	public static void leavesTraversal(ITree<?> tree, ITraversalVisitor visitor) throws TreeException {		
		    if (tree.getNumChildren() == 0) {
				visitor.visitKey(tree.getLabel());
		    } else {
			    for (Iterator<?> iter = tree.getChildrenIterator(); iter.hasNext();) {
					ITree<?> element = (ITree<?>) iter.next();
					leavesTraversal(element, visitor);
				}			
		    }
	}
	
}
