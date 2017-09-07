package es.ua.dlsi.im3.core.score.mensural.meters;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeSignatureMensuralTest {
    @Test
    public void testDurations() throws Exception {
        // See La Polifonia Clásica. Samuel Rubio. Page 28 (Interpretación de Tirabassi)
        // In any case, the importance of the durations is the relative durations of the figures, not the absolute one
        TempusPerfectumCumProlationeImperfecta pm = new TempusPerfectumCumProlationeImperfecta();
        assertEquals(2, pm.getSemibreveDuration().intValue());
        assertEquals(6, pm.getBreveDuration().intValue());

        TempusPerfectumCumProlationePerfecta pM = new TempusPerfectumCumProlationePerfecta();
        assertEquals(3, pM.getSemibreveDuration().intValue());
        assertEquals(9, pM.getBreveDuration().intValue());


        TempusImperfectumCumProlationeImperfecta im = new TempusImperfectumCumProlationeImperfecta();
        assertEquals(2, im.getSemibreveDuration().intValue());
        assertEquals(4, im.getBreveDuration().intValue());


        TempusImperfectumCumProlationePerfecta iM = new TempusImperfectumCumProlationePerfecta();
        assertEquals(3, iM.getSemibreveDuration().intValue());
        assertEquals(6, iM.getBreveDuration().intValue());
    }

}