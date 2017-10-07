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

import java.awt.image.BufferedImage;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.Symbol;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 * @author drizo
 */
public class ScoreImageTagsView extends Group {
	ScoreImageTags scoreImageTags;
	ObservableList<SymbolView> symbolViews;
	SymbolView currentSymbolView;

	public ScoreImageTagsView(ScoreImageTags scoreImageTags) throws IM3Exception {
		this.scoreImageTags = scoreImageTags;
		symbolViews = FXCollections.observableArrayList();
		for (Symbol symbol : scoreImageTags.symbolsProperty()) {
			onSymbolAdded(symbol);
		}
		scoreImageTags.setChanged(false);

		scoreImageTags.symbolsProperty().addListener(new ListChangeListener<Symbol>() {
			@Override
			public void onChanged(Change<? extends Symbol> c) {
				// System.out.println(this.toString() + " " + c.toString());
				while (c.next()) { // use alwaus this scheme
					if (c.wasPermutated()) {
						for (int i = c.getFrom(); i < c.getTo(); ++i) {
							// permutate //TOO
						}
					} else if (c.wasUpdated()) {
						// update item //TODO
					} else if (c.wasRemoved()) {
						// TODO optimizarlo si va lenta esta operación n^2
						for (final Symbol symbol : c.getRemoved()) {
							symbolViews.removeIf(new Predicate<SymbolView>() {
								@Override
								public boolean test(SymbolView t) {
									return t.getSymbol() == symbol;
								}
							});

							getChildren().removeIf(new Predicate<Node>() {
								@Override
								public boolean test(Node t) {
									return (t instanceof SymbolView) && ((SymbolView) t).getSymbol() == symbol;
								}
							});
						}
					} else {
						for (Symbol symbol : c.getAddedSubList()) {
							try {
								onSymbolAdded(symbol);
							} catch (IM3Exception e) {
								Logger.getLogger(ScoreImageTagsView.class.getName()).log(Level.WARNING, "Cannot paint symbol {0}: {1}", new Object[]{symbol, e});
								throw new RuntimeException(e);
							}
						}
					}
				}
			}
		});
	}

	public Color getNextColor() {
		Color color;

		if (getChildren().size() % 2 == 0) {
			color = Color.BLUE;
		} else {
			color = Color.GREEN;
		}
		return color;
	}

	public final void onSymbolAdded(Symbol symbol) throws IM3Exception {
		if (currentSymbolView == null || symbol != currentSymbolView.getSymbol()) {
			Logger.getLogger(ScoreImageTagsView.class.getName()).log(Level.INFO, "Symbol {0} added, creating view",
					symbol);
			SymbolView sv = new SymbolView(symbol, getNextColor());
			symbolViews.add(sv);
			getChildren().add(sv);
		} // else done in newSymbolComplete
	}

	public final void addSymbol(Symbol symbol) {
		// delegate to model - it will trigger the listeners and onSymbolAdded
		// will be invoked
		scoreImageTags.addSymbol(symbol);
	}

	public void createNewSymbol(BufferedImage pageImage) throws IM3Exception {
		Symbol symbol = new Symbol(pageImage);
		currentSymbolView = new SymbolView(symbol, getNextColor());
		getChildren().add(currentSymbolView);
	}

	public Symbol newSymbolComplete() throws IM3Exception {
		currentSymbolView.finishSymbol();
		symbolViews.add(currentSymbolView);
		scoreImageTags.addSymbol(currentSymbolView.getSymbol());
		Symbol result = currentSymbolView.getSymbol();
		currentSymbolView = null;
		return result;
	}

	public void cancelNewSymbol() {
		getChildren().remove(currentSymbolView);
		currentSymbolView = null;
	}

	public ObservableList<SymbolView> symbolViewsProperty() {
		return symbolViews;
	}

	@Override
	public String toString() {
		return scoreImageTags.toString();
	}

	public String getName() {
		return scoreImageTags.getName();
	}

	public SymbolView getCurrentSymbolView() {
		return currentSymbolView;
	}

	public void removeSymbol(Symbol symbol) {
		this.scoreImageTags.removeSymbol(symbol);
		// the view is remove by the listener
		if (currentSymbolView != null && currentSymbolView.getSymbol() == symbol) {
			currentSymbolView = null;
		}
	}

	boolean isDrawingSymbol() {
		return currentSymbolView != null;
	}

	public ScoreImageTags getScoreImageTags() {
		return scoreImageTags;
	}

}
