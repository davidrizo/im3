package es.ua.dlsi.im3.core.score;
/**
@author drizo
@date 12/09/2011
 **/
public enum ScaleMembershipDegree {
	notbelongs (0.0f),
	low (0.1f),
	medium (0.5f),
	high (1.0f);
	
	/**
	 * Numeric tempo that conceptually represents the membership degree
	 */
	float value;
	private ScaleMembershipDegree(float degreeValue) {
		this.value = degreeValue;
	}
	/**
	 * @return the degree
	 */
	public final float getValue() {
		return value;
	}
	
	
}
