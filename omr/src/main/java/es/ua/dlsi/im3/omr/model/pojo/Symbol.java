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

    double y;
    double height;

    public Symbol(GraphicalToken graphicalToken, double x, double y, double width, double height) {
        this.graphicalToken = graphicalToken;
        this.accepted = false;
        this.x = x;
        this.width = width;
        this.height = height;
        this.y = y;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public GraphicalToken getGraphicalToken() {
        return graphicalToken;
    }

    public void setGraphicalToken(GraphicalToken graphicalToken) {
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

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
