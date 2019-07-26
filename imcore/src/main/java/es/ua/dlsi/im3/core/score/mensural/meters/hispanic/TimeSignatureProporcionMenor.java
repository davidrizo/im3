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

package es.ua.dlsi.im3.core.score.mensural.meters.hispanic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.mensural.meters.Perfection;
import es.ua.dlsi.im3.core.score.mensural.meters.TimeSignatureMensural;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;

import java.util.List;

//TODO Integrar con C32 "internacional"
/**
 *
 * @author drizo
 */
public class TimeSignatureProporcionMenor extends TimeSignatureMensural {

    public TimeSignatureProporcionMenor() {
    		super(Perfection.perfectum, Perfection.imperfectum);
    }

    @Override
    public String toString() {
    		return "CZ";
    }

    @Override
    public void applyImperfectionRules(List<AtomFigure> figureList) {

    }

    @Override
	public boolean equals(Object other) {
		return other instanceof TimeSignatureProporcionMenor;
	}

	@Override
	public Time getDuration() {
		return getSemibreveDuration();
	}


	@Override
	public SignTimeSignature clone() {
		return new TimeSignatureProporcionMenor();
	}

	@Override
	public String getSignString() {
		return "C3/2";
	}
}
