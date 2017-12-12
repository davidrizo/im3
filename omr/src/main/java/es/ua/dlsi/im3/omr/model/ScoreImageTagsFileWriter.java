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
package es.ua.dlsi.im3.omr.model;

import es.ua.dlsi.im3.omr.old.mensuraltagger.components.ScoreImageTags;
import es.ua.dlsi.im3.omr.traced.Point;
import es.ua.dlsi.im3.omr.traced.Stroke;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO Cambiar de paquete
/**
 *
 * @author drizo
 */
public class ScoreImageTagsFileWriter {
	static enum State {
		eReadingSymbol, eReadingStrokes
	};

	public void write(ScoreImageTags sit, File file) throws FileNotFoundException {
		Logger.getLogger(ScoreImageTagsFileWriter.class.getName()).log(Level.INFO, "Writing in {0}",
				file.getAbsolutePath());
		try (PrintStream ps = new PrintStream(file)) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			DecimalFormat df = (DecimalFormat)nf;
			DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
		    dfs.setDecimalSeparator('.');
		    df.applyPattern("#.#");
		    df.setDecimalFormatSymbols(dfs);		    
			for (Symbol symbol : sit.symbolsProperty()) {
				if (symbol.getPositionedSymbolType() != null) {
					ps.print(symbol.getStrokes().size());
					ps.print(';');
					ps.print(symbol.getPositionedSymbolType().getSymbol().toString());
					ps.print(';');
					ps.println(symbol.getPositionedSymbolType().getPosition().toString());
				} else {
					ps.println(symbol.getStrokes().size());
				}
				List<Stroke> strokeList = symbol.getStrokes();
				for (Stroke s : strokeList) {
					for (Point p : s.pointsProperty()) {
						ps.print(p.getRelativeTime());
						ps.print(',');
						ps.print(df.format(p.getX()));
						//System.out.println(df.format(p.getX()));
						ps.print(',');
						ps.print(df.format(p.getY()));
						ps.print(';');
					}
					ps.println();
				}

				ps.println();
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ScoreImageTagsFileWriter.class.getName()).log(Level.SEVERE, null, ex);
			throw ex;
		}

	}
}
