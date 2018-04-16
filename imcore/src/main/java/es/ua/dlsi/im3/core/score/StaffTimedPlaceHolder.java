package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

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
    public void move(Time offset) throws IM3Exception {
        this.time = time.add(offset);
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
	public Collection<Connector> getConnectors() {
		if (connectorCollection == null) {
			return null;
		} else {
			return connectorCollection.getConnectors();
		}		
	}

	@Override
	public void addConnector(Connector connector) {
		if (connectorCollection == null) {
			connectorCollection = new ConnectorCollection();
		}
		connectorCollection.add(connector);
	}

	@Override
	public boolean containsConnectorFrom(ISymbolWithConnectors fromSymbol) {
		if (connectorCollection == null) {
			return false;
		} else {
			return connectorCollection.containsConnectorFrom(fromSymbol);
		}
		
	}

	@Override
	public boolean containsConnectorTo(ISymbolWithConnectors fromSymbol) {
		if (connectorCollection == null) {
			return false;
		} else {
			return connectorCollection.containsConnectorTo(fromSymbol);
		}		
	}

}
