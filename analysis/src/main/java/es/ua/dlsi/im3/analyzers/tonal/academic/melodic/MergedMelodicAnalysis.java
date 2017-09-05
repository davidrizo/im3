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
package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.core.IM3RuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author drizo
 */
public class MergedMelodicAnalysis {

    /**
     * Value = probability of the result
     * Sorted by values
     */
    ArrayList<Map.Entry<MelodicAnalysisNoteKinds, Double>> sortedValues;

    /**
     * @param kind        Kinds of melodic analyses
     * @param confidences confidences of those values
     */
    public MergedMelodicAnalysis(MelodicAnalysisNoteKinds[] kind, double[] confidences) {
        HashMap<MelodicAnalysisNoteKinds, Double> values;
        if (kind.length != confidences.length) {
            throw new IM3RuntimeException("The lengths of the kinds and confidence do not match");
        }
        double sum = 0;
        for (double d : confidences) {
            sum += d;
        }
        values = new HashMap<>();
        for (int i = 0; i < kind.length; i++) {
            if (confidences[i] > 0.0) {
                values.put(kind[i], confidences[i] / sum);
            }
        }

        //// sort by value
        Comparator<Map.Entry<MelodicAnalysisNoteKinds, Double>> byMapValues = new Comparator<Map.Entry<MelodicAnalysisNoteKinds, Double>>() {
            @Override
            public int compare(Map.Entry<MelodicAnalysisNoteKinds, Double> left, Map.Entry<MelodicAnalysisNoteKinds, Double> right) {
                return -left.getValue().compareTo(right.getValue()); // sort descending
            }
        };

        // create a list of map entries
        sortedValues = new ArrayList<>();

        // add all candy bars
        sortedValues.addAll(values.entrySet());

        // sort the collection
        Collections.sort(sortedValues, byMapValues);

    }


    //TODO Test

    /**
     * @return Map sorted by descending probability
     */
    public List<Map.Entry<MelodicAnalysisNoteKinds, Double>> getValuesSorted() {
        return this.sortedValues;
    }

    /**
     * Best result has confidence enough
     *
     * @return
     */
    public boolean isBestResultWithConfidence() {
        if (this.sortedValues.isEmpty()) {
            return false;
        } else {
            return this.sortedValues.get(0).getValue() >= 0.6; //TODO Puesto a ojo
        }
    }

    public MelodicAnalysisNoteKinds getBestResult() {
        if (this.sortedValues.isEmpty()) {
            return null;
        } else {
            return this.sortedValues.get(0).getKey();
        }
    }

    @Override
    public String toString() {
        if (sortedValues.size() == 1) {
            return this.sortedValues.get(0).getKey().getAbbreviation();
        } else if (sortedValues.isEmpty()) {
            return "";
        } else {
            if (isBestResultWithConfidence()) {
                return this.sortedValues.get(0).getKey().getAbbreviation() + "+";
            } else {
                return "?+"; //TODO Ver cómo lo hacemos visualmente
            }
        }
    }


}
