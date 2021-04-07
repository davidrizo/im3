package es.ua.dlsi.im3.core.score.io;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.Measure;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.clefs.*;
import es.ua.dlsi.im3.core.score.mensural.meters.*;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMayor;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import es.ua.dlsi.im3.core.io.ImportException;

/**
 * Common methods for importers
 * @author drizo
 *
 */
public class ImportFactories {
	public static Clef createClef(NotationType notationType, String shape, int line, Integer octaveChange)
			throws ImportException {
		Clef result;
		switch (shape) {
		case "G":
            if (line == 3) {
                result = new ClefG3(); // artificial, used for experiments
            } else if (line == 1) {
				result = new ClefG1();
			} else if (line != 2) {
				throw new ImportException("Invalid line " + line + " for G clef");
			} else {
                if (octaveChange != null) {
                    switch (octaveChange) {
                        case -2:
                            result = new ClefG2QuindicesimaBassa();
                            break;
                        case -1:
                            result = new ClefG2OttavaBassa();
                            break;
                        case 0:
                            result = new ClefG2();
                            break;
                        case 2:
                            result = new ClefG2QuindicesimaAlta();
                            break;
                        case 1:
                            result = new ClefG2OttavaAlta();
                            break;
                        default:
                            throw new ImportException("Invalid octave change " + octaveChange + " for G clef");
                    }
                } else {
                    // if no clef octave change
                    result = new ClefG2();
                }
            }
			break;
		case "F":
			switch (line) {
			case 3:
				result = new ClefF3();
				break;
			case 4:
				if (octaveChange != null) {
					switch (octaveChange) {
					case -2:
						result = new ClefF4QuindicesimaBassa();
						break;
					case -1:
						result = new ClefF4OttavaBassa();
						break;
					case 0:
						result = new ClefF4();
						break;
					case 2:
						result = new ClefF4QuindicesimaAlta();
						break;
					case 1:
						result = new ClefF4OttavaAlta();
						break;
					default:
						throw new ImportException("Invalid octave change " + octaveChange + " for F clef");
					}
				} else {
					// if no clef octave change
					result = new ClefF4();
				}
				break;
			case 5:
				result = new ClefF5();
				break;
			default:
				throw new ImportException("Invalid line " + line + " for F clef");
			}
			break;
		case "C":
			switch (line) {
			case 1:
				result = new ClefC1();
				break;
			case 2:
				result = new ClefC2();
				break;
			case 3:
				result = new ClefC3();
				break;
			case 4:
				result = new ClefC4();
				break;
			case 5:
				result = new ClefC5();
				break;
			default:
				throw new ImportException("Invalid line " + line + " for C clef");
			}
			break;
		case "percussion":
			result = new ClefPercussion();
			break;
		case "tab":
			result = new ClefTab();
			break;
		case "none":
			result = new ClefEmpty();
			break;
		default:
			throw new ImportException("Unknown clef pitch: " + shape);
		}
		result.setNotationType(notationType);
		return result;
	}
	
	/*
	private void createTimeSignature() throws ImportException, IM3Exception {
		Meter meter = null;
		
		if (meterSign != null) {
			switch (meterSign) {
				case "common": 
					meter = new MeterCommonTime();
					break;
				case "cut":
					meter = new MeterCutTime();
					break;
				default:
					throw new ImportException("Unknown symbol type for meter: '" + meterSign + "'");
			}
		} else {
			if (meterBeats == null || meterBeatType == null) {
				throw new ImportException("Missing beats ( "+ meterBeats + ") or beat type ( " + meterBeatType + ") in meter");
			}
			meter = new FractionalMeter(Integer.parseInt(meterBeats), Integer.parseInt(meterBeatType)); 
		}	
		lastMeter = meter;
	}*/

	public static TimeSignature processMeter(String meterSym, String meterCount, 
			String meterUnit, NotationType notationType) throws ImportException, IM3Exception {
		TimeSignature ts = null;
		
		if (meterSym != null) {
			switch (meterSym) {
				case "common":
				case "ct":
				case "Ct":
					if (notationType == NotationType.eModern) {
						ts = new TimeSignatureCommonTime();
					} else if (notationType == NotationType.eMensural) {
						ts = new TempusImperfectumCumProlationeImperfecta();
					} else {
						throw new ImportException("Unsupported notation type: " + notationType);
					}
					break;
				case "cut":
				case "Ccut":
					if (notationType == NotationType.eModern) {
						ts = new TimeSignatureCutTime();
					} else if (notationType == NotationType.eMensural) {
						ts = new TempusImperfectumCumProlationeImperfectaDiminutum();
					} else {
						throw new ImportException("Unsupported notation type: " + notationType);
					}
					break;
				case "CZ":
				case "cz": //TODO David
					ts = new TimeSignatureProporcionMenor();
					break;
				case "CcutZ": //TODO David
					ts = new TimeSignatureProporcionMayor();
					break;
				case "O":
					ts = new TempusPerfectumCumProlationeImperfecta();
					break;
				case "O.":
				case "Odot":
					ts = new TempusPerfectumCumProlationePerfecta();
					break;
				case "C.":
				case "Cdot":
					ts = new TempusImperfectumCumProlationePerfecta();
					break;
				default:
					throw new ImportException("Unknown symbol type for meter: '" + meterSym + "'");
			}
		} else {
			if (meterCount == null || meterUnit == null) {
				throw new ImportException("Missing beats or beat type in non sign or mensural num meter");
			}
			ts = new FractionalTimeSignature(Integer.parseInt(meterCount), Integer.parseInt(meterUnit)); 
		}
		return ts;
	}

	public static Measure processMeasure(ScoreSong song, Time currentMeasureTime, String currentMeasureNumber) throws ImportException, IM3Exception {
		Measure prevMeasure = song.getMeasureWithOnset(currentMeasureTime);
		Measure currentMeasure;
		if (prevMeasure != null) {
			if (currentMeasureNumber != null && prevMeasure.getNumber() != Integer.parseInt(currentMeasureNumber)) {
				throw new ImportException("Two measures at same time (" + currentMeasureTime + ") with different number: " + currentMeasureNumber + " and " + prevMeasure.getNumber());
			}	
			currentMeasure = prevMeasure;
		} else {
			currentMeasure = new Measure(song);
			if (currentMeasureNumber != null) {
				currentMeasure.setNumber(Integer.parseInt(currentMeasureNumber));
			}
			song.addMeasure(currentMeasureTime, currentMeasure);
		}	
		//currentMeasure.addStaffOrGroup(staff); //TODO Groups
		return currentMeasure;
	}
}
