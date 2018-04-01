package es.ua.dlsi.im3.analysis.hierarchical.motives;

import es.ua.dlsi.im3.core.adt.graph.GraphNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Motive {
	GraphNode<? extends MotivesNodeLabel, MotivesEdgeLabel> containedInNode;
	StringProperty name;
	StringProperty description;
	/**
	 * e.g. red should be represented as FF0000
	 */
	StringProperty hexaColor;
	
	public Motive() {
		name = new SimpleStringProperty();
		description = new SimpleStringProperty();
		hexaColor = new SimpleStringProperty();
	}

	public GraphNode<? extends MotivesNodeLabel, MotivesEdgeLabel> getContainedInNode() {
		return containedInNode;
	}

	/**
	 * Package visibility
	 * @param containedInNode
	 */
	void setContainedInNode(GraphNode<? extends MotivesNodeLabel, MotivesEdgeLabel> containedInNode) {
		this.containedInNode = containedInNode;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	
	public String getHexaColor() {
		return hexaColor.get();
	}

	public void setHexaColor(String hexaColor) {
		this.hexaColor.set(hexaColor);
	}

	public StringProperty nameProperty() {
		return name;
	}
	public StringProperty descriptionProperty() {
		return description;
	}
	
	public StringProperty hexaColorProperty() {
		return hexaColor;
	}
	
	
}
