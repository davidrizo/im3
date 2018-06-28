package es.ua.dlsi.im3.core.score.io.kern;

import java.util.Objects;

/**
 * @autor drizo
 */
public class KernText {
    String text;

    public KernText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KernText)) return false;
        KernText kernText = (KernText) o;
        return Objects.equals(text, kernText.text);
    }

    @Override
    public int hashCode() {

        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return text;
    }
}
