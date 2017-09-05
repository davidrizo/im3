/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.adt.tree;

/**
 * @author david
 */
public interface ITreeLabel {
	/**
	 * Returns the label in string format
	 * @return
	 */
	public String getStringLabel();
	/**
	 * Used to print the instrumentKey in GraphvizTreeExporter
	 * @return
	 * @throws Exception 
	 */
	public String getColor() throws Exception;
	
	public ITreeLabel clone();
}
