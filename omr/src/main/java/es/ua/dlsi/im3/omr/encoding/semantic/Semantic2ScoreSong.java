package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.staves.Pentagram;

/**
 * It converts the semantic encoding to a ScoreSong
 */
public class Semantic2ScoreSong {
    public ScoreSong convert(SemanticEncoding semanticEncoding, NotationType notationType) throws IM3Exception {
        String name = "Converted from semantic";
        ScoreSong song = new ScoreSong();
        Staff staff = new Pentagram(song, "1", 1); //TODO
        staff.setName(name);
        staff.setNotationType(notationType);
        song.addStaff(staff);
        ScorePart scorePart = song.addPart();
        scorePart.setName(name);
        scorePart.addStaff(staff);
        ScoreLayer scoreLayer = scorePart.addScoreLayer(staff);
        //TODO De momento sólo generamos un pentagrama

        if (notationType == NotationType.eModern) {
            Measure measure = new Measure(song, 1);
            song.addMeasure(Time.TIME_ZERO, measure); // first measure
        }

        SemanticSymbolType propagated = null;
        for (SemanticSymbol semanticSymbol: semanticEncoding.getSymbols()) {
            propagated = semanticSymbol.getSymbol().semantic2ScoreSong(scoreLayer, propagated);
        }
        if (notationType == NotationType.eModern) {
            // if not closed by an ending line
            if (!song.getLastMeasure().hasEndTime()) {
                song.getLastMeasure().setEndTime(song.getSongDuration());
            }
        }
        return song;
    }
}
