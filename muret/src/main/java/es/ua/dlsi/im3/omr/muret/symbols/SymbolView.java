package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.muret.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

/**
 * @autor drizo
 */
public class SymbolView extends BoundingBoxBasedView<OMRSymbol> {
    private static final Paint HOVER = Color.BLUE;
    private static final Paint SELECTED = Color.RED;
    private final RegionView regionView;

    Shape shapeInStaff;

    public SymbolView(ImageBasedAbstractController controller, RegionView regionView, OMRSymbol owner, Color color) throws IM3Exception {
        super(controller, regionView, owner.getX()-regionView.getOwner().getFromX(), owner.getY()-regionView.getOwner().getFromY(), owner.getWidth(), owner.getHeight(), owner, color);
        this.regionView = regionView;
        shapeInStaff = regionView.getAgnosticStaffView().addSymbol(this);
        initInteractionOnShape();
    }

    //TODO Armonizar todo esto (p.ej. tecla corrección....) - que lo lleve todo el controlador - ahora hacemos doble click - también botón arriba
    private void initInteractionOnShape() {
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
                selected.setValue(true);
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
        if (shapeInStaff != null) {
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

    public void doEdit() {
        this.doHighlight(true);
        shapeInStaff.setFill(SELECTED);
        regionView.getAgnosticStaffView().correctSymbol(this);
    }

    public void endEdit() {
        shapeInStaff.setFill(Color.BLACK);
        this.doHighlight(false);
    }

    public void changePosition(int lineSpaces) {
        owner.chagePosition(lineSpaces);
    }

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
        if (event.getCode() == KeyCode.DELETE) {
            this.regionView.delete(this);
        }
    }
}
