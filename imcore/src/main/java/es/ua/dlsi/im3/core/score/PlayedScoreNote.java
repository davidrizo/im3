package es.ua.dlsi.im3.core.score;

import java.util.ArrayList;
import java.util.List;

/**
 * The note that should be played computed from any score symbol
 * @author drizo
 *
 */
public class PlayedScoreNote implements Comparable<PlayedScoreNote> {
	List<AtomPitch> correspondsToAtomPitches;
	ScientificPitch pitch;
	Time onset;
	Time duration;
	public PlayedScoreNote(AtomPitch correspondsToPitch, ScientificPitch pitch, Time onset, Time duration) {
		super();
		correspondsToAtomPitches = new ArrayList<>();
		correspondsToAtomPitches.add(correspondsToPitch);
		this.pitch = pitch;
		this.onset = onset;
		this.duration = duration;
	}
	/**
	 * Package visibility
	 */
	void addCorrespondingPitch(AtomPitch ap) {
		correspondsToAtomPitches.add(ap);
	}
	/**
	 * Package visibility
	 */
	void addCorrespondingPitches(List<AtomPitch> aps) {
		correspondsToAtomPitches.addAll(aps);
	}
	public final ScientificPitch getPitch() {
		return pitch;
	}
	public final Time getOnset() {
		return onset;
	}
	public final Time getDuration() {
		return duration;
	}
	public final List<AtomPitch> getCorrespondsToAtomPitches() {
		return correspondsToAtomPitches;
	}
	@Override
	public int compareTo(PlayedScoreNote o) {
		int diff = onset.compareTo(o.onset);
		if (diff == 0) {
			diff = pitch.compareTo(o.pitch);
			if (diff == 0) {
				diff = duration.compareTo(o.duration);
			}
		}
		return diff;
	}
	@Override
	public String toString() {
		return "PlayedScoreNote [onset=" + onset + ", pitch=" + pitch + ", duration=" + duration + "]";
	}
	
	
}
