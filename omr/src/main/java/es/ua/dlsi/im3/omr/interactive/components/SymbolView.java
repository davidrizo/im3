/*
 * Copyright (C) 2016 David Rizo Valero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ua.dlsi.im3.omr.interactive.components;

import java.util.ArrayList;
import java.util.List;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.omr.PositionedSymbolType;
import es.ua.dlsi.im3.omr.mensuralspanish.MensuralSymbols;
import es.ua.dlsi.im3.omr.model.Symbol;
import es.ua.dlsi.im3.omr.model.SymbolType;
import es.ua.dlsi.im3.omr.traced.Stroke;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * The painting of the symbol in a list view is not made here but in
 * SymbolListCell
 * 
 * @author drizo
 */
public class SymbolView extends Group {
	private static final double DEFAULT_WIDTH = 2;
	private static final double SELECTED_WIDTH = 5;
	
	ObjectProperty<Symbol> symbol;
	StrokeView currentStrokeView;
	private final ObjectProperty<Color> color;
	private final Color unselectedColor;
	/**
	 * Used to change the color when editing
	 */
	private final ObjectProperty<Color> musicalContentColor;
	Text notationSymbolView;
	Group notationSymbolViewInStaff;
	ImageView imageView;
	
	/**
	 * Whether the user has accepted it
	 */
	private final ObjectProperty<Boolean> checked;
	/**
	 * Used for showing the group in a listview, we cannot set the same group in
	 * two parents
	 */
	private final Group miniatureGroup;
	private boolean editing;
	
	/**
	 * To allow undo and cancel
	 */
	private PositionInStaff currentPosition;
	private DoubleProperty strokeWidth;

	public SymbolView(Symbol symbol, Color color) throws IM3Exception {
		musicalContentColor = new SimpleObjectProperty<>(Color.BLACK);
		miniatureGroup = new Group();
		editing = false;
		this.color = new SimpleObjectProperty<>(color);
		this.strokeWidth = new SimpleDoubleProperty(DEFAULT_WIDTH);
		this.checked = new SimpleObjectProperty<>(false);
		unselectedColor = color;
		this.symbol = new SimpleObjectProperty<>(symbol);
		currentStrokeView = null;
		for (Stroke stroke : symbol.getStrokes()) {
			addStroke(stroke);
		}

		createImageView();
		drawNotationSymbolView();
		createInteraction();
	}

