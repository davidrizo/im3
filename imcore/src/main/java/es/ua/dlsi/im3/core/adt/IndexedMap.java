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

import java.util.TreeMap;

import es.ua.dlsi.im3.core.IM3RuntimeException;

/**
 *
 * @author drizo
 * @param <ItemType>
 */
public class IndexedMap<ItemType extends IIndexed> {
	TreeMap<Integer, ItemType> elements;

	public IndexedMap() {
		elements = new TreeMap<>();
	}

	public void addItem(ItemType item) throws IM3RuntimeException {
		if (elements.containsKey(item.getIndex())) {
			throw new IM3RuntimeException("The map already contains a item with index " + item.getIndex());
		}
		elements.put(item.getIndex(), item);
	}

	/**
	 * 
	 * @param index
	 * @return null if not available
	 */
	public ItemType getItem(int index) {
		return elements.get(index);
	}

	/**
	 *
	 * @return
	 */
	public TreeMap<Integer, ItemType> getAllItems() {
		return elements;
	}

	public int size() {
		return elements.size();
	}

	public boolean containsValue(ItemType value) {
		return elements.containsValue(value);
	}

	public void removeItem(ItemType item) {
		this.elements.remove(item.getIndex());
	}
}
