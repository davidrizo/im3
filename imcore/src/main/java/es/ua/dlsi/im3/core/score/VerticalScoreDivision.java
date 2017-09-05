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
package es.ua.dlsi.im3.core.score;

import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * This is a core symbol for allowing the recursive design scheme
 *
 * @author drizo
 */
public abstract class VerticalScoreDivision implements Comparable<VerticalScoreDivision>, IUniqueIDObject, INotationTypeDependant  {
	String ID;
	/**
	 * Using a hierarchical notation: e.g. 1.3.2
	 */
	private String hierarchicalOrder;

	/**
	 * Unique order in the score
	 */
	private int numberIdentifier;

	String name; 
	private final ScoreSong notationSong;
	private NotationType notationType;
	
	/**
	 * Bar lines are stored just here. If stored in a staff group, they will cross all staves in the group. 
	 * If stored in a staff will be local to the staff
	 */
	protected TreeMap<Time, Barline> barLines;


	public VerticalScoreDivision(ScoreSong notationSong, String hierarchicalOrder, int numberIdentifier) {
		this.notationSong = notationSong;
		this.hierarchicalOrder = hierarchicalOrder;
		this.numberIdentifier = numberIdentifier;
	}

	// ----------------------------------------------------------------------
	// ----------------------- General information
	// --------------------------------
	// ----------------------------------------------------------------------
	
	
	public int getNumberIdentifier() {
		return numberIdentifier;
	}

	public final NotationType getNotationType() {
		return notationType;
	}

	public final void setNotationType(NotationType notationType) {
		this.notationType = notationType;
	}

	public void setNumberIdentifier(int numberIdentifier) {
		this.numberIdentifier = numberIdentifier;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ScoreSong getScoreSong() {
		return notationSong;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + Objects.hashCode(this.hierarchicalOrder);
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
		final VerticalScoreDivision other = (VerticalScoreDivision) obj;
        return Objects.equals(this.hierarchicalOrder, other.hierarchicalOrder);
    }

	@Override
	public int compareTo(VerticalScoreDivision o) {
		return this.hierarchicalOrder.compareTo(o.hierarchicalOrder);
	}

	// abstract double computeDmin();

	// ----------------------------------------------------------------------
	// ----------------------- Children information
	// --------------------------------
	// ----------------------------------------------------------------------
	abstract Staff getTopStaff() throws IM3Exception;

	abstract Staff getBottomStaff() throws IM3Exception;

	// 2014 marzo public abstract void addBarline(Barline barline);
	// public abstract List<CoreSymbol> getCoreSymbols();
	// abstract void computeOnsets();
	abstract List<Staff> getContainedStaves();

	public String getHierarchicalOrder() {
		return hierarchicalOrder;
	}

	public void setHierarchicalOrder(String order) {
		this.hierarchicalOrder = order;
	}

	@Override
	public String __getID() {
		return ID;
	}

	@Override
	public void __setID(String id) {
		this.ID = id;
	}


	public void addBarline(Barline ts) throws IM3Exception {
		this.barLines.put(ts.getTime(), ts);
	}
	
}
