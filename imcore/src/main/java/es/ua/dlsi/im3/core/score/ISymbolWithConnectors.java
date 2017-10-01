package es.ua.dlsi.im3.core.score;

import java.util.Collection;

public interface ISymbolWithConnectors extends IUniqueIDObject {

	Collection<Connector> getConnectors();
	void addConnector(Connector connector);
	boolean containsConnectorFrom(ISymbolWithConnectors fromSymbol);
	boolean containsConnectorTo(ISymbolWithConnectors fromSymbol);


}
