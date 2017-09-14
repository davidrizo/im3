package es.ua.dlsi.im3.core.score.mensural.meters;

/**
 * Visually rendered as C with a dot inside
 * 1 breve = 2 semibreves, 1 semibreve = 3 minim
 */
public class TempusImperfectumCumProlationePerfecta extends TimeSignatureMensural {
    public TempusImperfectumCumProlationePerfecta() {
        super(Perfection.imperfectum, Perfection.perfectum);
    }
}