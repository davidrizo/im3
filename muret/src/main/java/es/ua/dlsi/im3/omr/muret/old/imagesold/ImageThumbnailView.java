package es.ua.dlsi.im3.omr.muret.old.imagesold;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowInput;
import es.ua.dlsi.im3.omr.muret.old.OMRApp;
import es.ua.dlsi.im3.omr.muret.old.PredefinedIcon;
import es.ua.dlsi.im3.omr.muret.model.OMRInstrument;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageThumbnailView extends BorderPane {
    private final VBox labels;
    private final Node previewIcon;
    private final ImagesController imagesController;
    Label labelOrder;
    private final Node interactionIcon;
    OMRImage omrImage;
    AnchorPane mainPane;
    ImageView imageView;
    Image image;
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;
    /**
     * Used to reorder using drag and drop
     */
    Rectangle leftDropbox;
    Rectangle rightDropbox;
    Line highlightLine;

    IOpenImageHandler openImageHandler;
    IDeleteImageHandler deleteImageHandler;

    public ImageThumbnailView(ImagesController imagesController, OMRImage omrImage) {
        this.omrImage = omrImage;
        this.imagesController = imagesController;
        mainPane = new AnchorPane();

        //image = SwingFXUtils.toFXImage(omrImage.getBufferedImage(), null);
        try {
            URL url = omrImage.getImageFile().toURI().toURL();
            image = new Image(url.toString(), WIDTH, WIDTH, true, false); // resize, get resized pixels, if not, too much memory is stored in memory
        } catch (MalformedURLException e) {
            e.printStackTrace(); //TODO
            throw new IM3RuntimeException(e);
        }
        imageView = new ImageView(image);
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);
        labels  = new VBox(5);
        labels.setAlignment(Pos.CENTER);
        labelOrder = new Label("Image " + omrImage.getOrder());
        labels.getChildren().add(labelOrder);
        labels.getChildren().add(new Label(omrImage.getImageRelativeFileName()));
        /*Abril for (OMRInstrument instrument: omrImage.getInstrumentList()) {
            labels.getChildren().add(new Label(instrument.toString()));
        }*/
        updateLabel();
        setBottom(labels);
        setCenter(mainPane);

        highlightLine = new Line(0, 0, imageView.getFitWidth(), 0);
        highlightLine.setStroke(Color.RED);
        highlightLine.setStrokeWidth(5);
        setTop(highlightLine);

        leftDropbox = createDropbox();
        rightDropbox = createDropbox();
        setLeft(leftDropbox);
        setRight(rightDropbox);

        mainPane.getChildren().add(imageView);
        interactionIcon = createInteractionIcon();
        mainPane.getChildren().add(interactionIcon);
        AnchorPane.setRightAnchor(interactionIcon, 20.0); //TODO
        AnchorPane.setTopAnchor(interactionIcon, 10.0); //TODO
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        
        previewIcon = createPreviewIcon();
        mainPane.getChildren().add(previewIcon);
        AnchorPane.setLeftAnchor(previewIcon, 20.0); //TODO
        AnchorPane.setTopAnchor(previewIcon, 10.0); //TODO

        highlight(false);
    }

    private Node createPreviewIcon() {
        // see http://aalmiray.github.io/ikonli/cheat-sheet-openiconic.html for icons
        PredefinedIcon previewIcon = new PredefinedIcon("oi-zoom-in");
        previewIcon.setIconColor(Color.DARKBLUE);

        previewIcon.setOnMouseEntered(event -> {
            previewIcon.setFill(Color.RED);
        });
        previewIcon.setOnMouseExited(event -> {
            previewIcon.setFill(Color.DARKBLUE);
        });
        previewIcon.setOnMouseClicked(event -> {
            doPreview();
        });

        return previewIcon;
    }

    private void doPreview() {
        Stage stage = new Stage(StageStyle.UTILITY);
        String title;
        /*if (omrImage.getInstrumentList().isEmpty()) {
            stage.setTitle("Image " + omrImage.getOrder() + ", " + omrImage.getImageRelativeFileName() + " " + omrImage.getInstrumentList());
        } else {
            stage.setTitle("Image " + omrImage.getOrder() + ", " +  omrImage.getInstrumentList() + " " + omrImage.getImageRelativeFileName());
        }*/
        stage.setTitle("Image " + omrImage.getOrder() + ", " +  omrImage.getImageRelativeFileName());

        stage.setWidth(900); //TODO
        stage.setHeight(700);
        Group group = new Group();
        Scene scene = new Scene(group);
        stage.setScene(scene);
        ImageView imageView = new ImageView();
        imageView.fitWidthProperty().bind(stage.widthProperty());
        imageView.fitHeightProperty().bind(stage.heightProperty());
        imageView.setPreserveRatio(true);
        Image image = null;
        try {
            image = this.omrImage.getImage();
            imageView.setImage(image);
            group.getChildren().add(imageView);
            stage.showAndWait();
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot load image file", e);
            ShowError.show(OMRApp.getMainStage(), "Cannot load image file", e);
        }
    }

    private Node createInteractionIcon() {
        Text interactionDots = new Text("···");

        interactionDots.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        interactionDots.setFill(Color.DARKBLUE);

        interactionDots.setOnMouseEntered(event -> {
            interactionDots.setFill(Color.RED);
        });
        interactionDots.setOnMouseExited(event -> {
            interactionDots.setFill(Color.DARKBLUE);
        });

        interactionDots.setOnMouseClicked(event -> {
            ContextMenu contextMenu = new ContextMenu();

            MenuItem openMenu = new MenuItem("Open");
            contextMenu.getItems().add(openMenu);
            contextMenu.getItems().add(new SeparatorMenuItem());
            openMenu.setOnAction(event1 -> {
                if (openImageHandler != null) {
                    openImageHandler.openImage(this);
                }
            });

            //TODo Modelo
            Set<OMRInstrument> omrInstruments = imagesController.getModel().getCurrentProject().getInstruments().getInstrumentSet();

            for (OMRInstrument instrument: omrInstruments) {
                MenuItem menuInstrument = new MenuItem(instrument.getName());
                contextMenu.getItems().add(menuInstrument);
                menuInstrument.setOnAction(eventMenuInstrument -> {
                    addInstrumentToPage(instrument);
                });
            }

            MenuItem addInstrument = new MenuItem("Add other instrument...");
            contextMenu.getItems().add(addInstrument);
            addInstrument.setOnAction(event1 -> {
                String name = ShowInput.show(OMRApp.getMainStage(), "New instrument", "Introduce the instrument name");
                if (name != null) {
                    OMRInstrument instrument = imagesController.getModel().getCurrentProject().addInstrument(name);
                    addInstrumentToPage(instrument);
                }
            });

            contextMenu.getItems().add(new SeparatorMenuItem());


            contextMenu.getItems().add(new SeparatorMenuItem());
            MenuItem deleteMenu = new MenuItem("Delete image");
            contextMenu.getItems().add(deleteMenu);
            deleteMenu.setOnAction(event1 -> {
                if (deleteImageHandler != null) {
                    deleteImageHandler.deletePage(this);
                }
            });


            contextMenu.show(interactionDots, event.getScreenX(), event.getScreenY());
        });


        return interactionDots;
    }

    private void removeInstrumentFromPage(OMRInstrument instrument) {
        //TODO Abril - ¿tiene sentido hacer esto aquí?
        /*ICommand command = new ICommand() {
            OMRInstrument deletedInstrument;
            Label deletedLabel;
            int positionOfLabel;
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                deletedInstrument = instrument;
                deletedLabel = null;
                for (int i=0; deletedLabel == null && i<labels.getChildren().size(); i++) {
                    Node node = labels.getChildren().get(i);
                    if (node instanceof Label && ((Label)node).getText().equals(instrument.getName())) {
                        deletedLabel = (Label) node;
                        positionOfLabel = i;
                    }
                }
                if (deletedLabel == null) {
                    throw new IM3Exception("Cannot find the label for instrument " + instrument.getName());
                }

                doExecute();
            }

            private void doExecute() {
                omrImage.removeInstrument(instrument);
                labels.getChildren().remove(deletedLabel);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                omrImage.addInstrument(deletedInstrument);
                labels.getChildren().add(positionOfLabel, deletedLabel);
            }

            @Override
            public void redo() throws Exception {
                doExecute();
            }

            @Override
            public String getEventName() {
                return "Remove instrument";
            }

            @Override
            public String toString() {
                return "Remove instrument " + deletedInstrument;
            }
        };

        try {
            imagesController.getCommandManager().executeCommand(command);
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot add instrument", e);
        }*/
    }

    private void addInstrumentToPage(OMRInstrument instrument) {
        //TODO Abril - ¿tiene sentido hacer esto aquí?
        /*ICommand command = new ICommand() {
            OMRInstrument insertedInstrument;
            Label insertedLabel;
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                insertedInstrument = instrument;
                omrImage.addInstrument(instrument);
                doExecute();
            }

            private void doExecute() {
                insertedLabel = new Label(instrument.getName());
                labels.getChildren().add(insertedLabel);

            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                omrImage.removeInstrument(instrument);
                labels.getChildren().remove(insertedLabel);
            }

            @Override
            public void redo() throws Exception {
                doExecute();
            }

            @Override
            public String getEventName() {
                return "Add instrument";
            }

            @Override
            public String toString() {
                return "Add instrument " + insertedInstrument;
            }
        };

        try {
            imagesController.getCommandManager().executeCommand(command);
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot add instrument", e);
        }
        */
    }

    private Rectangle createDropbox() {
        Rectangle r = new Rectangle(20, 100);
        r.setFill(Color.TRANSPARENT);
        return r;
    }

    public Rectangle getLeftDropbox() {
        return leftDropbox;
    }

    public Rectangle getRightDropbox() {
        return rightDropbox;
    }

    public OMRImage getOMRImage() {
        return omrImage;
    }

    public void updateLabel() {
        labelOrder.setText("Image " + omrImage.getOrder());
    }

    public IOpenImageHandler getOpenImageHandler() {
        return openImageHandler;
    }

    public void setOpenImageHandler(IOpenImageHandler openImageHandler) {
        this.openImageHandler = openImageHandler;
    }

    public IDeleteImageHandler getDeleteImageHandler() {
        return deleteImageHandler;
    }

    public void setDeleteImageHandler(IDeleteImageHandler deleteImageHandler) {
        this.deleteImageHandler = deleteImageHandler;
    }

    @Override
    public String toString() {
        return omrImage.toString();
    }

    public void highlight(boolean highlight) {
        highlightLine.setVisible(highlight);
    }
}
