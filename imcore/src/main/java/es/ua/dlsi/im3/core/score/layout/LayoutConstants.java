package es.ua.dlsi.im3.core.score.layout;

public class LayoutConstants {
    /**
     * Usually unitsPerEM are 2048 or 1000
     */
    public static final double UNITS_PER_EM = 20480;
    /**
     * Number of pixels an EM takes by default
     */
    public static final double EM = 36;

    /**
     * Defined as SMuFL
     */
    public static final double SPACE_HEIGHT = EM/4;
    // TODO: 16/9/17  ¿Debe ser igual a EM?
    public static final float FONT_SIZE = (float) EM;
    public static final float PDF_LEFT_MARGIN = 20;
    public static final float PDF_TOP_MARGIN = 40;
    public static final double TOP_MARGIN = PDF_TOP_MARGIN;
    public static final double STAFF_SEPARATION = EM*2;
    /**
     * Separation between the note head and the dot
     */
    public static final double DOT_SEPARATION = EM/4;
    /**
     * Separation between the accidental and the note head
     */
    public static final double ACCIDENTAL_HEAD_SEPARATION = EM/8;
    /**
     * The extra to be added to the head size
     */
    public static final double LEDGER_LINE_EXCESS_OVER_NOTE_HEAD = EM/16;
}
