package es.ua.dlsi.im3.omr.muret.regions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.command.ICommand;
import es.ua.dlsi.im3.gui.command.IObservableTaskRunner;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowChoicesDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.classifiers.segmentation.ISymbolClusterer;
import es.ua.dlsi.im3.omr.classifiers.segmentation.SymbolClusterer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.IImageSymbolRecognizer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.SymbolRecognizerFactory;
import es.ua.dlsi.im3.omr.model.entities.Region;
import es.ua.dlsi.im3.omr.model.entities.Symbol;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import es.ua.dlsi.im3.omr.muret.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.muret.model.*;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

/**
 * @autor drizo
 */
public class DocumentAnalysisController extends ImageBasedAbstractController  {
    @FXML
    Button btnChangeSymbolsRegion;

    @FXML
    ToggleButton toggleBtnSplitIntoPages;

    @FXML
    ToggleButton toggleBtnSplitIntoRegions;

    @FXML
    ToggleGroup toggleGroupSplitMode;

    @FXML
    Button btnDeleteTreeItem;
    enum InteractionMode {eIdle, eSplittingPages, eSplittingRegions};

    ObjectProperty<InteractionMode> interactionMode;

    //TODO Pasar esto a un modelo
    ImageView imageView;
    private Group viewsGroup;
    private HashMap<OMRPage, PageView> pages;
    private HashMap<OMRRegion, RegionView> regions;
    private HashMap<OMRSymbol, SymbolView> symbols;

    public DocumentAnalysisController() {
        pages = new HashMap<>();
        regions = new HashMap<>();
        symbols = new HashMap<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        btnChangeSymbolsRegion.disableProperty().bind(symbolSelectionBasedActionsEnabled.not());
        btnDeleteTreeItem.disableProperty().bind(treeView.getSelectionModel().selectedItemProperty().isNull());
        initInteractionMode();
        createTreeViewContextMenu();
    }




    @FXML
    private void handleRecognizePages() {
        // TODO: 21/4/18
    }

    @FXML
    private void handleRecognizeRegions() {
        // TODO: 21/4/18
    }

    @FXML
    private void handleChangeSymbolsRegion() {
        doChangeSymbolsRegion();
    }

    private void doChangeSymbolsRegion() {
        List<OMRSymbol> selectedSymbols = new LinkedList<>();
        for (BoundingBoxBasedView boundingBoxBasedView: selectedElements) {
            if (!(boundingBoxBasedView.getOwner() instanceof OMRSymbol)) {
                ShowError.show(OMRApp.getMainStage(), "A selected element is not a symbol: " + boundingBoxBasedView.getOwner());
                return;
            }
            selectedSymbols.add((OMRSymbol) boundingBoxBasedView.getOwner());
        }


        LinkedList<OMRRegion> regions = new LinkedList<>();
        for (OMRPage page: omrImage.getPages()) {
            for (OMRRegion omrRegion: page.getRegions()) {
                regions.add(omrRegion);
            }
        }

        ShowChoicesDialog<OMRRegion> dlg = new ShowChoicesDialog<>();
        OMRRegion region = dlg.show(OMRApp.getMainStage(), "Change symbols to region", "Choose a region", regions, null);
        try {
            omrImage.changeRegion(selectedSymbols, region);
            omrImage.recomputeRegionBoundingBoxes();
            // now reload all model - we do not observe and change changes
            loadPages();
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot change region or recompute region bounding boxes", e);
        }
    }



