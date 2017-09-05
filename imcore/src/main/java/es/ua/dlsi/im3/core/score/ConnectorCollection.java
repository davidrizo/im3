package es.ua.dlsi.im3.core.score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConnectorCollection {
	List<Connector<?,?>> connectors;

	public ConnectorCollection() {
		connectors = new ArrayList<>();
	}

	public Collection<Connector<?, ?>> getConnectors() {
		return connectors;
	}

	public void add(Connector<?, ?> connector) {
		connectors.add(connector);		
	}

	public boolean containsConnectorTo(Class<?> claz, ISymbolWithConnectors toSymbol) {
		for (Connector<?,?> c: this.connectors) {
			if (c.getTo() == toSymbol && c.getClass() == claz) {
				return true;
			}
		}
		return false;
	}

	public boolean containsConnectorFrom(Class<?> claz, ISymbolWithConnectors fromSymbol) {
		for (Connector<?,?> c: this.connectors) {
			if (c.getFrom() == fromSymbol && c.getClass() == claz) {
				return true;
			}
		}
		return false;
	}
	
}
