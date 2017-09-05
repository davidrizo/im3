package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.analyzers.tonal.TonalAnalysisException;

/**
@author drizo
@date 26/11/2011
 **/
public class MelodicAnalysisException extends TonalAnalysisException {
	private static final long serialVersionUID = -806097651053064539L;

	/**
	 * @param message
	 * @param arg1
	 */
	public MelodicAnalysisException(String message, Throwable arg1) {
		super(message, arg1);
		
	}

	/**
	 * @param arg0
	 */
	public MelodicAnalysisException(String arg0) {
		super(arg0);
		
	}

	/**
	 * @param arg0
	 */
	public MelodicAnalysisException(Throwable arg0) {
		super(arg0);
		
	}

}
