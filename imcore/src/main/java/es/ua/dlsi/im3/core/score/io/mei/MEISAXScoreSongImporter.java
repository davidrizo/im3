/*
 * Copyright (C) 2017 David Rizo Valero
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
package es.ua.dlsi.im3.core.score.io.mei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.io.XMLSAXScoreSongImporter;
import es.ua.dlsi.im3.core.score.io.kern.KernImporter;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.mensural.meters.*;
import org.apache.commons.lang3.math.Fraction;
import org.xml.sax.SAXException;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.dynamics.DynamicMarkForte;
import es.ua.dlsi.im3.core.score.dynamics.DynamicMarkFortePossible;
import es.ua.dlsi.im3.core.score.dynamics.DynamicMarkFortissimo;
import es.ua.dlsi.im3.core.score.dynamics.DynamicMarkMezzoForte;
import es.ua.dlsi.im3.core.score.dynamics.DynamicMarkMezzoPiano;
import es.ua.dlsi.im3.core.score.dynamics.DynamicMarkPianissimo;
import es.ua.dlsi.im3.core.score.dynamics.DynamicMarkPiano;
import es.ua.dlsi.im3.core.score.dynamics.DynamicMarkPianoPossible;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.core.score.staves.PercussionStaff;
import es.ua.dlsi.im3.core.io.ImportException;

//TODO fillMissingElements
//TODO Contexts - no so hard-coded!!
/**
 * MusicXML implemented with SAX to improve performance over JAXB.
 * 
 * @author drizo
 */
public class MEISAXScoreSongImporter extends XMLSAXScoreSongImporter {
    DurationEvaluator durationEvaluator;

    public MEISAXScoreSongImporter(DurationEvaluator durationEvaluator) {
        this.durationEvaluator = durationEvaluator;
    }

    class PendingConnectorOrMark {
		Measure measure;
		String tstamp;
		String tstamp2;
		String startid;
		String endid;
		String tag;
		String content;
		Staff staff;
		public ScoreLayer layer;

        @Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((content == null) ? 0 : content.hashCode());
			result = prime * result + ((endid == null) ? 0 : endid.hashCode());
			result = prime * result + ((measure == null) ? 0 : measure.hashCode());
			result = prime * result + ((staff == null) ? 0 : staff.hashCode());
			result = prime * result + ((startid == null) ? 0 : startid.hashCode());
			result = prime * result + ((tag == null) ? 0 : tag.hashCode());
			result = prime * result + ((tstamp == null) ? 0 : tstamp.hashCode());
			result = prime * result + ((tstamp2 == null) ? 0 : tstamp2.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PendingConnectorOrMark other = (PendingConnectorOrMark) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (content == null) {
				if (other.content != null)
					return false;
			} else if (!content.equals(other.content))
				return false;
			if (endid == null) {
				if (other.endid != null)
					return false;
			} else if (!endid.equals(other.endid))
				return false;
			if (measure == null) {
				if (other.measure != null)
					return false;
			} else if (!measure.equals(other.measure))
				return false;
			if (staff == null) {
				if (other.staff != null)
					return false;
			} else if (!staff.equals(other.staff))
				return false;
			if (startid == null) {
				if (other.startid != null)
					return false;
			} else if (!startid.equals(other.startid))
				return false;
			if (tag == null) {
				if (other.tag != null)
					return false;
			} else if (!tag.equals(other.tag))
				return false;
			if (tstamp == null) {
				if (other.tstamp != null)
					return false;
			} else if (!tstamp.equals(other.tstamp))
				return false;
			if (tstamp2 == null) {
				if (other.tstamp2 != null)
					return false;
			} else if (!tstamp2.equals(other.tstamp2))
				return false;
			return true;
		}
		private MEISAXScoreSongImporter getOuterType() {
			return MEISAXScoreSongImporter.this;
		}
		@Override
		public String toString() {
			return "PendingConnectorOrMark [measure=" + measure.getNumber() +
					", tstamp=" + tstamp + ", tstamp2=" + tstamp2 + ", startid="
					+ startid + ", endid=" + endid + ", tag=" + tag + ", content=" + content + ", staff=" 
					+ (staff!=null?staff.getNumberIdentifier():"null") +
					"]";
		}
	}
	
	static final HashMap<String, Figures> FIGURES = new HashMap<>();
	static {
		FIGURES.put("maxima", Figures.MAXIMA);
		FIGURES.put("longa", Figures.LONGA);
		FIGURES.put("brevis", Figures.BREVE);
		FIGURES.put("semibrevis", Figures.SEMIBREVE);
		FIGURES.put("minima", Figures.MINIM);
		FIGURES.put("semiminima", Figures.SEMIMINIM);
		FIGURES.put("fusa", Figures.FUSA);
        FIGURES.put("semifusa", Figures.SEMIFUSA);
		FIGURES.put("long", Figures.QUADRUPLE_WHOLE);
		FIGURES.put("breve", Figures.DOUBLE_WHOLE);
		FIGURES.put("1", Figures.WHOLE);
		FIGURES.put("2", Figures.HALF);
		FIGURES.put("4", Figures.QUARTER);
		FIGURES.put("8", Figures.EIGHTH);
		FIGURES.put("16", Figures.SIXTEENTH);
		FIGURES.put("32", Figures.THIRTY_SECOND);
		FIGURES.put("64", Figures.SIXTY_FOURTH);
		FIGURES.put("128", Figures.HUNDRED_TWENTY_EIGHTH);
		FIGURES.put("256", Figures.TWO_HUNDRED_FIFTY_SIX);
	}
	
	protected Figures getFigure(String content, HashMap<String, String> elementAttributes) throws ImportException {
		Figures figure = FIGURES.get(content);
		if (figure == null) {
			throw new ImportException("Unknown figure type: " + content);
		}
		return figure;
	}
    private int horizontalOrderInStaff;
    HashMap<String, Object> xmlIDs;
	HashMap<Measure, Integer> measurePositions;
	HashSet<PendingConnectorOrMark> pendingConnectorOrMarks;
	String titleType;
	// they may be used in handleElementContent, not always initialized, only for elements that we know will use them
	//protected HashMap<String, String> attributesMap;
	HashMap<String, Time> currentTime;
	Time lastMeasureEndTime; 
	Time maximumVoicesTime;
	
	ScorePart currentScorePart;
	Clef lastClef;
	Staff lastStaff;
	ScoreLayer lastVoice;
	SimpleNote currentNote;
    private ScoreLyric lastVerse;
    private HashMap<String, AtomPitch> currentTies; // map code (stack + layer + note) - AtomPitch
	private String personRole;
	int staffCount=0;
	int layerCount=0;
	
	//Time lastTime; //TODO Quitar como campo
	StaffTimedPlaceHolder currentStaffTimedPlaceHolder;
	HashMap<Time, StaffTimedPlaceHolder> placeHolders;
	protected boolean importingMusic = false;
	private SimpleChord lastChord;
	private Time dynamTime;
    private Time harmTime;
	private String harmType; // use with care, not approved yet by MEI
    private Key pendingHarmKey;
    private TonalFunction pendingHarmTonalFunction;
    private Time pendingHarmKeyTime;
    private Time pendingHarmTonalFunctionTime;
    /**
     * Accidentals in a previous note in the measure (key = diatonic pitch+ octave*7)
     */
	private HashMap<Integer, Accidentals> previousAccidentals;

    private Custos lastCustosWithoutPitch;

    private boolean inOssia = false;
	private HashMap<String, Staff> staffNumbers;
	HierarchicalIDGenerator hierarchicalIdGenerator;
	private AtomPitch lastAtomPitch;
	private HashMap<String, ScoreLayer> layers;
	private Time figureDuration;
	private String lastMeterCount;
	private String lastMeterUnit;
	private String lastMeterSym;
	private String lastKeySig;
	private String lastKeyMode;
	private String lastTransDiat;
	private String lastTransSemi;
	
	private String lastModusmaiorStr;
	private String lastModusminorStr;
	private String lastTempusStr;
	private String lastProlatioStr;
	//private boolean updateMeasure;
	//private String lastMeasureXMLID;
	//private String lastMeasureNumber;
	private Integer tupletNum;
	private Integer tupletNumBase;
	private ArrayList<Atom> tupletElements;
	private String tupletXMLID;
	private ArrayList<SimpleMeasureRest> pendingMeasureRestsToSetDuration;
    private ArrayList<SimpleMultiMeasureRest> pendingMultiMeasureRestsToSetDuration;
	private Measure currentMeasure;
    private KernImporter kernImporter; // used for importing <harm>
    private String beamedGroupXMLID;
    private ArrayList<SingleFigureAtom> beamedGroupElements;



    @Override
	protected void init() throws ParserConfigurationException, SAXException, IM3Exception {
		song = new ScoreSong(); //TODO ¿Y si es una colección?
		staffNumbers = new HashMap<>();
		currentTies = new HashMap<>();
		currentTime = new HashMap<>();
        previousAccidentals = new HashMap<>();
		xmlIDs = new HashMap<>();
		pendingConnectorOrMarks = new HashSet<>();
		placeHolders = new HashMap<>();
		hierarchicalIdGenerator = new HierarchicalIDGenerator();
		layers = new HashMap<>();
		maximumVoicesTime = Time.TIME_ZERO;
		lastMeasureEndTime = Time.TIME_ZERO;
        beamedGroupElements = null;
	}
	
