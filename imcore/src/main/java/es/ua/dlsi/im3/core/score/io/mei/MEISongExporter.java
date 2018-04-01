package es.ua.dlsi.im3.core.score.io.mei;

import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.io.ISongExporter;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.io.kern.HarmExporter;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.mensural.meters.Perfection;
import es.ua.dlsi.im3.core.score.mensural.meters.TimeSignatureMensural;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import es.ua.dlsi.im3.core.score.staves.AnalysisStaff;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.metadata.Person;
/**
 * 
 * @author drizo
 *
 */
public class MEISongExporter implements ISongExporter {

    protected ScoreSong song;
	protected StringBuilder sb;
	//private KeySignature lastKeySignature;
	//private TimeSignature lastTimeSignature;
	private HashMap<Staff, Clef> lastClef;
	static DecimalFormat decimalFormat;
	private HashMap<Measure, HashMap<Staff, ArrayList<StaffMark>>> marksPerBar;

	private TimeSignature commonStartTimeSignature;
	//private ArrayList<ConnectorWithLayer> barConnectors;
	//private HashMap<Measure, HashMap<Staff, ArrayList<Attachment>>> attachmentsPerBar;
	private KeySignature commonStartKeySignature;
    private HarmExporter harmExporter;

    /**
     * Accidentals in a previous note in the measure (key = diatonic pitch+ octave*7)
     */
    private HashMap<Integer, Accidentals> previousAccidentals;

    /**
     * If true, we add @type attribute to the harm element
     */
    boolean useHarmTypes;
    public static final String VERSION = "3.0.0";
    public static final String HARM_TYPE_KEY = "key";
    public static final String HARM_TYPE_DEGREE = "degree";
    public static final String HARM_TYPE_TONAL_FUNCTION = "tonalFunction";
    private int skipMeasures;
    private BeamGroup lastBeam;

	/*FRACCIONES class ConnectorWithLayer {
		Connector<?,?> connector;
		ScoreLayer layer;
		public ConnectorWithLayer(Connector<?,?> connector, ScoreLayer layer) {
			super();
			this.connector = connector;
			this.layer = layer;
		}
		public Connector<?,?> getConnector() {
			return connector;
		}
		public ScoreLayer getLayer() {
			return layer;
		}
		
	}*/
	
	@Override
	public void exportSong(File file, ScoreSong song) throws ExportException {
		PrintStream ps = null;
		try {
			ps = new PrintStream(file, "UTF-8");
			this.song = song;
			preprocess();
			ps.print(exportSong());
		} catch (Exception e) {
			throw new ExportException(e);
		}
		if (ps != null) {
			ps.close();
		}
	}

    private int generatePreviousAccidentalMapKey(DiatonicPitch noteName, int octave) {
        return noteName.getOrder() + octave * 7;
    }


	protected void preprocess() throws IM3Exception {
	    generateMissingIDs();

        this.marksPerBar = new HashMap<>();
        this.lastBeam = null;
        this.lastClef = null;
        this.previousAccidentals = new HashMap<>();
		if (song.getStaves() != null) {
			for (Staff staff: song.getStaves()) {
				if (!(staff instanceof AnalysisStaff)) {
					for (StaffMark mark: staff.getMarks()) {
					    if (song.hasMeasures()) {
                            Measure bar = song.getMeasureActiveAtTime(mark.getTime());
                            HashMap<Staff, ArrayList<StaffMark>> barStaves = marksPerBar.get(bar);
                            if (barStaves == null) {
                                barStaves = new HashMap<>();
                                marksPerBar.put(bar, barStaves);
                            }
                            ArrayList<StaffMark> marks = barStaves.get(staff);
                            if (marks == null) {
                                marks = new ArrayList<>();
                                barStaves.put(staff, marks);
                            }
                            marks.add(mark);
                        }
					}
				}
			}
		}
		
		/*attachmentsPerBar = new HashMap<>();
		if (song.getStaves() != null) {
			for (Staff staff: song.getStaves()) {
				if (!(staff instanceof AnalysisStaff)) {
					for (Attachment attachment: staff.getAttachments()) {
						Measure bar = song.getBarActiveAtTime(attachment.getCoreSymbol().getgetTime());
						HashMap<Staff, ArrayList<StaffMark>> barStaves = marksPerBar.get(bar);
						if (barStaves == null) {
							barStaves = new HashMap<>();
							marksPerBar.put(bar, barStaves);
						}
						ArrayList<StaffMark> marks = barStaves.get(staff);
						if (marks == null) {
							marks = new ArrayList<>();
							barStaves.put(staff, marks);
						marks.add(mark);
					}
				}
			}
		}		*/
	}

    private void generateMissingIDs() {
        // TODO: 22/3/18 Hacerlo para todos los elementos - ahora lo hacemos para bars y atoms
        for (Measure measure: song.getMeasures()) {
            if (measure.__getID() == null) {
                this.generateID(measure, false);
            }
        }

        for (AtomFigure atomFigure: song.getAtomFiguresSortedByTime()) {
            if (atomFigure.getAtom().__getID() == null) {
                this.generateID(atomFigure.getAtom(), false);
            }
        }
    }

    public boolean isUseHarmTypes() {
        return useHarmTypes;
    }

    public void setUseHarmTypes(boolean useHarmTypes) {
        this.useHarmTypes = useHarmTypes;
    }

