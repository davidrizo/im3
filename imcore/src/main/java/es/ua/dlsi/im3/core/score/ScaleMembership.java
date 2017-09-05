package es.ua.dlsi.im3.core.score;

/**
@author drizo
@date 12/09/2011
**/
public class ScaleMembership {
	/**
	 * e.g. neapolitan
	 */
	String label; 
	ScaleMembershipDegree degree;
	/**
	 * Optionally set to know the name of the scales it belongs
	 */
	Scale scale;
	/**
	 * @return the scale
	 */
	public final Scale getScale() {
		return scale;
	}
	/**
	 * @param label
	 * @param degree
	 */
	public ScaleMembership(String label, ScaleMembershipDegree degree) {
		super();
		this.label = label;
		this.degree = degree;
	}
	/**
	 * @return the label
	 */
	public final String getLabel() {
		return label;
	}
	/**
	 * @return the degree
	 */
	public final ScaleMembershipDegree getDegree() {
		return degree;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((degree == null) ? 0 : degree.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScaleMembership other = (ScaleMembership) obj;
		if (degree != other.degree)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScaleMembership [label=" + label + ", degree=" + degree + " in scale " + scale + "]";
	}
	
	/**
	 * It builds a new scale membership given this as template and assigning the scale parameter
	 * @param scale
	 * @return
	 */
	public ScaleMembership buildFromThis(Scale scale) {
		ScaleMembership sm = new ScaleMembership(label, degree);
		sm.scale = scale;
		return sm;
	}
	
	public static ScaleMembership max(ScaleMembership a, ScaleMembership b) {
	    if (a.degree.getValue() >= b.getDegree().getValue()) {
		return a;
	    } else {
		return b;
	    }
	}
}