	/*private String getLayerCode() throws ImportException {
		if (lastStaff == null) {
			throw new ImportException("lastStaff=null");
		}
		if (lastVoice == null) {
			throw new ImportException("lastVoice=null");
		}
		return getLayerCode(lastStaff, lastVoice);
	}*/

    private String getLayerCode(Staff staff, ScoreLayer voice) throws ImportException {
        //String code = lastStaff==null?"_":lastStaff.hashCode() + "_" + lastVoice.hashCode();
        String code = staff.hashCode() + "_" + voice.hashCode();
        return code;
    }

	private Time getCurrentTime() throws ImportException {
		if (lastVoice == null) { // e.g. staffDef where no time is defined yet
			return Time.TIME_ZERO; 
		}
		String code = getLayerCode(lastStaff, lastVoice);
		Time time = currentTime.get(code);
		if (time == null) {
			currentTime.put(code, Time.TIME_ZERO);
			return Time.TIME_ZERO;
		} else {
			return time;
		}
	}
	
	private void setCurrentTime(Time time) throws ImportException {
        setCurrentTime(lastStaff, lastVoice, time);
		//String code = getLayerCode();
		////currentTime.put(code, time);
		maximumVoicesTime = Time.max(time, maximumVoicesTime);
    }

    private void setCurrentTime(Staff staff, ScoreLayer voice, Time time) throws ImportException {
        String code = getLayerCode(staff, voice);
        currentTime.put(code, time);
        maximumVoicesTime = Time.max(time, maximumVoicesTime);
    }

	private void updateCurrentTime() throws ImportException, IM3Exception {
        setCurrentTime(lastVoice.getDuration());
	}
	
	private void setXMLID(String xmlid, IUniqueIDObject object) throws IM3Exception {
        if (xmlid != null) {
            song.getIdManager().assignID(xmlid, object);
        } else {
            song.getIdManager().assignNextID(object);
            xmlid = object.__getID();
        }
		xmlIDs.put(xmlid, object);
	}

	private void addElementToVoiceStaffOrTuplet(Atom atom, String xmlid, HashMap<String, String> attributesMap, Staff elementStaff) throws ImportException, IM3Exception {
		setXMLID(xmlid, atom);

		if (beamedGroupElements != null && atom instanceof SingleFigureAtom) {
			beamedGroupElements.add((SingleFigureAtom) atom);
		}

		if (tupletElements != null) {
            tupletElements.add(atom);
        } else {
            lastVoice.add(atom); // sets the time
            //lastChord.setTime(getCurrentTime());
            elementStaff.addCoreSymbol(atom); // if note it will be inserted as staff change in other place
            //lastChord.setStaff(lastStaff); in addCoreSymbol
            if (atom instanceof SingleFigureAtom && !(atom instanceof SimpleMultiMeasureRest)) {
                processPossibleMensuralImperfection(attributesMap, ((SingleFigureAtom)atom).getAtomFigure());
            }
            updateCurrentTime();
        }
	}
	
