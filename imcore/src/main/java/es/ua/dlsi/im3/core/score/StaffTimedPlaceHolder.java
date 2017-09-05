package es.ua.dlsi.im3.core.score;

import java.util.Collection;


public class StaffTimedPlaceHolder implements ITimedElementInStaff, ITimedSymbolWithConnectors {
	Time time;
	private Staff staff;
	private String ID;
	ConnectorCollection connectorCollection;
	
	public StaffTimedPlaceHolder(Staff staff, Time time) {
		this.time = time;
		this.staff = staff;
	}

	@Override	
	public Time getTime() {
		return this.time;
	}
	
	@Override
	public final Staff getStaff() {
		return staff;
	}

	@Override
	public void setStaff(Staff staff) {
		this.staff = staff;
	}
	    

	@Override
	public String __getID() {
		return ID;
	}

	@Override
	public void __setID(String id) {
		this.ID = id;
		
	}

	@Override
	public String __getIDPrefix() {
		return "PH";
	}

	@Override
	public Collection<Connector<?, ?>> getConnectors() {
		if (connectorCollection == null) {
			return null;
		} else {
			return connectorCollection.getConnectors();
		}		
	}

	@Override
	public void addConnector(Connector<?, ?> connector) {
		if (connectorCollection == null) {
			connectorCollection = new ConnectorCollection();
		}
		connectorCollection.add(connector);
	}

	@Override
	public boolean containsConnectorFrom(Class<? extends Connector<?, ?>> connectorClass,
			ISymbolWithConnectors fromSymbol) {
		if (connectorCollection == null) {
			return false;
		} else {
			return connectorCollection.containsConnectorFrom(connectorClass, fromSymbol);
		}
		
	}

	@Override
	public boolean containsConnectorTo(Class<? extends Connector<?, ?>> connectorClass,
			ISymbolWithConnectors fromSymbol) {
		if (connectorCollection == null) {
			return false;
		} else {
			return connectorCollection.containsConnectorTo(connectorClass, fromSymbol);
		}		
	}
}
