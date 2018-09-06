package es.ua.dlsi.im3.gui.javafx.dialogs;

import es.ua.dlsi.im3.gui.javafx.FXMLViewController;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import java.util.Optional;

public class FXMLViewControllerDialog extends FXMLViewController {
    protected final Dialog dialog;
    private final ButtonType btnOK;
    private final ButtonType btnCancel;
    protected Node btnOKNode;

    public FXMLViewControllerDialog(Stage stage, String title, String fxmlName) {
        super(stage, fxmlName, false);
        dialog = new Dialog<>();
        if (stage != null) {
            dialog.initOwner(stage);
        }
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(getRoot());
        btnOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(btnOK);

        btnOKNode = dialog.getDialogPane().lookupButton(btnOK);

        btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(btnCancel);

    }
    public boolean show() {
        Optional<ButtonType> result = dialog.showAndWait();

        return (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE);
    }
}
