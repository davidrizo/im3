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
package es.ua.dlsi.im3.core.score;

import java.util.HashMap;
import java.util.HashSet;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * The ledger lines are owned by the staff because the same ledger lines can be
 * used in different layers and chords or notes.
 * 
 * @author drizo
 */
public class LedgerLines extends StaffMark {
	HashSet<AtomFigure> associatedNonRests;
	HashMap<PositionAboveBelow, Integer> ledgerLines;
	double width;

	/**
	 * 
	 * @param snr
	 * @param numberOfLines
	 * @param above
	 *            True if above, false if belog
	 * @throws IM3Exception 
	 */
	public LedgerLines(Staff staff, AtomFigure snr, int numberOfLines,
			PositionAboveBelow position) throws IM3Exception  {
		super(staff, snr.getTime());
		associatedNonRests = new HashSet<>();
		ledgerLines = new HashMap<>();
		addScoreNonRest(snr, numberOfLines, position);
	}

	public double getWidth() {
		return width;
	}
	/**
	 * Has no nonRest associated yet
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return associatedNonRests.isEmpty();
	}

	public void removeScoreNonRest(AtomFigure snr) {
		associatedNonRests.remove(snr);
	}

	public final void addScoreNonRest(AtomFigure snr, int numberOfLines, PositionAboveBelow position) {
		associatedNonRests.add(snr);
		ledgerLines.put(position, numberOfLines);
	}

	public HashMap<PositionAboveBelow, Integer> getLedgerLines() {
		return ledgerLines;
	}

	public int getLedgerLines(PositionAboveBelow position) {
		Integer result = ledgerLines.get(position);
		if (result == null) {
			return 0;
		} else {
			return result;
		}
	}
}
