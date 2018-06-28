package org.harmony_analyser.jharmonyanalyser.chord_analyser;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to handle functional complexity and transition complexity
 *
 * version 1.0
 */

public class Harmanal {
    /* Public / Package methods */

    /**
     * Gets function roots table for a chord, sorted by distance and formatted
     */

    public static List<String> getRootsFormatted(Chord chord) {
        List<String> result = new ArrayList<>();
        DatabaseTable roots = getRoots(chord).sortByValueByFirstNumeric();

        if (!roots.isEmpty()) {
            for (List<String> key : roots.getAllKeys()) {
                result.addAll(roots.getValues(key.get(0), key.get(1)).stream()
                        .map(value -> key.get(0) + " (" + key.get(1) + ")\nroot: " + key.get(2) + " steps: " + value)
                        .collect(Collectors.toList()));
            }
        }
        return result;
    }

    /**
     * Gets the transitions table for two harmonies, sorted by distance and formatted
     */

    public static List<String> getTransitionsFormatted(Chord chord1, Chord chord2) {
        List<String> result = new ArrayList<>();

        DatabaseTable unsortedTransitions = getTransitions(chord1, chord2);
        DatabaseTable transitions = unsortedTransitions.sortByValueByFirstNumeric();
        List<List<String>> keys = transitions.getAllKeys();
        List<List<String>> values = transitions.getAllValues();

        for (int i = 0; i < keys.size(); i++) {
            result.add(keys.get(i).get(0) + ": " + keys.get(i).get(1) + "->" + keys.get(i).get(3) + " steps: " + values.get(i).get(0));
        }
        return result;
    }

    /* Chord derivation and complexity */

    /**
     * Gets the chord complexity for the chord or -1 if no roots were found
     */

    public static int getHarmonyComplexity(Chord chord) {
        DatabaseTable roots = getRoots(chord).sortByValueByFirstNumeric();
        if (roots.isEmpty()) {
            return -1;
        } else {
            return Integer.parseInt(roots.getAllValues().get(0).get(0));
        }
    }

    /**
     * Gets the derivation from the root to the chord in a given key
     */

    static List<String> getHarmonyDerivation(Chord chord, Chord root, Key key) {
        return getHarmonyDerivation(root, chord.subtractTones(root), key);
    }

    /**
     * Gets the chord complexity of the chord from the root in a given key
     */

    static int getHarmonyComplexity(Chord chord, Chord root, Key key) {
        return getHarmonyDerivation(chord,root,key).size()-1;
    }

    /* Transition complexity */

    /**
     * Gets the transition complexity for two harmonies or -1 if no common keys were found
     */

    public static int getTransitionComplexity(Chord chord1, Chord chord2) {
        if (chord1.equals(Chord.EMPTY_CHORD) || chord2.equals(Chord.EMPTY_CHORD)) {
            return 0;
        }
        DatabaseTable unsortedTransitions = getTransitions(chord1, chord2);
        DatabaseTable transitions = unsortedTransitions.sortByValueByFirstNumeric();

        if (transitions.isEmpty()) {
            return -1;
        } else {
            return Integer.parseInt(transitions.getAllValues().get(0).get(0));
        }
    }

    /**
     * Gets function roots table for a chord
     */

    static DatabaseTable getRoots(Chord chord) {
        Key key;
        DatabaseTable result = new DatabaseTable();

        // try all Major keys
        for (int i = 0; i < 12; i++) {
            key = new Key(i, Chordanal.MAJOR);
            result.addAll(getRoots(chord, key));
        }

        // try all Minor keys
        for (int i = 0; i < 12; i++) {
            key = new Key(i, Chordanal.MINOR);
            result.addAll(getRoots(chord, key));
        }
        return result;
    }

    static Chord getRootHarmony(Chord chord) {
        DatabaseTable roots = Harmanal.getRoots(chord);
        if (roots.equals(DatabaseTable.EMPTY_RESULT) || (roots.isEmpty())) {
            return Chord.EMPTY_CHORD;
        }
        List<String> rootKeys = roots.getAllKeys().get(0);
        Scanner sc1 = new Scanner(rootKeys.get(2));
        String tone1 = "";
        String tone2 = "";
        String tone3 = "";
        if (sc1.hasNext()) {
            tone1 = sc1.next();
        }
        if (sc1.hasNext()) {
            tone2 = sc1.next();
        }
        if (sc1.hasNext()) {
            tone3 = sc1.next();
        }
        return Chordanal.createHarmonyFromRelativeTones(tone1 + " " + tone2 + " " + tone3);
    }

