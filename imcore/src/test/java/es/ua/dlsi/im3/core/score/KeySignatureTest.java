package es.ua.dlsi.im3.core.score;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import es.ua.dlsi.im3.core.IM3Exception;

public class KeySignatureTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTransposingInstruments() throws IM3Exception {
		KeySignature ks = new KeySignature(NotationType.eModern, new Key(-1, Mode.MAJOR));
		assertEquals(-1, ks.getInstrumentKey().getFifths());
		assertEquals(-1, ks.getInstrumentKey().getFifths());
		ks.setTranspositionInterval(Intervals.FIFTH_PERFECT_ASC.createInterval());
		assertEquals(-1, ks.getInstrumentKey().getFifths());
		assertEquals(0, ks.getConcertPitchKey().getFifths());
	}

}
