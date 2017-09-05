package es.ua.dlsi.im3.core.score.io.paec;

import es.ua.dlsi.im3.core.io.ExportException;

import java.util.HashMap;

/**
 * It contains both the exported string and a mapping between each exported symbol and its possition in the string
 * @author drizo
 */
public class PaecStringExport {

    public class Position {
	int start;
	int duration;

	public Position(int start, int duration) {
	    this.start = start;
	    this.duration = duration;
	}

	public int getStart() {
	    return start;
	}

	public int getDuration() {
	    return duration;
	}

	public int getEnd() {
	    return start + duration;
	}
	
	
    }
    private StringBuffer buffer;
    
    /**
     * Key = Represented object
     * Value = pair start - figureAndDots
     */
    private HashMap<Object, Position> positions;  
    
    
    public PaecStringExport() {
	buffer = new StringBuffer();
	positions = new HashMap<>();
    }
    
    public void append(Object symbol, String str) throws ExportException {
	int s = buffer.length();
	buffer.append(str);
	register(symbol, s, str.length());
    }
    
    public void append(Object symbol, StringBuilder sb) throws ExportException {
	int s = buffer.length();
	buffer.append(sb);
	register(symbol, s, sb.length());
    }

    public void append(char c)  {
	buffer.append(c);
    }

    public void append(int i)  {
	buffer.append(i);
    }
    
    public void append(String s)  {
	buffer.append(s);
    }
    
    /**
     * @param o
     * @return null if not found
     */
    public Position getPosition(Object o) {
	return positions.get(o);
	
    }
 
    @Override
    public String toString() {
	return buffer.toString();
    }
    
    private void register(Object symbol, int s, int length) throws ExportException {
	Position p = positions.get(symbol);
	if (p == null) {
	    positions.put(symbol, new Position(s, length));
	} else {
	    throw new ExportException("The symbol " + symbol.toString() + " has been already registered");
	}
    }
    
    
}
