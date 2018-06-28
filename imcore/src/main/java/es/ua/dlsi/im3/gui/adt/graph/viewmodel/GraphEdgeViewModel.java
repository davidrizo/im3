package es.ua.dlsi.im3.gui.adt.graph.viewmodel;

import es.ua.dlsi.im3.core.adt.graph.GraphEdge;
import es.ua.dlsi.im3.core.adt.graph.IEdgeLabel;
import es.ua.dlsi.im3.core.adt.graph.INodeLabel;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

public class GraphEdgeViewModel<LabelNodeType extends INodeLabel, LabelEdgeType extends IEdgeLabel> {
	GraphNodeViewModel<LabelNodeType, LabelEdgeType> from;
	GraphNodeViewModel<LabelNodeType, LabelEdgeType> to;
	GraphEdge<LabelEdgeType> graphEdge;
	Line line;
	boolean visible;
	
	public GraphEdgeViewModel(GraphEdge<LabelEdgeType> graphEdge, GraphNodeViewModel<LabelNodeType, LabelEdgeType> from, GraphNodeViewModel<LabelNodeType, LabelEdgeType> to) {
		super();
		this.graphEdge = graphEdge;
		this.from = from;
		this.to = to;
		line = new Line(null, null, null, null); // TODO: 1/5/18
		visible = true;
	}

	public GraphNodeViewModel<LabelNodeType, LabelEdgeType> getFrom() {
		return from;
	}

	public GraphNodeViewModel<LabelNodeType, LabelEdgeType> getTo() {
		return to;
	}

	public GraphEdge<LabelEdgeType> getGraphEdge() {
		return graphEdge;
	}

	public GraphEdge<LabelEdgeType> getLabel() {
		return graphEdge;
	}
	
	//TODO tipo de línea, ...
	public Line getLine() {
		return line;
	}
	
	public void setFromX(double x) {
        throw new UnsupportedOperationException("TO-DO"); // TODO: 1/5/18
    }

	public void setFromY(double y) {
            throw new UnsupportedOperationException("TO-DO"); // TODO: 1/5/18
	}

	public void setToY(double y) {
            throw new UnsupportedOperationException("TO-DO"); // TODO: 1/5/18
	}

	public void setVisible(boolean visible) {
		this.visible = visible;		
	}

	public boolean isVisible() {
		return visible;
	}

	public void setLineWidth(double edgeLineWidth) {
		this.line.setThickness(edgeLineWidth);		
	}
}
