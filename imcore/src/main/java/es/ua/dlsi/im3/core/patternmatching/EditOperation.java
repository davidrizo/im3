package es.ua.dlsi.im3.core.patternmatching;

/**
 *
 * @author drizo
 */
public class EditOperation {
    public static final int EMPTY = -1;
    int from;
    int to;
    double cost;
    //String additionalInformation;

    public EditOperation(int from, int to, double cost) {
        this.from = from;
        this.to = to;
        this.cost = cost;
    }

    /*public void setAdditionalInformation(String s) {
        this.additionalInformation = s;
    }*/

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EditOperation other = (EditOperation) obj;
        if (this.from != other.from) {
            return false;
        }
        if (this.to != other.to) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.from;
        hash = 53 * hash + this.to;
        return hash;
    }

    public Object clone() {
        return new EditOperation(from, to, cost);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('<');
        if (from != EMPTY) {
            sb.append(from);
        } else {
            sb.append('-');
        }
        sb.append(',');
        if (to != EMPTY) {
            sb.append(to);
        }else {
            sb.append('-');
        }
        sb.append('=');
        sb.append(cost);
        sb.append('>');
        return sb.toString();
    }
}