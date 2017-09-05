package es.ua.dlsi.im3.core.score.io.harmony;

import es.ua.dlsi.im3.core.score.harmony.Harm;

/**
 * Created by drizo on 23/6/17.
 */
public class HarmonyExporter {
    public String exportKey(Harm harm) {
        StringBuilder sb = new StringBuilder();

        sb.append(harm.getKey().getAbbreviationString()); //TODO hacer el abbreviation string aquí

        if (harm.getAlternate() != null) {
            sb.append('[');
            sb.append(harm.getAlternate().getKey().getAbbreviationString()); //TODO hacer el abbreviation string aquí
            sb.append(']');
        }

        return sb.toString();
    }

    public String exportTonalFunction(Harm harm) {
        StringBuilder sb = new StringBuilder();

        sb.append(harm.getTonalFunction().getAbbr());

        if (harm.getAlternate() != null) {
            sb.append('[');
            sb.append(harm.getAlternate().getTonalFunction().getAbbr());
            sb.append(']');
        }

        return sb.toString();
    }
}