    // TODO: 23/4/18 Botones comunes con right click treeview
    private void createTreeViewContextMenu() {
        //MenuItem entry1 = new MenuItem("Test with Icon", graphicNode);
        //MenuItem entryNewRegionWithSelectedSymbols = new MenuItem("Create new region with selected symbols");
        //entryNewRegionWithSelectedSymbols.setOnAction(ae -> doNewRegionWithSelectedSymbols());
        //entryNewRegionWithSelectedSymbols.disableProperty().bind(symbolSelectionBasedActionsEnabled);

        MenuItem moveSymbolsToRegion = new MenuItem("Move symbols to other region");
        moveSymbolsToRegion.setOnAction(ae -> doChangeSymbolsRegion());
        moveSymbolsToRegion.disableProperty().bind(symbolSelectionBasedActionsEnabled.not());
        treeView.setContextMenu(new ContextMenu(moveSymbolsToRegion));

    }
    /*private void doNewRegionWithSelectedSymbols() {
        // TODO: 23/4/18 De momento ahora creo siempre Staff
        List<OMRSymbol> selectedSymbols = new LinkedList<>();
        for (BoundingBoxBasedView boundingBoxBasedView: selectedElements) {
            if (!(boundingBoxBasedView.getOwner() instanceof OMRSymbol)) {
                ShowError.show(OMRApp.getMainStage(), "A selected element is not a symbol: " + boundingBoxBasedView.getOwner());
                return;
            }
            selectedSymbols.add((OMRSymbol) boundingBoxBasedView.getOwner());
        }
        omrImage.createRegion(RegionType.staff, selectedSymbols);
        // TODO: 23/4/18 Que se actualize el treeView
    }*/

    @FXML
    public void handleRecognizeSymbols() {
        //TODO Pasar a un modelo + comando
        IImageSymbolRecognizer symbolRecognizer = SymbolRecognizerFactory.getInstance().create();
        //TODO Proceso background - diálogo
        try {
            List<Symbol> recognizedSymbols = symbolRecognizer.recognize(omrImage.getImageFile());
            omrImage.replaceSymbols(recognizedSymbols);
            loadPages();
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot recognize symbols", e);

        }
    }

    @FXML
    public void handleDivideSymbolsIntoRegions() {
        try {
            List<OMRSymbol> allOMRSymbols = omrImage.getAllSymbols();
            List<Symbol> allSymbols = new LinkedList<>();
            for (OMRSymbol omrSymbol : allOMRSymbols) {
                allSymbols.add(omrSymbol.createPOJO());
            }

            //TODO A modelo
            ISymbolClusterer symbolClusterer = new SymbolClusterer();
            ShowChoicesDialog<Integer> choicesDialog = new ShowChoicesDialog<>();
            Integer[] staves = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
            Integer choice = choicesDialog.show(OMRApp.getMainStage(), "Division of symbols in regions", "Select the expected number of staves", staves, 6);
            if (choice != null) {
                SortedSet<Region> recognizedRegions = symbolClusterer.cluster(allSymbols, choice);
                omrImage.clear();
                OMRPage omrPage = new OMRPage(omrImage, omrImage.getBoundingBox().getFromX(), omrImage.getBoundingBox().getFromY(),
                        omrImage.getBoundingBox().getToX(), omrImage.getBoundingBox().getToY());
                omrImage.addPage(omrPage);
                int id = 1;
                for (Region region: recognizedRegions) {
                    OMRRegion omrRegion = new OMRRegion(omrPage, id++, region);
                    omrPage.addRegion(omrRegion);
                }
                loadPages();
            }
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot divide symbols", e);
        }
    }


    @FXML
    private void handleSplitModes() {
        // TODO: 25/4/18
        if (toggleGroupSplitMode.getSelectedToggle() == null) {
            interactionMode.setValue(InteractionMode.eIdle);
        } else if (toggleGroupSplitMode.getSelectedToggle() == toggleBtnSplitIntoPages) {
            interactionMode.setValue(InteractionMode.eSplittingPages);
        } else if (toggleGroupSplitMode.getSelectedToggle() == toggleBtnSplitIntoRegions) {
            interactionMode.setValue(InteractionMode.eSplittingRegions);
        } else {
            ShowError.show(OMRApp.getMainStage(), "Unknown interaction mode: " + toggleGroupSplitMode.getSelectedToggle());
        }
    }

    @Override
    protected void doDeleteTreeItems() throws IM3Exception {
        for (BoundingBoxBasedView boundingBoxBasedView : selectedElements) {
            // something more ellegant?
            boolean collapse = false;
            if (boundingBoxBasedView instanceof PageView) {
                PageView pageView = (PageView) boundingBoxBasedView;
                omrImage.deletePage(pageView.getOwner());
                collapse = true;
            } else if (boundingBoxBasedView instanceof RegionView) {
                RegionView regionView = (RegionView) boundingBoxBasedView;
                collapse = true;
            } else if (boundingBoxBasedView instanceof SymbolView) {
                SymbolView symbolView = (SymbolView) boundingBoxBasedView;
                symbolView.getOwner().getOMRRegion().removeSymbol(symbolView.getOwner());
            }
            loadPages(); //TODO Observables
            if (collapse) {
                doCollapseTreeView();
            }
        }
    }

