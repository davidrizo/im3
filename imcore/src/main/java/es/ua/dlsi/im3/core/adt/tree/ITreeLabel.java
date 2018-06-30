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
	public String getColor();
	
	public ITreeLabel clone();

    /**
     * Horizontal position for being drawn
     * @return null if it does not have this information
     */
	public Double getPredefinedHorizontalPosition();
}
