package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.gui.javafx.dialogs.ShowInput;
import es.ua.dlsi.im3.omr.model.entities.Instrument;
import es.ua.dlsi.im3.omr.muret.model.OMRInstrument;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @autor drizo
 */
public class InstrumentsController implements Initializable {
    @FXML
    Button btnAdd;

    @FXML
    Button btnAddOther;

    @FXML
    Button btnRemove;

    @FXML
    ListView<OMRInstrument> lvAvailableInstruments;

    @FXML
    ListView<OMRInstrument> lvImageInstruments;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnAdd.disableProperty().bind(lvAvailableInstruments.getSelectionModel().selectedItemProperty().isNull());
        btnRemove.disableProperty().bind(lvImageInstruments.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void handleAdd() {
        lvImageInstruments.getItems().add(lvAvailableInstruments.getSelectionModel().getSelectedItem());
        lvAvailableInstruments.getItems().remove(lvAvailableInstruments.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void handleAddOther() {
        String instrumentName = ShowInput.show(this.btnAdd.getScene().getWindow(), "Instruments", "Name of the new instrument");
        if (instrumentName != null) {
            lvImageInstruments.getItems().add(new OMRInstrument(instrumentName));
        }
    }

    @FXML
    private void handleRemove() {
        lvAvailableInstruments.getItems().add(lvImageInstruments.getSelectionModel().getSelectedItem());
        lvImageInstruments.getItems().remove(lvImageInstruments.getSelectionModel().getSelectedItem());
    }


    public List<OMRInstrument> getInstruments() {
        return lvImageInstruments.getItems();
    }

    public void loadInstruments(Collection<OMRInstrument> projectInstruments, Collection<OMRInstrument> imageInstruments) {
        lvAvailableInstruments.getItems().addAll(projectInstruments);
        lvImageInstruments.getItems().addAll(imageInstruments);
    }
}