	@Override
	public void doHandleOpenElement(String element, HashMap<String, String> attributesMap) throws ImportException {
		String number;
		String label;
		String dotsStr;
		int dots;
		String dur;
		Figures figure;
		String xmlid;
		String clefLine;
		String clefShape;
		String tstamp;
		//String tstamp2;
		String staffNumber;
		String layerNumber;
		//Time time;
		Staff elementStaff;
		PendingConnectorOrMark pendingConnectorOrMark;
		
		//attributesMap = getAttributes(element, saxAttributes);
		try {
			if (element.equals("music")) {
				importingMusic = true; //TODO gestionar esto de otra forma - ¿estados? - ¿consume() como en eventos JavaFX?
			} else if (importingMusic) { // avoid parse other MEI extensions such as the hierarchical analysis here
				String accid;
				switch (element) {
				case "work":
					//TODO song.setWo 
					break;
				case "title":
					titleType = getOptionalAttribute(attributesMap, "type");
					break;
				case "persName":
					personRole = getOptionalAttribute(attributesMap, "role");
					break;
				case "ossia":
					inOssia = true;
					break;
				case "scoreDef":
					lastKeySig = getOptionalAttribute(attributesMap, "key.sig");
					lastKeyMode = getOptionalAttribute(attributesMap, "key.mode");
					lastTransDiat = getOptionalAttribute(attributesMap, "trans.diat");
					lastTransSemi = getOptionalAttribute(attributesMap, "trans.semi");					
					lastMeterCount = getOptionalAttribute(attributesMap, "meter.count");
					lastMeterUnit = getOptionalAttribute(attributesMap, "meter.unit");
					lastMeterSym = getOptionalAttribute(attributesMap, "meter.sym");
					lastModusmaiorStr = getOptionalAttribute(attributesMap, "modusmaior");
					lastModusminorStr = getOptionalAttribute(attributesMap, "modusminor");
					lastTempusStr = getOptionalAttribute(attributesMap, "tempus");
					lastProlatioStr = getOptionalAttribute(attributesMap, "prolatio");
					
					break;
					//TODO staff groups (ej. garison.mei)
				/*case "staffGrp":
					label = getOptionalAttribute(attributesMap, "label");
					//TODO
					currentScorePart = new ScorePart(song, song.getParts().size()+1); //TODO
					currentScorePart.setName(label);
					song.addPart(currentScorePart);					
					break;*/
				case "instrDef":
                    label = getOptionalAttribute(attributesMap, "label");
                    String instrNumber = getOptionalAttribute(attributesMap, "n");

                    ScorePart p = null;
                    if (instrNumber != null) {
                        int nn = Integer.parseInt(instrNumber);
                        p = song.getPartWithNumber(nn);
                    }

                    if (p != null) {
                        currentScorePart = p;
                    } else {
                        currentScorePart = new ScorePart(song, song.getParts().size() + 1); //TODO
                        currentScorePart.setName(label);
                        song.addPart(currentScorePart);
                    }
                    break;
				case "staffDef": 
					lastStaff = processStaff(attributesMap);
					label = getOptionalAttribute(attributesMap, "label");
					lastStaff.setName(label);
					
					clefLine = getOptionalAttribute(attributesMap, "clef.line");
					clefShape = getOptionalAttribute(attributesMap, "clef.shape");
					if (clefLine != null || clefShape != null) {
						processClef(clefLine, clefShape, getCurrentTime(), 
								getOptionalAttribute(attributesMap, "clef.dis"),
								getOptionalAttribute(attributesMap, "clef.dis.place")
								);
					}

					String notationType = getOptionalAttribute(attributesMap, "notationtype");
                    if (notationType != null) {
						if (notationType.equals("mensural")) {
							lastStaff.setNotationType(NotationType.eMensural);
                        } else {
							throw new ImportException("Unsupported notation type import: " + notationType);
						}
					}
					String meterCount = getOptionalAttribute(attributesMap, "meter.count");
					String meterUnit = getOptionalAttribute(attributesMap, "meter.unit");
					String meterSym  = getOptionalAttribute(attributesMap, "meter.sym");	
					String modusmaiorStr = getOptionalAttribute(attributesMap, "modusmaior");
					String modusminorStr = getOptionalAttribute(attributesMap, "modusminor");
					String tempusStr = getOptionalAttribute(attributesMap, "tempus");
					String prolatioStr = getOptionalAttribute(attributesMap, "prolatio");
					
					if (meterCount == null && meterUnit == null && meterSym == null) {
						meterCount = lastMeterCount;
						meterUnit = lastMeterUnit;
						meterSym	 = lastMeterSym;	
					} 
					
					if (prolatioStr == null && tempusStr == null && modusmaiorStr == null && modusminorStr == null) {
						modusmaiorStr = lastModusmaiorStr;
						modusminorStr = lastModusminorStr;
						tempusStr = lastTempusStr;
						prolatioStr	 = lastProlatioStr;					
					}
					
					processMeter(null, lastStaff, meterSym, meterCount, meterUnit, modusmaiorStr, modusminorStr, tempusStr, prolatioStr);
					String staffKeySig = getOptionalAttribute(attributesMap, "key.sig");
					String staffKeyMode = getOptionalAttribute(attributesMap, "key.mode");
					String staffTransDiat = getOptionalAttribute(attributesMap, "trans.diat");
					String staffTransSemi = getOptionalAttribute(attributesMap, "trans.semi");
					if (staffKeySig == null) {
						if (lastKeySig == null) {
							Logger.getLogger(MEISAXScoreSongImporter.class.getName()).log(Level.WARNING, "key.sig is null both in scoreDef and staffDeff");
						}

						staffKeySig = lastKeySig; // defined in scoreDef
						staffKeyMode = lastKeyMode; // defined in scoreDef
						staffTransDiat = lastTransDiat;
						staffTransSemi = lastTransSemi;
					}

					if (staffKeySig != null) {
                        processKey(null, lastStaff, staffKeySig, staffKeyMode, staffTransDiat, staffTransSemi);
                    }
					 
					//TODO Instrumentos transpositores
					// commented because they have 
					/*lastMeterCount = null;
					lastMeterUnit = null;
					lastMeterSym = null;
					lastKeySig = null;
					lastKeyMode = null;
					lastModusmaiorStr = null;
					lastModusminorStr = null;
					lastTempusStr = null;
					lastProlatioStr = null;*/
					
					break;
				case "mensur":
					modusmaiorStr = getOptionalAttribute(attributesMap, "modusmaior");
					modusminorStr = getOptionalAttribute(attributesMap, "modusminor");
					tempusStr = getOptionalAttribute(attributesMap, "tempus");
					prolatioStr = getOptionalAttribute(attributesMap, "prolatio");					
					TimeSignatureMensural ts = processPossibleMensuralMeter(modusmaiorStr, modusminorStr, tempusStr, prolatioStr );
					if (ts == null) {
						throw new ImportException("@mensur does not contain any parameter (modusmaior, tempus...)");
					}
					lastStaff.addTimeSignature(ts);
					break;				
				case "measure":
					staffCount=0;
                    previousAccidentals.clear();
					number = getOptionalAttribute(attributesMap, "n");
					xmlid = getOptionalAttribute(attributesMap, "xml:id");
					//updateMeasure = true;
					//lastMeasureXMLID = xmlid;
					//lastMeasureNumber = number;
					pendingMeasureRestsToSetDuration = null;
					pendingMultiMeasureRestsToSetDuration = null;
					currentMeasure = ImportFactories.processMeasure(song, lastMeasureEndTime, number);
                    xmlIDs.put(xmlid, currentMeasure);
					if (lastMeasureEndTime.isZero()) {
						maximumVoicesTime = Time.TIME_ZERO; // for mixed mensural and modern
					}
					break;
                case "sb":
                    horizontalOrderInStaff = 0;
                    Time sbtime = getCurrentTime();
                    // TODO: 17/11/17 A system en lugar de staff
                    if (!lastStaff.hasSystemBreak(sbtime )) {
                        lastStaff.addSystemBreak(new SystemBreak(sbtime, true));
                    }
                    break;
                case "pb":
                    // TODO: 17/11/17 A system en lugar de staff
                    horizontalOrderInStaff = 0;
                    Time pbtime = getCurrentTime();
                    // TODO: 17/11/17 A system en lugar de staff
                    if (!lastStaff.hasPageBreak(pbtime )) {
                        lastStaff.addPageBreak(new PageBreak(pbtime, true));
                    }
                    break;
				case "barLine":
                    previousAccidentals.clear();
                    horizontalOrderInStaff++;
                    //updateTimesGivenMeasure();
					Time markTime = getCurrentTime();
					MarkBarline barline = new MarkBarline(markTime);
					lastStaff.addMarkBarline(barline);
					break;
				case "staff":
					/*if (updateMeasure) {
						updateMeasure = false;
						updateTimesGivenMeasure();
						currentMeasure = ImportFactories.processMeasure(song, getCurrentTime(), lastMeasureNumber);
						xmlIDs.put(lastMeasureXMLID, currentMeasure);
					}*/
					
					staffCount++;	
					layerCount=0;
                    previousAccidentals.clear();
					number = getOptionalAttribute(attributesMap, "n");
					lastStaff = findStaff(number);
					break;
				case "layer":
					layerCount++;
					number = getOptionalAttribute(attributesMap, "n");
					lastVoice = processLayer(number);
                    horizontalOrderInStaff = 0;
					break;
				case "tuplet":
					tupletXMLID = getOptionalAttribute(attributesMap, "xml:id");
					tupletNum = Integer.parseInt(getAttribute(attributesMap, "num"));
					tupletNumBase = Integer.parseInt(getAttribute(attributesMap, "numbase"));
					tupletElements = new ArrayList<>();
					break;
				case "beam":
                    beamedGroupXMLID = getOptionalAttribute(attributesMap, "xml:id");
				    beamedGroupElements = new ArrayList<>();
					break;
				case "chord":
					//TODO stem dir
					xmlid = getOptionalAttribute(attributesMap, "xml:id");
					xmlIDs.put(xmlid, lastChord);
					dotsStr = getOptionalAttribute(attributesMap, "dots");
					dots = dotsStr==null?0:Integer.parseInt(dotsStr);
					dur = getAttribute(attributesMap, "dur");
					figure = getFigure(dur, attributesMap);
					
					//if (currentBeam != null) {
					//	currentBeam.addNoteOrChord(currentNote);
					//}
					
					lastChord = new SimpleChord(figure, dots);
					addElementToVoiceStaffOrTuplet(lastChord, xmlid, attributesMap, lastStaff);
					break;
				case "note":
					xmlid = getOptionalAttribute(attributesMap, "xml:id");
					String staffChange = getOptionalAttribute(attributesMap, "staff");
					
					if (staffChange != null) {
						elementStaff = findStaff(staffChange);
					} else {
						elementStaff = lastStaff;
					}
					dotsStr = getOptionalAttribute(attributesMap, "dots");
					dots = dotsStr==null?0:Integer.parseInt(dotsStr);
					dur = getOptionalAttribute(attributesMap, "dur");
					
					// scientific pitch
					String accidGes = getOptionalAttribute(attributesMap, "accid.ges");
					accid = getOptionalAttribute(attributesMap, "accid");
					String oct = getOptionalAttribute(attributesMap, "oct");
					int octave = Integer.parseInt(oct);
					String pname = getOptionalAttribute(attributesMap, "pname");
					
					PitchClass pc = new PitchClass(DiatonicPitch.valueOf(pname.toUpperCase()));
					Accidentals writtenAccidental = null;

					Time time = getCurrentTime(); //TODO ¿También cuando hay cambio de pentagrama?

					int previousAccidentalMapKey = generatePreviousAccidentalMapKey(pc.getNoteName(), octave);
                    Accidentals previousAccidental = previousAccidentals.get(previousAccidentalMapKey);
                    if (previousAccidental == null) {
                        try {
                            KeySignature ks = elementStaff.getRunningKeySignatureAt(time);
                            previousAccidental = ks.getAccidentalOf(pc.getNoteName());
                        } catch (IM3Exception e) {
                            //noop - for non key scores
                        }
                    }

                    if (accid == null && accidGes == null && previousAccidental != null) {
                        pc.setAccidental(previousAccidental); // TODO: 24/9/17 Diferenciar explicit e implicit. Igual en MEIExporter que aún lo exporta todo
                    } else {
                        if (accidGes != null) {
                            writtenAccidental = accidToAccidental(accidGes);
                            pc.setAccidental(writtenAccidental);
                            previousAccidentals.put(previousAccidentalMapKey, writtenAccidental);
                        }
                        if (accid != null) {
                            Accidentals acc = accidToAccidental(accid);
                            if (writtenAccidental != null && acc != writtenAccidental) {
                                throw new ImportException("Written accidental (" + writtenAccidental + ") inconsistent with performed accidental (" + acc + ")");
                            }
                            pc.setAccidental(acc);
                            previousAccidentals.put(previousAccidentalMapKey, acc);
                        }
                    }
                    ScientificPitch sp = new ScientificPitch(pc, octave);


					//TODO
					//currentNote.setStemDirection(parseStemDir(getOptionalAttribute(attributesMap, "stem.dir")));
					//figure = getFigure(dur, attributesMap);

					if (dur != null) {
						figure = getFigure(dur, attributesMap);
					} else {
						figure = null;
					}

                    //System.out.println("SP=" + sp);
                    AtomFigure currentAtomFigure;
					if (lastChord != null) {
						currentAtomFigure = lastChord.getAtomFigure();
						if (figure != null && (!currentAtomFigure.getFigure().equals(figure) || currentAtomFigure.getDots() != dots)) {
							throw new ImportException("Cannot import a chord with different figure durations");
						}
						lastAtomPitch = lastChord.addPitch(sp);
						setXMLID(xmlid, lastAtomPitch);
						if (elementStaff != lastStaff) {
							lastAtomPitch.setStaffChange(elementStaff);
							elementStaff.addCoreSymbol(lastAtomPitch);
						}
                        // TODO: 18/10/17 Comprobar grace notes acorde
                        //lastAtomPitch.setWrittenExplicitAccidental(writtenAccidental);
					} else {
						if (figure == null) {
							throw new ImportException("Cannot import note not in chord without dur");
						}

						currentNote = new SimpleNote(figure, dots, sp);
						currentAtomFigure = currentNote.getAtomFigure();
						lastAtomPitch = currentNote.getAtomPitch();
                        horizontalOrderInStaff++;
                        String grace = getOptionalAttribute(attributesMap, "grace");
                        // TODO: 18/10/17 acc, unacc, unknown - de quién quita la nota el valor
                        currentNote.setGrace(grace != null);

						/*if (elementStaff != lastStaff) {
							lastAtomPitch.setStaffChange(elementStaff);							
							elementStaff.addCoreSymbol(lastAtomPitch);
						} else {
							lastStaff.addCoreSymbol(currentNote);
						}*/
						//lastAtomPitch.setWrittenExplicitAccidental(writtenAccidental);
						addElementToVoiceStaffOrTuplet(currentNote, xmlid, attributesMap, elementStaff);
						//currentNote.setTime(getCurrentTime());
						//if (currentBeam != null) {
						//	currentBeam.addNoteOrChord(currentNote);
						//}

					}

					if (lastCustosWithoutPitch != null) {
                        lastCustosWithoutPitch.setDiatonicPitch(lastAtomPitch.getScientificPitch().getPitchClass().getNoteName());
                        lastCustosWithoutPitch.setOctave(lastAtomPitch.getScientificPitch().getOctave());
                        lastCustosWithoutPitch = null;
                    }
                    // TODO: 18/10/17 Comprobar Fermata con chords
					processPossibleMensuralImperfection(attributesMap, currentAtomFigure);
					String tie = getOptionalAttribute(attributesMap, "tie");
					if (tie != null) { 						
						handleTie(tie, figure, dots, sp, xmlid, attributesMap);
					}

					String mfunc = getOptionalAttribute(attributesMap, "mfunc");
					if (mfunc != null) {
						mfunc = "mf" + mfunc.toUpperCase(); // from sus to mfSUS
						MelodicFunction mf = MelodicFunction.valueOf(mfunc);
						lastAtomPitch.setMelodicFunction(mf);
					}


					updateCurrentTime();

					// after the note has time
                    processFermata(currentAtomFigure, attributesMap); // fermata as an attribute - it can be also added as an element


                    break;
				case "accid":
				    //TODO Posibilidad de poner horizontalPositionInStaff
					accid = getOptionalAttribute(attributesMap, "accid");
					accidGes = getOptionalAttribute(attributesMap, "accid.ges");
					
					writtenAccidental = null;
					
					if (accidGes != null) {
						writtenAccidental = accidToAccidental(accidGes);
						currentNote.getAtomPitch().setWrittenExplicitAccidental(writtenAccidental);
					} 
					
					if (accid != null) {
						Accidentals acc = accidToAccidental(accid);
						if (writtenAccidental != null && acc != writtenAccidental) {
							throw new ImportException("Written accidental (" + writtenAccidental + ") inconsistent with performed accidental (" + acc + ")");
						}
						currentNote.setWrittenExplicitAccidental(acc);
					}
					break;
					
				case "rest":
					xmlid = getOptionalAttribute(attributesMap, "xml:id");
					dotsStr = getOptionalAttribute(attributesMap, "dots");
					dots = dotsStr==null?0:Integer.parseInt(dotsStr);
					dur = getOptionalAttribute(attributesMap, "dur");
					figure = getFigure(dur, attributesMap);					
					SimpleRest rest = new SimpleRest(figure, dots);
                    horizontalOrderInStaff++;
					//rest.setTime(getCurrentTime());
					String restStaffChange = getOptionalAttribute(attributesMap, "staff");
					
					if (restStaffChange != null) {
						elementStaff = findStaff(restStaffChange);
					} else {
						elementStaff = lastStaff;
					}

					addElementToVoiceStaffOrTuplet(rest, xmlid, attributesMap, elementStaff);

                    // after the note has time
                    processFermata(rest.getAtomFigure(), attributesMap);

                    break;
				case "mRest":
					//TODO
					xmlid = getOptionalAttribute(attributesMap, "xml:id");
					
					dur = getOptionalAttribute(attributesMap, "dur");
					if (dur != null) {
						figure = getFigure(dur, attributesMap);
					} else {
						figure = null;
					}
					
					//TODO mrests vacíos
					String mRestStaffChange = getOptionalAttribute(attributesMap, "staff");
					
					if (mRestStaffChange != null) {
						elementStaff = findStaff(mRestStaffChange);
					} else {
						elementStaff = lastStaff;
					}
					
					//SimpleMeasureRest mrest = new SimpleMeasureRest(currentMeasure.getDuration(elementStaff).getExactTime());
					SimpleMeasureRest mrest = null;
					if (figure != null) {
						mrest = new SimpleMeasureRest(figure, figure.getDuration());
					} else {
						mrest = new SimpleMeasureRest(Figures.WHOLE, Figures.NO_DURATION.getDuration());
						if (pendingMeasureRestsToSetDuration == null) {
							pendingMeasureRestsToSetDuration = new ArrayList<>();
						}
						pendingMeasureRestsToSetDuration.add(mrest);
					}

                    horizontalOrderInStaff++;
                    //rest.setTime(getCurrentTime());
					addElementToVoiceStaffOrTuplet(mrest, xmlid, attributesMap, elementStaff);
					break;
                case "multiRest":
						//TODO
						xmlid = getOptionalAttribute(attributesMap, "xml:id");

						Integer num = Integer.parseInt(getAttribute(attributesMap, "num"));

						SimpleMultiMeasureRest multiMeasureRest = new SimpleMultiMeasureRest(Figures.WHOLE, Figures.NO_DURATION.getDuration(), num);
						if (pendingMultiMeasureRestsToSetDuration == null) {
                            pendingMultiMeasureRestsToSetDuration = new ArrayList<>();
                        }
                        pendingMultiMeasureRestsToSetDuration.add(multiMeasureRest);

                        horizontalOrderInStaff++;

                    //rest.setTime(getCurrentTime());
						addElementToVoiceStaffOrTuplet(multiMeasureRest, xmlid, attributesMap, lastStaff);
						break;
                case "clef":
					//TODO No sé para qué vale el parámetro staff aquí, cuando está dentro de uno ya...
					clefLine = getOptionalAttribute(attributesMap, "line");
					clefShape = getOptionalAttribute(attributesMap, "shape");				
					tstamp = getOptionalAttribute(attributesMap, "tstamp");
					Time clefTime;
					if (tstamp != null) {
						clefTime = decodeTStamp(currentMeasure, attributesMap);
					} else {
						clefTime = getCurrentTime();
					}

                    //tstamp = getOptionalAttribute(attributesMap, "tstamp");
					//double clefTime = getCurrentTime();
					//if (tstamp != null) {
					//	clefTime += Double.parseDouble(tstamp);
					//}
                    Clef clef = processClef(clefLine, clefShape, clefTime, getOptionalAttribute(attributesMap, "dis"),
                            getOptionalAttribute(attributesMap, "dis.place"));
                    horizontalOrderInStaff++;
                    break;
                case "custos":
                    oct = getOptionalAttribute(attributesMap, "oct");
                    pname = getOptionalAttribute(attributesMap, "pname");
                    Custos custos;
                    if (oct != null && pname != null) {
                        octave = Integer.parseInt(oct);
                        DiatonicPitch dp = DiatonicPitch.valueOf(pname.toUpperCase());
                        custos = new Custos(lastStaff, getCurrentTime(), dp, octave);
                        lastCustosWithoutPitch = null;
                    } else {
                        custos = new Custos(lastStaff, getCurrentTime());
                        lastCustosWithoutPitch = custos;
                    }

                    lastStaff.addCustos(custos);
                    break;
				case "tie":
					staffNumber = getOptionalAttribute(attributesMap, "staff");
					pendingConnectorOrMark = new PendingConnectorOrMark();
					pendingConnectorOrMark.tag = element;
					pendingConnectorOrMark.measure = currentMeasure;
					pendingConnectorOrMark.startid = getAttribute(attributesMap, "startid");
					pendingConnectorOrMark.endid = getAttribute(attributesMap, "endid");
					if (pendingConnectorOrMarks.contains(pendingConnectorOrMark)) {
						throw new ImportException("Duplicating pending connector: " + pendingConnectorOrMark);
					}
					pendingConnectorOrMarks.add(pendingConnectorOrMark);
					break;
                case "fermata":
                case "trill":
				case "phrase": 
				case "slur":
				case "hairpin":
					staffNumber = getOptionalAttribute(attributesMap, "staff");
					layerNumber = getOptionalAttribute(attributesMap, "layer");
					pendingConnectorOrMark = new PendingConnectorOrMark();
					pendingConnectorOrMark.tag = element;
					pendingConnectorOrMark.measure = currentMeasure;
					if (layerNumber != null) {
                        pendingConnectorOrMark.layer = processLayer(layerNumber);
                    } else {
					    if (lastVoice == null) {
					        throw new ImportException("Last voice is null while importing a " + element);
                        }
                        pendingConnectorOrMark.layer = lastVoice;
                    }
					pendingConnectorOrMark.tstamp = getOptionalAttribute(attributesMap, "tstamp");
					pendingConnectorOrMark.tstamp2 = getOptionalAttribute(attributesMap, "tstamp2");
					pendingConnectorOrMark.startid = getOptionalAttribute(attributesMap, "startid");
					pendingConnectorOrMark.endid = getOptionalAttribute(attributesMap, "endid");
					if (staffNumber != null) {
						pendingConnectorOrMark.staff = findStaff(staffNumber);
					}
					if (element.equals("hairpin")) {
						pendingConnectorOrMark.content = getAttribute(attributesMap, "form");
					} else if (element.equals("fermata")) {
                        pendingConnectorOrMark.content = getAttribute(attributesMap, "place");
                    }
					if (pendingConnectorOrMarks.contains(pendingConnectorOrMark)) {
						throw new ImportException("Duplicating pending connector: " + pendingConnectorOrMark);
					}
                    pendingConnectorOrMarks.add(pendingConnectorOrMark);
					break;
				case "dynam":
					dynamTime = decodeTStamp(currentMeasure, attributesMap);
					staffNumber = getAttribute(attributesMap, "staff");
					lastStaff = findStaff(staffNumber);
					break;
                case "harm":
                    harmTime = decodeTStamp(currentMeasure, attributesMap);
                    harmType = getOptionalAttribute(attributesMap, "type");
                    break;
                case "verse":
                    String verseNumberStr = getOptionalAttribute(attributesMap, "n");
                    lastVerse = new ScoreLyric(verseNumberStr == null ? null : Integer.parseInt(verseNumberStr), lastAtomPitch, null, null);
                    lastAtomPitch.addLyric(lastVerse);
                    break;
                case "syl":
                    String sylType = getOptionalAttribute(attributesMap, "wordpos");
                    if (sylType != null) {
                        lastVerse.setSyllabic(wordpos2Syllabic(sylType));
                    }
                    break;
                case "dot":
                    Time addedDuration = lastAtomPitch.getAtomFigure().addDot(); // TODO: 27/10/17 Ver casos como mensural patriarca.mei donde aparece nota - barra - dot
                    horizontalOrderInStaff++;
                    lastAtomPitch.addDisplacedDot(new DisplacedDot(getCurrentTime(), lastAtomPitch));
                    setCurrentTime(getCurrentTime().add(addedDuration));
                    break;
				}
			}
		} catch (Exception e) {
			throw new ImportException(e);
		}
	}

