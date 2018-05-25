package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.entities.Symbol;
import es.ua.dlsi.im3.omr.muret.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @autor drizo
 */
public class SymbolCorrectionController extends ImageBasedAbstractController {
    @FXML
    BorderPane mainBorderPane;
    @FXML
    VBox regionsPane;
    @FXML
    Button btnChangeSymbol;
    @FXML
    ToggleButton toggleBtnAddSymbol;
    @FXML
    ToggleButton toggleButtonEditMusic;

    AgnosticSymbolFont agnosticSymbolFont;

    BooleanProperty addingSymbol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        regionsPane.prefHeightProperty().bind(mainPane.heightProperty());
        regionsPane.prefWidthProperty().bind(mainPane.widthProperty());
        btnChangeSymbol.disableProperty().bind(selectedSymbol.isNull());
        initAddSymbolInteraction();
    }

    @Override
    public void setOMRImage(OMRImage omrImage) throws IM3Exception {
        agnosticSymbolFont = new AgnosticSymbolFonts().getAgnosticSymbolFont(omrImage.getOmrProject().getNotationType());
        super.setOMRImage(omrImage);
    }

    @Override
    protected BoundingBoxBasedView addSymbol(BoundingBoxBasedView regionView, OMRSymbol omrSymbol) throws IM3Exception {
        RegionView regionViewCast = (RegionView) regionView;
        SymbolView symbolView = new SymbolView(this, regionViewCast, omrSymbol, Color.DARKGREEN); //TODO
        regionViewCast.addSymbolView(symbolView);
        return symbolView;
    }
    @Override
    protected BoundingBoxBasedView addRegion(BoundingBoxBasedView pageView, OMRRegion omrRegion) throws IM3Exception {
        RegionView regionView = new RegionView(this, agnosticSymbolFont, (PageView) pageView, omrRegion, Color.RED); //TODO;
        regionsPane.getChildren().add(regionView);

        return regionView;
    }

    @Override
    protected BoundingBoxBasedView addPage(OMRPage omrPage) {
        return new PageView(this, omrPage, Color.BLUE); //TODO
    }

    @Override
    protected void doDeleteTreeItems() throws IM3Exception {
        if (selectedSymbol.get() instanceof SymbolView) {
            SymbolView symbolView = (SymbolView) selectedSymbol.get();
            symbolView.getOwner().getOMRRegion().removeSymbol(symbolView.getOwner());
        }
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
        doChangeSymbol();
    }

    public void doChangeSymbol() {
        ((SymbolView)selectedSymbol.get()).doEdit();
    }


    private void initAddSymbolInteraction() {
        addingSymbol = new SimpleBooleanProperty(false);
        toggleBtnAddSymbol.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                addingSymbol.setValue(newValue);
            }
        });
    }

    public boolean isAddingSymbol() {
        return addingSymbol.get();
    }

    public BooleanProperty addingSymbolProperty() {
        return addingSymbol;
    }

    public void setSymbolCorrectionToolbar(Node toolbar) {
        mainBorderPane.setBottom(toolbar);
    }

    public void removeSymbolCorrectionToolbar() {
        mainBorderPane.setBottom(null);
    }
}
