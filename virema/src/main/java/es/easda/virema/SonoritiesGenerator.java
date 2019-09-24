package es.easda.virema;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreAnalysisHook;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Segment;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.staves.AnalysisStaff;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SonoritiesGenerator {
    public Sonorities generateSonorities() throws IM3Exception {
        List<Segment> segmentList = new ArrayList<>();
        OpenSaveFileDialog openSaveFileDialog = new OpenSaveFileDialog();
        File file = openSaveFileDialog.openFile("VIREMA", "MusicXML", "xml");
        if (file != null) {
            MusicXMLImporter importer = new MusicXMLImporter();
            ScoreSong song = importer.importSong(file);
            AnalysisStaff analysisStaff = new AnalysisStaff(song, "99", 99);
            song.addAnalysisPart().addStaff(analysisStaff);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creating analysis hooks");
            song.createAnalysisHooksForMinimumSubdivisionsInBar(analysisStaff);
            TreeMap<Time, ScoreAnalysisHook> analysisHooks = analysisStaff.getAnalysisHooks();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "{0} analysis hooks created", analysisHooks.size());
            ScoreAnalysisHook prev = null;
            for (ScoreAnalysisHook analysisHook: analysisHooks.values()) {
                if (prev != null) {
                    segmentList.add(new Segment(prev.getTime(), analysisHook.getTime()));
                }
                prev = analysisHook;
            }
            if (prev != null) {
                segmentList.add(new Segment(prev.getTime(), song.getSongDuration()));
            }
            return new Sonorities(song, segmentList);
        } else {
            return null;
        }
    }
}
