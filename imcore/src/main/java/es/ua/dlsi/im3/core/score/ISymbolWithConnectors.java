package es.ua.dlsi.im3.core.score;

import java.util.Collection;

public interface ISymbolWithConnectors extends IUniqueIDObject {

	Collection<Connector<?,?>> getConnectors();
	void addConnector(Connector<?,?> connector);
	boolean containsConnectorFrom(Class<? extends Connector<?,?>> connectorClass, ISymbolWithConnectors fromSymbol);
	boolean containsConnectorTo(Class<? extends Connector<?,?>> connectorClass, ISymbolWithConnectors fromSymbol);


}
