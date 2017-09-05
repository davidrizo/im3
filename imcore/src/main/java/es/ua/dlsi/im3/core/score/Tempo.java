/*
 * Created on 10-ene-2004
 *//*
	* Created on 10-ene-2004
	*/
package es.ua.dlsi.im3.core.score;

//TODO Unificar esto con Tempo de Played. Hacer algo parecido con Key y Meter

/**
 * @author david
 */
public class Tempo implements ITimedElement, Comparable<Tempo>, IUniqueIDObject {
	//FRACCIONES ScoreSong song;

	private Time time;
	private String ID;

	/**
	 * microseconds Per midi-Quarters
	 *
	 * @return the microsecondsPermidiQuarters
	 */
	public final long getMicrosecondsPermidiQuarters() {
		return 60000000L / (long) tempo;
	}

	/**
	 * Tempo
	 */
	private int tempo;
	private ScoreSong song;

	/**
	 * Constructor
	 *
	 * @param tick
	 * @param atempo
	 */
	public Tempo(int atempo) {
		this.tempo = atempo;
		this.time = new Time();
	}

	/**
	 * Getter
	 *
	 * @return Tempo
	 */
	public int getTempo() {
		return tempo;
	}

	/**
	 * Setter
	 *
	 * @param f
	 *            tempo
	 */
	public void setTempo(int f) {
		tempo = f;
	}

	/**
	 * toString methode: creates a String representation of the object
	 *
	 * @return the String representation
	 * @author info.vancauwenberge.tostring plugin
	 *
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Tempo[");
		buffer.append("tempo = ").append(tempo);
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Tempo)) {
			System.err.println("The other object is not a Tempo in equals");
		}
		Tempo other = (Tempo) arg0;
		return this.tempo == other.tempo;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 83 * hash + this.tempo;
		return hash;
	}

	/**
	 * * It does not take into account the time
	 *
	 * @param o
	 * @return If the compared element is not a tempo and they onset in the same
	 *         time, the result is always -1
	 */
	@Override
	public int compareTo(Tempo o) {
		return (int) (this.tempo - o.tempo);
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
	/*FRACCIONES public long millisecondsToTicksJustTempo(long milliseconds) {

		long ticks = (long) ((double) milliseconds * (double) ScoreSong.DEFAULT_RESOLUTION * 1000.0
				/ (double) this.getMicrosecondsPermidiQuarters());
		return ticks;
	}*/

	/**
	 * It only computes the equivalence of milliseconds - ticks without taking
	 * into account previous tempo changes. In order to get the correct
	 * milliseconds use the song method ticksToMilliseconds that uses all tempo
	 * change information
	 *
	 * @param resolution
	 * @param tickCount
	 * @return
	 */
	/*FRACCIONES Habrá que ver qué relación hacemos public long ticksToMilliseconds(long tickCount) {
		return ((long) (((double) (tickCount) * (double) (this.getMicrosecondsPermidiQuarters()))
				/ ((double) ScoreSong.DEFAULT_RESOLUTION * 1000.0)));
	}*/

	@Override
	public Tempo clone() {
		return new Tempo(this.tempo);
	}

	@Override
	public String __getID() {
		return ID;
	}

	@Override
	public void __setID(String id) {
		this.ID = id;
	}

	@Override
	public String __getIDPrefix() {
		return "T";
	}

	@Override	
	public Time getTime()  {
		return this.time;
	}
	
	public final void setTime(Time time) {
		this.time = time;
	}

	public void setSong(ScoreSong ScoreSong) {
		this.song = ScoreSong;		
	}

	public final ScoreSong getSong() {
		return song;
	}
	
	
}
