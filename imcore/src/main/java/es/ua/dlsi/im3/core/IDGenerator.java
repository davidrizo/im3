package es.ua.dlsi.im3.core;

/**
 * Identifier generation
 */
public class IDGenerator {
	private static long nextID = 1;
	
	public static long getID() {
		long id = nextID;
		nextID++;
		return id;
	}
	
	private IDGenerator() {}
}
