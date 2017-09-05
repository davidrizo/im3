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

import java.util.TreeMap;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 *
 * @author drizo
 */
public class IDManager {
	private int nextID;
	TreeMap<String, IUniqueIDObject> usedIDs;

	// it should be created by the song
	IDManager(ScoreSong song) {
		nextID = 0;
		usedIDs = new TreeMap<>();
	}

	/* Avoid using the same id manager for all songs because several songs simultaneously charged by have the same IDS
	 * public synchronized static IDManager getInstance() {
		if (instance == null) {
			instance = new IDManager();
		}
		return instance;
	}*/

	public synchronized void assignNextID(IUniqueIDObject object) {
		boolean exit = false;
		do {
			if (object.__getID() != null) {
				IUniqueIDObject prevAssignedObject = usedIDs.get(object.__getID());
				if (prevAssignedObject == object) {
					return; // it was the same object
				} else if (prevAssignedObject == null) {
					usedIDs.put(object.__getID(), object); // it was an empty ID
					return;
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append(object.__getIDPrefix());
			sb.append(nextID++);
			String r = sb.toString();
			if (!usedIDs.containsKey(r)) {
				usedIDs.put(r, object);
				object.__setID(r);
				exit = true;
			}
		} while (!exit);
	}

	public synchronized void assignID(String ID, IUniqueIDObject object) throws IM3Exception {
		if (object == null) {
			throw new IM3Exception("The object is null and cannot be assigned an ID");
		}
		if (ID == null) {
			assignNextID(object);
		} else {
			IUniqueIDObject prevObj = usedIDs.get(ID);
			if (prevObj == null) {
				usedIDs.put(ID, object);
				object.__setID(ID);
			} else {
				if (prevObj != object) {
					throw new IM3Exception("The ID " + ID + " was already used for other object '" + usedIDs.get(ID).toString()
							+ "', while assigning it to '" + object.toString() + "'");
				} // else already inserted
			}
		}
	}

	/**
	 * 
	 * @param ID
	 * @return null if not found
	 */
	public IUniqueIDObject getObjectFromID(String ID) {
		return usedIDs.get(ID);
	}
	
	public String getID(IUniqueIDObject o) {
		if (o.__getID() == null) {
			assignNextID(o);
		}
		return o.__getID();
	}
}
