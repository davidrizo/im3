package es.ua.dlsi.im3.omr.interactive.pages;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowMessage;
import es.ua.dlsi.im3.omr.interactive.model.ImageFile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PagesController implements Initializable {
    @FXML
    Label labelProjectName;

    @FXML
    FlowPane flowPane;

    IconAdd iconAdd;

    State state;

    ArrayList<PageThumbnailView> thumbnailViews;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        state = State.idle; //TODO cambiar estado en drag & drop
        iconAdd = new IconAdd();
        thumbnailViews = new ArrayList<>();

        //TODO YA!!!
        ArrayList<File> files = new ArrayList<>();
        try {
            FileUtils.readFiles(new File("/Users/drizo/Documents/EASD.A/docencia/alicante-2017-2018/inv/imagenes_patriarca"), files, "tif");
            files.sort(new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (int i=0; i<files.size(); i++) {
                ImageFile imageFile = new ImageFile(files.get(i)); //TODO - del modelo
                imageFile.setOrder(i); //TODO Debería estar guardado en el modelo

                PageThumbnailView pageView = new PageThumbnailView(imageFile);
                flowPane.getChildren().add(pageView.getRoot());
                thumbnailViews.add(pageView);
                addInteraction(pageView);
            }
            addPageAddIcon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPageAddIcon() {
        flowPane.getChildren().add(iconAdd.getRoot());
        //TODO Refactorizar
        iconAdd.getRoot().setOnMouseEntered(event -> {
            iconAdd.highlight(true);
        });
        iconAdd.getRoot().setOnMouseExited(event -> {
            iconAdd.highlight(false);
        });
        iconAdd.getRoot().setOnMouseClicked(event -> {
            doAddPage();
        });
    }

    private void doAddPage() {
        ShowMessage.show(null, "TO-DO ADD PAGE");
    }

    private void addInteraction(PageThumbnailView pageView) {
        pageView.getRoot().setOnDragDetected(event -> {
            Dragboard db = pageView.getRoot().startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            // Store the node ID in order to know what is dragged.
            content.putString(new Integer(pageView.getImageFile().getOrder()).toString());
            db.setContent(content);
            event.consume();
        });

        initDropZone(pageView.getLeftDropbox(), pageView, false);
        initDropZone(pageView.getRightDropbox(), pageView, true);
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
                int toOrder = pageView.getImageFile().getOrder();
                if (insertAfter) {
                    toOrder++;
                }
                Logger.getLogger(PagesController.class.getName()).log(Level.INFO, "Drag&drop: moving page at position {0} to position {1}", new Object[]{db.getString(), toOrder});
                moveThumbnail(Integer.parseInt(db.getString()), toOrder);

                event.setDropCompleted(true);
                event.consume();

            }
        });
    }

    private void moveThumbnail(int fromOrder, int toOrder) {
        //TODO Command para poder deshacer
        // remove thumbnail from list
        Node thumbnailNode = flowPane.getChildren().remove(fromOrder);
        PageThumbnailView thumbnailView = thumbnailViews.remove(fromOrder);

        if (fromOrder < toOrder) {
            toOrder--; // because we have removed it from lists
        }

        if (thumbnailNode == null) {
            throw new IM3RuntimeException("No thumbnail at index " + fromOrder + " in flowPane");
        }

        if (thumbnailView == null) {
            throw new IM3RuntimeException("No thumbnailView at index " + fromOrder + " in flowPane");
        }

        // add to position
        flowPane.getChildren().add(toOrder, thumbnailNode);
        thumbnailViews.add(toOrder, thumbnailView);

        // recompute ordering
        for (int i= 0; i<thumbnailViews.size(); i++) {
            PageThumbnailView tv = thumbnailViews.get(i);
            tv.getImageFile().setOrder(i);
            tv.updateLabel();
        }
    }


}