	private void createInteraction() {
		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				color.set(Color.GREENYELLOW);
				
			}
		});
		this.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				color.set(unselectedColor);				
			}
		});
		
	}

	public ObjectProperty<Symbol> symbolProperty() {
		return symbol;
	}

	public ObjectProperty<Boolean> checkedProperty() {
		return checked;
	}

	public void addNewStroke() {
		Stroke stroke = new Stroke();
		symbol.get().addStroke(stroke);
		addStroke(stroke);
	}

	/*
	 * public void addNewPoint(double x, double y) throws Exception { if
	 * (currentStrokeView == null) { throw new Exception("No active stroke"); }
	 * if (lastX != -1) { currentStrokeView.getElements().add(new MoveTo(lastX,
	 * lastY)); currentStrokeView.getElements().add(new LineTo(x, y)); } lastX =
	 * x; lastY = y; }
	 */

	public final void addStroke(Stroke stroke) {
		currentStrokeView = createStrokeView(stroke);
		StrokeView miniaturePath = createStrokeView(stroke);
		getChildren().add(currentStrokeView);
		miniatureGroup.getChildren().add(miniaturePath);
	}

	private StrokeView createStrokeView(Stroke stroke) {
		StrokeView result = new StrokeView(stroke);

		/*
		 * result.setOnMouseEntered(new EventHandler<MouseEvent>() {
		 * 
		 * @Override public void handle(MouseEvent event) { Path thispath =
		 * (Path) event.getTarget(); thispath.setStroke(Color.BLUE); } });
		 * 
		 * result.setOnMouseExited(new EventHandler<MouseEvent>() {
		 * 
		 * @Override public void handle(MouseEvent event) { Path thispath =
		 * (Path) event.getTarget(); thispath.setStroke(color); } });
		 */

		result.strokeWidthProperty().bind(strokeWidth);
		result.strokeProperty().bind(color);

		return result;
	}

	void select() {
		color.setValue(Color.RED);
		strokeWidth.set(SELECTED_WIDTH);
	}

	void unselect() {
		strokeWidth.set(DEFAULT_WIDTH);
		color.setValue(unselectedColor);
	}

	public Group getMiniatureGroup() {
		return miniatureGroup;
	}

	public Symbol getSymbol() {
		return this.symbol.get();
	}
	
	private void drawNotationSymbolView() throws IM3Exception {
        // TODO: 7/10/17 Usando sólo mensural
        PositionedSymbolType<MensuralSymbols> pst = this.symbol.get().getPositionedSymbolType();
		if (pst != null) {
            System.err.println("TODO!!!!!!!!!!!!!!!!! - DRAW NOTATION SYMBOL VIEW");
            /*notationSymbolView = NotationSymbolRenderer.getInstance().render(pst.getSymbol(), false);
			notationSymbolView.fillProperty().bind(musicalContentColor);
			currentPosition = pst.getPosition();
			drawInStaff();*/
            System.err.println("TODO!!!!!!!!!!!!!!!!! - DRAW NOTATION SYMBOL VIEW");
		}
	}
	
	public void setPositionedSymbolType(PositionedSymbolType pst) throws IM3Exception {
		this.symbol.get().setPositionedSymbolType(pst);
		drawNotationSymbolView();
	}
	
	public void setSymbolType(SymbolType st) throws IM3Exception {
		if (this.symbol.isNull().get()) {
			setPositionedSymbolType(new PositionedSymbolType(st, PositionsInStaff.LINE_3));
		} else {
			setPositionedSymbolType(new PositionedSymbolType(st, this.symbol.get().getPositionInStaff()));
		}		
	}

	// see Pentagram.computeYPositionForLinespace for more information
	private static final double spaceHeight = 0.25;
	private void drawInStaff() throws IM3Exception {
		notationSymbolViewInStaff = new Group();
		double w = notationSymbolView.getFont().getSize(); // cuadrado
		double marginLeft = w * 0.25; // empirical
		double marginLeftLedgerLines = w * 0.15; // empirical
		double width = w * 1.5; // 1 + 0.25 + 0.25
		Line [] lines = new Line[5]; //TODO ledger lines en pequeño y gris...
		
		for (int i=0; i<5; i++) {
			double y = i*notationSymbolView.getFont().getSize() * spaceHeight;
			Line line = new Line(0, y, width, y);
			line.setStrokeWidth(1);
			line.strokeProperty().bind(musicalContentColor);
			lines[i] = line;
			notationSymbolViewInStaff.getChildren().add(line);
		}
		
		// set ledger lines below
		for (int i=1; i<=3; i++) {
			double y = lines[0].getEndY() - i*notationSymbolView.getFont().getSize() * spaceHeight;
			Line line = new Line(marginLeftLedgerLines, y, width-marginLeftLedgerLines, y);
			line.setStrokeWidth(1);
			line.setStroke(Color.LIGHTGRAY);
			line.getStrokeDashArray().addAll(4d);
			//lines[i] = line;
			notationSymbolViewInStaff.getChildren().add(line);			
		}

		// set ledger lines below
		for (int i=1; i<=3; i++) {
			double y = lines[4].getEndY() + i*notationSymbolView.getFont().getSize() * spaceHeight;
			Line line = new Line(marginLeftLedgerLines, y, width-marginLeftLedgerLines, y);
			line.setStrokeWidth(1);
			line.setStroke(Color.LIGHTGRAY);
			line.getStrokeDashArray().addAll(4d);
			//lines[i] = line;
			notationSymbolViewInStaff.getChildren().add(line);			
		}
		
		notationSymbolView.setLayoutX(marginLeft);
		repositionVertically();
		notationSymbolViewInStaff.getChildren().add(notationSymbolView);
	}
	
	private void repositionVertically()  {
        // TODO: 7/10/17 Esto sólo va con pentagramas que tiene 8 líneas
        notationSymbolView.setLayoutY((8-currentPosition.getLineSpace()) * notationSymbolView.getFont().getSize() * spaceHeight / 2.0);
	}

	public Group getNotationSymbolViewInStaff() {
		return notationSymbolViewInStaff;
	}
	
	public Text getNotationSymbolView() {
		return notationSymbolView;
	}

	public StrokeView getCurrentStrokeView() {
		return currentStrokeView;
	}

	void setSortedPossibleNotationSymbols(ArrayList<PositionedSymbolType> notationSymbols) {
		this.symbol.get().setSortedPossibleNotationSymbols(notationSymbols);
	}

	public List<Text> getSortedPossibleNotationSymbols() throws IM3Exception {
		ArrayList<Text> result = new ArrayList<>();
		for (PositionedSymbolType<MensuralSymbols> ns : this.symbol.get().getSortedPossibleNotationSymbols()) {
			try {
			    MensuralSymbols symbol = ns.getSymbol();
				Text graphic = NotationSymbolRenderer.getInstance().render(symbol, true);
				graphic.setUserData(ns.getSymbol());
				result.add(graphic);
			} catch (Exception e) {
				System.err.println(e.toString()); //TODO
			}
		}
		return result;
	}

	public void check() {
		this.checked.set(true);
		if (this.editing) {
			symbol.get().setPositionInStaff(currentPosition);
			musicalContentColor.set(Color.BLACK);
			cancelEdit();
		}
	}

	boolean isChecked() {
		return this.checked.get();
	}

	public ImageView getImageView() {
		return imageView;
	}

	private void createImageView() {
		if (symbol.get().getSymbolImage() != null) {
			WritableImage fxImage = new WritableImage((int) symbol.get().getWidth(), (int) symbol.get().getHeight());
			imageView = new ImageView(SwingFXUtils.toFXImage(symbol.get().getSymbolImage(), fxImage));
		}
		
		
	}

	public void finishSymbol() throws IM3Exception {
		symbol.get().finishSymbol();
		createImageView();
	}

	public boolean isEditing() {
		return editing;
	}

	public void startEdit() {
		this.editing = true;		
		musicalContentColor.set(Color.RED);
		if (symbol.get().getPositionedSymbolType() != null) {
			currentPosition = symbol.get().getPositionInStaff();
		}
	}

	public void cancelEdit()  {
		this.editing = false; 
		currentPosition = symbol.get().getPositionInStaff();
		musicalContentColor.set(Color.BLACK);
		repositionVertically();
	}
	
	public void changePosition(RepositionDirection direction) throws IM3Exception {
		if (direction == RepositionDirection.DOWN) {
			currentPosition = currentPosition.getPositionBelow();
		} else {
			currentPosition = currentPosition.getPositionAbove();
		}
		repositionVertically();
	}
}
