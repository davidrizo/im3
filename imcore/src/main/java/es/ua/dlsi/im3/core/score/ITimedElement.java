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

import java.util.Comparator;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;

/**
 *
 * @author drizo
 */
public interface ITimedElement {
	/**
	 * By default, time should be zero
	 * @return
	 * @throws IM3Exception
	 */
	Time getTime();
	
    //void setTime(Time time) throws IM3Exception; 
//    public void move(long offset) throws IM3Exception;
	
	Comparator<ITimedElement> TIMED_ELEMENT_COMPARATOR = new Comparator<ITimedElement>() {
		@Override
		public int compare(ITimedElement o1, ITimedElement o2) {
			int diff;
			diff = o1.getTime().compareTo(o2.getTime());
			if (diff == 0) {
				try {
					diff = SymbolsOrderer.compareClasses(o1, o2);
				} catch (IM3Exception e) {
					throw new IM3RuntimeException(e);
				}
				if (diff == 0) {
					diff = o1.hashCode() - o2.hashCode();
				}
			}
			return diff;
		}
		
	};
	
}
