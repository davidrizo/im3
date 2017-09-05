/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.played.io;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import es.ua.dlsi.im3.core.played.Key;
import es.ua.dlsi.im3.core.played.Meter;
import es.ua.dlsi.im3.core.played.PlayedNote;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.SongTrack;
import es.ua.dlsi.im3.core.played.Tempo;
import es.ua.dlsi.im3.core.played.Key.Mode;
import es.ua.dlsi.im3.core.io.ImportException;

import java.util.logging.Level;

// TODO Tests unitarios
/**
 * Imports a song from a MIDI file
 *
 * @author david
 */
public class MidiSongImporter {

	Logger logger = Logger.getLogger(MidiSongImporter.class.getName());
	/**
	 * MIDI extension
	 */
	public static String FILE_EXTENSION = ".mid";
	/**
	 * MIDI extension
	 */
	public static String KAR_FILE_EXTENSION = ".kar";
	/**
	 * If true, instead of reading the tracks into layers, 16 layers are
	 * created, and each note is inserted in the voice corresponding to the midi
	 * event channel. By default it is false
	 */
	private boolean useChannelsAsTracks;

	/**
	 * Use the tracks as layers
	 */
	public MidiSongImporter() {
		useChannelsAsTracks = false;
	}

	/**
	 * @param auseChannelsAsTracks
	 *            If false, use the tracks as layers, if true the voice is the
	 *            message channel for each event
	 */
	public MidiSongImporter(boolean auseChannelsAsTracks) {
		useChannelsAsTracks = auseChannelsAsTracks;
	}

	/**
	 * Returns the note event type
	 *
	 * @param codigo
	 * @return 2 for note on, 1 for note off, 0 otherwise
	 */
	/*
	 * private int noteEventType(int code) { int valor=0;
	 * 
	 * if (code>=128 && code<=143) valor=1; else if (code>=144 && code<=159)
	 * valor=2;
	 * 
	 * return valor; }
	 */
	/**
	 * Converts a byte into its hexadecimal notation.
	 *
	 * @param num
	 *            a byte (1bytes)
	 * @param padding
	 *            fit the length to 2 by filling with '0' when padding is true
	 * @return hexadecimal notation of the byte
	 */
	public static String valueOf(byte num, boolean padding) {
		String hex = Integer.toHexString((int) num);

		if (padding) {
			hex = "00" + hex;
			int len = hex.length();

			hex = hex.substring(len - 2, len);
		}
		return hex;
	}

	/**
	 * Converts a byte sequence into its hexadecimal notation.
	 *
	 * @param seq
	 *            a byte sequence
	 * @return hexadecimal notation of the byte sequence
	 */
	public static String valueOf(byte[] seq) {
		if (seq == null) {
			return null;
		}
		StringBuffer buff = new StringBuffer();

		for (int i = 0; i < seq.length; i++) {
			buff.append(valueOf(seq[i], true));
		}
		return buff.toString();
	}

	/**
	 * Converts a string in hexadecimal notation into integer.
	 *
	 * @param hex
	 *            string in hexadecimal notation
	 * @return a integer (4bytes)
	 */
	public static int parseInt(String hex) throws NumberFormatException {
		if (hex == null) {
			throw new IllegalArgumentException("Null string in hexadecimal notation.");
		}
		if (hex.equals("")) {
			return 0;
		}
		Integer num = Integer.decode("0x" + hex);
		long n = num.longValue();

		if (n > 4294967295L || n < 0L) {
			throw new NumberFormatException("Out of range for integer.");
		}
		return num.intValue();
	}

	/**
	 * Convert a byte array into a integer (4 bytes)
	 *
	 * @param seq
	 * @return
	 */
	public static int parseInt(byte[] seq) {
		return parseInt(valueOf(seq));
	}

	/**
	 * @see es.ua.dlsi.im.ISongImporter #importSong(java.io.File)
	 */
	public PlayedSong importSong(File file) throws ImportException {
		return importSong(file, false);
	}

