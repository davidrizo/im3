package es.ua.dlsi.im3.core.score.io.kern;

import java.util.Objects;

/**
 * This instruction is not present in IM3, this is why we import it here
 * @autor drizo
 */
public class KernInstrument {
    String instrument;

    public KernInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KernInstrument)) return false;
        KernInstrument that = (KernInstrument) o;
        return Objects.equals(instrument, that.instrument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrument);
    }

    @Override
    public String toString() {
        return instrument;
    }
}
