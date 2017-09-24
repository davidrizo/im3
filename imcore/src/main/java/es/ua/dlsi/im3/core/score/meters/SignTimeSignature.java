package es.ua.dlsi.im3.core.score.meters;

import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.TimeSignature;

/**
 * A time signature specified with just a sign. This is a convenience class for the layout factories
 */
public abstract class SignTimeSignature extends TimeSignature {
    public SignTimeSignature(NotationType notationType) {
        super(notationType);
    }

    /**
     * A kind of toString just for the sign
     * @return
     */
    public abstract String getSignString();
}
