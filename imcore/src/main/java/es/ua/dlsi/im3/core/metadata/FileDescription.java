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
public class FileDescription {
    Header header;
    Publication publication;
    List<Source> sources;

    public FileDescription() {
	sources = new ArrayList<>();
    }

    public List<Source> getSources() {
	return sources;
    }

    public void setSources(List<Source> sources) {
	this.sources = sources;
    }

    public void setPublication(Publication publication) {
	this.publication = publication;
    }

    public Publication getPublication() {
	return publication;
    }

    public Header getHeader() {
	return header;
    }

    public void setHeader(Header header) {
	this.header = header;
    }

    
    
    
    

    
}