    private void processFermata(AtomFigure atomFigure, HashMap<String, String> attributesMap) throws IM3Exception {
        String fermata = getOptionalAttribute(attributesMap, "fermata");
        if (fermata != null) {
            PositionAboveBelow positionAboveBelow;
            switch (fermata) {
                case "above":
                    positionAboveBelow = PositionAboveBelow.ABOVE;
                    break;
                case "below":
                    positionAboveBelow = PositionAboveBelow.BELOW;
                    break;
                default:
                    throw new ImportException("Unknown fermata position: '" + fermata + "'");
            }
            lastStaff.addFermata(atomFigure, positionAboveBelow);
        }
        
    }

    private Syllabic wordpos2Syllabic(String sylType) throws ImportException {
        switch (sylType) {
            case "i": // initial
                return Syllabic.begin;
            case "m": // middle
                return Syllabic.middle;
            case "t": // terminal
                return Syllabic.end;
            default:
                throw new ImportException("Unknown syllabic type: " + sylType);

        }
    }

    private int generatePreviousAccidentalMapKey(DiatonicPitch noteName, int octave) {
        return noteName.getOrder() + octave * 7;
    }


    /**
	 * 
	 * @param staff If null it will be inserted to all staves
	 * @param meterSym
	 * @param meterCount
	 * @param meterUnit
	 * @param modusmaiorStr
	 * @param modusminorStr
	 * @param tempusStr
	 * @param prolatioStr
	 * @throws IM3Exception 
	 * @throws ImportException 
	 */
	private void processMeter(String xmlid, Staff staff, String meterSym, String meterCount, String meterUnit,
			String modusmaiorStr, String modusminorStr, String tempusStr, String prolatioStr) throws ImportException, IM3Exception {
		TimeSignature timeSignature;
		TimeSignatureMensural mensuralMeter = processPossibleMensuralMeter(modusmaiorStr, modusminorStr, tempusStr, prolatioStr);
		if (mensuralMeter != null) {
			timeSignature = mensuralMeter;
		} else {
			timeSignature = ImportFactories.processMeter(meterSym, meterCount, meterUnit);
			timeSignature.setStaff(lastStaff);
		}
		if (staff == null) {
			for (Staff s: song.getStaves()) {
				addTimeSignatureIgnoreIfExists(s, timeSignature, maximumVoicesTime, xmlid);
			}
		} else {
			addTimeSignatureIgnoreIfExists(staff, timeSignature, getCurrentTime(), xmlid);	
		}		
	}
	
