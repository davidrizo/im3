package es.ua.dlsi.im3.core.score.io.paec;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.ISongExporter;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO Rehacerlo
/**
 *
 * @author drizo
 */
public class PaecExporter implements ISongExporter {
    private ScoreLayer voice;
    private ScoreSong scoreSong;
    private ScorePart part;
    private PaecStringExport export;
    private int runningOctave = -1;
    private AtomFigure runningRhythm = null;
    private Accidentals runningAccidental = null;
    
    public PaecStringExport exportVoiceToString(ScoreSong song, ScoreLayer voice) throws ExportException {
	try {
	    export = new PaecStringExport();
	    this.voice = voice;
	    this.scoreSong = song;
	    exportInitialClef();
	    exportInitialTimeSignature();
	    exportInitialKeySignature();
	    printSeparator();
	    exportMusicalContent();
	} catch (IM3Exception | NoMeterException e) {
	    throw new ExportException(e);
	} 	
	return export;
    }
    public PaecStringExport exportSongToString(ScoreSong song) throws ExportException {
	if (song.getParts().isEmpty()) {
	    throw new ExportException("Trying to export an empty song");
	}
	if (song.getParts().size() > 1) {
	    throw new ExportException("Only single part can be exported");
	}
	part = song.getParts().get(0);
	if (part.getLayers().isEmpty()) {
	    throw new ExportException("Trying to export a part without voices");
	}
	if (part.getLayers().size() > 1) {
	    throw new ExportException("Trying to export a part with multiple voices, only one is allowed");
	}
	return exportVoiceToString(song, part.getVoicesSortedByNumber().iterator().next());
    }
    
    @Override
    public void exportSong(File file, ScoreSong song) throws ExportException {
	String paec = exportSongToString(song).toString();
	try  (PrintStream ps = new PrintStream(file)) {
	    ps.print(paec);
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(PaecExporter.class.getName()).log(Level.SEVERE, null, ex);
	    throw new ExportException(ex);
	}

    }

    private void exportInitialClef() throws ExportException {
	/*ScoreClef clef;
	if (part.getStaves() == null) {
	    Logger.getLogger(PaecExporter.class.getName()).log(Level.INFO, "No clef specified, detecting which is the best");	    
	    clef = ScoreClef.G2; //TODO Quizás podríamos añadir una staff
	} else if (part.getStaves().size() != 1) {
	    throw new ExportException("Cannot export more than 1 staff");
	} else {
	    ScoreStaff s = part.getStaves().iterator().next();
	    try {
		clef = s.getClefAtTime(0);
	    } catch (IM2Exception ex) {
		Logger.getLogger(PaecExporter.class.getName()).log(Level.SEVERE, null, ex);
		throw new ExportException(ex);
	    }
	}
	
	// print the clef
	// TODO ver lo de mensural
	StringBuilder sb = new StringBuilder();
	sb.append('%');
	if (clef.getOctaveTransposition() == -1 && clef.getNote().equals(DiatonicPitch.G)) {
	    sb.append('g');
	} else if (clef.getOctaveTransposition() != 0) {
	    throw new ExportException("Only G2 ottava bassa clefs are allowed transposed clefs: " + clef.toString());
	}	
	if (!clef.getNote().equals(DiatonicPitch.G) && !clef.getNote().equals(DiatonicPitch.C) && !clef.getNote().equals(DiatonicPitch.F)) {
	    throw new ExportException("Only C, G, F clefs are allowed: " + clef.toString());
	}
	sb.append(clef.getNote().name()); 
	if (clef.isMensural()) {
	    sb.append('+');
	} else {
	    sb.append('-');
	}
	sb.append(clef.getLine());	
	export.append(clef, sb);*/
	//TODO REFACTORIZACION
    }

    private void exportInitialTimeSignature() throws NoMeterException, ExportException {
	   //TODO exportTimeSignature(scoreSong.getUniqueMeterWithOnset(Time.TIME_ZERO));
    }
    
    private void exportInitialKeySignature() throws NoKeyException, ExportException {
		//TODO exportKeySignature(scoreSong.getUniqueKeyWithOnset(Time.TIME_ZERO));
    }    

    private void exportTimeSignature(TimeSignature ts) throws ExportException {
        if (ts instanceof FractionalTimeSignature) {
            FractionalTimeSignature fts = (FractionalTimeSignature) ts;
            StringBuilder sb = new StringBuilder();
            sb.append('@');
            sb.append(fts.getNumerator());
            sb.append('/');
            sb.append(fts.getDenominator());
            export.append(ts, sb);
        } else {
            throw new ExportException("TODO - Not implemented yet " + ts.getClass());
        }
    }

    private void exportKeySignature(Key ks) throws ExportException {
	StringBuilder sb = new StringBuilder();
	DiatonicPitch[] alteredNotes = ks.getAlteredNoteNames();
	if (alteredNotes.length > 0) {
	    sb.append('$');
	    if (ks.getAccidental().equals(Accidentals.SHARP)) {
		sb.append('x');
	    } else if (ks.getAccidental().equals(Accidentals.FLAT)) {
		sb.append('b');
	    } else {
		throw new ExportException("Invalid accidental for key signature with altered notes: " + ks.getAccidental());
	    }
	    for (DiatonicPitch noteName : alteredNotes) {
		sb.append(noteName.name().toUpperCase());
	    }
	}
	export.append(ks, sb);
    }
    
