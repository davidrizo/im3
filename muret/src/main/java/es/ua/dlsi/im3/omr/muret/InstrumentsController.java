package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.gui.javafx.dialogs.ShowInput;
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
    Button btnAddOther;

    @FXML
    ListView<OMRInstrument> lvAvailableInstruments;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvAvailableInstruments.getItems().addAll(MuRET.getInstance().getModel().getCurrentProject().getInstruments().getInstrumentSet());
    }

    @FXML
    private void handleAddOther() {
        String instrumentName = ShowInput.show(this.btnAddOther.getScene().getWindow(), "Instruments", "Name of the new instrument");
        if (instrumentName != null) {
            OMRInstrument newInstrument = MuRET.getInstance().getModel().getCurrentProject().addInstrument(instrumentName);
            lvAvailableInstruments.getItems().add(newInstrument);
            lvAvailableInstruments.getSelectionModel().select(newInstrument);
        }
    }

    public OMRInstrument getSelectedInstrument() {
        return lvAvailableInstruments.getSelectionModel().getSelectedItem();
    }

    public void selectInstrument(OMRInstrument instrument) {
        if (instrument != null) {
            lvAvailableInstruments.getSelectionModel().select(instrument);
        }
    }
}
