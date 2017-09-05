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
package es.ua.dlsi.im3.core.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author drizo
 */
public class Header {
    List<Title> titles;
    List<Person> persons;
    
    public Header() {
	titles = new ArrayList<>();
	persons = new ArrayList<>();
    }

    public List<Title> getTitles() {
	return titles;
    }

    public void addTitle(Title title) {
	this.titles.add(title);
    }
    
    public List<Person> getPersons() {
	return persons;
    }

    public void addPerson(Person person) {
	this.persons.add(person);
    }

    /**
     * If it has it returns several titles concatenated
     * @param separator The separator between titles
     * @return 
     */
    public String getTitleContatenated(String separator) {
	StringBuilder sb = new StringBuilder();
	for (Title title : titles) {
	    if (sb.length() > 0) {
		sb.append(separator);
	    }
	    sb.append(title);
	}
	return sb.toString();
    }

	public String getPerson(String role) {
		for (Person person : persons) {
			if (role.equals(person.getRole())) {
				return person.getName();
			}
		}
		return null;
	}

	public String getPerson(PersonRoles role) {
		for (Person person : persons) {
			if (role.getTitle().equals(person.getRole())) {
				return person.getName();
			}
		}
		return null;
	}

    
    
}
