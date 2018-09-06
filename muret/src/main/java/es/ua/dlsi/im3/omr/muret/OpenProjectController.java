package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.gui.javafx.BackgroundProcesses;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.model.OMRProjectPreview;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @autor drizo
 */
public class OpenProjectController implements Initializable {
    private static final int MAX = 9;

    @FXML
    FlowPane flowPaneLastProjects;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createRecentProjectsPanel();
    }

    private void createRecentProjectsPanel() {
        flowPaneLastProjects.getChildren().clear();
        LinkedList<String> recentProjects = RecentProjectsModel.getInstance().getRecentProjects();
        int count = 0;
        for (int i=recentProjects.size()-1; count < MAX && i>=0; i--) {
            try {
                addRecentProject(recentProjects.get(i));
            } catch (FileNotFoundException e) {
                // if file not found don't show it as recent projects
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot find recent file {0}", e.getMessage());
            } catch (ImportException e) {
                // if other error, don't show it as recent projects, but warn it
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find recent file {0}", e.getMessage());
            }
        }
        addOpenOtherProjectButton();

    }

    private void addOpenOtherProjectButton() {
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                OpenSaveFileDialog openSaveFileDialog = new OpenSaveFileDialog();
                File file = openSaveFileDialog.openFile(MuRET.getInstance().getMainStage().getOwner(), "Select a MuRET project file", "MuRET files", "mrt");
                openProject(file);
            }
        };
        Button button = Utils.addOpenOtherProjectButton("/fxml/muret/images/recentprojects_folder.png", 145, 145, "buttonRecentProject", eventHandler);
        flowPaneLastProjects.getChildren().add(button);
    }

    private void openProject(File mrtFile) {
        OrderImagesController orderImagesController = (OrderImagesController) MuRET.getInstance().openWindow("/fxml/muret/orderimages.fxml", true, true);
        orderImagesController.loadOMRProject(mrtFile);
    }

    private void addRecentProject(String mrtFilePath) throws FileNotFoundException, ImportException {
        File mrtFile = new File(mrtFilePath);
        if (!mrtFile.exists()) {
            throw new FileNotFoundException(mrtFilePath);
        }

        OMRProjectPreview omrProjectPreview = new OMRProjectPreview(mrtFile);

        // create the button
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("buttonRecentProject_anchorpane");

        anchorPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openProject(mrtFile);
                RecentProjectsModel.getInstance().addProject(mrtFile.getAbsolutePath());
            }
        });

        Image posterFrameImage = null;
        try {
            posterFrameImage = new Image(omrProjectPreview.getPosterFrameImage().toURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            ShowError.show(MuRET.getInstance().getMainStage(), "Cannot read poster frame", e);
        }

        ImageView posterFrameImageView = new ImageView(posterFrameImage);
        posterFrameImageView.getStyleClass().add("buttonRecentProject_image");
        posterFrameImageView.setFitHeight(160); //TODO The CSS does not work
        posterFrameImageView.setFitWidth(160);
        anchorPane.getChildren().add(posterFrameImageView);

        VBox vBoxTitleComposer = new VBox();
        vBoxTitleComposer.getStyleClass().add("buttonRecentProject_vbox");
        anchorPane.getChildren().add(vBoxTitleComposer);
        Label titleText = new Label();
        titleText.getStyleClass().add("buttonRecentProject_text");
        vBoxTitleComposer.getChildren().add(titleText);
        if (omrProjectPreview.getTitle() != null) {
            titleText.setText(omrProjectPreview.getTitle());
        } else {
            try {
                titleText.setText(FileUtils.getFileWithoutPathOrExtension(mrtFile));
            } catch (IOException e) {
                e.printStackTrace();
                ShowError.show(MuRET.getInstance().getMainStage(), "Cannot read file name " + mrtFile.getName(), e);
            }
        }

        if (omrProjectPreview.getComposer() != null) {
            Label composerText = new Label(omrProjectPreview.getComposer());
            composerText.getStyleClass().add("buttonRecentProject_text");
            vBoxTitleComposer.getChildren().add(composerText);
        }

        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO
            }
        };
        Button moreButton = Utils.addOpenOtherProjectButton("/fxml/muret/images/dots.png", 22, 6, "moreButton", eventHandler);
        anchorPane.getChildren().add(moreButton);
        AnchorPane.setLeftAnchor(moreButton, 10.0);
        AnchorPane.setTopAnchor(moreButton, 10.0);


        //button.setGraphic(stackPane);
        flowPaneLastProjects.getChildren().add(anchorPane);
    }

    @FXML
    private void handleClearRecentProjects() {
        RecentProjectsModel.getInstance().clear();
        createRecentProjectsPanel();
    }

    @FXML
    private void handleClose() {
        MuRET.getInstance().closeCurrentWindow();
    }

}
