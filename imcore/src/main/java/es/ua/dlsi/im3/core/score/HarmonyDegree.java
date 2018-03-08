/*
 * Copyright (C) 2014 David Rizo Valero
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
package es.ua.dlsi.im3.core.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;

/**
 * It contains a single degree (e.g. V), an extended degree (e.g. V7), or a
 * secondary dominant (e.g. VII(V), i.e., 7th of the 5th)
 * 
 * @author drizo
 */
public class HarmonyDegree implements Comparable<HarmonyDegree> {
	/**
	 * In the case of VII(V), this array contains already the computed values given a base instrumentKey, e.g. CM VII(V): i=0 => VII (instrumentKey=GM or Gm), i=1 => V (instrumentKey CM)
	 */
	RomanChord[] edegree;

	/**
	 * 
	 * @param instrumentKey
	 * @param degreeSequence
	 *            A tempo for a single degree, or the sequence VII, V for
	 *            representing VII(V)
	 */
	public HarmonyDegree(Key key, 
			ExtendedDegree... degreeSequence) throws IM3Exception {
		init(key,  degreeSequence);
	}

	/**
	 * If the string is a VII(V) it means it is a 5th of the 7th, e.g. In FM, it
	 * is the B chord
	 * 
	 * @param instrumentKey
	 * @param string
	 * @throws IM3Exception
	 */
	public HarmonyDegree(Key key,  
			String string) throws IM3Exception {
		String[] split = string.split("[()]");
		ArrayList<ExtendedDegree> degreeSeq = new ArrayList<>();
		//int j = 0;
		for (int i = 0; i < split.length; i++) {
			if (!split[i].isEmpty()) { // for things like )()( in V(V)(V)
				degreeSeq.add(new ExtendedDegree(split[i]));
			}
		}
		ExtendedDegree[] v = new ExtendedDegree[degreeSeq.size()];
		for (int i = 0; i < v.length; i++) {
			v[i] = degreeSeq.get(i); 
		}
		init(key, v);
	}

	/**
	 * 
	 * @param instrumentKey
	 * @param string
	 * @return null if it is not a harmony degree
	 */
	//TODO Quitar InKey del nombre del método
	public static HarmonyDegree parseStringInKey(Key key, 
			String string) {
		try {
			HarmonyDegree h = new HarmonyDegree(key, 
					string);
			return h;
		} catch (IM3Exception e) {
			Logger.getLogger(HarmonyDegree.class.getName()).log(Level.INFO,
					//"{0} is not a harmony degree in instrumentKey {1}: {2}", new Object[] { string, instrumentKey, e.getMessage() });
					"{0} is not a harmony degree: {1}", new Object[] { string, e.getMessage() });
			return null;
		}
	}

	/**
	 * 
	 * @return true for things like V(V) or VII(V) or V(V)(V)
	 */
	public boolean hasSecondaryDegree() {
		return edegree.length > 1;
	}

	/**
	 * 
	 * @return The only chord when no secondary degrees are found or the last
	 *         one of the sequence, e.g. the 7th of the 5th in the case of
	 *         VII(V)
	 * @throws IM3Exception 
	 */
	public RomanChord getActualChord() {
		return edegree[0]; // 20160429
		//return edegree[edegree.length - 1]; // 20160411
		//return lastChord;
	}

	public PitchClass getRoot() throws IM3Exception {
		return getActualChord().getRoot();
	}

	// TODO Test unitario
	/**
	 * e.g. Key = CM, VII(V) = 7th of the 5th --> 7th of G => B natural
	 * 
	 * @param edegree
	 * @param d
	 * @return
	 */
	private RomanChord constructChordOver(Key key, RomanChord previous, ExtendedDegree d) throws IM3Exception {
		PitchClass previousRoot = previous.getNoModeKey().computeRoot(previous.getDegree().getDegree());
		Key newKey = new Key(previousRoot, Mode.MINOR); // TODO Visto con Plácido: pasar a menor es la mejor estrategia
		return new RomanChord(newKey, d);
	}
	
	/*private RomanChord constructChordOver(RomanChord previous, ExtendedDegree d) throws IM3Exception {
		Degree newDegree = previous.getDegree().getDegree().add(d.getDegree());
		RomanChord result = new RomanChord(new ExtendedDegree(newDegree));
		return result;
		//PitchClass previousRoot = previous.getKey().computeRoot(previous.getDegree().getDegree());
		//Key newKey = new Key(previousRoot, previous.getKey().getMode());
		//return new RomanChord(newKey, d);
	}*/	

	/**
	 * Parse reverse: VII (V) is a 7th of 5th, then 5th must be computed before 7th 
	 * @param instrumentKey
	 * @param degreeSequence
	 * @throws IM3Exception
	 */
	private void init(Key key, 
			ExtendedDegree[] degreeSequence) throws IM3Exception {
		edegree = new RomanChord[degreeSequence.length];
		for (int i = edegree.length - 1; i >= 0; i--) {
			ExtendedDegree d = degreeSequence[i];
			if (i == edegree.length - 1) {
				edegree[i] = new RomanChord(key, d);
			} else {
				edegree[i] = constructChordOver(key, edegree[i + 1], d);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(edegree[0].toString());
		for (int i = 1; i < edegree.length; i++) {
			sb.append('(');
			sb.append(edegree[i].toString());
			sb.append(')');
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Arrays.deepHashCode(this.edegree);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final HarmonyDegree other = (HarmonyDegree) obj;
        return Arrays.deepEquals(this.edegree, other.edegree);
    }

	@Override
	public int compareTo(HarmonyDegree o) {
		try {
			return this.getRoot().compareTo(o.getRoot());
		} catch (IM3Exception ex) {
			Logger.getLogger(HarmonyDegree.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3RuntimeException(ex);
		}
	}

	// TODO Test
	public void changeKey(Key kc) throws IM3Exception {
		ExtendedDegree[] old = new ExtendedDegree[this.edegree.length];
		for (int i = 0; i < old.length; i++) {
			old[i] = edegree[i].getDegree();
		}
		init(kc, old);
	}

}
