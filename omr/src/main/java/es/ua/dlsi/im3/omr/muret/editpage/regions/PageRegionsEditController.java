package es.ua.dlsi.im3.omr.muret.editpage.regions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.muret.editpage.PageBasedController;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.model.pojo.*;
import es.ua.dlsi.im3.omr.classifiers.segmentation.IDocumentSegmenter;
import es.ua.dlsi.im3.omr.classifiers.segmentation.DocumentSegmenterFactory;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageRegionsEditController extends PageBasedController<RegionEditPageView> {
    @FXML
    ToolBar toolbarRegions;
    @FXML
    Toggle toggleRegions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected RegionEditPageView createPageView(OMRPage omrPage, PageBasedController<RegionEditPageView> regionPageViewPageBasedController, ReadOnlyDoubleProperty widtProperty) {
        return new RegionEditPageView(omrPage, this, vboxPages.widthProperty());
    }

    @FXML
    public void handleRecognizeRegions() {
        try {
            doRecognizeRegions(); //TODO a modelo y con comandos
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot recognize regions", e);
            ShowError.show(OMRApp.getMainStage(), "Cannot recognize regions", e);
        }
    }

    private void doRecognizeRegions() throws IM3Exception {
        IDocumentSegmenter pageSegmenter = DocumentSegmenterFactory.getInstance().create();

        for (OMRPage omrPage: this.pages.keySet()) {
            List<Region> regions = pageSegmenter.segment(omrPage.getImageFileURL());
            for (Region region: regions) {
                OMRRegion omrRegion = new OMRRegion(region);
                omrPage.addRegion(omrRegion);
            }
        }
    }
}