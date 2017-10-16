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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;

/**
 * It orders symbols according to its time and the kind of symbol, e.g. clef
 * must be set before time signature
 * 
 * @author drizo
 */
public class SymbolsOrderer {
	private static int getClassOrder(ITimedElement o) {
        if (o instanceof MarkBarline) {
            return 1;
        } else if (o instanceof Clef) {
			return 2;
		} else if (o instanceof KeySignature) {
			return 3;
		} else if (o instanceof TimeSignature) {
			return 4;
		} else if (o instanceof StaffTimedPlaceHolder) {
            return 5;
		} else {
			return 6;
		}
	}

	// compare by time. As we have several classes in the same collection we
	// order them, the order of classes will be:
	// first clefs, next instrumentKey signature, then time signature, finally sounding
	// symbols
	static int compareClasses(ITimedElement o1, ITimedElement o2) throws IM3Exception {
		int order1 = getClassOrder(o1);
		int order2 = getClassOrder(o2);

		if (order1 == order2) {
			return o1.getTime().compareTo(o2.getTime());
		} else {
			return order1 - order2;
		}
	}

	public static void sortList(List<? extends ITimedElement> input) {
		Collections.sort(input, new Comparator<ITimedElement>() {
			// compare by time. As we have several classes in the same
			// collection we order them (see compareClasses), the order of
			// classes will be:
			// first clefs, next instrumentKey signature, then time signature, finally
			// sounding symbols (compare
			@Override
			public int compare(ITimedElement o1, ITimedElement o2) {
					int diff;
					try {
						diff = o1.getTime().compareTo(o2.getTime());
						if (diff != 0) {
							return diff;
						} else {
							diff = compareClasses(o1, o2);
							if (diff != 0) {
								return diff;
							} else {
								// any order
								return o1.toString().compareTo(o2.toString()); 
							}
						}
					} catch (IM3Exception e) {
						throw new IM3RuntimeException(e);
					}
			}
		});
	}
}
