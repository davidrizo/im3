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

import java.io.File;
import java.io.FileNotFoundException;

import es.ua.dlsi.im3.omr.model.ScoreImageTagsFileWriter;
import es.ua.dlsi.im3.omr.model.Symbol;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author drizo
 */
public class ScoreImageTags {

    File file;
    ObservableList<Symbol> symbols;
    BooleanProperty changed;

    public ScoreImageTags(File file) {
	this.file = file;
	symbols = FXCollections.observableArrayList();
	changed = new SimpleBooleanProperty(false);
    }

    public void addSymbol(Symbol symbol) {
	symbols.add(symbol);
	changed.set(true);
    }

    void removeSymbol(Symbol symbol) {
	symbols.remove(symbol);
	changed.set(true);
    }

    public ObservableList<Symbol> symbolsProperty() {
	return symbols;
    }

    @Override
    public String toString() {
	return file.getName();
    }

    public String getName() {
	return file.getName();
    }
    
    public BooleanProperty changedProperty() {
	return changed;
    }
    
    public void save() throws FileNotFoundException {
        ScoreImageTagsFileWriter writer = new ScoreImageTagsFileWriter();
        writer.write(this, file);
        changed.set(false);
    }
    
    public void setChanged(boolean b) {
	changed.set(b);
    }

    public int getNumTags() {
        return symbols.size();
    }
}