	private void addTimeSignatureIgnoreIfExists(Staff staff, TimeSignature ts, Time time, String xmlid) throws IM3Exception {
		if (staff.getTimeSignatureWithOnset(time) == null) {
			ts.setTime(time);
			song.getIdManager().assignID(xmlid, ts);
			staff.addTimeSignature(ts);
		} 
	}

	private void processPossibleMensuralImperfection(HashMap<String, String> attributesMap,
			AtomFigure currentAtomFigure) throws IM3Exception, ImportException {
		String numBase = getOptionalAttribute(attributesMap, "numbase");
		String num = getOptionalAttribute(attributesMap, "num");
		TimeSignature lastTimeSignature = lastStaff.getRunningTimeSignatureAt(currentAtomFigure);
		if (lastTimeSignature instanceof TimeSignatureMensural) {
			TimeSignatureMensural mmeter = (TimeSignatureMensural) lastTimeSignature;
			figureDuration = mmeter.getDuration(currentAtomFigure.getFigure());
			currentAtomFigure.setSpecialDuration(figureDuration);
		}
		if (numBase != null || num != null) {
			if (numBase == null || num == null) {
				throw new ImportException("When @numbase or @num are specified, both must be present");
			}
			// e.g. imperfection in mensural //TODO Tuplets
			int irregularGroupActualFigures = Integer.parseInt(num);
			int irregularGroupInSpaceOfFigures = Integer.parseInt(numBase);
			// it computes the duration
			currentAtomFigure.setIrregularGroup(irregularGroupActualFigures, irregularGroupInSpaceOfFigures);
		}
		
		String colored = getOptionalAttribute(attributesMap, "colored");
		if (colored != null) {
			currentAtomFigure.setColored(Boolean.getBoolean(colored));
		}
	}

	private ScoreLayer processLayer(String number) throws IM3Exception {
		String voiceNumber = lastStaff.getNumberIdentifier() + "_" + number;
		ScoreLayer voice = layers.get(voiceNumber);
		if (voice == null) {
            ScorePart scorePart = getScorePart(lastStaff);

			//voice = currentScorePart.addScoreLayer(lastStaff);
            voice = scorePart.addScoreLayer(lastStaff);
            voice.setDurationEvaluator(durationEvaluator);
			layers.put(voiceNumber, voice);
		}
		return voice;
	}

	//TODO Esto sólo permite que en un staff haya un instrumento
    private ScorePart getScorePart(Staff lastStaff) throws ImportException {
	    for (ScorePart part: song.getParts()) {
	        for (Staff staff: part.getStaves()) {
	            if (staff == lastStaff) {
	                return part;
                }
            }
        }
        throw new ImportException("Staff" + lastStaff + " not found");
    }

    private Staff processStaff(HashMap<String, String> attributesMap) throws ImportException, IM3Exception {
		Staff staff;
		String number = getOptionalAttribute(attributesMap, "n");
		String lines = getOptionalAttribute(attributesMap, "lines");
		String label = getOptionalAttribute(attributesMap, "label");
		
		if (lines == null || lines.equals("5")) {
				staff = new Pentagram(song, hierarchicalIdGenerator.nextStaffHierarchicalOrder(null),
						hierarchicalIdGenerator.getNextVerticalDivisionIdentifier());
		} else if (lines.equals("1")) {
				staff = new PercussionStaff(song, hierarchicalIdGenerator.nextStaffHierarchicalOrder(null),
						hierarchicalIdGenerator.getNextVerticalDivisionIdentifier());
		} else {
				throw new ImportException("Unimplemented staves with " + lines + " lines");
		}
		staffNumbers.put(number, staff);
        staff.setName(label);
		staff.setOssia(inOssia);
		song.addStaff(staff);
        staff.addPart(currentScorePart); // TODO: 20/11/17 Parts when two parts in a staff
        return staff;
	}

	private Staff findStaff(String number) throws ImportException {
		Staff result = staffNumbers.get(number);
		if (result == null) {
			throw new ImportException("Cannot find staff with number '" + number + "'");
		}
		return result;
	}

	/**
	 * 
	 * @param prolatioStr 
	 * @param tempusStr 
	 * @param modusminorStr 
	 * @param modusmaiorStr 
	 * @return null if not found
	 * @throws ImportException
	 * @throws IM3Exception
	 */
	private TimeSignatureMensural processPossibleMensuralMeter(String modusmaiorStr, String modusminorStr, String tempusStr, String prolatioStr) throws ImportException, IM3Exception {
		if (modusmaiorStr != null || modusminorStr != null || tempusStr != null || prolatioStr != null) {
			// mensural		
			Perfection modusMaior = convertMeiMensuralPerfectionNumber(modusmaiorStr);
			Perfection modusMinor = convertMeiMensuralPerfectionNumber(modusminorStr);
			Perfection tempus = convertMeiMensuralPerfectionNumber(tempusStr);
			Perfection prolatio = convertMeiMensuralPerfectionNumber(prolatioStr);
			TimeSignatureMensural meter = TimeSignatureMensuralFactory.getInstance().create(modusMaior, modusMinor, tempus, prolatio);
			meter.setStaff(lastStaff);
			Time time = getCurrentTime();
			meter.setTime(time); // if not set here, the equals does not work
			//TODO @sign, @orient, @slash, @dot (pag 144 de mei guidelines) - ver tb. mails foros
			return meter;
		} else {
			return null;
		}
	}



    private Perfection convertMeiMensuralPerfectionNumber(String number) throws ImportException {
		if (number == null) {
			return null;
		} else if (number.equals("2")) {
			return Perfection.imperfectum;
		} else if (number.equals("3")) {
			return Perfection.perfectum;
		} else {
			throw new ImportException("Invalid mensural meter perfection specification: " + number + ", should be null, 2, or 3");
		}
	}

	/*FRACCIONES private StemDirection parseStemDir(String stemDir) throws ImportException {
		if (stemDir == null) {
			return StemDirection.computed;
		} else if (stemDir.equals("up")) {
			return StemDirection.up;
		} else if (stemDir.equals("down")) {
			return StemDirection.down;
		} else {
			throw new ImportException("Invalid stem direction: " + stemDir);
		}
	}*/

	public Time decodeTStamp(Measure measure, HashMap<String, String> attributesMap) throws ImportException {
		String tstamp = getOptionalAttribute(attributesMap, "tstamp");
		if (tstamp != null) {
			return decodeTStamp(measure, tstamp);
		}
		throw new ImportException("Cannot get tstamp among attributes: " + attributesMap);
	}
	
	public Time decodeTStamp(Measure measure, String tstamp) throws ImportException {
		try {
			return measure.getTime().add(new Time(Fraction.getFraction(Double.parseDouble(tstamp)-1)));
		} catch (Exception e) {
			throw new ImportException(e);
		}
	}

