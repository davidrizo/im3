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

import java.util.Objects;

/**
 * @author drizo
 */
public class ScoreAnalysisKind implements IIndexed {
    String name;
    int order;

    public ScoreAnalysisKind(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getIndex() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


    @Override
    public int compareTo(IIndexed o) {
        return order - o.getIndex();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + this.order;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ScoreAnalysisKind other = (ScoreAnalysisKind) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return this.order == other.order;
    }


    @Override
    public String toString() {
        return name;
    }

}
