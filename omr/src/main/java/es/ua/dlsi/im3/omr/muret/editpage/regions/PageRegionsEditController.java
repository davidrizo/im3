package es.ua.dlsi.im3.omr.muret.editpage.regions;

import es.ua.dlsi.im3.omr.muret.editpage.PageBasedController;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.model.pojo.*;
import es.ua.dlsi.im3.omr.classifiers.segmentation.IDocumentSegmenter;
import es.ua.dlsi.im3.omr.classifiers.segmentation.DocumentSegmenterFactory;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.*;

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
        //TODO Comandos
        /*IDocumentSegmenter pageSegmenter = DocumentSegmenterFactory.getInstance().create();
        List<Page> pages =
        List<Region> regions = pageSegmenter.segment(omrPage.getImageFileURL());

        for (Map.Entry<OMRPage, RegionEditPageView> pageEntry : pages.entrySet()) {
            OMRPage omrPage = pageEntry.getKey();
            List<Region> regions = pageSegmenter.segment(omrPage.getImageFileURL());
            omrPage.clearRegions();
            omrPage.addRegions(regions);
        }*/
    }
}