	private Clef processClef(String clefLine, String clefShape, Time time, String octaveDisplace, String octaveDisplacePosition) throws ImportException, IM3Exception {
		Integer octaveChange = null;
		if (octaveDisplace != null) {
			int idisp = Integer.parseInt(octaveDisplace);
			if (idisp == 8) {
				octaveChange = 1;
			} else if (idisp == 15) {
				octaveChange = 2;
			} else {
				throw new ImportException("Unsupported octave displace: " + octaveDisplace);
			}
			if (octaveDisplacePosition == null) {
				throw new ImportException("Missing @clef.dis.place");
			}
			if (octaveDisplacePosition.equals("below")) {
				octaveChange = -octaveChange;
			} else if (octaveDisplacePosition.equals("below")) {
				// nothing
			} else {
				throw new ImportException("Invalid @clef.dis.place");
			}
		}		
		
		lastClef = ImportFactories.createClef(lastStaff.getNotationType(), clefShape, 
				Integer.parseInt(clefLine), octaveChange);
		lastClef.setTime(time);
		lastStaff.addClef(lastClef);
		return lastClef;
	}


	/**
	 * @param staff If null, it will be inserted to all staves
	 * @param keySig
	 * @param keyMode
	 * @param transSemi 
	 * @param transDiat 
	 * @throws ImportException
	 * @throws IM3Exception
	 */
	private void processKey(String xmlid, Staff staff, String keySig, String keyMode, String transDiat, String transSemi) throws ImportException, IM3Exception {
		int fifths;
		if (keySig.isEmpty()) {
			throw new ImportException("Cannot parse empty keysig"); 
		}
		if (keySig.equals("0")) {
			fifths = 0;
		} else {
			fifths = Integer.parseInt(keySig.substring(0, keySig.length()-1));
			char sharpOrFlat = keySig.charAt(keySig.length()-1);
			if (sharpOrFlat == 's') {
				// nothing
			} else if (sharpOrFlat == 'f') {
				fifths = -fifths;
			} else {
				throw new ImportException("Unkown key termination, expected 's' or 'f': " + keySig);
			}
		}
		Mode mode;
		if (keyMode == null || keyMode.equals("major")) {
			mode = Mode.MAJOR;
		} else if (keyMode.equals("minor")) {
			mode = Mode.MINOR;
		} else {
			throw new ImportException("Invalid mode: '" + keyMode + "'");
		}
		Key key = new Key(fifths, mode);
		KeySignature result = new KeySignature(null, key);
		
		if (transDiat != null || transSemi != null) {
			if (transDiat == null || transSemi == null) {
				throw new ImportException("Missing trans.diat or trans.semi");
			}			
			int intervalName = Integer.parseInt(transDiat);
			int semitones = Integer.parseInt(transSemi);
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
			result.setTranspositionInterval(transpositionInterval);
		}
		
		if (staff == null) {
			for (Staff s: song.getStaves()) {
				addKeySignatureIgnoreIfExists(s, result, maximumVoicesTime, xmlid);
			}
		} else {
			addKeySignatureIgnoreIfExists(staff, result, maximumVoicesTime, xmlid);
		}		
	}
	

	private void addKeySignatureIgnoreIfExists(Staff staff, KeySignature ts, Time time, String xmlid) throws IM3Exception {
		if (staff.getKeySignatureWithOnset(time) == null) {
			ts.setTime(time);
			song.getIdManager().assignID(xmlid, ts);
			staff.addKeySignature(ts);
		} 
	}

	/**
	 * If the note has been absorbed (tied to previous)
	 * @param type
	 * @param figure
	 * @param dots
	 * @param sp 
	 * @param xmlid 
	 * @param attributesMap 
	 * @return
	 * @throws ImportException
	 * @throws IM3Exception
	 */
	private void handleTie(String type, Figures figure, int dots, ScientificPitch sp, String xmlid, HashMap<String, String> attributesMap) throws ImportException, IM3Exception {		
		String tieCode = lastStaff.hashCode() + "_" + lastVoice.hashCode() + "_" + sp.hashCode();

		if (lastAtomPitch == null) {
			throw new IM3RuntimeException("lastAtomPitch cannot be null");
		}
		AtomPitch tiedFrom = currentTies.get(tieCode);
		if (tiedFrom == null) {
			if (type.equals("i")) { // from
				currentTies.put(tieCode, lastAtomPitch);
			} // else already inserted
		} else {
			if (type.equals("t")) { // end
				//Atom fromAtom = tiedFrom.getAtomFigure().getAtom();
				//SimpleNote tiedNote = new SimpleNote(figure, dots, tiedFrom.getScientificPitch());
				//tiedNote.getAtomPitch().setTiedFromPrevious(tiedFrom);
				tiedFrom.setTiedToNext(lastAtomPitch);
				currentTies.remove(tieCode);
				//addElementToVoiceStaffOrTuplet(tiedNote, xmlid, attributesMap);
			} else if (type.equals("m")) { // middle
				tiedFrom.setTiedToNext(lastAtomPitch);
				currentTies.remove(tieCode);
				
				currentTies.put(tieCode, lastAtomPitch);
			}
			
		}
	}
	
	private Accidentals accidToAccidental(String accid) throws ImportException {
		switch (accid) {
		case "s":
			return Accidentals.SHARP;
		case "f":
			return Accidentals.FLAT;
		case "n":
			return Accidentals.NATURAL;
		case "ss":
		case "x":
			return Accidentals.DOUBLE_SHARP;
		case "ff":
			return Accidentals.DOUBLE_FLAT;
		case "tf":
			return Accidentals.TRIPLE_FLAT;
		default:
			throw new ImportException("Invalid accid: " + accid);
		}
	}

	@Override
	public void handleElementContent(String currentElement, String content) throws ImportException {
		try {
			switch (currentElement) {
                case "title": //TODO Gestionar esto bien (work, ....)  - lo único que es obligatorio es fileDesc
                    if (//song.getWork() != null && // it is inside a work
                            "titleStmt".equals(getParentElement())) {
                        //song.getWork().setTitle(content);
                        song.addTitle(content);
                    }
                    break;
                case "persName":
                    if (//song.getWork() != null && // it is inside a work
                            "respStmt".equals(getParentElement()) && personRole != null) {
                        //song.getWork().addPerson(personRole, content);
                        song.addPerson(personRole, content);
                    }
                    break;
                case "dynam":
                    addDynamics(lastStaff, content, dynamTime);
                    dynamTime = null;
                    break;
                case "harm":
                    addHarm(content, harmTime);
                    harmTime = null;
                    break;
                case "syl":
                    lastVerse.setText(content);
                    break;
            }
		} catch (IM3Exception e) {
			throw new ImportException(e);
		}
	}

    private void addHarm(String content, Time harmTime) throws IM3Exception, ImportException {
	    if (kernImporter == null) {
            kernImporter = new KernImporter();
        }

        Key key = song.getUniqueKeyActiveAtTime(harmTime);
        if (harmType == null) {
            Harm harm = kernImporter.readHarmony(key, content);
            harm.setTime(harmTime);
            song.addHarm(harm);
        } else {
	        // use with care, not yet approved by MEI
            Harm harm = song.getHarmWithOnsetOrNull(harmTime);

            switch (harmType) {
                case MEISongExporter.HARM_TYPE_TONAL_FUNCTION:
                    TonalFunction tonalFunction = TonalFunction.getTonalFunctionFromString(content);
                    if (harm != null) {
                        harm.setTonalFunction(tonalFunction);
                    } else {
                        pendingHarmTonalFunctionTime = harmTime;
                        pendingHarmTonalFunction = tonalFunction;
                    }
                    break;
                case MEISongExporter.HARM_TYPE_DEGREE:
                    if (harm != null) {
                        throw new ImportException("There is other harm with degree specified (" + harm + ") at time (" + harmTime + ") when inserting degree: "  + content);
                    }

                    if (pendingHarmKeyTime != null && pendingHarmKey != null && !pendingHarmKeyTime.equals(harmTime)) {
                        throw new ImportException("The pending harm key (" + pendingHarmKey + ") has a different time (" + pendingHarmKeyTime + "), expected (" + harmTime + ")");
                    }
                    harm = kernImporter.readHarmony(pendingHarmKey != null?pendingHarmKey:key, content);

                    if (pendingHarmTonalFunctionTime != null && pendingHarmTonalFunction != null && !pendingHarmTonalFunctionTime.equals(harmTime)) {
                        throw new ImportException("The pending harm tonal function (" + pendingHarmTonalFunction + ") has a different time (" + pendingHarmTonalFunctionTime + "), expected (" + harmTime + ")");
                    }

                    harm.setTonalFunction(pendingHarmTonalFunction);
                    harm.setTime(harmTime);
                    song.addHarm(harm);

                    pendingHarmKey = null;
                    pendingHarmKeyTime = null;
                    pendingHarmTonalFunction = null;
                    pendingHarmTonalFunctionTime = null;
                    break;
                case MEISongExporter.HARM_TYPE_KEY:
                    Key newKey = Key.getKeyFromName(content);
                    if (harm != null) {
                        harm.setKey(newKey);
                    } else {
                        pendingHarmKey = newKey;
                        pendingHarmKeyTime = harmTime;
                    }
                    break;
            }
        }
    }

