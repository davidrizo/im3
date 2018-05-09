package es.ua.dlsi.im3.gui.graph.viewmodel;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.graph.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.util.Collection;
import java.util.Map;

/**
 * Used to maintain the view information such as coordinates that must be set by a renderer.
 * It is kept in this structure for allowing several simultaneous views of the same model 
 * @author drizo
 *
 * @param <LabelNodeType>
 * @param <LabelEdgeType>
 */
public class DirectedGraphViewModel<LabelNodeType extends INodeLabel, LabelEdgeType extends IEdgeLabel> {
	DirectedGraph<LabelNodeType, LabelEdgeType> graph;
	ObservableMap<GraphNode<LabelNodeType, LabelEdgeType>, GraphNodeViewModel<LabelNodeType, LabelEdgeType>> nodeMap;
	ObservableMap<GraphEdge<LabelEdgeType>, GraphEdgeViewModel<LabelNodeType, LabelEdgeType>> edgeMap;
	private IDirectedGraphRenderer<LabelNodeType, LabelEdgeType> renderer;
	
	/**
	 * It should be created using a IDirectedGraphRenderer
	 * @param graph
	 * @throws IM3Exception
	 */
	public DirectedGraphViewModel(IDirectedGraphRenderer<LabelNodeType, LabelEdgeType> renderer, DirectedGraph<LabelNodeType, LabelEdgeType> graph) throws IM3Exception {
		super();
		this.renderer = renderer;
		this.graph = graph;
		
		build();
	}

	private void build() throws IM3Exception {
		nodeMap = FXCollections.observableHashMap();
		edgeMap = FXCollections.observableHashMap();
		Collection<GraphNode<LabelNodeType, LabelEdgeType>> nodes = graph.getNodes();
		for (GraphNode<LabelNodeType, LabelEdgeType> node: nodes) {
			addNode(node);
		}
		
		for (GraphNode<LabelNodeType, LabelEdgeType> node: nodes) {
			GraphNodeViewModel<LabelNodeType, LabelEdgeType> from = nodeMap.get(node);
			if (from == null) {
				throw new IM3Exception("Cannot find source node");
			}
			
			if (from != null && node.getOutEdges() != null) {
				for (GraphEdge<LabelEdgeType> edge: node.getOutEdges()) {
					addEdge(from, edge);
				}
			}
		}	
		
		// observe changes
		graph.nodeMapProperty().addListener(new MapChangeListener<String, GraphNode<LabelNodeType, LabelEdgeType>>() {
			@Override
			public void onChanged(
					Change<? extends String, ? extends GraphNode<LabelNodeType, LabelEdgeType>> change) {
				if (change.wasRemoved()) {
					removeNode(change.getValueRemoved());
				} else if (change.wasAdded()) {
					try {
						addNode(change.getValueAdded());
					} catch (IM3Exception e) {
						throw new IM3RuntimeException("Cannot create node view model for " + change);
					}
				} else {
					throw new IM3RuntimeException("Neither added or removed !!");
				}
				
			}			
		});
		
		final int hashCode = this.hashCode();

		graph.edgesProperty().addListener(new ListChangeListener<GraphEdge<LabelEdgeType>>() {
			@Override
			public void onChanged(Change<? extends GraphEdge<LabelEdgeType>> c) {
				/// System.out.println("Changes: " + c);
				/// System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
				while (c.next()) {
					System.out.println("C: " + c);
					if (c.wasRemoved()) {
						for (GraphEdge<LabelEdgeType> e: c.getRemoved()) {
							removeEdge(e);
						}
					} else if (c.wasAdded()) {
						try {
							for (GraphEdge<LabelEdgeType> e: c.getAddedSubList()) {
								System.out.println("Adding edge: " + e + 
										" to graph VM: " + hashCode);
								addEdge(e);
								System.out.println("End adding edge");
							}
						} catch (IM3Exception e) {
							throw new IM3RuntimeException("Cannot create edge view model for " + c);
						}
					} else {
						throw new IM3RuntimeException("Neither added or removed edge !!");
					}					
				}
			}
			
		});
	}
	
