package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.played.SongTrack;
import es.ua.dlsi.im3.core.score.Atom;

import java.util.List;

public interface IVoiceSeparator {
    /**
     * @param track
     * @return List of voices, where each voice is a list of atoms
     * @throws IM3Exception
     */
    List<List<Atom>> separateVoices(SongTrack track) throws IM3Exception;
}
