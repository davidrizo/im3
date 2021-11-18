package es.ua.dlsi.im3.core;

import java.util.UUID;

/**
 * Identifier generation
 */
public class IDGenerator {
	private static long nextID = 1;
	private static boolean useRandomUUID = false;

	public static String getID() {
		if (useRandomUUID) {
			return UUID.randomUUID().toString();
		} else {
			long id = nextID;
			nextID++;
			return Long.toString(id);
		}
	}
	
	private IDGenerator() {}

	public static boolean isUseRandomUUID() {
		return useRandomUUID;
	}

	public static void setUseRandomUUID(boolean useRandomUUID) {
		IDGenerator.useRandomUUID = useRandomUUID;
	}
}
