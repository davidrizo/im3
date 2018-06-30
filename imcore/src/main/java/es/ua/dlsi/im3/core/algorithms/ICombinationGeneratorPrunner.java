package es.ua.dlsi.im3.core.algorithms;

import es.ua.dlsi.im3.core.IM3Exception;

/**
@author drizo
@date 11/06/2009
 **/
public interface ICombinationGeneratorPrunner {

	boolean isValid(int[] currentSolution, int ielement);

}
