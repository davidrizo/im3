package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.HashSet;

/**
 *
 * @author drizo
 */
public class Arc extends Shape {
    double fromX;
    double fromY;

    double toX;
    double toY;
    
    double middlePointX;
    double middlePointY;

    double middleInnerPointX;
    double middleInnerPointY;
    
    double thicknessAtCenter;

    public Arc() {
    }

    public double getFromX() {
	return fromX;
    }

    public double getFromY() {
	return fromY;
    }

    public double getToX() {
	return toX;
    }

    public double getToY() {
	return toY;
    }

    public double getMiddlePointX() {
	return middlePointX;
    }

    public double getMiddlePointY() {
	return middlePointY;
    }

    public void setFromX(double fromX) {
	this.fromX = fromX;
    }

    public void setFromY(double fromY) {
	this.fromY = fromY;
    }

    public void setToX(double toX) {
	this.toX = toX;
    }

    public void setToY(double toY) {
	this.toY = toY;
    }

    public void setMiddlePointX(double middlePointX) {
	this.middlePointX = middlePointX;
    }

    public void setMiddlePointY(double middlePointY) {
	this.middlePointY = middlePointY;
    }

    public double getThicknessAtCenter() {
	return thicknessAtCenter;
    }

    public void setThicknessAtCenter(double thicknessAtCenter) {
	this.thicknessAtCenter = thicknessAtCenter;
    }

    public void computeShapeLayout() {	
    }

    public double getMiddleInnerPointX() {
	return middleInnerPointX;
    }

    public void setMiddleInnerPointX(double middleInnerPointX) {
	this.middleInnerPointX = middleInnerPointX;
    }

    public double getMiddleInnerPointY() {
	return middleInnerPointY;
    }

    public void setMiddleInnerPointY(double middleInnerPointY) {
	this.middleInnerPointY = middleInnerPointY;
    }



    @Override
    public void generateSVG(StringBuilder sb, int tabs, LayoutFont layoutFont, HashSet<Glyph> usedGlyphs) throws ExportException {

    }

    @Override
    public void generatePDF(PDPageContentStream contents, LayoutFont layoutFont, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException {

    }
}
