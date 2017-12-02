package es.ua.dlsi.im3.omr.interactive.pages;

import es.ua.dlsi.im3.omr.interactive.PredefinedIcon;
import es.ua.dlsi.im3.omr.interactive.model.Instrument;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PageThumbnailView {
    private final VBox labels;
    private final Node previewIcon;
    Label labelOrder;
    private final Node interactionIcon;
    OMRPage omrPage;
    AnchorPane mainPane;
    ImageView imageView;
    Image image;
    BorderPane borderPane;
    /**
     * Used to reorder using drag and drop
     */
    Rectangle leftDropbox;
    Rectangle rightDropbox;


    public PageThumbnailView(OMRPage omrPage) {
        this.omrPage = omrPage;
        borderPane = new BorderPane();
        mainPane = new AnchorPane();

        image = SwingFXUtils.toFXImage(omrPage.getBufferedImage(), null);
        imageView = new ImageView(image);
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        imageView.setStyle("-fx-background-color: black");
        imageView.setPreserveRatio(true);
        labels  = new VBox(5);
        labels.setAlignment(Pos.CENTER);
        labelOrder = new Label("Page " + omrPage.getOrder());
        labels.getChildren().add(labelOrder);
        labels.getChildren().add(new Label(omrPage.getImageRelativeFileName()));
        for (Instrument instrument: omrPage.getInstrumentList()) {
            labels.getChildren().add(new Label(instrument.toString()));
        }
        updateLabel();
        borderPane.setBottom(labels);
        borderPane.setCenter(mainPane);

        leftDropbox = createDropbox();
        rightDropbox = createDropbox();
        borderPane.setLeft(leftDropbox);
        borderPane.setRight(rightDropbox);

        mainPane.getChildren().add(imageView);
        interactionIcon = createInteractionIcon();
        mainPane.getChildren().add(interactionIcon);
        AnchorPane.setRightAnchor(interactionIcon, 20.0); //TODO
        AnchorPane.setTopAnchor(interactionIcon, 10.0); //TODO
        AnchorPane.setTopAnchor(borderPane, 0.0);
        AnchorPane.setLeftAnchor(borderPane, 0.0);
        AnchorPane.setRightAnchor(borderPane, 0.0);
        AnchorPane.setBottomAnchor(borderPane, 0.0);
        
        previewIcon = createPreviewIcon();
        mainPane.getChildren().add(previewIcon);
        AnchorPane.setLeftAnchor(previewIcon, 20.0); //TODO
        AnchorPane.setTopAnchor(previewIcon, 10.0); //TODO

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
        if (omrPage.getInstrumentList().isEmpty()) {
            stage.setTitle("Page " + omrPage.getOrder() + ", " + omrPage.getImageRelativeFileName() + " " + omrPage.getInstrumentList());
        } else {
            stage.setTitle("Page " + omrPage.getOrder() + ", " +  omrPage.getInstrumentList() + " " + omrPage.getImageRelativeFileName());
        }

        stage.setWidth(900); //TODO
        stage.setHeight(700);
        Group group = new Group();
        Scene scene = new Scene(group);
        stage.setScene(scene);
        ImageView imageView = new ImageView();
        imageView.fitWidthProperty().bind(stage.widthProperty());
        imageView.fitHeightProperty().bind(stage.heightProperty());
        imageView.setPreserveRatio(true);
        imageView.setImage(image);
        group.getChildren().add(imageView);
        stage.showAndWait();
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

            //TODo Modelo
            Instrument [] instruments = {
                    new Instrument("Tiple 1º, 1º coro"),
                    new Instrument("Tiple 2ª, 1º coro"),
                    new Instrument("Alto, 1º coro"),
                    new Instrument("Tenor, 1º coro"),
                    new Instrument("Tiple, 2º coro"),
                    new Instrument("Alto, 2º coro"),
                    new Instrument("Tenor, 2º coro"),
                    new Instrument("Bajo, 2º coro")
            };

            for (Instrument instrument: instruments) {
                MenuItem menuInstrument = new MenuItem(instrument.getName());
                contextMenu.getItems().add(menuInstrument);
                menuInstrument.setOnAction(eventMenuInstrument -> {
                    addInstrumentToPage(instrument);
                });
            }

            MenuItem addInstrument = new MenuItem("Add instrument...");
            contextMenu.getItems().add(addInstrument);

            contextMenu.getItems().add(new SeparatorMenuItem());
            MenuItem deleteMenu = new MenuItem("Delete");
            contextMenu.getItems().add(deleteMenu);

            contextMenu.show(interactionDots, event.getScreenX(), event.getScreenY());
        });


        return interactionDots;
    }

    private void addInstrumentToPage(Instrument instrument) {
        //TODO Sinc modelo
        omrPage.addInstrument(instrument);
        labels.getChildren().add(new Label(instrument.getName()));
    }

    private Rectangle createDropbox() {
        Rectangle r = new Rectangle(20, 100);
        r.setFill(Color.TRANSPARENT);
        return r;
    }

    public Node getRoot() {
        return borderPane;
    }

    public Rectangle getLeftDropbox() {
        return leftDropbox;
    }

    public Rectangle getRightDropbox() {
        return rightDropbox;
    }

    public OMRPage getOmrPage() {
        return omrPage;
    }

    public void updateLabel() {
        labelOrder.setText("Page " + omrPage.getOrder());
    }
}
