package es.ua.dlsi.im3.gui.adt.graph.javafx;

import es.ua.dlsi.im3.core.adt.graph.IEdgeLabel;
import es.ua.dlsi.im3.core.adt.graph.INodeLabel;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class GraphFXInteractionManager<LabelNodeType extends INodeLabel, LabelEdgeType extends IEdgeLabel> {
	Line interactionConnectingLine;
	GraphNodeFX<LabelNodeType, LabelEdgeType> lastSelectedNode;
	DirectedGraphViewFX<LabelNodeType, LabelEdgeType> graphFX;
    //IM3 ViewController viewController;
	private EventHandler<MouseEvent> mouseMoveEventHandler;
	private EventHandler<KeyEvent> escapeEventHandler;
	
	public GraphFXInteractionManager(DirectedGraphViewFX<LabelNodeType, LabelEdgeType> graphFX) {
    //IM3 , ViewController viewController) {
		this.interactionConnectingLine = null;
		this.lastSelectedNode = null;
		this.graphFX = graphFX;
        //IM3 this.viewController = viewController;
		initInteraction();
	}

	public void onNodeClicked(GraphNodeFX<LabelNodeType, LabelEdgeType> graphNodeFX) {
		if (interactionConnectingLine == null) {
			lastSelectedNode = graphNodeFX;
			interactionConnectingLine = new Line();
			interactionConnectingLine.setStroke(Color.BLUE); //TODO
			interactionConnectingLine.setStrokeWidth(1);
			graphFX.addEdgeTemporaryLine(interactionConnectingLine);
			//TODO conectores (igual que en edgefx)
			interactionConnectingLine.setStartX(graphNodeFX.getRoot().getLayoutBounds().getMaxX());
			interactionConnectingLine.setStartY(graphNodeFX.getRoot().getLayoutBounds().getMaxY());
			interactionConnectingLine.setEndX(interactionConnectingLine.getStartX());
			interactionConnectingLine.setEndY(interactionConnectingLine.getStartY());
			//viewController.getInteractionPresenter().registerEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveEventHandler);
            //IM3 GenericEventsInteractionController.getInstance().registerEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveEventHandler);
            //IM3 GenericEventsInteractionController.getInstance().registerEventHandler(KeyEvent.KEY_PRESSED, escapeEventHandler);
		} else {
			//System.out.println("!!!!!!!!!! Connecting!!!");
			graphFX.connect(lastSelectedNode, graphNodeFX);
			removeConnectingLine();
		}
	}
	
	private void initInteraction() {
		mouseMoveEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (interactionConnectingLine != null) {
					Point2D point = interactionConnectingLine.getParent().sceneToLocal(event.getSceneX(), event.getSceneY());
					interactionConnectingLine.setEndX(point.getX()-2); // -2 to avoid the line receive the mouse click event that should receive the node
					interactionConnectingLine.setEndY(point.getY()-2);
					event.consume();
				}
			}
		};
		escapeEventHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					removeConnectingLine();
					event.consume();
				}
			}
		};
	}

	protected void removeConnectingLine() {
		graphFX.removeEdgeTemporaryLine(interactionConnectingLine);
		interactionConnectingLine = null;
		lastSelectedNode = null;
		// we don't want to receive more mouse move events
		//viewController.getInteractionPresenter().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveEventHandler);
        //IM3 GenericEventsInteractionController.getInstance().removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveEventHandler);
        //IM3 GenericEventsInteractionController.getInstance().removeEventHandler(KeyEvent.KEY_PRESSED, escapeEventHandler);
	}	
}
