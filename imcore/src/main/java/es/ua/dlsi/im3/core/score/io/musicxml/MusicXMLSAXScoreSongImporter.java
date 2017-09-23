/*
 * Copyright (C) 2016 David Rizo Valero
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
package es.ua.dlsi.im3.core.score.io.musicxml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import es.ua.dlsi.im3.core.score.*;
import org.apache.commons.lang3.math.Fraction;
import org.xml.sax.SAXException;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.core.score.io.XMLSAXScoreSongImporter;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.core.score.staves.PercussionStaff;
import es.ua.dlsi.im3.core.io.ImportException;


/**
 * MusicXML implemented with SAX to improve performance over JAXB.
 * 
 * @author drizo
 */
public class MusicXMLSAXScoreSongImporter extends XMLSAXScoreSongImporter {
	static final HashMap<String, Figures> FIGURES = new HashMap<>();
	private static final String MIDDLE_TIE = "__middle__";
	// TODO MusicXML figures, ¿normalizar en MusicXMLImporter?
	static {
		FIGURES.put("breve", Figures.BREVE);
		FIGURES.put("whole", Figures.WHOLE);
		FIGURES.put("half", Figures.HALF);
		FIGURES.put("quarter", Figures.QUARTER);
		FIGURES.put("eighth", Figures.EIGHTH);
		FIGURES.put("16th", Figures.SIXTEENTH);
		FIGURES.put("32th", Figures.THIRTY_SECOND);
		FIGURES.put("32nd", Figures.THIRTY_SECOND);
		FIGURES.put("64th", Figures.SIXTY_FOURTH);
		FIGURES.put("128th", Figures.HUNDRED_TWENTY_EIGHTH);
		FIGURES.put("256th", Figures.TWO_HUNDRED_FIFTY_SIX);
	}
	
	//private ArrayDeque<AMPartGroup> partGroups;
	ScorePart currentScorePart;
	//private Measure currentMeasure;
	Atom currentNote;
	AtomPitch lastAtomPitch;
	Integer currentDivisions;
	Accidentals lastAccidental;
	HashMap<String, Staff> staffNumbers;
	HashMap<String, ScoreLayer> voiceNumbers;
	String lastStaffNumber;
	String lastVoiceNumber;
	//AMTuplet lastTuplet;
	//HashMap<String, AMBeam> currentBeams;
	//private HashMap<String, AMSlur> currentSlurs;
	private HashMap<String, AtomPitch> openedTiesFrom;
	String lastInsertedTieTag;  
	//AMBeam lastBeam;
	HashMap<Integer, String> lastLyrics;
    private Integer lastLyricNumber;
	//Clef lastClef;
	//AMHairpin currentHairpin;
	HierarchicalIDGenerator hierarchicalIdGenerator;
	ArrayList<ITimedElementInStaff> tupletAtoms;
	Integer tupletActualNotes;
	Integer tupletNormalNotes;
	Figures tupletNormalFigure;
	Time tupletAccumulatedTime;
	
	String lastTieNumber;
	String lastTieType;


	/**
	 * Used to disambiguate symbols like duration that can be found in note or backup symbols
	 */
	enum eContexts {
		eNote, eForward, eBackup, eChord, eRest, eMeter, eClef, eTranspose
	}
	private eContexts context;
	private Staff defaultStaff; // the staff when "staff" element has not been specified
    private String noNumberStaffNumber; // the number given to the staff when it had no number
	private ScoreLayer defaultVoice;	// the voice when "voice" element has not been specified
	private Integer lastStaffLines;
	//private String lastStaffType;
	private HashMap<String, ScorePart> partNumbers;
	private int dots;
	private Time lastDuration;
	//AMStaffPlaceHolder currentStaffPlaceHolder;
	private Integer lastOctave;
	private DiatonicPitch lastNoteName;
	private String keyMode;
	private Integer keyFifiths;
	private String meterBeats;
	private String meterBeatType;
	private String meterSymbol;
	private String clefSign;
	private Integer clefLine;
	private Integer clefOctaveChange;
	private TreeMap<StaffAndVoice, ArrayList<ITimedElementInStaff>> measureElementsToInsert;
	private SingleFigureAtom lastAtom;
	private String currentMeasureNumber;
	//private Time currentMeasureTime;
	private Figures lastFigure;
	private String tupletNormalFigureStr;
	private Time tupletExpectedDuration;
	private Time measureStartTime;
	private Integer keyTransposeChromatic;
	private Integer keyTransposeDiatonic;
	private KeySignature lastKey;
	private String measureRest;
	private TimeSignature lastTimeSignature;

	@Override
	protected void init() throws ParserConfigurationException, SAXException {
		song = new ScoreSong();
		currentDivisions = null;
		partNumbers  = new HashMap<>();
		hierarchicalIdGenerator = new HierarchicalIDGenerator();
		//partGroups = new ArrayDeque<>();
	}
	
	private String getLayerCode(Staff staff, ScoreLayer voice) throws ImportException {
		if (staff == null) {
			throw new ImportException("staff=null");
		}
		if (voice == null) {
			throw new ImportException("voice=null");
		}
		//String code = lastStaff==null?"_":lastStaff.hashCode() + "_" + lastVoice.hashCode();
		String code = staff.hashCode() + "_" + voice.hashCode();
		return code;
	}
	
