package es.ua.dlsi.im3.core.played;

public class Key {
	public enum Mode {MAJOR, MINOR};
	
	private Mode mode;
	/**
	 * 0 = C major or A minor, negative = flats, positive = sharps
	 */
	private int fifths;
	
	long time;

	public Key(int sf, Mode mode) {
		this.fifths = sf;
		this.mode = mode;
	}

	public final Mode getMode() {
		return mode;
	}

	public final void setMode(Mode mode) {
		this.mode = mode;
	}

	public final int getFifths() {
		return fifths;
	}

	public final void setFifths(int sf) {
		this.fifths = sf;
	}

	public final long getTime() {
		return time;
	}

	public final void setTime(long time) {
		this.time = time;
	}

	public void overwriteWith(Key key) {
		this.time = key.time;
		this.fifths = key.fifths;
		this.mode = key.mode;		
	}

}
