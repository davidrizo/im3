package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.connectors.LayoutSlur;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

import java.util.*;

public class PageLayout extends ScoreLayout {
    private final CoordinateComponent height;
    private final CoordinateComponent width;
    private final boolean includePageAndSystemBreaks;
    private ArrayList<Page> pages;

    public PageLayout(ScoreSong song, Collection<Staff> staves, boolean includePageAndSystemBreaks, LayoutFonts font, CoordinateComponent width, CoordinateComponent height) throws IM3Exception {
        super(song, staves, font);
        this.includePageAndSystemBreaks = includePageAndSystemBreaks;
        this.width = width;
        this.height = height;
    }

    public PageLayout(ScoreSong song, Collection<Staff> staves, boolean includePageAndSystemBreaks, HashMap<Staff, LayoutFonts> fonts, CoordinateComponent width, CoordinateComponent height) throws IM3Exception {
        super(song, staves, fonts);
        this.includePageAndSystemBreaks = includePageAndSystemBreaks;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void createConnectors() throws IM3Exception {
        super.createConnectors();
        System.err.println("TO-DO CONNECTORS IN PAGE LAYOUT"); // TODO: 1/10/17 Connectors en Page Layout
    }

    /**
     * It a connector spans two layout systems it must be split into two
     * @param from
     * @param to
     * @throws IM3Exception
     */
    @Override
    protected void createSlur(IConnectableWithSlurInStaff from, IConnectableWithSlurInStaff to) throws IM3Exception {
        if (from == null) {
            throw new IM3Exception("Cannot find a layout symbol for core symbol " + from + " for connector " + to);
        }

        if (from.getLayoutStaff() == to.getLayoutStaff()) {
            LayoutSlur layoutSlur = new LayoutSlur(from, to);
            addConnector(layoutSlur);
        } else {
            // create two broken slurs
            LayoutSlur layoutSlurFrom = new LayoutSlur(from, null);
            addConnector(layoutSlurFrom);

            LayoutSlur layoutSlurTo = new LayoutSlur(null, to);
            addConnector(layoutSlurTo);
        }
    }


    @Override
    public void layout() throws IM3Exception {
        // TODO: 19/9/17 En esta versión creamos todos los símbolos cada vez - habría que crear sólo los necesarios
        //TODO scoreSong.getStaffGroups()
        pages = new ArrayList<>(); //TODO supongo que no habrá que rehacerlo siempre

        // TODO: 25/9/17 Intentar unificar código con HorizontalLayout

        if (staves.isEmpty()) {
            throw new IM3Exception("No staves found");
        }
        // add the system breaks, with a default duration that will be able to fit the new clef and new key signature
        if (includePageAndSystemBreaks) {
            for (SystemBreak sb: staves.iterator().next().getSystemBreaks().values()) {
                // use the first layout font, it can be any one
                LayoutSystemBreak lsb = new LayoutSystemBreak(layoutFonts.values().iterator().next(), sb);
                simultaneities.add(lsb);
            }
            // TODO: 20/11/17 He quitado los page breaks
            /*for (PageBreak sb: staves.iterator().next().getPageBreaks().values()) {
                // use the first layout font, it can be any one
                LayoutPageBreak lsb = new LayoutPageBreak(layoutFonts.values().iterator().next(), sb);
                simultaneities.add(lsb);
            }*/
            //--------
        } // else discard system breaks

        // perform a first horizontal layout
        doHorizontalLayout(simultaneities);

        // now locate the points where insert line and page breaks and create LayoutStaff
        //TODO Page breaks - ahora todo en un page
        Page lastPage = new Page(new CoordinateComponent(width), new CoordinateComponent(height)); //TODO
        pages.add(lastPage);
        double layoutStaffStartingX = 0;
        double nextY = LayoutConstants.TOP_MARGIN;

        LayoutStaffSystem lastSystem = null;
        Simultaneities lastLayoutSimultaneities = null;

        //TODO Calcular por dónde partir, por donde no quepa - ahora sólo miro los system breaks manuales
        ArrayList<LayoutCoreSymbol> newSimultaneitiesToAdd = new ArrayList<>(); // breaks, clefs, key signatures
        for (Simultaneity simultaneity: simultaneities.getSimiltaneities()) {
            boolean pageBreak = simultaneity.isPageBreak();
            if (pageBreak) {
                lastPage = new Page(new CoordinateComponent(width), new CoordinateComponent(height)); //TODO
                pages.add(lastPage);
                layoutStaffStartingX = 0;
                nextY = LayoutConstants.TOP_MARGIN;
            }

            if (lastSystem == null || simultaneity.isSystemBreak() || pageBreak) { // TODO que sea también porque no quepan
                layoutStaffStartingX = simultaneity.getX();
                if (lastSystem != null) {
                    lastSystem.setEndingTime(simultaneity.getTime());
                }
                lastSystem = new LayoutStaffSystem();
                lastLayoutSimultaneities = new Simultaneities();
                lastSystem.setStartingTime(simultaneity.getTime());
                lastSystem.setStartingX(layoutStaffStartingX);
                lastPage.addSystem(lastSystem);

                if (staves.size() > 1) {
                    nextY += LayoutConstants.SYSTEM_SEPARATION;
                } // if not do not need to separate more
                // TODO - si no cabe en página que se cree otra

                for (Staff staff : staves) {
                    CoordinateComponent y = new CoordinateComponent(nextY);
                    Coordinate staffLeftTopCoordinate = new Coordinate(null, y);
                    Coordinate staffRightTopCoordinate = new Coordinate(lastPage.getCanvas().getWidthCoordinateComponent(), y);
                    LayoutStaff layoutStaff = new LayoutStaff(this, staffLeftTopCoordinate, staffRightTopCoordinate, staff);
                    layoutStaves.put(staff, layoutStaff);
                    lastSystem.addLayoutStaff(layoutStaff);
                    lastPage.getCanvas().add(layoutStaff.getGraphics());// TODO: 25/9/17 ¿Realmente hace falta el canvas?
                    nextY += LayoutConstants.STAFF_SEPARATION;

                    // in not first system, add clefs and key signatures
                    if (lastPage.getSystemsInPage().size() > 1) {
                        Time time = simultaneity.getTime();
                        Clef clef = staff.getClefAtTime(time);
                        if (clef == null) {
                            clef = staff.getRunningClefAt(time);
                            LayoutCoreClef layoutCoreClef = new LayoutCoreClef(getLayoutFont(staff), clef);
                            layoutCoreClef.setSystem(lastSystem);
                            newSimultaneitiesToAdd.add(layoutCoreClef);
                            layoutCoreClef.setTime(time);
                            layoutStaff.add(layoutCoreClef);
                        } // if not it will be inserted because it is explicit

                        KeySignature ks = staff.getKeySignatureWithOnset(time);
                        if (ks == null) {
                            ks = staff.getRunningKeySignatureAt(time);
                            LayoutCoreKeySignature layoutCoreKs = new LayoutCoreKeySignature(getLayoutFont(staff), ks);
                            layoutCoreKs.setSystem(lastSystem);
                            layoutCoreKs.setTime(time);
                            newSimultaneitiesToAdd.add(layoutCoreKs);
                            layoutStaff.add(layoutCoreKs);
                        } // if not it will be inserted because it is explicit
                    }

                    // TODO: 26/9/17 NotePitch que esté en otro (changed) staff
                    // add to staff
                }
            }

            for (LayoutCoreSymbol coreSymbol: simultaneity.getSymbols()) {
                coreSymbol.setSystem(lastSystem);

                if (coreSymbol instanceof LayoutCoreSymbolInStaff) {
                    LayoutCoreSymbolInStaff layoutCoreSymbolInStaff = (LayoutCoreSymbolInStaff) coreSymbol;
                    Staff staff = layoutCoreSymbolInStaff.getCoreStaff();
                    if (staff == null) {
                        throw new IM3RuntimeException("This should not happen: " + layoutCoreSymbolInStaff + " has not a staff");
                    }
                    LayoutStaff layoutStaff = lastSystem.get(staff);
                    if (layoutStaff == null) {
                        throw new IM3RuntimeException("This should not happen: " + staff + " has not a layoutStaff in the system");
                    }
                    layoutStaff.add(layoutCoreSymbolInStaff);
                } else {
                    if (coreSymbol instanceof LayoutCoreBarline) {
                        LayoutCoreBarline layoutCoreBarline = (LayoutCoreBarline) coreSymbol;
                        Staff staff = layoutCoreBarline.getStaff();

                        if (staff == null) {
                            //layoutCoreBarline.setLayoutStaff(lastSystem.getBottomStaff(), lastSystem.getTopStaff()); // covers all staves
                            throw new IM3RuntimeException("LayoutCoreBarline has not staff");
                        } else {
                            LayoutStaff layoutStaff = lastSystem.get(staff);
                            layoutCoreBarline.setLayoutStaff(layoutStaff, layoutStaff);
                            lastPage.getCanvas().getElements().add(layoutCoreBarline.getGraphics());

                            lastSystem.addLayoutCoreBarline(layoutCoreBarline);
                        }
                    }
                    //TODO Quizás mejor en el system
                   // page.getCanvas().add(coreSymbol.getGraphics());
                }

            }

        }

        if (lastSystem != null) {
            lastSystem.setEndingTime(Time.TIME_MAX);
        }

        for (LayoutCoreSymbol coreSymbol : newSimultaneitiesToAdd) {
            simultaneities.add(coreSymbol);
        }

        for (Page page: pages) {
            for (LayoutStaffSystem staffSystem : page.getSystemsInPage()) {
                for (LayoutStaff layoutStaff : staffSystem.getStaves()) {
                    //layoutStaff.createNoteAccidentals(staffSystem.getStartingTime(), staffSystem.getEndingTime());
                    //20180208 layoutStaff.createNoteAccidentals(staffSystem.getStartingTime(), staffSystem.getEndingTime());
                    layoutStaff.createNoteAccidentals();
                }
            }
        }

        for (Page page: pages) {
            for (LayoutStaffSystem system: page.getSystemsInPage()) {
               system.createStaffConnectors(connectors); //TODO ¿Qué pasa si un conector salta de página?
            }
        }

        //simultaneities.printDebug();

        doHorizontalLayout(simultaneities); // TODO: 26/9/17 ¿Y si cambia la anchura y hay que volver a bajar elementos de línea?
        for (Simultaneity simultaneity: simultaneities.getSimiltaneities()) {
            for (LayoutCoreSymbol coreSymbol: simultaneity.getSymbols()) {
                LayoutStaffSystem system = coreSymbol.getSystem();
                coreSymbol.setX(simultaneity.getX() - system.getStartingX());
                /*if (coreSymbol instanceof LayoutCoreSymbolInStaff) {
                    LayoutCoreSymbolInStaff layoutCoreSymbolInStaff = (LayoutCoreSymbolInStaff) coreSymbol;
                    Staff staff = layoutCoreSymbolInStaff.getCoreStaff();
                    if (staff == null) {
                        throw new IM3RuntimeException("This should not happen: " + layoutCoreSymbolInStaff + " has not a staff");
                    }
                    LayoutStaff layoutStaff = system.get(staff);
                    if (layoutStaff == null) {
                        throw new IM3RuntimeException("This should not happen: " + staff + " has not a layoutStaff in the system");
                    }
                    layoutStaff.add(layoutCoreSymbolInStaff);
                } else {
                    if (coreSymbol instanceof LayoutCoreBarline) {
                        LayoutCoreBarline layoutCoreBarline = (LayoutCoreBarline) coreSymbol;
                        layoutCoreBarline.setLayoutStaff(system.getBottomStaff(), system.getTopStaff());
                    }
                    //TODO Quizás mejor en el system
                    page.getCanvas().add(coreSymbol.getGraphics());
                }*/

            }
        }

        createConnectors();
        createBeams();


        for (Page page: pages) {
            // add the connectors to the canvas
            for (LayoutConnector connector : connectors) {
                if (connector.getGraphics().getCanvas() == null) {
                    page.getCanvas().add(connector.getGraphics());
                }
            }

            for (LayoutBeamGroup beam : beams) {
                if (beam.getGraphicsElement().getCanvas() == null) {
                    page.getCanvas().add(beam.getGraphicsElement());
                }
            }
        }

    }

    @Override
    public List<Canvas> getCanvases() {
        ArrayList<Canvas> result = new ArrayList<>();
        for (Page page: pages) {
            result.add(page.getCanvas());
        }
        return result;
    }
}
