package es.ua.dlsi.im3.midrepresentations.sequences;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.played.PlayedNote;
import es.ua.dlsi.im3.core.played.SongTrack;

import java.util.ArrayList;
//TODO Test unitario
/**
 * It computes several mid representations from a monodic source
 */
public class MultipleMidRepresentationsFromSongTrackExtractor {
    private final ArrayList<Integer> pitchIntervals;
    private final ArrayList<Long> IOIs;
    private final ArrayList<Double> IORs;

    public MultipleMidRepresentationsFromSongTrackExtractor(SongTrack track) throws IM3Exception {
        if (!track.isMonophonic()) {
            throw new IM3Exception("The track is not monophonic");
        }

        pitchIntervals = new ArrayList<>();
        IOIs = new ArrayList<>();

        PlayedNote lastNote = null;
        for (PlayedNote note: track.getPlayedNotes()) {
            int interval;
            long ioi;
            if (lastNote == null) {
                interval = 0;
                ioi = 0;
            } else {
                interval = note.getMidiPitch() - lastNote.getMidiPitch();
                ioi = note.getTime() - lastNote.getTime();
            }
            IOIs.add(ioi);
            pitchIntervals.add(interval);
            lastNote = note;
        }
        /*if (lastNote != null) {
            IOIs.add(lastNote.getDurationInTicks());
        }*/

        IORs = new ArrayList<>();

        Double lastIOI = null;
        for (long ioi: IOIs) {
            double ior;
            if (lastIOI == null) {
                ior = 0;
            } else {
                ior = lastIOI / (double)ioi;
            }

            IORs.add(ior);
            lastIOI = (double)ioi;
        }
    }

    public ArrayList<Integer> getPitchIntervals() {
        return pitchIntervals;
    }

    public ArrayList<Long> getIOIs() {
        return IOIs;
    }

    public ArrayList<Double> getIORs() {
        return IORs;
    }
}
