package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import es.ua.dlsi.im3.omr.muret.old.OMRApp;
import es.ua.dlsi.im3.omr.muret.old.PredefinedIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It contains low resolution / size versions of the image
 * @autor drizo
 */
public class ImageThumbnailView extends AnchorPane {
    private static final double PORTRAIT_WIDTH = 188;
    private static final double LANDSCAPE_WIDTH = 392;
    private static final double HEIGHT = 245;
    private final OrderImagesController orderImagesController;
    OMRImage omrImage;
    ImageView imageView;
    Image image;
    Button moreButton;
    private VBox vBoxInstruments;

    public ImageThumbnailView(OrderImagesController orderImagesController, OMRImage omrImage) throws IM3Exception {
        this.orderImagesController = orderImagesController;
        this.omrImage = omrImage;
        createImageView();
        createMoreButton();
        createPreviewButton();
        createLabels();
        createMoveButton();
    }

    private void createLabels() {
        Text text = new Text(omrImage.getImageFile().getName());
        text.getStyleClass().add("buttonFileNameText");
        this.getChildren().add(text);
        AnchorPane.setRightAnchor(text, 10.0);
        AnchorPane.setBottomAnchor(text, 10.0);
    }

    private void createImageView() throws IM3Exception {
        URL url = null;
        try {
            url = omrImage.getImageFile().toURI().toURL();
        } catch (MalformedURLException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot load image URL", e);
            throw new IM3Exception(e);
        }

        // resize, get resized pixels, if not, too much memory is stored in memory
        if (omrImage.getImage().getHeight() > omrImage.getImage().getWidth()) {
            image = new Image(url.toString(), PORTRAIT_WIDTH, HEIGHT, true, false);
        } else {
            image = new Image(url.toString(), LANDSCAPE_WIDTH, HEIGHT, true, false);
        }
        imageView = new ImageView(image);
        this.getChildren().add(imageView);


    }

    private void createMoreButton() {
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO
            }
        };
        moreButton = Utils.addOpenOtherProjectButton("/fxml/muret/images/dots.png", 22, 6, "moreButton", eventHandler);
        this.getChildren().add(moreButton);
        AnchorPane.setLeftAnchor(moreButton, 10.0);
        AnchorPane.setTopAnchor(moreButton, 10.0);
    }

    private void createPreviewButton() {
        Node previewIcon = createPreviewIcon();
        this.getChildren().add(previewIcon);
        AnchorPane.setRightAnchor(previewIcon, 10.0);
        AnchorPane.setTopAnchor(previewIcon, 10.0);
    }

    private Node createPreviewIcon() {
        // see http://aalmiray.github.io/ikonli/cheat-sheet-openiconic.html for icons
        PredefinedIcon previewIcon = new PredefinedIcon("oi-zoom-in");
        previewIcon.setIconColor(Color.WHITE);

        previewIcon.setOnMouseEntered(event -> {
            previewIcon.setFill(Color.RED);
        });
        previewIcon.setOnMouseExited(event -> {
            previewIcon.setFill(Color.WHITE);
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
        stage.setTitle("Image " + omrImage.getOrder() + ", " +  omrImage.getInstrumentList() + " " + omrImage.getImageRelativeFileName());

        stage.setWidth(900); //TODO
        stage.setHeight(700);
        Group group = new Group();
        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.setMaximized(true);
        
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

    private void createMoveButton() {
        Node moveIcon = createMoveIcon();
        this.getChildren().add(moveIcon);
        AnchorPane.setLeftAnchor(moveIcon, 10.0);
        AnchorPane.setBottomAnchor(moveIcon, 10.0);
    }

    private Node createMoveIcon() {
        // see http://aalmiray.github.io/ikonli/cheat-sheet-openiconic.html for icons
        PredefinedIcon moveIcon = new PredefinedIcon("oi-move");
        moveIcon.setIconColor(Color.WHITE);

        moveIcon.setOnMouseEntered(event -> {
            moveIcon.setFill(Color.RED);
        });
        moveIcon.setOnMouseExited(event -> {
            moveIcon.setFill(Color.WHITE);
        });
        moveIcon.setOnMouseClicked(event -> {
        });

        Tooltip.install(moveIcon, new Tooltip("Drag and drop the image"));

        return moveIcon;
    }


    public OMRImage getOMRImage() {
        return omrImage;
    }
}
