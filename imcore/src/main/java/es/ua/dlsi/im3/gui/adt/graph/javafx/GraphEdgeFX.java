package es.ua.dlsi.im3.gui.adt.graph.javafx;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.gui.adt.graph.viewmodel.GraphEdgeViewModel;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;

public class GraphEdgeFX {

	private GraphEdgeViewModel<?, ?> edge;
	private Line line;

	public GraphEdgeFX(GraphEdgeViewModel<?, ?> edge, DoubleBinding scaleX, DoubleBinding scaleY) throws IM3Exception {
		if (edge == null) {
			throw new IM3RuntimeException("Edge is null!!!");
		}
		this.edge = edge;
		
		//System.out.println("EDGE: " + edge.toString() + " from " + edge.getFrom().hashCode() + " to " + edge.getTo().hashCode());
		line = new Line();
		this.line.setVisible(edge.isVisible());
		line.setStroke(Color.BLUE); //TODO
		line.setFill(Color.BLUE); 
		line.setStrokeWidth(2); //TODO
		line.setStrokeType(StrokeType.CENTERED);
		line.startXProperty().bind(scaleX.multiply(edge.getLine().getFrom().getAbsoluteX()));
		line.startYProperty().bind(scaleY.multiply(edge.getLine().getFrom().getAbsoluteY()));
		line.endXProperty().bind(scaleX.multiply(edge.getLine().getTo().getAbsoluteX()));
		line.endYProperty().bind(scaleY.multiply(edge.getLine().getTo().getAbsoluteY()));
	}

	public Line getRoot() {
		return line;
	}

	public GraphEdgeViewModel<?, ?> getEdge() {
		return edge;
	}
	
	
}
