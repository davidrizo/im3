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
 * The fermate are owned by the staff because the same fermata can be used in
 * different layers and chords or notes.
 * 
 * @author drizo
 */
public class Fermate extends StaffMark implements INotationTypeDependant {
	HashSet<AtomFigure> associatedDurationalSymbols;
	HashMap<PositionAboveBelow, Fermata> fermate;
	NotationType notationType;

	/**
	 * 
	 * @param snr
	 *            True if above, false if belog
	 * @throws IM3Exception 
	 */
	public Fermate(NotationType notationType, Staff staff, AtomFigure snr, PositionAboveBelow position) {
		super(staff, snr.getTime());
		this.notationType = notationType;
		associatedDurationalSymbols = new HashSet<>();
		fermate = new HashMap<>();
		if (snr != null) {
			addDurationalSymbol(snr, position); // we may want to create a
												// fermata just for
												// visualization alone
		}
	}
	/**
	 * Has no nonRest associated yet
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return associatedDurationalSymbols.isEmpty();
	}

	public void removeScoreNonRest(AtomFigure snr) {
		associatedDurationalSymbols.remove(snr);
	}

	public final void addDurationalSymbol(AtomFigure snr, PositionAboveBelow position) {
		associatedDurationalSymbols.add(snr);
		Fermata ll = fermate.get(position);
		if (ll == null) {
			ll = new Fermata(this, position);
			fermate.put(position, ll);
		}
        snr.setFermata(ll);
	}

	public HashMap<PositionAboveBelow, Fermata> getFermate() {
		return fermate;
	}

	public Fermata getFermata(PositionAboveBelow position) {
		return fermate.get(position);
	}

	@Override
	public NotationType getNotationType() {
		return notationType;
	}

    @Override
    public void move(Time offset) {
	    Time newTime = getTime().add(offset);
	    getStaff().moveFermate(this.getTime(), newTime);
	    this.setTime(newTime);
    }

}
