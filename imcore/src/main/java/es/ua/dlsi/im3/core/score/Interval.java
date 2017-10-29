package es.ua.dlsi.im3.core.score;

//import es.ua.dlsi.im3.model.score.cmn.CMNNote;
import java.util.Objects;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;

public class Interval implements Comparable<Interval> {

	/**
	 * 1st = 1, 2nd = 2, 3rd = 3, 8 ... The tempo 0 means empty
	 *
	 */
	protected int simpleName = 0;
	/**
	 * It may be a 13th
	 *
	 */
	protected int name = 0;

	/**
	 * Major, minor, diminished, augmented
	 */
	protected IntervalMode mode;
	/**
	 * Direction
	 */
	protected MotionDirection direction;

	protected int semitones;

	/**
	 * For the compound intervals. The 13th will have name=5 and
	 * octave=additionalOctaves
	 */
	protected int additionalOctaves;

	/**
	 * See http://wiki.ccarh.org/wiki/Base_40
	 */
	protected int base40Difference;

	/**
	 * @param name
	 * @param mode
	 * @param direction
	 * @throws IM3Exception
	 */
	public Interval(int name, IntervalMode mode, MotionDirection direction) throws IM3Exception {
		super();
		this.mode = mode;
		this.direction = direction;
		setName(name);
	}

	protected Interval() {
	}

	/**
	 * If this is ascending, generate the same desdending
	 */
	public Interval createContraryInterval() throws IM3Exception {
		if (direction.equals(MotionDirection.ASCENDING)) {
			return new Interval(name, mode, MotionDirection.DESCENDING);
		} else if (direction.equals(MotionDirection.DESCENDING)) {
			return new Interval(name, mode, MotionDirection.ASCENDING);
		} else {
			return new Interval(name, mode, direction);
		}
	}

	private Interval(Intervals itv, int additionalOctaves) throws IM3Exception {
		this.name = itv.getName() + (7 * additionalOctaves);
		this.simpleName = itv.getName();
		this.direction = itv.getDirection();
		this.mode = itv.getMode();
		this.semitones = itv.getSemitones() + additionalOctaves * 12;
		base40Difference = itv.getBase40Difference();
		this.additionalOctaves = additionalOctaves;
	}

	/**
	 * Package visibility, used by Intervals enum
	 *
	 * @param name2
	 * @param mode2
	 * @param direction2
	 * @param semitones2
	 */
	Interval(int name2, IntervalMode mode2, MotionDirection direction2, int semitones2, int base40Difference) {
		if (name2 < 1 || name2 >= 8) {
			throw new IM3RuntimeException("Invalid simple interval name: " + name2);
		}
		this.simpleName = name2;
		this.name = name2;
		this.mode = mode2;
		this.direction = direction2;
		this.semitones = semitones2;
		this.base40Difference = base40Difference;
	}

	public int getAdditionalOctaves() {
		return additionalOctaves;
	}

	public void setAdditionalOctaves(int newAdditionalOctaves) {
		int semitonesChange = newAdditionalOctaves*12 - this.additionalOctaves*12;
		
		this.additionalOctaves = newAdditionalOctaves;
		this.name = simpleName + (7 * additionalOctaves);
		this.semitones += semitonesChange;
	}

	public boolean isCompound() {
		return additionalOctaves >= 1;
	}

	/**
	 * @return the name (1 = 1st, 2 = 2nd...)
	 */
	public final int getName() {
		return name;
	}

	public int getSimpleName() {
		return simpleName;
	}

	/**
	 * @param name
	 *            the name to set
	 * @throws IM3Exception
	 */
	public final void setName(int name) throws IM3Exception {
		this.name = name;
		if (name < 8) {
			simpleName = name;
		} else {
			simpleName = name % 8 + 1;
			additionalOctaves = name / 8;
		}

		if (this.name == 1 && this.mode == IntervalMode.PERFECT) {
			this.direction = MotionDirection.EQUAL;
		}
		boolean found = false;
		for (int i = 0; !found && i < Intervals.values().length; i++) {
			Intervals itv = Intervals.values()[i];
			if (simpleName == itv.getName() && mode.equals(itv.getMode()) && direction.equals(itv.getDirection())) {
				found = true;
				this.semitones = itv.getSemitones() + additionalOctaves * 12;
				base40Difference = itv.getBase40Difference();
			}
		}
		if (!found) {
			throw new IM3Exception("Cannot find semitones for interval " + this.toString()); // TODO
																								// Intervalos
																								// compuestos
		}
	}

