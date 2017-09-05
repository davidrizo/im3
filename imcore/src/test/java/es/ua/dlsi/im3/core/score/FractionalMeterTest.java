package es.ua.dlsi.im3.core.score;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;

public class FractionalMeterTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetMeasureDuration() {
		assertEquals(2.0, new FractionalTimeSignature(2,4).getMeasureDuration().getComputedTime(), 0.0001);
		assertEquals(3.0, new FractionalTimeSignature(3,4).getMeasureDuration().getComputedTime(), 0.0001);
		assertEquals(4.0, new FractionalTimeSignature(2,2).getMeasureDuration().getComputedTime(), 0.0001);
		assertEquals(3.0, new FractionalTimeSignature(6,8).getMeasureDuration().getComputedTime(), 0.0001);
		//TODO Más
	}

}
