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

import java.util.ArrayList;
import java.util.List;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * @author drizo
 */
public class StaffGroup extends VerticalScoreDivision {
	List<VerticalScoreDivision> children;

	public StaffGroup(ScoreSong notationSong, String hierarchicalOrder,
			int numberIdentifier) {
		super(notationSong, hierarchicalOrder, numberIdentifier);
		this.children = new ArrayList<>();
	}

	// ----------------------------------------------------------------------
	// ----------------------- General information
	// --------------------------------
	// ----------------------------------------------------------------------
	// ----------------------------------------------------------------------
	// ----------------------- Children information
	// --------------------------------
	// ----------------------------------------------------------------------
	public boolean isEmpty() {
		return children == null || children.isEmpty();
	}

	public List<VerticalScoreDivision> getChildren() {
		return children;
	}

	@Override
	Staff getTopStaff() throws IM3Exception {
		if (this.isEmpty()) {
			throw new IM3Exception("Staff group without staves inside");
		}
		return this.children.get(0).getTopStaff();
	}

	@Override
	Staff getBottomStaff() throws IM3Exception {
		if (this.isEmpty()) {
			throw new IM3Exception("Staff group without staves inside");
		}
		return this.children.get(this.getChildren().size() - 1).getBottomStaff();
	}

	public void addChild(VerticalScoreDivision s) {
		this.children.add(s);
	}

	@Override
	List<Staff> getContainedStaves() {
		List<Staff> result = new ArrayList<>();
		for (VerticalScoreDivision child : children) {
			result.addAll(child.getContainedStaves());
		}
		return result;
	}

	@Override
	public String __getIDPrefix() {
		return "SYS";
	}
}
