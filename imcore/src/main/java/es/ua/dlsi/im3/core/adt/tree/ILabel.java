/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.adt.tree;

/**
 * @author david
 */
public interface ILabel {
	/**
	 * Returns the label in string format
	 * @return
	 */
    String getStringLabel();
	/**
	 * Used to print the instrumentKey in GraphvizTreeExporter
	 * @return
	 * @throws Exception 
	 */
    String getColor() throws Exception;
	ILabel clone();
}