    private void addDynamics(Staff staff, String amdynamics, Time time) throws IM3Exception, ImportException {
		DynamicMark m;
		switch (amdynamics) { 
		case "fff":
			m = new DynamicMarkFortePossible(staff, time);
			break;					
		case "ff":
			m = new DynamicMarkFortissimo(staff, time);
			break;					
		case "f":
			m = new DynamicMarkForte(staff, time);
			break;					
		case "mf":
			m = new DynamicMarkMezzoForte(staff, time);
			break;
		case "mp":
			m = new DynamicMarkMezzoPiano(staff, time);
			break;
		case "p": 
			m = new DynamicMarkPiano(staff, time);
			break;
		case "pp": 
			m = new DynamicMarkPianissimo(staff, time);
			break;
		case "ppp": 
			m = new DynamicMarkPianoPossible(staff, time);
			break;		
			default:
				throw new ImportException("Unsupported dynamics: "  +  amdynamics);
		}
		staff.addMark(m);
	}	
	
	@Override
	protected void handleElementClose(String closingElement) throws ImportException, IM3Exception {
		if (importingMusic) {
			switch (closingElement) {
			case "chord":
				lastChord = null;
				break;
			case "beam":
                BeamGroup beamedGroup = new BeamGroup(false);
                for (SingleFigureAtom atom: beamedGroupElements) {
                    beamedGroup.add(atom);
                }
                beamedGroupElements = null;
                beamedGroupXMLID = null;
                break;
			case "tuplet":
				// now create the tuplets
				// get whole duration (without taking into account the tuplet, i.e. 8th + 8th + 8th)
				Time wholeNonTupletDuration = Time.TIME_ZERO;
				for (Atom atom: tupletElements) {
					wholeNonTupletDuration = wholeNonTupletDuration.add(atom.getDuration());
				}				
				Time wholeTupletDuration = wholeNonTupletDuration.divide(tupletNum).multiply(tupletNumBase);
				
				Figures eachFigure = Figures.findDuration(wholeTupletDuration.divideBy(Fraction.getFraction(tupletNumBase, 1)), NotationType.eModern); // no tuplets in non modern
				
				SimpleTuplet tuplet = new SimpleTuplet(tupletNum, tupletNumBase, eachFigure, tupletElements);
				tupletElements = null;
				addElementToVoiceStaffOrTuplet(tuplet, tupletXMLID, null, lastStaff);
                tupletXMLID = null;
				break;
			case "music":
				importingMusic = false;
				break;
			case "dynam":
				dynamTime = null;
				break;
			case "ossia":
				inOssia = false;
				break;
			case "scoreDef":
				if (lastMeterCount != null ||  lastProlatioStr != null) {
					processMeter(null, null, lastMeterSym, lastMeterCount, lastMeterUnit, lastModusmaiorStr, lastModusminorStr, lastTempusStr, lastProlatioStr);					
				}
				if (lastKeySig != null) {
					processKey(null, null, lastKeySig, lastKeyMode, null, null);
				}
				break;
			case "staffDef":
			    if (currentScorePart == null) {
			        currentScorePart = song.addPart();
                }
			    currentScorePart.addStaff(lastStaff);
			    break;
			case "measure":
			    //TODO Check there are not any notes or rests
                if (pendingMeasureRestsToSetDuration != null && !pendingMeasureRestsToSetDuration.isEmpty()
                    && pendingMultiMeasureRestsToSetDuration != null && !pendingMultiMeasureRestsToSetDuration.isEmpty()) {
                    throw new ImportException("Cannot create both multimeasure rests and measure rests");
                }

				if (pendingMeasureRestsToSetDuration != null && !pendingMeasureRestsToSetDuration.isEmpty()) {
                    Time measureDuration = null;
                    for (SimpleMeasureRest mrest : pendingMeasureRestsToSetDuration) {
                        if (measureDuration == null) {
                            if (!currentMeasure.getTime().isZero() || !(mrest instanceof SimpleMeasureRest)) { // avoid first bar for anacrusis (not for multimeasure rests)
                                TimeSignature ts = mrest.getStaff().getRunningTimeSignatureAt(mrest);
                                if (ts == null) {
                                    throw new ImportException("Cannot infer the measure duration without time signatures at element " + mrest);
                                }
								measureDuration = ts.getDuration();
								currentMeasure.setEndTime(currentMeasure.getTime().add(measureDuration));
                            } else {
                                if (maximumVoicesTime.isZero()) { // for multimeasure rests starting the staff
                                    TimeSignature ts = mrest.getStaff().getRunningTimeSignatureAt(mrest);
                                    if (ts == null) {
                                        throw new ImportException("Cannot infer the measure duration without time signatures at element " + mrest);
                                    }
                                    measureDuration = ts.getDuration();
                                    currentMeasure.setEndTime(currentMeasure.getTime().add(measureDuration));
                                    //throw new ImportException("Cannot infer the measure duration for mRest or multiRest " + mrest);
                                } else {
                                    measureDuration = maximumVoicesTime.substract(currentMeasure.getTime());
                                    currentMeasure.setEndTime(currentMeasure.getTime().add(measureDuration));
                                }
                            }
                            mrest.setDuration(measureDuration);
                            lastVoice = mrest.getLayer();
                            setCurrentTime(mrest.getOffset());
                        }
                    }
                } else if (pendingMultiMeasureRestsToSetDuration != null && !pendingMultiMeasureRestsToSetDuration.isEmpty()) {
                    // ckeck all num are the same
                    Integer num = null;

                    for (SimpleMultiMeasureRest mm : pendingMultiMeasureRestsToSetDuration) {
                        if (num == null) {
                            num = mm.getNumMeasures();
                        } else if (num != mm.getNumMeasures()) {
                            throw new ImportException("Two multimeasure rests have different num: " + num + " and " + mm.getNumMeasures());
                        }
                    }

                    SimpleMultiMeasureRest firstMrest = pendingMultiMeasureRestsToSetDuration.get(0);
                    TimeSignature ts = firstMrest.getStaff().getRunningTimeSignatureAt(firstMrest);
                    if (ts == null) {
                        throw new ImportException("Cannot infer the measure duration without time signatures at element " + firstMrest);
                    }
                    Time measureDuration = ts.getDuration();

                    currentMeasure.setEndTime(currentMeasure.getTime().add(measureDuration));

                    Time time = currentMeasure.getEndTime();
                    Integer nmeasure = currentMeasure.getNumber();
                    // create the number of measures (but the current one)
                    for (int i=0; i<num-1; i++) {
                        Measure measure;
                        if (nmeasure != null) {
                            measure = new Measure(song, nmeasure++);
                        } else {
                            measure = new Measure(song);
                        }
                        song.addMeasure(time, measure);
                        time =time.add(measureDuration);
                        measure.setEndTime(time);
                        currentMeasure = measure;

                    }

                    for (SimpleMultiMeasureRest mm : pendingMultiMeasureRestsToSetDuration) {
                        mm.setDuration(measureDuration.multiply(num));
                        setCurrentTime(mm.getStaff(), mm.getLayer(), mm.getOffset());
                    }
				} else {
					currentMeasure.setEndTime(maximumVoicesTime);
				}

                //lastMeasureEndTime = maximumVoicesTime;
				lastMeasureEndTime = currentMeasure.getEndTime();
				maximumVoicesTime = lastMeasureEndTime;
                //currentMeasure.setEndTime(maximumVoicesTime);
				//updateTimesGivenMeasure(measure);
				break;
			}
		}
	}
	
