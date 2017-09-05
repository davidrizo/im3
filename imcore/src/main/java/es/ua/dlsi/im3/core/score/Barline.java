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


//TODO Heredar, tipos de bar line
/**
 * It includes the barline number. It may be later hidden, but its layout must be computed here 
 * @author drizo
 */
public class Barline implements ITimedElementInStaff {
	boolean repeatBackwards;
	boolean repeatForward;
	Time time; // it does not depend on the Measure direct

	Staff staff;
	Measure bar;

	/**
	 * 
	 * @param bar It may be null (for mensural notation)
	 * @param staff
	 * @param repeatBackwards
	 * @param repeatForward
	 */
	public Barline(Measure bar, Staff staff, boolean repeatBackwards, boolean repeatForward) {
		this.time = new Time();
		this.bar = bar;
		this.repeatBackwards = repeatBackwards;
		this.repeatForward = repeatForward;
		this.staff = staff;
	}

	public Barline(Measure bar, Staff staff) {
		this(bar, staff, false, false);
	}

	public boolean isRepeatBackwards() {
		return repeatBackwards;
	}

	public void setRepeatBackwards(boolean repeatBackwards) {
		this.repeatBackwards = repeatBackwards;
	}

	public boolean isRepeatForward() {
		return repeatForward;
	}

	public void setRepeatForward(boolean repeatForward) {
		this.repeatForward = repeatForward;
	}

	/*
	 * @Override public double getComputedDurationRatio() { return 0; }
	 */

	/*
	 * @Override public void computeLocalLayout() throws NotationException {
	 * this.startX = x; this.endX = x; this.startY =
	 * getStaff().getTopLine().getAbsoluteY(); this.endY =
	 * getStaff().getBottomLine().getAbsoluteY();
	 * 
	 * super.computeLocalLayout(); }
	 */

	// void setVerticalScoreDivision(VerticalScoreDivision vsd) {
	// this.ve
	// instead of computing always the time from the measure that is time
	// consuming
	public void setTime(Time time) {
		this.time = time;
	}
	
	@Override	
	public Time getTime() {
		return this.time;
	}
	
	@Override
	public Staff getStaff() {
		return staff;
	}

	@Override
	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public Measure getBar() {
		return bar;
	}
}
