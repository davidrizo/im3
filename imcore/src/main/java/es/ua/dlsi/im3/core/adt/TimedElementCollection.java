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
package es.ua.dlsi.im3.core.adt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElement;
import es.ua.dlsi.im3.core.score.Time;

//TODO TESTS Unitarios
/**
 * Common behaviour for timed symbols like keys, meters, tempos
 * 
 * @author drizo
 * @param <TimedElementType>
 */
public class TimedElementCollection<TimedElementType extends ITimedElement> {
	protected TreeMap<Time, TimedElementType> map;

	public TimedElementCollection() {
		map = new TreeMap<>();
	}

	public void addValue(TimedElementType value) throws IM3Exception {
		if (this.map.containsKey(value.getTime())) {
			throw new IM3Exception("There is other element (" + this.map.get(value.getTime()) + ") in time " + value.getTime() + ": "
					+ this.map.get(value.getTime()).toString() + " while inserting " + value.toString());
		}

		Logger.getLogger(TimedElementCollection.class.getName()).log(Level.FINEST, "Adding {0} at time {1}",
				new Object[] { value, value.getTime() });
		this.map.put(value.getTime(), value);
	}

	public void addValueOverride(TimedElementType value) {
		Logger.getLogger(TimedElementCollection.class.getName()).log(Level.FINEST, "Adding {0} at time {1}",
				new Object[] { value, value.getTime() });
		this.map.put(value.getTime(), value);
	}

	public void remove(TimedElementType value) {
		this.map.remove(value.getTime());
		map.tailMap(value.getTime());
	}

	/**
	 * It returns the active element at a time
	 * 
	 * @param time
	 * @return
	 * @throws IM3Exception
	 */
	public TimedElementType getValueAtTime(Time time) throws IM3Exception {
		Map.Entry<Time, TimedElementType> entry = map.floorEntry(time);
		if (entry == null) {
			String className = "";
			if (!map.isEmpty()) {
				className = " in map of types " + map.firstEntry().getValue().getClass().toString();
			}
			throw new IM3Exception("No element at time " + time + className);
		}
		return entry.getValue();
	}

	/**
	 * It returns the active element at a time
	 * 
	 * @param time
	 * @return The element or null if not exists
	 */
	public TimedElementType getValueAtTimeOrNull(Time time) {
		Map.Entry<Time, TimedElementType> entry = map.floorEntry(time);
		if (entry == null) {
			return null;
		} else {
			return entry.getValue();
		}
	}

	public int getCount() {
		return map.size();
	}

	public Collection<TimedElementType> getValues() {
		return map.values();
	}

	public NavigableMap<Time, TimedElementType> getOrderdValuesWithTimeLowerOrEqualThan(Time time) {
		return map.subMap(Time.TIME_ZERO, true, time, true);
	}

	public NavigableMap<Time, TimedElementType> getOrderdValuesWithTimeHigherThan(Time time) {
		return map.subMap(time, false, Time.TIME_MAX, true);
	}

	/**
	 * It adds all values to a ordered set and returns it
	 * 
	 * @return
	 */
	public ArrayList<TimedElementType> getOrderedValues() {
		ArrayList<TimedElementType> result = new ArrayList<>();
		result.addAll(map.values());
		return result;
	}

	/**
	 * It obtains the element figureAndDots by computing the figureAndDots from
	 * a given element to its successor
	 *
	 * @param h
	 * @param songDuration
	 * @return
	 */
	public Time computeElementDurationFromIOI(TimedElementType h, Time songDuration) {
		Map.Entry<Time, TimedElementType> succ = this.map.higherEntry(h.getTime());
		// System.out.println(h + " --- " + succ);
		if (succ == null) {
			// it is the last
			return songDuration.substract(h.getTime());
		} else {
			return succ.getValue().getTime().substract(h.getTime());
		}
	}

	/**
	 * @return null if empty
	 */
	public TimedElementType getFirstElement() {
		if (map.isEmpty()) {
			return null;
		} else {
			return map.firstEntry().getValue();
		}
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<Time> getKeys() {
		return map.keySet();
	}

	public void clear() {
		map.clear();
	}

	public TimedElementType getLastValue() {
		if (map.isEmpty()) {
			return null;
		} else {
			return map.lastEntry().getValue();
		}
	}

	/**
	 * 
	 * @param fromTime
	 *            inclusive
	 * @param toTime
	 *            exclusive
	 * @return
	 */
	public ArrayList<TimedElementType> getOrderedValuesWithOnsetInRange(Time fromTime, Time toTime) {
		return new ArrayList<>(map.subMap(fromTime, toTime).values());
	}

	public int size() {
		return map.size();
	}

	public TimedElementType getElementWithTime(Time time) {
		return map.get(time);
	}

	/*FRACCIONES public void replace(TimedElementType from, TimedElementType to) throws IM3Exception {
		if (!map.containsKey(from.getTime()) || !map.containsValue(from)) {
			throw new IM3Exception("The element to be replaced is not contained in the collection");
		}

		to.setTime(from.getTime());
		map.put(from.getTime(), to);
	}*/
}
