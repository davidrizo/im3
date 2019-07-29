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

import es.ua.dlsi.im3.core.IM3Exception;

/**
 *
 * @author drizo
 */
public abstract class TimeSignature implements INotationTypeDependant, ITimedElementInStaff, IUniqueIDObject, IStaffElementWithoutLayer, ITimedElementWithSetter {
	protected Time time;
	private String ID;	
	protected NotationType notationType;
	protected Staff staff;
	
    public TimeSignature(NotationType notationType) {
    		this.notationType = notationType;
    		this.time = null;
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

	@Override
	public void setTime(Time time) {
		this.time = time;
	}

    @Override
    public void move(Time offset) throws IM3Exception {
        Staff prevStaff = staff;
        staff.remove(this);
        if (time == null) {
			this.time = offset;
		} else {
			this.time = time.add(offset);
		}

        prevStaff.addTimedElementInStaff(this);
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

    /**
     * It returns the duration of a measure, i.e., the unit denoted by the figures denoted by the meter (mensural notation
     * does not use measure)
     * @return
     */
    public abstract Time getDuration();

    /**
     * It returns the beat of an onset (e.g. 0,1,2,3 for 4/4 bars)
     *
     * @param onset
     * @return Integer value if it starts in a beat, float value with decimals
     * if the onset is located between two beats. It starts from 0
     */
    public int getIntegerBeat(Time onset)  {
        //return (int) Math.IEEEremainder(onset, getMeasureDuration(resolution));
        //return (int) (onset  % getMeasureDuration(resolution));
        return (int) getBeat(onset); //TODO Pruebas unitarias
    }

    /**
     * It returns the beat of an onset
     *
     * @param onset
     * @return Integer value if it starts in a beat, float value with decimals
     * if the onset is located between two beats. It starts from 0
     */
    public double getBeat(Time onset) {
        double offset = onset.substract(this.getTime()).mod(getDuration());
        return offset;
    }
}
