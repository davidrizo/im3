package es.ua.dlsi.im3.omr.interactive.pageedit;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.gui.javafx.JavaFXUtils;
import es.ua.dlsi.im3.omr.interactive.DashboardController;
import es.ua.dlsi.im3.omr.interactive.model.OMRInstrument;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.model.pojo.Region;
import es.ua.dlsi.im3.omr.segmentation.IPageSegmenter;
import es.ua.dlsi.im3.omr.segmentation.PageSegmenterFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class PageEditController implements Initializable {
    @FXML
    ToolBar toolbar;

    @FXML
    ToolBar toolbarImages;

    @FXML
    ToolBar toolbarRegions;

    @FXML
    ToolBar toolbarSymbols;

    @FXML
    ToolBar toolbarMusic;

    @FXML
    VBox vboxPages;

    @FXML
    ScrollPane scrollPane;

    @FXML
    ToggleGroup tgPageViewStep;

    /**
     * Music editing
     */
    ToggleGroup tgInstruments;

    private DashboardController dashboard;

    private HashMap<OMRPage, PageView> pages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tgInstruments = new ToggleGroup();
        System.out.println("TBM=" + toolbarMusic);
    }

    public void setDashboard(DashboardController dashboard) {
        this.dashboard = dashboard;
    }

    public DashboardController getDashboard() {
        return dashboard;
    }

    public void setPages(OMRPage omrPage, List<OMRPage> pagesToOpen) {
        pages = new HashMap<>();

        // create the buttons
        createInstrumentButtons(omrPage);
        createImageViews(pagesToOpen, omrPage);
    }

    private void createInstrumentButtons(OMRPage selectedOMRPage) {
        toolbarMusic.getItems().add(0, new Label("Select an instrument"));
        int i = 1;
        for (OMRInstrument instrument : selectedOMRPage.getInstrumentList()) {
            ToggleButton button = new ToggleButton(instrument.getName());
            button.setToggleGroup(tgInstruments);
            toolbarMusic.getItems().add(i++, button); // add before other buttons and separator
        }
    }

    private void createImageViews(List<OMRPage> pagesToOpen, OMRPage selectedOMRPage) {
        PageView selectedPageView = null;
        for (OMRPage omrPage : pagesToOpen) {
            PageView pageView = new PageView(omrPage, this, vboxPages.widthProperty());
            pages.put(omrPage, pageView);
            vboxPages.getChildren().add(pageView);
            if (omrPage == selectedOMRPage) {
                selectedPageView = pageView;
            }

            Button btn = new Button(omrPage.toString());
            toolbarImages.getItems().add(btn);
            btn.setOnAction(event -> {
                focus(pageView);
            });
        }

        focus(selectedPageView);
    }

    private void focus(PageView selectedPageView) {
        boolean found = false;
        for (Node node : vboxPages.getChildren()) {
            if (node == selectedPageView) {
                JavaFXUtils.ensureVisible(scrollPane, node);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IM3RuntimeException("Cannot find the selected page");
        }
    }

    @FXML
    public void handleRegionsStep() {
        toolbarRegions.setVisible(true);
        toolbarSymbols.setVisible(false);
        toolbarMusic.setVisible(false);
    }

    @FXML
    public void handleSymbolsStep() {
        toolbarRegions.setVisible(false);
        toolbarSymbols.setVisible(true);
        toolbarMusic.setVisible(false);
    }

    @FXML
    public void handleMusicStep() {
        toolbarRegions.setVisible(false);
        toolbarSymbols.setVisible(false);
        toolbarMusic.setVisible(true);
    }

    // --------- Regions step

    @FXML
    public void handleRecognizeRegions() {
        //TODO Comandos
        IPageSegmenter pageSegmenter = PageSegmenterFactory.getInstance().create();
        for (Map.Entry<OMRPage, PageView> pageEntry : pages.entrySet()) {
            OMRPage omrPage = pageEntry.getKey();
            List<Region> regions = pageSegmenter.segment(omrPage.getImageFile());
            omrPage.clearRegions();
            omrPage.addRegions(regions);
        }
    }

    // --------- Symbols step
    @FXML
    public void handleRecognizeSymbols() {

    }
}