	/*private Time getCurrentTime(Staff staff, ScoreLayer voice) throws ImportException, IM3Exception {
		String code = getLayerCode(staff, voice);
		Time time = currentTime.get(code);
		if (time == null) {
			Time lastTime = voice.getDuration();
			currentTime.put(code, lastTime);
			return lastTime;
		} else {
			return time;
		}
	}
	private void addToCurrentTime(Staff staff, ScoreLayer voice, Time amount) throws ImportException, IM3Exception {
		String code = getLayerCode(staff, voice);
		Time time = currentTime.get(code);
		Time newTime;
		if (time == null) {
			newTime = amount;
		} else {
			newTime = time.add(amount);
		}
		currentTime.put(code, newTime);
	}	
	private void substractFromCurrentTime(Staff staff, ScoreLayer voice, Time amount) throws ImportException, IM3Exception {
		String code = getLayerCode(staff, voice);
		Time time = currentTime.get(code);
		Time newTime;
		if (time == null) {
			newTime = amount;
		} else {
			newTime = time.substract(amount);
		}
		currentTime.put(code, newTime);
	}		*/
	@Override
	protected void doHandleOpenElement(String element, HashMap<String,String> attributes) throws ImportException {
		String ID;
		String type;
		String value;
		String number;
		Staff staff;
		
		try {
			switch (element) {
			case "score-timewise":
				throw new ImportException("Unsupported format: score-timewise");
			case "score-partwise":
				break;
			case "work": // score-partwise.work
				//TODOsong.setWork(new AMWork());
				break;
			case "movement-title": // score-partwise.movement-title
			case "movement-number": // score-partwise.movement-number
				//TODO if (song.getMovement() == null) {
					//song.setMovement(new AMMovement());
				//}
				break;
			case "identification":// score-partwise.identification
				//TODO song.setIdentification(new AMIdentification());
				break;
			case "creator":// score-partwise.identification.creator
				// unimplemented score-partwise.identification subelements:
				// rights, encoding, source, relation, miscellaneous
				value = getAttribute(attributes, "type");
				expectedContentForElement.add(element, value);
				break;
			// unimplemented score-partwise subelements: defaults, credit
			case "part-group": // score-partwise.part-list.part-group
				// unimplemented all the other part-group subelements
				// (group-name-display...)
				/*type = getAttribute(attributes, "type");
				number = getAttribute(attributes, "number");
				if ("start".equals(type)) {
					AMPartGroup partGroup = new AMPartGroup(number);
					if (partGroups.isEmpty()) {
						song.addPartGroup(partGroup);
					} else {
						partGroups.peek().addSubgroup(partGroup);
					}
					partGroups.add(partGroup);
				} else if ("stop".equals(type)) {
					partGroups.pop();
				}*/
				break;
			case "part-list":
				break;
			case "score-part": // score-partwise.part-list.score-part
				currentScorePart = song.addPart();
				ID = getOptionalAttribute(attributes, "id");
				if (ID != null) { 
					partNumbers.put(ID, currentScorePart);
				}
				break;		
			// unimplemented score-partwise.part-list.score-part.identification
			case "part-name": // score-partwise.part-list.score-part.part-name
				break; // handled in handleElementContent
				// unimplemented rest of score-part subelements
			case "part":
				ID = getAttribute(attributes, "id");
				currentScorePart = getPart(ID);
				measureStartTime = Time.TIME_ZERO;
				currentDivisions = null; 
				defaultStaff = null;
                noNumberStaffNumber = null;
				defaultVoice = null;
				resetStaffVoiceNumbers(); 
				staffNumbers = new HashMap<>();
				voiceNumbers = new HashMap<>();
				openedTiesFrom = new HashMap<>();
				//currentBeams = new HashMap<>();
				//currentSlurs = new HashMap<>();
				//openedTiesFrom = new HashMap<>();
				//currentHairpin = null;
				//currentTime = new HashMap<>();
				//lastDuration = 0;
				break;
			case "attributes":
				//lastStaffType = null;
				lastStaffLines = null;
				break;
			case "staff-details":
				// used when creating the staff
				lastStaffNumber = getOptionalAttribute(attributes, "number");
				break;
			case "measure":
				currentMeasureNumber = getOptionalAttribute(attributes, "number");
				//currentMeasureTime = getCurrentTime(getDefaultStaff(), getDefaultVoice());
				measureElementsToInsert = new TreeMap<>();				
				break;
			case "direction":
				currentNote = null;
				//currentStaffPlaceHolder = new AMStaffPlaceHolder(currentScorePart.getElements().size(), currentTime);
				//currentScorePart.addElement(currentStaffPlaceHolder);
				break;
			case "note":
				resetStaffVoiceNumbers();
				//currentStaffPlaceHolder = null;	
				lastAccidental = null;
				lastNoteName = null;
				lastOctave = null;
				lastDuration = null;
				lastFigure = null;				
				dots = 0;
				context = eContexts.eNote;
				break;
			case "backup":
				resetStaffVoiceNumbers();
				context = eContexts.eBackup;
				break;
			case "forward":
				resetStaffVoiceNumbers();
				context = eContexts.eForward;
				break;
			case "grace":
				//TODO currentNote.setGraceNote(true);
				// unimplemented attributes steal-time or make-time
				//TODO String slash = getOptionalAttribute(attributes, "slash");
				//TODO currentNote.setSlashedGraceNote(slash != null && slash.equals("true"));
				break;
			case "chord":
				context = eContexts.eChord;
				break;
			case "rest":
				context = eContexts.eRest;
				measureRest = getOptionalAttribute(attributes, "measure");
				break;
				// not implemented display-step and octave
			case "unpitched":
				// unimplemented
				showUnimplemented(element);
				break;
			case "cue":
				//TODO currentNote.setCueNote(true);
				break;
				// element instrument not implemented
				// editorial layer not implemented
			case "dot":
				dots++;
				break; // unimplemented print-style and placement of dots
			case "accidental":
				/*TODO lastAccidental = new AMAccidental();
				String cautionary = getOptionalAttribute(attributes, "cautionary");
				if (cautionary != null) {
					lastAccidental.setCautionary(parseYesOrNo("cautionary", cautionary));
				}
				String editorial = getOptionalAttribute(attributes, "editorial");
				if (editorial != null) {
					lastAccidental.setEditorial(parseYesOrNo("editorial", editorial));
				}*/
				break;
			case "beam":
				/*number = getAttribute(attributes, "number");
				
				lastBeam = currentBeams.get(number);
				if (lastBeam == null) {
					lastBeam = new AMBeam(number);
					currentBeams.put(number, lastBeam);
				} 
				String repeater = getOptionalAttribute(attributes, "repeater");
				if (repeater != null) {
					lastBeam.setRepeater(parseYesOrNo("repeater", repeater));
				}
				lastBeam.addNoteOrChord(currentNote);*/
				break;
			case "tie":  // content
			case "tied": // graphical
				String tieType = getAttribute(attributes, "type");
				if (lastTieType != null && tieType.equals("start") && lastTieType.equals("stop")) {
					lastTieType = MIDDLE_TIE;  
				} else {
					lastTieType = tieType;	
				}				
				lastTieNumber = getOptionalAttribute(attributes, "number");
				//handleTie(element, attributes); // sometimes both are present
				// unimplemented time-only			
				break;
			case "slur":
				/*number = getOptionalAttribute(attributes, "number");
				if (number == null) {
					number = "Unkown";
				}
				type = getAttribute(attributes, "type");
				//System.out.println("SLur " + number + " type " + type);
				PositionAboveBelow position;
				String placement = getOptionalAttribute(attributes, "placement");
				String orientation = getOptionalAttribute(attributes, "orientation");
				if (placement == null && orientation == null) {
					position = PositionAboveBelow.UNDEFINED;
				} else if (orientation != null) {
					position = getPositionFromOrientation(orientation);
				} else {
					// placement != null
					position = getPositionFromPlacement(placement);				
				}
				
				AMSlur slur = currentSlurs.get(number);
				if (slur == null) {
					slur = new AMSlur();	
					slur.setPlacement(position);
					if (!type.equals("start")) {
						throw new ImportException("Unexpected slur type '" + type + "', expected 'start'");
					}
					currentSlurs.put(number, slur);
					currentNote.addConnector(slur);
					slur.setFrom(currentNote);
				} else {
					if (type.equals("start")) {
						throw new ImportException("Unexpected slur type '" + type + "', expected 'stop' or 'continue'");
					} else if (type.equals("stop")) {
						slur.setTo(currentNote);
						currentNote.addConnector(slur);
						currentScorePart.addConnector(slur);		
						currentSlurs.remove(number);
					} else {
						throw new ImportException("Unimplemented slur type 'continue'");
					}				
				}
				*/
				break; // unimplemented bezier...
			case "wedge":
				/*if (currentStaffPlaceHolder == null) {
					throw new ImportException("Uninitialized currentStaffPlaceHolder ");
				}
				type = getAttribute(attributes, "type");
				if (type.equals("stop")) {
					if (currentHairpin == null) {
						throw new ImportException("Missing a starting wedge");
					} else {
						currentStaffPlaceHolder.addConnector(currentHairpin);
						currentHairpin.setTo(currentStaffPlaceHolder);
						currentScorePart.addConnector(currentHairpin);
						currentHairpin = null;
					}
				} else {
					if (currentHairpin != null) {
						throw new ImportException("Other wedge was started but not finished yet");
					}
					AMHairpin hairpin;
					if (type.equals("crescendo")) {
						hairpin = new AMHairpinCrescendo();
					} else if (type.equals("diminuendo")) {
						hairpin = new AMHairpinDiminuendo();
					} else {
						throw new ImportException("Invalid wedge type: '" + type + "'");
					}
					hairpin.setFrom(currentStaffPlaceHolder);
					currentStaffPlaceHolder.addConnector(hairpin);
					currentHairpin = hairpin;
				}*/
				break;
			case "tuplet":
				showUnimplemented(element);
				break;
				//TODO ornaments, technical, articulations
			case "p":
			case "pp":
			case "ppp":
			case "f":
			case "ff":
			case "fff":
			case "mp":
			case "mf":
			case "sf":
			case "sfp":
			case "pppp":
			case "ppppp":
			case "ffff":
			case "fffff":
			case "sfpp":
			case "fp":
			case "rf":
			case "rfz":
			case "sfz":
			case "sfzz":
			case "fz":
				/*if (currentStaffPlaceHolder != null) {
					currentStaffPlaceHolder.addMarker(new AMDynamics(element, currentTime));
				} else if (currentNote != null) {
					currentNote.addMarker(new AMDynamics(element, currentTime));
				} else {
					throw new ImportException("No current staff holder or current note");
				}*/
				break;
			case "fermata":
				/*type = getOptionalAttribute(attributes, "type");
				AMFermata fermata = new AMFermata();
				fermata.setPosition(getPositionForFermata(type));
				currentNote.addMarker(fermata);*/
				break;
			case "lyric":
                String lastLyricNumberStr = getOptionalAttribute(attributes, "number");
				if (lastLyricNumber == null) {
					lastLyricNumber = -1;
				} else {
				    lastLyricNumber = Integer.parseInt(lastLyricNumberStr);
                }
				
				break;
				// unimplemented note.play
			case "cancel":
				showUnimplemented(element);
				break;
			case "key":
				break;
			case "transpose":
				context = eContexts.eTranspose;
				break;
			case "time":
				context = eContexts.eMeter;
				meterSymbol= getOptionalAttribute(attributes, "symbol");
				
				/*lastTimeSignature = new AMTimeSignature(currentScorePart.getElements().size(), currentTime);
				currentScorePart.addElement(lastTimeSignature);
				if (symbol != null) {
					lastTimeSignature.setSymbol(symbol);
				}*/
				break;
			case "clef":
				resetStaffVoiceNumbers();
				context = eContexts.eClef;
				number = getOptionalAttribute(attributes, "number");
				if (number != null) {
					lastStaffNumber = number;
				}
				/*lastClef = new AMClef(currentScorePart.getElements().size(), currentTime);
				currentScorePart.addElement(lastClef);
				if (number != null) {
					staff = getOrCreateStaff(number);
					lastClef.setStaff(staff);
				}*/
				break;
				// unimplemented: harmony, figured-bass, print, barline, grouping
			//default:
			//	Logger.getLogger(MusicXMLSAXScoreSongImporter.class.getName()).log(Level.INFO, "Unimplemented element {0}", element);
			}
		} catch (Exception e) {
			throw new ImportException(e);
		}
	}
		
