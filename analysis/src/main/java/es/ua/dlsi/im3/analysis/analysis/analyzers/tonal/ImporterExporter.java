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

package es.ua.dlsi.im3.analysis.analysis.analyzers.tonal;


import es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.academic.melodic.MelodicAnalysisNoteKinds;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
//import es.ua.dlsi.im3.core.score.io.harmony.RomanAnalysisDegreeReader;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.io.harmony.HarmonyImporter;
import es.ua.dlsi.im3.core.score.io.kern.KernImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @deprecated Used to import old MusicXML made tonal analyses (From ICMC'07 to Euromac 2014
 * and Springer muret tonal analysis book
 * Once all corpora imported into MEI this class may be removed
 *
 * @author drizo
 */
public class ImporterExporter {

    public ScoreSong readMusicXML(File file, boolean inferMelodicAnalysisFromHarmonies) throws ImportException {
        try {
            MusicXMLImporter importer = new MusicXMLImporter();
            ScoreSong song = (ScoreSong) importer.importSong(file);
            readManualAnalysis(song, inferMelodicAnalysisFromHarmonies);
            removeInputAnalysisPart(song);
            // harmoniesToAnalyses(song);
            return song;
        } catch (Exception ex) {
            Logger.getLogger(ImporterExporter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ImportException("Error reading file " + file.getAbsoluteFile(), ex);
        }
    }

    private Harmony getOrCreateHarmony(ScoreSong song, Time time) throws IM3Exception {
        Harmony h = song.getHarmonyWithOnsetOrNull(time);
        if (h == null) {
            Harmony previousKey = song.getHarmonyActiveAtTimeOrNull(time);
            h = new Harmony(previousKey, null);
            song.addHarmony(time, h);
        }
        return h;
    }

    /**
     * It loads the manual analysis from the lyrics of the song
     *
     * @param song
     * @param inferMelodicAnalysisFromHarmonies
     */
    private void readManualAnalysis(ScoreSong song, boolean inferMelodicAnalysisFromHarmonies)
            throws IM3Exception, ImportException {

        Key previousKey = song.getUniqueKeyWithOnset(Time.TIME_ZERO);
        KernImporter harmImporter = new KernImporter();
        HarmonyImporter harmonyImporter = new HarmonyImporter();
        // important to have them sorted to work with them with previous key
        TreeSet<AtomPitch> atomPitches = song.getAtomPitchesSortedByTimeStaffAndPitch();
        Key EMPTY = new Key(0, Mode.UNKNOWN); // it will be replaced later

        for (AtomPitch n: atomPitches) {
            // boolean found = false;
            if (n.getLyrics() != null) {
                Key [] key = null;
                TonalFunction [] tonalFunction = null;
                Harm harm = null;

                for (Iterator<Map.Entry<Integer, ScoreLyric>> iter = n.getLyrics().entrySet()
                        .iterator(); iter.hasNext();) {
                    Map.Entry<Integer, ScoreLyric> lyric = iter.next();
                    String string = lyric.getValue().getText();


                    MelodicAnalysisNoteKinds kind = MelodicAnalysisNoteKinds
                            .findAbbrForMelodicAnalysisStr(string);

                    if (kind != null) {
                        n.setMelodicFunction(MelodicAnalysisNoteKinds.melodicAnalysisKindToMelodicFunction(kind));
                    } else {
                        boolean accepted = false;
                        if (kind != null) {
                            n.setMelodicFunction(MelodicAnalysisNoteKinds.melodicAnalysisKindToMelodicFunction(kind));
                            iter.remove();
                        } else {
                            Logger.getLogger(ImporterExporter.class.getName()).log(Level.FINE, "Parsing harmony '" + string + "'");
                            // important to set the tonal function first to
                            // avoid the D to be parsed as a D key instead
                            // of Dominant
                            if (!string.startsWith("I") && !string.startsWith("V")) { // avoid unnecessary failed parsings
                                if (tonalFunction == null) {
                                    try {
                                        tonalFunction = harmonyImporter.readTonalFunction(string);
                                        accepted = tonalFunction != null;
                                    } catch (ImportException e) {
                                        // not a tonal function
                                    }
                                }

                                if (!accepted) {
                                    if (key == null) {
                                        try {
                                            key = harmonyImporter.readKey(string);
                                            accepted = key != null;
                                        } catch (ImportException e) {
                                            // not a key
                                        }
                                    }
                                }
                            }

                            if (!accepted) {
                                if (harm == null) {
                                    try {
                                        harm = harmImporter.readHarmony(EMPTY, string);
                                        accepted = true;
                                    } catch (Exception e) {
                                        // it was not a degree specification
                                    }
                                }
                            }
                        }
                    }
                }

                if (harm == null && (tonalFunction != null || key != null)) {
                    throw new ImportException("There is no degree (harm) but there are tonal function or key in lyrics: " + n.getLyrics());
                }
                if (key == null) {
                    key = new Key[1];
                    key[0] = previousKey;
                }

                if (harm != null) {
                    harm.setTime(n.getTime());

                    if (harm.getAlternate() != null) {
                        if (key.length != 2) {
                            throw new ImportException("Expecting two keys (e.g. CM[GM]) for a pivot harmony: " + n.getLyrics());
                        }
                        harm.setKey(key[0]);
                        harm.getAlternate().setKey(key[1]);
                        previousKey = key[1];

                        if (tonalFunction.length != 2) {
                            throw new ImportException("Expecting two tonal functions (e.g. CM[GM]) for a pivot harmony: " + n.getLyrics());
                        }
                        harm.setTonalFunction(tonalFunction[0]);
                        harm.getAlternate().setTonalFunction(tonalFunction[1]);

                    } else {
                        if (key.length != 1) {
                            throw new ImportException("Expecting one key (e.g. CM) for non a pivot harmony: " + n.getLyrics());
                        }
                        harm.setKey(key[0]);
                        previousKey = key[0];

                        if (tonalFunction.length != 1) {
                            throw new ImportException("Expecting one tonal function (e.g. CM) for non a pivot harmony: " + n.getLyrics());
                        }
                        harm.setTonalFunction(tonalFunction[0]);

                    }
                    song.addHarm(harm);
                }
            }
        }
    }

    public void writeXML(ScoreSong song, File file) throws IM3Exception, ExportException {
		/*HashMap<Staff, ScoreLyricVerse> lyricLines = new HashMap<>();
		// create lyrics for the analyses
		ArrayList<AtomPitch> atomPitches = song.getAtomPitches();
		for (AtomPitch n: atomPitches) {
			Staff staff = n.getStaff();
			ScoreLyricVerse ssl = staff.createAndAddScoreLyrics();
			lyricLines.put(staff, ssl);
			ScoreLyric sl = new ScoreLyric(ssl, n, MelodicAnalysisNoteKinds.melodicFunctionToMelodicAnalysisKind(n.getMelodicFunction()).getAbbreviation());
			n.addLyric(ssl, sl);
		}*/

        // export
		/*MusicXMLExporter exporter = new MusicXMLExporter();
		exporter.exportSong(file, song);

		for (ScorePart part : song.getParts()) {
			List<Staff> staves = part.getStaves();
			for (Staff staff : staves) {
				ScoreLyricVerse ssl = lyricLines.get(staff);
				if (ssl != null) {
					staff.removeScoreLyrics(ssl);
					for (StaffLayer layer : staff.getLayers()) {
						List<LayeredCoreSymbol> notes = layer.getCoreSymbols();
						for (LayeredCoreSymbol lcs : notes) {
							if (lcs instanceof ScoreDurationalSymbol) {
								ScoreDurationalSymbol n = (ScoreDurationalSymbol) lcs;
								n.removeLyric(ssl);
							}
						}
					}
				}
			}
		}*/
    }

	/*
	 * private void checkStaves(ScoreSong song) throws IM2Exception { for
	 * (ScorePart<ScoreDurationalElement<FiguresModern>> part: song.getParts())
	 * { if (part.getStaves().isEmpty()) { ScoreStaff ss = part.addStaff(); if
	 * (part.needsF4Clef()) { ss.addClef(0, ScoreClef.F4); } else {
	 * ss.addClef(0, ScoreClef.G2); } // add staff info to the notes for
	 * (ScoreDurationalSymbol<FiguresModern> e:
	 * part.getDurationalSymbolsSortedByTime()) { e.setStaff(ss); } } } }
	 */

	/*
	 * private void checkClefs(ScoreSong song) { for
	 * (ScorePart<ScoreDurationalElement<FiguresModern>> part: song.getParts())
	 * { for (ScoreStaff ss: part.getStaves()) { if (ss.getClefs().isEmpty() &&
	 * !(ss instanceof ScoreAnalysisStaff)) { if (ss.needsF4Clef()) {
	 * ss.addClef(0, ScoreClef.F4); } else { ss.addClef(0, ScoreClef.G2); } } }
	 * } }
	 */

    /**
     * It removes the input parts that just contain rests
     *
     * @param song
     */
    private void removeInputAnalysisPart(ScoreSong song) {
        for (Iterator<ScorePart> iter = song.getParts().iterator(); iter.hasNext();) {
            ScorePart part = iter.next();
            if (!part.containsNonRests()) {
                iter.remove();
            }
        }
    }
}
