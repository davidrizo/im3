package es.ua.dlsi.im3.omr.model.pojo;

public class Symbol {
    /**
     * Whether it has been accepted by the user
     */
    private boolean accepted;

    GraphicalToken graphicalToken;

    /**
     * Absolute to the image
     */
    double x;
    double width;

    public Symbol(GraphicalToken graphicalToken, double x, double width) {
        this.graphicalToken = graphicalToken;
        this.accepted = false;
        this.x = x;
        this.width = width;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public GraphicalToken getGraphicalSymbol() {
        return graphicalToken;
    }

    public void setGraphicalSymbol(GraphicalToken graphicalToken) {
        this.graphicalToken = graphicalToken;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }
}
