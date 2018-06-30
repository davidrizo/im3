package es.ua.dlsi.im3.core.adt.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

import com.sun.java.swing.plaf.gtk.GTKConstants;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.IADT;
import es.ua.dlsi.im3.core.adt.IIndexed;
import es.ua.dlsi.im3.core.adt.IndexedMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class DirectedGraph<LabelNodeType extends INodeLabel, LabelEdgeType extends IEdgeLabel> implements IADT {
	GraphNode<LabelNodeType, LabelEdgeType> startNode;
    /**
     * Map from ID to node
     */
	ObservableMap<String, GraphNode<LabelNodeType, LabelEdgeType>> nodes;
	/**
	 * Edges are inserted from the nodes
	 */
	ObservableList<GraphEdge<LabelEdgeType>> edges;
	private static final String NODE_START_ID = "GNstart";
	
	public DirectedGraph(boolean addStartNode) {
		this.nodes = FXCollections.observableHashMap();
		this.edges = FXCollections.observableArrayList();
		startNode = new GraphNode<>(this, NODE_START_ID, null);
		if (addStartNode) {
            this.nodes.put(startNode.getID(), startNode);
        }
	}

    /**
     * Add start node
     */
    public DirectedGraph() {
	    this(true);
    }

	public GraphNode<LabelNodeType, LabelEdgeType> getStartNode() {
		return startNode;
	}

	public void addNode(GraphNode<LabelNodeType, LabelEdgeType> node) throws IM3Exception {
		if (nodes.containsKey(node.getID())) {
			throw new IM3Exception("There exists another node with ID " + node.getID());
		}
		nodes.put(node.getID(), node);
	}

	public Collection<GraphNode<LabelNodeType, LabelEdgeType>> getNodes() {
		return nodes.values();
	}
	
	public ObservableMap<String, GraphNode<LabelNodeType, LabelEdgeType>> nodeMapProperty() {
		return nodes;
	}

	public GraphNode<LabelNodeType, LabelEdgeType> getNode(String ID) throws IM3Exception {
		GraphNode<LabelNodeType, LabelEdgeType> result = nodes.get(ID);
		if (result == null) {
			throw new IM3Exception("Cannot find a node with ID='" + ID + "'");
		}
		return result;
	}

    public GraphNode<LabelNodeType, LabelEdgeType> getNodeOrNull(String ID) {
        GraphNode<LabelNodeType, LabelEdgeType> result = nodes.get(ID);
        return result;
    }

	/**
	 * Package visibility. Edges are inserted from the nodes
	 * @param edge
	 */
	void onEdgeAdded(GraphEdge<LabelEdgeType> edge) {
		//System.out.println("ON EDGE ADDED: " + edge);
		//TODO ¿por qué lo inserta dos veces?
		if (!edges.contains(edge)) {
			edges.add(edge);
		} else {			
			throw new IM3RuntimeException("Edge " + edge + " was already inserted");
		}
		//System.out.println("EDGE ADDED END");
	}

	/**
	 * Edges are removed from the nodes
	 * @param edge
	 */
	void onEdgeRemoved(GraphEdge<LabelEdgeType> edge) {
		edges.remove(edge);
	}

	public ObservableList<GraphEdge<LabelEdgeType>> edgesProperty() {
		return edges;
	}


    public ObservableList<GraphEdge<LabelEdgeType>> getEdges() {
        return edges;
    }


    public void writeDot(File file, boolean printEdgeLabels) throws FileNotFoundException, IM3Exception {
        PrintStream os = new PrintStream(new FileOutputStream(file));
        os.println("digraph dfa {");

        // first add all nodes
        for (Map.Entry<String, GraphNode<LabelNodeType, LabelEdgeType>> entry: nodes.entrySet()) {
            os.println("s" + entry.getKey() + "[label=\"" + entry.getValue().getLabel() + "\", shape=circle];");
        }

        // then edges
        for (Map.Entry<String, GraphNode<LabelNodeType, LabelEdgeType>> entry: nodes.entrySet()) {
            if (entry.getValue().getOutEdges() != null) {
                for (GraphEdge<LabelEdgeType> edge : entry.getValue().getOutEdges()) {
                    if (printEdgeLabels) {
                        os.println("s" + edge.getSourceNode().getID() + "->s" + edge.getTargetNode().getID() + "[label=\"" + edge.getLabel().toString() + "\"];");
                    } else {
                        os.println("s" + edge.getSourceNode().getID() + "->s" + edge.getTargetNode().getID() + ";");
                    }
                }
            }
        }

        os.println("}");
        if (os != null) {
            os.close();
        }
    }


}
