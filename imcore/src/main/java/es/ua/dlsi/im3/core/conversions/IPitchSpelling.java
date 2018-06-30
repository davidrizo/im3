package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.played.Key;
import es.ua.dlsi.im3.core.score.ScientificPitch;

public interface IPitchSpelling {
    ScientificPitch computePitchSpelling(Key key, int midiPitch, boolean isLastNote, ScientificPitch previousScientificPitch);
}
