package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
@author drizo
@date 05/03/2012
 **/
public class NoKeyException extends IM3Exception {

	public NoKeyException(Exception e) {
		super(e);
	}
	public NoKeyException() {
		super("No instrumentKey in song");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4051526016676099464L;

}