    public String exportSong(ScoreSong song) throws IM3Exception {
    		this.song = song;
    		return exportSong();
    }
    public String exportSong() throws IM3Exception {
		sb = new StringBuilder();


		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<?xml-model href=\"http://music-encoding.org/schema/3.0.0/mei-all.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n");
		sb.append("<?xml-model href=\"http://music-encoding.org/schema/3.0.0/mei-all.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n");
		sb.append("<mei xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns=\"http://www.music-encoding.org/ns/mei\" meiversion=\""+VERSION+"\">\n");
		processHead(1);
		processBeforeMusic(1);
		processMusic(1);
		sb.append("</mei>\n");
		return sb.toString();
	}

	protected void processBeforeMusic(int tabs) {
	}

	private void processHead(int tabs) {
		XMLExporterHelper.start(sb, tabs, "meiHead");
		processFileDesc(tabs+1);
		processEncodingDesc(tabs+1);
		processWorkDesc(tabs+1);
		XMLExporterHelper.end(sb, tabs, "meiHead");
	}
	

	private void processFileDesc(int tabs) {
		XMLExporterHelper.start(sb, tabs, "fileDesc");
		XMLExporterHelper.start(sb, tabs+1, "titleStmt");
		XMLExporterHelper.text(sb, tabs+2, "title", song.getTitle());
		XMLExporterHelper.end(sb, tabs+1, "titleStmt");
		XMLExporterHelper.startEnd(sb, tabs+1, "pubStmt");
		XMLExporterHelper.end(sb, tabs, "fileDesc");		
	}

	
	private void processEncodingDesc(int tabs) {
		XMLExporterHelper.start(sb, tabs, "encodingDesc");
		XMLExporterHelper.start(sb, tabs+1, "appInfo");
		XMLExporterHelper.start(sb, tabs+2, "application");
		XMLExporterHelper.text(sb, tabs+3, "name", "IM3 Java Library © David Rizo");
		XMLExporterHelper.end(sb, tabs+2, "application");
		XMLExporterHelper.end(sb, tabs+1, "appInfo");
		XMLExporterHelper.end(sb, tabs, "encodingDesc");		
	}

	private void processWorkDesc(int tabs) {
		XMLExporterHelper.start(sb, tabs, "workDesc");
		XMLExporterHelper.start(sb, tabs+1, "work");
		XMLExporterHelper.start(sb, tabs+1, "titleStmt");
		XMLExporterHelper.text(sb, tabs+2, "title", song.getTitle());
		XMLExporterHelper.start(sb, tabs+2, "respStmt");
		List<Person> persons = song.getPersons();
		if (persons != null) {
			for (Person person: persons) {
				XMLExporterHelper.text(sb, tabs+3, "persName", person.getName(), "role", person.getRole());
			}
		}
		
		XMLExporterHelper.end(sb, tabs+2, "respStmt");
		XMLExporterHelper.end(sb, tabs+1, "titleStmt");		
		XMLExporterHelper.end(sb, tabs+1, "work");
		XMLExporterHelper.end(sb, tabs, "workDesc");
		
	}
	
	private void processMusic(int tabs) throws IM3Exception {
		XMLExporterHelper.start(sb, tabs, "music");
		XMLExporterHelper.start(sb, tabs+1, "body");
		XMLExporterHelper.start(sb, tabs+2, "mdiv");
		XMLExporterHelper.start(sb, tabs+3, "score");

		commonStartTimeSignature = findFirstTimeSignature();
		commonStartKeySignature = findFirstKeySignature();
		
		ArrayList<String> params = new ArrayList<>();
		Key commonKey;
		if (commonStartKeySignature != null) {
			commonKey = commonStartKeySignature.getConcertPitchKey();
		} else {
			commonKey = null;
		}
		processScoreDef(params, commonStartTimeSignature, commonKey, null);
		// nuevo
		XMLExporterHelper.start(sb, tabs+4, "scoreDef", params);
		processStaffDef(tabs+5);
		XMLExporterHelper.end(sb, tabs+4, "scoreDef");		
		// end nuevo
		
		processSections(tabs+4);		
		XMLExporterHelper.end(sb, tabs+3, "score");
		XMLExporterHelper.end(sb, tabs+2, "mdiv");
		XMLExporterHelper.end(sb, tabs+1, "body");
		XMLExporterHelper.end(sb, tabs, "music");
	}

	/**
	 * 
	 * @return null if two different key signatures
	 */
	private KeySignature findFirstKeySignature() {
		KeySignature ks = null;
		for (Staff staff: song.getStaves()) {
			KeySignature staffKS = staff.getKeySignatureWithOnset(Time.TIME_ZERO);
			if (ks == null) {
				ks = staffKS;
			} else if (staffKS != null && (ks.getAccidentals() != null && staffKS.getAccidentals() != null && !ks.getAccidentals().equals(staffKS.getAccidentals()) ||
                    ks.getInstrumentKey() != null && staffKS.getInstrumentKey() != null && ks.getInstrumentKey().getMode() != staffKS.getInstrumentKey().getMode())) {
				return null;
			}
		}
		return ks;
	}

	private TimeSignature findFirstTimeSignature() {
		TimeSignature ks = null;
		for (Staff staff: song.getStaves()) {
			TimeSignature staffKS = staff.getTimeSignatureWithOnset(Time.TIME_ZERO);
			if (ks == null) {
				ks = staffKS;
			} else if (staffKS != null && !ks.equals(staffKS)) {
				return null;
			}
		}
		return ks;
	}

	private void processScoreDef(ArrayList<String> params, TimeSignature ts, Key key, Interval transpositionInterval) throws IM3Exception {
		if (ts != null) {
			//TODO symbol (C, C/...) meters
			if (ts instanceof FractionalTimeSignature) {
				FractionalTimeSignature fm = (FractionalTimeSignature) ts;
				params.add("meter.count");
				params.add(Integer.toString(fm.getNumerator()));
				params.add("meter.unit");
				params.add(Integer.toString(fm.getDenominator()));
			} else if (ts instanceof TimeSignatureMensural) {
				processMensuralTimeSignature(ts, params);				
			} else if (ts instanceof TimeSignatureCommonTime) {
				params.add("meter.sym");
				params.add("common");
			} else if (ts instanceof TimeSignatureCutTime) {
				params.add("meter.sym");
				params.add("cut");
			} else {
				throw new ExportException("Unknown time signature type: " + ts);
			}
		}
		if (key != null) {
			params.add("key.sig");
			if (key.getAccidental() == Accidentals.FLAT) {
				params.add((-key.getFifths()) + "f"); //TODO ¿b o f?
			} else if (key.getAccidental() == Accidentals.SHARP) {
				params.add(key.getFifths() + "s");
			} else if (key.getAccidental() == null || key.getAccidental() == Accidentals.NATURAL) {
				params.add("0");
			} else {
				throw new ExportException("Unsupported key accidental: " + key.getAccidental());
			}
			if (key.getMode() != Mode.UNKNOWN) {
                params.add("key.mode");
                params.add(key.getMode().name().toLowerCase());
            }
		}		
		
		if (transpositionInterval != null) {
			params.add("trans.diat");
			params.add(Integer.toString(-(transpositionInterval.getName()-1)));
			params.add("trans.semi");
			params.add(Integer.toString(transpositionInterval.getSemitones()));
		}
	}
	private void processSections(int tabs) throws IM3Exception {
		XMLExporterHelper.start(sb, tabs, "section");
		processSongWithoutBars(tabs+1); // e.g. mensural
		processMeasures(tabs+1); // CWMN
		XMLExporterHelper.end(sb, tabs, "section");
	}

	private void processFirstClef(Clef clef, ArrayList<String> params) throws ExportException {
		processClef(clef, params, "clef.");
	}
	
	private void processClefChange(Clef clef, ArrayList<String> params) throws ExportException {
		processClef(clef, params, "");
	}

	private void processClef(Clef clef, ArrayList<String> params, String prefix) {
		if (clef == null) {
			//throw new ExportException("Clef is null");
            Logger.getLogger(MEISongExporter.class.getName()).log(Level.WARNING, "Clef is null");
        } else {
            params.add(prefix + "line");
            params.add(Integer.toString(clef.getLine()));
            params.add(prefix + "shape");
            params.add(clef.getNote().name().toUpperCase()); //TODO percusion, tabs
            if (clef.getOctaveChange() != 0) {
                params.add(prefix + "dis");
                params.add(Integer.toString(Math.abs(clef.getOctaveChange() * 8)));
                params.add(prefix + "dis.place");
                if (clef.getOctaveChange() < 0) {
                    params.add("below");
                } else {
                    params.add("above");
                }
            }
        }
	}
	private void processStaffDef(int tabs) throws IM3Exception {
		lastClef = new HashMap<>();
		XMLExporterHelper.start(sb, tabs, "staffGrp");
		for (Staff staff: song.getStaves()) {
			if (!(staff instanceof AnalysisStaff)) {
				ArrayList<String> params = new ArrayList<>();
				params.add("n");
				params.add(getNumber(staff));
				Clef clef = staff.getClefAtTime(Time.TIME_ZERO);
				lastClef.put(staff, clef);
				processFirstClef(clef, params);
				params.add("lines");
				params.add(Integer.toString(staff.getLineCount()));
				if (staff.getName() != null) {
					params.add("label");
					params.add(staff.getName());
				}
				
				TimeSignature staffTS;
				if (commonStartTimeSignature == null) {
					staffTS = staff.getTimeSignatureWithOnset(Time.TIME_ZERO);
				} else {
					staffTS = null; // do not repeat
				}
				
				KeySignature staffKS;
				if (commonStartKeySignature == null) {
					staffKS = staff.getKeySignatureWithOnset(Time.TIME_ZERO);
				} else {
					staffKS = null;
				}
				
				if (staffTS != null || staffKS != null) {
					processScoreDef(params, staffTS, staffKS.getInstrumentKey(), staffKS.getTranspositionInterval());
					//XMLExporterHelper.startEnd(sb, tabs, "scoreDef", scoreDefParams);
				} 

				if (staff.getNotationType() == NotationType.eMensural) {
				    params.add("notationtype");
                    params.add("mensural");
                }
				XMLExporterHelper.startEnd(sb, tabs+1, "staffDef", params);
			}
		}
		XMLExporterHelper.end(sb, tabs, "staffGrp");
	}
		
	private void processMensuralTimeSignature(TimeSignature meter, ArrayList<String> params) {
		//lastTimeSignature = meter;
		TimeSignatureMensural mm = (TimeSignatureMensural) meter;
		
		if (mm instanceof TimeSignatureProporcionMenor) {
			params.add("meter.sym");
			params.add("cz"); //TODO David
		} else {
			if (mm.getModusMaior() != null) {
				params.add("modusmaior");
				params.add(mensuralTimeSignaturePerfectionToNumber(mm.getModusMaior()));
			}
			if (mm.getModusMinor() != null) {
				params.add("modusminor");
				params.add(mensuralTimeSignaturePerfectionToNumber(mm.getModusMinor()));
			}
			if (mm.getTempus() != null) {
				params.add("tempus");
				params.add(mensuralTimeSignaturePerfectionToNumber(mm.getTempus()));
			}
			if (mm.getProlatio() != null) {
				params.add("prolatio");
				params.add(mensuralTimeSignaturePerfectionToNumber(mm.getProlatio()));
			}
		}
	}

	private String mensuralTimeSignaturePerfectionToNumber(Perfection p) {
		if (p == Perfection.imperfectum) {
			return "2";
		} else if (p == Perfection.perfectum) {
			return "3";
		} else {
			throw new IM3RuntimeException("Invalid perfection: '" + p + "'");
		}
	}

	/*class SymbolsInStavesAndLayers {
		HashMap<Staff, HashMap<ScoreLayer, ArrayList<LayeredCoreSymbol>>> symbols;
		
		public SymbolsInStavesAndLayers() {
			symbols = new HashMap<>();
		}
		
		private ArrayList<LayeredCoreSymbol> getSymbolsInLayer(HashMap<ScoreLayer, ArrayList<LayeredCoreSymbol>> map, ScoreLayer layer) {
			ArrayList<LayeredCoreSymbol> result = map.get(layer);
			if (result == null) {
				result = new ArrayList<>();
				map.put(layer, result);
			}
			return result;			
		}
		public ArrayList<LayeredCoreSymbol> getSymbolsInLayer(Staff staff, ScoreLayer layer) {
			HashMap<ScoreLayer, ArrayList<LayeredCoreSymbol>> map = symbols.get(staff);
			if (map == null) {
				map = new HashMap<>();
				symbols.put(staff, map);
			}
			return getSymbolsInLayer(map, layer);			
		}

		public HashMap<Staff, HashMap<ScoreLayer, ArrayList<LayeredCoreSymbol>>> getSymbols() {
			return symbols;
		}
	}
	
	private int indexOfBarAtTime(Time time) throws ExportException, IM3Exception {
		Measure bar = song.getBarActiveAtTime(time);
		if (bar == null) {
			throw new ExportException("No bar found for time " + time);
		}
		return bar.getNumber();
	}*/
	
	private void processMeasures(int tabs) throws IM3Exception {
		if (song.hasMeasures()) {
			ArrayList<Measure> bars = song.getMeasuresSortedAsArray();
			Key lastKey = null;
			Key lastHarmKey = null;
			TimeSignature lastTimeSignature = null;
			boolean firstMeasure = true;
			Harm lastHarm = null;
            skipMeasures = 0; // used for multimeasure rests
			for (Measure bar : bars) {
                for (Staff staff : song.getStaves()) {
                    if (staff.hasSystemBreak(bar.getTime())) { // TODO: 24/9/17 ¿Y si está en medio de un compás?
                        XMLExporterHelper.startEnd(sb, tabs, "sb"); //TODO ID
                    }
                    if (staff.hasPageBreak(bar.getTime())) { // TODO: ¿Y si está en medio de un compás?
                        XMLExporterHelper.startEnd(sb, tabs, "pg"); //TODO ID
                    }
                }
			    if (skipMeasures > 0) {
                    skipMeasures --;
                }
                if (skipMeasures == 0) {
                    //TODO xmlid
                    boolean processScoreDef = false;

                    //boolean differentKeysInStaves = false;
                    Key barKey = null;
                    try {
                        barKey = song.getUniqueKeyWithOnset(bar.getTime());
                    } catch (IM3Exception e) {
                        //differentKeysInStaves = true;
                        throw new IM3Exception("Unsupported MEI exporting of staves with different concert pitch (sounded, not written) keys", e);
                    }

                    if (lastKey == null || barKey != null && !barKey.equals(lastKey)) {
                        processScoreDef = true;
                    }
                    TimeSignature barTimeSignature = null;
                    try {
                        barTimeSignature = song.getUniqueMeterWithOnset(bar.getTime());
                    } catch (IM3Exception e) {
                        // when multimeter music
                    }
                    if (lastTimeSignature == null || barTimeSignature != null && !barTimeSignature.equals(lastTimeSignature)) {
                        processScoreDef = true;
                    }

                    if (processScoreDef) {
                        ArrayList<String> params = new ArrayList<>();
                        processScoreDef(params, barTimeSignature, barKey, null);
                        if (!params.isEmpty()) {
                            XMLExporterHelper.startEnd(sb, tabs, "scoreDef", params);
                        }
                    }

                    //TODO metcon is also used to express incomplete or exceeding duration measures (see chor001.krn and .mei)
                    if (song.isAnacrusis() && firstMeasure) {
                        if (bar.getNumber() != null) {
                            XMLExporterHelper.start(sb, tabs, "measure", "n",
                                    Integer.toString(bar.getNumber()), "xml:id",
                                    generateID(bar, false), "metcon", "false");
                        } else {
                            XMLExporterHelper.start(sb, tabs, "measure", "xml:id",
                                    generateID(bar, false), "metcon", "false");

                        }
                    } else {
                        if (bar.getNumber() != null) {
                            XMLExporterHelper.start(sb, tabs, "measure", "n",
                                    Integer.toString(bar.getNumber()), "xml:id",
                                    generateID(bar, false));
                        } else {
                            XMLExporterHelper.start(sb, tabs, "measure", "xml:id",
                                    generateID(bar, false));
                        }
                    }
                    firstMeasure = false;

                    //FRACCIONES barConnectors = new ArrayList<>();

                    //SymbolsInStavesAndLayers symbolsInBar = new SymbolsInStavesAndLayers();
                    for (Staff staff : song.getStaves()) {
                        previousAccidentals = new HashMap<>();
                        if (!(staff instanceof AnalysisStaff) && staff.getNotationType() == NotationType.eModern) { // && bar.contains(staff)) {
                            XMLExporterHelper.start(sb, tabs + 1, "staff", "n", getNumber(staff));
                            for (ScoreLayer layer : staff.getLayers()) {
                                XMLExporterHelper.start(sb, tabs + 2, "layer", "n", getNumber(layer));
                                processBarLayer(tabs + 3, bar, staff, layer); //TODO que no salga si en la capa no hay nada
                                if (lastBeam != null) {
                                    closeBeam(tabs);
                                }
                                XMLExporterHelper.end(sb, tabs + 2, "layer");
                            }
                            XMLExporterHelper.end(sb, tabs + 1, "staff");
                            processBar(tabs + 1, bar, staff); // 20171206 - after adding fermate and trills - it was before the end of staff
                        }
                    }

                    List<Harm> harms = song.getHarmsWithOnsetWithin(bar);
                    if (harms != null) {
                        for (Harm harm : harms) {
                            if (lastHarmKey == null) {
                                lastHarmKey = song.getUniqueKeyActiveAtTime(harm.getTime());
                            }
                            if (harmExporter == null) {
                                harmExporter = new HarmExporter();
                            }
                            String tstamp = generateTStamp(bar, harm.getTime());

                            if (useHarmTypes) {
                                if (!harm.getKey().equals(lastHarmKey)) {
                                    XMLExporterHelper.startEndTextContentSingleLine(sb, tabs + 1, "harm", harm.getKey().getAbbreviationString(), "tstamp", tstamp, "type", HARM_TYPE_KEY);
                                }
                                XMLExporterHelper.startEndTextContentSingleLine(sb, tabs + 1, "harm", harmExporter.exportHarm(harm), "tstamp", tstamp, "type", HARM_TYPE_DEGREE);
                                if (harm.getTonalFunction() != null) {
                                    XMLExporterHelper.startEndTextContentSingleLine(sb, tabs + 1, "harm", harm.getTonalFunction().getAbbr(), "tstamp", tstamp, "type", HARM_TYPE_TONAL_FUNCTION);
                                }
                                lastHarmKey = harm.getKey();
                            } else {
                                // export just degree
                                XMLExporterHelper.startEndTextContentSingleLine(sb, tabs + 1, "harm", harmExporter.exportHarm(harm), "tstamp", tstamp);
                            }
                        }
                    }

                    // connectors
                    // all of theses should already be inserter for (Connector connector: song.getConnectors()) {

                    /*FRACCIONES  for (ConnectorWithLayer connector: barConnectors) {
                        generateConnector(tabs, bar, connector.getConnector(), connector.getLayer().getStaff(), connector.getLayer());
                    }*/

                    XMLExporterHelper.end(sb, tabs, "measure");
                    lastKey = barKey;
                    lastTimeSignature = barTimeSignature;
                }
			}
		}
	}

	private void processBar(int tabs, Measure bar, Staff staff) throws IM3Exception {
		// now add all marks
        if (marksPerBar != null) {
            HashMap<Staff, ArrayList<StaffMark>> mpb = marksPerBar.get(bar);
            if (mpb != null) { // not all bars have marks
                ArrayList<StaffMark> marks = mpb.get(staff);
                if (marks != null) {
                    for (StaffMark staffMark : marks) {
                        if (staffMark instanceof Fermate) {
                            Fermate fermate = (Fermate) staffMark;
                            for (Fermata fermata : fermate.getFermate().values()) {
                                if (fermata.getPosition() != PositionAboveBelow.UNDEFINED) {
                                    XMLExporterHelper.startEnd(sb, tabs, "fermata", "place", fermata.getPosition().name().toLowerCase(),
                                            "staff", getNumber(staff), "tstamp", generateTStamp(bar, staffMark.getTime()));
                                } else {
                                    XMLExporterHelper.startEnd(sb, tabs, "fermata",
                                            "staff", getNumber(staff), "tstamp", generateTStamp(bar, staffMark.getTime()));

                                }
                            }
                        } else 	if (staffMark instanceof Trill) {
                            Trill trill = (Trill) staffMark;
                            if (trill.getPosition() != PositionAboveBelow.UNDEFINED) {
                                XMLExporterHelper.startEnd(sb, tabs, "trill", "place", trill.getPosition().name().toLowerCase(),
                                        "staff", getNumber(staff), "tstamp", generateTStamp(bar, staffMark.getTime()));
                            } else {
                                XMLExporterHelper.startEnd(sb, tabs, "trill",
                                        "staff", getNumber(staff), "tstamp", generateTStamp(bar, staffMark.getTime()));

                            }
                        } else 	if (staffMark instanceof DynamicMark) {
                            XMLExporterHelper.text(sb, tabs, "dynam", ((DynamicMark) staffMark).getText(), "staff", getNumber(staff), "tstamp", generateTStamp(bar, staffMark.getTime()));
                        }
                    }
                }
            }
            //barConnectors
        }
	}

	
	private String getNumber(Staff staff) {
		return Integer.toString(staff.getNumberIdentifier());
	}

	private String getNumber(ScoreLayer layer) {
		return Integer.toString(layer.getNumber());
	}
	
	/*FRACCIONES private void generateConnector(int tabs, Measure bar, Connector<?,?> connector, Staff staff, ScoreLayer layer) throws ExportException, IM3Exception {
		if (connector instanceof Slur) {
			if (!(connector.getFrom() instanceof ITimedElement)) {
				throw new ExportException("Cannot export a slur linked to a non timed element");
			}
			if (!(connector.getTo() instanceof ITimedElement)) {
				throw new ExportException("Cannot export a slur linked to a non timed element");
			}
			ArrayList<String> params = new ArrayList<>();
			params.add("staff");
			params.add(getNumber(staff));
			params.add("layer");
			params.add(getNumber(layer));
			//String tstamp = generateTStamp(bar, ((ITimedElement)connector.getFrom()).getTime());
			//String tstamp2 = generateTStamp2(bar, ((ITimedElement)connector.getTo()).getTime()); //TODO ¿end time?
			//params.add("tstamp");
			//params.add(tstamp);
			//params.add("tstamp2");
			p//arams.add(tstamp2);
			params.add("startid");
			params.add(generateID(connector.getFrom()));
			params.add("endid");
			params.add(generateID(connector.getTo()));
			XMLExporterHelper.startEnd(sb, tabs, "slur", params);
		} else if (connector instanceof Tie) {
			//TODO TIE
			System.err.println("TO-DO Tie");
		} else if (connector instanceof Wedge) {
			String tstamp = generateTStamp(bar, ((ITimedElement)connector.getFrom()).getTime());
			String tstamp2 = generateTStamp2(bar, ((ITimedElement)connector.getTo()).getTime());
			ArrayList<String> params = new ArrayList<>();
			params.add("staff");
			params.add(getNumber(staff));
			params.add("layer");
			params.add(getNumber(layer));			
			params.add("tstamp");
			params.add(tstamp);
			params.add("tstamp2");
			params.add(tstamp2);
			params.add("form");
			if (connector instanceof DynamicCrescendo) {
				params.add("cres");
			} else if (connector instanceof DynamicDiminuendo) {
				params.add("dim");
			} else {
				throw new ExportException("Cannot export class: " + connector.getClass());
			}
			XMLExporterHelper.startEnd(sb, tabs, "hairpin", params);
			
		} else if (!(connector instanceof Beam)) {
			throw new UnsupportedOperationException("Unsupported exporting " + connector.getClass());
		} 
	}*/

    /**
     *
     * @param symbol
     * @param reference If true an # is added
     * @return
     */
	private String generateID(IUniqueIDObject symbol, boolean reference) {
		if (reference) {
			return "#" + song.getIdManager().getID(symbol);
		} else {
			return song.getIdManager().getID(symbol);
		}
	}
	
	private void processSongWithoutBars(int tabs) throws IM3Exception {
		// order notes, key changes and meter changes, then process them		
		for (int il=0; il<song.getStaves().size(); il++) {
            previousAccidentals = new HashMap<>();
			Staff staff = song.getStaves().get(il);
			//TODO En processWithBars hemos preguntado por si el staff está entre los staves del bar
			if (staff.getNotationType() == NotationType.eMensural) { 
				boolean firstLayer = true;
				if (!(staff instanceof AnalysisStaff)) {
					List<ITimedElementInStaff> staffSymbols = new ArrayList<>();
					for (Clef clef: staff.getClefs()) {
						if (!clef.getTime().isZero()) {
							staffSymbols.add(clef);
						}
					}
					for (TimeSignature ts: staff.getTimeSignatures()) {
						if (commonStartTimeSignature != null && !ts.getTime().isZero()) {
							staffSymbols.add(ts); // the first one is exported in staffDef or scoreDef
						}
					}
					for (KeySignature ks: staff.getKeySignatures()) {
						if (commonStartKeySignature != null && !ks.getTime().isZero()) {
							staffSymbols.add(ks); // the first one is exported in staffDef or scoreDef
						}
					}
					for (MarkBarline markBarline: staff.getMarkBarLines()) {
                        staffSymbols.add(markBarline);
                    }
					
					XMLExporterHelper.start(sb, tabs, "staff", "n", getNumber(staff));
					for (ScoreLayer layer: staff.getLayers()) {
						List<ITimedElementInStaff> symbols = new ArrayList<>();
						for (int i=0; i<layer.size(); i++) {
							symbols.add(layer.getAtom(i));
						}
						XMLExporterHelper.start(sb, tabs+1, "layer", "n", getNumber(layer));
						if (firstLayer) { 
							firstLayer = false;
							symbols.addAll(staffSymbols);
						}					
						//Collections.sort(symbols, ITimedElementInStaff.TIMED_ELEMENT_COMPARATOR);
                        SymbolsOrderer.sortList(symbols);
						for (ITimedElementInStaff slr : symbols) {
							if (slr instanceof Clef) { 
								ArrayList<String> params = new ArrayList<>();
								processClefChange((Clef) slr, params);
								XMLExporterHelper.startEnd(sb, tabs+2, "clef", params);							
								lastClef.put(staff, (Clef) slr);
							} else if (slr instanceof TimeSignature) {
								//TODO ¿habrá que mejor coger el TimeSignature por el @sign...
								processTimeSignature(tabs+2, (TimeSignature) slr);
							} else if (slr instanceof KeySignature) {
								throw new UnsupportedOperationException("TO-DO Key change");
							} else if (slr instanceof Atom) {
                                processAtom(tabs + 2, (Atom) slr, staff);
                            } else if (slr instanceof MarkBarline) {
							    processBarLine(sb, tabs+2, (MarkBarline)slr, staff);
							} else {
								throw new ExportException("Unsupported symbol type for export: '" + slr.getClass() + "'");
							}
						}
                        if (lastBeam != null) {
                            closeBeam(tabs);
                        }
						XMLExporterHelper.end(sb, tabs+1, "layer");
					}
					XMLExporterHelper.end(sb, tabs, "staff");
				}
			}
		}
	}

    private void processBarLine(StringBuilder sb, int tabs, MarkBarline slr, Staff staff) {
        XMLExporterHelper.startEnd(sb, tabs, "barLine");
    }


    private void processTimeSignature(int tabs, TimeSignature timeSignature) {
		if (timeSignature instanceof TimeSignatureMensural) {
			ArrayList<String> params = new ArrayList<>();
			processMensuralTimeSignature(timeSignature, params);
			XMLExporterHelper.startEnd(sb, tabs, "mensur", params);
		} else {
            System.err.println("TO-DO MEI Export !!!!!!!!!!!Modern meter change!!!!!!!!!!!!!!!!!!!!");
            //throw new UnsupportedOperationException("TO-DO Modern meter change");
		}
	}

	private void processBarLayer(int tabs, Measure bar, Staff staff, ScoreLayer layer) throws IM3Exception {
		//TODO se podría optimizar más, pero no sé si vale la pena para 10 símbolos por compás...
		List<Atom> atoms = layer.getAtomsWithOnsetWithin(bar);
		for (Atom atom: atoms) {
			Clef clefForSymbol = staff.getRunningClefAtOrNull(atom.getTime());
			if (clefForSymbol != null && !lastClef.get(staff).equals(clefForSymbol)) {
				ArrayList<String> params = new ArrayList<>();
				processClefChange(clefForSymbol, params);					
				//double timeStamp = (double) (bar.getTime() - clefForSymbol.getTime()) / (double) AbstractSong.DEFAULT_RESOLUTION;
				params.add("tstamp");
				params.add(generateTStamp(bar, clefForSymbol.getTime()));
				XMLExporterHelper.startEnd(sb, tabs, "clef", params);
				
				lastClef.put(staff, clefForSymbol);
			}
			processAtom(tabs, atom, staff); //TODO ¿Siempre vale?				
		}


		// process non durational symbols
		/*FRACCIONES List<StaffTimedPlaceHolder> placeHolders = layer.getStaffPlaceHoldersWithOnsetWithin(bar);
		for (StaffTimedPlaceHolder staffTimedPlaceHolder : placeHolders) {
			addConnectors(staffTimedPlaceHolder, staffTimedPlaceHolder.getLayer());
		}*/
	}

	public static String generateTStamp(Measure bar, Time time) throws IM3Exception {
		double tstamp = time.substract(bar.getTime()).getComputedTime(); 
		if (tstamp < 0) {
			throw new ExportException("Invalid negative tstamp: " + tstamp + ", from time=" + time + " in bar " + bar + " of time " + bar.getTime());
		}
		if (decimalFormat == null) {
			decimalFormat = new DecimalFormat();
			decimalFormat.setMinimumFractionDigits(0);
			decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		}
		return decimalFormat.format(tstamp+1);
	}

	public static String generateTStamp2(Measure bar, Time time) throws IM3Exception {
		Measure destBar = bar.getSong().getMeasureActiveAtTime(time);
		int barsCrossed = destBar.getNumber() - bar.getNumber();
		StringBuilder sb = new StringBuilder();
		sb.append(barsCrossed);
		sb.append('m');
		sb.append('+');
		sb.append(generateTStamp(destBar, time));
		return sb.toString();
	}

	// could be more ellegant !!
	private String getFigureMEIDur(Figures figure) throws ExportException {
		switch (figure) {
			case MAXIMA: return "maxima";
			case LONGA: return "longa"; 
			case BREVE: return "brevis";
			case SEMIBREVE: return "semibrevis";
			case MINIM: return "minima";
			case SEMIMINIM: return "semiminima";
			case FUSA: return "fusa";
			case SEMIFUSA: return "semifusa";
			case QUADRUPLE_WHOLE: return "long";
			case DOUBLE_WHOLE: return "breve";
			case WHOLE: return "1";
			case HALF: return "2";
			case QUARTER: return "4";
			case EIGHTH: return "8";
			case SIXTEENTH: return "16";
			case THIRTY_SECOND: return "32";
			case SIXTY_FOURTH: return "64";
			case TWO_HUNDRED_FIFTY_SIX: return "128";
			default: 
				throw new ExportException("Unsupported figure: " + figure);
			
		}
	}
	
	
	private void fillDurationParams(AtomFigure atomFigure, ArrayList<String> params) throws ExportException {
		params.add("dur");
		if (atomFigure.getFigure() == null) {
			throw new ExportException("Durational symbol has not a figure: " + atomFigure);
		}
		params.add(getFigureMEIDur(atomFigure.getFigure()));
		if (atomFigure.getDots() > 0) {
			params.add("dots");
			params.add(Integer.toString(atomFigure.getDots()));
		}
		if (atomFigure.getIrregularGroupActualFigures() != null) {
			params.add("num");
			params.add(atomFigure.getIrregularGroupActualFigures().toString());
		}
		if (atomFigure.getIrregularGroupInSpaceOfFigures() != null) {
			params.add("numbase");
			params.add(atomFigure.getIrregularGroupInSpaceOfFigures().toString());
		}
		if (atomFigure.hasColoration()) {
			params.add("colored");
			params.add(Boolean.toString(atomFigure.isColored()));			
		}
        /*if (atomFigure.getFermata() != null) {
		    params.add("fermata");
		    switch (atomFigure.getFermata().getPosition()) {
                case UNDEFINED: //TODO Correcto?
                case ABOVE:
                    params.add("above");
                    break;
                case BELOW:
                    params.add("below");
                    break;
                default:
                    throw new ExportException("Unsupported fermata position: " + atomFigure.getFermata().getPosition());
            }

        }*/
	}
	
	private void processAtom(int tabs, Atom atom, Staff defaultStaff) throws IM3Exception {
		ArrayList<String> params = new ArrayList<>();
		params.add("xml:id");
		params.add(generateID(atom, false));
		
		if (defaultStaff == null) {
			throw new IM3RuntimeException("The defaultStaff is null");
		}
		if (atom.getStaff() == null) {
			throw new IM3RuntimeException("The staff of " + atom + "  is null");
		}
		if (atom.getStaff() != defaultStaff) {
			params.add("staff");
			params.add(getNumber(atom.getStaff()));
		}

		if (atom instanceof SingleFigureAtom) {
            SingleFigureAtom sfatom = (SingleFigureAtom) atom;

            if (sfatom.getBelongsToBeam() != lastBeam) {
                if (lastBeam != null) {
                    // close previous beam, just export if not computed
                    closeBeam(tabs);
                }

                // open new beam, just export if not computed
                lastBeam = sfatom.getBelongsToBeam();
                if (lastBeam != null) {
                    XMLExporterHelper.start(sb, tabs, "beam"); //TODO ID, ¿staff?
                }

            } // else it is the same beam (on no one), no-op

			if (atom instanceof SimpleMeasureRest) {
				SimpleMeasureRest mrest = (SimpleMeasureRest) atom;
				if (mrest.getAtomFigure().getFigure() != Figures.WHOLE) { // e.g. for anacrusis
					//SingleFigureAtom sfatom = (SingleFigureAtom) atom;
					fillDurationParams(sfatom.getAtomFigure(), params);
				}
				XMLExporterHelper.startEnd(sb, tabs, "mRest", params);
			} else if (atom instanceof SimpleMultiMeasureRest) {
                SimpleMultiMeasureRest mrest = (SimpleMultiMeasureRest) atom; //TODO ID
                params.add("num");
				params.add(Integer.toString(mrest.getNumMeasures()));
				XMLExporterHelper.startEnd(sb, tabs, "multiRest", params);
                skipMeasures = mrest.getNumMeasures();
			} else {
				fillDurationParams(sfatom.getAtomFigure(), params);

				if (sfatom.getExplicitStemDirection() != null) {
                    params.add("stem.dir");
                    params.add(sfatom.getExplicitStemDirection().name().toLowerCase());
                }

				if (atom instanceof SimpleRest) {			
					XMLExporterHelper.startEnd(sb, tabs, "rest", params); 
				} else if (atom instanceof SimpleChord) {
					XMLExporterHelper.start(sb, tabs, "chord", params);
					processPitches(sb, tabs, sfatom.getAtomFigure(), atom.getAtomPitches(), params);
					//TODO Ties en chords
					XMLExporterHelper.end(sb, tabs, "chord");
				} else if (atom instanceof SimpleNote) {
					processPitches(sb, tabs-1, sfatom.getAtomFigure(), atom.getAtomPitches(), params); // -1 because process pitches adds 1
				} else {
					throw new UnsupportedOperationException("Unsupported yet: " + atom.getClass());
				}
			}
		} else if (atom instanceof SimpleTuplet) {
				SimpleTuplet tuplet = (SimpleTuplet) atom;
				params.add("num");
				params.add(Integer.toString(tuplet.getCardinality()));
				params.add("numbase");
				params.add(Integer.toString(tuplet.getInSpaceOfAtoms()));
				XMLExporterHelper.start(sb, tabs, "tuplet", params);
				for (Atom tupletAtom: tuplet.getAtoms()) {
					processAtom(tabs+1, tupletAtom, defaultStaff);
				}
				if (lastBeam != null) {
				    closeBeam(tabs);
                }
				XMLExporterHelper.end(sb, tabs, "tuplet");				
		} else {
			throw new UnsupportedOperationException("Unsupported yet: " + atom.getClass());
		}
		
		//TODO Beams...

		
	}

    private void closeBeam(int tabs) {
        if (!lastBeam.isComputed()) {
            XMLExporterHelper.end(sb, tabs, "beam");
        }
        lastBeam = null;
    }

    private void processPitches(StringBuilder sb, int tabs, AtomFigure atomFigure, List<AtomPitch> atomPitches, ArrayList<String> params) throws IM3Exception {
		if (atomPitches != null) {
			boolean multiplePitches = atomPitches.size() > 1;
			for (AtomPitch atomPitch: atomPitches) {
				ArrayList<String> noteParams = new ArrayList<>();
				if (multiplePitches) {
					noteParams.add("xml:id");
					noteParams.add(generateID(atomPitch, false));
				}
				for (int i=0; i<params.size()-1; i+=2) {
					if (!params.get(i).equals("xml:id") || !multiplePitches) {
						noteParams.add(params.get(i));
						noteParams.add(params.get(i+1));
					}
				}
				
				/*if (!multiplePitches) {
					fillDurationParams(atomFigure, noteParams);
				}*/
				processPitchesParams(atomPitch, noteParams, atomFigure.getLayer());

                if (atomPitch.getLyrics() == null || atomPitch.getLyrics().isEmpty()) {
                    XMLExporterHelper.startEnd(sb, tabs+1, "note", noteParams);
                } else {
				    XMLExporterHelper.start(sb, tabs+1, "note", noteParams);

				    if (atomPitch.getLyrics() != null && !atomPitch.getLyrics().isEmpty()) {
                        for (ScoreLyric scoreLyric : atomPitch.getLyrics().values()) {
                            XMLExporterHelper.start(sb, tabs+2, "verse", "n", scoreLyric.getVerse().toString()); //TODO ID

                            if (scoreLyric.getSyllabic() != null && scoreLyric.getSyllabic() != Syllabic.single) {
                                XMLExporterHelper.text(sb, tabs+4, "syl", scoreLyric.getText(), "wordpos", syllabic2WordPos(scoreLyric.getSyllabic()));
                            } else {
                                XMLExporterHelper.text(sb, tabs+4, "syl", scoreLyric.getText());    
                            }

                            XMLExporterHelper.end(sb, tabs+2, "verse");
                        }
                    }
                    XMLExporterHelper.end(sb, tabs+1, "note");
                }
            }
		}
		/*if (atomPitches.getContinuationPitches() != null) {
			for (AtomContinuationPitch atomContPitch: atomPitches.getContinuationPitches()) {
				ArrayList<String> noteParams = new ArrayList<>();
				noteParams.add("xml:id");
				noteParams.add(generateID(atomContPitch));
				if (generateDurations) {
					fillDurationParams(atomPitches, noteParams);
				}				
				processPitchesParams(atomContPitch.getFromPitch(), false, true, noteParams, atomPitches.getLayer());
				XMLExporterHelper.startEnd(sb, tabs+1, "note", noteParams);				
			}
		}
		addConnectors(atomPitches, atomPitches.getLayer()); //TODO*/ 
	}

    private String syllabic2WordPos(Syllabic syllabic) throws ExportException {
	    switch (syllabic) {
            case begin:
                return "i";
            case middle:
                return "m";
            case end:
                return "t";
            default:
                throw new ExportException("Unsupported syllabic: " + syllabic);
        }
    }

	/*FRACTIONS private void addNoteTies(AtomFigure note, ArrayList<String> params) {
		//TODO esto es con los acordes, con las notas estamos repitiendo lo mismo (ver processPitchesParams)
		if (note.getTiedToNext() != null) {
			params.add("tie");
			params.add("i");
		} else if (note.getTiedFrom() != null) {
			params.add("tie");
			params.add("t"); //TO-DO from + to --> "m"			
		}
	}*/

	private void addConnectors(ISymbolWithConnectors symbol, ScoreLayer layer) {
		//System.out.println("Adding: " + symbol + "---" + symbol.getConnectors());
		/*FRACCIONES if (symbol.getConnectors() != null) {
			for (Connector<?,?> connector: symbol.getConnectors()) {
				//System.out.println("\t" + connector.getClass());
				if (connector.getFrom() == symbol) {				
					barConnectors.add(new ConnectorWithLayer(connector, layer));
				}
			}
		}*/
		
	}
	private void processPitchesParams(AtomPitch atomPitch, ArrayList<String> params, ScoreLayer layer) throws IM3Exception {
		//TODO addConnectors(atomPitch.getAtomFigure(), layer);
		
		if (atomPitch.getStaffChange() != null) {
			params.add("staff");
			params.add(getNumber(atomPitch.getStaffChange()));
		}
		
		ScientificPitch scorePitch = atomPitch.getScientificPitch();
		params.add("pname");
		params.add(scorePitch.getPitchClass().getNoteName().name().toLowerCase());
		params.add("oct");
		params.add(Integer.toString(scorePitch.getOctave()));

		int prevAccMapKey = generatePreviousAccidentalMapKey(
		        scorePitch.getPitchClass().getNoteName(), scorePitch.getOctave());

		Accidentals pitchAccidental = scorePitch.getPitchClass().getAccidental();
		Accidentals previousAccidental = previousAccidentals.get(prevAccMapKey);
		if (previousAccidental == null) {
            KeySignature ks = atomPitch.getStaff().getRunningKeySignatureOrNullAt(atomPitch.getTime());
            if (ks != null) {
                previousAccidental = ks.getAccidentalOf(scorePitch.getPitchClass().getNoteName());
            }
        }

        String accidGes = null;
        if (atomPitch.getWrittenExplicitAccidental() != null || previousAccidental != pitchAccidental && !(previousAccidental == null && pitchAccidental == Accidentals.NATURAL)) {
            String accid;
            Accidentals acc;
            if (atomPitch.getWrittenExplicitAccidental() != null) {
                acc =  atomPitch.getWrittenExplicitAccidental();
            } else {
                acc = pitchAccidental;
            }
            accid = generateAccidental(acc);
            previousAccidentals.put(generatePreviousAccidentalMapKey(scorePitch.getPitchClass().getNoteName(), scorePitch.getOctave()),
                    pitchAccidental);

            params.add("accid");
            params.add(accid);
        } else if (pitchAccidental != null && pitchAccidental != Accidentals.NATURAL) {
            String accid = generateAccidental(pitchAccidental);
            previousAccidentals.put(prevAccMapKey, pitchAccidental);

            params.add("accid.ges");
            params.add(accid);

        }


		/*String accidGes = null;
		if (scorePitch.getPitchClass().getAccidental() != null &&
				scorePitch.getPitchClass().getAccidental() != Accidentals.NATURAL
				) {
				//(atomPitch.isForcePaintAccidental() || scorePitch != null)) {
			params.add("accid.ges");
			accidGes = generateAccidental(scorePitch.getPitchClass().getAccidental());
			params.add(accidGes);
            previousAccidentals.put(generatePreviousAccidentalMapKey(scorePitch.getPitchClass().getNoteName(), scorePitch.getOctave()),
                    scorePitch.getPitchClass().getAccidental());
        }
		
		if (atomPitch.getWrittenExplicitAccidental() != null) {
			String accid = generateAccidental(atomPitch.getWrittenExplicitAccidental());
            previousAccidentals.put(generatePreviousAccidentalMapKey(scorePitch.getPitchClass().getNoteName(), scorePitch.getOctave()),
                    scorePitch.getPitchClass().getAccidental());

			if (accidGes == null || !accidGes.equals(accid)) {
                params.add("accid");
                params.add(accid);
            }
		}*/

		//TODO esto es con los acordes, con las notas estamos repitiendo lo mismo ? ¿Lo dejamos así?
		if (atomPitch.isTiedToNext() && atomPitch.isTiedFromPrevious()) {
			params.add("tie");
			params.add("m");			
		} else if (atomPitch.isTiedToNext()) {
			params.add("tie");
			params.add("i");
		} else if (atomPitch.isTiedFromPrevious()) {
			params.add("tie");
			params.add("t"); //TO-DO from + to --> "m"			
		}

		if (atomPitch.getMelodicFunction() != null) {
			params.add("mfunc");
			params.add(atomPitch.getMelodicFunction().name().substring(2).toLowerCase()); // from mfSUS to sus
		}
	}

	private String generateAccidental(Accidentals accidental) throws ExportException {
		switch (accidental) {
		case DOUBLE_FLAT: 
			return "ff"; 
		case FLAT: 
			return "f";
		case NATURAL: 
			return "n"; 
		case SHARP:
			return "s";
		case DOUBLE_SHARP:
			return "ss";
		case TRIPLE_FLAT:
			return "tf";
		default:
				throw new ExportException("Unsupported accidental: "+ accidental);
		}
	}

	public ScoreSong getSong() {
		return song;
	}

	

}
