/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.played.io;

/**
 * @author david
 */
public interface IMidiConstants {
	/**
	 * Tempo byte
	 */
	int TEMPO = 81;
	/**
	 * Program byte
	 */
	int PROGRAM = 192;
	/**
	 * PlayedNote on byte
	 */
	int NOTEON = 144;
	/**
	 * PlayedNote off byte
	 */
	int NOTEOFF = 128;
	/**
	 * Sustain byte
	 */
	int SUSTAIN = 64;
	/**
	 * Reverb byte
	 */
	int REVERB = 91;
	/**
	 * Meter byte
	 */
	int METER = 88;
	/**
	 * Key signature
	 */
	int KEY_SIGNATURE = 0x59;
	/**
	 * Track name
	 */
	int TRACK_NAME = 0x03;
	/**
	 * Lyrics
	 */
	int LYRICS = 0x05;
}
