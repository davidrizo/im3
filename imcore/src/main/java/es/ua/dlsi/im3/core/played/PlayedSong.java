package es.ua.dlsi.im3.core.played;

import java.util.*;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;

//TODO IO - Que se pueda leer la resolución del fichero MIDI y se guarde tal cual sin conversiones como hacíamos antes en imjava
/**
 * It represents the song as it is played. Here, notes contain basic performance
 * information: onset, figureAndDots, pitch
 *
 * @author drizo
 * @date 03/06/2011
 *
 */
public class PlayedSong {
	/**
	 * Default resolution
	 */
	public static final int DEFAULT_RESOLUTION = 960; // in order to be
														// divisible by 2 and by
														// 3 - Logic works with
														// it
	protected int resolution;
	/**
	 * Original filename
	 */
	protected String filename;
	/**
	 * Name
	 */
	protected String title;

	/**
	 * Voice, tracks
	 */
	ArrayList<SongTrack> tracks;
	
	TreeMap<Long, Meter> meters;
	TreeMap<Long, Key> keys;
	TreeMap<Long, Tempo> tempi;

	public PlayedSong() {
		this(DEFAULT_RESOLUTION);
	}

	public PlayedSong(int resolution) {
		super();
		tracks = new ArrayList<>();
		this.meters = new TreeMap<>();
		this.keys = new TreeMap<>();
		this.tempi = new TreeMap<>();
		this.resolution = resolution;
	}
	
	/**
	 * @return the tracks
	 */
	public final ArrayList<SongTrack> getTracks() {
		return tracks;
	}

	public SongTrack addTrack(int number) {
		SongTrack track = new SongTrack(this, number);
		tracks.add(track);
		return track;
	}

	public String toLongString() {
		return "PlayedSong [, tracks=" + tracks + "]";
	}

	/**
	 * Used for things like ListView from javafx
	 *
	 * @return
	 */
	/*
	 * @Override public String toString() { return this.filename; }
	 */
	/**
	 * Add a new track with number = maximum track number found + 1
	 *
	 * @return
	 */
	public SongTrack addTrack() {
		int max = 0;
		for (SongTrack track : tracks) {
			max = Math.max(max, track.getNumber());
		}
		return addTrack(max + 1);
	}

	/**
	 * Find a track with the given midi channel
	 *
	 * @param midiChannel
	 *            1..16
	 * @return
	 */
	public SongTrack getTrack(int midiChannel) {
		for (SongTrack track : tracks) {
			if (track.getDefaultMidiChannel() == midiChannel) {
				return track;
			}
		}
		return null;
	}

	/**
	 *
	 * @return PlayedSong figureAndDots in ticks
	 * @throws IM3Exception
	 */
	public long getSongDuration() throws IM3Exception {
		long max = 0;
		for (SongTrack track : tracks) {
			max = Math.max(max, track.computePlayedDuration());
		}
		return max;
	}

	// TODO Javi Test unitario
	// private - computeFigure deber�a devolver por medio de recursividad con la
	// duraci�n restante varias figuras
	/**
	 * It computes the rhythm given a figureAndDots
	 *
	 * @param figureAndDots
	 * @return
	 */

	/*
	 * public Rhythm computeFigure(long figureAndDots) throws IM3Exception {
	 * //TODO Javi acabarlo, ahora no contemplamos ni ligados, ni puntillos for
	 * (Figures fig : Figures.values()) { double figdur = fig.getRatio() *
	 * resolution; if (figdur == figureAndDots) { return new Rhythm(fig, 0); }
	 * else if (figdur * 1.5 == figureAndDots) { return new Rhythm(fig, 1); }
	 * else if (figdur * 1.75 == figureAndDots) { return new Rhythm(fig, 2); } }
	 * throw new IM3Exception("Cannot obtain a rhythm for figureAndDots " +
	 * figureAndDots + " using resolution " + resolution); }
	 */
	// TODO URGENT No tengo en cuenta cambios de comp�s
	/**
	 * It creates the set of bars given the different time signatures
	 *
	 * @param track
	 * @return
	 */
	/*
	 * public void createBarsFromPlayedNotes() throws IM3Exception { if
	 * (this.timeSignatures.size() > 1) { throw new
	 * IM3Exception("Cannot compute the bars with time changes so far"); }
	 * 
	 * Meter ts = timeSignatures.first(); long figureAndDots =
	 * this.computePlayedDuration(); long t = 0; int i=0; while (t <
	 * figureAndDots) { Measure bar = new Measure(this, t, i++); t +=
	 * ts.getMeasureDuration(resolution); this.addBar(bar); } }
	 */
	// TODO Javi Test unitario
	public boolean remove(SongTrack track) {
		return this.tracks.remove(track);
	}

