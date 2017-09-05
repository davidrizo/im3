/*
 * Copyright (C) 2015 David Rizo Valero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ua.dlsi.im3.core.utils;

import static org.junit.Assert.assertEquals;

import es.ua.dlsi.im3.core.utils.Histogram;
import org.junit.Test;

/**
 *
 * @author drizo
 */
public class HistogramTest {
    @Test
    public void testIt() {
	Histogram<String> h = new Histogram<>();
	
	h.addElement("Hola");
	h.addElement("Hola");
	h.addElement("qué");
	h.addElement("tal");
	h.addElement("adios");
	
	assertEquals(5, h.getCountOfElements());
	assertEquals(4, h.size());
	assertEquals("Hola", h.getMaxElement());
	assertEquals(2, h.getCountOfElement("Hola"));
	assertEquals(0, h.getCountOfElement("Pepe"));
	assertEquals(2.0/5.0, h.getProbability("Hola"), 0.001);
	assertEquals(1.0/5.0, h.getProbability("tal"), 0.001);
	assertEquals(0.0, h.getProbability("Pepe"), 0.001);
	
	assertEquals((2.0+1.0+1.0+1.0) / 4.0, h.getAverage(), 0.00001);
    }
}
