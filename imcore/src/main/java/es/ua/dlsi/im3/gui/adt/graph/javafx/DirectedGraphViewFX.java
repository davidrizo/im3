package es.ua.dlsi.im3.gui.adt.graph.javafx;

import java.util.HashMap;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.graph.GraphEdge;
import es.ua.dlsi.im3.core.adt.graph.GraphNode;
import es.ua.dlsi.im3.core.adt.graph.IEdgeLabel;
import es.ua.dlsi.im3.core.adt.graph.INodeLabel;
import es.ua.dlsi.im3.gui.adt.graph.viewmodel.DirectedGraphViewModel;
import es.ua.dlsi.im3.gui.adt.graph.viewmodel.GraphEdgeViewModel;
import es.ua.dlsi.im3.gui.adt.graph.viewmodel.GraphNodeViewModel;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.utils.ParallelModelFactory;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.MapChangeListener;
import javafx.scene.Group;
import javafx.scene.shape.Line;

/**
 * It is a miniature of the graph
 * @author drizo
 *
 */
public class DirectedGraphViewFX<LabelNodeType extends INodeLabel, LabelEdgeType extends IEdgeLabel> { //IM3 implements IExternalNotationComponentGroupView {
	DirectedGraphViewModel<LabelNodeType, LabelEdgeType> graph;
	Group root;	
	DoubleBinding scaleX;
	DoubleBinding scaleY;
	HashMap<GraphNodeViewModel<LabelNodeType, LabelEdgeType>, GraphNodeFX<LabelNodeType, LabelEdgeType>> nodes;
	HashMap<GraphEdgeViewModel<LabelNodeType, LabelEdgeType>, GraphEdgeFX> edges;
	ParallelModelFactory<IGraphLabelView> factory;
	
	private GraphFXInteractionManager<LabelNodeType, LabelEdgeType> interactionManager;
    //IM3 ViewController viewController;
	
	public DirectedGraphViewFX(//IM3 ViewController viewController,
                               DirectedGraphViewModel<LabelNodeType, LabelEdgeType> graph, DoubleBinding scaleX, DoubleBinding scaleY) throws IM3Exception {
		super();
        //IM3 this.viewController = viewController;
		interactionManager = new GraphFXInteractionManager<>(this); //IM3 , viewController);
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.graph = graph;
		this.nodes = new HashMap<>();
		this.edges = new HashMap<>();
		root = new Group();
		factory = new ParallelModelFactory<>("views", "View");
		//mainPanel.prefHeightProperty().bind(width);
		//mainPanel.prefHeightProperty().bind(height);
		paintGraph();
	}

	private void paintGraph() throws IM3Exception {
		for (GraphNodeViewModel<LabelNodeType, LabelEdgeType> node: this.graph.getNodes()) {
			if (node.isVisible()) { // some nodes are not represented
				//GraphNodeFX<LabelNodeType, LabelEdgeType> mn = 
				paintNode(node);
			}
		}	
		
		for (GraphNodeViewModel<LabelNodeType, LabelEdgeType> node: this.graph.getNodes()) {
			if (node.getOutEdges() != null) {
				for (GraphEdgeViewModel<LabelNodeType, LabelEdgeType> edge: node.getOutEdges()) {					
					paintEdge(edge);
				}
			}
		}
		
		// observe changes
		graph.nodeMapProperty().addListener(new MapChangeListener<GraphNode<LabelNodeType, LabelEdgeType>, GraphNodeViewModel<LabelNodeType, LabelEdgeType>>() {
			@Override
			public void onChanged(
					Change<? extends GraphNode<LabelNodeType, LabelEdgeType>, ? extends GraphNodeViewModel<LabelNodeType, LabelEdgeType>> change) {
				if (change.wasRemoved()) {
					removeNode(change.getValueRemoved());
				} else if (change.wasAdded()) {
					try {
						paintNode(change.getValueAdded());
					} catch (IM3Exception e) {
						e.printStackTrace();
						Logger.getLogger(DirectedGraphViewFX.class.getName()).warning("Cannot create node view for " + change);
						throw new IM3RuntimeException("Cannot create node view for " + change);
					}
				} else {
					throw new IM3RuntimeException("Neither added or removed !!");
				}				
			}			
		});	
		
		graph.edgeMapProperty().addListener(new MapChangeListener<GraphEdge<LabelEdgeType>, GraphEdgeViewModel<LabelNodeType, LabelEdgeType>>() {

			@Override
			public void onChanged(
					Change<? extends GraphEdge<LabelEdgeType>, ? extends GraphEdgeViewModel<LabelNodeType, LabelEdgeType>> change) {
				
				if (change.wasAdded()) {
					try {
						paintEdge(change.getValueAdded());
					} catch (IM3Exception e) {
						e.printStackTrace();
						Logger.getLogger(DirectedGraphViewFX.class.getName()).warning("Cannot create edge view for " + change);
						throw new IM3RuntimeException("Cannot create edge view for " + change);
					}
				} else if (change.wasRemoved()) {
					removeEdge(change.getValueRemoved());
				} else {
					throw new IM3RuntimeException("Neither added or removed !!");
				}								
			}
		});
	}

