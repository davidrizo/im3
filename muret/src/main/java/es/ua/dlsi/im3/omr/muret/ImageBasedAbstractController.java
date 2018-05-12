package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.model.*;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.*;

/**
 * Used as a convenience class to contain common behaviour and properties for both DocumentAnalysisController and SymbolCorrectionController
 * @autor drizo
 */
public abstract class ImageBasedAbstractController extends MuretAbstractController implements Initializable  {
    @FXML
    protected ScrollPane scrollPane;

    @FXML
    protected Pane mainPane;

    @FXML
    protected TreeView treeView;

    @FXML
    protected TextArea textAreaComments;

    protected DoubleProperty scale;

    protected HashSet<BoundingBoxBasedView> selectedElements;

    protected BooleanProperty symbolSelectionBasedActionsEnabled;

    private HashMap<IOMRBoundingBox, BoundingBoxBasedView> elements; // the superset of pages, regions and symbols

    protected OMRImage omrImage;

    protected ObjectProperty<BoundingBoxBasedView> selectedSymbol;

    @Override
    public Node getRoot() {
        return scrollPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedSymbol = new SimpleObjectProperty<>();

        symbolSelectionBasedActionsEnabled = new SimpleBooleanProperty(false);
        // TODO: 21/4/18 Que el botón de reconocimiento de regiones no esté activo si no hay páginas
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeView.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends TreeItem> observable,
                            TreeItem old_val, TreeItem new_val) {
                        /*TreeItem selectedItem = new_val;
                        if (selectedItem.getValue() instanceof IOMRBoundingBox) {
                            BoundingBoxBasedView boundingBoxBasedView = elements.get(selectedItem.getValue());
                            if (boundingBoxBasedView == null) {
                                ShowError.show(OMRApp.getMainStage(), "Cannot find the view for " + selectedItem.getValue());
                            } else {
                                select(boundingBoxBasedView);
                            }
                        }*/
                        handleTreeSelection();
                    }
                });

        initZoom();
    }




    private void initZoom() {
        scale = new SimpleDoubleProperty(1);
        Scale scaleTransformation = new Scale();
        scaleTransformation.xProperty().bind(scale);
        scaleTransformation.yProperty().bind(scale);
        scaleTransformation.pivotXProperty().bind(mainPane.layoutXProperty());
        scaleTransformation.pivotYProperty().bind(mainPane.layoutYProperty());
        mainPane.getTransforms().add(scaleTransformation);
    }

    protected void handleTreeSelection() {
        for (BoundingBoxBasedView boundingBoxBasedView: selectedElements) {
            boundingBoxBasedView.highlight(false);
        }
        selectedElements.clear();

        symbolSelectionBasedActionsEnabled.setValue(false);
        ObservableList<TreeItem> selectedItems = treeView.getSelectionModel().getSelectedItems();
        for (TreeItem treeItem: selectedItems) {
            if (treeItem != null) {
                Object value = treeItem.getValue();
                if (value != null && value instanceof IOMRBoundingBox) {
                    BoundingBoxBasedView boundingBoxBasedView = elements.get(value);

                    if (boundingBoxBasedView.getOwner() instanceof OMRSymbol) {
                        if (selectedElements.isEmpty()) {
                            symbolSelectionBasedActionsEnabled.setValue(true);
                        }
                    } else {
                        symbolSelectionBasedActionsEnabled.setValue(false);
                    }

                    if (boundingBoxBasedView == null) {
                        ShowError.show(OMRApp.getMainStage(), "Cannot find the view for " + value);
                    } else {
                        selectedElements.add(boundingBoxBasedView);
                        boundingBoxBasedView.highlight(true);
                        //JavaFXUtils.ensureVisibleX(scrollPane, boundingBoxBasedView); //TODO No va bien, además, luego el zoomToFit no va
                    }
                }
            }
        }

        selectedSymbol.setValue(null);
        if (selectedElements.size() == 1) {
            BoundingBoxBasedView selected = selectedElements.iterator().next();
            if (selected.getOwner() instanceof OMRSymbol) {
                selectedSymbol.set(selected);
            }
        }

    }

    public void setOMRImage(OMRImage omrImage) throws IM3Exception {
        this.omrImage = omrImage;
        textAreaComments.textProperty().bindBidirectional(omrImage.commentsProperty());
        loadPages();
    }

    protected void loadPages() throws IM3Exception {
        elements = new HashMap<>();
        selectedElements = new HashSet<>();

        treeView.setRoot(new TreeItem<>(omrImage.toString()));
        treeView.getRoot().setExpanded(true);

        for (OMRPage omrPage : omrImage.getPages()) {
            createAndAddPageView(omrPage);
        }

    }

    private void createAndAddPageView(OMRPage omrPage) throws IM3Exception {
        TreeItem pageTreeItem = new TreeItem<>(omrPage);
        pageTreeItem.setExpanded(true);
        treeView.getRoot().getChildren().add(pageTreeItem);

        BoundingBoxBasedView pageView = addPage(omrPage);
        elements.put(omrPage, pageView);

        for (OMRRegion omrRegion: omrPage.getRegions()) {
            TreeItem regionTreeItem = new TreeItem<>(omrRegion);
            pageTreeItem.getChildren().add(regionTreeItem);
            regionTreeItem.setExpanded(true);

            BoundingBoxBasedView regionView = addRegion(pageView, omrRegion);
            elements.put(omrRegion, regionView);
            
            for (OMRSymbol omrSymbol: omrRegion.symbolsProperty()) {
                TreeItem symbolTreeItem = new TreeItem<>(omrSymbol);
                regionTreeItem.getChildren().add(symbolTreeItem);
                elements.put(omrSymbol, addSymbol(regionView, omrSymbol));
            }
        }
    }

    protected abstract BoundingBoxBasedView addSymbol(BoundingBoxBasedView regionView, OMRSymbol omrSymbol) throws IM3Exception;

    protected abstract BoundingBoxBasedView addRegion(BoundingBoxBasedView pageView, OMRRegion omrRegion) throws IM3Exception;

    protected abstract BoundingBoxBasedView addPage(OMRPage omrPage);


    @FXML
    private void handleDeleteTreeItem() {
        try {
            doDeleteTreeItems();
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot delete tree item", e);
        }
    }

    protected abstract void doDeleteTreeItems() throws IM3Exception;


    @FXML
    private void handleZoomIn() {
        scale.setValue(scale.doubleValue()+0.1);
    }

    @FXML
    private void handleZoomOut() {
        scale.setValue(scale.doubleValue()-0.1);

    }

    @FXML
    private void handleZoomReset() {
        scale.setValue(1);
    }

    @FXML
    private void handleZoomToFit() {
        scale.setValue(getZoomToFitRatio());
    }

    protected abstract double getZoomToFitRatio();

    @FXML
    private void handleCollapse() {
        doCollapseTreeView();
    }

    protected void doCollapseTreeView() {
        if (treeView.getRoot() != null) {
            treeView.getRoot().setExpanded(true);
            List<TreeItem> pages= treeView.getRoot().getChildren();
            for (TreeItem page: pages) {
                page.setExpanded(true); // pages

                List<TreeItem> regions= page.getChildren();
                for (TreeItem region: regions) {
                    region.setExpanded(false);
                }
            }
        }
    }

}
