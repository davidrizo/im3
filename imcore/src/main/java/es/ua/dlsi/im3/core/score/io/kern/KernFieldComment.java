package es.ua.dlsi.im3.core.score.io.kern;

import java.util.Objects;

/**
 * This instruction is not present in IM3, this is why we import it here
 * @autor drizo
 */
public class KernFieldComment {
    String comment;

    public KernFieldComment(String instrument) {
        this.comment = instrument;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KernFieldComment)) return false;
        KernFieldComment that = (KernFieldComment) o;
        return Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment);
    }

    @Override
    public String toString() {
        return comment;
    }
}
