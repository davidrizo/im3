/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.adt.tree;

/**
 * This instrumentKey will contain a single string
 * @author drizo
 */
public class StringLabel implements ITreeLabel {
	/**
	 * Label
	 */
	protected String label;

	/**
	 * Constructor
	 * @param label
	 * @throws TreeException 
	 */
	public StringLabel(String label) throws TreeException {
		if (label == null) {
			throw new TreeException("Empty label parameter for StringKey");
		}
		this.label = label;
	}

	/**
	 * @return Returns the label.
	 */
	public final String getLabel() {
		return label;
	}

	/**
	 * @param label The label to set.
	 */
	public final void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof StringLabel)) {
			System.err.println("The parameter of StringKey.equals is not a StringKey");
			return false;
		}
		return label.equals(((StringLabel)arg0).getLabel());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.label.toString();
	}
	
	public String getStringLabel() {
		return this.label.toString();
	}

	public String getColor() {
		return null;
	}
	
	public StringLabel clone() {
		try {
			return new StringLabel(this.label);
		} catch (TreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public boolean isEmptyLabel() {
		return label == null;
	}
	
}
