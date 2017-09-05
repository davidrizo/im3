package es.ua.dlsi.im3.core.played;

import java.util.HashMap;

import es.ua.dlsi.im3.core.score.ScientificPitch;

/**
 * @author drizo
 * @date 03/06/2011
 *
 */
public class PlayedNote implements Comparable<PlayedNote> {
	public static final int REST = 0;
	/**
	 * Time in ticks
	 */
	private long time;
	/**
	 * Absolute pitch
	 */
	int midiPitch;
	/**
	 * Duration in ticks
	 */
	protected long ticks;
	/**
	 * MIDI velocity
	 */
	private int velocity;
	/**
	 * MIDI channel
	 */
	private int midiChannel;
	/**
	 * Text accompanying the note
	 */
	private String text;

	/**
	 * The scientific pitch may be null. It is used when a pitch spelling
	 * algorithm is used or when the played song is created from a score song
	 */
	private ScientificPitch scientificPitch;

	public ScientificPitch getScientificPitch() {
		return scientificPitch;
	}

	public void setScientificPitch(ScientificPitch scientificPitch) {
		this.scientificPitch = scientificPitch;
	}

	/**
	 * @return the text
	 */
	public final String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public final void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the midiChannel
	 */
	public final int getMidiChannel() {
		return midiChannel;
	}

	/**
	 * @return the velocity
	 */
	public final int getVelocity() {
		return velocity;
	}

	/**
	 * @return the midiPitch
	 */
	/**
	 * It is inserted to the track
	 *
	 * @param midiPitch
	 * @param duration
	 */
	public PlayedNote(int midiPitch, long duration) {
		this.midiPitch = midiPitch;
		this.ticks = duration;
		decorations = new HashMap<>();
	}

	public final int getMidiPitch() {
		return midiPitch;
	}

	/**
	 * @param midiPitch
	 *            the midiPitch to set
	 */
	public final void setMidiPitch(int midiPitch) {
		this.midiPitch = midiPitch;
	}

	/**
	 * @param amount
	 */
	public final void extend(long amount) {
		this.ticks += amount;
	}

	@Override
	public int compareTo(PlayedNote o) {
		if (this.time < o.time) {
			return -1;
		} else if (this.time > o.time) {
			return 1;
		} else {
			return midiPitch - o.midiPitch;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PlayedNote [ midiPitch=" + midiPitch + ", duration (ticks)=" + ticks + "]";
	}

	public void setVelocity(int vel) {
		this.velocity = vel;
	}

	public void setMidiChannel(int midiChannel) {
		this.midiChannel = midiChannel;

	}

	/**
	 * @return the ticks
	 */
	public final long getDurationInTicks() {
		return ticks;
	}

	protected final void setTicks(long duration) {
		this.ticks = duration;
	}

	/**
	 * Not used, just here for legacy algorithms
	 *
	 * @return
	 */
	public boolean isSilence() {
		return this.midiPitch == REST;
	}

	public Integer getOctave() {
		return this.midiPitch % 12;
	}

	/**
	 * @return the decorations
	 */
	/**
	 * Decorations added at runtime <decoration kind, tempo>
	 */
	HashMap<Class<?>, INoteDecoration> decorations;

	public final HashMap<Class<?>, INoteDecoration> getDecorations() {
		return decorations;
	}

	public INoteDecoration getDecoration(Class<?> clazz) {
		if (decorations == null) {
			return null;
		}
		return decorations.get(clazz);
	}

	/**
	 *
	 * @param decoration
	 */
	public void addDecoration(INoteDecoration decoration) {
		if (decorations == null) {
			this.decorations = new HashMap<>();
		}
		this.decorations.put(decoration.getClass(), decoration);
	}

	public long getEndTime() {
		return time + ticks;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}
}
