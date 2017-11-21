package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.score.ScoreSong;

/**
 * It converts a played (MIDI like) song to a score representation. For each
 * non-empty track a score part is built. For each voice in the track a staff in
 * the score part is created.
 * @author drizo
 */
public interface IPlayedSongToScoreSongConverter {
    ScoreSong createScoreSongFromPlayed(PlayedSong played, IVoiceSeparator voiceSeparator, IDurationTranslator durationTranslator, IPitchSpelling pitchSpelling) throws IM3Exception;
}