	protected void removeEdge(GraphEdgeViewModel<LabelNodeType, LabelEdgeType> edgeRemoved) {
		GraphEdgeFX edgeFX = edges.get(edgeRemoved);
		if (edgeFX == null) {
			throw new IM3RuntimeException("Cannot find edge " + edgeRemoved);
		}
		
		root.getChildren().remove(edgeFX.getRoot());		
	}

	private void paintEdge(GraphEdgeViewModel<LabelNodeType, LabelEdgeType> edge) throws IM3Exception {
		GraphEdgeFX me = new GraphEdgeFX(edge, scaleX, scaleY);
		edges.put(edge, me);
		root.getChildren().add(me.getRoot());
	}

	protected void removeNode(GraphNodeViewModel<LabelNodeType, LabelEdgeType> nodeRemoved) {
		GraphNodeFX<LabelNodeType, LabelEdgeType> nodeFX = nodes.get(nodeRemoved);
		if (nodeFX == null) {
			throw new IM3RuntimeException("Cannot find node " + nodeRemoved);
		}
		
		root.getChildren().remove(nodeFX.getRoot());		
	}
	
	private GraphNodeFX<LabelNodeType, LabelEdgeType> paintNode(GraphNodeViewModel<LabelNodeType, LabelEdgeType> node) throws IM3Exception {
		IGraphLabelView labelView = null;
		if (node.getLabel() != null) {
			try {
				labelView = factory.createParallelClassFor(node.getLabel());
				labelView.paint(scaleX, scaleY);
			} catch (IM3Exception e) {
				Logger.getLogger(DirectedGraphViewFX.class.getName()).info("Label " + node.getLabel().getClass() + " has not an associated view");
				throw e;
			} catch (InstantiationException e) {
				e.printStackTrace();
				Logger.getLogger(DirectedGraphViewFX.class.getName()).warning("Cannot instantiate view: " + e);
				throw new IM3Exception(e);
			}
		} 
		GraphNodeFX<LabelNodeType, LabelEdgeType> mn = new GraphNodeFX<>(this, node, labelView, scaleX, scaleY);
		nodes.put(node, mn);						
		root.getChildren().add(mn.getRoot());
		return mn;
	}

    //IM3 @Override
	public Group getRoot() {
		return root;
	}


	public GraphFXInteractionManager<LabelNodeType, LabelEdgeType> getInteractionManager() {
		return interactionManager;
	}

	public void addEdgeTemporaryLine(Line interactionConnectingLine) {
		root.getChildren().add(interactionConnectingLine);
	}

	public void removeEdgeTemporaryLine(Line interactionConnectingLine) {
		root.getChildren().remove(interactionConnectingLine);		
	}

	//TODO Comando
	public void connect(GraphNodeFX<LabelNodeType, LabelEdgeType> lastSelectedNode,
			GraphNodeFX<LabelNodeType, LabelEdgeType> graphNodeFX) {
		//TODO Label edge pongo null ahora
		GraphEdgeViewModel<LabelNodeType, LabelEdgeType> edge;
		try {
			edge = lastSelectedNode.getGraphNodeViewModel().connectTo(graphNodeFX.getGraphNodeViewModel(), null);
			//paintEdge(edge);
		} catch (IM3Exception e) {
			Logger.getLogger(DirectedGraphViewFX.class.getName()).warning("Cannot create edge: " + e);
			ShowError.show(null, "Cannot create edge", e); // TODO: 1/5/18 stage = null
		}
	}

	
	
	
}
