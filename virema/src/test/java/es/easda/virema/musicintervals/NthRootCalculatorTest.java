package es.easda.virema.musicintervals;

import org.junit.Test;

import static org.junit.Assert.*;

public class NthRootCalculatorTest {

    private NthRootCalculator nthRootCalculator = new NthRootCalculator();

    @Test
    public void whenBaseIs125AndNIs3_thenNthRootIs5() {
        Double result = nthRootCalculator.calculate(125.0, 3.0);
        assertEquals(result, (Double) 5.0d);

        Double result2 = nthRootCalculator.calculate(2, 12);
        assertEquals(result2, (Double) 1.0594630943592953);

    }
}
