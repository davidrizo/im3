package es.ua.dlsi.im3.mavr.model.harmony;

import es.ua.dlsi.im3.core.adt.graph.INodeLabel;
import es.ua.dlsi.im3.core.score.Key;
import es.ua.dlsi.im3.core.score.Mode;
import es.ua.dlsi.im3.core.score.ScientificPitch;
import es.ua.dlsi.im3.core.utils.Sonority;
import org.harmony_analyser.jharmonyanalyser.chord_analyser.Chord;
import org.harmony_analyser.jharmonyanalyser.chord_analyser.Chordanal;
import org.harmony_analyser.jharmonyanalyser.chord_analyser.Tone;

import java.util.Objects;
import java.util.SortedSet;

/**
 * @autor drizo
 */
public class NodeChordLabel implements INodeLabel {
    private final int index;
    Sonority sonority;
    org.harmony_analyser.jharmonyanalyser.chord_analyser.Key key;
    Chord chord;
    Tone root;

    public NodeChordLabel(int index, Key key, Sonority sonority) {
        this.index = index;
        this.sonority = sonority;
        StringBuilder kstringBuilder = new StringBuilder();
        kstringBuilder.append(key.getPitchClass().toString());
        kstringBuilder.append(' ');
        if (key.getMode() == Mode.MINOR) {
            kstringBuilder.append("minor");
        } else {
            kstringBuilder.append("major");
        }
        this.key = Chordanal.createKeyFromName(kstringBuilder.toString());
        this.key.setIm3Name(key.getAbbreviationString());

        // chord
        StringBuilder cstringBuilder = new StringBuilder();
        for (ScientificPitch scientificPitch: sonority.getScientificPitches()) {
            if (cstringBuilder.length() > 0) {
                cstringBuilder.append(' ');
            }
            cstringBuilder.append(scientificPitch.toString());
        }
        this.chord = Chordanal.createHarmonyFromTones(cstringBuilder.toString());
        this.root = Chordanal.getRootTone(this.chord);
    }

    public Sonority getSonority() {
        return sonority;
    }

    public org.harmony_analyser.jharmonyanalyser.chord_analyser.Key getKey() {
        return key;
    }

    public Chord getChord() {
        return chord;
    }

    public Tone getRoot() {
        return root;
    }

    public int getIndex() {
        return index;
    }


}
