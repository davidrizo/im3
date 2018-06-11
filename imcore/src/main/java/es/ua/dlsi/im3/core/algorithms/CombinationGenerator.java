package es.ua.dlsi.im3.core.algorithms;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IProgressObserver;

import java.util.ArrayList;



/**
 * It generates all non repeated combinations of indices
@author drizo
@date 28/11/2008
 **/
public class CombinationGenerator {
	ICombinationGeneratorPrunner prunner;
	private int startFrom;
	private IProgressObserver observer;
	long debugMaxPossibilities;
	long debugPosibility;
	
	public CombinationGenerator() {
	    
	}

	public CombinationGenerator(IProgressObserver observer) {
	    this.observer = observer;
	    debugPosibility = 0;
	}
	
	/**
	 * each position contains the maximum value for this position
	 * e.g, for elements = {2,3,2} the valid combinations are: {000,001,010,011,020,021,100,101,110,111,120,121}
	 * This problem is similar to the N-queens in backtracking. In this one, the rows are the element, and the column is the valid elements for each position
	 * @param elements
	 * @throws IM3Exception 
	 */
	private void generateAllCombinations(ArrayList<int[]> solutions, int [] currentSolution, int ielement, int [] elements, boolean rejectRepeatedIndexes, boolean sortedIndexes) throws IM3Exception {
		if (ielement < elements.length) {
			for (int i=startFrom; i<elements[ielement]; i++) {
				if (observer != null) {
				    observer.setCurrentProgress(debugPosibility++, debugMaxPossibilities);
				}			    
				currentSolution[ielement] = i;
				boolean prune = false;
				if (rejectRepeatedIndexes || sortedIndexes) {
					for (int ii=0; !prune && ii<ielement; ii++) {
							if (currentSolution[ii] != -1 && rejectRepeatedIndexes && currentSolution[ii] == currentSolution[ielement]) {
								prune = true;
							}
							if (sortedIndexes && currentSolution[ii] > currentSolution[ielement]) {
								prune = true;
							}
					}
				}
				if (!prune && prunner != null) {
					prune = !prunner.isValid(currentSolution, ielement);
				}
				if (!prune) {
					generateAllCombinations(solutions, currentSolution, ielement+1, elements, rejectRepeatedIndexes, sortedIndexes);
				}
			}
		} else {
			solutions.add(currentSolution.clone());
		}
	}
	
	/**
	 * each position contains the maximum value for this position
	 * e.g, for elements = {2,3,2} the valid combinations are: {000,001,010,011,020,021,100,101,110,111,120,121}
	 * This problem is similar to the N-queens in backtracking. In this one, the rows are the element, and the column is the valid elements for each position
	 * @param elements
	 * @param prunner Used to prune subtrees in backtracking process
	 * @throws IM3Exception 
	 */
	public ArrayList<int[]> generateAllCombinations(int [] elements, ICombinationGeneratorPrunner prunner) throws IM3Exception {
		computeDebugMaxPossibilities(elements);
		this.prunner = prunner;
		this.startFrom=0;
		ArrayList<int[]> solutions = new ArrayList<int[]>();
		int [] currentSolution = new int[elements.length];
		for (int i=0; i<currentSolution.length; i++) {
			currentSolution[i]=-1;
		}
		/*System.out.println(elements.length + "\n\t");
		for (int i=0; i<elements.length; i++) {
			System.out.println(elements[i] + "\t");
		}*/
		generateAllCombinations(solutions, currentSolution, 0, elements, false, false);
		return solutions;
	}
	/**
	 * each position contains the maximum value for this position
	 * It does not allow for repeated indexes
	 * @param elements
	 * @param prunner Used to prune subtrees in backtracking process
	 * @throws IM3Exception 
	 */
	public ArrayList<int[]> generateAllNonRepeatedCombinations(int [] elements, ICombinationGeneratorPrunner prunner) throws IM3Exception {
	    computeDebugMaxPossibilities(elements);
		this.prunner = prunner;
		ArrayList<int[]> solutions = new ArrayList<int[]>();
		this.startFrom=0;
		int [] currentSolution = new int[elements.length];
		for (int i=0; i<currentSolution.length; i++) {
			currentSolution[i]=-1;
		}
		
		/*System.out.println(elements.length + "\n\t");
		for (int i=0; i<elements.length; i++) {
			System.out.println(elements[i] + "\t");
		}*/
		generateAllCombinations(solutions, currentSolution, 0, elements, true, false);
		return solutions;
	}
	
	/**
	 * each position contains the maximum value for this position
	 * It does not allow for repeated indexes. It may output partial results (-1 in the result int[] means empty)
	 * @param elements
	 * @param prunner Used to prune subtrees in backtracking process
	 * @throws IM3Exception 
	 */
	public ArrayList<int[]> generateAllNonRepeatedGroups(int [] elements, ICombinationGeneratorPrunner prunner) throws IM3Exception {
	    computeDebugMaxPossibilities(elements);
		this.prunner = prunner;
		//this.startFrom=-1;
		this.startFrom=-1;
		ArrayList<int[]> solutions = new ArrayList<int[]>();
		int [] currentSolution = new int[elements.length];
		for (int i=0; i<currentSolution.length; i++) {
			currentSolution[i]=-1;
		}
		
		/*System.out.println(elements.length + "\n\t");
		for (int i=0; i<elements.length; i++) {
			System.out.println(elements[i] + "\t");
		}*/
		generateAllCombinations(solutions, currentSolution, 0, elements, true, false); 
//		generateAllCombinations(solutions, currentSolution, 0, elements, true, true);// comentado 20110618
		return solutions;
	}

	/*public ArrayList<int[]> generateAllSortedCombinations(int[] elements,
			ICombinationGeneratorPrunner prunner2) throws IM3Exception {
		this.prunner = prunner;
		this.startFrom=0;
		ArrayList<int[]> solutions = new ArrayList<int[]>();
		int [] currentSolution = new int[elements.length];
		for (int i=0; i<currentSolution.length; i++) {
			currentSolution[i]=-1;
		}
		System.out.println(elements.length + "\n\t");
		for (int i=0; i<elements.length; i++) {
			System.out.println(elements[i] + "\t");
		}
		generateAllCombinations(solutions, currentSolution, 0, elements, false, true);
		return solutions;	
	}*/

    private void computeDebugMaxPossibilities(int [] elements) {
	if (observer != null) {
	    debugMaxPossibilities = 1;
	    for (int maxvalues : elements) {
		debugMaxPossibilities *= (long)maxvalues;
		observer.logText("Number of possibilities tmp" + debugMaxPossibilities);
	    }
	    
	    observer.logText("Number of possibilities " + debugMaxPossibilities);
	}
    }
	
}