	private void resetStaffVoiceNumbers() {
		// note this lastStaffNumber is different for all parts
		lastVoiceNumber = "1";
		lastStaffNumber = "1";				
	}

	private ScorePart getPart(String iD) throws ImportException {
		ScorePart part = partNumbers.get(iD);
		if (part == null) {
			throw new ImportException("Part with ID '" + iD + "' not found");
		}
		return part;
	}


	@Override
	public void handleElementContent(String currentElement, String content) throws ImportException {
		String type;
		Figures figure;
		//AMPartGroup partGroup;
		try {
			switch (currentElement) {
			// score-partwise.work
			case "work-number":
				//TODO song.getWork().setNumber(content);
				break;
			case "work-title":
				song.addTitle(content);
				break;
			case "opus":
				//TODO song.getWork().setHrefOpus(content);
				break;
			case "movement-number":
				//TODO song.getMovement().setNumber(content);
				break;
			case "movement-title":
			    song.addTitle(content);
				//song.getMovement().setTitle(content);
				break;
			case "creator":
				type = getElementContentFor(currentElement);
				song.addPerson(type, content);
				//song.getIdentification().addCreator(new AMCreator(type, content));
				break;
			case "group-name":
				//TODO partGroup = song.getLastPartGroup();
				//TODO partGroup.setName(content);
				break;
			case "part-name":
				if (currentScorePart == null) {
					throw new ImportException("No current part for giving part-name");
				}
				currentScorePart.setName(content);
				break;
			case "staff-type":
				//lastStaffType = content;
				break;
			case "staff-lines":
				lastStaffLines = Integer.parseInt(content);
				break;
			case "step":
				lastNoteName = DiatonicPitch.noteFromName(content);
				break;
			case "alter":
				lastAccidental = Accidentals.alterToAccidentals(Integer.parseInt(content));
				break;
			case "octave":
				lastOctave = Integer.parseInt(content);
				break;
			case "duration":
				// in MusicXML the divisions are set in terms of meter denominator, not as the MIDI resolution, where the 
				// resolution is set in terms of quarter. We normalize this fact with this constant that will be the ratio between
				// the meter denominator and the quarter
				if (currentDivisions == null) {
					throw new ImportException("Divisions are not set yet");
				}
				int iduration = Integer.parseInt(content);
				lastDuration = new Time(Fraction.getFraction(iduration, currentDivisions));
				break;
			case "voice":
				//lastVoice = getOrCreateLayer(content);
				lastVoiceNumber = content;
				break;
			case "type":
				figure = getFigure(content);
				lastFigure = figure; 
				break;
			case "accidental":
				lastAccidental = decodeAccidental(content);
				break;
			case "actual-notes":
				Integer tmpTupletActualNotes = Integer.parseInt(content);
				if (tupletActualNotes != null && tmpTupletActualNotes != tupletActualNotes) {
					throw new ImportException("Different actual notes for notes in tuplet: " + tmpTupletActualNotes + " vs. " + tupletActualNotes);
				}
				tupletActualNotes = tmpTupletActualNotes;
				break;
			case "normal-notes":
				Integer tmpTupletNormalNotes = Integer.parseInt(content);
				if (tupletNormalNotes != null && tmpTupletNormalNotes != tupletNormalNotes) {
					throw new ImportException("Different normal notes for notes in tuplet: " + tmpTupletNormalNotes + " vs. " + tupletNormalNotes);
				}
				tupletNormalNotes = tmpTupletNormalNotes;
				break;
			case "normal-type":
				if (tupletNormalFigureStr != null && !tupletNormalFigureStr.equals(content)) {
					throw new ImportException("Different normal figure for notes in tuplet: " + content + " vs. " + tupletNormalFigureStr);
				}
				tupletNormalFigureStr = content;
				tupletNormalFigure = getFigure(content);
				break;
			case "stem":
				//currentNote.setStemDirection(parseStemDir(content));
				break;
			case "notehead":
				//currentNote.setNoteHead(content);
				break;
			case "notehead-text":
				//currentNote.setNoteHeadText(content);
				break;
			case "staff":
				lastStaffNumber = content;				
				/*if (currentStaffPlaceHolder != null) {
					currentStaffPlaceHolder.setStaff(staff);
				} else if (currentNote != null) {
					currentNote.setStaff(getOrCreateStaff(content));
				} else {
					throw new ImportException("Expected a note or a place holder");
				}*/
				break;
			case "beam":
				/*if (content.equals("stop")) {
					currentBeams.remove(lastBeam.getNumber());
					lastBeam = null; // unimplementeed forward and backward hook and continue
				}*/
				break;
			case "syllabic":
				//lastLyrics.setSyllabic(content);
				break;
			case "text":
			    if (lastLyricNumber == null) {
			        throw new ImportException("Unexpected text without a lyrics context");
                }
                if (lastLyrics == null) {
			        lastLyrics = new HashMap<>();
                }

                lastLyrics.put(lastLyricNumber, content);
			    break;
			case "divisions": // measure.divisions
				currentDivisions = Integer.parseInt(content);
				break;
			case "fifths":
				keyFifiths = Integer.parseInt(content);
				break;
			case "diatonic":
				keyTransposeDiatonic = Integer.parseInt(content);
				break;
			case "chromatic":
				keyTransposeChromatic = Integer.parseInt(content);
				break;
			case "mode":
				keyMode = content;
				break;
			case "beats":
				meterBeats = content;
				break;
			case "beat-type":
				meterBeatType = content;
				break;
			case "sign":
				/*if (context == eContexts.eMeter) {
					meterSign = content;
				} else */
				if (context == eContexts.eClef) {
					clefSign = content;
				} else {
					throw new ImportException("Invalid context here: " + context);
				}
				break;
			case "line":
				clefLine = Integer.parseInt(content);
				break;			
			case "octave-change":
				clefOctaveChange = Integer.parseInt(content);
				break;
			}
		} catch (Exception e) {
			throw new ImportException(e);
		}
	}	

