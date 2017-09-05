/*
 * Created on 05-mar-2004
 * David Rizo Valero
 * drizo@dlsi.ua.es
 */
package es.ua.dlsi.im3.core.adt.tree;

import java.util.Iterator;

import es.ua.dlsi.im3.core.adt.IADT;


/**
 * Tree that can be traversed
 * @author David Rizo Valero
 * @param <KeyType>
 */
public interface ITree<KeyType extends ITreeLabel> extends IADT {

    /**
     * @return True when has no children
     */
    boolean isLeaf();

    /**
     * @return Key of the tree
     */
    KeyType getLabel();
    
    /**
     * @return An iterator to children collection
     */
    Iterator<? extends ITree<KeyType>> getChildrenIterator();

    /**
     * @return The number of children
     */
    int getNumChildren();

	ITree<KeyType> getPropagatedFrom();

	/**
	 * Unique root node number
	 * @return
	 */
	int getNodeNumber();

}
