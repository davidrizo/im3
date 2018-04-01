package es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * Created by drizo on 13/6/17.
 */
public class MeterStabilityRanks {
    /**
     * The lesser the value, the most stable or strong //TODO Generalizarlo -
     * podemos usar los �rboles m�tricos It is important that the order is held,
     * but also the absolute numbers can be shared between meters //TODO
     */
    static int[] NOTE_STABILITIES_4x4
            = {1, 9, 5, 13,
            3, 11, 7, 15,
            2, 10, 6, 14,
            4, 12, 8, 16};
    static int[] NOTE_STABILITIES_2x4
            = {1, 5, 3, 7,
            2, 6, 4, 8};
    static int[] NOTE_STABILITIES_3x4
            = {1, 7, 4, 10,
            2, 8, 5, 11,
            3, 9, 6, 12};
    static int[] NOTE_STABILITIES_6x8
            = {1, 5, 9,
            3, 7, 11,
            2, 6, 10,
            4, 8, 12};
    static int[] NOTE_STABILITIES_9x8
            = {1, 7, 13,
            4, 10, 16,
            2, 8, 14,
            5, 11, 17,
            3, 9, 15,
            6, 12, 18};
    static int[] NOTE_STABILITIES_12x8
            = {1, 9, 17,
            5, 13, 21,
            3, 11, 19,
            7, 15, 23,
            2, 10, 18,
            6, 14, 22,
            4, 12, 20,
            8, 16, 24};
    public static int MAX_INSTABILITY = 25;

    /**
     * It returns true for the strong beats of the meter
     *
     * @param beat
     * @return
     * @throws IM3Exception
     */
    public static int stabilityRank(int meterNumerator, double beat) throws IM3Exception {
        int semi;
        double f = beat * 4.0; // *4 because we use 16ths
        if (Math.floor(f) != f) {
            return MAX_INSTABILITY; // When note does not onset just in a sixteenth onset (e.g. it is the second 32th of a 32ths group)
        } else {
            semi = (int) f;
        }

        //URGENT - mejor que usar esto usar �rboles r�tmicos
        //System.out.println(beat + " -> semi " + semi);
        switch (meterNumerator) {
            case 4:
                return NOTE_STABILITIES_4x4[semi];
            case 3:
                return NOTE_STABILITIES_3x4[semi];
            case 2:
                return NOTE_STABILITIES_2x4[semi];
            case 12:
                return NOTE_STABILITIES_12x8[semi];
            case 9:
                return NOTE_STABILITIES_9x8[semi];
            case 6:
                return NOTE_STABILITIES_6x8[semi];
            default:
                throw new IM3Exception("The numerator " + meterNumerator + " is not implemented yet");
        }
    }

}
