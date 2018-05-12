package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.entities.Symbol;
import es.ua.dlsi.im3.omr.muret.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @autor drizo
 */
public class SymbolCorrectionController extends ImageBasedAbstractController {
    @FXML
    VBox regionsPane;
    @FXML
    Button btnChangeSymbol;

    AgnosticSymbolFont agnosticSymbolFont;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        regionsPane.prefHeightProperty().bind(mainPane.heightProperty());
        regionsPane.prefWidthProperty().bind(mainPane.widthProperty());
        btnChangeSymbol.disableProperty().bind(selectedSymbol.isNull());
    }

    @Override
    public void setOMRImage(OMRImage omrImage) throws IM3Exception {
        agnosticSymbolFont = new AgnosticSymbolFonts().getAgnosticSymbolFont(omrImage.getOmrProject().getNotationType());
        super.setOMRImage(omrImage);
    }

    @Override
    protected BoundingBoxBasedView addSymbol(BoundingBoxBasedView regionView, OMRSymbol omrSymbol) throws IM3Exception {
        RegionView regionViewCast = (RegionView) regionView;
        SymbolView symbolView = new SymbolView(regionViewCast, omrSymbol, Color.DARKGREEN); //TODO
        regionViewCast.addSymbolView(symbolView);
        return symbolView;
    }
    @Override
    protected BoundingBoxBasedView addRegion(BoundingBoxBasedView pageView, OMRRegion omrRegion) throws IM3Exception {
        RegionView regionView = new RegionView(agnosticSymbolFont, (PageView) pageView, omrRegion, Color.RED); //TODO;
        regionsPane.getChildren().add(regionView);

        return regionView;
    }

    @Override
    protected BoundingBoxBasedView addPage(OMRPage omrPage) {
        return new PageView(omrPage, Color.BLUE); //TODO
    }

    @Override
    protected void doDeleteTreeItems() throws IM3Exception {

    }

    @Override
    protected double getZoomToFitRatio() {
        return 0;
    }

    @FXML
    private void handleGotoDocumentAnalysis() {
        this.dashboard.openImageDocumentAnalysis(omrImage);
    }

    @FXML
    private void handleRecognizeSymbols() {
        throw new UnsupportedOperationException("TO-DO"); // TODO región seleccionada
    }

    @FXML
    private void handleChangeSymbol() {
        ((SymbolView)selectedSymbol.get()).doEdit();
    }

}