	/*private void createLayer() throws IM3Exception {
		defaultVoice = getOrCreateLayer("-1");
	}*/

	
	private ScoreLayer getOrCreateLayer(Staff staff, String number) throws IM3Exception {
		if (number == null) {
			if (defaultVoice == null) {
				defaultVoice = currentScorePart.addScoreLayer(staff);
				staff.addLayer(defaultVoice);
				voiceNumbers.put(Integer.toString(voiceNumbers.size()+1), defaultVoice);
			}
			return defaultVoice;
		} else {			
			ScoreLayer layer = voiceNumbers.get(number);
			if (layer == null) {
				layer = currentScorePart.addScoreLayer(staff);
				staff.addLayer(layer);
				voiceNumbers.put(number, layer);
			}
			if (voiceNumbers.size() == 1) {
				defaultVoice = layer;
			}
			return layer;
		}		
	}


	private Figures getFigureFromDuration(Time duration) throws ImportException {
		for (Figures fig: Figures.values()) {
			if (fig.getNotationType() == NotationType.eModern && fig.getDuration().equals(duration)) {
				return fig;
			}
		}
		throw new ImportException("Cannot find figure for duration " + duration);
	}


	@Override
	protected void handleElementClose(String closingElement) throws ImportException {
		try {
			switch (closingElement) {		
			case "direction":
				//currentStaffPlaceHolder = null;
				break;
			case "staff-details":
                getOrCreateStaff(lastStaffNumber);
				//createStaff(lastStaffNumber, lastStaffLines);
				break;
			case "forward":
				addElementToMeasure(new MusicXMLForward(lastDuration));
				break;
			case "backup":
				addElementToMeasure(new MusicXMLBackup(lastDuration));
				break;
			case "note":
				createNoteRestOrChord();
				lastLyrics = null;
				lastLyricNumber = null;
				/*if (context == eContexts.eNote || context == eContexts.eRest) {
					addToCurrentTime(lastDuration);					 
				} else if (context == eContexts.eBackup) {
					substractFromCurrentTime(lastDuration);				
				} else if (context == eContexts.eForward) {
					addToCurrentTime(lastDuration);
				} else if (context != eContexts.eChord) {
					throw new ImportException("Duration expected to be inside a note, forward or backup and is '" + (context==null?"empty":context) +"'");
				}*/				
				break;
			case "key":
				createKey();
				break;
			case "transpose":
				processTransposition();
				break;
			case "time":
				createTimeSignature();
				break;
			case "clef":
				createClef();
				break;
			case "attributes":
				/*if (lastStaff == null || staffLines != null || staffType != null) {
					createStaff();
				}			
				if (lastClef != null) {
					lastClef.setTime(getCurrentTime());
					lastStaff.addClef(lastClef);
				}
				if (lastKey != null) {
					lastKey.setTime(getCurrentTime());
					//song.addKey(currentTime, lastKey);
					defaultStaff.addKeySignature(lastKey);
				}
				
				if (lastTimeSignature != null) {
					lastTimeSignature.setTime(getCurrentTime());
					defaultStaff.addTimeSignature(lastTimeSignature);
				}
				lastStaffNumber = null;*/
				
				break;
			case "measure":
				/*8 mayo if (lastStaff == null) {
					createStaff(); // when none explicit has been created
				}*/
				
				/*for (ScoreLayer layer: layerNumbers.values()) {
					lastStaff.addLayer(layer);	
				}*/		
				Measure currentMeasure = ImportFactories.processMeasure(song, measureStartTime, currentMeasureNumber);
				processMeasureElements(currentMeasure);
				break;
			case "part":
				/*8 mayo if (lastStaff == null) {
					createStaff(); // when none explicit has been created
				}
				if (lastVoice == null) {
					createLayer();
				}
				for (ScoreLayer layer: layerNumbers.values()) {
					lastStaff.addLayer(layer);	
				}*/
				break;
			}
		} catch (Exception e) {
			throw new ImportException(e);
		}
	}



