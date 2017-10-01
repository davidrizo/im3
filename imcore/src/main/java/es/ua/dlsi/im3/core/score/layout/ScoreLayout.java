package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreBarline;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LayoutSlur;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NotePitch;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.layout.layoutengines.BelliniLayoutEngine;

import java.util.*;

/**
 * It contains staves that can be split in several. Symbols like
 * slurs may also be splitted into two parts
 * @author drizo
 *
 */
public abstract class ScoreLayout {
    protected final ScoreSong scoreSong;
    protected final LayoutFont layoutFont;
    protected final LayoutSymbolFactory layoutSymbolFactory;
    protected final Simultaneities simultaneities;
    protected final double noteHeadWidth;
    protected HashMap<Staff, List<LayoutCoreSymbolInStaff>> coreSymbolsInStaves;
    /**
     * Used for building connectors
     */
    HashMap<AtomPitch, NotePitch> layoutPitches;

    protected final List<LayoutCoreBarline> barlines;
    protected final List<LayoutConnector> connectors;


    public ScoreLayout(ScoreSong song, LayoutFonts font) throws IM3Exception { //TODO ¿y si tenemos que sacar sólo unos pentagramas?
        this.scoreSong = song;
        layoutFont = FontFactory.getInstance().getFont(font);
        layoutSymbolFactory = new LayoutSymbolFactory();
        simultaneities = new Simultaneities();
        barlines = new ArrayList<>();
        connectors = new ArrayList<>();

        Pictogram noteHead = new Pictogram("_NHWC_", getLayoutFont(), NotePitch.NOTE_HEAD_WIDTH_CODEPOINT, // TODO: 22/9/17 Quizás esto debería ser cosa del FontLayout
                new Coordinate(new CoordinateComponent(0),
                        new CoordinateComponent(0)
                ));
        noteHeadWidth = noteHead.getWidth();
        
        createLayoutSymbols();
    }

    private void createLayoutSymbols() throws IM3Exception {
        coreSymbolsInStaves = new HashMap<>();
        layoutPitches = new HashMap<>();
        for (Staff staff: scoreSong.getStaves()) {
            ArrayList<LayoutCoreSymbolInStaff> coreSymbolsInStaff = new ArrayList<>();
            coreSymbolsInStaves.put(staff, coreSymbolsInStaff);
            // add contents of staff
            List<ITimedElementInStaff> symbols = staff.getCoreSymbolsOrdered();
            for (ITimedElementInStaff symbol: symbols) {
                LayoutCoreSymbol layoutCoreSymbol = layoutSymbolFactory.createCoreSymbol(layoutFont, symbol);
                //createLayout(symbol, layoutStaff);
                if (layoutCoreSymbol != null) {
                    simultaneities.add(layoutCoreSymbol);
                    if (layoutCoreSymbol instanceof LayoutCoreSymbolInStaff) {
                        coreSymbolsInStaff.add((LayoutCoreSymbolInStaff) layoutCoreSymbol);
                    } else {
                        throw new IM3RuntimeException("Unimplemented " + layoutCoreSymbol.getClass()); // TODO: 24/9/17 Debemos ponerlos en otra lista? Beamed groups?
                    }

                    //TODO Esto no me gusta
                    if (layoutCoreSymbol instanceof LayoutCoreSingleFigureAtom) {
                        LayoutCoreSingleFigureAtom sfa = (LayoutCoreSingleFigureAtom) layoutCoreSymbol;
                        for (NotePitch notePitch: sfa.getNotePitches()) {
                            layoutPitches.put(notePitch.getAtomPitch(), notePitch);
                        }
                    }
                }
            }

            // create barlines
            // TODO: 21/9/17 Deberíamos poder crear barlines de system
            for (Measure measure: scoreSong.getMeasures()) {
                LayoutCoreBarline barline = new LayoutCoreBarline(layoutFont, measure.getEndTime());
                simultaneities.add(barline);
                barlines.add(barline);
            }

            createConnectors();
            
            //layoutStaff.createBeaming();
            //System.out.println("Staff " + staff.getNumberIdentifier());
            //simultaneities.printDebug();
        }
    }

    /**
     * This must be implemented in the different layout. A connector, e.g. a slur, could be split between staves
     */
    protected void createConnectors() throws IM3Exception {
// TODO: 1/10/17 Implementarlo en hijos, una ligadura puede que haya que partirla
        NotePitch previousNotePitch = null;

        for (Map.Entry<Staff, List<LayoutCoreSymbolInStaff>> entry: this.coreSymbolsInStaves.entrySet()) {
            for (LayoutCoreSymbolInStaff coreSymbolInStaff: entry.getValue()) {
                if (coreSymbolInStaff instanceof LayoutCoreSingleFigureAtom) {
                    LayoutCoreSingleFigureAtom lcsfa = (LayoutCoreSingleFigureAtom) coreSymbolInStaff;
                    for (NotePitch notePitch: lcsfa.getNotePitches()) {
                        // create ties
                        if (notePitch.getAtomPitch().isTiedFromPrevious()) {
                            if (notePitch.getAtomPitch().getTiedFromPrevious() != previousNotePitch.getAtomPitch()) {
                                throw new IM3Exception("Atom pitches to tie are not the same");
                            }

                            LayoutSlur tie = new LayoutSlur(previousNotePitch, notePitch);
                            addConnector(tie);
                        }

                        // create slurs and other connectors
                        Collection<Connector> atomPitchConnectors = notePitch.getAtomPitch().getConnectors();
                        if (atomPitchConnectors != null) {
                            for (Connector connector: atomPitchConnectors) {
                                // just create "to" connectors to avoid duplicate in both directions
                                if (connector.getTo() == notePitch.getAtomPitch()) {
                                    // TODO: 1/10/17 Factory como LayoutCoreSymbol para connectors

                                    NotePitch from = layoutPitches.get(connector.getFrom());
                                    if (from == null) {
                                        throw new IM3Exception("Cannot find a NotePitch for AtomPitch " + connector.getFrom() + " for connector " + connector);
                                    }

                                    if (connector instanceof Slur) {
                                        LayoutSlur layoutSlur = new LayoutSlur(from, notePitch);
                                        addConnector(layoutSlur);
                                    } else {
                                        System.err.println("CONNECTOR NON SUPPORTED: " + connector.getClass());
                                    }
                                }
                            }
                        }


                        previousNotePitch = notePitch;
                    }
                }
            }
        }
    }

    protected void doHorizontalLayout(Simultaneities simultaneities) throws IM3Exception {
        // Replace for a factory if required
        ILayoutEngine layoutEngine = new BelliniLayoutEngine(1, 1, noteHeadWidth/2); // TODO: 22/9/17 ¿qué valor ponemos?
        layoutEngine.reset(simultaneities);
        layoutEngine.doHorizontalLayout(simultaneities);
    }


    public abstract void layout() throws IM3Exception;
    public abstract List<Canvas> getCanvases();

    public LayoutFont getLayoutFont() {
        return layoutFont;
    }

    protected void addConnector(LayoutSlur connector) {
        this.connectors.add(connector);
    }


}