    /**
     * Gets the common roots table for two harmonies
     */

    public static DatabaseTable getCommonRoots(Chord chord1, Chord chord2) {
        DatabaseTable roots1 = getRoots(chord1).sortByValueByFirstNumeric();
        DatabaseTable roots2 = getRoots(chord2).sortByValueByFirstNumeric();

        DatabaseTable rootsTemporary = roots1.naturalJoinByFirstAndSecond(roots2);

        List<String> commonRootsRows = rootsTemporary.getAll();
        DatabaseTable result = new DatabaseTable();
        for (String row : commonRootsRows) {
            String[] keyValue = row.split(";");
            String[] key = keyValue[0].split(",");
            String[] value = keyValue[1].split(",");
            Chord firstOption = Chordanal.createHarmonyFromRelativeTones(key[2]);
            Chord secondOption = Chordanal.createHarmonyFromRelativeTones(key[3]);
            if (firstOption.containsMapped(secondOption)) {
                result.add(key[0] + "," + key[1] + "," + key[3] + ";" + value[0] + "," + value[1]);
            } else if (secondOption.containsMapped(firstOption)) {
                result.add(key[0] + "," + key[1] + "," + key[2] + ";" + value[0] + "," + value[1]);
            }
        }

        return result;
    }

    /**
     * Gets the common roots table for two harmonies grouped by the key
     */

    static DatabaseTable getCommonRootsByKey(Chord chord1, Chord chord2) {
        DatabaseTable roots1 = getRoots(chord1).sortByValueByFirstNumeric();
        DatabaseTable roots2 = getRoots(chord2).sortByValueByFirstNumeric();

        return roots1.naturalJoinByFirst(roots2);
    }

    /**
     * Gets the common ancestors table for two harmonies
     */

    static DatabaseTable getCommonAncestors(Chord chord1, Chord chord2) {
        DatabaseTable commonRoots = getCommonRoots(chord1, chord2);
        List<String> commonRootsRows = commonRoots.getAll();

        DatabaseTable result = new DatabaseTable();

        for (String row : commonRootsRows) {
            String[] keyValue = row.split(";");
            String[] key = keyValue[0].split(",");

            Chord root = Chordanal.createHarmonyFromRelativeTones(key[2]);
            Key commonKey = Chordanal.createKeyFromName(key[0]);

            List<String> rightDerivation1 = new ArrayList<>();
            List<String> rightDerivation2 = new ArrayList<>();
            int closestAncestor = 0;
            for (List<String> derivation1 : getHarmonyDerivations(chord1, root, commonKey)) {
                for (List<String> derivation2 : getHarmonyDerivations(chord2, root, commonKey)) {
                    int commonMovesNumber;
                    if (derivation1.size() < derivation2.size()) {
                        commonMovesNumber = derivation1.size();
                    } else {
                        commonMovesNumber = derivation2.size();
                    }
                    int indexOfCommonAncestor = -1;
                    for (int i = 0; i < commonMovesNumber; i++) {
                        if (!derivation1.get(i).equals(derivation2.get(i))) {
                            break;
                        }
                        indexOfCommonAncestor++;
                    }
                    if (indexOfCommonAncestor >= closestAncestor) {
                        closestAncestor = indexOfCommonAncestor;
                        rightDerivation1 = derivation1;
                        rightDerivation2 = derivation2;
                    }
                }
            }
            result.add(key[0] + "," + key[1] + "," + rightDerivation1.get(closestAncestor) + ";" + (rightDerivation1.size() - closestAncestor -1) + "," + (rightDerivation2.size() - closestAncestor -1));
        }
        return result;
    }

    /**
     * Gets the transitions table for two harmonies
     */

