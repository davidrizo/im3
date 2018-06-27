package es.ua.dlsi.im3.gui.score.javafx;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreClef;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.gui.javafx.EditCell;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.EventType;
import es.ua.dlsi.im3.gui.score.IScoreSongViewEventSubscriptor;
import es.ua.dlsi.im3.gui.score.javafx.kern.KernField;
import es.ua.dlsi.im3.gui.score.javafx.kern.KernRecord;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author drizo
 */
public class ScoreEditController implements Initializable, IScoreSongViewEventSubscriptor {
    @FXML
    TextArea kernInputTextArea;
    @FXML
    ScrollPane scrollMainPane;
    @FXML
    MenuItem menuItemSave;
    @FXML
    MenuItem menuItemSaveMEIAs;
    @FXML
    TableView<KernRecord> tableViewKern;

    ObjectProperty<ScoreEditModel> model;
    private ScoreSongView scoreSongView;

    public ScoreEditController() {
        model = new SimpleObjectProperty<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuItemSave.disableProperty().bind(model.isNull());

        try {
            initKernInput();
        } catch (ImportException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot init", e);
            ShowError.show(ScoreEditApp.getMainStage(), "Cannot init", e);
        }
    }


    @FXML
    private void handleOpenMEI() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File file = dlg.openFile("Open notation file", "MEI files", "mei");
        MEISongImporter importer = new MEISongImporter();
        ScoreSong scoreSong = null;
        try {
            scoreSong = importer.importSong(file);
            model.set(new ScoreEditModel(scoreSong, scrollMainPane.getWidth(), scrollMainPane.getHeight()));
            scoreSongView = new ScoreSongView(model.get().getLayout());
            initInteraction();
            scrollMainPane.setContent(scoreSongView.getMainPanel());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot load MEI file", e);
            ShowError.show(ScoreEditApp.getMainStage(), "Cannot load MEI file", e);
        }
    }

    @FXML
    private void handleSaveMEI() {
        // TODO: 15/3/18 Comprobar que no existe ...
    }



    @FXML
    private void handleSaveMEIAs() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File file = dlg.saveFile("Save notation file", "MEI files", "mei");
        MEISongExporter exporter = new MEISongExporter();
        try {
            exporter.exportSong(file, model.get().getScoreSong());
        } catch (ExportException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot save MEI file", e);
            ShowError.show(ScoreEditApp.getMainStage(), "Cannot save MEI file", e);
        }
    }

    @FXML
    private void handleNew() {
        try {
            model.setValue(new ScoreEditModel());
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot create new score", e);
            ShowError.show(ScoreEditApp.getMainStage(), "Cannot create new score", e);
        }

    }

    private void initInteraction() {
        InteractionElementType [] elementsToSubscribe = new InteractionElementType [] {
                InteractionElementType.slur, InteractionElementType.accidental, InteractionElementType.clef
        };

        scoreSongView.getInteractionPresenter().subscribe(this, EventType.contextMenuRequest, elementsToSubscribe);
        scoreSongView.getInteractionPresenter().subscribe(this, EventType.mouseEntered, elementsToSubscribe);
        scoreSongView.getInteractionPresenter().subscribe(this, EventType.mouseExited,  elementsToSubscribe);
        scoreSongView.getInteractionPresenter().subscribe(this, EventType.dblClick, elementsToSubscribe);

        Scene scene = ScoreEditApp.getMainStage().getScene();
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                doKeyPressed(event);
            }
        });
    }

    @Override
    public void onEvent(EventType eventType, GraphicsElement graphicsElement) {
        switch (eventType) {
            case contextMenuRequest:
                doContextMenuRequest(graphicsElement);
                break;
            case mouseEntered:
                doMouseEntered(graphicsElement);
                break;
            case mouseExited:
                doMouseExited(graphicsElement);
                break;
            case dblClick:
                doDblClick(graphicsElement);
                break;
        }
    }

    private void doDblClick(GraphicsElement graphicsElement) {
        scoreSongView.getInteractionPresenter().selectElements(graphicsElement);
    }

    private void doMouseExited(GraphicsElement graphicsElement) {
        scoreSongView.getInteractionPresenter().onMouseExited(graphicsElement);
    }

    private void doMouseEntered(GraphicsElement graphicsElement) {
        scoreSongView.getInteractionPresenter().onMouseEntererd(graphicsElement);
    }

    private void doContextMenuRequest(GraphicsElement graphicsElement) {
        ShowError.show(ScoreEditApp.getMainStage(), "TO-DO: " + graphicsElement);
    }

    private void doKeyPressed(KeyEvent event) {
        List<GraphicsElement> selectedElements = scoreSongView.getInteractionPresenter().getSelectedElements();
        try {
            for (GraphicsElement selectedElement : selectedElements) {
                if (processKeyPressed(selectedElement.getNotationSymbol(), event.getCode(), event.isShiftDown())) {
                    event.consume();
                }
            }
        } catch (IM3Exception e) {
            String message = "Cannot process key event " + event;
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, message, e);
            ShowError.show(ScoreEditApp.getMainStage(), message, e);
        }
    }

    /**
     * Events are processed here and not in a polymorphic method because it depends on the application, not the object itself
     * @param notationSymbol
     * @param code
     */
    private boolean processKeyPressed(NotationSymbol notationSymbol, KeyCode code, boolean shiftDown) throws IM3Exception {
        // TODO: 14/3/18 Se podría hacer con reflexión
        boolean processed = false;
        if (notationSymbol instanceof LayoutCoreClef) {
            LayoutCoreClef layoutCoreClef = (LayoutCoreClef) notationSymbol;
            Clef clef = layoutCoreClef.getCoreSymbol();
            // TODO: 14/3/18 Generalizar esto
            Clef newClef = null;
            if (code == KeyCode.UP) {
                newClef = moveClefLine(clef, 1);
                processed = true;
            } else if (code == KeyCode.DOWN) {
                newClef = moveClefLine(clef, -1);
                processed = true;
            } else if (code == KeyCode.ACCEPT) {
                System.out.println("Clef " + clef + " ACCEPT");
                //TODO
            } else if (code == KeyCode.CANCEL) {
                //TODO
            }
            if (newClef != null) {
                scoreSongView.getLayout().replace(clef, newClef, shiftDown); //TODO documentarlo: con shift se cambian los pitches
                //scoreSongView.getLayout().repaint(); //TODO Deberíamos tener algo más genérico, cuando se cambie algo que se llame aquí
                scoreSongView.repaint();

            }
        }
        return processed;
    }

    // TODO: 14/3/18 LLevar a una clase especializada
    private Clef moveClefLine(Clef clef, int lineDifference) throws IM3Exception {
        try {
            return ImportFactories.createClef(clef.getNotationType(), clef.getNote().name(), clef.getLine() + lineDifference, clef.getOctaveChange());
        } catch (ImportException e) {
            throw new IM3Exception("Cannot create clef " + clef.getNote() + " at line " + (clef.getLine() + lineDifference) + " and octave change " + clef.getOctaveChange());
        }
    }

    private TableColumn<KernRecord, KernField> initializeKernColumn(int spine) {
        TableColumn<KernRecord, KernField> column = new TableColumn<>("Col");
        column.setCellValueFactory(record -> {
            SimpleObjectProperty<KernField> property = new SimpleObjectProperty<>();
            property.setValue(record.getValue().getField(spine));
            return property;
        });

        column.setCellFactory(EditCell.<KernRecord, KernField>forTableColumn(
                new StringConverter<KernField>() {
                    @Override
                    public String toString(KernField object) {
                        return object.toString();
                    }

                    @Override
                    public KernField fromString(String string) {
                        try {
                            return KernField.parseString(string);
                        } catch (ImportException e) {
                            Logger.getLogger(ScoreEditController.class.getName()).log(Level.INFO, "Cannot parse '"+string+"'", e);
                            throw new IM3RuntimeException(e);
                        }
                    }
                }
        ));
        // updates the dateOfBirth field on the PersonTableData object to the
        // committed value
        column.setOnEditCommit(event -> {
            final KernField value = event.getNewValue() != null ? event.getNewValue()
                    : event.getOldValue();
            ((KernRecord) event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()))
                    .setField(spine, value);

            updateScore(spine, value);
            tableViewKern.refresh();
        });

        return column;
    }

    //TODO - ver cómo hacer esto bien - ahora sólo inserto en el pentagram
    private void updateScore(int spine, KernField value) {
        if (value.getParsedObject() instanceof Clef) {
            Clef clef = (Clef) value.getParsedObject();
            try {
                scoreSongView.getLayout().add(clef);
                scoreSongView.repaint(); //TODO ¿hace falta?
            } catch (IM3Exception e) {
                Logger.getLogger(ScoreEditController.class.getName()).log(Level.INFO, "Cannot update score", e);
                ShowError.show(null, "Cannot update score", e);
            }
        }
    }

    private void setTableEditable() {
        tableViewKern.setEditable(true);
        // allows the individual cells to be selected
        tableViewKern.getSelectionModel().cellSelectionEnabledProperty().set(true);
        // when character or numbers pressed it will start edit in editable
        // fields
        tableViewKern.setOnKeyPressed(event -> {
            if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
                editFocusedCell();
            } else if (event.getCode() == KeyCode.RIGHT
                    || event.getCode() == KeyCode.TAB) {
                tableViewKern.getSelectionModel().selectNext();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                // work around due to
                // TableView.getSelectionModel().selectPrevious() due to a bug
                // stopping it from working on
                // the first column in the last row of the table
                selectPrevious();
                event.consume();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void editFocusedCell() {
        final TablePosition<KernRecord, ?> focusedCell = tableViewKern
                .focusModelProperty().get().focusedCellProperty().get();
        tableViewKern.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    @SuppressWarnings("unchecked")
    private void selectPrevious() {
        if (tableViewKern.getSelectionModel().isCellSelectionEnabled()) {
            // in cell selection mode, we have to wrap around, going from
            // right-to-left, and then wrapping to the end of the previous line
            TablePosition<KernRecord, ?> pos = tableViewKern.getFocusModel()
                    .getFocusedCell();
            if (pos.getColumn() - 1 >= 0) {
                // go to previous row
                tableViewKern.getSelectionModel().select(pos.getRow(),
                        getTableColumn(pos.getTableColumn(), -1));
            } else if (pos.getRow() < tableViewKern.getItems().size()) {
                // wrap to end of previous row
                tableViewKern.getSelectionModel().select(pos.getRow() - 1,
                        tableViewKern.getVisibleLeafColumn(
                                tableViewKern.getVisibleLeafColumns().size() - 1));
            }
        } else {
            int focusIndex = tableViewKern.getFocusModel().getFocusedIndex();
            if (focusIndex == -1) {
                tableViewKern.getSelectionModel().select(tableViewKern.getItems().size() - 1);
            } else if (focusIndex > 0) {
                tableViewKern.getSelectionModel().select(focusIndex - 1);
            }
        }
    }

    private TableColumn<KernRecord, ?> getTableColumn(
            final TableColumn<KernRecord, ?> column, int offset) {
        int columnIndex = tableViewKern.getVisibleLeafIndex(column);
        int newColumnIndex = columnIndex + offset;
        return tableViewKern.getVisibleLeafColumn(newColumnIndex);
    }


    private void initKernInput() throws ImportException {
        // remove headers
        /*tableViewKern.skinProperty().addListener((a, b, newSkin) -> {
            TableHeaderRow headerRow = ((TableViewSkinBase) newSkin).getTableHeaderRow();
            headerRow.setVisible(false);
            headerRow.setMinHeight(0);
            headerRow.setPrefHeight(0);
            headerRow.setMaxHeight(0);
        });*/

        // TODO: 3/4/18 - add - remove rows
        tableViewKern.getColumns().clear();
        for (int ispine=0; ispine<4; ispine++) {
            TableColumn<KernRecord, KernField> tableColumn = initializeKernColumn(ispine);
            tableViewKern.getColumns().add(tableColumn);
        }

        setTableEditable();

        for (int ivoice = 0; ivoice < 4; ivoice++) {
            KernRecord kernRecord = new KernRecord(4);
            for (int i = 0; i < 100; i++) {
                kernRecord.add(new KernField(null));
            }
            tableViewKern.getItems().add(kernRecord);
        }


    }

}