    private void printSeparator() {
	export.append(' ');
    }

    private void exportMusicalContent() throws IM3Exception, ExportException {
        if (scoreSong.getMeaureCount() > 0) {
            for (Measure measure: scoreSong.getMeasuresSortedAsArray()) {
                exportBar(measure);
                exportBarLine(measure);
            }
        }
    }

    private void exportBarLine(Measure bar) {
	//TODO Ahora slo tenemos barrado simple
	export.append('/');
    }

    private void exportBar(Measure bar) throws IM3Exception, ExportException {
	//TODO keytimechange, irregular group, triplet, repetgroup
	/*TODO ArrayList<ScoreDurationalSymbol> elements = voice.getDurationalSymbolsWithOnsetWithin(bar);
	for (ScoreDurationalSymbol<FiguresModern> scoreSoundingElement : elements) {
	    // TODO este if no me gusta
	    if (scoreSoundingElement instanceof ModernNote) {
		exportNote((ModernNote) scoreSoundingElement);
	    } else if (scoreSoundingElement instanceof ModernRest) {
		exportRest((ModernRest) scoreSoundingElement);
	    } else {
		throw new ExportException(scoreSoundingElement.getClass().getName() + " not supported yet");
	    }
	}*/
    }
    
    /*private void exportNote(ModernNote note) throws ExportException {
	notePropsChange(note);
	//TODO faltan las grace notes
	StringBuilder sb = new StringBuilder();
	if (note.isPause()) {
	    sb.append('(');
	    sb.append(exportNoteValue(note));
	    sb.append(')');
	} else {
	    sb.append(exportNoteValue(note));
	}
	export.append(note, sb);
    }

    private StringBuilder exportNoteValue(ModernNote note) {
	StringBuilder sb = new StringBuilder();
	sb.append(note.getScorePitch().getNoteName().name().toUpperCase()); // TODO Slurs not supported yet
	//TODO Àcmo se hacen las ligadas - con slur?
	return sb;
    }
    
    
    private void exportRest(ModernRest rest) throws ExportException {
	notePropsChange(rest.getScoreDuration());
	
	if (rest.isPause()) {
	    export.append(rest, "(-)");
	} else {
	    export.append(rest, "-");
	}
    }

    private void notePropsChange(ModernNote note) throws ExportException { //TODO Podramos decir a qu notas afecta este cambio de propiedad
	if (runningAccidental == null || !runningAccidental.equals(note.getScorePitch().getAccidental())) { // TODO que no se pongan donde no hagan falta
	    if (note.getScorePitch().getAccidental().equals(Accidentals.DOUBLE_FLAT)) {
		export.append("bb");
	    } else if (note.getScorePitch().getAccidental().equals(Accidentals.FLAT)) {
		export.append('b');
	    } else if (note.getScorePitch().getAccidental().equals(Accidentals.NATURAL)) {
		export.append('n');
	    } else if (note.getScorePitch().getAccidental().equals(Accidentals.SHARP)) {
		export.append('x');
	    } else if (note.getScorePitch().getAccidental().equals(Accidentals.DOUBLE_SHARP)) {
		export.append("xx");
	    } else {
		throw new ExportException("Invalid accidental " + note.getScorePitch().getAccidental());
	    }
	    runningAccidental = note.getScorePitch().getAccidental();
	}
	
	if (runningOctave == -1 || runningOctave != note.getOctave()) {
	    switch (note.getOctave()) {
		case 1: export.append(",,,"); break;
		case 2: export.append(",,"); break;
		case 3: export.append(","); break;			
		case 4: export.append("'"); break;
		case 5: export.append("''"); break;
		case 6: export.append("'''"); break;
		case 7: export.append("''''"); break;			
		default:
		    throw new ExportException("Invalid octave: " + note.getOctave());
	    }
	    
	    
	    runningOctave = note.getOctave();
	}
	
	notePropsChange(note.getScoreDuration());
    }

    private void notePropsChange(ScoreFigureAndDots<FiguresModern> r) throws ExportException {
	if (runningRhythm == null || !runningRhythm.equals(r)) {
	    
	    switch (r.getFigure().getBeatEncoding()) {
		case 9: export.append(0); break; //TODO Longa not implementente in Figure
		case 0: export.append(0); break; 
		case 1: export.append(1); break; 		    
		case 2: export.append(2); break; 		    
		case 4: export.append(4); break; 		    
		case 8: export.append(8); break; 		    
		case 16: export.append(6); break; 		    
		case 32: export.append(3); break; 		    
		case 64: export.append(5); break; 		    
		case 128: export.append(7); break; 		    
		default:
		    throw new ExportException("Figure not exportable to PAEC: " + r.getFigure().toString() );
	    }
	    
	    for (int i=0; i<r.getDots(); i++) {
		export.append('.');
	    }
	    runningRhythm = r;
	}
    }
*/
}
