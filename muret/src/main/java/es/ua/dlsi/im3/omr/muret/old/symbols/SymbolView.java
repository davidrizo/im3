package es.ua.dlsi.im3.omr.muret.old.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.gui.interaction.ISelectableTraversable;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.muret.old.IOMRSymbolBaseView;
import es.ua.dlsi.im3.omr.muret.old.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.old.OMRApp;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import es.ua.dlsi.im3.omr.muret.old.BoundingBoxBasedView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class SymbolView extends BoundingBoxBasedView<OMRSymbol> implements IOMRSymbolBaseView {
    private static final Paint HOVER = Color.BLUE;
    private static final Paint SELECTED = Color.RED;
    private final RegionView regionView;
    Shape shapeInStaff;
    boolean editing;
    StrokesView strokesView;

    public SymbolView(String ID, ImageBasedAbstractController controller, RegionView regionView, OMRSymbol owner, Color color) throws IM3Exception {
        super(ID, controller, regionView, owner.getX()-regionView.getOwner().getFromX(), owner.getY()-regionView.getOwner().getFromY(), owner.getWidth(), owner.getHeight(), owner, color);
        this.regionView = regionView;
        shapeInStaff = regionView.getAgnosticStaffView().addSymbol(this);
        initInteractionOnShape();
        if (owner.getStrokes() != null) {
            strokesView = new StrokesView(owner.getStrokes(), -regionView.getOwner().getFromX(), -regionView.getOwner().getFromY(), RegionView.STROKES_COLOR); //TODO Strokes color no así
        }
    }

    public StrokesView getStrokesView() {
        return strokesView;
    }

    public void setStrokesView(StrokesView strokesView) {
        this.strokesView = strokesView;
    }

    //TODO Armonizar todo esto (p.ej. tecla corrección....) - que lo lleve todo el controlador - ahora hacemos doble click - también botón arriba
    private void initInteractionOnShape() {
        editing = false;
        shapeInStaff.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!selected.get()) {
                    setHover(true);
                }
            }
        });

        shapeInStaff.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!selected.get()) {
                    setHover(false);
                }
            }
        });

        shapeInStaff.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    try {
                        controller.doSelect(owner);
                    } catch (IM3Exception e) {
                        Logger.getLogger(SymbolView.class.getName()).log(Level.WARNING, "Cannot select symbol", e);
                        ShowError.show(OMRApp.getMainStage(), "Cannot select symbol", e);
                    }
                    //selected.setValue(true);
                }
            }
        });

        this.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                doHover(newValue);
            }
        });
    }

    @Override
    protected void doHover(boolean enable) {
        super.doHover(enable);
        if (shapeInStaff != null && !selected.get()) {
            if (enable) {
                shapeInStaff.setFill(HOVER);
            } else {
                shapeInStaff.setFill(Color.BLACK);
            }
        }
    }

    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {

    }

    @Override
    public void doHighlight(boolean highlight) {
        super.doHighlight(highlight);
        if (shapeInStaff != null) { // may not be still created
            if (highlight) {
                shapeInStaff.setFill(SELECTED);
            } else {
                shapeInStaff.setFill(Color.BLACK);
            }
        }
    }

    /*public void doEdit() {
        if (!editing) {
            editing = true;
            this.doHighlight(true);
            shapeInStaff.setFill(SELECTED);
            regionView.getAgnosticStaffView().correctSymbol(this);
        }
    }*/

   /* public void endEdit() {
        if (editing) {
            editing = false;
            shapeInStaff.setFill(Color.BLACK);
            this.doHighlight(false);
            regionView.doEndEdit();
        }
    }*/

    public PositionInStaff changePosition(int lineSpaces) {
        return owner.chageRelativePosition(lineSpaces);
    }

    /*public PositionInStaff getPositionInStaff() {
        return owner.getPositionInStaff();
    }

    public void setPositionInStaff(PositionInStaff positionInStaff) {
        owner.setPositionInStaff(positionInStaff);
    }*/

    public AgnosticSymbolType changeSymbolType(AgnosticSymbolType agnosticSymbolType) {
        return owner.changeAgnosticSymbolType(agnosticSymbolType);
    }

    public void setShapeInStaff(Shape newShape) {
        this.shapeInStaff = newShape;
        initInteractionOnShape(); //TODO Ver por qué no se deja seleccionar otra vez
    }

    @Override
    public void handle(KeyEvent event) {
        super.handle(event);
        switch (event.getCode()) {
            case DELETE:
                this.regionView.delete(this);
                break;
            /*case C:
                ((SymbolCorrectionController)controller).doChangeSymbol();
                break;*/
            case ENTER:
                acceptCorrection();
                break;
            case ESCAPE:
                cancelCorrection();
                //regionView.doEndEdit();
                break;
            case F:
                doFlipStem();
                break;
            case UP:
                if (event.isShortcutDown()) {
                    regionView.getAgnosticStaffView().doChangePosition(1, this);
                    break;
                }
                break;
            case DOWN:
                if (event.isShortcutDown()) {
                    regionView.getAgnosticStaffView().doChangePosition(-1, this);
                    break;
                }
                break;
        }
    }

    @Override
    public ISelectableTraversable getSelectionParent() {
        return controller;
    }

    @Override
    public OMRSymbol getOMRSymbol() {
        return this.owner;
    }

    public void acceptCorrection() {
        throw new UnsupportedOperationException("TO-DO");
    }

    public void cancelCorrection() {
        throw new UnsupportedOperationException("TO-DO");
    }

    public void doChangePosition(int linespace) {
        regionView.doChangePosition(this, linespace);
    }

    public void doChangeSymbolType(String agnosticString) {
        regionView.doChangeSymbolType(this, agnosticString);
    }

    public void doFlipStem() {
        regionView.doFlipStem(this);
    }
}
