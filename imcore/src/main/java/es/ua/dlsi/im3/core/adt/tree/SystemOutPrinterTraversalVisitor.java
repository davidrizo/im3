/*
 * Created on 02-mar-2004
 * David Rizo Valero
 * drizo@dlsi.ua.es
 */
package es.ua.dlsi.im3.core.adt.tree;


/**
 * Prints in standard output the tree separating trees by parenthesis
 * @author David Rizo Valero
 */
public class SystemOutPrinterTraversalVisitor implements ITraversalVisitor {

    /**
     * @see es.ua.dlsi.tree.ITraversalVisitor#startTree()
     */
    public void startTree() {
        System.out.println('(');
    }

    /**
     * @see es.ua.dlsi.tree.ITraversalVisitor#visitKey(es.ua.dlsi.tree.IKey)
     */
    public void visitKey(ITreeLabel key) {
        System.out.println(key.toString());
    }

    /**
     * @see es.ua.dlsi.tree.ITraversalVisitor#endTree()
     */
    public void endTree() {
        System.out.println(')');
    }

    /**
     * @see es.ua.dlsi.tree.ITraversalVisitor#getCollectedData()
     */
    public Object getCollectedData() {
        return null; // we don't implement it
    }

}
