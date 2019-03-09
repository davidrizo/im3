package es.ua.dlsi.im3.core.score;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeTest {

    @Test
    public void overlaps() {
        Time t0 = Time.TIME_ZERO;
        Time t5 = new Time(5, 1);
        Time t10 = new Time(10, 1);
        Time t15 = new Time(15, 1);
        Time t20 = new Time(20, 1);

        assertFalse(Time.overlaps(t0, t5, t10, t15));
        assertFalse(Time.overlaps(t15, t20, t5, t10));
        assertTrue(Time.overlaps(t0, t10, t5, t15));
        assertTrue(Time.overlaps(t0, t20, t5, t15));
        assertTrue(Time.overlaps(t5, t10, t0, t20));
        assertTrue(Time.overlaps(t5, t10, t5, t20));
    }
}