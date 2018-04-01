package es.ua.dlsi.im3.analysis.analysis.analyzers.tonal;

import es.ua.dlsi.im3.core.IM3Exception;

/**
@author drizo
@date 07/06/2011
 **/
public class TonalAnalysisException extends IM3Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public TonalAnalysisException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public TonalAnalysisException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public TonalAnalysisException(Throwable arg0) {
		super(arg0);
	}

}
