package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Component;
import es.ua.dlsi.im3.core.score.layout.coresymbols.connectors.LayoutSlur;
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
    protected TreeSet<Staff> staves;
    protected LayoutSymbolFactory layoutSymbolFactory;
    protected Simultaneities simultaneities;
    protected HashMap<Staff, List<LayoutCoreSymbolInStaff>> coreSymbolsInStaves;
    protected HashMap<Staff, LayoutFont> layoutFonts;
    protected HashMap<Staff, Pictogram> noteHeads;
    protected HashMap<Staff, Double> noteHeadWidths;
    protected HashMap<Staff, LayoutStaff> layoutStaves;
    protected HashMap<BeamGroup, List<LayoutCoreSingleFigureAtom>> singleLayoutFigureAtomsInBeam;
    protected HashMap<Object, NotationSymbol> coreSymbolViews;
    /**
     * Used for building connectors
     */
    HashMap<ITimedSymbolWithConnectors, IConnectableWithSlur> layoutConnectorEnds;

    protected HashMap<Staff, List<LayoutCoreBarline>> barlines;
    protected List<LayoutConnector> connectors;
    protected List<LayoutBeamGroup> beams;


    // TODO: 20/11/17 mejor que el parámetro staves esté en el método abstract layout() y que toda la inicialización se haga ahí
    /**
     * @param song
     * @param font Same font for all staves
     * @throws IM3Exception
     */
    public ScoreLayout(ScoreSong song, Collection<Staff> staves, LayoutFonts font) throws IM3Exception { //TODO ¿y si tenemos que sacar sólo unos pentagramas?
        this.scoreSong = song;
        layoutFonts = new HashMap<>();
        initStaves(staves);
        for (Staff staff: staves) {
            LayoutFont layoutFont = FontFactory.getInstance().getFont(font);
            layoutFonts.put(staff, layoutFont);
        }
        init();
    }

    private void initStaves(Collection<Staff> staves) {
        this.staves = new TreeSet<>(new Comparator<Staff>() {
            @Override
            public int compare(Staff o1, Staff o2) {
                return o1.getHierarchicalOrder().compareTo(o2.getHierarchicalOrder());
            }
        });
        this.staves.addAll(staves);

    }

    public ScoreLayout(ScoreSong song, Collection<Staff> staves, HashMap<Staff, LayoutFonts> fonts) throws IM3Exception { //TODO ¿y si tenemos que sacar sólo unos pentagramas?
        this.scoreSong = song;
        layoutFonts = new HashMap<>();
        initStaves(staves);
        for (Staff staff: staves) {
            LayoutFonts font = fonts.get(staff);
            if (font == null) {
                throw new IM3Exception("Cannot find the staff " + staff + " in the parameter");
            }
            LayoutFont layoutFont = FontFactory.getInstance().getFont(font);
            layoutFonts.put(staff, layoutFont);
        }
        init();
    }


    private void init() throws IM3Exception {
        layoutSymbolFactory = new LayoutSymbolFactory();
        simultaneities = new Simultaneities();
        barlines = new HashMap<>();
        layoutStaves = new HashMap<>();
        connectors = new ArrayList<>();
        noteHeads = new HashMap<>();
        noteHeadWidths = new HashMap<>();

        for (Map.Entry<Staff, LayoutFont> layoutFontEntry: layoutFonts.entrySet()) {
            Staff staff = layoutFontEntry.getKey();
            LayoutFont layoutFont = layoutFontEntry.getValue();

            initWidths(staff, layoutFont);
        }
        createLayoutSymbols();
    }

    private void initWidths(Staff staff, LayoutFont layoutFont) throws IM3Exception {
        Pictogram noteHead = new Pictogram("_NHWC_", layoutFont, layoutFont.getFontMap().getUnicodeNoteHeadWidth(),
                new Coordinate(new CoordinateComponent(0),
                        new CoordinateComponent(0)
                ));
        double noteHeadWidth = noteHead.getWidth();
        noteHeads.put(staff, noteHead);
        noteHeadWidths.put(staff, noteHeadWidth);
    }

    public void addStaff(LayoutStaff layoutStaff, LayoutFont layoutFont) throws IM3Exception {
        initWidths(layoutStaff.getStaff(), layoutFont);
        layoutStaves.put(layoutStaff.getStaff(), layoutStaff);
        layoutFonts.put(layoutStaff.getStaff(), layoutFont);
    }

    private void createLayoutSymbols() throws IM3Exception {
        // TODO: 1/10/17 Beaming - parámetro para que se pueda deshabilitar
        //layoutStaff.createBeaming();
        coreSymbolViews = new HashMap<>();
        singleLayoutFigureAtomsInBeam = new HashMap<>();
        coreSymbolsInStaves = new HashMap<>();
        layoutConnectorEnds = new HashMap<>();
        for (Staff staff: staves) {
            ArrayList<LayoutCoreSymbolInStaff> coreSymbolsInStaff = new ArrayList<>();
            coreSymbolsInStaves.put(staff, coreSymbolsInStaff);
            // add contents of staff
            List<ITimedElementInStaff> symbols = staff.getCoreSymbolsOrdered();

            for (ITimedElementInStaff symbol: symbols) {
                createLayoutSymbol(coreSymbolsInStaff, symbol);
            }
            if (staff.getNotationType() == null) {
                throw new IM3Exception("The staff " + staff+  " has not a notation type");
            }

            List<LayoutCoreBarline> staffBarLines = new LinkedList<>();
            barlines.put(staff, staffBarLines);
            if (staff.getNotationType().equals(NotationType.eModern)) {
                // create barlines
                // TODO: 21/9/17 Deberíamos poder crear barlines de system
                for (Measure measure : scoreSong.getMeasures()) {
                    LayoutCoreBarline barline = new LayoutCoreBarline(staff, getLayoutFont(staff), measure);
                    simultaneities.add(barline);
                    staffBarLines.add(barline);
                }
            }

            createConnectors();
            createBeams();
            //System.out.println("Staff " + staff.getNumberIdentifier());
            //simultaneities.printDebug();
        }

        // now link dependant symbols
        for (Map.Entry<Object, NotationSymbol> entry: coreSymbolViews.entrySet()) {
            if (entry.getValue() instanceof LayoutAttachmentInStaff) {
                LayoutAttachmentInStaff layoutAttachmentInStaff = (LayoutAttachmentInStaff) entry.getValue();
                AttachmentInStaff<ITimedElementInStaff> coreSymbol = (AttachmentInStaff) layoutAttachmentInStaff.getCoreSymbol();
                NotationSymbol attachedToView = coreSymbolViews.get(coreSymbol.getAttachedTo());
                if (attachedToView == null) {
                    throw new IM3Exception("Cannot find the view of the attached to object: " + coreSymbol.getAttachedTo());
                }
                layoutAttachmentInStaff.setAttachedToView(attachedToView);
            }

        }
    }

    public LayoutFont getLayoutFont(Staff staff) throws IM3Exception {
        LayoutFont layoutFont = layoutFonts.get(staff);
        if (layoutFont == null) {
            throw new IM3Exception("Staff not found in layoutFonts: " + staff);
        }
        return layoutFont;
    }
    private void createLayoutSymbol(ArrayList<LayoutCoreSymbolInStaff> coreSymbolsInStaff, ITimedElementInStaff symbol) throws IM3Exception {
        if (symbol instanceof CompoundAtom) {
            for (Atom subatom : ((CompoundAtom) symbol).getAtoms()) {
                createLayoutSymbol(coreSymbolsInStaff, subatom);
            }
        } else {
            LayoutCoreSymbol layoutCoreSymbol = layoutSymbolFactory.createCoreSymbol(getLayoutFont(symbol.getStaff()), symbol);

            coreSymbolViews.put(symbol, layoutCoreSymbol);
            if (layoutCoreSymbol.getComponents() != null) {
                List<Component<LayoutCoreSymbol>> components = layoutCoreSymbol.getComponents();
                for (Component<LayoutCoreSymbol> component : components) {
                    coreSymbolViews.put(component.getModelElement(), component);
                }
            }

            if (symbol instanceof ITimedSymbolWithConnectors) {
                if (!(layoutCoreSymbol instanceof IConnectableWithSlur)) { // TODO: 31/10/17 Esto no está bien diseñado
                    throw new IM3Exception("Design Inconsistency, " + layoutCoreSymbol + " should implement IConnectableWithSlur because "+ symbol + " is a ITimedSymbolWithConnectors");
                }
                layoutConnectorEnds.put((ITimedSymbolWithConnectors)symbol, (IConnectableWithSlur)layoutCoreSymbol);
            }
            addCoreSymbolToSimultaneities(coreSymbolsInStaff, layoutCoreSymbol);

            // now add the symbols dependant of the main symbol (e.g. displaced dot)
            /*Collection<LayoutCoreSymbol> dependantSymbols = layoutCoreSymbol.getDependantCoreSymbols();
            if (dependantSymbols != null) {
                for (LayoutCoreSymbol ds : dependantSymbols) {
                    addCoreSymbolToSimultaneities(coreSymbolsInStaff, ds);
                }
            }*/
        }
    }

    private void addCoreSymbolToSimultaneities(ArrayList<LayoutCoreSymbolInStaff> coreSymbolsInStaff, LayoutCoreSymbol layoutCoreSymbol) throws IM3Exception {
        //createLayout(symbol, layoutStaff);
        if (layoutCoreSymbol != null) {
            simultaneities.add(layoutCoreSymbol);
            if (layoutCoreSymbol instanceof LayoutCoreSymbolInStaff) {
                coreSymbolsInStaff.add((LayoutCoreSymbolInStaff) layoutCoreSymbol);
            } else {
                throw new IM3RuntimeException("Unimplemented " + layoutCoreSymbol.getClass()); // TODO: 24/9/17 Debemos ponerlos en otra lista? Beamed groups?
            }

            //TODO Esto de añadir los layout pitches con el instanceof no me gusta
            if (layoutCoreSymbol instanceof LayoutCoreSingleFigureAtom) {
                LayoutCoreSingleFigureAtom sfa = (LayoutCoreSingleFigureAtom) layoutCoreSymbol;

                BeamGroup beam = sfa.getCoreSymbol().getBelongsToBeam();
                if (beam != null) {
                    List<LayoutCoreSingleFigureAtom> layoutAtomsInBeam = singleLayoutFigureAtomsInBeam.get(beam);
                    if (layoutAtomsInBeam == null) {
                        layoutAtomsInBeam = new ArrayList<>();
                        singleLayoutFigureAtomsInBeam.put(sfa.getCoreSymbol().getBelongsToBeam(), layoutAtomsInBeam);
                    }
                    layoutAtomsInBeam.add(sfa);
                }

                for (NotePitch notePitch : sfa.getNotePitches()) {
                    layoutConnectorEnds.put(notePitch.getAtomPitch(), notePitch);
                }
            }
        }
    }

    // TODO: 1/10/17 Esto debería ser distinto en layoutPages por si hay que partir un beam (no es normal, pero...)
    protected void createBeams() throws IM3Exception {
        beams = new ArrayList<>();

        for (Map.Entry<BeamGroup, List<LayoutCoreSingleFigureAtom>> entry: singleLayoutFigureAtomsInBeam.entrySet()) {
            LayoutFont layoutFont = getLayoutFont(entry.getValue().get(0).getCoreStaff());
            LayoutBeamGroup layoutBeamGroup = new LayoutBeamGroup(entry.getKey(), entry.getValue(), layoutFont);
            layoutBeamGroup.createBeams();
            beams.add(layoutBeamGroup);
        }

        /*for (Map.Entry<Staff, List<LayoutCoreSymbolInStaff>> entry: this.coreSymbolsInStaves.entrySet()) {
            for (LayoutCoreSymbolInStaff coreSymbolInStaff : entry.getValue()) {
                // TODO: 1/10/17 Esto debería ser para cualquier tipo de Atom (LayoutCoreSymbolWithDuration
                if (coreSymbolInStaff instanceof LayoutCoreSingleFigureAtom) {
                    LayoutCoreSingleFigureAtom lcsfa = (LayoutCoreSingleFigureAtom) coreSymbolInStaff;
                    SingleFigureAtom sfa = lcsfa.getCoreSymbol();
                    if (sfa.getParentAtom() != null) {
                        if (sfa.getParentAtom() instanceof BeamedGroup) {
                            System.out.println("BEAM!!!!");
                        } else {
                            throw new IM3Exception("Unsupported parent " + sfa.getParentAtom().getClass());
                        }
                    }
                }
            }
        }*/
    }

    /**
     * This must be implemented in the different layout. A connector, e.g. a slur, could be split between staves
     */
    protected void createConnectors() throws IM3Exception {
        // TODO: 1/10/17 Factory como LayoutCoreSymbol para connectors

        NotePitch previousNotePitch = null;

// TODO: 1/10/17 Implementarlo en hijos, una ligadura puede que haya que partirla
        // create connectors between LayoutCoreSymbolInStaff
        for (Map.Entry<Staff, List<LayoutCoreSymbolInStaff>> entry: this.coreSymbolsInStaves.entrySet()) {
            for (LayoutCoreSymbolInStaff coreSymbolInStaff: entry.getValue()) {
                if (coreSymbolInStaff instanceof LayoutCoreSingleFigureAtom) {
                    LayoutCoreSingleFigureAtom lcsfa = (LayoutCoreSingleFigureAtom) coreSymbolInStaff;
                    createConnectors(lcsfa, lcsfa.getCoreSymbol());
                    for (NotePitch notePitch: lcsfa.getNotePitches()) {
                        // create ties
                        if (notePitch.getAtomPitch().isTiedFromPrevious()) {
                            if (notePitch.getAtomPitch().getTiedFromPrevious() != previousNotePitch.getAtomPitch()) {
                                throw new IM3Exception("Atom pitches to tie are not the same");
                            }

                            LayoutSlur slur = new LayoutSlur(previousNotePitch, notePitch);
                            addConnector(slur);
                        }

                        // create slurs and other connectors
                        createConnectors(notePitch, notePitch.getAtomPitch());
                        if (notePitch.getAtomPitch().getConnectors() != null) {
                            System.out.println("CONNECTORS: " + notePitch.getAtomPitch().getConnectors());
                        }
                        previousNotePitch = notePitch;
                    }
                } else if (coreSymbolInStaff instanceof LayoutCoreStaffTimedPlaceHolder) {
                    // this is only for connectors
                    LayoutCoreStaffTimedPlaceHolder layoutCoreStaffTimedPlaceHolder = (LayoutCoreStaffTimedPlaceHolder) coreSymbolInStaff;
                    createConnectors(layoutCoreStaffTimedPlaceHolder, layoutCoreStaffTimedPlaceHolder.getCoreSymbol());
                }
            }
        }
    }

    private void createConnectors(IConnectableWithSlur layoutCoreSymbolInStaff, ITimedSymbolWithConnectors symbolWithConnectors) throws IM3Exception {
        Collection<Connector> atomPitchConnectors = symbolWithConnectors.getConnectors();

        if (atomPitchConnectors != null) {
            for (Connector connector: atomPitchConnectors) {
                // just create "to" connectors to avoid duplicate in both directions
                if (connector.getTo() == symbolWithConnectors) {

                    IConnectableWithSlur from = layoutConnectorEnds.get(connector.getFrom());
                    if (from == null) {
                        throw new IM3Exception("Cannot find a layout symbol for core symbol " + connector.getFrom() + " for connector " + connector);
                    }

                    if (connector instanceof Slur) {
                        LayoutSlur layoutSlur = new LayoutSlur(from, layoutCoreSymbolInStaff);
                        addConnector(layoutSlur);
                    } else {
                        System.err.println("CONNECTOR NON SUPPORTED: " + connector.getClass());
                    }
                }
            }
        }
    }

    protected void doHorizontalLayout(Simultaneities simultaneities) throws IM3Exception {
        // Replace for a factory if required
        // use the maximum noteHeadWidth among all the staves
        double noteHeadWidth = 0;
        for (Double nh: noteHeadWidths.values()) {
            noteHeadWidth = Math.max(noteHeadWidth, nh);
        }
        ILayoutEngine layoutEngine = new BelliniLayoutEngine(1, 1, noteHeadWidth/2); // TODO: 22/9/17 ¿qué valor ponemos?
        layoutEngine.reset(simultaneities);
        layoutEngine.doHorizontalLayout(simultaneities);
    }


    public abstract void layout() throws IM3Exception;

    public abstract Collection<Canvas> getCanvases();

    protected void addConnector(LayoutConnector connector) {
        this.connectors.add(connector);
    }


    public Collection<LayoutFont> getLayoutFonts() {
        return layoutFonts.values();
    }

    public Collection<LayoutStaff> getLayoutStaves() {
        return layoutStaves.values();
    }
}
