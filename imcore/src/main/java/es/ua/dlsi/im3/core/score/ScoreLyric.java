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

import es.ua.dlsi.im3.core.adt.IIndexed;

/**
 *
 * @author drizo
 */
public class ScoreLyric implements IIndexed {
    Integer verse;
    String text;
    AtomPitch owner;
    Syllabic syllabic;

    public ScoreLyric(Integer verse, AtomPitch owner, String content, Syllabic syllabic) {
	    this.owner = owner;
	    this.verse = verse;
	    this.text = content;
	    this.syllabic = syllabic;
    }

    public Syllabic getSyllabic() {
        return syllabic;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public int getIndex() {return this.verse;}

    public Integer getVerse() {
        return verse;
    }

    public AtomPitch getOwner() {
        return owner;
    }

    @Override
    public int compareTo(IIndexed o) {
	return this.getIndex() - o.getIndex();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSyllabic(Syllabic syllabic) {
        this.syllabic = syllabic;
    }

    public void setVerseNumber(Integer verseNumber) {
        this.verse = verseNumber;
    }
}
