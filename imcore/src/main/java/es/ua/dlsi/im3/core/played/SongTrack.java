package es.ua.dlsi.im3.core.played;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;

/**
 * @author drizo
 * @date 29/02/2012
 *
 */
public class SongTrack {

	/**
	 * Order in its parent
	 */
	int number;
	/**
	 * Name of the part
	 */
	String name;

	/**
	 * Notes in the track
	 */
	protected TreeSet<PlayedNote> playedNotes;

	private int defaultMIDIChannel;

	private final PlayedSong playedSong;

	public SongTrack(PlayedSong playedSong, int number) {
		this.playedSong = playedSong;
		this.number = number;
		playedNotes = new TreeSet<>();
		defaultMIDIChannel = 0;

	}

	/**
	 * It adds the note
	 *
	 * @param time
	 * @param note
	 */
	public void addNote(long time, PlayedNote note) {
		note.setTime(time);
		playedNotes.add(note);
	}

	public int getNumNotes() {
		return playedNotes.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	@Override
	public String toString() {
		return "SongTrack [name=" + name + " " + playedNotes + "]";
	}

	/**
	 * This is a slow method
	 *
	 * @param note
	 */
	public void removeNote(PlayedNote note) {
		this.playedNotes.remove(note);
	}

	/**
	 * @param midiChannel
	 *            1..16
	 */
	public void setDefaultMidiChannel(int midiChannel) {
		this.defaultMIDIChannel = midiChannel;

	}

	/**
	 *
	 * @return 1..16
	 */
	public int getDefaultMidiChannel() {
		return this.defaultMIDIChannel;
	}

	public boolean isDefaultMidiChannelSet() {
		return defaultMIDIChannel != 0;
	}

	public long computePlayedDuration() {
		if (playedNotes.isEmpty()) {
			return 0;
		}

		PlayedNote sse = playedNotes.last();
		if (sse == null) {
			return 0;
		} else {
			return sse.getEndTime();
		}
	}

	/**
	 * It traverses the voice and return true when there are no polyphony
	 *
	 * @return True if the voice is monophonic
	 * @throws IM3Exception
	 */
	public boolean isMonophonic() {
		ArrayList<PlayedNote> v = this.getNotesAsArray();
		for (int n = 0; n < v.size(); n++) {
			PlayedNote note = v.get(n);

			// for each note we check if it overlaps any other. To
			// do so, we get the intersection in time of both notes,
			// with the last note on, and the earlier note off. If they
			// overlap, the note on will have a previous tick than then
			// note off.
			for (int n2 = n + 1; n2 < v.size(); n2++) {
				PlayedNote note2 = v.get(n2);
				long nON = Math.max(note.getTime(), note2.getTime());
				long nOFF = Math.min(note.getTime() + note.getDurationInTicks(),
						note2.getTime() + note2.getDurationInTicks());

				boolean noPolyphony = nON >= nOFF;
				if (!noPolyphony) {
					// System.err.println("Overlap note no." + n + " " +
					// note.toString() + ", with no." + n2 + " " +
					// note2.toString());
					Logger.getLogger(SongTrack.class.getName()).log(Level.INFO,
							"Overlap note no.{0} {1} t={2}, with no.{3} {4} t={5}", new Object[] { n,
									note.toString(), note.getTime(), n2, note2.toString(), note2.getTime() });
					return false;
				}
			}
		}
		return true;
	}

	/**
	 *
	 * @return sorted array
	 */
	public ArrayList<PlayedNote> getNotesAsArray() {
		ArrayList<PlayedNote> v = new ArrayList<>();
		for (PlayedNote note : this.playedNotes) {
			v.add(note);
		}
		return v;
	}

	public boolean isEmpty() {
		return playedNotes.isEmpty();
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayedSong getPlayedSong() {
		return playedSong;
	}

	public boolean containsPlayedNotes() {
		return !playedNotes.isEmpty();
	}

	/**
	 * Quantize the notes of the voice If start/end is <= gridCenter move to
	 * left, it not move to right If after move end to left, the figureAndDots
	 * is 0, move to right @param figure Use one of the IMConstants
	 * figure @param quantizeDuration If true, the ending of the n
	 *
	 * o te is also quantized @throws IMException @throws NoMeterException
	 */
	public void quantize(Figures figure, boolean quantizeDuration) throws IM3Exception {
		// @ TODO METER CHANGES AND METER != 4

		
		long gridDuration = (long) (playedSong.getResolution() * figure.getDuration().doubleValue());
		// System.out.println(figure + ", gd=" + gridDuration);
		long gridCenter = gridDuration / 2;
		// System.out.println("GD:" + gridDuration);

		if (gridDuration == 0) {
			throw new IM3Exception("The gridDuration = 0");
		}

		for (PlayedNote note : playedNotes) {
			long on = note.getTime();
			// long off = note.getTime() + note.getDurationInTicks();
			// long measureDuration = note.getBar().getDurationInTicks();
			// long measureStart = Math.round(on / measureDuration) *
			// measureDuration;
			/*
			 * long relativeOn = on - measureStart; long relativeOff =
			 * relativeOn + note.getDurationInTicks();
			 */
			long relativeOn = on;
			long relativeOff = relativeOn + note.getDurationInTicks();
			long prevRelativeOff = relativeOff;

			if (relativeOn % gridDuration != 0) {
				// System.out.println(relativeOn + "," + relativeOff + ", "+
				// (relativeOff - relativeOn) + ", " + (relativeOff -
				// (relativeOn - relativeOn % gridDuration + gridDuration)));
				if (relativeOn % gridDuration <= gridCenter
						|| (relativeOff - (relativeOn - relativeOn % gridDuration + gridDuration) < gridDuration)) {
					relativeOn -= relativeOn % gridDuration; // move to the
																// begining
					// System.out.println("antes");
				} else {
					relativeOn -= relativeOn % gridDuration; // move to the end
					relativeOn += gridDuration;
					// System.out.println("desupues");
				}

				if (relativeOff - relativeOn < gridDuration) {
					relativeOff = relativeOn + gridDuration;
				}
				// note.setTime(relativeOn + measureStart);
				note.setTime(relativeOn);
				note.setTicks(relativeOff - relativeOn);
				checkNoteDuration(note.getDurationInTicks(), gridDuration);
			}

			if (quantizeDuration) {
				relativeOff = prevRelativeOff;
				// relativeOff = relativeOn + note.getDurationInTicks();
				if (relativeOff % gridDuration != 0 || relativeOff - relativeOn == 0) {
					// ajustamos la nota a la posición de la rejilla más cercana
					// System.out.println("ScoreDuration: " + (relativeOff -
					// relativeOn));
					if (relativeOff % gridDuration <= gridCenter && relativeOff - relativeOn > gridDuration) {
						relativeOff -= relativeOff % gridDuration; // desplazamos
																	// al
																	// principio
					} else {
						relativeOff -= relativeOff % gridDuration; // desplazamos
																	// al off
						relativeOff += gridDuration;
					}

					if (relativeOff - relativeOn == 0) { // si se solapan y
															// dejamos duración
															// 0 movemos la
															// cuantización del
															// on
						relativeOn -= gridDuration;
						// note.setTime(relativeOn + measureStart);
						note.setTime(relativeOn);
					}

					note.setTicks(relativeOff - relativeOn);
					checkNoteDuration(note.getDurationInTicks(), gridDuration);
					if (relativeOff - relativeOn == 0) {
						throw new IM3Exception(
								"Quantization error with a figureAndDots = 0, on note " + note.toString());
						/*
						 * fprintf(stderr,
						 * "Cuantización incorrecta con duración 0\n");
						 * fprintf(stderr,
						 * "Antes:  on(%d), off(%d), cortes en [%d, %d]\n",on0,
						 * off0, on0 - on0 % gridDuration, on0 - on0 %
						 * gridDuration+gridDuration); fprintf(stderr,
						 * "Ahora:  on(%d), off(%d)\n", on, off); exit(1);
						 */
					}
				}
			}
		}
	}

	/**
	 * Launches an exception if the figureAndDots < than the grid figureAndDots
	 * (the minimum note length) @param figureAndDots Note figureAndDots @param
	 * grid
	 *
	 * D uration Minimum @throws IMException
	 */
	private void checkNoteDuration(long duration, long gridDuration) throws IM3Exception {
		if (duration < gridDuration) {
			throw new IM3Exception("The note figureAndDots (" + duration
					+ ") is lower than the minimum quantized grid figureAndDots (" + gridDuration + ")");
		}
	}

	public TreeSet<PlayedNote> getPlayedNotes() {
		return playedNotes;
	}

	// TODO Test unitario
	/**
	 * Polphony reduction Miguel
	 *
	 * @param poption
	 *            1->high, 2->low, 3->both
	 * @throws IMException
	 */
	/*
	 * Lo tenemos en poly2mono en song public void polyReduction(int poption)
	 * throws IM3Exception {
	 * 
	 * PlayedNote highNt, lowNt; int highPitch = 0, lowPitch = 129;
	 * 
	 * ArrayList<PlayedNote> notes = playedNotes.getOrderedValuesByTime(); for
	 * (int iter = 0; iter < notes.size(); iter++) { PlayedNote ntG =
	 * (PlayedNote) notes.get(iter);
	 * 
	 * ArrayList<PlayedNote> inter = new ArrayList(); inter.add(ntG);
	 * 
	 * //order notes that begin before ntG ends for (int iterAux = iter + 1;
	 * iterAux < notes.size(); iterAux++) { PlayedNote auxNt = (PlayedScoreNote)
	 * notes.get(iterAux);
	 * 
	 * long maxTime = ntG.getEndTime(); long time = auxNt.getTime();
	 * 
	 * if (time < maxTime) //events that begin before ntG end { int ex = 0; int
	 * it;
	 * 
	 * for (it = 0; it < inter.size() && ex != 1; it++) //order insert {
	 * PlayedNote nt2 = (PlayedScoreNote) inter.get(it); if (poption == 1 || poption
	 * == 3) //order top-down { if (nt2.getMidiPitch() < auxNt.getMidiPitch()) {
	 * it--; ex = 1; } } else if (poption == 2) //order down-top { if
	 * (nt2.getMidiPitch() > auxNt.getMidiPitch()) { it--; ex = 1; } }
	 * 
	 * } inter.add(it, auxNt); } else { break; }
	 * 
	 * } // System.out.println("llega "+inter.size()); if (inter.size() > 1 &&
	 * poption != 3) //highest or lowest note { long Tinic, Tend; for (int it =
	 * 0; it < inter.size(); it++) { PlayedNote nt = (PlayedScoreNote) inter.get(it);
	 * 
	 * Tinic = nt.getTime(); Tend = nt.getEndTime();
	 * 
	 * // System.out.println("Superior " +nt.toString()); //
	 * System.out.println("max Tinic ="+ Long.toString(Tinic) + " Tend=" +
	 * Long.toString(Tend)); inter.trimToSize();
	 * 
	 * for (int it2 = inter.size() - 1; it2 > it; --it2) {
	 * 
	 * nt = (PlayedNote) inter.get(it2);
	 * 
	 * long TinicL, TendL; TinicL = nt.getTime(); TendL = nt.getEndTime();
	 * 
	 * // System.out.println("Inferior " +nt.toString()); //
	 * System.out.println("max TinicL ="+ Long.toString(TinicL) + " TendL=" +
	 * Long.toString(TendL)); if (TinicL < Tinic) { if (TendL > Tinic)
	 * //intersecs { //cut the end case 2,3
	 * nt.setTicks((nt.getDurationInTicks()) - (TendL - Tinic)); //
	 * System.out.println("cut end"); } } else if (TendL > Tend) { if (TinicL <
	 * Tend) //intersecs { if ((Tend - TinicL) <= (nt.getDurationInTicks()) *
	 * 0.1) //cut beginning case 5. only if cut < kCutPer { nt.setTime(Tend);
	 * nt.setTicks((nt.getDurationInTicks()) - (Tend - TinicL)); //
	 * System.out.println("cut beginning");
	 * 
	 * } else { if (notes.indexOf(nt) == iter) //removes actual note { iter--; }
	 * inter.remove(nt); notes.remove(nt); // System.out.println("delete1"); } }
	 * } else //all inside { //delete the note case 4 if (notes.indexOf(nt) ==
	 * iter) //removes actual note { iter--; } inter.remove(nt);
	 * notes.remove(nt); // System.out.println("delete2"); } } }
	 * //////////////////////////////////////////////////////////////////
	 * }//highest lowest end else if (inter.size() > 2 && poption == 3)
	 * //highest and lowest notes { long TinicM, TendM, Tinic, Tend; for (int
	 * maxit = 0; maxit < inter.size() - 1; ++maxit) { PlayedNote nt =
	 * (PlayedNote) inter.get(maxit); TinicM = nt.getTime(); TendM =
	 * nt.getEndTime();
	 * 
	 * // System.out.println("Superior " +nt.toString()); //
	 * System.out.println("max Tinic ="+ Long.toString(TinicM) + " Tend=" +
	 * Long.toString(TendM)); for (int minit = inter.size() - 1; minit > maxit +
	 * 1; --minit) {
	 * 
	 * nt = (PlayedNote) inter.get(minit);
	 * 
	 * if (nt.getTime() > TinicM) { Tinic = nt.getTime(); } else { Tinic =
	 * TinicM; } long endT = nt.getEndTime(); if (endT < TendM) { Tend = endT; }
	 * else { Tend = TendM; }
	 * 
	 * // System.out.println("Inferior " +nt.toString()); //
	 * System.out.println("max Tinic ="+ Long.toString(Tinic) + " Tend=" +
	 * Long.toString(Tend)); if (Tinic < Tend) { for (int actit = maxit + 1;
	 * inter.size() > 2 && actit < minit; ++actit) {
	 * 
	 * // System.out.println("antes minit="+Integer.toString(minit)+
	 * " actit="+Integer.toString(actit)); PlayedNote ntact = (PlayedScoreNote)
	 * inter.get(actit);
	 * 
	 * long TinicL, TendL; TinicL = ntact.getTime(); TendL = ntact.getEndTime();
	 * 
	 * // System.out.println("Actual " +ntact.toString()); //
	 * System.out.println("max TinicL ="+ Long.toString(TinicL) + " TendL=" +
	 * Long.toString(TendL)); if (TinicL < Tinic) { if (TendL > Tinic)
	 * //intersecs { //cut the end case 2,3 // System.out.println("cut end"); //
	 * System.out.println("Tinic ="+ Long.toString(Tinic) + " TendL=" +
	 * Long.toString(TendL)); ntact.setTicks((ntact.getDurationInTicks()) -
	 * (TendL - Tinic)); } } else if (TendL > Tend) { if (TinicL < Tend)
	 * //intersecs { if ((Tend - TinicL) <= (ntact.getDurationInTicks()) * 0.1)
	 * //cut beginning case 5. only if cut < kCutPer % { //
	 * System.out.println("cut beginning"); ntact.setTime(Tend);
	 * ntact.setTicks((ntact.getDurationInTicks()) - (Tend - TinicL)); } else {
	 * if (notes.indexOf(ntact) == iter) //removes actual note { iter--; }
	 * inter.remove(ntact); notes.remove(ntact); actit--; minit--; //
	 * System.out.println("delete1"); } } } else //all inside { //delete the
	 * note case 4 if (notes.indexOf(ntact) == iter) //removes actual note {
	 * iter--; } inter.remove(ntact); notes.remove(ntact); actit--; minit--; //
	 * System.out.println("delete2"); } } } }
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * this.playedNotes = new DurationalTimedElementSetCollection<>(); for
	 * (PlayedNote note : notes) { this.playedNotes.addValue(note); } }
	 */

}
