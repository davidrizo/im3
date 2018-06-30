package es.ua.dlsi.im3.omr.muret.images;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.command.ICommand;
import es.ua.dlsi.im3.gui.command.IObservableTaskRunner;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowConfirmation;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.DashboardController;
import es.ua.dlsi.im3.omr.muret.MuretAbstractController;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.muret.model.OMRModel;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
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

public class ImagesController extends MuretAbstractController {
    @FXML
    FlowPane flowPane;
    @FXML
    Label labelProjectPath;
    @FXML
    ScrollPane scrollPane;

    @FXML
    TextArea textAreaComentarios;

    IconAdd iconAdd;

    State state;

    HashMap<OMRImage, ImageThumbnailView> imageThumbnailViewHashMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        state = State.idle; //TODO cambiar estado en drag & drop
        iconAdd = new IconAdd();
        imageThumbnailViewHashMap = new HashMap<>();
        flowPane.prefWidthProperty().bind(scrollPane.widthProperty());
    }

    private void listenToImagesChanges() {
        /*dashboard.getModel().getCurrentProject().imagesProperty().addListener(new ListChangeListener<OMRImage>() {
            @Override
            public void onChanged(Change<? extends OMRImage> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        //for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        //    //TODO permutar
                        //}
                    } else if (c.wasUpdated()) {
                        //update item - no lo necesitamos de momento porque lo tenemos todo con binding, si no podríamos actualizar aquí
                    } else {
                        for (OMRImage remitem : c.getRemoved()) {
                            removeImageView(remitem);
                        }
                        for (OMRImage additem : c.getAddedSubList()) {
                            createImageView(additem, true);
                        }
                    }
                }
            }
        });*/
    }

    private void loadProjectImages() {
        textAreaComentarios.textProperty().bindBidirectional(getModel().getCurrentProject().commentsProperty());

        try {
            for (OMRImage OMRImage: dashboard.getModel().getCurrentProject().imagesProperty()) {
                createImageView(OMRImage, false);
            }
            addImageAddIcon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createImageView(OMRImage omrImage, boolean skipLoadIcon) {
        ImageThumbnailView imageView = new ImageThumbnailView(this, omrImage);
        if (skipLoadIcon) {
            flowPane.getChildren().add(flowPane.getChildren().size() - 1, imageView); // before the addImage icon
        } else {
            flowPane.getChildren().add(imageView); // before the addImage icon
        }
        imageThumbnailViewHashMap.put(omrImage, imageView);
        addInteraction(imageView);
    }

    private void removeImageView(OMRImage image) {
        ImageThumbnailView view = imageThumbnailViewHashMap.get(image);
        if (view == null) {
            throw new IM3RuntimeException("Cannot find image " + image);
        }
        flowPane.getChildren().remove(view);
        recomputeOrdering();
    }



    private void addImageAddIcon() {
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


    private void addInteraction(ImageThumbnailView imageView) {
        imageView.setOnDragDetected(event -> {
            Dragboard db = imageView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            // Store the node ID in order to know what is dragged.
            content.putString(new Integer(imageView.getOMRImage().getOrder()-1).toString());
            db.setContent(content);
            event.consume();
        });

        imageView.setOpenImageHandler(handler -> {
            doOpenImage(imageView);
        });

        imageView.setDeleteImageHandler(handler -> {
            doDeleteImage(imageView);
        });

        initDropZone(imageView.getLeftDropbox(), imageView, false);
        initDropZone(imageView.getRightDropbox(), imageView, true);

        imageView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                doOpenImage(imageView);
            }
        });

        imageView.setOnMouseEntered(event -> {
            imageView.highlight(true);
        });

        imageView.setOnMouseExited(event -> {
            imageView.highlight(false);
        });

    }

    /**
     *
     * @param dropbox
     * @param imageView Use object rather than target ordering to keep the orderings updated
     * @param insertAfter
     */
    private void initDropZone(Rectangle dropbox, ImageThumbnailView imageView, boolean insertAfter) {
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
            // Reorder images
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int toOrder = imageView.getOMRImage().getOrder()-1;
                if (insertAfter) {
                    toOrder++;
                }
                Logger.getLogger(ImagesController.class.getName()).log(Level.INFO, "Drag&drop: moving image at position {0} to position {1}", new Object[]{db.getString(), toOrder});
                moveThumbnail(imageView, Integer.parseInt(db.getString()), toOrder);

                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

    private void moveThumbnail(final ImageThumbnailView image, final int fromOrder, final int toOrder) {
        ICommand command = new ICommand() {
            ArrayList<ImageThumbnailView> currentOrdering;
            @Override
            public void execute(IObservableTaskRunner observer) {
                currentOrdering = new ArrayList<>();
                for (int i=0; i<flowPane.getChildren().size()-1; i++) {
                    currentOrdering.add((ImageThumbnailView) flowPane.getChildren().get(i));
                }
                doExecute();
            }

            private void doExecute() {
                // remove thumbnail from list
                ImageThumbnailView thumbnailNode = (ImageThumbnailView) flowPane.getChildren().remove(fromOrder);

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
            public void undo() {
                // set the saved ordering
                flowPane.getChildren().clear();
                for (ImageThumbnailView imageThumbnailView: currentOrdering) {
                    flowPane.getChildren().add(imageThumbnailView);
                }
                flowPane.getChildren().add(iconAdd.getRoot());
                recomputeOrdering();
            }

            @Override
            public void redo() {
                doExecute();
            }

            @Override
            public String getEventName() {
                return "Move image";
            }

            @Override
            public String toString() {
                return "Move " + image.toString();
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
            ImageThumbnailView tv = (ImageThumbnailView) flowPane.getChildren().get(i);
            tv.getOMRImage().setOrder(i+1);
            tv.updateLabel();
        }
    }

    private void doAddImages() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        List<File> files = dlg.openFiles("Select an image", new String[]{"JPG", "TIF", "TIFF"}, new String[]{"jpg", "tif", "tiff"});

        if (files != null) {
            try {
                ICommand command = new ICommand() {
                    ArrayList<OMRImage> addedImages;
                    ArrayList<File> sortedFiles;
                    @Override
                    public void execute(IObservableTaskRunner observer) throws Exception {
                        addedImages = new ArrayList<>();
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
                            addedImages.add(dashboard.getModel().getCurrentProject().addImage(file));
                        }
                    }

                    @Override
                    public boolean canBeUndone() {
                        return false; //TODO Habrá que evitar que se copien los ficheros directamente en el modelo
                    }

                    @Override
                    public void undo() throws Exception {
                        for (OMRImage image: addedImages) {
                            dashboard.getModel().getCurrentProject().deleteImage(image);
                        }
                    }

                    @Override
                    public void redo() throws Exception {
                        doExecute();
                    }

                    @Override
                    public String getEventName() {
                        return "Add image";
                    }

                    @Override
                    public String toString() {
                        return "Add image(s)";
                    }
                };

                dashboard.getCommandManager().executeCommand(command);
            } catch (IM3Exception e) {
                e.printStackTrace();
                ShowError.show(OMRApp.getMainStage(), "Cannot add image", e);
            }
        }
    }

    private void doDeleteImage(ImageThumbnailView imageView) {
        if (ShowConfirmation.show(OMRApp.getMainStage(), "Do you want to delete " + imageView.getOMRImage().toString() + "?. It cannot be undone")) {
            try {
                ICommand command = new ICommand() {
                    @Override
                    public void execute(IObservableTaskRunner observer) throws Exception {
                        doExecute();
                        dashboard.save();
                    }

                    private void doExecute() throws IM3Exception {
                        dashboard.getModel().getCurrentProject().deleteImage(imageView.getOMRImage());
                    }

                    @Override
                    public boolean canBeUndone() {
                        return false;  //TODO Habrá que evitar que se copien los ficheros directamente en el modelo
                    }

                    @Override
                    public void undo() {
                        dashboard.getModel().getCurrentProject().addImage(imageView.getOMRImage());
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
                        return "Delete " + imageView.toString();
                    }
                };
                dashboard.getCommandManager().executeCommand(command);
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot delete image", e);
            }
        }
    }

    private void doOpenImage(ImageThumbnailView pageView) {
        dashboard.openImageDocumentAnalysis(pageView);
    }


    @Override
    public void setDashboard(DashboardController dashboard) {
        this.dashboard = dashboard;
        labelProjectPath.setText(dashboard.getModel().getCurrentProject().getImagesFolder().getAbsolutePath());
        loadProjectImages();
        listenToImagesChanges();
    }

    public CommandManager getCommandManager() {
        return dashboard.getCommandManager();
    }

    public OMRModel getModel() { return dashboard.getModel();}

    @Override
    public Node getRoot() {
        return flowPane;
    }
}
