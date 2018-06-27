package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.interaction.ISelectableTraversable;
import es.ua.dlsi.im3.gui.interaction.ISelectionChangeListener;
import es.ua.dlsi.im3.gui.interaction.SelectionManager;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.model.*;
import es.ua.dlsi.im3.omr.muret.symbols.SymbolView;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used as a convenience class to contain common behaviour and properties for both DocumentAnalysisController and SymbolCorrectionController.
 *
 * The selection is managed here, not in the element itself
 * @autor drizo
 */
public abstract class ImageBasedAbstractController extends MuretAbstractController implements Initializable, ISelectableTraversable {
    @FXML
    protected ScrollPane scrollPane;

    @FXML
    protected Pane mainPane;

    /**
     * Values of tree items contain the model object (OMRPage, OMRRegion, OMRSymbol)
     */
    @FXML
    protected TreeView treeView;

    /**
     * The equivalence between OMRPage, OMRRegion, OMRSymbol and TreeItem
     */
    private HashMap<IOMRBoundingBox, TreeItem> mapOMRElementTreItem;
    /**
     * The superset of pages, regions and symbols and their views
     */
    private HashMap<IOMRBoundingBox, BoundingBoxBasedView> mapOMRElementView;

    @FXML
    protected TextArea textAreaComments;

    protected DoubleProperty scale;

    protected BooleanProperty symbolSelectionBasedActionsEnabled;

    protected OMRImage omrImage;

    @Override
    public Node getRoot() {
        return scrollPane;
    }

    protected KeyEventManager keyEventManager;
    protected SelectionManager selectionManager;