	public SortedSet<PlayedNote> getAllPlayedNotes() throws IM3Exception {
		TreeSet<PlayedNote> result = new TreeSet<>();
		for (SongTrack track : tracks) {
			result.addAll(track.getPlayedNotes());
		}
		return result;
	}

	public boolean containsPlayedNotes() {
		for (SongTrack track : tracks) {
			if (track.containsPlayedNotes()) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		for (SongTrack track : this.tracks) {
			if (track.containsPlayedNotes()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Transform all layers in song into an only voice with the skyline
	 * algorithm
	 *
	 * @return
	 * @throws IM3Exception
	 */
	/*
	 * public void skyline() throws IM3Exception { SongTrack newVoice =
	 * this.addTrack();
	 * 
	 * for (SongTrack track : this.tracks) { if (track != newVoice) { for
	 * (PlayedNote note: track.getPlayedNotes()) { if (note.getMidiChannel() !=
	 * 9) { newVoice.addNote(note); } } this.tracks.remove(track); } }
	 * this.polyTracks2MonoTracks(); }
	 * 
	 * public void polyTracks2MonoTracks() throws IM3Exception { for (SongTrack
	 * track : this.tracks) { track.poly2Mono(); } }
	 */
	public SongTrack getUniqueTrack() throws IM3Exception {
		return getUniqueTrack(false);
	}

	/**
	 * If true, empty tracks are removed
	 *
	 * @param removeEmpty
	 * @return
	 * @throws IM3Exception
	 */
	public SongTrack getUniqueTrack(boolean removeEmpty) throws IM3Exception {
		if (removeEmpty) {
			for (Iterator<SongTrack> iterator = tracks.iterator(); iterator.hasNext();) {
				SongTrack track = iterator.next();
				if (track.isEmpty()) {
					iterator.remove();
				}
			}
		}

		if (tracks.size() > 1) {
			throw new IM3Exception("PlayedSong has more than one tracks, it has " + tracks.size());
		}
		return tracks.get(0);
	}

	public void removeDrumsTrack() { // TODO Test unitario
		for (SongTrack track : this.tracks) {
			if (track.isDefaultMidiChannelSet() && track.getDefaultMidiChannel() == 9) {
				this.remove(track);
			}
		}
	}

	// TODo Test unitario
	/**
	 * Quantize the notes of the voice
	 *
	 * @param figure
	 *            Use one of the IMConstants figure
	 * @param quantizeDuration
	 *            If true, the ending of the note is also quantized
	 * @throws IMException
	 * @throws NoMeterException
	 */
	public void quantize(Figures figure, boolean quantizeDuration) throws IM3Exception {
		for (SongTrack track : this.tracks) {
			track.quantize(figure, quantizeDuration);
		}
	}

	/**
	 * Given a song with only a voice with notes, it returns it
	 *
	 * @return
	 */
	public SongTrack getUniqueVoice() throws IM3Exception {
		return getUniqueVoice(3);
	}

	/**
	 * Given a song with only a voice with notes, it returns it
	 *
	 * @param minimumNumberOfNotes
	 * @return
	 * @throws IM3Exception
	 */
	public SongTrack getUniqueVoice(int minimumNumberOfNotes) throws IM3Exception {
		SongTrack voice = null;
		int numNotesPrevious = 0;
		for (SongTrack v : tracks) {
			if (numNotesPrevious > minimumNumberOfNotes && voice != null && v.getNumNotes() > minimumNumberOfNotes) {
				throw new IM3Exception(
						"This song has more than a track with layers: " + voice.toString() + " and " + v.toString());
			} else if (v.getNumNotes() > minimumNumberOfNotes || voice == null) {
				voice = v;
				numNotesPrevious = v.getNumNotes();
			}
		}
		if (voice == null) {
			throw new IM3Exception("The song has not any voice");
		}
		return voice;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// TODO Test unitario
	/**
	 * It leaves a monophonic version of all layers. For each tick, the
	 * uppermost note that is on is selected. It works on the played notes
	 * version of the melody, not on the notated notes. If you need to work with
	 * the notated notes version, invoke constructNotatedNotes after doing the
	 * poly2Mono The algorithm in time O(n) used works as follows:
	 * <ol>
	 * <li>Create an ordered list <i>L</i> by time of the <i>note on</i> and
	 * <i>off</i> of all notes</li>
	 * <li>Create a temporal working list <i>P</i> ordered by pitch</i>.
	 * <li>Iterate though the list <i>L</i> keeping the current note <i>CN</i>.
	 * For each element <i>I</i> (<i>note on</i> or <i>off</i>): if <i>I</i> is
	 * <i>ON</i> insert <i>I</i> into the list <i>P</i>. If it is <i>OFF</i>
	 * remove it from the list <i>P</i>. In addition:
	 * <ul>
	 * <li>If <i>CN</i> is empty and <i>I</i> is <i>off</i> throw an error</li>
	 * <li>If <i>CN</i> is empty and <i>I</i> is <i>on</i> insert a new note as
	 * <i>CN</i></li>
	 * <li>If <i>CN</i> is not empty and <i>I</i> is <i>on</i> and the pitch of
	 * <i>I</i> is lower or equal than <i>CN</i> leave <i>CN</i> as it is</li>
	 * <li>If <i>CN</i> is not empty and <i>I</i> is <i>on</i> and the pitch of
	 * <i>I</i> is higher than <i>CN</i> then finish <i>CN</i> giving it
	 * figureAndDots, and insert a new note as <i>CN</i> with the pitch of
	 * <i>I</i></li> li>If <i>CN</i> is not empty and <i>I</i> is <i>off</i> and
	 * <i>CN</i> is the same pitch as <i>I</i>finish <i>CN</i>
	 * </ul>
	 * </li>
	 * </ol>
	 * 
	 * @Deprecated No funciona!!!! - la deja polifónca It removes all tracks and
	 *             leaves just a mono track
	 * @throws IMException
	 */

	public List<PlayedNote> poly2Mono() throws IM3Exception {
		class _NoteEvent {

			boolean isNoteOn; // if false is note off
			long tick; // time
			PlayedNote note;

			public _NoteEvent(boolean isNoteOn, long tick, PlayedNote note) {
				this.isNoteOn = isNoteOn;
				this.tick = tick;
				this.note = note;
			}

			public String toString() {
				StringBuffer sb = new StringBuffer();
				if (isNoteOn) {
					sb.append('+');
				} else {
					sb.append('-');
				}
				sb.append(note.getMidiPitch());

				return sb.toString();
			}

		}

		Comparator<_NoteEvent> poly2MonoComparatorNotesByTime = new Comparator<_NoteEvent>() {
			public int compare(_NoteEvent n0, _NoteEvent n1) {

				if (n0.tick < n1.tick) {
					return -1;
				} else if (n0.tick > n1.tick) {
					return 1;
				} else {
					// it is important to keep note on before note off for the
					// construction of the algorithm
					if (n0.isNoteOn && !n1.isNoteOn) {
						return -1; // first note on
					} else if (!n0.isNoteOn && n1.isNoteOn) {
						return 1;
					}
					// else look the pitch
					if (n0.note == null) {
						Logger.getLogger(PlayedSong.class.getName())
								.severe("The first _NoteEvent has not a note object");
						return 0;
					}
					if (n1.note == null) {
						Logger.getLogger(PlayedSong.class.getName())
								.severe("The second _NoteEvent has not a note object");
						return 0;
					}
					// (first higher notes)
					int result = n1.note.getMidiPitch() - n0.note.getMidiPitch();

					if (result == 0) {
						return n1.hashCode() - n0.hashCode(); // sometimes,
																// there are 2
																// noteon on the
																// same pitch
																// and same
																// tick. We want
																// both are put
						// if we put 0, one of them would be removed (not
						// inserted)
					}
					return result;
				}
			}

			public boolean equals(Object arg0) {
				return true;
			}
		};
		Comparator<_NoteEvent> poly2MonoComparatorNotesByKey = new Comparator<_NoteEvent>() {
			public int compare(_NoteEvent n0, _NoteEvent n1) {
				if (n0.note == null) {
					Logger.getLogger(PlayedSong.class.getName()).severe("The first _NoteEvent has not a note object");
					return 0;
				}
				if (n1.note == null) {
					Logger.getLogger(PlayedSong.class.getName()).severe("The second _NoteEvent has not a note object");
					return 0;
				}
				// if same pitch return 0 (first higher notes)
				int res = n1.note.getMidiPitch() - n0.note.getMidiPitch();
				if (res == 0) {
					res = (int) (n0.tick - n1.tick);
					// sometimes, there are 2 noteon on the same pitch and same
					// tick. We want both are put
					// if we put 0, one of them would be removed (not inserted)
					if (res == 0) {
						res = n0.hashCode() - n1.hashCode();
					}
					return res;
				} else {
					return res;
				}
			}

			public boolean equals(Object arg0) {
				return true;
			}
		};
		// first step
		long lastNoteOff = -1; // to close the last note
		TreeSet<_NoteEvent> L = new TreeSet<>(poly2MonoComparatorNotesByTime); // ordered
																				// list
																				// of
																				// note
																				// on
																				// /
																				// note
																				// off
		for (SongTrack track : this.getTracks()) {
			for (PlayedNote element : track.getNotesAsArray()) {
				_NoteEvent nON = new _NoteEvent(true, element.getTime(), element);
				_NoteEvent nOFF = new _NoteEvent(false, element.getTime() + element.getDurationInTicks(), element);
				L.add(nON);
				L.add(nOFF);
			}
		}

		// next step
		ArrayList<PlayedNote> notesMono = new ArrayList<>();
		TreeSet<_NoteEvent> P = new TreeSet<>(poly2MonoComparatorNotesByKey);
		PlayedNote CN = null;
		for (Iterator<_NoteEvent> iter = L.iterator(); iter.hasNext();) {
			_NoteEvent element = iter.next(); // element is I in
															// the description
															// of the algorithm
															// in the javadoc

			// System.out.print(element.isNoteOn + " " + element.tick + " " +
			// element.note.getMidiPitch() + "\t");
			if (element.isNoteOn) {
				P.add(element);
				// System.out.println("Pa="+P.toString());
			} else {
				lastNoteOff = element.tick;
				boolean found = false; // remove this pitch
				for (Iterator<_NoteEvent> iterator2 = P.iterator(); !found && iterator2.hasNext();) {
					_NoteEvent element2 = iterator2.next();
					if (element2.note.getMidiPitch() == element.note.getMidiPitch()) {
						iterator2.remove();
						found = true;
					}

				}

				if (!found) {
					throw new IM3Exception("Cannot find note to remove: " + element.note.getMidiPitch());
				}
				// System.out.println("Pr="+P.toString());
				// P.remove(new Integer(element.note.getMidiPitch()));
			}

			if (CN == null) {
				if (element.isNoteOn) {
					CN = new PlayedNote(element.note.getMidiPitch(), element.note.getDurationInTicks());
					CN.setTime(element.note.getTime());
				} else {
					throw new IM3Exception("The next _NoteEvent after a silence (or first note) is a Note OFF");
				}
			} else {
				if (element.isNoteOn) {
					if (CN.getMidiPitch() < element.note.getMidiPitch()) {
						// to avoid cases in which an upper pitch finishes at
						// the same time as a lower one
						if (element.tick - CN.getTime() > 0) {
							CN.setTicks(element.tick - CN.getTime());
							notesMono.add(CN);
							// System.out.println("Note added (1): " +
							// CN.toString());
							CN = new PlayedNote(element.note.getMidiPitch(), element.note.getDurationInTicks());
							CN.setTime(element.note.getTime());
						} else {
							CN = null;
						}
					}
				} else {
					if (element.note.getMidiPitch() == CN.getMidiPitch()) {
						if (element.tick - CN.getTime() > 0) {
							CN.setTicks(element.tick - CN.getTime());
							notesMono.add(CN);
						}
						// System.out.println("Note added (2): " +
						// CN.toString());
						if (P.isEmpty()) {
							CN = null;
						} else {
							// take the note with the highest pitch
							// Integer pitch = (Integer) P.first();
							_NoteEvent pitch = P.first();
							//david 20170327 CN = new PlayedNote(element.note.getMidiPitch(), element.note.getDurationInTicks());
							CN = new PlayedNote(pitch.note.getMidiPitch(), element.note.getDurationInTicks());
							CN.setTime(element.note.getTime());

						}
					}
				}
			}
		}

		// close the last note (if required)
		if (CN != null) {
			if (lastNoteOff - CN.getTime() > 0) {
				CN.setTicks(lastNoteOff - CN.getTime());
				notesMono.add(CN);
			}
			// System.out.println("Note added (3): " + CN.toString());
		}

		// replace the old notes vector for the new one
		this.tracks.clear();
		SongTrack track = this.addTrack();
		for (PlayedNote note : notesMono) {
			track.addNote(note.getTime(), note);
		}

		return notesMono;
	}
	/**
	 * @return
	 */
	public int getResolution() {
		return resolution;
	}

	public void addMeter(long time, Meter meter) {
		meter.setTime(time);
		this.meters.put(time, meter);
	}

	public void addTempoChange(long time, Tempo tempo) {
		tempo.setTime(time);
		this.tempi.put(time, tempo);
	}

	public void addKey(long time, Key key) {
		key.setTime(time);
		this.keys.put(time, key);
	}

	public Collection<Meter> getMeters() {
		return meters.values();
	}

	public Collection<Key> getKeys() {
		return keys.values();
	}

	public Collection<Tempo> getTempoChanges() {
		return tempi.values();
	}

	public Meter getActiveMeterAt(long time) throws IM3Exception {
        Map.Entry<Long, Meter> entry = meters.floorEntry(time);
        if (entry == null) {
            throw new IM3Exception("Cannot find a meter active at time " + time);
        }
        return entry.getValue();
	}


    //TODO Test unitario
    /**
     * It computes the onsets of the measures given the different time signatures
     * @return
     * @throws IM3Exception
     */
	public Measures computeMeasureOnsets() throws IM3Exception {
        if (meters.isEmpty()) {
            throw new IM3Exception("There are not meters");
        }

        long songDuration = this.getSongDuration();

        Measures result = new Measures();
        if (songDuration != 0) {
            ArrayList<Meter> mts = new ArrayList<>();
            mts.addAll(meters.values());
            int n = mts.size();
            long t = 0;
            int number=0;
            for (int i = 0; i < n; i++) {
                double toTime;
                if (i == n - 1) {
                    toTime = songDuration;
                } else {
                    toTime = mts.get(i + 1).getTime();
                }
                long dur = mts.get(i).getMeasureDurationAsTicks(resolution);
                int nbars = (int) Math.ceil((toTime - t) / dur);
                for (int ib = 0; ib < nbars; ib++) {
                    Measure measure = new Measure(number++, t, dur);
                    result.add(measure);
                    t += dur;
                }
            }
        }

        return result;
    }
}