	/**
	 * @return the mode
	 */
	public final IntervalMode getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public final void setMode(IntervalMode mode) {
		this.mode = mode;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public final void setDirection(MotionDirection direction) {
		this.direction = direction;
	}

	/**
	 * @return the direction
	 */
	public final MotionDirection getDirection() {
		return direction;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 47 * hash + this.name;
		hash = 47 * hash + Objects.hashCode(this.mode);
		hash = 47 * hash + Objects.hashCode(this.direction);
		return hash;
	}

	public boolean equalsWithoutDirection(Intervals itvs) {
		return itvs.getMode() == this.mode && itvs.getName() == this.name;
	}

	/**
	 * 
	 * @param obj
	 *            Interval, VagueInterval
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Interval) {
			return compareTo((Interval) obj) == 0;
		} else if (obj instanceof VagueInterval) {
			boolean result = true;
			VagueInterval vi = (VagueInterval) obj;
			if (vi.getName() != 0) {
				result = result && this.name == vi.getName();
			}
			if (vi.getDirection() != null) {
				result = result && this.direction.equals(vi.getDirection());
			}
			if (vi.getMode() != null) {
				result = result && this.mode.equals(vi.getMode());
			}

			return result;
		} else if (obj instanceof Intervals) {
			Intervals itvs = (Intervals) obj;
			return itvs.getDirection() == this.direction && itvs.getMode() == this.mode && itvs.getName() == this.name;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}
		final Interval other = (Interval) obj;
		if (this.name != other.name) {
			return false;
		}
		if (this.mode != other.mode) {
			return false;
		}
        return this.direction == other.direction;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/*
	 * @Override public boolean equals(Object obj) { if (obj instanceof
	 * Interval) { return compareTo((Interval) obj)==0; } else if (obj
	 * instanceof VagueInterval) { boolean result = true; VagueInterval vi =
	 * (VagueInterval) obj; if (vi.getName() != 0) { result = result &&
	 * this.name == vi.getName(); } if (vi.getDirection() != null) { result =
	 * result && this.direction.equals(vi.getDirection()); } if (vi.getMode() !=
	 * null) { result = result && this.mode.equals(vi.getMode()); }
	 * 
	 * return result; } else { return false; } } //TODO Revisar esto
	 * 
	 * @Override public int compareTo(Interval o) { int dname = name - o.name;
	 * if (dname == 0) { int dmode = mode.compareTo(o.mode); if (dmode == 0) {
	 * return direction.compareTo(o.direction); } else { return dmode; } } else
	 * { return dname; } }
	 */
	/**
	 * Computes the interval. See compute(from,to,forcedDirection) also
	 *
	 * @param from
	 * @param to
	 * @return
	 * @throws IM3Exception
	 */
	/*
	 * public static Interval compute(PitchClass from, PitchClass to) throws
	 * IM3Exception { int order = from.getNoteName().getHorizontalOrderInStaff(); int toorder =
	 * to.getNoteName().getHorizontalOrderInStaff(); int name, sts; MotionDirection direction; if
	 * (order == toorder) { name = toorder - order; sts = to.getSemitonesFromC()
	 * - from.getSemitonesFromC(); if (sts == 0) { direction =
	 * MotionDirection.EQUAL; } else if (sts < 0) { direction =
	 * MotionDirection.DESCENDING; } else { direction =
	 * MotionDirection.ASCENDING; } } else if (order < toorder) { name = toorder
	 * - order; sts = to.getSemitonesFromC() - from.getSemitonesFromC();
	 * direction = MotionDirection.ASCENDING; } else { name = order - toorder;
	 * sts = to.getSemitonesFromC() - from.getSemitonesFromC(); direction =
	 * MotionDirection.DESCENDING; } // +1 because 1 = unison, not 0 return
	 * Intervals.getInterval(name+1, sts, direction); }
	 */
	/**
	 * Only look in the given direction. Using compute without direction obtains
	 * a 6th descending from A to C. If we use direction, this interval would be
	 * ascending 3rd
	 *
	 * @param from
	 * @param to
	 * @param forcedDirection
	 * @return
	 * @throws IM3Exception
	 */

	/*
	 * 2014 public static Interval compute(PitchClass from, PitchClass to,
	 * MotionDirection forcedDirection) throws IM3Exception { if (from.isRest())
	 * { throw new IM3Exception("Cannot compute the interval from a rest"); } if
	 * (to.isRest()) { throw new IM3Exception(
	 * "Cannot compute the interval to a rest"); }
	 * 
	 * int order = from.getNoteName().getHorizontalOrderInStaff(); int toorder =
	 * to.getNoteName().getHorizontalOrderInStaff(); int name, sts; MotionDirection direction; if
	 * (order == toorder) { name = toorder - order; sts = to.getSemitonesFromC()
	 * - from.getSemitonesFromC(); if (sts == 0) { direction =
	 * MotionDirection.EQUAL; } else if (sts < 0) { direction =
	 * MotionDirection.DESCENDING; } else { direction =
	 * MotionDirection.ASCENDING; } } else if (order < toorder) { name =
	 * (toorder - order) % 8; sts = to.getSemitonesFromC() -
	 * from.getSemitonesFromC(); direction = MotionDirection.ASCENDING; } else {
	 * name = (order - toorder) % 8; sts = to.getSemitonesFromC() -
	 * from.getSemitonesFromC(); direction = MotionDirection.DESCENDING; }
	 * 
	 * // +1 because 1 = unison, not 0 //System.out.println(from + " > " + to);
	 * Interval result = Intervals.getInterval(name+1, sts, direction);
	 * 
	 * if (forcedDirection != direction) { result.invert(); } if (result.name ==
	 * 8) { result.name = 1; }
	 * 
	 * return result; }
	 */
	/**
	 * It uses the http://wiki.ccarh.org/wiki/Base_40 encoding to compute the
	 * interval The span of calculation is an octave. e.g: from=C, to=A, result
	 * = 6th ascending. If from=A, to=C, result = 6th descending
	 *
	 * @param from
	 * @param to
	 * @return
	 * @throws IM3Exception
	 */
	public static Interval compute(PitchClass from, PitchClass to) throws IM3Exception {
		int diff = to.getBase40Chroma() - from.getBase40Chroma();
		MotionDirection direction;
		if (diff > 0) {
			direction = MotionDirection.ASCENDING;
		} else if (diff == 0) {
			direction = MotionDirection.EQUAL;
		} else {
			direction = MotionDirection.DESCENDING;
		}

		// for octaves
		int sumoctave = 0;
		if (diff >= 38) {
			diff -= 40;
			sumoctave = 1;
		} else if (diff <= -38) {
			diff += 40;
			sumoctave = 1;
		}

		for (Intervals itv : Intervals.values()) {
			if (itv.getDirection() == direction && diff == itv.getBase40Difference()) {
				// da return new Interval(itv.getName()+sumoctave,
				// itv.getMode(), direction);
				return new Interval(itv, sumoctave);
			}
		}

		throw new IM3Exception("Cannot compute the interval from " + from.toString() + " to " + to.toString());
	}

	/**
	 * It uses the http://wiki.ccarh.org/wiki/Base_40 encoding to compute the
	 * interval
	 *
	 * @param from
	 * @param to
	 * @param forceDirection
	 *            Forces the interval to be computed using this interval
	 *            direction: e.g. from = A, to=C, direction=ascending,
	 *            result=3rd ascending. It the interval is a 1P, the direction
	 *            is set to EQUAL
	 * @return
	 * @throws IM3Exception
	 */
	public static Interval compute(PitchClass from, PitchClass to, MotionDirection forceDirection) throws IM3Exception {
		int diff = to.getBase40Chroma() - from.getBase40Chroma();

		if (diff > 0 && forceDirection == MotionDirection.DESCENDING) {
			diff -= 40; // a octave below
		} else if (diff < 0 && forceDirection == MotionDirection.ASCENDING) {
			diff += 40; // a octave below
		} else if (diff == 0) {
			forceDirection = MotionDirection.EQUAL;
		}

		// for octaves
		int sumoctave = 0;
		if (diff >= 38) {
			diff -= 40;
			sumoctave = 1;
		} else if (diff <= -38) {
			diff += 40;
			sumoctave = 1;
		}
		for (Intervals itv : Intervals.values()) {
			if (itv.getDirection() == forceDirection && diff == itv.getBase40Difference()) {
				// da return new Interval(itv.getName()+sumoctave,
				// itv.getMode(), forceDirection);
				return new Interval(itv, sumoctave);
			}
		}

		throw new IM3Exception(
				"Cannot compute the " + forceDirection + " interval from " + from.toString() + " to " + to.toString());
	}

	/**
	 * It uses the http://wiki.ccarh.org/wiki/Base_40 encoding to compute the
	 * interval
	 *
	 * @param from
	 * @param to
	 * @return
	 * @throws IM3Exception
	 */
	public static Interval compute(ScientificPitch from, ScientificPitch to) throws IM3Exception {
		int diff = to.getBase40() - from.getBase40();
		int diffInOctave = diff % 40;
		int octaves = diff / 40;
		MotionDirection direction;
		if (diffInOctave > 0) {
			direction = MotionDirection.ASCENDING;
		} else if (diffInOctave == 0) {
			direction = MotionDirection.EQUAL;
		} else {
			direction = MotionDirection.DESCENDING;
		}

		for (Intervals itv : Intervals.values()) {
			if (itv.getDirection() == direction && diffInOctave == itv.getBase40Difference()) {
				// da return new Interval(itv.getName() + octaves*8,
				// itv.getMode(), direction);
				return new Interval(itv, octaves);
			}
		}

		throw new IM3Exception("Cannot compute the interval from " + from.toString() + " to  " + to.toString());
	}

	// TODO No trata acordes
	/**
	 * Calculate the interval. Currently, it does not manage well chords
	 *
	 * @param from
	 * @param to
	 * @return null if the interval cannot be computed (from | to) (null | rest)
	 * @throws IM3Exception
	 */
	/*
	 * public static Interval compute(CMNNote from, CMNNote to) throws
	 * IM3Exception { if (from == null || from.isRest() || to == null ||
	 * to.isRest()) { return null; } else { //TODO Hacer tests unitarios -
	 * Comprobar Placido int midiFrom = from.getMidiPitch(); int midiTo =
	 * to.getMidiPitch(); int ordering = midiFrom - midiTo; MotionDirection
	 * direction; if (ordering < 0) { direction = MotionDirection.ASCENDING; }
	 * else if (ordering > 0) { direction = MotionDirection.DESCENDING; } else {
	 * direction = MotionDirection.EQUAL; }
	 * 
	 * Interval itv = compute(from.getPitchClass(), to.getPitchClass());
	 * 
	 * if (itv.getDirection() != direction) { // get the complementary
	 * itv.invert(); }
	 * 
	 * return itv;
	 * 
	 * // if not found, throw an exception //throw new IM3Exception(
	 * "Interval not found for notes " + from.toMinString() + " to " +
	 * to.toMinString() +", interval name=" + name + " with " + semitones +
	 * " semitones"); } }
	 */
	public void invert() {
		this.name = 9 - this.name;
		this.direction = MotionDirection.invert(this.direction);
		this.mode = IntervalMode.complementary(this.mode);

	}

	public int getSemitones() {
		return semitones;
	}

	public void setSemitones(int semitones) {
		this.semitones = semitones;
	}

	/*
	 * void setShortName(String shortName) { this.shortName = shortName; }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name + "_" + this.mode + "_" + this.direction;
	}

	/**
	 * Get a pitch class given the interval
	 *
	 * @param from
	 * @return
	 * @throws IM3Exception
	 */
	public PitchClass computePitchClassFrom(PitchClass from) throws IM3Exception {
		int newPCBase40 = (40 + (from.getBase40Chroma() + base40Difference + 40 * additionalOctaves)) % 40; // sum
																											// 40
																											// to
																											// avoid
																											// negatives

		for (PitchClasses pc : PitchClasses.values()) {
			if (newPCBase40 == pc.getBase40ChromaValue()) {
				return pc.getPitchClass();
			}
		}

		throw new IM3Exception("Cannot compute the new pitch class from " + from + " for interval " + this);
	}

	/**
	 * Get a pitch class given the interval
	 *
	 * @param from
	 * @return
	 * @throws IM3Exception
	 */
	public ScientificPitch computeScientificPitchFrom(ScientificPitch from) throws IM3Exception {
		int newPCBase40 = (from.getBase40() + base40Difference + 40 * additionalOctaves);

		int newPCBase40_folded = newPCBase40 % 40;
		int octave = newPCBase40 / 40;

		for (PitchClasses pc : PitchClasses.values()) {
			if (newPCBase40_folded == pc.getBase40ChromaValue()) {
				return new ScientificPitch(pc.getPitchClass(), octave);
			}
		}

		throw new IM3Exception("Cannot compute the new scientific pitch from " + from + " for interval " + this);
	}

	// TODO Código casi repetido con computeScientificPitchFrom
	/**
	 * Get a pitch class given the interval
	 *
	 * @param from
	 * @return
	 * @throws IM3Exception
	 */
	/*
	 * 2014 public PitchClass computePitchClassFrom(PitchClass from) throws
	 * IM3Exception { DiatonicPitch dest; Accidentals acc; int octaveOffset = 0; int
	 * diffsemitones;
	 * 
	 * switch (direction) { case EQUAL: case ASCENDING: dest =
	 * DiatonicPitch.noteFromOrder((from.getNoteName().getHorizontalOrderInStaff() + (this.getName()
	 * - 1)) % 7); // -1 because unison = 1 if (dest.getHorizontalOrderInStaff() <
	 * from.getNoteName().getHorizontalOrderInStaff()) { octaveOffset = 12; }
	 * 
	 * diffsemitones = octaveOffset + dest.getSemitonesFromC() -
	 * from.getSemitonesFromC(); acc =
	 * Accidentals.accidentalForAlter((this.semitones - diffsemitones) % 12);
	 * return new PitchClass(dest, acc); case DESCENDING: dest =
	 * DiatonicPitch.noteFromOrder((7 + from.getNoteName().getHorizontalOrderInStaff() -
	 * (this.getName() -1)) % 7); if (dest.getHorizontalOrderInStaff() >
	 * from.getNoteName().getHorizontalOrderInStaff()) { octaveOffset = -12; }
	 * 
	 * diffsemitones = octaveOffset + dest.getSemitonesFromC() -
	 * from.getSemitonesFromC(); acc =
	 * Accidentals.accidentalForAlter(this.semitones - diffsemitones); return
	 * new PitchClass(dest, acc); default: throw new IM3Exception(
	 * "Unknown interval direction: " + direction); } }
	 */
	// TODO Código casi repetido con computeScientificPitchFrom
	/**
	 * Get a scientific pitch given the interval
	 *
	 * @param from
	 * @return
	 * @throws IM3Exception
	 */
	/*
	 * 2014 public ScientificPitch computeScientificPitchFrom(ScientificPitch
	 * from) throws IM3Exception { DiatonicPitch dest; Accidentals acc; int
	 * octaveOffset = 0; int diffsemitones; int octave = from.getOctave();
	 * switch (direction) { case EQUAL: case ASCENDING: dest =
	 * DiatonicPitch.noteFromOrder((from.getPitchClass().getNoteName().getHorizontalOrderInStaff() +
	 * (this.getName() - 1)) % 7); // -1 because unison = 1 if (dest.getHorizontalOrderInStaff()
	 * < from.getPitchClass().getNoteName().getHorizontalOrderInStaff()) { octaveOffset = 12;
	 * octave+=1; }
	 * 
	 * diffsemitones = octaveOffset + dest.getSemitonesFromC() -
	 * from.getPitchClass().getSemitonesFromC(); acc =
	 * Accidentals.accidentalForAlter((this.semitones - diffsemitones) % 12);
	 * return new ScientificPitch(new PitchClass(dest, acc), octave); case
	 * DESCENDING: dest = DiatonicPitch.noteFromOrder((7 +
	 * from.getPitchClass().getNoteName().getHorizontalOrderInStaff() - (this.getName() -1)) %
	 * 7); if (dest.getHorizontalOrderInStaff() > from.getPitchClass().getNoteName().getHorizontalOrderInStaff())
	 * { octaveOffset = -12; octave-=1; }
	 * 
	 * diffsemitones = octaveOffset + dest.getSemitonesFromC() -
	 * from.getPitchClass().getSemitonesFromC(); acc =
	 * Accidentals.accidentalForAlter(this.semitones - diffsemitones); return
	 * new ScientificPitch(new PitchClass(dest, acc), octave); default: throw
	 * new IM3Exception("Unknown interval direction: " + direction); } }
	 */
	/*
	 * public static int calculateIntervalName(int fromPitch, int toPitch)
	 * throws Exception { int result = (toPitch - fromPitch) %7 + 1; if (result
	 * <= 0) { throw new Exception("Interval name <=0"); } return result; }
	 * 
	 * 
	 * public static Interval newAscendingIntervalBetween(PlayedScoreNote
	 * previousNote, PlayedScoreNote note) throws Exception { int prevOctave =
	 * previousNote.getOctave(); while (previousNote.getAbsPitch() >
	 * note.getAbsPitch()) { previousNote.setOctave(previousNote.getOctave()-1);
	 * } Interval result = newIntervalBetween(previousNote, note);
	 * previousNote.setOctave(prevOctave); // leave as it were before return
	 * result; }
	 * 
	 * public static Interval newIntervalBetween(PlayedScoreNote previousNote,
	 * PlayedScoreNote note) throws Exception { final int [] MAJOR_SEMITONES =
	 * {0,2,4,5,7,9,11};//TODO Ver PlayedScoreNote.getMIDI
	 * 
	 * int pitchName = note.getPitchName()-1 + note.getOctave()*7; int pitch =
	 * MAJOR_SEMITONES[note.getPitchName()-1] + note.getOctave()*12; int
	 * absPitch = pitch + note.getAlter(); int lastPitchName =
	 * previousNote.getPitchName()-1 + previousNote.getOctave()*7; int lastPitch
	 * = MAJOR_SEMITONES[previousNote.getPitchName()-1] +
	 * previousNote.getOctave()*12; int lastAbsPitch = lastPitch +
	 * previousNote.getAlter(); boolean isAscending = pitch >= lastPitch; int
	 * intervalName; int steps = 0; // semitonos
	 * 
	 * if (isAscending) { intervalName = calculateIntervalName(lastPitchName,
	 * pitchName); steps = (absPitch - lastAbsPitch) % 12; // URGENT Revisar con
	 * Placido } else { intervalName = calculateIntervalName(pitchName,
	 * lastPitchName); steps = (lastAbsPitch - absPitch) % 12; // URGENT Revisar
	 * con Placido }
	 * 
	 * int difference = (steps - MAJOR_SEMITONES[intervalName - 1]) % 12;
	 * 
	 * char modifier; switch (difference) { case 0: if (intervalName == 1 ||
	 * intervalName == 4 || intervalName == 5) { modifier = PERFECT; } else {
	 * modifier = MAJOR; } break; case 1: modifier = AUGMENTED;break; case 2:
	 * modifier = DOUBLE_AUGMENTED;break; case -1: if (intervalName == 4 ||
	 * intervalName == 5) { modifier = DIMINISHED; } else { modifier = MINOR; }
	 * break; case -2: if (intervalName == 4 || intervalName == 5) { modifier =
	 * DOUBLE_DIMINISHED; } else { modifier = DIMINISHED; } break; default:
	 * throw new Exception("Cannot calculate this modifier, difference = " +
	 * difference + " from note " + previousNote + " to note " + note); } char
	 * dir; if (intervalName == 1 && modifier == PERFECT) { dir = 'S'; } else {
	 * dir = isAscending?'A':'D'; } Interval interval = new
	 * Interval(intervalName, modifier, dir); //System.out.println("Interval: "
	 * + interval.toString()); return interval; } public String toString() {
	 * StringBuffer sb = new StringBuffer(); if (name != -0) { sb.append(name);
	 * } if (modifier != NO_MODIFIER) { if (this.name == 1 && this.modifier ==
	 * 'M') { sb.append(PERFECT); } else { sb.append(modifier); }
	 * sb.append(','); } if (this.name == 1 && (this.modifier == 'M' ||
	 * this.modifier == 'P')) { sb.append("same"); } else if (direction == 'A')
	 * { sb.append("asc"); } else if (direction == 'D') { sb.append("desc"); }
	 * else if (direction == 'S') { sb.append("same"); } else if (direction ==
	 * NO_DIRECTION) { sb.append("no"); } return sb.toString(); }
	 */
	/**
	 * It returns true if all the not null values are equal. E.g, if the other
	 * has not tempo for the modifier field, it is not taken into account for
	 * the comparison
	 */
	/*
	 * public boolean equals(Object other) { Interval iother = (Interval) other;
	 * if (other == null) { return false; } return (name == NO_NAME ||
	 * iother.name == NO_NAME || name == iother.name) && (modifier ==
	 * NO_MODIFIER || iother.modifier == NO_MODIFIER || modifier ==
	 * iother.modifier) && (direction == NO_DIRECTION || iother.direction ==
	 * NO_DIRECTION || direction == iother.direction); }
	 */
	/**
	 * Major / perfect pitch distances. The index = interval name. The tempo for
	 * the 0 cell is undefined.
	 */
	// private static final int [] PITCH_DISTANCES = {0, 0, 2, 4, 5, 7, 9, 11};
	// TODO Probar
	/**
	 * Distance in terms of absolute pitch distance
	 *
	 * @return
	 */

	/*
	 * public int getPitchDistance() { int result = PITCH_DISTANCES[this.name];
	 * if (this.name == 4 || this.name == 5) { switch (modifier) { case
	 * DIMINISHED: result--; break; case DOUBLE_DIMINISHED: result-=2; break;
	 * case AUGMENTED: result++; break; case DOUBLE_AUGMENTED: result+=2; break;
	 * } } else { switch (modifier) { case MINOR: result--; break; case
	 * DIMINISHED: result-=2; break; case DOUBLE_DIMINISHED: result-=3; break;
	 * case AUGMENTED: result++; break; case DOUBLE_AUGMENTED: result+=2; break;
	 * }
	 * 
	 * } return 0; }
	 */
	/**
	 * Add an interval
	 *
	 * @param itv
	 * @return
	 * @throws IM3Exception
	 */
	public Interval addInterval(Interval itv) throws IM3Exception {
		if (this.direction != itv.getDirection()) {
			throw new IM3Exception("Cannot add two intervals with different direction");
		}
		int newname = this.name + itv.name - 1;
		int newsemis = this.semitones + itv.semitones;
		return Intervals.getInterval(newname, newsemis, this.direction);
	}

	@Override
	public int compareTo(Interval o) {
		return base40Difference - o.base40Difference;
	}

	/**
	 * 
	 * @return true for 1P, 3m, 3M, 5P, 6m, 6M, 8
	 */
	public boolean isConsonant() {
		return equalsWithoutDirection(Intervals.UNISON_PERFECT) || equalsWithoutDirection(Intervals.THIRD_MINOR_DESC)
				|| equalsWithoutDirection(Intervals.THIRD_MAJOR_DESC)
				|| equalsWithoutDirection(Intervals.FIFTH_PERFECT_DESC)
				|| equalsWithoutDirection(Intervals.SIXTH_MINOR_ASC)
				|| equalsWithoutDirection(Intervals.SIXTH_MAJOR_ASC);

	}
}