    private long nextID = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapOMRElementTreItem = new HashMap<>();
        initTreeViewInteraction();
        initZoom();
        registerKeyEventManager();
        initSelectionManager();
    }

    protected String getNextID(String prefix) {
        String result = prefix + nextID;
        nextID++;
        return result;
    }

    private void initSelectionManager() {
        symbolSelectionBasedActionsEnabled = new SimpleBooleanProperty(false);
        selectionManager = new SelectionManager();
        selectionManager.subscribe(new ISelectionChangeListener() {
            @Override
            public void onSelectionChange(Collection<ISelectable> selection) {
                doOnSelectionChange(selection);
            }
        });
    }

    private void doOnSelectionChange(Collection<ISelectable> selection) {
        symbolSelectionBasedActionsEnabled.setValue(selectionManager.isCommonBaseClass(IOMRSymbolBaseView.class));
    }

    private void initTreeViewInteraction() {
        // TODO: 21/4/18 Que el botón de reconocimiento de regiones no esté activo si no hay páginas
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change c) {
                handleTreeSelection();
            }
        });
    }

    private void registerKeyEventManager() {
        keyEventManager = OMRApp.getKeyEventManager();
        keyEventManager.setCurrentKeyEventHandler(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                // propagate to all selected mapOMRElementView until one consumes it
                Class<?> baseClass = selectionManager.getCommonBaseClass();
                if (baseClass != null && BoundingBoxBasedView.class.isAssignableFrom(baseClass)) {
                    for (ISelectable selectable: selectionManager.getSelection()) {
                        BoundingBoxBasedView boundingBoxBasedView = (BoundingBoxBasedView) selectable;
                        boundingBoxBasedView.handle(event);
                    }
                }
            }
        });
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
        selectionManager.clearSelection();
        ObservableList<TreeItem> selectedItems = treeView.getSelectionModel().getSelectedItems();
        selectionManager.clearSelection();
        LinkedList<ISelectable> selection = new LinkedList<>();
        for (TreeItem treeItem: selectedItems) {
            if (treeItem != null) {
                Object value = treeItem.getValue();
                BoundingBoxBasedView boundingBoxBasedView = mapOMRElementView.get(value);
                selection.add(boundingBoxBasedView);
            }
        }
        selectionManager.select(selection);
    }

    public void setOMRImage(OMRImage omrImage) throws IM3Exception {
        this.omrImage = omrImage;
        textAreaComments.textProperty().bindBidirectional(omrImage.commentsProperty());
        loadPages();
    }

    protected void loadPages() throws IM3Exception {
        mapOMRElementView = new HashMap<>();

        treeView.setRoot(new TreeItem<>(omrImage.toString()));
        treeView.getRoot().setExpanded(true);

        for (OMRPage omrPage : omrImage.getPages()) {
            createAndAddPageView(omrPage);
        }

        selectionManager.clearSelection();
    }

    private void createAndAddPageView(OMRPage omrPage) throws IM3Exception {
        TreeItem pageTreeItem = new TreeItem<>(omrPage);
        pageTreeItem.setExpanded(true);
        treeView.getRoot().getChildren().add(pageTreeItem);
        mapOMRElementTreItem.put(omrPage, pageTreeItem);
        BoundingBoxBasedView pageView = addPage(omrPage);
        mapOMRElementView.put(omrPage, pageView);

        // listen to model changes
        omrPage.regionsProperty().addListener(new SetChangeListener<OMRRegion>() {
            @Override
            public void onChanged(Change<? extends OMRRegion> change) {
                Logger.getLogger(ImageBasedAbstractController.class.getName()).log(Level.INFO, "Page region changed {0}", change);
                //TODO Inserción
                if (change.wasRemoved()) {
                    OMRRegion removedElement = change.getElementRemoved();
                    TreeItem removedTreeItem = mapOMRElementTreItem.get(removedElement);
                    if (removedTreeItem == null) {
                        throw new IM3RuntimeException("Cannot find the removed tree item for " + removedElement);
                    }
                    pageTreeItem.getChildren().remove(removedTreeItem);
                    mapOMRElementView.remove(removedElement);
                } else if (change.wasAdded()) {
                    //ShowError.show(null, "TODO ADD " + change); //TODO
                    System.err.println("TODO ADD " + change); //TODO Change
                }
            }
        });
        for (OMRRegion omrRegion: omrPage.regionsProperty()) {
            TreeItem regionTreeItem = new TreeItem<>(omrRegion);
            mapOMRElementTreItem.put(omrRegion, regionTreeItem);
            pageTreeItem.getChildren().add(regionTreeItem);
            regionTreeItem.setExpanded(true);

            BoundingBoxBasedView regionView = addRegion(pageView, omrRegion);
            mapOMRElementView.put(omrRegion, regionView);

            omrRegion.symbolsProperty().addListener(new SetChangeListener<OMRSymbol>() {
                @Override
                public void onChanged(Change<? extends OMRSymbol> change) {
                    Logger.getLogger(ImageBasedAbstractController.class.getName()).log(Level.INFO, "Region symbol changed {0}", change);
                    //TODO Inserción
                    if (change.wasRemoved()) {
                        OMRSymbol removedElement = change.getElementRemoved();
                        TreeItem removedTreeItem = mapOMRElementTreItem.get(removedElement);
                        if (removedTreeItem == null) {
                            throw new IM3RuntimeException("Cannot find the removed tree item for " + removedElement);
                        }
                        regionTreeItem.getChildren().remove(removedTreeItem);
                        BoundingBoxBasedView elementView = mapOMRElementView.remove(removedElement);
                        if (elementView == null) {
                            throw new IM3RuntimeException("Cannot find the removed item view for " + removedElement);
                        }
                        regionView.onSymbolRemoved(elementView);
                    } else if (change.wasAdded()) {
                        // TODO: 21/5/18 Ver por qué esto se lanza varias veces
                        OMRSymbol addedElement = change.getElementAdded();
                        try {
                            addSymbolViewToRegion(regionView, regionTreeItem, addedElement);
                        } catch (IM3Exception e) {
                            Logger.getLogger(ImageBasedAbstractController.class.getName()).log(Level.INFO, "Cannot add new symbol", e);
                            ShowError.show(OMRApp.getMainStage(), "Cannot add new symbol", e);
                        }
                    }
                }
            });

            for (OMRSymbol omrSymbol: omrRegion.symbolsProperty()) {
                addSymbolViewToRegion(regionView, regionTreeItem, omrSymbol);
            }
        }
    }

    private void addSymbolViewToRegion(BoundingBoxBasedView regionView, TreeItem regionTreeItem, OMRSymbol omrSymbol) throws IM3Exception {
        TreeItem symbolTreeItem = new TreeItem<>(omrSymbol);
        mapOMRElementTreItem.put(omrSymbol, symbolTreeItem);
        mapOMRElementView.put(omrSymbol, addSymbol(regionView, omrSymbol));

        // look for the correct position
        int indexToInsert = regionTreeItem.getChildren().size();
        for (int i=0; i<regionTreeItem.getChildren().size(); i++) {
            Object child = regionTreeItem.getChildren().get(i);
            OMRSymbol childSymbol = (OMRSymbol) ((TreeItem)child).getValue();
            if (omrSymbol.compareTo(childSymbol) < 0) {
                indexToInsert = i;
                break;
            }
        }
        regionTreeItem.getChildren().add(indexToInsert, symbolTreeItem);
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

    public void onSymbolChanged(OMRSymbol owner) throws IM3Exception {
        TreeItem treeItem = mapOMRElementTreItem.get(owner);
        if (treeItem != null) {
            treeItem.setValue(null); // force reference change for updating view
            treeItem.setValue(owner);
        } else {
            throw new IM3Exception("Cannot find the symbol " + owner + " in the treeview");
        }
    }

    /**
     * It selects the view object
     * @param boundingBoxBasedView
     * @param <OwnerType>
     */
    public <OwnerType extends IOMRBoundingBox> void doSelect(BoundingBoxBasedView<OwnerType> boundingBoxBasedView) {
        treeView.getSelectionModel().clearSelection();
        treeView.getSelectionModel().select(mapOMRElementTreItem.get(boundingBoxBasedView.getOwner())); // it should propagate the selection
    }

    /**
     * It selects the model object
     * @param omrSymbol
     * @return View object
     */
    public BoundingBoxBasedView doSelect(OMRSymbol omrSymbol) throws IM3Exception {
        BoundingBoxBasedView viewObject = mapOMRElementView.get(omrSymbol);
        if (viewObject == null) {
            throw new IM3Exception("Cannot find a view object for " + omrSymbol);
        }
        doSelect(viewObject);
        return viewObject;
    }

    public void unselect() {
        selectionManager.clearSelection();
    }



}
