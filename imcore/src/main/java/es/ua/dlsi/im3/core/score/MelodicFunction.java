package es.ua.dlsi.im3.core.score;

/**
 * Based on MEI mfunc attribute, mfwhich is based in turn in Humdrum **embel syntax.
 * Created by drizo on 14/6/17.
 */
public enum MelodicFunction {
    /**
     * Accented lower neighbor
     */
    mfALN,
    /**
     * Anticipation
     */
    mfANT,
    /**
     * Appogiatura
     */
    mfAPP,
    /**
     * Accented passing tone
     */
    mfAPT,
    /**
     * Arpeggio tone (chordal tone)
     */
    mfARP,
    /**
     * Arpeggio tone (7th added to the chord)
     */
    mfARP7,
    /**
     * Accented upper neighbor
     */
    mfAUN,
    /**
     * Changing tone
     */
    mfCHG,
    /**
     * Chromatic lower neighbor
     */
    mfCLN,
    /**
     * Chord tone (i.e., not an embellishment).
     */
    mfCT,
    /**
     * Chord tone (7th added to the chord)
     */
    mfCT7,
    /**
     * Chromatic upper neighbor
     */
    mfCUN,
    /**
     * Chromatic unaccented passing tone
     */
    mfCUP,
    /**
     * Escape tone
     */
    mfET,
    /**
     * Lower neighbor
     */
    mfLN,
    /**
     * Pedal tone
     */
    mfPED,
    /**
     * Repeated tone
     */
    mfREP,
    /**
     * Retardation
     */
    mfRET,
    /**
     * 2-3 retardation
     */
    mf23RET,
    /**
     * 7-8 retardation
     */
    mf78RET,
    /**
     * Suspension
     */
    mfSUS,
    /**
     * 4-3 suspension
     */
    mf43SUS,
    /**
     * 7-6 suspension
     */
    mf76SUS,
    /**
     * 9-8 suspension
     */
    mf98SUS,
    /**
     * Upper neighbor
     */
    mfUN,
    /**
     * Upper neighbor (7th added to the chord)
     */
    mfUN7,
    /**
     * Unaccented passing tone
     */
    mfUPT,
    /**
     * Unaccented passing tone (7th added to the chord)
     */
    mfUPT7
}
