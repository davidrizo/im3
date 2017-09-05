package es.ua.dlsi.im3.core.played;

public class Tempo {
	int tempo;
	long time;
	public Tempo(int value) {
		super();
		this.tempo = value;
	}
	public final int getTempo() {
		return tempo;
	}
	public final void setTempo(int value) {
		this.tempo = value;
	}
	public final long getTime() {
		return time;
	}
	public final void setTime(long time) {
		this.time = time;
	}
	/**
	 * microseconds Per midi-Quarters
	 *
	 * @return the microsecondsPermidiQuarters
	 */
	public final long getMicrosecondsPermidiQuarters() {
		return 60000000L / (long) tempo;
	}

	/**
	 *
	 * It only computes the equivalence of milliseconds - ticks without taking
	 * into account previous tempo changes
	 *
	 * @param resolution
	 * @param milliseconds
	 * @return
	 */
	public long millisecondsToTicksJustTempo(int resolution, long milliseconds) {

		long ticks = (long) ((double) milliseconds * resolution * 1000.0
				/ (double) this.getMicrosecondsPermidiQuarters());
		return ticks;
	}


}