	/**
	 * Once a new measure is found, all time counters should be updated
	 * @param measure 
	 * @return
	 * @throws ImportException 
	 * @throws IM3Exception 
	 */
	private void updateTimesGivenMeasure(Measure measure) throws ImportException, IM3Exception {
		/*if (measure != null) {
			TimeSignature lastTimeSignature = lastStaff.getRunningTimeSignatureAt(currentMeasure);
			if (lastTimeSignature != null && lastTimeSignature instanceof ITimeSignatureWithDuration) { 
				setCurrentTime(measure.getEndTime());
			}
		} else if (lastVoice != null) {
			setCurrentTime(Time.TIME_ZERO);
		}*/
		setCurrentTime(measure.getEndTime());
	}

	
	@Override
	protected void postProcess() throws ImportException, IM3Exception {
		measurePositions = new HashMap<>();
		ArrayList<Measure> measures = null;
		int i=0;
		if (song.hasMeasures()) {
			for (Measure measure: song.getMeasuresSortedAsArray()) {
				measurePositions.put(measure, i++);
			}
			measures = song.getMeasuresSortedAsArray();
		}
		
		for (Staff staff: song.getStaves()) {
			TimeSignature ts = staff.getTimeSignatureWithOnset(Time.TIME_ZERO);
			if (ts instanceof TimeSignatureMensural) {
				staff.changeEmptyNotationType(NotationType.eMensural);
			} else {
				staff.changeEmptyNotationType(NotationType.eModern);
			}
			
			// check anacrusis
			if (measures != null && !measures.isEmpty()) {
				Time maxEndTime = Time.TIME_ZERO;
				for (Atom atom : staff.getAtomsWithOnsetWithin(measures.get(0))) {
					maxEndTime = Time.max(maxEndTime, atom.getOffset());
				}
				Time measureDuration = ts.getDuration();
				int diff = maxEndTime.compareTo(measureDuration);
				if (diff < 0) {
					song.setAnacrusisOffset(measureDuration.substract(maxEndTime));
				} //else if (diff > 0) { Not valid for multimeasure rests
				//throw new ImportException("Fist measure duration based on atom is " + maxEndTime + " and expected first measure duration based on time signature is " + measureDuration);
				//} // else normal measure
			}
		}
		 
		for (PendingConnectorOrMark pendingConnectorOrMark : pendingConnectorOrMarks) {
			Object fromElement;
			String fromStr, toStr;
			if (pendingConnectorOrMark.startid != null) {
				fromElement = findXMLID(pendingConnectorOrMark.startid);
				fromStr = pendingConnectorOrMark.startid;
			} else if (pendingConnectorOrMark.tstamp != null) {
				fromElement = getPlaceHolderFromTStamp(pendingConnectorOrMark.measure, pendingConnectorOrMark.tstamp, pendingConnectorOrMark.staff, pendingConnectorOrMark.layer);
                fromStr = pendingConnectorOrMark.tstamp;

                if (pendingConnectorOrMark.tag.equals("trill") || pendingConnectorOrMark.tag.equals("fermata")) {
                    // look for a single figure atom in the time stamp
                    Atom atom = pendingConnectorOrMark.staff.getAtomWithOnset(((ITimedSymbolWithConnectors)fromElement).getTime());
                    if (atom != null && !(atom instanceof SingleFigureAtom)) {
                        throw new ImportException("Cannot add a trill to other thing than SingleFigureAtom, it is " + atom.getClass());
                    }
                    fromElement = atom;
                }

			} else {
				throw new ImportException("Missing either startid or endif for connector " + pendingConnectorOrMark.tag);
			}
            Object toElement = null;
			if (!pendingConnectorOrMark.tag.equals("trill") && !pendingConnectorOrMark.tag.equals("fermata")) {
                if (pendingConnectorOrMark.endid != null) {
                    toElement = findXMLID(pendingConnectorOrMark.endid);
                    toStr = pendingConnectorOrMark.endid;
                } else if (pendingConnectorOrMark.tstamp2 != null) {
                    toElement = getPlaceHolderFromTStamp2(pendingConnectorOrMark.staff, pendingConnectorOrMark.layer, pendingConnectorOrMark.measure, measures, pendingConnectorOrMark.tstamp2);
                    toStr = pendingConnectorOrMark.tstamp2;
                } else {
                    throw new ImportException("Missing either endif for connector " + pendingConnectorOrMark.tag);
                }
            }

            ITimedSymbolWithConnectors from = null;
            ITimedSymbolWithConnectors to;
            PositionAboveBelow positionAboveBelow = PositionAboveBelow.UNDEFINED;
            if (pendingConnectorOrMark.tag.equals("trill") || pendingConnectorOrMark.tag.equals("fermata")) {
                if (!(fromElement instanceof SingleFigureAtom)) {
                    throw new ImportException("Cannot add a trill to other thing than SingleFigureAtom, and it is " + fromElement.getClass());
                }
                SingleFigureAtom sfa = (SingleFigureAtom) fromElement;
                from = sfa;
                if (pendingConnectorOrMark.staff == null) {
                    pendingConnectorOrMark.staff = sfa.getStaff();
                }

                if (pendingConnectorOrMark.content != null) {
                    switch (pendingConnectorOrMark.content) {
                        case "above":
                            positionAboveBelow = PositionAboveBelow.ABOVE;
                            break;
                        case "below":
                            positionAboveBelow = PositionAboveBelow.BELOW;
                            break;
                        default:
                            throw new ImportException("Unknown fermata position: '" + pendingConnectorOrMark.content + "'");
                    }
                }

            }

			switch (pendingConnectorOrMark.tag) {
			/*case "slur":
				case "phrase": // TODO - deberían ser semánticamente diferentes
					Slur slur = new Slur(fromElement, toElement);
					FRACCIONES try {
						currentScorePart.addConnector(slur);
					} catch (ImportException e) {
						e.printStackTrace();
						throw new ImportException("Duplicated slur from " + fromStr + " to " + toStr);
					}
					fromElement.addConnector(slur);
					toElement.addConnector(slur);
					break;*/
                case "fermata":
                    SingleFigureAtom sfa = (SingleFigureAtom) from;
                    pendingConnectorOrMark.staff.addFermata(sfa.getAtomFigure(), positionAboveBelow);
                    break;
                case "trill":
                    SingleFigureAtom sfa2 = (SingleFigureAtom) from;
                    Trill trill = new Trill(pendingConnectorOrMark.staff, positionAboveBelow, sfa2);
                    sfa2.addMark(trill); // TODO: 18/10/17 Normalizar dónde añadimos los objetos
                    lastStaff.addMark(trill);
                    break;
                case "slur":
                    if (fromElement instanceof ITimedSymbolWithConnectors) {
                        from = (ITimedSymbolWithConnectors) fromElement;
                    } else {
                        throw new ImportException("Unsupported slur from " + fromElement.getClass());
                    }
                    if (toElement instanceof ITimedSymbolWithConnectors) {
                        to = (ITimedSymbolWithConnectors) toElement;
                    } else {
                        throw new ImportException("Unsupported slur to " + fromElement.getClass()); // TODO: 1/10/17 Slurs desde cualquier cosa (startid...): StaffTimedPlaceHolder
                    }

                    Slur slur = new Slur(from, to);
                    from.addConnector(slur);
                    to.addConnector(slur);
                    break;
				case "tie":
					if (fromElement instanceof SimpleNote) {
						 from = ((SimpleNote) fromElement).getAtomPitch();
					} else if (!(fromElement instanceof AtomPitch)) { 
						throw new ImportException("Expected an AtomPitch and found " + fromElement.getClass());
					} else {
						from = (AtomPitch) fromElement;
					}
					if (toElement instanceof SimpleNote) {
						 to = ((SimpleNote) toElement).getAtomPitch();
					} else if (!(toElement instanceof AtomPitch)) { 
						throw new ImportException("Expected an AtomPitch and found " + fromElement.getClass());
					} else {
						to = (AtomPitch) toElement;
					}
					AtomPitch fromPitch=null, toPitch=null;
					if (from instanceof AtomPitch) {
					    fromPitch = (AtomPitch) from;
                    }
                    if (to instanceof AtomPitch) {
                        toPitch = (AtomPitch) to;
                    }

					if (fromPitch.getTiedToNext() == null) {
						fromPitch.setTiedToNext(toPitch);
						// TODO ¿Es necesaria la representación gráfica ahora del tie?
						/*AMTie tie = new AMTie(from); 
						tie.setTo(to);
						from.addConnector(tie);
						to.addConnector(tie);
						currentScorePart.addConnector(tie);*/
					} else if (fromPitch.getTiedToNext() != fromPitch) {
						throw new ImportException("The AtomPitch " + from + " already has a tie to other AtomPitch");
					}
					break;
				/*FRACCIONES case "hairpin":
					String form = pendingConnectorOrMark.content;
					AMHairpin hairpin;
					if (form.equals("cres")) {
						hairpin = new AMHairpinCrescendo();
					} else if (form.equals("dim")) {
						hairpin = new AMHairpinDiminuendo();
					} else {
						throw new ImportException("Invalid hairpin type: '" + form + "'");
					}
					hairpin.setFrom(fromElement);
					hairpin.setTo(toElement);
					fromElement.addConnector(hairpin);
					toElement.addConnector(hairpin);
					break;		*/			
			}
		}
	}

	protected ITimedSymbolWithConnectors getPlaceHolderFromTStamp2(Staff staff, ScoreLayer layer, Measure fromMeasure, List<Measure> measures, String tstamp2) throws ImportException, IM3Exception {
		String [] strings = tstamp2.split("m\\+");
		
		int nmeasure;
		double tstamp;
		if (strings.length == 1) {
			nmeasure = 0;
			tstamp = Double.parseDouble(strings[0])-1;
		} else if (strings.length == 2) {
			nmeasure = Integer.parseInt(strings[0]);
			tstamp = Double.parseDouble(strings[1])-1;
		} else {
			throw new ImportException("Expected format <number>m+<number> and found: " + tstamp2);
		}
		Integer measurePosition = measurePositions.get(fromMeasure);
		if (measurePosition == null) {
			throw new ImportException("From measure " + fromMeasure + " not found in measurePositions");
		}
		Measure destMeasure = measures.get(measurePosition + nmeasure); 
		Time time = destMeasure.getTime().add(new Time(Fraction.getFraction(tstamp)));
		return getOrCreatePlaceHolder(time, staff, layer);
	}

	protected ITimedSymbolWithConnectors getPlaceHolderFromTStamp(Measure measure, String tstamp, Staff staff, ScoreLayer layer) throws ImportException, IM3Exception {
		Time ts = decodeTStamp(measure, tstamp);
		return getOrCreatePlaceHolder(ts, staff, layer); 
	}


	protected ITimedSymbolWithConnectors getOrCreatePlaceHolder(Time ts, Staff staff, ScoreLayer layer) throws IM3Exception {
		StaffTimedPlaceHolder placeHolder =  placeHolders.get(ts);
		if (placeHolder == null) {
			//placeHolder = new StaffTimedPlaceHolder(currentScorePart.getElements().size(), ts);
			placeHolder = new StaffTimedPlaceHolder(staff, ts);
			//FRACCIONES placeHolder.setLayer(layer);
			staff.addCoreSymbol(placeHolder);
			//placeHolder.setStaff(staff);
			//currentScorePart.addElement(placeHolder);
		}
		return placeHolder;
	}


	public Object findXMLID(String id) throws ImportException {
		if (id.startsWith("#")) {
			id = id.substring(1);
		}
		Object result = xmlIDs.get(id);
		if (result == null) {
			throw new ImportException("No element with xml:id='" + id + "' found");
		}
		return result;
	}
	
}
