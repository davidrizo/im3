/*
 * Created on 02-mar-2004
 * David Rizo Valero
 * drizo@dlsi.ua.es
 */
package es.ua.dlsi.im3.core.adt.tree;


/**
 * It is called for each one of the possible stages in tree traversal
 * @author David Rizo Valero
 */
public interface ITraversalVisitor {

    /**
     * Start of traversal method on tree
     */
    void startTree();

    /**
     * Key visited
     * @param instrumentKey Tree node instrumentKey
     * @throws TreeException
     */
    void visitKey(ITreeLabel key) throws TreeException;

    /**
     * End of traversal method on tree
     */
    void endTree();

    /**
     * It returns the collected data. It can return null if not implemented
     * @return Any object or null
     */
    Object getCollectedData();
}
