/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.played.io;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import es.ua.dlsi.im3.core.played.Key;
import es.ua.dlsi.im3.core.played.Meter;
import es.ua.dlsi.im3.core.played.PlayedNote;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.SongTrack;
import es.ua.dlsi.im3.core.played.Tempo;
import es.ua.dlsi.im3.core.played.Key.Mode;
import es.ua.dlsi.im3.core.io.ExportException;


/**
 * Exports a song to a MIDI file
 * @author david
 */
public class MidiSongExporter {
	private static final String ZERO = "0";
	private static final String PREFIX_HEXA = "0x";
	private static final String EMPTY = "";
	/**
	 * LOG2
	 */
	private static final double LOG2 = Math.log(2);
    // TODO: 29/10/17 Generalizar esto
    // TODO: 29/10/17 For EWSC Word Builder 
    private boolean resetEWSCWordBuilderMessage;

    /**
	 * Converts a string in hexadecimal notation into byte.
	 * @param hex string in hexadecimal notation
	 * @return a byte (1bytes)
	 */
	public static byte parseByte(String hex) throws NumberFormatException {
	        if (hex == null) {
	                throw new IllegalArgumentException("Null string in hexadecimal notation.");
	        } 
	        if (hex.equals(EMPTY)) {
	                return 0;
	        } 
	        Integer num = Integer.decode(PREFIX_HEXA + hex);
	        int n = num.intValue();
	
	        if (n > 255 || n < 0) {
	                throw new NumberFormatException("Out of range for byte.");
	        } 
	        return num.byteValue();
	}	
    /**
     * Converts a string in hexadecimal notation into byte sequence.
     * @param str a string in hexadecimal notation
     * @return byte sequence
     */
    public static byte[] parseSeq(String str) throws NumberFormatException {
            if (str == null || str.equals(EMPTY)) {
                    return null;
            } 
            int len = str.length();
            if (len % 2 != 0) {
            		str = ZERO + str;
            		len++;
                    //throw new NumberFormatException("Illegal length of string in hexadecimal notation.");
            } 
            int numOfOctets = len / 2;
            byte[] seq = new byte[numOfOctets];

            for (int i = 0; i < numOfOctets; i++) {
                    String hex = str.substring(i * 2, i * 2 + 2);
                    seq[i] = parseByte(hex);
            } 
            
            return seq;
    }
    /**
     * COnvert int to byte[]
     * @param i
     * @return
     */
    public static byte[] parseInt(int i) {
    		return parseSeq(Integer.toHexString(i));
    }
    /**
     * COnvert int to byte[]
     * @param i
     * @return
     */
    public static byte[] parseLong(long i) {
    		return parseSeq(Long.toHexString(i));
    }
    /**
     * Put the meter into the track
     * @param track
     * @param meter
     * @throws InvalidMidiDataException 
     */
    private void putTimeSignatureChange(Track track, Meter meter, long tick) throws InvalidMidiDataException {
		//System.out.println("Exporting meter");
		MetaMessage mm = new MetaMessage();
		int den = (int)(Math.log(meter.getDenominator()) / LOG2);
		byte [] msg = { 
			(byte)(meter.getNumerator()), (byte) den, 
			(byte)0x18, (byte)0x8}; // we are ignoring this content in the importing of MIDI files 
		mm.setMessage(IMidiConstants.METER, msg, msg.length);
		MidiEvent e = new MidiEvent(mm,tick);
		// 0x18 0x8 
		track.add(e);
    }
    /**
     * Put the track name as a metaevent in the track
     * @param track
     * @param trackName
     * @throws InvalidMidiDataException 
     */
    private void putTrackName(Track track, String trackName) throws InvalidMidiDataException {
		MetaMessage mm = new MetaMessage();
		byte [] msg = trackName.getBytes();		
		mm.setMessage(IMidiConstants.TRACK_NAME, msg, msg.length);
		MidiEvent e = new MidiEvent(mm,0);
		track.add(e);  
    }
    
	private void putKeySignature(Track track, Key element, long tick) throws InvalidMidiDataException {
		MetaMessage mm = new MetaMessage();
		int mode = element.getMode()==Mode.MAJOR?0:1;
		int sf = element.getFifths();
		byte [] msg = { 
				(byte)(sf), (byte) mode}; 
		
		mm.setMessage(IMidiConstants.KEY_SIGNATURE, msg, msg.length);
		MidiEvent e = new MidiEvent(mm,tick);
		track.add(e);  
	}
    
