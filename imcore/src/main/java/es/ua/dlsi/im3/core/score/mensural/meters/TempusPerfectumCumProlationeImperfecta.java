package es.ua.dlsi.im3.core.score.mensural.meters;

/**
 * Visually rendered as O
 * 1 breve = 3 semibreves, 1 semibreve = 2 minim
 */
public class TempusPerfectumCumProlationeImperfecta extends TimeSignatureMensural {
    public TempusPerfectumCumProlationeImperfecta() {
        super(Perfection.perfectum, Perfection.imperfectum);
    }
}