	protected void addEdge(GraphEdge<LabelEdgeType> e) throws IM3Exception {
		GraphNodeViewModel<LabelNodeType, LabelEdgeType> fromVM = nodeMap.get(e.getSourceNode());
		if (fromVM == null) {
			throw new IM3RuntimeException("Node " + fromVM + " not found in view model");
		}
		addEdge(fromVM, e);		
	}

	protected void removeEdge(GraphEdge<LabelEdgeType> e) {
		if (edgeMap.remove(e) == null) {
			throw new IM3RuntimeException("Edge " + e + " not found in view model");			
		}		
	}

	private void addNode(GraphNode<LabelNodeType, LabelEdgeType> node) throws IM3Exception {
		GraphNodeViewModel<LabelNodeType, LabelEdgeType> nodeVM = new GraphNodeViewModel<>(this, node);
		renderer.render(nodeVM); // important to be located before the nodeMap put because it triggers the nodeMap change listeners and node must be rendered first
		nodeMap.put(node, nodeVM);
	}


	protected void removeNode(GraphNode<LabelNodeType, LabelEdgeType> graphNode) {
		if (nodeMap.remove(graphNode) == null) {
			throw new IM3RuntimeException("Node " + graphNode + " not found in view model");
		}
	}

	private GraphEdgeViewModel<LabelNodeType, LabelEdgeType> addEdge(GraphNodeViewModel<LabelNodeType, LabelEdgeType> from, GraphEdge<LabelEdgeType> edge) throws IM3Exception {
		/// System.out.println("PEPE FROM VM: " + from.hashCode() + ", " + from.getGraphNode().hashCode());
		/// System.out.println("### Edge > from " + edge.getSourceNode().hashCode() + " to " + edge.getTargetNode().hashCode());
				
		GraphNodeViewModel<LabelNodeType, LabelEdgeType> to = nodeMap.get(edge.getTargetNode());
		for (Map.Entry<GraphNode<LabelNodeType, LabelEdgeType>, GraphNodeViewModel<LabelNodeType, LabelEdgeType>> nm: nodeMap.entrySet()) {
			/// System.out.println("ooooo: " + nm.getKey().hashCode() + "\t=\t" + nm.getValue().hashCode());
		}
		if (to == null) {
			throw new IM3Exception("Cannot find target node");
		}

		GraphEdgeViewModel<LabelNodeType, LabelEdgeType> edgeVM = new GraphEdgeViewModel<>(edge, from, to);
		/// System.out.println("%%%%% " + from.hashCode() + " --- " + to.hashCode());
		from.addOutgoingEdge(edgeVM);
		renderer.render(edgeVM); // important to be located before the nodeMap put because it triggers the edgeMap change listeners and node must be rendered first
		edgeMap.put(edge, edgeVM);
		return edgeVM;
	}

	public Collection<GraphNodeViewModel<LabelNodeType, LabelEdgeType>> getNodes() {
		return nodeMap.values();
	}
	
	public ObservableMap<GraphNode<LabelNodeType, LabelEdgeType>, GraphNodeViewModel<LabelNodeType, LabelEdgeType>> nodeMapProperty() {
		return nodeMap;
	}

	
	public ObservableMap<GraphEdge<LabelEdgeType>, GraphEdgeViewModel<LabelNodeType, LabelEdgeType>> edgeMapProperty() {
		return edgeMap;
	}

	public GraphEdgeViewModel<LabelNodeType, LabelEdgeType> onEdgeAdded(GraphEdge<LabelEdgeType> edge) throws IM3Exception {
		GraphNodeViewModel<LabelNodeType, LabelEdgeType> from = nodeMap.get(edge.getSourceNode());
		if (from == null) {
			throw new IM3Exception("Cannot find source node");
		}
		
		return addEdge(from, edge);		
	}
}
