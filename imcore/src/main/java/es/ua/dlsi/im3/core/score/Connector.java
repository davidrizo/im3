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
 * The connector is attached to the "from" symbol
 * 
 * @author drizo
 * @param <NotationSymbolTypeFrom>
 * @param <NotationSymbolTypeTo>
 */
public abstract class Connector<NotationSymbolTypeFrom extends ISymbolWithConnectors, NotationSymbolTypeTo extends ISymbolWithConnectors> {
	NotationSymbolTypeFrom from;
	NotationSymbolTypeTo to;

	public Connector(NotationSymbolTypeFrom from, NotationSymbolTypeTo to) {
		this.from = from;
		this.to = to;
	}

	public NotationSymbolTypeFrom getFrom() {
		return from;
	}

	public NotationSymbolTypeTo getTo() {
		return to;
	}

	public void setTo(NotationSymbolTypeTo to) {
		this.to = to;
	}

	public void setFrom(NotationSymbolTypeFrom from) {
		this.from = from;
	}

	@Override
	public String toString() {
		return "Connector [from=" + from + ", to=" + to + "]";
	}

}
