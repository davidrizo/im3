package es.ua.dlsi.im3.gui.javafx;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import javafx.beans.binding.DoubleExpression;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class SelectionRectangle {
    enum State {
		firstClick, // until it has some width and height it is not really created 
		creating,  // from the first click to the click release, while dragging
		created // after mouse release
		}; 
	State state;
	Rectangle selectionRectangle;
	Group selectingRectangleGroup;

	Line topLine;
	Line leftLine;
	Line bottomLine;
	Line rightLine;
	
	public SelectionRectangle(double x, double y) {
		selectionRectangle = new Rectangle();
		selectionRectangle.setStrokeWidth(0); //TODO
		selectionRectangle.setFill(Color.RED);
		selectionRectangle.setOpacity(0.2);
		selectionRectangle.setX(x);
		selectionRectangle.setY(y);
		state = State.firstClick;
		
		int VISUAL_GAP = 20; // empirical 
		topLine = createSelectionHandler(
				Cursor.V_RESIZE,
				selectionRectangle.xProperty().add(VISUAL_GAP),
				selectionRectangle.yProperty(),
				selectionRectangle.xProperty().add(selectionRectangle.widthProperty()).subtract(VISUAL_GAP),
				selectionRectangle.yProperty()
				);
		
		topLine.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				double difference = selectionRectangle.getY() - event.getY();
				
				selectionRectangle.setY(event.getY());
				selectionRectangle.setHeight(selectionRectangle.getHeight()+difference);
				event.consume();
			}
		});
		
		leftLine = createSelectionHandler(
				Cursor.H_RESIZE,
				selectionRectangle.xProperty(),
				selectionRectangle.yProperty().add(VISUAL_GAP),
				selectionRectangle.xProperty(),
				selectionRectangle.yProperty().add(selectionRectangle.heightProperty()).subtract(VISUAL_GAP)
				);
		leftLine.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				double difference = selectionRectangle.getX() - event.getX();
				/*if (difference < 0) {
					selectionRectangle.setX(selectionRectangle.getX() + selectionRectangle.getWidth());
					selectionRectangle.setWidth(event.getX()-selectionRectangle.getX());
				} else */ //TODO cuando se cruzan
				{					
					selectionRectangle.setX(event.getX());
					selectionRectangle.setWidth(selectionRectangle.getWidth()+difference);
				}
				event.consume();
			}
		});		

		bottomLine = createSelectionHandler(
				Cursor.V_RESIZE,
				selectionRectangle.xProperty().add(VISUAL_GAP),
				selectionRectangle.yProperty().add(selectionRectangle.heightProperty()),
				selectionRectangle.xProperty().add(selectionRectangle.widthProperty()).subtract(VISUAL_GAP),
				selectionRectangle.yProperty().add(selectionRectangle.heightProperty())
				);
		bottomLine.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				double newHeight = event.getY()-selectionRectangle.getY();
				
				selectionRectangle.setHeight(newHeight);
				event.consume();
			}
		});				
		rightLine = createSelectionHandler(
				Cursor.H_RESIZE,
				selectionRectangle.xProperty().add(selectionRectangle.widthProperty()),
				selectionRectangle.yProperty().add(VISUAL_GAP),
				selectionRectangle.xProperty().add(selectionRectangle.widthProperty()),
				selectionRectangle.yProperty().add(selectionRectangle.heightProperty()).subtract(VISUAL_GAP)
				);
		rightLine.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				double newWidth = event.getX()-selectionRectangle.getX();
				
				selectionRectangle.setWidth(newWidth);
				event.consume();
			}
		});		
		
		selectingRectangleGroup = new Group();
		selectingRectangleGroup.getChildren().addAll(selectionRectangle, topLine, bottomLine, leftLine, rightLine);
	}
	
	
	protected Line createSelectionHandler(Cursor cursor, DoubleExpression fromX, DoubleExpression fromY, DoubleExpression toX, DoubleExpression toY) {
		/*Distant light = new Distant();
        light.setAzimuth(-135.0f);
 
        Lighting l = new Lighting();
        l.setLight(light);
        l.setSurfaceScale(5.0f);*/
        
		Line handlerLine = new Line();
		handlerLine.startXProperty().bind(fromX);
		handlerLine.startYProperty().bind(fromY);
		handlerLine.endXProperty().bind(toX);
		handlerLine.endYProperty().bind(toY);
		handlerLine.setStrokeWidth(5);
		handlerLine.setStrokeLineCap(StrokeLineCap.ROUND);
		handlerLine.setStroke(Color.DARKBLUE);
		handlerLine.setCursor(cursor);
		
		handlerLine.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				event.consume(); // avoid passing the click to the main handler to be able to drag it
			}
		});
		
		
		return handlerLine;
	}

	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}

	public void changeEndPoint(double x, double y) {
		selectionRectangle.setWidth(x - selectionRectangle.getX());
		selectionRectangle.setHeight(y - selectionRectangle.getY());
	}

	public Bounds getBounds() {
		return selectionRectangle.getBoundsInLocal();
	}

	public Group getRoot() {
		return selectingRectangleGroup;
	}

	public void changeState() {
		switch (state) {
		case firstClick: 
			state = State.creating;
			break;
		case creating:
		case created:
			state = State.created;
			break;
		default:
			throw new IM3RuntimeException("Unexpected state: " + state);
		}
	}

	public State getState() {
		return state;
	}

    public boolean isInFirstClickState() {
	    return state == State.firstClick;
    }



}