    private void initInteractionMode() {
        interactionMode = new SimpleObjectProperty<>(InteractionMode.eIdle);

        mainPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (interactionMode.get() == InteractionMode.eSplittingPages) {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                        doSplitPage(event.getX(), event.getY());
                    }
                } else if (interactionMode.get() == InteractionMode.eSplittingRegions) {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                        doSplitRegion(event.getX(), event.getY());
                    }
                }
            }
        });
    }

    private void doSplitRegion(double x, double y) {
        ICommand command = new ICommand() {
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                omrImage.splitRegionAt(x, y);
                loadPages(); //TODO observables
            }

            @Override
            public boolean canBeUndone() {
                return false; //TODO
            }

            @Override
            public void undo() throws Exception {

            }

            @Override
            public void redo() throws Exception {

            }

            @Override
            public String getEventName() {
                return "Create regions";
            }
        };
        try {
            getDashboard().getCommandManager().executeCommand(command);
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot split region", e);
        }
    }

    private void doSplitPage(double x, double y) {
        ICommand command = new ICommand() {
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                omrImage.splitPageAt(x);
                loadPages(); //TODO observables
            }

            @Override
            public boolean canBeUndone() {
                return false; //TODO
            }

            @Override
            public void undo() throws Exception {

            }

            @Override
            public void redo() throws Exception {

            }

            @Override
            public String getEventName() {
                return "Create regions";
            }
        };
        try {
            getDashboard().getCommandManager().executeCommand(command);
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot split page", e);
        }
    }

    @FXML
    private void handleSetOnePageAndRegion() {
        try {
            omrImage.leaveJustOnePageAndRegion();
            loadPages();
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot set everything in one page and region", e);
        }
    }

    @Override
    public void setOMRImage(OMRImage omrImage) throws IM3Exception {
        viewsGroup = new Group();

        imageView = new ImageView(omrImage.getImage());
        imageView.setPreserveRatio(true);
        mainPane.setMinWidth(imageView.getImage().getWidth());
        mainPane.setMinHeight(imageView.getImage().getHeight());
        mainPane.getChildren().add(imageView);
        mainPane.getChildren().add(viewsGroup);

        super.setOMRImage(omrImage);
    }

    @Override
    protected void loadPages() throws IM3Exception {
        viewsGroup.getChildren().clear();
        super.loadPages();
    }

    @Override
    protected BoundingBoxBasedView addSymbol(BoundingBoxBasedView regionView, OMRSymbol omrSymbol) {
        SymbolView symbolView = new SymbolView((RegionView) regionView, omrSymbol, Color.GREEN);
        symbols.put(omrSymbol, symbolView);
        viewsGroup.getChildren().add(symbolView);
        return symbolView;
    }

    @Override
    protected BoundingBoxBasedView addRegion(BoundingBoxBasedView pageView, OMRRegion omrRegion) {
        RegionView regionView = new RegionView((PageView) pageView, omrRegion, Color.RED);
        regions.put(omrRegion, regionView);
        viewsGroup.getChildren().add(regionView);
        return regionView;
    }

    @Override
    protected BoundingBoxBasedView addPage(OMRPage omrPage) {
        PageView pageView = new PageView(omrPage, Color.BLUE); // TODO: 21/4/18 Colores
        pages.put(omrPage, pageView);
        viewsGroup.getChildren().add(pageView);
        return pageView;
    }

    @Override
    protected double getZoomToFitRatio() {
        double xRatio = this.scrollPane.getViewportBounds().getWidth() / this.imageView.getLayoutBounds().getWidth();
        double yRatio = this.scrollPane.getViewportBounds().getHeight() / this.imageView.getLayoutBounds().getHeight();
        if (xRatio > yRatio) {
            return xRatio;
        } else {
             return yRatio;
        }
    }

    @FXML
    private void handleGotoSymbolCorrection() {
        this.dashboard.openImageSymbolCorrection(omrImage);
    }
}
