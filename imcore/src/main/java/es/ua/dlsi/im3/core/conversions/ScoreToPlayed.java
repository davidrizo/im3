package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.played.PlayedNote;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.SongTrack;
import es.ua.dlsi.im3.core.score.*;

/**
 *
 * @author drizo
 */
public class ScoreToPlayed {
	
    public void createSongTrack(ScorePart part, SongTrack track) throws IM3Exception {
        if (part.isEmpty()) {
            throw new IM3Exception("Cannot convert to played notes from empty score");
        }
        track.setName(part.getName());
        int resolution = track.getPlayedSong().getResolution();
        for (ScoreLayer voice : part.getLayers()) {
            for (AtomPitch atomPitch : voice.getAtomPitches()) {
                //TODO Lyrics
                PlayedNote pn = new PlayedNote(atomPitch.getScientificPitch().computeMidiPitch(),
                        (long) (atomPitch.getAtomFigure().getComputedDuration() * resolution));

                pn.setScientificPitch(atomPitch.getScientificPitch());
                track.addNote((long) atomPitch.getTime().multiply(track.getPlayedSong().getResolution()).getComputedTime(), pn);

            }
        }
    }

    public PlayedSong createPlayedSongFromScore(ScoreSong score) throws IM3Exception {
    		return createPlayedSongFromScore(PlayedSong.DEFAULT_RESOLUTION, score);
    }
    /**
     *
     * @throws IM3Exception
     */
    public PlayedSong createPlayedSongFromScore(int resolution, ScoreSong score) throws IM3Exception {
        if (score == null) {
            throw new IM3Exception("Score is null");
        }
        PlayedSong played = new PlayedSong(resolution);

        //TODO URGENT Pasar las tonalidades, tempos y time signatures
        /*for (Key ks : score.getKeys()) {
            played.addKey(ks.getTime(), (Key) ks.clone());
        }

        for (Meter ts : score.getMeters()) {
            played.addMeter(ts.getTime(), (Meter) ts.clone());
        }

        for (Tempo ts : score.getTempoChanges()) {
            played.addTempoChange(ts.getTime(), (Tempo) ts.clone());
        }*/

        for (ScorePart part : score.getParts()) {
            SongTrack track = played.addTrack();
            createSongTrack(part, track);
        }
        return played;
    }

}
