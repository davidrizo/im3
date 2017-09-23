package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimeSignatureTest {
    @Test
    public void getBeat() throws Exception {
        TimeSignature ts34 = new FractionalTimeSignature(3, 4);
        Time t0 = Time.TIME_ZERO;
        assertEquals(0, ts34.getIntegerBeat(t0));
        assertEquals(0.0, ts34.getBeat(t0), 0.001);

        Time t8th = new Time(1,2);
        assertEquals(0, ts34.getIntegerBeat(t8th));
        assertEquals(0.5, ts34.getBeat(t8th), 0.001);

        Time tQuarter = new Time(1);
        assertEquals(1, ts34.getIntegerBeat(tQuarter));
        assertEquals(1.0, ts34.getBeat(tQuarter), 0.001);

        Time t2_Quarter = new Time(2);
        assertEquals(2, ts34.getIntegerBeat(t2_Quarter));
        assertEquals(2.0, ts34.getBeat(t2_Quarter), 0.001);


        Time t2_8th = new Time(1,2).add(new Time(2));
        assertEquals(2, ts34.getIntegerBeat(t2_8th));
        assertEquals(2.5, ts34.getBeat(t2_8th), 0.001);



    }

}