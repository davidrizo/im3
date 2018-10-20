package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.staves.Pentagram;

import java.util.HashMap;

/**
 * It converts the humdrum matrix into a song
 */
public class HumdrumMatrix2ScoreSong {
    ScoreSong scoreSong;
    HashMap<Integer, NotationType> spineNotationType;
    HashMap<Integer, ScorePart> spineInPart;
    HashMap<Integer, Staff> spineInStaff;
    HashMap<Integer, ScoreLayer> spineInLayer;


    public ScoreSong convert(HumdrumMatrix humdrumMatrix) throws IM3Exception {
        scoreSong = new ScoreSong();
        spineNotationType = new HashMap<>();
        spineInPart = new HashMap<>();
        spineInStaff = new HashMap<>();
        spineInLayer = new HashMap<>();

        for (int row=0; row<humdrumMatrix.getRowCount(); row++) {
            for (int spine=0; spine < humdrumMatrix.getSpineCount(row); spine++) {
                HumdrumMatrixItem item = humdrumMatrix.get(row, spine);

                if (item.getHumdrumEncoding().equals("**mens")) {
                    spineNotationType.put(spine, NotationType.eMensural);
                } else if (item.getHumdrumEncoding().equals("**kern")) {
                    spineNotationType.put(spine, NotationType.eModern);
                } else if (item.getHumdrumEncoding().equals(".")) {
                    // continuation, skip
                } else {
                    if (item.getParsedObject() instanceof ScorePart) {
                        ScorePart part = (ScorePart) item.getParsedObject();
                        scoreSong.addPart(part);
                        spineInPart.put(spine, part);
                    } else if (item.getParsedObject() instanceof Staff) {
                        Staff staff = (Staff) item.getParsedObject();
                        NotationType notationType = spineNotationType.get(spine);
                        if (notationType == null) {
                            throw new IM3Exception("Missing notation type (**mens or **kern) for staff in spine #" + spine);
                        }
                        staff.setNotationType(notationType);
                        scoreSong.addStaff(staff);
                        spineInStaff.put(spine, staff);
                        ScorePart part = getSpinePart(spine);
                        part.addStaff(staff);
                    } else if (item.getParsedObject() instanceof Clef) {
                        ScoreLayer layer = getSpineLayer(spine);
                        Clef clef = (Clef) item.getParsedObject();
                        clef.setTime(layer.getDuration());
                        layer.getStaff().addClef(clef);
                    } else if (item.getParsedObject() instanceof Key) {
                        ScoreLayer layer = getSpineLayer(spine);
                        Key key = (Key) item.getParsedObject();
                        KeySignature keySignature = new KeySignature(layer.getStaff().getNotationType(), key);
                        keySignature.setTime(layer.getDuration());
                        layer.getStaff().addKeySignature(keySignature);
                    } else if (item.getParsedObject() instanceof TimeSignature) {
                        ScoreLayer layer = getSpineLayer(spine);
                        TimeSignature timeSignature = (TimeSignature) item.getParsedObject();
                        timeSignature.setTime(layer.getDuration());
                        layer.getStaff().addTimeSignature(timeSignature); //TODO MeterSign y Fractional
                    } else if (item.getParsedObject() instanceof MarkBarline) {
                        ScoreLayer layer = getSpineLayer(spine);
                        MarkBarline markBarline = (MarkBarline) item.getParsedObject();
                        markBarline.setTime(layer.getDuration());
                        layer.getStaff().addMarkBarline(markBarline);
                    } else if (item.getParsedObject() instanceof Atom) {
                        ScoreLayer layer = getSpineLayer(spine);
                        Atom atom = (Atom) item.getParsedObject();
                        layer.add(atom);
                    } else if (item.getHumdrumEncoding().equals("!sb")) {
                        //TODO Normalizar esto
                        ScoreLayer layer = getSpineLayer(spine);
                        PartSystemBreak partSystemBreak = new PartSystemBreak(layer.getDuration(), true);
                        layer.getStaff().addSystemBreak(partSystemBreak);

                    } else if (item.getHumdrumEncoding().equals("!pb")) {
                        //TODO Normalizar esto
                        ScoreLayer layer = getSpineLayer(spine);
                        PartPageBreak partPageBreak = new PartPageBreak(layer.getDuration(), true);
                        layer.getStaff().addPageBreak(partPageBreak);
                    } else if (!item.getHumdrumEncoding().equals("!")) {
                    //TODO Acabar
                        System.err.println("TO-DO: " + item.getHumdrumEncoding());
                    }
                }


            }
        }

        return scoreSong;
    }

    private ScoreLayer getSpineLayer(int spine) throws IM3Exception {
        ScoreLayer scoreLayer = spineInLayer.get(spine);
        if (scoreLayer == null) {
            Staff staff = getSpineStaff(spine);
            ScorePart scorePart = getSpinePart(spine);
            scoreLayer = scorePart.addScoreLayer(staff);
            spineInLayer.put(spine, scoreLayer);
        }
        return scoreLayer;
    }

    private ScorePart getSpinePart(int spine) {
        ScorePart part = spineInPart.get(spine);
        if (part == null) {
            part = scoreSong.addPart();
            spineInPart.put(spine, part);
        } //TODO ¿Si sale *Iinstrumento?

        return part;
    }

    private Staff getSpineStaff(int spine) {
        Staff staff = spineInStaff.get(spine);
        if (staff == null) {
            staff = new Pentagram(scoreSong, ""+ scoreSong.getStaves().size(), scoreSong.getStaves().size());
            spineInStaff.put(spine, staff);
        }

        return staff;
    }

}
