package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @autor drizo
 */
public enum  RestFigures {
longa, breve, whole, half, seminima, quarter , eighth , eighth_cut , eighth_void , sixteenth , fusa , semifusa , thirty_second , sixty_fourth , hundred_twenty_eighth , two_hundred_fifty_six;

    public String toAgnosticString() {
        return this.name();
    }
}
