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

import es.ua.dlsi.im3.core.IM3RuntimeException;

/**
 *
 * @author drizo
 */
public class EncodingDescription {
    List<String> editorialDeclarationParagraphs;

    public EncodingDescription() {
	editorialDeclarationParagraphs = new ArrayList<>();
    }
    
    public void addEditorialDeclarationParagraph(String p) {
	editorialDeclarationParagraphs.add(p);
    }
    
    public String getEditorialDeclarationParagraph(int i) {
	if (i<0 || i>= editorialDeclarationParagraphs.size()) {
	    throw new IM3RuntimeException("Invalid editorial description index: " +i);
	}
	return editorialDeclarationParagraphs.get(i);
    }

    public List<String> getEditorialDeclarationParagraphs() {
	return editorialDeclarationParagraphs;
    }
}
