package io;

import java.util.ArrayList;

public class Console {
	public enum LOGMODE {
		GRAPH, PACKET
	}

	private static ArrayList<LOGMODE> logmodes;

	static {
		logmodes = new ArrayList<>();
	}

	public static void activate(LOGMODE e) {
		logmodes.add(e);
	}

	public static void log(String message, LOGMODE e) {
		if (logmodes.contains(e)) System.out.println("[" + System.currentTimeMillis() + "] " + message);
	}
}
