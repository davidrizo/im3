package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.*;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

import java.util.*;

public class PageLayout extends ScoreLayout {
    private final CoordinateComponent height;
    private final CoordinateComponent width;
    private ArrayList<Page> pages;

    public PageLayout(ScoreSong song, LayoutFonts font, CoordinateComponent width, CoordinateComponent height) throws IM3Exception {
        super(song, font);
        this.width = width;
        this.height = height;
    }

    @Override
    public void layout() throws IM3Exception {
        // TODO: 19/9/17 En esta versión creamos todos los símbolos cada vez - habría que crear sólo los necesarios
        //TODO scoreSong.getStaffGroups()
        pages = new ArrayList<>(); //TODO supongo que no habrá que rehacerlo siempre

        // TODO: 25/9/17 Intentar unificar código con HorizontalLayout

        // add the system breaks, with a default duration that will be able to fit the new clef and new key signature
        for (SystemBreak sb: scoreSong.getSystemBreaks().values()) {
            LayoutSystemBreak lsb = new LayoutSystemBreak(layoutFont, sb);
            simultaneities.add(lsb);
        }

        // perform a first horizontal layout
        doHorizontalLayout(simultaneities);

        // now locate the points where insert line and page breaks and create LayoutStaff
        //TODO Page breaks - ahora todo en un page
        Page page = new Page(new CoordinateComponent(width), new CoordinateComponent(height)); //TOOD
        pages.add(page);
        double layoutStaffStartingX = 0;
        double nextY = LayoutConstants.TOP_MARGIN;
        LayoutStaffSystem lastSystem = null;
        Simultaneities lastLayoutSimultaneities = null;

        //TODO Calcular por dónde partir, por donde no quepa - ahora sólo miro los system breaks manuales
        ArrayList<LayoutCoreSymbol> newSimultaneitiesToAdd = new ArrayList<>(); // breaks, clefs, key signatures
        for (Simultaneity simultaneity: simultaneities.getSimiltaneities()) {
            if (lastSystem == null || simultaneity.isSystemBreak()) { // TODO que sea también porque no quepan
                layoutStaffStartingX = simultaneity.getX();
                if (lastSystem != null) {
                    lastSystem.setEndingTime(simultaneity.getTime());
                }
                lastSystem = new LayoutStaffSystem();
                lastLayoutSimultaneities = new Simultaneities();
                lastSystem.setStartingTime(simultaneity.getTime());
                lastSystem.setStartingX(layoutStaffStartingX);
                page.addSystem(lastSystem);

                if (scoreSong.getStaves().size() > 1) {
                    nextY += LayoutConstants.SYSTEM_SEPARATION;
                } // if not do not need to separate more
                // TODO - si no cabe en página que se cree otra

                for (Staff staff : scoreSong.getStaves()) {
                    CoordinateComponent y = new CoordinateComponent(nextY);
                    Coordinate staffLeftTopCoordinate = new Coordinate(null, y);
                    Coordinate staffRightTopCoordinate = new Coordinate(page.getCanvas().getWidthCoordinateComponent(), y);
                    LayoutStaff layoutStaff = new LayoutStaff(this, staffLeftTopCoordinate, staffRightTopCoordinate, staff);
                    lastSystem.addLayoutStaff(layoutStaff);
                    page.getCanvas().add(layoutStaff.getGraphics());// TODO: 25/9/17 ¿Realmente hace falta el canvas?
                    nextY += LayoutConstants.STAFF_SEPARATION;

                    // in not first system, add clefs and key signatures
                    if (page.getSystemsInPage().size() > 1) {
                        Time time = simultaneity.getTime();
                        Clef clef = staff.getClefAtTime(time);
                        if (clef == null) {
                            clef = staff.getRunningClefAt(time);
                            LayoutCoreClef layoutCoreClef = new LayoutCoreClef(layoutFont, clef);
                            layoutCoreClef.setSystem(lastSystem);
                            newSimultaneitiesToAdd.add(layoutCoreClef);
                            layoutCoreClef.setTime(time);
                            layoutStaff.add(layoutCoreClef);
                        } // if not it will be inserted because it is explicit

                        KeySignature ks = staff.getKeySignatureWithOnset(time);
                        if (ks == null) {
                            ks = staff.getRunningKeySignatureAt(time);
                            LayoutCoreKeySignature layoutCoreKs = new LayoutCoreKeySignature(layoutFont, ks);
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
                        layoutCoreBarline.setLayoutStaff(lastSystem.getBottomStaff(), lastSystem.getTopStaff());
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

        for (LayoutStaffSystem staffSystem: page.getSystemsInPage()) {
            for (LayoutStaff layoutStaff: staffSystem.getStaves()) {
                //layoutStaff.createNoteAccidentals(staffSystem.getStartingTime(), staffSystem.getEndingTime());
                layoutStaff.createNoteAccidentals(staffSystem.getStartingTime(), staffSystem.getEndingTime());
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
