package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import javafx.event.EventHandler;
import javafx.scene.input.ContextMenuEvent;
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
    private Paint previousColor;

    enum State {idle, editing};

    State state = State.idle;

    public SymbolView(RegionView regionView, OMRSymbol owner, Color color) throws IM3Exception {
        super(regionView, owner.getX()-regionView.getOwner().getFromX(), owner.getY()-regionView.getOwner().getFromY(), owner.getWidth(), owner.getHeight(), owner, color);
        this.regionView = regionView;
        shapeInStaff = regionView.getAgnosticStaffView().addSymbol(owner);
        initInteractionOnShape();
    }

    //TODO Armonizar todo esto (p.ej. tecla corrección....) - ahora hacemos doble click - también botón arriba
    private void initInteractionOnShape() {
        shapeInStaff.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (state == State.idle) {
                    previousColor = shapeInStaff.getFill();
                    shapeInStaff.setFill(HOVER);
                }
            }
        });

        shapeInStaff.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (state == State.idle) {
                    shapeInStaff.setFill(previousColor);
                }
            }
        });

        shapeInStaff.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (state == State.idle) {
                    if (event.getClickCount() == 2) {
                        doEdit();
                    }
                }
            }
        });
    }

    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {

    }

    @Override
    public void highlight(boolean highlight) {
        super.highlight(highlight);
        if (shapeInStaff != null) { // may not be still created
            if (highlight) {
                shapeInStaff.setFill(color);
            } else {
                shapeInStaff.setFill(Color.BLACK);
            }
        }
    }

    public void doEdit() {
        shapeInStaff.setFill(SELECTED);
        regionView.getAgnosticStaffView().correctSymbol(this);
    }

    public void changePosition(int lineSpaces) {
        owner.chagePosition(lineSpaces);
    }

    public void changeSymbolType(AgnosticSymbolType agnosticSymbolType) {
        owner.changeAgnosticSymbolType(agnosticSymbolType);
    }

    public void setShapeInStaff(Shape newShape) {
        this.shapeInStaff = newShape;
    }


}
