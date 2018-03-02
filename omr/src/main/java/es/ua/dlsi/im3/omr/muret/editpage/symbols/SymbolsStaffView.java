package es.ua.dlsi.im3.omr.muret.editpage.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreClef;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSignTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.omr.muret.Event;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.muret.editpage.TranscriptionStaffView;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It contains a set of lines where agnostic symbols will be positioned
 */
public class SymbolsStaffView extends TranscriptionStaffView {
    private static double MARGIN_TOP = 20; //TODO
    LayoutFont manuscriptLayoutFont;
    NotationType notationType;
    /**
     * Bottom is 0, next 1, etc...
     */
    Line[] lines;

    Group linesGroup;
    Group transcriptionGroup;

    SymbolsRegionView symbolsRegionView;

    SymbolViewState state;
    private SymbolView editingSymbol;

    public SymbolsStaffView(OMRPage page, OMRRegion region) throws IM3Exception {
        super(page, region);
        manuscriptLayoutFont = page.getManuscriptLayoutFont();
        drawStaff();
        loadRegion();
        loadTranscription();
        initInteraction();
    }

    private void loadRegion() {
        symbolsRegionView = new SymbolsRegionView(this, staffRegion);
        manuscriptPane.getChildren().add(symbolsRegionView);
    }

    private void drawStaff() {
        linesGroup = new Group();
        lines = new Line[5];
        for (int i=0; i<lines.length; i++) {
             double startX = 0;
             double endX = 1400; //TODO
             double y = MARGIN_TOP + i* LayoutConstants.SPACE_HEIGHT; //TODO See LayoutStaff later for doing zoom
             lines[4-i] = new Line(startX, y, endX, y);
             linesGroup.getChildren().add(lines[4-i]);
        }
        transcriptionPane.getChildren().add(linesGroup);

        transcriptionPane.setMinHeight(100); //TODO
    }

    private void initInteraction() {
        state = SymbolViewState.idle;
        manuscriptStaffExcerptView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                handleEvent(new Event<MouseEvent>(t));
                //doMousePressed(t);
                //t.consume();
            }
        });

        manuscriptStaffExcerptView.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                handleEvent(new Event<MouseEvent>(t));
                //doMouseDragged(t);
                //t.consume();
            }

        });

        manuscriptStaffExcerptView.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                handleEvent(new Event<MouseEvent>(t));
                //doMouseReleased();
                //t.consume();
                //} catch (IM3Exception e) {
                //  ShowError.show(OMRApp.getMainStage(), "Cannot add staff", e);
                //}
            }
        });
        OMRApp.getKeyEventManager().setCurrentKeyEventHandler(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("EVENTO TECLADO");
                handleEvent(new Event<KeyEvent>(event));
            }
        });
    }

    public void handleEvent(Event t) {
        KeyEvent keyEvent = null;
        MouseEvent mouseEvent = null;
        if (t.getContent() instanceof MouseEvent) {
            mouseEvent = (MouseEvent) t.getContent();
        } else if (t.getContent() instanceof KeyEvent) {
            keyEvent = (KeyEvent) t.getContent();
        }
        switch (state) {
            case idle:
                if (t instanceof SymbolEditEvent) {
                    editingSymbol = ((SymbolEditEvent)t).getSymbolView();
                    editingSymbol.beginEdit();
                    ((SymbolEditEvent)t).getContent().consume();
                    changeState(SymbolViewState.editing);
                }
                break;
            case editing:
                if (mouseEvent != null && mouseEvent.getButton() == MouseButton.PRIMARY) {
                    editingSymbol.acceptEdit();
                    changeState(SymbolViewState.idle);
                    mouseEvent.consume();
                } else if (keyEvent != null) {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        editingSymbol.acceptEdit(); //TODO Comando
                        changeState(SymbolViewState.idle);
                        keyEvent.consume();
                    } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                        editingSymbol.cancelEdit();
                        changeState(SymbolViewState.idle);
                        keyEvent.consume();
                    } else if (keyEvent.getCode() == KeyCode.DELETE) {
                        editingSymbol.getRegionView().getOmrRegion().removeSymbol(editingSymbol.getOmrSymbol());
                        changeState(SymbolViewState.idle);
                        keyEvent.consume();
                    }
                }
                break;
        }
    }


    protected void changeState(SymbolViewState newState) {
        state = newState;
        Logger.getLogger(SymbolsStaffView.class.getName()).log(Level.INFO, "Changing state to {0}", newState);
    }


    private void loadTranscription() throws IM3Exception {
        notationType = page.getNotationType();
        if (notationType == null) {
            throw new IM3Exception("Project has not a notation type set");
        }
        transcriptionGroup = new Group();
        //TODO PUESTO A PIÑÓN DE PRUEBA
        LayoutCoreClef layoutCoreClef = new LayoutCoreClef(manuscriptLayoutFont, new ClefG2());
        layoutCoreClef.getPosition().setX(new CoordinateComponent(40));
        layoutCoreClef.getPosition().setReferenceY(new CoordinateComponent(lines[1].getStartY())); //TODO
        transcriptionGroup.getChildren().add(layoutCoreClef.getGraphics().getJavaFXRoot());
        SVGPath p0 = (SVGPath) layoutCoreClef.getGraphics().getJavaFXRoot();
        p0.setFill(symbolsRegionView.stripeColors[0]);
        //layoutCoreClef.setColor(symbolsRegionView.getSymbolViewArrayList().get(0).getColor());

        LayoutCoreSignTimeSignature layoutCoreSignTimeSignature = new LayoutCoreSignTimeSignature(manuscriptLayoutFont, new TimeSignatureCommonTime(notationType));
        layoutCoreSignTimeSignature.getPosition().setX(new CoordinateComponent(110)); //TODO Igual que la X del símbolo
        layoutCoreSignTimeSignature.getPosition().setReferenceY(new CoordinateComponent(lines[2].getStartY())); //TODO
        transcriptionGroup.getChildren().add(layoutCoreSignTimeSignature.getGraphics().getJavaFXRoot());
        transcriptionPane.getChildren().add(transcriptionGroup);
        SVGPath p1 = (SVGPath) layoutCoreSignTimeSignature.getGraphics().getJavaFXRoot();
        //p1.setFill(symbolsRegionView.getSymbolViewArrayList().get(1).getColor());
        p1.setFill(symbolsRegionView.stripeColors[1]);
        //layoutCoreSignTimeSignature.setColor(symbolsRegionView.getSymbolViewArrayList().get(0).getColor());


    }
}
