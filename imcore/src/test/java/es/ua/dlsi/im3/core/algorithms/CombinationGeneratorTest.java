package es.ua.dlsi.im3.core.algorithms;

import static org.junit.Assert.*;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

/**
 @author drizo
 @date 28/11/2008
 **/
public class CombinationGeneratorTest {

    @Test
    public final void testGenerateAllCombinations() throws Exception {
        int [] elements = {2,3,2};
        CombinationGenerator cg = new CombinationGenerator();
        ArrayList<int[]> solutions =  cg.generateAllCombinations(elements, null);
        String expected = "000,001,010,011,020,021,100,101,110,111,120,121";
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<solutions.size(); i++) {
            if (i>0) {
                sb.append(',');
            }
            for (int j=0; j<solutions.get(i).length; j++) {
                sb.append(solutions.get(i)[j]);
            }
        }
        System.out.println(sb.toString());
        assertEquals("All combinations", expected, sb.toString());

    }

    @Test
    public final void testGenerateBestCombinationsNoRepetition() throws Exception {
        ICombinationGeneratorPrunner prunner = new ICombinationGeneratorPrunner(){

            // check if any of the indexes is repeated
            public boolean isValid(int[] currentSolution, int ielement) {
                for (int i=0; i<ielement; i++) {
                    if (currentSolution[i] == currentSolution[ielement]) {
                        return false;
                    }
                }
                return true;
            }
        };


        int [] elements = {2,3,2};
        CombinationGenerator cg = new CombinationGenerator();
        ArrayList<int[]> solutions =  cg.generateAllCombinations(elements, prunner);
        String expected = "021,120";
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<solutions.size(); i++) {
            if (i>0) {
                sb.append(',');
            }
            for (int j=0; j<solutions.get(i).length; j++) {
                sb.append(solutions.get(i)[j]);
            }
        }
        System.out.println(sb.toString());
        assertEquals("All combinations", expected, sb.toString());


        // with rejection
        cg = new CombinationGenerator();
        solutions =  cg.generateAllNonRepeatedCombinations(elements, null);
        sb = new StringBuffer();
        for (int i=0; i<solutions.size(); i++) {
            if (i>0) {
                sb.append(',');
            }
            for (int j=0; j<solutions.get(i).length; j++) {
                sb.append(solutions.get(i)[j]);
            }
        }
        System.out.println(sb.toString());
        assertEquals("All combinations (non repeated)", expected, sb.toString());

    }

    @Test
    public final void testGenerateBestGroup() throws Exception {
        ICombinationGeneratorPrunner prunner = new ICombinationGeneratorPrunner(){

            // it does not allow for odd indexes
            public boolean isValid(int[] currentSolution, int ielement) {
                //return currentSolution[ielement] % 2 == 0;
                return true;
            }
        };

        int [] elements = {3,3,3};
        CombinationGenerator cg = new CombinationGenerator();
        ArrayList<int[]> solutions =  cg.generateAllNonRepeatedGroups(elements, prunner);
        //String expected = "-1-1-1,-1-10,-1-11,-1-12,-101,-102,-112,012";
        //TODO Ver si esto deberia ser asi  (ver calculo de acordes)
        String expected = "-1-1-1,-1-10,-1-11,-1-12,-10-1,-101,-102,-11-1,-110,-112,-12-1,-120,-121,0-1-1,0-11,0-12,01-1,012,02-1,021,1-1-1,1-10,1-12,10-1,102,12-1,120,2-1-1,2-10,2-11,20-1,201,21-1,210";
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<solutions.size(); i++) {
            if (i>0) {
                sb.append(',');
            }
            for (int j=0; j<solutions.get(i).length; j++) {
                sb.append(solutions.get(i)[j]);
            }
        }
        System.out.println(sb.toString());
        assertEquals("All combinations", expected, sb.toString());


    }

}