	public PlayedSong importSong(File file, boolean skipDrums) throws ImportException {
		PlayedSong playedSong;
		boolean channelsAsTracks = useChannelsAsTracks;
		Key lastKeySignature = null;
		try {
			if (skipDrums) {
				logger.log(Level.INFO, "Skipping drums channel from MIDI file {0}", file.toString());
			}
			Sequence sequence = MidiSystem.getSequence(file);

			playedSong = new PlayedSong(sequence.getResolution());
			playedSong.setFilename(file.getAbsolutePath());
			playedSong.setTitle(file.getName());
			if (!channelsAsTracks && MidiSystem.getMidiFileFormat(file).getType() == 0) {
				logger.log(Level.FINE, "File ''{0}'' is type 0, spliting it into channels", file.getAbsolutePath());
				channelsAsTracks = true;
			}
			Meter firstTimeSignatureFound = null;
			if (sequence.getDivisionType() != Sequence.PPQ) {
				throw new ImportException("Sequence has not division type PPQ");
			}

			/*
			 * String strResolutionType = null; if (sequence.getDivisionType()
			 * == Sequence.PPQ) { strResolutionType = " ticks per beat"; } else
			 * { strResolutionType = " ticks per frame"; }
			 * System.out.println("Resolution: " + sequence.getResolution() +
			 * strResolutionType);
			 */
			if (channelsAsTracks) { // add first 16 layers (one for each midi
									// channel)
				for (int i = 0; i < 16; i++) {
					playedSong.addTrack().setDefaultMidiChannel(i + 1);
				}
			}

			long[][] midiON = new long[16][128]; // -1 if empty, other on NOTE
													// ON
			int[][] velocities = new int[16][128];
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < midiON.length; j++) {
					midiON[i][j] = -1;
				}
			}
			int lastNum = -1, lastDen = -1;
			Track[] tracks = sequence.getTracks();
			for (int i = 0; i < tracks.length; i++) {
				// System.out.println((i+1) + "->size=" + tracks[i].size() + ",
				// ticks="+tracks[i].ticks());
				int[] midiChannelEventsCount = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; // used
																									// to
																									// guess
																									// the
																									// main
																									// midi
																									// channel
																									// (only
																									// for
																									// useChannelsAsTracks
																									// =
																									// false)
				SongTrack voice = null;
				if (!channelsAsTracks) {
					voice = playedSong.addTrack(); // the layers are inserted
													// for each track
				}

				for (int j = 0; j < tracks[i].size(); j++) {
					MidiEvent e = tracks[i].get(j);
					// System.out.println(e.getTick());
					if (e.getMessage() instanceof MetaMessage) {
						// URGENT - leerlos todos bien y escribirlos sobre mi
						// metaevento analizado
						// Ahora lo leemos todo a MidiMetaevent
						/*
						 * MidiMetaevent midiMetaevent = new MidiMetaevent();
						 * midiMetaevent.setSourceMessage((MidiMessage)
						 * e.getMessage().clone());
						 * midiMetaevent.setTime(e.getTick());
						 * voice.addMetaevent(midiMetaevent);
						 */

						// switch
						// (((int)((e.getMessage().getMessage()[1])&0xFF))) {
						MetaMessage mm = (MetaMessage) e.getMessage();
						switch (mm.getType()) {
						case IMidiConstants.METER: // 0x58 // TODO - primer
													// param ¿tiempo?
							int num = ((int) ((mm.getMessage()[3]))); // mm.getMessage()[3])&0xFF)
							int den = ((int) ((mm.getMessage()[4]))); // mm.getMessage()[2])&0xFF)
							// System.out.println("Num="+num+",Den="+den);
							if (lastNum != num || lastDen != den) {
								//// song.addMetaelement(new Meter(num, den));
								Meter meter = new Meter(num, (int) Math.pow(2, den));
								playedSong.addMeter(e.getTick(), meter);
								// System.out.println("Add " + meter.toString()
								// + " at tick " + e.getTick());
								if (firstTimeSignatureFound == null || meter.getTime() == 0) {
									firstTimeSignatureFound = meter;
								}

								lastNum = num;
								lastDen = den;
							}
							break;
						case IMidiConstants.TEMPO: // 0x51
							// System.err.println("Primer param de
							// meter="+((int)((mm.getMessage()[0]))));
							// System.out.println("Tempo");
							byte[] bytes = new byte[3];
							// System.err.println("--->"+valueOf(mm.getMessage()));
							bytes[0] = mm.getMessage()[3];
							bytes[1] = mm.getMessage()[4];
							bytes[2] = mm.getMessage()[5];
							// System.err.println(">-->"+(long)parseInt(bytes));
							long microsecondsPermidiQuarters = (long) parseInt(bytes);
							long tempo = 60000000L / microsecondsPermidiQuarters;
							// System.out.println(tempo);
							// float tempo = ((int)((mm.getMessage()[1])&0xFF));
							// song.addTempoChange(new Tempo(e.getTick(), (int)
							// tempo, microsecondsPermidiQuarters));
							Tempo tempoo = new Tempo((int) tempo);
							playedSong.addTempoChange(e.getTick(), tempoo);
							// System.out.println("Tempo = " + tempo + " byts
							// midi="+ (long)parseInt(bytes));

							break;
						case IMidiConstants.TRACK_NAME: // 0x03
							// only for track mode (not for channel mode)
							if (!channelsAsTracks) {
								StringBuffer sb = new StringBuffer();
								for (int im = 3; im < mm.getLength(); im++) {
									sb.append((char) (mm.getMessage()[im]));
								}
								voice.setName(sb.toString().trim());
							}
						}
						// TODO Javi Importar resto de metamensajes del
						// estandard midi
					} else if (e.getMessage() instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) e.getMessage();
						int midiChannel = sm.getChannel();
						midiChannelEventsCount[midiChannel]++;
						if (channelsAsTracks) {
							voice = playedSong.getTrack(midiChannel + 1);
							if (voice == null) {
								throw new ImportException("Cannot find part for MIDI channel " + midiChannel);
							}
						}
						switch (sm.getCommand()) {
						case ShortMessage.NOTE_ON:
						case ShortMessage.NOTE_OFF:
							if (skipDrums && sm.getChannel() == 9) {
								// skip note
								// System.out.println("MIDI: " +
								// sm.getChannel());

							} else {
								int noteNumber = sm.getData1();
								int velocity = sm.getData2();
								if (noteNumber < 0 || noteNumber >= 128) {
									throw new ImportException("PlayedNote number not valid");
								}
								if (velocity != 0 && sm.getCommand() == ShortMessage.NOTE_ON) {
									if (e.getTick() < 0) {
										throw new ImportException("The note tick is < 0: " + e.getTick()
												+ " while importing " + file.getAbsolutePath());
									}
									midiON[midiChannel][noteNumber] = e.getTick(); // TODO
																														// Javi
																														// Falta
																														// añadir
																														// la
																														// velocity
									velocities[midiChannel][noteNumber] = velocity;
								} else if (midiON[midiChannel][noteNumber] != -1) { // maybe,
																					// a
																					// note
																					// on
																					// with
																					// velocity
																					// 0
																					// without
																					// a
																					// note
																					// on
																					// can
																					// ocurr
									long midiDuration = (long) ((double) e.getTick()
											- midiON[midiChannel][noteNumber]);
									long duration = midiDuration;
									if (duration > 0) { // avoid incorrect notes
										// System.out.println("ScoreDuration="+figureAndDots);
										PlayedNote note = new PlayedNote(noteNumber, duration);
										voice.addNote(midiON[midiChannel][noteNumber], note);
										note.setVelocity(velocities[midiChannel][noteNumber]);
										velocities[midiChannel][noteNumber] = 0;
										note.setMidiChannel(midiChannel);
										// voice.addNote(note);
										// System.out.println("JJJ"+note.toString()
										// + " channel " +
										// note.getMidiChannel());
									} /*
										 * else { System.err.println(e.getTick()
										 * + " -- " +
										 * midiON[midiChannel][noteNumber]); }
										 */

									// System.out.println("Dtime="+note.getTime()+"
									// midi="+note.getMidiNote() + " dur "+
									// note.getDurationInTicks());

								}
							}
							break;
						}
					}

					// TODO URGENT - ES REDUNDANTE CON EL CODIGO DE ARRIBA (IS
					// METAEVENT)
					// if it's a metaevent or exclusive
					if (e.getMessage().getStatus() == 255) {
						// URGENT - leerlos todos bien y escribirlos sobre mi
						// metaevento analizado
						// Ahora lo leemos todo a MidiMetaevent
						// revisar (repetido arriba)
						/*
						 * MidiMetaevent midiMetaevent = new MidiMetaevent();
						 * midiMetaevent.setSourceMessage((MidiMessage)
						 * e.getMessage().clone());
						 * midiMetaevent.setTime(e.getTick());
						 * voice.addMetaevent(midiMetaevent);
						 */
						Key keySignature = null;
						int sf, mi;
						switch ((e.getMessage().getMessage()[1]) & 0xFF) {
						// case IMidiConstants.METER: // 0x58
						// int num =
						// ((int)((e.getMessage().getMessage()[3])&0xFF));
						// int den =
						// ((int)((e.getMessage().getMessage()[2])&0xFF));
						// int num = ((int)((e.getMessage().getMessage()[3])));
						// int den = ((int)((e.getMessage().getMessage()[2])));
						// song.addMetaelement(new Meter(num, den));
						// break;
						// case IMidiConstants.TEMPO: // 0x51
						// float tempo =
						// ((int)((e.getMessage().getMessage()[1])&0xFF));
						// song.addMetaelement(new Tempo(tempo));
						// break;
						case IMidiConstants.KEY_SIGNATURE:
							if (e.getMessage().getMessage().length >= 5) {
								sf = ((int) ((e.getMessage().getMessage()[3])));
								mi = ((int) ((e.getMessage().getMessage()[4])));
								// System.err.println(sf + " - " + mi);

								keySignature = new Key(sf, mi == 0 ? Mode.MAJOR : Mode.MINOR);
								// Key keySignature = new Key(e.getTick(),
								// Key.getKeyFromKeySignature(sf, mode),mode);
								// TODO Hacerlo con flag verbose
								// System.out.println("\t#(midi import)Key
								// change at " + e.getTick() + ": " +
								// Key.toString());
								if (lastKeySignature != null
										&& lastKeySignature.getTime() == e.getTick()) {
									if (!lastKeySignature.equals(keySignature)) {
										logger.warning("Two instrumentKey changes (" + lastKeySignature.toString() + " and "
												+ keySignature + " at the same tick (" + (e.getTick())
												+ "), leaving last one");
										lastKeySignature.overwriteWith(keySignature);
									}
								} else {
									playedSong.addKey(e.getTick(), keySignature);
									lastKeySignature = keySignature;
								}
							}
							break;
						}
					}
				}
				if (!channelsAsTracks) {
					// locate the most repeated channel
					int max = 0;
					int channel = -1;
					for (int cnl = 0; cnl < 16; cnl++) {
						if (midiChannelEventsCount[cnl] > max) {
							max = midiChannelEventsCount[cnl];
							channel = cnl + 1; // start by 1
						}
					}
					voice.setDefaultMidiChannel(channel);
				}
			}

			return playedSong;
		} catch (InvalidMidiDataException e) {
			throw new ImportException(e);
		} catch (IOException e) {
			throw new ImportException(e);
		} 
	}

	/**
	 * @return Returns the useChannelsAsTracks.
	 */
	public final boolean isUseChannelsAsTracks() {
		return useChannelsAsTracks;
	}

	/**
	 * @param useChannelsAsTracks
	 *            The useChannelsAsTracks to set.
	 */
	public final void setUseChannelsAsTracks(boolean useChannelsAsTracks) {
		this.useChannelsAsTracks = useChannelsAsTracks;
	}

}
