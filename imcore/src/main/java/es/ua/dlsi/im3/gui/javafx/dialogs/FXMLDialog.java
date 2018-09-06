package es.ua.dlsi.im3.gui.javafx.dialogs;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.FXMLViewController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Optional;

public class FXMLDialog {
    protected final Dialog dialog;
    private final ButtonType btnOK;
    private final ButtonType btnCancel;
    protected Node btnOKNode;
    protected Initializable controller;

    public FXMLDialog(Window ownerDialog, String title, String urlFXML) throws IM3Exception {
        btnOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(urlFXML));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            controller = fxmlLoader.getController();
        } catch (IOException e) {
            throw new IM3Exception(e);
        }

        dialog = new Dialog<>();
        dialog.initOwner(ownerDialog);
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(root);
        dialog.getDialogPane().getButtonTypes().add(btnOK);

        btnOKNode = dialog.getDialogPane().lookupButton(btnOK);
        dialog.getDialogPane().getButtonTypes().add(btnCancel);
    }


    public boolean show() {
        Optional<ButtonType> result = dialog.showAndWait();

        return (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE);
    }

    public Initializable getController() {
        return controller;
    }
}
