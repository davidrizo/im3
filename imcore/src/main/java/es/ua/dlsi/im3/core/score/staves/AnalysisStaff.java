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

package es.ua.dlsi.im3.core.score.staves;

import java.util.TreeMap;
import java.util.Map.Entry;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreAnalysisHook;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.Time;

/**
 * The layer that contains all analysis hooks is the hooks layer
 * @author drizo
 */
public class AnalysisStaff extends Staff {	
	TreeMap<Time, ScoreAnalysisHook> analysisHooks;

	public AnalysisStaff(ScoreSong song, String hierarchicalOrder,
			int numberIdentifier) {
		super(song, hierarchicalOrder, numberIdentifier, 1);
		analysisHooks = new TreeMap<>();
	}

	@Override
	public boolean isPitched() {
		return false;
	}

	@Override
	public String __getIDPrefix() {
		return "AS";
	}

	public void addAnalysisHook(ScoreAnalysisHook e) throws IM3Exception {
		analysisHooks.put(e.getTime(), e);
	}


	public ScoreAnalysisHook findAnalysisHookWithOnset(Time time) throws IM3Exception {
		ScoreAnalysisHook result = analysisHooks.get(time);
		if (result == null) {
			throw new IM3Exception("Cannot find an analysis hook with time " + time);
		} else {
			return result;
		}
		
	}


	public ScoreAnalysisHook findLastAnalysisHookBeforeOrEqualsOnset(Time time) throws IM3Exception {
		Entry<Time, ScoreAnalysisHook> entry = analysisHooks.floorEntry(time);
		if (entry == null) {
			throw new IM3Exception("Cannot find an analysis hook with time <=" + time);
		} else {
			return entry.getValue();
		}
	}


	public ScoreAnalysisHook findLastAnalysisHookAfterOrEqualsOnset(Time time) throws IM3Exception {
		Entry<Time, ScoreAnalysisHook> entry = analysisHooks.ceilingEntry(time);
		if (entry == null) {
			throw new IM3Exception("Cannot find an analysis hook with time >=" + time);
		} else {
			return entry.getValue();
		}
	}


	public ScoreAnalysisHook findLastAnalysisHook() {
		return analysisHooks.lastEntry().getValue();
	}


	public boolean hasAnalysisHooks() {
		return !analysisHooks.isEmpty();
	}
		
}
