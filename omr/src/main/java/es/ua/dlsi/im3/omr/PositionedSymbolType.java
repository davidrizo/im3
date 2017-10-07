package es.ua.dlsi.im3.omr;

import es.ua.dlsi.im3.core.score.PositionInStaff;

public class PositionedSymbolType<SymbolType> {
	SymbolType symbol;
	PositionInStaff position;
	public PositionedSymbolType(SymbolType symbol, PositionInStaff position) {
		super();
		this.symbol = symbol;
		this.position = position;
	}
	public SymbolType getSymbol() {
		return symbol;
	}
	public void setSymbol(SymbolType symbol) {
		this.symbol = symbol;
	}
	public PositionInStaff getPosition() {
		return position;
	}
	public void setPosition(PositionInStaff position) {
		this.position = position;
	}
	
	
	
	
}
