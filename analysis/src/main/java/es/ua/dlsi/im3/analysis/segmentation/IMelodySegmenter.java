/*
 * Created on 17-nov-2004
 */
package es.ua.dlsi.im3.analysis.segmentation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.played.SongTrack;
import es.ua.dlsi.im3.core.score.Segment;

import java.util.List;

/**
 * All classes that segment a melody must implement this interface
 * @author drizo
 */
public interface IMelodySegmenter {
	/**
	 * It returns the notes that start phrases
	 * @param track Monophonic voice that contains the melody to be segmentated
	 * @return An list of Segments
	 * @throws IM3Exception
	 */
	List<Segment> segmentate(SongTrack track) throws IM3Exception;
}