    static DatabaseTable getTransitions(Chord chord1, Chord chord2) {
        DatabaseTable result = new DatabaseTable();

        // Transition between T/S/D
        DatabaseTable commonKeys = getCommonRootsByKey(chord1, chord2);
        List<List<String>> keys1 = commonKeys.getAllKeys();
        List<List<String>> values1 = commonKeys.getAllValues();
        for (int i = 0; i < keys1.size(); i++) {
            result.add(keys1.get(i).get(0) + "," + keys1.get(i).get(1) + "," + Integer.parseInt(values1.get(i).get(0)) + "," + keys1.get(i).get(2) + "," + Integer.parseInt(values1.get(i).get(1)) + ";" + (Integer.parseInt(values1.get(i).get(0)) + Integer.parseInt(values1.get(i).get(1))));
        }

        // Transition amongst T/S/D
        DatabaseTable commonAncestors = getCommonAncestors(chord1, chord2);
        if (commonAncestors.equals(DatabaseTable.EMPTY_RESULT)) {
            return DatabaseTable.EMPTY_RESULT;
        }
        List<List<String>> keys2 = commonAncestors.getAllKeys();
        List<List<String>> values2 = commonAncestors.getAllValues();
        for (int i = 0; i < keys2.size(); i++) {
            result.add(keys2.get(i).get(0) + "," + keys2.get(i).get(1) + "," + Integer.parseInt(values2.get(i).get(0)) + "," + keys2.get(i).get(1) + "," + Integer.parseInt(values2.get(i).get(1)) + ";" + (Integer.parseInt(values2.get(i).get(0)) + Integer.parseInt(values2.get(i).get(1))));
        }

        return result;
    }

    /* Private methods */

    /**
     * Fills all permutation from startList (empty list) to endList (list of Tones) into result
     */

    private static void permutateListOfTones(List<Tone> startList, List<Tone> endList, List<List<Tone>> result) {
        if (endList.size() <= 1) {
            List<Tone> permResult = new ArrayList<>();
            permResult.addAll(startList);
            permResult.addAll(endList);
            result.add(permResult);
        } else {
            for (int i = 0; i < endList.size(); i++) {
                List<Tone> newEndList = new ArrayList<>();
                for ( int j = 0; j < i; j++ ) newEndList.add(endList.get(j));
                for ( int j = i+1; j < endList.size(); j++ ) newEndList.add(endList.get(j));

                List<Tone> newStartList = new ArrayList<>();
                newStartList.addAll(startList);
                newStartList.add(endList.get(i));

                permutateListOfTones(newStartList, newEndList, result);
            }
        }
    }

    /**
     * Gets the remaining tone of the function root if it was not complete
     */

    private static Tone getRootCompletionTone(Chord root, Key key) {
        Tone result = Tone.EMPTY_TONE;
        if (root.tones.size() != 2) {
            return result;
        }
        if (key.getTonic().containsMapped(root)) {
            for (Tone functionTone : key.getTonic().tones) {
                if (!root.containsMapped(functionTone)) {
                    result = functionTone;
                }
            }
        }
        if (key.getSubdominant().containsMapped(root)) {
            for (Tone functionTone : key.getSubdominant().tones) {
                if (!root.containsMapped(functionTone)) {
                    result = functionTone;
                }
            }
        }
        if (key.getDominant().containsMapped(root)) {
            for (Tone functionTone : key.getDominant().tones) {
                if (!root.containsMapped(functionTone)) {
                    result = functionTone;
                }
            }
        }
        return result;
    }

    /**
     * Gets function roots table for a chord for specific key
     */

    private static DatabaseTable getRoots(Chord chord, Key key) {
        DatabaseTable result = new DatabaseTable();
        DatabaseTable rows;

        if (!(rows = getRoots(chord, key.getTonic(), Chordanal.TONIC, key)).equals(DatabaseTable.EMPTY_RESULT)) {
            result.addAll(rows);
        }
        if (!(rows = getRoots(chord, key.getSubdominant(), Chordanal.SUBDOMINANT, key)).equals(DatabaseTable.EMPTY_RESULT)) {
            result.addAll(rows);
        }
        if (!(rows = getRoots(chord, key.getDominant(), Chordanal.DOMINANT, key)).equals(DatabaseTable.EMPTY_RESULT)) {
            result.addAll(rows);
        }
        return result;
    }

    /**
     * Gets function roots table for a chord for specific key and function
     */

