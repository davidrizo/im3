package es.ua.dlsi.im3.omr.muret.editpage.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.muret.editpage.PageBasedController;
import es.ua.dlsi.im3.omr.muret.model.*;
import es.ua.dlsi.im3.omr.model.pojo.*;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.ISymbolsRecognizer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.SymbolRecognizerFactory;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.*;

public class PageSymbolsEditController extends PageBasedController<SymbolsPageView> {

    @FXML
    ToolBar toolbarSymbols;

    /**
     * It true, the symbol
     */
    boolean boundingBoxMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected SymbolsPageView createPageView(OMRPage omrPage, PageBasedController<SymbolsPageView> symbolsPageViewPageBasedController, ReadOnlyDoubleProperty widthProperty) throws IM3Exception {
        //return new SymbolsPageView(omrPage, this, widthProperty);
        return new SymbolsPageView(omrPage);
    }


    // --------- Symbols step
    @FXML
    public void handleRecognizeSymbols() {
        ISymbolsRecognizer recognizer = SymbolRecognizerFactory.getInstance().create();
        //TODO decir que hemos guardado con un splash screen
        dashboard.save();
        HashMap<Page, OMRPage> pagesMap = new HashMap<>();
        ArrayList<Page> pojoPages = new ArrayList<>();
        //TODO ¿Mejor en modelo?
        for (OMRPage omrPage: pages.keySet()) {
            Page pojoPage = omrPage.createPOJO();
            pagesMap.put(pojoPage, omrPage);
            pojoPages.add(pojoPage);
        }

        // recognize
        try {
            recognizer.recognize(dashboard.getModel().getCurrentProject().getImagesFolderURL(), pojoPages);
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot recognize symbols", e);
        }

        // load results in OMRPage / OMRRegion
        for (Page pojoPage: pojoPages) {
            OMRPage omrPage = pagesMap.get(pojoPage);
            if (omrPage == null) {
                throw new IM3RuntimeException("Cannot find omrPage for POJO " + pojoPage);
            }
            for (Region pojoRegion: pojoPage.getRegions()) {
                OMRRegion omrRegion = omrPage.findRegion(pojoRegion);
                if (omrRegion == null) {
                    throw new IM3RuntimeException("OMRRegion not found for POJO symbolsRegionView " + pojoRegion);
                }
                omrRegion.clearSymbols();
                for (Symbol symbol: pojoRegion.getSymbols()) {
                    // use the y and height from the symbolsRegionView
                    omrRegion.addSymbol(new OMRSymbol(symbol));
                }
            }
        }
    }
}