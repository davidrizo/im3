package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.played.*;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.Key;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

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
            PlayedNote prevNote = null;
            for (AtomPitch atomPitch : voice.getAtomPitches()) {
                long duration = (long) (atomPitch.getAtomFigure().getComputedDuration() * resolution);
                int midiPitch = atomPitch.getScientificPitch().computeMidiPitch();
                if (atomPitch.isTiedFromPrevious()) {
                    if (prevNote == null) {
                        throw new IM3Exception("Tied note without a previous one");
                    }
                    if (prevNote.getMidiPitch() != midiPitch) {
                        throw new IM3Exception("Tied note without a different midi pitch: " + midiPitch + " vs. " + prevNote.getMidiPitch());
                    }
                    prevNote.setDurationInTicks(prevNote.getDurationInTicks() + duration);
                } else {
                    //TODO Lyrics
                    PlayedNote pn = new PlayedNote(midiPitch, duration);
                    pn.setScientificPitch(atomPitch.getScientificPitch());
                    track.addNote((long) atomPitch.getTime().multiply(track.getPlayedSong().getResolution()).getComputedTime(), pn);
                    prevNote = pn;
                }
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

        // first create a global track to add keys, meters, tempos...
        SongTrack globalTrack = played.addTrack();
        createGlobalTrack(played, globalTrack, score);

        for (ScorePart part : score.getParts()) {
            SongTrack track = played.addTrack();
            createSongTrack(part, track);
        }
        return played;
    }

    private void createGlobalTrack(PlayedSong playedSong, SongTrack track, ScoreSong score) throws IM3Exception {
        LinkedList<IPlayedEvent> events = new LinkedList<>();

        TreeSet<KeySignature> scoreKeys = new TreeSet<>(new Comparator<KeySignature>() {
            @Override
            public int compare(KeySignature o1, KeySignature o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
        TreeSet<TimeSignature> scoreTimeSignatures = new TreeSet<>(new Comparator<TimeSignature>() {
            @Override
            public int compare(TimeSignature o1, TimeSignature o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });

        for (ScorePart part : score.getParts()) {
            for (Staff staff: part.getStaves()) {
                scoreKeys.addAll(staff.getKeySignatures());
                scoreTimeSignatures.addAll(staff.getTimeSignatures());
            }
        }

        for (KeySignature scoreKeySignature: scoreKeys) {
            es.ua.dlsi.im3.core.played.Key playedKey = convert(playedSong.getResolution(), scoreKeySignature);
            events.add(playedKey);
        }

        for (TimeSignature scoreTimeSignature: scoreTimeSignatures) {
            es.ua.dlsi.im3.core.played.Meter playedMeter = convert(playedSong.getResolution(), scoreTimeSignature);
            events.add(playedMeter);
        }

        events.sort(new Comparator<IPlayedEvent>() {
            @Override
            public int compare(IPlayedEvent o1, IPlayedEvent o2) {
                if (o1.getTime() - o2.getTime() < 0) {
                    return -1;
                } else if (o1 == o2) {
                    return 0; // just equals if same object
                } else {
                    return 1;
                }
            }
        });

        for (IPlayedEvent event: events) {
            if (event instanceof es.ua.dlsi.im3.core.played.Key) {
                playedSong.addKey(event.getTime(), (es.ua.dlsi.im3.core.played.Key) event);
            } else if (event instanceof Meter) {
                playedSong.addMeter(event.getTime(), (Meter) event);
            } else {
                throw new IM3Exception("Invalid event type: " + event.getClass());
            }
        }
    }

    private es.ua.dlsi.im3.core.played.Key convert(int resolution, KeySignature scoreKeySignature) throws IM3Exception {
        es.ua.dlsi.im3.core.played.Key.Mode mode;
        Key scoreKey = scoreKeySignature.getConcertPitchKey();
        if (scoreKey.getMode() == Mode.MINOR) {
            mode = es.ua.dlsi.im3.core.played.Key.Mode.MINOR;
        } else {
            mode = es.ua.dlsi.im3.core.played.Key.Mode.MAJOR;
        }
        es.ua.dlsi.im3.core.played.Key result = new es.ua.dlsi.im3.core.played.Key(scoreKey.getFifths(), mode);
        result.setTime((long) scoreKeySignature.getTime().multiply(resolution).getComputedTime());
        return result;
    }

    private Meter convert(int resolution, TimeSignature scoreTS) throws IM3Exception {
        int num;
        int den;

        if (scoreTS instanceof TimeSignatureCommonTime) {
            num = 4;
            den = 4;
        } else if (scoreTS instanceof TimeSignatureCutTime) {
            num = 2;
            den = 2;
        } else if (scoreTS instanceof FractionalTimeSignature) {
            FractionalTimeSignature fts = (FractionalTimeSignature) scoreTS;
            num = fts.getNumerator();
            den = fts.getDenominator();
        } else {
            throw new IM3Exception("Invalid time signature type: " + scoreTS.getClass());
        }
        Meter result = new Meter(num, den);
        result.setTime((long) scoreTS.getTime().multiply(resolution).getComputedTime());
        return result;
    }
}
