package es.ua.dlsi.im3.gui.adt.graph.javafx;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graph.IEdgeLabel;
import es.ua.dlsi.im3.core.adt.graph.INodeLabel;
import es.ua.dlsi.im3.gui.adt.graph.viewmodel.GraphNodeViewModel;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GraphNodeFX<LabelNodeType extends INodeLabel, LabelEdgeType extends IEdgeLabel> {
	Node root;
	private DirectedGraphViewFX<LabelNodeType, LabelEdgeType> graphFX;
	private GraphNodeViewModel<LabelNodeType, LabelEdgeType> nodeViewModel;
	/**
	 * 
	 * @param node
	 * @param labelView Can be null
	 * @param scaleX
	 * @param scaleY
	 * @throws IM3Exception
	 */
	public GraphNodeFX(DirectedGraphViewFX<LabelNodeType, LabelEdgeType> graphFX, GraphNodeViewModel<LabelNodeType, LabelEdgeType> node, IGraphLabelView labelView, DoubleBinding scaleX, DoubleBinding scaleY) throws IM3Exception {
		this.graphFX = graphFX;
		this.nodeViewModel = node;
		Rectangle rectangle = new Rectangle();
		rectangle.xProperty().bind(scaleX.multiply(node.getX()));
		rectangle.yProperty().bind(scaleY.multiply(node.getY()));
		rectangle.widthProperty().bind(scaleX.multiply(node.getWidth()));
		rectangle.heightProperty().bind(scaleY.multiply(node.getHeight()));
		rectangle.setFill(Color.TRANSPARENT);
		//rectangle.setOpacity(0.01);
		rectangle.setStrokeWidth(1);
		rectangle.getStrokeDashArray().addAll(5d); //TODO Discontínua
		
		if (labelView == null) {
			root = rectangle;
			rectangle.setStroke(Color.GRAY);
		} else {
			rectangle.strokeProperty().bind(labelView.colorProperty());
			Group g = new Group(rectangle, labelView.getRoot());
			root = g;
		}		
		
		initInteraction();
	}

	public Node getRoot() {
		return root;
	}
	

	private void initInteraction() {
		root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				graphFX.getInteractionManager().onNodeClicked(GraphNodeFX.this);
			}			
		});
		
	}

	public GraphNodeViewModel<LabelNodeType, LabelEdgeType> getGraphNodeViewModel() {
		return this.nodeViewModel;
		
	}
	
}
