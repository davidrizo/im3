package es.ua.dlsi.im3.core.score.layout.graphics;

public class BoundingBox {
    double leftEnd;
    double rightEnd;

    public BoundingBox(double leftEnd, double rightEnd) {
        this.leftEnd = leftEnd;
        this.rightEnd = rightEnd;
    }

    public double getLeftEnd() {
        return leftEnd;
    }

    public double getRightEnd() {
        return rightEnd;
    }

    public void setLeftEnd(double leftEnd) {
        this.leftEnd = leftEnd;
    }

    public void setRightEnd(double rightEnd) {
        this.rightEnd = rightEnd;
    }

    public double getWidth() {
        return rightEnd - leftEnd;
    }

    @Override
    public String toString() {
        return "[" + leftEnd + ", " + rightEnd + "]";
    }
}
