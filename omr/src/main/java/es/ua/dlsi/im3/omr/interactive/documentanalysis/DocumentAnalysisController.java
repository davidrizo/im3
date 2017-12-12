package es.ua.dlsi.im3.omr.interactive.documentanalysis;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.gui.javafx.JavaFXUtils;
import es.ua.dlsi.im3.omr.interactive.DashboardController;
import es.ua.dlsi.im3.omr.interactive.model.OMRInstrument;
import es.ua.dlsi.im3.omr.interactive.model.OMRModel;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.model.pojo.Region;
import es.ua.dlsi.im3.omr.segmentation.IPageSegmenter;
import es.ua.dlsi.im3.omr.segmentation.PageSegmenterFactory;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DocumentAnalysisController implements Initializable {
    @FXML
    ToolBar toolbar;

    @FXML
    VBox vboxPages;

    @FXML
    ScrollPane scrollPane;

    ToggleGroup tgInstruments;

    private DashboardController dashboard;

    private HashMap<OMRPage, PageView> pages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tgInstruments = new ToggleGroup();
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
        toolbar.getItems().add(0, new Label("Select an instrument"));
        int i=1;
        for (OMRInstrument instrument: selectedOMRPage.getInstrumentList()) {
            ToggleButton button = new ToggleButton(instrument.getName());
            button.setToggleGroup(tgInstruments);
            toolbar.getItems().add(i++, button); // add before other buttons and separator
        }
    }

    private void createImageViews(List<OMRPage> pagesToOpen, OMRPage selectedOMRPage) {
        Separator sep = new Separator(Orientation.VERTICAL);
        sep.setMaxWidth(20);
        toolbar.getItems().add(sep);

        PageView selectedPageView = null;
        for (OMRPage omrPage: pagesToOpen) {
            PageView pageView = new PageView(omrPage, this, vboxPages.widthProperty());
            pages.put(omrPage, pageView);
            vboxPages.getChildren().add(pageView);
            if (omrPage == selectedOMRPage) {
                selectedPageView = pageView;
            }

            Button btn = new Button(omrPage.toString());
            toolbar.getItems().add(btn);
            btn.setOnAction(event -> {
                focus(pageView);
            });
        }

        focus(selectedPageView);
    }

    private void focus(PageView selectedPageView) {
        boolean found = false;
        for (Node node: vboxPages.getChildren()) {
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
    public void handleRecognizeRegions() {
        //TODO Comandos
        IPageSegmenter pageSegmenter = PageSegmenterFactory.getInstance().create();
        for (Map.Entry<OMRPage, PageView> pageEntry: pages.entrySet()) {
            OMRPage omrPage = pageEntry.getKey();
            List<Region> regions = pageSegmenter.segment(omrPage.getImageFile());
            omrPage.clearRegions();
            omrPage.addRegions(regions);
        }
    }
}
