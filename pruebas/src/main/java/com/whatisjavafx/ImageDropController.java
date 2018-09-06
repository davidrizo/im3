package com.whatisjavafx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class ImageDropController {

    @FXML
    private ImageView myImageView;

    @FXML
    void imageDragOver(DragEvent de) {
        Dragboard board = de.getDragboard();
        if (board.hasFiles()) {
            de.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    void imageDropped(DragEvent de) {
        try {
            Dragboard board = de.getDragboard();
            List<File> phil = board.getFiles();
            FileInputStream fis;
            fis = new FileInputStream(phil.get(0));
            Image image = new Image(fis);
            myImageView.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        // you could initialize
        // JavaFX Controls here
    }
}