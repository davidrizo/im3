package es.ua.dlsi.im3.omr.interactive.pages;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.command.ICommand;
import es.ua.dlsi.im3.gui.command.IObservableTaskRunner;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowConfirmation;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.DashboardController;
import es.ua.dlsi.im3.omr.interactive.OMRApp;
import es.ua.dlsi.im3.omr.interactive.model.OMRModel;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PagesController implements Initializable {
    @FXML
    FlowPane flowPane;

    IconAdd iconAdd;

    State state;

    HashMap<OMRPage, PageThumbnailView> pagePageThumbnailViewHashMap;
    private DashboardController dashboard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        state = State.idle; //TODO cambiar estado en drag & drop
        iconAdd = new IconAdd();
        pagePageThumbnailViewHashMap = new HashMap<>();
        loadProjectPages();
        listenToPagesChanges();
    }

    private void listenToPagesChanges() {
        OMRModel.getInstance().getCurrentProject().pagesProperty().addListener(new ListChangeListener<OMRPage>() {
            @Override
            public void onChanged(Change<? extends OMRPage> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        /*for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //TODO permutar
                        }*/
                    } else if (c.wasUpdated()) {
                        //update item - no lo necesitamos de momento porque lo tenemos todo con binding, si no podríamos actualizar aquí
                    } else {
                        for (OMRPage remitem : c.getRemoved()) {
                            removePageView(remitem);
                        }
                        for (OMRPage additem : c.getAddedSubList()) {
                            createPageView(additem, true);
                        }
                    }
                }
            }
        });
    }

    private void loadProjectPages() {
        try {
            for (OMRPage omrPage: OMRModel.getInstance().getCurrentProject().pagesProperty()) {
                createPageView(omrPage, false);
            }
            addPageAddIcon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPageView(OMRPage omrPage, boolean skipLoadIcon) {
        PageThumbnailView pageView = new PageThumbnailView(this, omrPage);
        if (skipLoadIcon) {
            flowPane.getChildren().add(flowPane.getChildren().size() - 1, pageView); // before the addImage icon
        } else {
            flowPane.getChildren().add(pageView); // before the addImage icon
        }
        pagePageThumbnailViewHashMap.put(omrPage, pageView);
        addInteraction(pageView);
    }

    private void removePageView(OMRPage page) {
        PageThumbnailView view = pagePageThumbnailViewHashMap.get(page);
        if (view == null) {
            throw new IM3RuntimeException("Cannot find page " + page);
        }
        flowPane.getChildren().remove(view);
        recomputeOrdering();
    }



    private void addPageAddIcon() {
        flowPane.getChildren().add(iconAdd.getRoot());
        //TODO Refactorizar - un tipo Icono
        iconAdd.getRoot().setOnMouseEntered(event -> {
            iconAdd.highlight(true);
        });
        iconAdd.getRoot().setOnMouseExited(event -> {
            iconAdd.highlight(false);
        });
        iconAdd.getRoot().setOnMouseClicked(event -> {
            doAddImages();
        });
    }


    private void addInteraction(PageThumbnailView pageView) {
        pageView.setOnDragDetected(event -> {
            Dragboard db = pageView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            // Store the node ID in order to know what is dragged.
            content.putString(new Integer(pageView.getOmrPage().getOrder()-1).toString());
            db.setContent(content);
            event.consume();
        });

        pageView.setOpenPageHandler(handler -> {
            doOpenPage(pageView);
        });

        pageView.setDeletePageHandler(handler -> {
            doDeletePage(pageView);
        });

        initDropZone(pageView.getLeftDropbox(), pageView, false);
        initDropZone(pageView.getRightDropbox(), pageView, true);

        pageView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                doOpenPage(pageView);
            }
        });

        pageView.setOnMouseEntered(event -> {
            pageView.highlight(true);
        });

        pageView.setOnMouseExited(event -> {
            pageView.highlight(false);
        });

    }

    /**
     *
     * @param dropbox
     * @param pageView Use object rather than target ordering to keep the orderings updated
     * @param insertAfter
     */
    private void initDropZone(Rectangle dropbox, PageThumbnailView pageView, boolean insertAfter) {
        dropbox.setOnDragEntered(event -> {
            //TODO Refactorizar
            dropbox.setFill(Color.GREY);
        });

        dropbox.setOnDragExited(event -> {
            //TODO Refactorizar
            dropbox.setFill(Color.TRANSPARENT);
        });

        dropbox.setOnDragOver((DragEvent event) -> {
            //TODOif (event.getGestureSource() != bodyImage &&
            //  event.getDragboard().hasString()) {

            event.acceptTransferModes(TransferMode.MOVE);
        });

        dropbox.setOnDragDropped(event -> {
            //TODO Refactorizar
            // Reorder pages
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int toOrder = pageView.getOmrPage().getOrder()-1;
                if (insertAfter) {
                    toOrder++;
                }
                Logger.getLogger(PagesController.class.getName()).log(Level.INFO, "Drag&drop: moving page at position {0} to position {1}", new Object[]{db.getString(), toOrder});
                moveThumbnail(pageView, Integer.parseInt(db.getString()), toOrder);

                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

    private void moveThumbnail(final PageThumbnailView page, final int fromOrder, final int toOrder) {
        ICommand command = new ICommand() {
            ArrayList<PageThumbnailView> currentOrdering;
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                currentOrdering = new ArrayList<>();
                for (int i=0; i<flowPane.getChildren().size()-1; i++) {
                    currentOrdering.add((PageThumbnailView) flowPane.getChildren().get(i));
                }
                doExecute();
            }

            private void doExecute() {
                // remove thumbnail from list
                PageThumbnailView thumbnailNode = (PageThumbnailView) flowPane.getChildren().remove(fromOrder);

                int to = toOrder;
                if (fromOrder < toOrder) {
                    to--; // because we have removed it from lists
                }

                if (thumbnailNode == null) {
                    throw new IM3RuntimeException("No thumbnail at index " + fromOrder + " in flowPane");
                }

                // add to position
                flowPane.getChildren().add(to, thumbnailNode);

                // recompute ordering
                recomputeOrdering();
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                // set the saved ordering
                flowPane.getChildren().clear();
                for (PageThumbnailView pageThumbnailView: currentOrdering) {
                    flowPane.getChildren().add(pageThumbnailView);
                }
                flowPane.getChildren().add(iconAdd.getRoot());
                recomputeOrdering();
            }

            @Override
            public void redo() throws Exception {
                doExecute();
            }

            @Override
            public String getEventName() {
                return "Move page";
            }

            @Override
            public String toString() {
                return "Move " + page.toString();
            }
        };
        try {
            dashboard.getCommandManager().executeCommand(command);
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot move", e);
        }

    }

    private void recomputeOrdering() {
        for (int i = 0; i< flowPane.getChildren().size()-1; i++) {
            PageThumbnailView tv = (PageThumbnailView) flowPane.getChildren().get(i);
            tv.getOmrPage().setOrder(i+1);
            tv.updateLabel();
        }
    }

    private void doAddImages() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        List<File> files = dlg.openFiles("Select an image", new String[]{"JPG", "TIF", "TIFF"}, new String[]{"jpg", "tif", "tiff"});

        if (files != null) {
            try {
                ICommand command = new ICommand() {
                    ArrayList<OMRPage> addedPages;
                    ArrayList<File> sortedFiles;
                    @Override
                    public void execute(IObservableTaskRunner observer) throws Exception {
                        addedPages = new ArrayList<>();
                        sortedFiles = new ArrayList<>(files); // cannot sort directly files (UnsupportedOperationException)
                        // sort by name before adding it
                        sortedFiles.sort(new Comparator<File>() {
                            @Override
                            public int compare(File o1, File o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                        doExecute();
                        dashboard.save();
                    }

                    private void doExecute() throws IM3Exception {
                        for (File file: sortedFiles) {
                            addedPages.add(OMRModel.getInstance().getCurrentProject().addPage(file));
                        }
                    }

                    @Override
                    public boolean canBeUndone() {
                        return false; //TODO Habrá que evitar que se copien los ficheros directamente en el modelo
                    }

                    @Override
                    public void undo() throws Exception {
                        for (OMRPage page: addedPages) {
                            OMRModel.getInstance().getCurrentProject().deletePage(page);
                        }
                    }

                    @Override
                    public void redo() throws Exception {
                        doExecute();
                    }

                    @Override
                    public String getEventName() {
                        return "Add page";
                    }

                    @Override
                    public String toString() {
                        return "Add page(s)";
                    }
                };

                dashboard.getCommandManager().executeCommand(command);
            } catch (IM3Exception e) {
                e.printStackTrace();
                ShowError.show(OMRApp.getMainStage(), "Cannot add image", e);
            }
        }
    }

    private void doDeletePage(PageThumbnailView pageView) {
        if (ShowConfirmation.show(OMRApp.getMainStage(), "Do you want to delete " + pageView.getOmrPage().toString() + "?. It cannot be undone")) {
            try {
                ICommand command = new ICommand() {
                    @Override
                    public void execute(IObservableTaskRunner observer) throws Exception {
                        doExecute();
                        dashboard.save();
                    }

                    private void doExecute() throws IM3Exception {
                        OMRModel.getInstance().getCurrentProject().deletePage(pageView.getOmrPage());
                    }

                    @Override
                    public boolean canBeUndone() {
                        return false;  //TODO Habrá que evitar que se copien los ficheros directamente en el modelo
                    }

                    @Override
                    public void undo() throws Exception {
                        OMRModel.getInstance().getCurrentProject().addPage(pageView.getOmrPage());
                    }

                    @Override
                    public void redo() throws Exception {
                        doExecute();
                    }

                    @Override
                    public String getEventName() {
                        return "Delete";
                    }

                    @Override
                    public String toString() {
                        return "Delete " + pageView.toString();
                    }
                };
                dashboard.getCommandManager().executeCommand(command);
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot delete page", e);
            }
        }
    }

    private void doOpenPage(PageThumbnailView pageView) {
        dashboard.openPage(pageView);
    }


    public void setDashboard(DashboardController dashboard) {
        this.dashboard = dashboard;
    }

    public DashboardController getDashboard() {
        return dashboard;
    }

    public CommandManager getCommandManager() {
        return dashboard.getCommandManager();
    }
}