	private void processTransposition() throws IM3Exception, ImportException {
		if (keyTransposeDiatonic != null || keyTransposeChromatic != null) {
			if (keyTransposeDiatonic == null || keyTransposeChromatic == null) {
				throw new ImportException("Missing transpose>diatonic or transpose>chromatic");
			}			
			if (lastKey == null) {
				throw new ImportException("Cannot transpose without a previous key");
			}
			int intervalName = keyTransposeDiatonic;
			int semitones = keyTransposeChromatic;
			MotionDirection motion;
			if (intervalName > 0) {
				motion = MotionDirection.ASCENDING;
			} else {
				motion = MotionDirection.DESCENDING;
				semitones = -semitones;
				intervalName = -intervalName;
			}
			intervalName = intervalName + 1; // diatonic notes are specified
			Interval transpositionInterval = Intervals.getInterval(intervalName, semitones, motion);
			lastKey.setTranspositionInterval(transpositionInterval);
			
			keyTransposeDiatonic = null;
			keyTransposeChromatic = null;
		}		
	}

	private void createTimeSignature() throws ImportException, IM3Exception {
		lastTimeSignature = ImportFactories.processMeter(meterSymbol, meterBeats, meterBeatType);
		addElementToMeasure(lastTimeSignature);		
		meterBeatType = null;
		meterBeats = null;
		meterSymbol = null;
	}

	private void createClef() throws ImportException, IM3Exception {
		Clef clef = ImportFactories.createClef(NotationType.eModern, clefSign, clefLine, clefOctaveChange);
		addElementToMeasure(clef);
	}

	private ArrayList<ITimedElementInStaff> getMeasureElementsToInsert() {
		StaffAndVoice sv = new StaffAndVoice(lastStaffNumber, lastVoiceNumber);
		ArrayList<ITimedElementInStaff> v = measureElementsToInsert.get(sv);
		if (v == null) {
			v = new ArrayList<>();
			sv.setTime(measureStartTime);
			measureElementsToInsert.put(sv, v);
		} 
		return v;
	}
	