    /**
     * Put a lyrics event for a note
     * @param track
     * @param lyric
     * @param time
     * @throws InvalidMidiDataException
     */
    private void putLyric(Track track, String lyric, long time) throws InvalidMidiDataException {
		MetaMessage mm = new MetaMessage();
		byte [] msg = lyric.getBytes();		
		mm.setMessage(IMidiConstants.LYRICS, msg, msg.length);
		MidiEvent e = new MidiEvent(mm,time);
		track.add(e);  
    }
    
    
    /**
     * Put the meter into the track
     * @param track
     * @throws InvalidMidiDataException
     */
    private void putTempoChange(Track track, Tempo tempo, long tick) throws InvalidMidiDataException {
		//System.out.println("Exporting tempo");
		//System.out.println("??"+ (60000000L / (long)tempo.getTempo()));
		byte[] btempo = parseLong(60000000L / (long)tempo.getTempo());
		int b;
		byte[] bytes = new byte[3];
		for (b=0; b<(3-btempo.length); b++) {
			bytes[b] = 0;
		}
		for (int bb=0; b<3; bb++,b++) {
			bytes[b] = btempo[bb];
		}
		byte [] msg = {
				bytes[0],
				bytes[1],
				bytes[2]}; 
		//System.err.println("##--->"+MidiSongImporter.valueOf(msg));
		//System.out.println(">>"+MidiSongImporter.parseInt(bytes));
		MetaMessage mm = new MetaMessage();
		mm.setMessage(IMidiConstants.TEMPO, msg, msg.length);
		MidiEvent e = new MidiEvent(mm, tick);
		track.add(e);
    }
    public void exportSong(File file, PlayedSong playedSong) throws ExportException {
		try {
			Sequence sequence = new Sequence(Sequence.PPQ, playedSong.getResolution());

			int i=-1;
			for (SongTrack part : playedSong.getTracks()) {
				Track track = sequence.createTrack();

                // TODO: 29/10/17 Generalizar a todos los mensajes posibles e instantes
                if (resetEWSCWordBuilderMessage) {
                    ShortMessage resetWBMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, 21, 127);
                    MidiEvent event = new MidiEvent(resetWBMessage, 0);
                    track.add(event);
                }

                i++;
				if (i==0) {

					for (Meter ts: playedSong.getMeters()) {
						putTimeSignatureChange(track, ts, ts.getTime());
					}

					for (Key ks: playedSong.getKeys()) {
						putKeySignature(track, ks, ks.getTime());
					}

					for (Tempo tp: playedSong.getTempoChanges()) {
						putTempoChange(track, tp, tp.getTime());
					}					
				}
				int defaultChannel;
				if (part.isDefaultMidiChannelSet()) {
					defaultChannel = part.getDefaultMidiChannel()-1;
				} else {
					defaultChannel = i; //TODO Saltarse el canal 10 si es por defecto y comprobar que no estamos repitiendo canal 
				}
				
				String trackName = part.getName();
				if (trackName != null) {
					putTrackName(track, trackName);
				}
				for (PlayedNote note : part.getPlayedNotes()) {
					int channel = note.getMidiChannel();
					if (channel == -1) {
						channel = defaultChannel;
					}
					//System.out.println("Ctime="+note.getTime()+" midi="+note.getMidiNote() + " dur "+ note.getDurationInTicks());
					ShortMessage messageON = new ShortMessage();
//					TODO Javi velocity siempre 64						
					int vel = note.getVelocity();
					messageON.setMessage(ShortMessage.NOTE_ON, channel, note.getMidiPitch(), vel==0?127:vel);  
					track.add(new MidiEvent(messageON, note.getTime()));
					
					ShortMessage messageOFF = new ShortMessage();
					messageOFF.setMessage(ShortMessage.NOTE_OFF, channel, note.getMidiPitch(), 0); 
					track.add(new MidiEvent(messageOFF, note.getTime() + note.getDurationInTicks()));
				
					String lyrics = note.getText();
					if (lyrics != null) {
						putLyric(track, lyrics, note.getTime());
					}
				}
			}
			
			/*20170327 Track track = sequence.createTrack();
			for (Harmony h : playedSong.getHarmonies()) {
				putLyric(track, h.getShortDescription(), h.getTime());
			} */
			
			if (MidiSystem.getMidiFileTypes(sequence).length == 0) {
				throw new ExportException("No MIDI file types supported");
			}
			MidiSystem.write(sequence, MidiSystem.getMidiFileTypes(sequence)[0], file);
			
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			throw new ExportException(e);
		} catch (IOException e) {
			throw new ExportException(e);
		} 
	}

    // TODO: 29/10/17 Generalizar esto
    public void addResetEWSCWordBuilderMessage() {
        resetEWSCWordBuilderMessage = true;
    }
}
