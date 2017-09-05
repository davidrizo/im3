/*
 * Copyright (C) 2013 David Rizo Valero
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

package es.ua.dlsi.im3.core.score.staves;

import java.util.HashMap;

import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.NoteNames;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;

/**
 * @author drizo
 */
public class Pentagram extends Staff {
	// private static final int MAX_ADITIONAL_LINES = 7;
	HashMap<Clef, Integer> clefBottomLineOctaves;
	HashMap<Clef, NoteNames> clefBottomLineNoteNames;

	/**
	 * The staff must be added to the score song using ScoreSong.addStaff to be
	 * added to the song
	 * 
	 * @param notationType
	 * @param firstPart
	 * @param hierarchicalOrder
	 * @param numberIdentifier
	 */
	public Pentagram(ScoreSong song, String hierarchicalOrder, int numberIdentifier) {
		super(song, hierarchicalOrder, numberIdentifier, 5);
		clefBottomLineOctaves = new HashMap<>();
		clefBottomLineNoteNames = new HashMap<>();
		lines = 5;
	}

	/*FRACTIONS private int getBottomLineOctave(Clef clef) {
		Integer res = clefBottomLineOctaves.get(clef);
		if (res == null) {
			int bottomLineNoteOrder = computeBottomLineNoteOrder(clef);
			res = bottomLineNoteOrder / 7;
			clefBottomLineOctaves.put(clef, res);
		}

		return res;
	}

	private int computeBottomLineNoteOrder(Clef clef) {
		int noteOrder = clef.getNote().getOrder() + clef.getNoteOctave() * 7;
		return noteOrder - (clef.getLine() - 1) * 2;
	}

	private NoteNames getBottomLineNoteName(Clef clef) {
		NoteNames res = clefBottomLineNoteNames.get(clef);
		if (res == null) {
			int bottomLineNoteOrder = computeBottomLineNoteOrder(clef);
			res = NoteNames.values()[bottomLineNoteOrder % 7];
			clefBottomLineNoteNames.put(clef, res);
		}

		return res;
	}*/

	@Override
	public boolean isPitched() {
		return true;
	}

	@Override
	public String __getIDPrefix() {
		return "PTGRM";
	}

}