	private void addElementToMeasure(ITimedElementInStaff element) throws ImportException, IM3Exception {
		getMeasureElementsToInsert().add(element);		
	}

	
	private void createNoteRestOrChord() throws ImportException, IM3Exception {
		ArrayList<ITimedElementInStaff> lastContainer;
		if (tupletActualNotes != null) {			
			if (tupletAtoms == null) {
				if (context != eContexts.eChord) {
					tupletAtoms = new ArrayList<>();
					tupletAccumulatedTime = Time.TIME_ZERO;
					lastContainer = tupletAtoms;
				} else {
					lastContainer = getMeasureElementsToInsert();
                }
			} else {
				lastContainer = tupletAtoms;
			}
		} else {
			lastContainer = getMeasureElementsToInsert();
		} 
		
		if (lastFigure == null) {
			if (lastDuration != null) {
				if (tupletActualNotes != null) {
					// duration is the actual time, not the figure time
					lastDuration = lastDuration.multiply(tupletActualNotes).divide(tupletNormalNotes);
				} else {
					try {
						lastFigure = getFigureFromDuration(lastDuration);
					} catch (ImportException e) {
						Logger.getLogger(MusicXMLSAXScoreSongImporter.class.getName()).log(Level.INFO, "Cannot get duration of duration {0}, is it a mRest?", lastDuration);
						lastFigure = null;
					}
				}
			} else {
				throw new ImportException("Missing duration and type");
			}
		}
		
		if (context == eContexts.eRest) {
			if (measureRest != null && measureRest.equals("yes")) {
				if (lastTimeSignature == null) {
					throw new IM3Exception("There is not a lastTimeSignature");
				}
				SimpleMeasureRest mrest;
				if (lastFigure != null) {
					mrest = new SimpleMeasureRest(lastFigure, lastDuration);
				} else {
					mrest = new SimpleMeasureRest(Figures.WHOLE, lastDuration);
				}				
				lastContainer.add(mrest);								
			} else {
				SimpleRest rest;
				if (lastFigure != null) {
					rest = new SimpleRest(lastFigure, dots);
				} else {
					rest = new SimpleRest(Figures.NO_DURATION, 0);
				} 
				lastContainer.add(rest);				
			}
		} else if (context == eContexts.eNote) {
			ScientificPitch sp = new ScientificPitch(lastNoteName, lastAccidental, lastOctave);
			SimpleNote simpleNote = new SimpleNote(lastFigure, dots, sp);

			lastAtom = simpleNote;
			lastAtomPitch = simpleNote.getAtomPitch();
			lastContainer.add(lastAtom);

            if (lastLyrics != null) {
                for (Entry<Integer, String> lyric: lastLyrics.entrySet()) {
                    lastAtomPitch.addLyric(lyric.getKey(), lyric.getValue());
                }
            }

        } else if (context == eContexts.eChord) {
			SimpleChord chord;

			if (lastAtom instanceof SimpleNote) {
				SimpleTuplet lastTuplet = null;				
				if (!lastContainer.isEmpty() && lastContainer.get(lastContainer.size()-1) instanceof SimpleTuplet) {
					lastTuplet = (SimpleTuplet) lastContainer.get(lastContainer.size()-1);
					lastTuplet.removeSubatom(lastAtom);
				} else {
					lastContainer.remove(lastAtom);
				}
				
				SimpleNote oldNote = ((SimpleNote)lastAtom);
				chord = new SimpleChord(lastAtom.getAtomFigure().getFigure(), lastAtom.getAtomFigure().getDots(), 
						oldNote.getAtomPitch().getScientificPitch());
				lastAtomPitch = chord.getAtomPitches().get(0);
				
				// if tied, it is in openedTiesFrom
				for (Entry<String, AtomPitch> entry: openedTiesFrom.entrySet()) {
					if (entry.getValue() == oldNote.getAtomPitch()) {
						entry.setValue(lastAtomPitch); // update it to the new pitch
						break;
					}
				}
				
				lastAtomPitch.setTiedFromPrevious(oldNote.getAtomPitch().getTiedFromPrevious());
				lastAtomPitch.setTiedToNext(oldNote.getAtomPitch().getTiedToNext());
				if (lastTuplet != null) {
					chord.setDuration(oldNote.getDuration());
					lastTuplet.addSubatom(chord);
				} else {
					lastContainer.add(chord);
				}
				
				ScientificPitch sp = new ScientificPitch(lastNoteName, lastAccidental, lastOctave);
				lastAtomPitch = chord.addPitch(sp);
				lastAtom = chord; 
			} else if (lastAtom instanceof SimpleChord) {
				chord = (SimpleChord) lastAtom;
				AtomFigure af = lastAtom.getAtomFigure();
				if (!af.getFigure().equals(lastFigure)) {
					throw new ImportException("The previous figure of the chord (" + af.getFigure() 
							+ ") is not the sane as the chord one (" + lastFigure + ")");
				}
				if (af.getDots() != dots) {
					throw new ImportException("The previous dots of the chord (" + af.getDots() 
							+ ") is not the sane as the chord one (" + dots + ")");
				}
				ScientificPitch sp = new ScientificPitch(lastNoteName, lastAccidental, lastOctave);
				if (chord.containsPitch(sp)) {
					throw new ImportException("The chord (" + lastAtom 
							+ ") already contains the pitch (" + sp + ")");				
				}
				lastAtomPitch = chord.addPitch(sp);
			} else {
				throw new ImportException("Invalid type: " + lastAtom.getClass());
			}
		} else {
			throw new IM3RuntimeException("Should not enter here");
		}
		
		// handle tuplet
		if (tupletActualNotes != null) { 
			if (context != eContexts.eChord) { // the first note has done this job
				tupletAccumulatedTime = tupletAccumulatedTime.add(lastFigure.getDuration());
				// could be optimized, but I think it is not necessary
				Figures tupletFigure;
				if (tupletNormalFigure == null) {
					tupletFigure = lastFigure;
				} else {
					tupletFigure = tupletNormalFigure;
				}
				
				if (tupletExpectedDuration == null) { // first time
					tupletExpectedDuration = tupletFigure.getDuration().multiply(tupletActualNotes);
				}
				
				int diff = tupletExpectedDuration.compareTo(tupletAccumulatedTime);
				if (diff < 0) {
					throw new ImportException("The sum of notes in the tuplet '" + tupletAccumulatedTime + "' is greater than the expected duration '" + tupletExpectedDuration + "'");
				} else if (diff == 0) {
					// it is full
					ArrayList<Atom> atomsForTuplet = new ArrayList<>();
					for (ITimedElementInStaff ta: tupletAtoms) {
						if (!(ta instanceof Atom)) {
							throw new ImportException("Expected an atom and found a " + ta.getClass());
						}
						atomsForTuplet.add((Atom) ta);
					}
					SimpleTuplet tuplet = new SimpleTuplet(tupletActualNotes, tupletNormalNotes, tupletFigure, atomsForTuplet);					
					addElementToMeasure(tuplet);					
					tupletNormalFigure = null;
					tupletActualNotes = null;
					tupletNormalNotes = null;
					tupletNormalFigureStr = null;
					tupletAtoms = null;
					tupletExpectedDuration = null;
				}
			}
		}
		
		// handle ties
		if (lastTieType != null) {
			if (lastAtomPitch == null) {
				throw new IM3RuntimeException("lastAtomPitch cannot be null");
			}
			
			if (lastTieNumber == null) {
				lastTieNumber = lastAtomPitch.getScientificPitch().toString();
			} else {
				lastTieNumber = lastTieNumber + "_" +lastAtomPitch.getScientificPitch().toString();
			}
			AtomPitch tieFrom = openedTiesFrom.get(lastTieNumber);
			//System.out.println("> "+ element + " " + type + " " + number + " " + currentNote);
			if (lastTieType.equals("start")) {
				openedTiesFrom.put(lastTieNumber, lastAtomPitch);
			} else if (lastTieType.equals("stop")) {
				if (tieFrom != null) {
					//System.out.println("\tUsing tie " + tie.hashCode());
					tieFrom.setTiedToNext(lastAtomPitch); 
					openedTiesFrom.remove(lastTieNumber);
					//System.out.println("\tSTOP: " + openedTiesFrom);
				} else {
					//System.out.println("\ttie = null, stop ommited-");
				}
			} // if not equals skip
			else if (lastTieType.equals(MIDDLE_TIE)) {
				if (tieFrom != null) {
					//System.out.println("\tUsing tie " + tie.hashCode());
					tieFrom.setTiedToNext(lastAtomPitch); 
					openedTiesFrom.remove(lastTieNumber);
					//System.out.println("\tSTOP: " + openedTiesFrom);
				} 
				openedTiesFrom.put(lastTieNumber, lastAtomPitch);
			}
			lastTieNumber = null;
			lastTieType = null;
		}
		
		tupletActualNotes = null;
	}


