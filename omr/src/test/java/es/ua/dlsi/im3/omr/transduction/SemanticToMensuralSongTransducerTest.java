package es.ua.dlsi.im3.omr.transduction;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.omr.model.pojo.SemanticSymbolEnum;
import es.ua.dlsi.im3.omr.model.pojo.SemanticToken;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class SemanticToMensuralSongTransducerTest {

    @Test
    public void transduceInto() throws IM3Exception {
        LinkedList<SemanticToken> tokenLinkedList = new LinkedList<SemanticToken>();
        tokenLinkedList.add(new SemanticToken(SemanticSymbolEnum.clef, "G2"));
        tokenLinkedList.add(new SemanticToken(SemanticSymbolEnum.timeSignature, "C"));
        tokenLinkedList.add(new SemanticToken(SemanticSymbolEnum.rest, "minim"));
        tokenLinkedList.add(new SemanticToken(SemanticSymbolEnum.note, "E4_minim"));

        SemanticToMensuralSongTransducer toMensuralSongTransducer = new SemanticToMensuralSongTransducer();
        ScoreSong scoreSong = new ScoreSong();
        ScorePart part = scoreSong.addPart();
        Pentagram staff = new Pentagram(scoreSong, "1", 1);
        part.addStaff(staff);
        ScoreLayer scoreLayer = part.addScoreLayer();
        scoreLayer.setStaff(staff);

        toMensuralSongTransducer.transduceInto(tokenLinkedList, staff, scoreLayer);
        assertEquals("Clef", new ClefG2(), staff.getClefAtTime(Time.TIME_ZERO));
        assertEquals("Meter", new TimeSignatureCommonTime(NotationType.eMensural), staff.getTimeSignatureWithOnset(Time.TIME_ZERO));
        TreeSet<Atom> atoms = scoreLayer.getAtomsSortedByTime();
        assertEquals("Atoms", 2, atoms.size());
        Iterator<Atom> iter = atoms.iterator();
        Atom atom0 = iter.next();
        Atom atom1 = iter.next();
        assertTrue("Rest", atom0 instanceof SimpleRest);
        assertEquals("Rest figure", Figures.MINIM, ((SimpleRest)atom0).getAtomFigure().getFigure());
        assertTrue("Note", atom1 instanceof SimpleNote);
        assertEquals("Note figure", Figures.MINIM, ((SimpleNote)atom1).getAtomFigure().getFigure());
        assertEquals("Note pitch", new ScientificPitch(PitchClasses.E, 4), ((SimpleNote)atom1).getPitch());
    }
}