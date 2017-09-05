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

/**
 *
 * @author drizo
 */
public abstract class TimeSignature implements INotationTypeDependant, ITimedElementInStaff, IUniqueIDObject {
	protected Time time;
	private String ID;	
	protected NotationType notationType;
	protected Staff staff;
	
    public TimeSignature(NotationType notationType) {
    		this.notationType = notationType;
    		this.time = new Time();
    }

    @Override
    public Time getTime() {
		return this.time;
    }
    
    @Override
    public NotationType getNotationType() {
    		return notationType;
    }

    @Override
	public Staff getStaff() {
		return staff;
	}

	@Override
	public void setStaff(Staff staff) {
		this.staff = staff;		
	}

	public void setTime(Time time) {
		this.time = time;
	}

	@Override
	public String __getID() {
		return ID;
	}

	@Override
	public void __setID(String id) {
		this.ID = id;
		
	}

	@Override
	public String __getIDPrefix() {
		return "TS";
	}

	public abstract boolean equals(Object other);

    public abstract boolean isCompound();
}