	/*private Time getMeasureDuration() throws ImportException {
		Time duration;
		if (lastTimeSignature instanceof ITimeSignatureWithDuration) {
			duration = ((ITimeSignatureWithDuration)lastTimeSignature).getMeasureDuration();
		} else {
			throw new ImportException("Cannot get the duration of a time signature without duration " + lastTimeSignature + " to create a measure rest");
		}
		return duration;
	}*/

	private Staff getOrCreateStaff(String number) throws ImportException, IM3Exception {
	    if (noNumberStaffNumber != null) {
	        number = noNumberStaffNumber;
        }
		if (number == null) {
			if (defaultStaff == null) {
				defaultStaff = createStaff(null, lastStaffLines);				
			} 
			return defaultStaff;
		} else {
			Staff staff = staffNumbers.get(number);
			if (staff == null) {
				staff = createStaff(number, 5);
			}
			if (staffNumbers.size() == 1) {
				defaultStaff = staff;
			}
			return staff;
		}
	}

	private Staff createStaff(String number, Integer staffLines) throws ImportException, IM3Exception {
		Staff staff;
        if (staffLines == null || staffLines == 5) {
				staff = new Pentagram(song, hierarchicalIdGenerator.nextStaffHierarchicalOrder(null),
						hierarchicalIdGenerator.getNextVerticalDivisionIdentifier());
		} else if (staffLines.equals("1")) {
				staff = new PercussionStaff(song, hierarchicalIdGenerator.nextStaffHierarchicalOrder(null),
						hierarchicalIdGenerator.getNextVerticalDivisionIdentifier());
		} else {
				throw new ImportException("Unimplemented staves with " + staffLines + " lines");
		}

		//staffNumbers.put(number, staff);
		//staff.setName(label);
		//staff.setOssia(inOssia);
		
		if (number == null) {
			number = Integer.toString(staffNumbers.size() + 1).toString(); // by default, count of staves
            noNumberStaffNumber = number;
		}
		staffNumbers.put(number, staff);		
		song.addStaff(staff);
		staff.setNotationType(NotationType.eModern);
		return staff;
	}


	private void createKey() throws ImportException, IM3Exception {
		if (keyFifiths == null) {
			throw new ImportException("Missing key fifths");
		}
		Mode mode;
		if (keyMode == null) {
			mode = Mode.MAJOR;
		} else {
			mode = Mode.stringToMode(keyMode);
		}
		lastKey = new KeySignature(null, new Key(keyFifiths, mode));
		addElementToMeasure(lastKey);
		//System.out.println(lastKey.getInstrumentKey() + " -> " + lastKey.getConcertPitchKey());
		keyMode = null;
		keyFifiths = null;
	}


	private PositionAboveBelow getPositionFromPlacement(String placement) {
		PositionAboveBelow position;
		if (placement == null) {
			position = PositionAboveBelow.UNDEFINED;
		} else if (placement.equals("below")) {
			position = PositionAboveBelow.BELOW;
		} else {
			position = PositionAboveBelow.ABOVE; 
		}
		return position;
	}

	
	private PositionAboveBelow getPositionFromOrientation(String orientation) throws ImportException {
		PositionAboveBelow position;
		if (orientation == null) {
			position = PositionAboveBelow.UNDEFINED;
		} else if (orientation.equals("under")) {
			position = PositionAboveBelow.BELOW;
		} else if (orientation.equals("over")) {
			position = PositionAboveBelow.ABOVE; 
		} else {
			throw new ImportException("Undefined orientation type: '" + orientation + "'");
		}
		return position;
	}
	
	private PositionAboveBelow getPositionForFermata(String type) {
		PositionAboveBelow position;
		if (type == null) {
			position = PositionAboveBelow.UNDEFINED;
		} else if (type.equals("upright")) {
			position = PositionAboveBelow.ABOVE;
		} else {
			position = PositionAboveBelow.BELOW; 
		}
		return position;
	}

	Figures getFigure(String content) throws ImportException {
		Figures figure = FIGURES.get(content);
		if (figure == null) {
			throw new ImportException("Unknown figure type: " + content);
		}
		return figure;
	}


	private StemDirection parseStemDir(String stemDir) throws ImportException {
		if (stemDir == null) {
			return StemDirection.computed;
		} else if (stemDir.equals("up")) {
			return StemDirection.up;
		} else if (stemDir.equals("down")) {
			return StemDirection.down;
		} else {
			throw new ImportException("Invalid stem direction: " + stemDir);
		}
	}
	private Accidentals decodeAccidental(String content) throws ImportException {
		switch (content) {
		case "sharp":
			return Accidentals.SHARP;
		case "flat":
			return Accidentals.FLAT;
		case "natural":
			return Accidentals.NATURAL;
		case "sharp-sharp":
		case "double-sharp":
			return Accidentals.DOUBLE_SHARP;
		case "flat-flat":
		case "double-flat":
			return Accidentals.DOUBLE_FLAT;
		case "triple-flat":
			return Accidentals.TRIPLE_FLAT;
		default:
			throw new ImportException("Invalid accidental: " + content);
		}
	}

