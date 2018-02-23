package es.ua.dlsi.im3.omr.interactive.pages;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.command.ICommand;
import es.ua.dlsi.im3.gui.command.IObservableTaskRunner;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowInput;
import es.ua.dlsi.im3.omr.interactive.OMRApp;
import es.ua.dlsi.im3.omr.interactive.PredefinedIcon;
import es.ua.dlsi.im3.omr.interactive.model.OMRInstrument;
import es.ua.dlsi.im3.omr.interactive.model.OMRModel;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import javafx.collections.ObservableSet;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Set;

public class PageThumbnailView extends BorderPane {
    private final VBox labels;
    private final Node previewIcon;
    private final PagesController pagesController;
    Label labelOrder;
    private final Node interactionIcon;
    OMRPage omrPage;
    AnchorPane mainPane;
    ImageView imageView;
    Image image;
    /**
     * Used to reorder using drag and drop
     */
    Rectangle leftDropbox;
    Rectangle rightDropbox;
    Line highlightLine;

    IOpenPageHandler openPageHandler;
    IDeletePageHandler deletePageHandler;

    public PageThumbnailView(PagesController pagesController, OMRPage omrPage) {
        this.omrPage = omrPage;
        this.pagesController = pagesController;
        mainPane = new AnchorPane();

        image = SwingFXUtils.toFXImage(omrPage.getBufferedImage(), null);
        imageView = new ImageView(image);
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);
        labels  = new VBox(5);
        labels.setAlignment(Pos.CENTER);
        labelOrder = new Label("Page " + omrPage.getOrder());
        labels.getChildren().add(labelOrder);
        labels.getChildren().add(new Label(omrPage.getImageRelativeFileName()));
        for (OMRInstrument instrument: omrPage.getInstrumentList()) {
            labels.getChildren().add(new Label(instrument.toString()));
        }
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

            MenuItem openMenu = new MenuItem("Open");
            contextMenu.getItems().add(openMenu);
            contextMenu.getItems().add(new SeparatorMenuItem());
            openMenu.setOnAction(event1 -> {
                if (openPageHandler != null) {
                    openPageHandler.openPage(this);
                }
            });

            //TODo Modelo
            Set<OMRInstrument> omrInstruments = pagesController.getModel().getCurrentProject().instrumentsProperty();

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
                    OMRInstrument instrument = pagesController.getModel().getCurrentProject().addInstrument(name);
                    addInstrumentToPage(instrument);
                }
            });

            contextMenu.getItems().add(new SeparatorMenuItem());
            for (OMRInstrument instrument: omrPage.getInstrumentList()) {
                MenuItem deleteInstrument = new MenuItem("Remove " + instrument);
                contextMenu.getItems().add(deleteInstrument);
                deleteInstrument.setOnAction(event1 -> {
                    removeInstrumentFromPage(instrument);
                });

            }

            contextMenu.getItems().add(new SeparatorMenuItem());
            MenuItem deleteMenu = new MenuItem("Delete page");
            contextMenu.getItems().add(deleteMenu);
            deleteMenu.setOnAction(event1 -> {
                if (deletePageHandler != null) {
                    deletePageHandler.deletePage(this);
                }
            });


            contextMenu.show(interactionDots, event.getScreenX(), event.getScreenY());
        });


        return interactionDots;
    }

    private void removeInstrumentFromPage(OMRInstrument instrument) {
        ICommand command = new ICommand() {
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
                omrPage.removeInstrument(instrument);
                labels.getChildren().remove(deletedLabel);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                omrPage.addInstrument(deletedInstrument);
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
            pagesController.getCommandManager().executeCommand(command);
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot add instrument", e);
        }
    }

    private void addInstrumentToPage(OMRInstrument instrument) {
        ICommand command = new ICommand() {
            OMRInstrument insertedInstrument;
            Label insertedLabel;
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                insertedInstrument = instrument;
                omrPage.addInstrument(instrument);
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
                omrPage.removeInstrument(instrument);
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
            pagesController.getCommandManager().executeCommand(command);
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot add instrument", e);
        }

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

    public OMRPage getOmrPage() {
        return omrPage;
    }

    public void updateLabel() {
        labelOrder.setText("Page " + omrPage.getOrder());
    }

    public IOpenPageHandler getOpenPageHandler() {
        return openPageHandler;
    }

    public void setOpenPageHandler(IOpenPageHandler openPageHandler) {
        this.openPageHandler = openPageHandler;
    }

    public IDeletePageHandler getDeletePageHandler() {
        return deletePageHandler;
    }

    public void setDeletePageHandler(IDeletePageHandler deletePageHandler) {
        this.deletePageHandler = deletePageHandler;
    }

    @Override
    public String toString() {
        return omrPage.toString();
    }

    public void highlight(boolean highlight) {
        highlightLine.setVisible(highlight);
    }
}
