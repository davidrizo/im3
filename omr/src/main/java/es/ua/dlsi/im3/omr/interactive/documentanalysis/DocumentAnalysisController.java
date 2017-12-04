package es.ua.dlsi.im3.omr.interactive.documentanalysis;

import es.ua.dlsi.im3.omr.interactive.DashboardController;
import es.ua.dlsi.im3.omr.interactive.model.OMRInstrument;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DocumentAnalysisController implements Initializable {
    @FXML
    ToolBar toolbar;

    @FXML
    VBox vboxPages;

    ToggleGroup tgInstruments;

    private DashboardController dashboard;
    private OMRPage selectedOMRPage;
    private List<OMRPage> pages;

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
        selectedOMRPage = omrPage;
        pages = pagesToOpen;

        // create the buttons
        createInstrumentButtons();
        createImageViews();
    }

    private void createInstrumentButtons() {
        toolbar.getItems().add(0, new Label("Select an instrument"));
        int i=1;
        for (OMRInstrument instrument: selectedOMRPage.getInstrumentList()) {
            ToggleButton button = new ToggleButton(instrument.getName());
            button.setToggleGroup(tgInstruments);
            toolbar.getItems().add(i++, button); // add before other buttons and separator
        }
    }

    private void createImageViews() {
        for (OMRPage omrPage: pages) {
            ImageView imageView = new ImageView();
            imageView.fitWidthProperty().bind(vboxPages.widthProperty());
            imageView.setPreserveRatio(true);
            imageView.setImage(SwingFXUtils.toFXImage(omrPage.getBufferedImage(), null));
            vboxPages.getChildren().add(imageView);
            if (omrPage == selectedOMRPage) {
                imageView.requestFocus(); //TODO
                //que se seleccione - poner iconos para navegar a las páginas directament
            }
        }
    }


}
