package es.ua.dlsi.im3.gui.score.javafx;
import es.ua.dlsi.im3.core.IM3Exception;
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
import es.ua.dlsi.im3.core.score.layout.graphics.RGBA;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.EventType;
import es.ua.dlsi.im3.gui.score.IScoreSongViewEventSubscriptor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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


    ObjectProperty<ScoreEditModel> model;
    private ScoreSongView scoreSongView;

    public ScoreEditController() {
        model = new SimpleObjectProperty<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuItemSave.disableProperty().bind(model.isNull());
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
            scoreSongView = new ScoreSongView(scoreSong, model.get().getLayout());
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



    private void initInteraction() {
        InteractionElementType [] elementsToSubscribe = new InteractionElementType [] {
                InteractionElementType.slur, InteractionElementType.accidental, InteractionElementType.clef
        };

        scoreSongView.getInteractionController().subscribe(this, EventType.contextMenuRequest, elementsToSubscribe);
        scoreSongView.getInteractionController().subscribe(this, EventType.mouseEntered, elementsToSubscribe);
        scoreSongView.getInteractionController().subscribe(this, EventType.mouseExited,  elementsToSubscribe);
        scoreSongView.getInteractionController().subscribe(this, EventType.dblClick, elementsToSubscribe);

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
        scoreSongView.getInteractionController().selectElements(graphicsElement);
    }

    private void doMouseExited(GraphicsElement graphicsElement) {
        scoreSongView.getInteractionController().onMouseExited(graphicsElement);
    }

    private void doMouseEntered(GraphicsElement graphicsElement) {
        scoreSongView.getInteractionController().onMouseEntererd(graphicsElement);
    }

    private void doContextMenuRequest(GraphicsElement graphicsElement) {
        ShowError.show(ScoreEditApp.getMainStage(), "TO-DO: " + graphicsElement);
    }

    private void doKeyPressed(KeyEvent event) {
        List<GraphicsElement> selectedElements = scoreSongView.getInteractionController().getSelectedElements();
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
}