    private static DatabaseTable getRoots(Chord chord, Chord function, int functionSign, Key key) {
        DatabaseTable result = new DatabaseTable();
        Chord common;

        common = chord.getCommonTones(function);

        if (common.tones.size() == 0) {
            return DatabaseTable.EMPTY_RESULT;
        }

        if (common.tones.size() == 3) {
            result.add(Chordanal.getKeyName(key) + "," + Chordanal.getFunctionName(functionSign) + "," + common.getToneNamesMapped() + ";" + getHarmonyComplexity(chord, Chordanal.createHarmonyFromRelativeTones(common.getToneNamesMapped()),key));
        } else if (common.tones.size() == 2) {
            Tone rootTone = Chordanal.getRootTone(function);
            if (rootTone.equals(Tone.EMPTY_TONE)) {
                return DatabaseTable.EMPTY_RESULT;
            }
            if ((common.tones.get(0).getNumberMapped() == rootTone.getNumberMapped()) || (common.tones.get(1).getNumberMapped() == rootTone.getNumberMapped())) {
                result.add(Chordanal.getKeyName(key) + "," + Chordanal.getFunctionName(functionSign) + "," + common.getToneNamesMapped() + ";" + getHarmonyComplexity(chord, Chordanal.createHarmonyFromRelativeTones(common.getToneNamesMapped()),key));
            }
        }
        return result;
    }

    /**
     * Gets the derivation from the root to the harmony in a given key, specifying the order of adding tones
     */

    private static List<String> getHarmonyDerivation(Chord root, List<Tone> added, Key key) {
        List<String> result = new ArrayList<>();
        Chord phraseForm = Chordanal.createHarmonyFromRelativeTones(root.getToneNamesMapped());

        result.add(root.getToneNamesMapped());

        boolean special;
        Tone rootCompletionTone;
        for (Tone tone : added) {
            if (key.getScaleHarmony().containsMapped(tone)) {
                // ADD operator step

                special = false;
                if (!(rootCompletionTone = getRootCompletionTone(root, key)).equals(Tone.EMPTY_TONE)) {
                    if ((root.tones.size() == 2) && (tone.getNumberMapped() == rootCompletionTone.getNumberMapped())) {
                        special = true;
                    }
                }
                if (special) {
                    // special situation - adding tone which belongs to root - for free

                    phraseForm.tones.add(tone);
                } else {
                    phraseForm.tones.add(tone);
                    result.add(phraseForm.getToneNamesMapped());
                }
            } else {
                // ADD + CHROMATIZE operators steps

                // ADD
                Tone diatonicTone = new Tone(tone.getNumber()-1);
                special = false;
                for (Tone rootTone : root.tones) {
                    if (diatonicTone.getNumberMapped() == rootTone.getNumberMapped()) {
                        special = true;
                    }
                }
                if (!(rootCompletionTone = getRootCompletionTone(root, key)).equals(Tone.EMPTY_TONE)) {
                    if (diatonicTone.getNumberMapped() == rootCompletionTone.getNumberMapped()) {
                        special = true;
                    }
                }
                if (special) {
                    // special situation - one semitone down is one of the tones of root - need to chromatize from up to down

                    diatonicTone.chromatizeUp();
                    diatonicTone.chromatizeUp();
                }

                phraseForm.tones.add(diatonicTone);
                result.add(phraseForm.getToneNamesMapped());

                // CHROMATIZE

                if (special) {
                    phraseForm.tones.get(phraseForm.tones.size()-1).chromatizeDown();
                } else {
                    phraseForm.tones.get(phraseForm.tones.size()-1).chromatizeUp();
                }
                result.add(phraseForm.getToneNamesMapped());
            }
        }
        return result;
    }

    /**
     * Gets list of all possible derivations from the root to the chord in a given key
     */

    private static List<List<String>> getHarmonyDerivations(Chord chord, Chord root, Key key) {
        List<List<String>> result = new ArrayList<>();
        List<Tone> blank = new ArrayList<>();
        List<Tone> added = chord.subtractTones(root);
        List<List<Tone>> permutations = new ArrayList<>();

        permutateListOfTones(blank, added, permutations);

        result.addAll(permutations.stream().map(list -> getHarmonyDerivation(root, list, key)).collect(Collectors.toList()));
        return result;
    }
}