	private void processMeasureElements(Measure currentMeasure) throws IM3Exception, ImportException {
		/*Histogram<StaffAndVoice> atomsInVoice = new Histogram<>();
		for (Entry<StaffAndVoice, ArrayList<ITimedElementInStaff>> entry: measureElementsToInsert.entrySet()) {
			for (ITimedElementInStaff element: entry.getValue()) {
				if (element instanceof Atom) {
					atomsInVoice.addElement(entry.getKey());
					System.out.println("Atom to insert in staff layer " + entry.getKey() + " " + element);
				}
			}			
		}*/
		
		ArrayList<StaffAndVoice> resetTimes = new ArrayList<>(); 
		ArrayList<SimpleMeasureRest> pendingMRestsToSetDuration = new ArrayList<>(); // their duration is not known until all measure is complete
		Time maxMeasureTime = Time.TIME_ZERO;
		
		for (Entry<StaffAndVoice, ArrayList<ITimedElementInStaff>> entry: measureElementsToInsert.entrySet()) {
			StaffAndVoice sv = entry.getKey();
			Staff staff = getOrCreateStaff(sv.getStaffNumber());
			ScoreLayer layer = getOrCreateLayer(staff, sv.getVoiceNumber());
			
			int countAtoms = 0;
			for (ITimedElementInStaff element: entry.getValue()) {
				if (element instanceof Atom) {
					countAtoms++;
				}
			}
			
			for (ITimedElementInStaff element: entry.getValue()) {
				if (element instanceof Atom) {
					Atom atom = (Atom) element;
					if (atom.getDuration().equals(Figures.NO_DURATION.getDuration())) {
						if (countAtoms == 1) {
							if (atom instanceof SimpleRest) {
								SimpleRest rest = (SimpleRest) atom;
								SimpleMeasureRest measureRest = new SimpleMeasureRest(rest.getAtomFigure().getFigure(), Figures.NO_DURATION.getDuration());
								atom = measureRest;
								measureRest.setStaff(staff);
								pendingMRestsToSetDuration.add(measureRest);
								Logger.getLogger(MusicXMLSAXScoreSongImporter.class.getName()).log(Level.INFO, "Replacing rest for mRest");
								resetTimes.add(sv);
							} else if (atom instanceof SimpleMeasureRest) {
								pendingMRestsToSetDuration.add((SimpleMeasureRest) atom);
								resetTimes.add(sv);
							}
							else {
								throw new ImportException("Whole measure figures are only implemented for measure rests, and this is a " + atom.getClass() + ", with expected duration");
							}
						} else {
							throw new ImportException(atom.getClass() + " in part " + currentScorePart.getNumber() + " in staff " + sv.getStaffNumber() + " and voice " + sv.getVoiceNumber() + " using just a non exact duration in a measure (" + currentMeasure + ") with " + countAtoms  + " atoms"); //TODO mejorar error	
						}
					} 
					
					layer.add(sv.getTime(), atom);
					sv.addTime(atom.getDuration());
					staff.addCoreSymbol(atom);
				} else if (element instanceof MusicXMLForward) {
					sv.addTime(element.getTime()); // actually, time is the duration
				} else if (element instanceof MusicXMLBackup) {
					sv.substractTime(element.getTime()); // actually, time is the duration
				} else {
					//TODO Ver si tenemos que insertar la Key y la TimeSignature en todos los pentagramas
					if (element instanceof KeySignature) {
						KeySignature ks = (KeySignature) element;
						ks.setTime(sv.getTime());
						staff.addKeySignature(ks);
					} else if (element instanceof TimeSignature) {
						TimeSignature ks = (TimeSignature) element;
						ks.setTime(sv.getTime());
						staff.addTimeSignature(ks);
					} else if (element instanceof Clef) {
						Clef ks = (Clef) element;
						ks.setTime(sv.getTime());
						staff.addClef(ks);
					}
				}
				//measureStartTime = sv.getTime();
				maxMeasureTime = Time.max(maxMeasureTime, sv.getTime());
			}
		}
		
		if (measureStartTime.compareTo(maxMeasureTime) < 0) {
			currentMeasure.setEndTime(maxMeasureTime);
		}
		
		Time measureDuration = null;
		for (SimpleMeasureRest measureRest: pendingMRestsToSetDuration) {
            if (!currentMeasure.isEndTimeSet()) {
				if (!currentMeasure.getTime().isZero()) { // avoid first bar for anacrusis
					TimeSignature ts = measureRest.getStaff().getRunningTimeSignatureAt(measureRest);
					if (ts == null) {
						throw new ImportException("Cannot infer the measure duration without time signatures at element " + measureRest);
					}
					measureDuration =  ts.getDuration();
					maxMeasureTime = currentMeasure.getTime().add(measureDuration);
				}
				currentMeasure.setEndTime(maxMeasureTime);
				if (currentMeasure.getDuration().getComputedTime() <= 0.0) {
					throw new ImportException("Cannot set a non positive (" + currentMeasure.getDuration() + " duration, in measure " 
							+ currentMeasure + "  this may be due to anacrusis measures without any duration specified");
				}
			} else {
				measureDuration = currentMeasure.getDuration();
			}
		
			if (measureDuration == null) {
				throw new ImportException("Measure duration is not computed for measure rest " + measureRest);
			}
			if (measureRest.getAtomFigure().getFigure().equals(Figures.NO_DURATION)) {
				measureRest.setFigure(Figures.WHOLE);
			}
			measureRest.setDuration(measureDuration);
		}
		measureStartTime = currentMeasure.getEndTime();
		for (StaffAndVoice rt: resetTimes) {
			rt.setTime(measureStartTime);
		}
	}
	
	//TODO Ver cambios de tonalidad o compás por enmedio ...
	@Override
	protected void postProcess() throws ImportException, IM3Exception {
		// check all staves have a time signature and key signature - they may have been inserted just
		// in the first staff
		if (!song.getStaves().isEmpty()) {
			Staff firstStaff = song.getStaves().get(0);
			KeySignature ks = firstStaff.getKeySignatureWithOnset(Time.TIME_ZERO);
			TimeSignature ts = firstStaff.getTimeSignatureWithOnset(Time.TIME_ZERO);
			if (ts == null) {
				Logger.getLogger(MusicXMLSAXScoreSongImporter.class.getName()).log(Level.WARNING, "No meter found, inserting 4/4");
				ts = new FractionalTimeSignature(4, 4);
				firstStaff.addTimeSignature(ts);
			}
			if (ks == null) {
				Logger.getLogger(MusicXMLSAXScoreSongImporter.class.getName()).log(Level.WARNING, "No key signature found, inserting C Major");
				ks = new KeySignature(NotationType.eModern, new Key(0, Mode.MAJOR));
			}
			ArrayList<Measure> measures = null;
			if (song.hasMeasures()) {
				measures = song.getMeasuresSortedAsArray();
			}
			
			for (int i=1; i<song.getStaves().size(); i++) {
				Staff staff = song.getStaves().get(i);
				if (staff.getKeySignatureWithOnset(Time.TIME_ZERO) == null) {
					staff.addKeySignature(ks);
				}
				if (staff.getTimeSignatureWithOnset(Time.TIME_ZERO) == null) {
					staff.addTimeSignature(ts);
				}
			}				
			// check anacrusis
			if (measures != null && !measures.isEmpty()) {
				Time maxEndTime = Time.TIME_ZERO;
				for (Atom atom : firstStaff.getAtomsWithOnsetWithin(measures.get(0))) {
					maxEndTime = Time.max(maxEndTime, atom.getOffset());
				}
				Time measureDuration = ts.getDuration();
				int diff = maxEndTime.compareTo(measureDuration);
				if (diff < 0) {
					song.setAnacrusisOffset(measureDuration.substract(maxEndTime));
				} else if (diff > 0) {
					throw new ImportException("Fist measure duration based on atom is " + maxEndTime + " and expected first measure duration based on time signature is " + measureDuration);
				} // else normal measure
			}

		}
	}
	
}
