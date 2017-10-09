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


import es.ua.dlsi.im3.core.IM3Exception;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author drizo
 */
public class NotationSymbolRenderer<SymbolType> {
	static NotationSymbolRenderer instance;

	//private final Font musicalFont;
	//private final Font textFont;

	//private final FontAndMapping mappingFactory;

	private NotationSymbolRenderer() {
		//mappingFactory = FontMappingFactory.getInstance().getSpanishMensural();
        //musicalFont = Font.loadFont(mappingFactory.getMusicalFontStream(), 48);
        //textFont = Font.loadFont(mappingFactory.getLyricsFontStream(), 48);
	}

	public Text render(SymbolType symbolType, boolean useStaffMappingInRests) throws IM3Exception {
		Text result;
		/*IFontMapping mapping = mappingFactory.getFontMapping();
		Font font = null;
		switch (symbolType) {
			case barline:
				result = new Text(mapping.findMapping(Barline.class));
				break;
			case beam:
				result = new Text(mapping.findBeam());
				//font = textFont;
				break;
			case brevis:
				result = new Text(mapping.findCompleteFigure(FiguresMensural.BREVE_WHITE));
				break;
			case brevis_rest:
				if (useStaffMappingInRests) {
					result = new Text(mapping.findRestWithStaffMapping(FiguresMensural.BREVE_WHITE));
				} else {
					result = new Text(mapping.findRestMapping(FiguresMensural.BREVE_WHITE));
				}
				break;
			case c_clef:
				result = new Text(mapping.findMapping(ClefC1.class));
				break;
			case coloured_brevis:
				result = new Text(mapping.findCompleteFigure(FiguresMensural.BREVE_BLACK));
				break;
			case coloured_minima:
				result = new Text(mapping.findCompleteFigure(FiguresMensural.MINIMA_BLACK));
				break;
			case coloured_semibrevis:
				result = new Text(mapping.findCompleteFigure(FiguresMensural.SEMIBREVE_BLACK));
				break;
			case coloured_semiminima:
				result = new Text(mapping.findCompleteFigure(FiguresMensural.SEMINIMA_BLACK));
				break;
			case common_time:
				result = new Text(mapping.findMapping(TimeSignatureCommonTime.class));
				break;
			case custos:
				result = new Text(mapping.findMapping(Custos.class));
				break;
			case cut_time:
				result = new Text(mapping.findMapping(TimeSignatureCutTime.class));
				break;
			case dot:
				result = new Text(mapping.findMapping(AugmentationDot.class));
				break;
			case double_barline:
				result = new Text(mapping.findMapping(DoubleBarline.class));
				break;
			case f_clef_1:
				result = new Text(mapping.findMapping(ClefF4.class));
				break;
			case f_clef_2:
				result = new Text(mapping.findMapping(ClefF4CompoundGlyph.class));
				break;
			case fermata:
				result = new Text(mapping.findFermataMapping(PositionAboveBelow.ABOVE));
				break;
			case flat:
				result = new Text(mapping.findMapping(Accidentals.FLAT));
				break;
			case g_clef:
				result = new Text(mapping.findMapping(ClefG2.class));
				break;
			case longa:
				result = new Text(mapping.findCompleteFigure(FiguresMensural.LONGA_WHITE));
				break;
			case longa_rest:
				if (useStaffMappingInRests) {
					result = new Text(mapping.findRestWithStaffMapping(FiguresMensural.LONGA_WHITE));
				} else {
					result = new Text(mapping.findRestMapping(FiguresMensural.LONGA_WHITE));
				}
				break;
			case minima:
				result = new Text(mapping.findCompleteFigure(FiguresMensural.MINIMA_WHITE));
				break;
			case minima_rest:
				result = new Text(mapping.findRestWithStaffMapping(FiguresMensural.MINIMA_WHITE));
				break;
			case proportio_maior:
				result = new Text(mapping.findMapping(TimeSignatureProporcionMayor.class));
				break;
			case proportio_minor:
				result = new Text(mapping.findMapping(TimeSignatureProporcionMenor.class));
				break;
			case semibrevis:
				result = new Text(mapping.findCompleteFigure(FiguresMensural.SEMIBREVE_WHITE));
				break;
			case semibrevis_rest:
				if (useStaffMappingInRests) {
					result = new Text(mapping.findRestWithStaffMapping(FiguresMensural.SEMIBREVE_WHITE));
				} else {
					result = new Text(mapping.findRestMapping(FiguresMensural.SEMIBREVE_WHITE));
				}
				break;
			case semiminima:
				result = new Text(mapping.findCompleteFigure(FiguresMensural.SEMINIMA_WHITE));
				break;
			case semiminima_rest:
				result = new Text(mapping.findRestWithStaffMapping(FiguresMensural.SEMINIMA_WHITE));
				break;
			case sharp:
				result = new Text(mapping.findMapping(Accidentals.SHARP));
				break;
			case ligature:
				result = new Text("Ligature");
				font = textFont;
				break;
			case undefined:
				result = new Text("Undefined");
				font = textFont;
				break;
			default:
				throw new IM2Exception("Unsupported symbol: " + symbolType);
		}

		if (font == null) {
			font = musicalFont;
		}
		result.setFont(font);
		return result;*/

		result = new Text("---TO-DO----"); // FIXME: 9/10/17
        return result;
    }

	public static synchronized NotationSymbolRenderer getInstance() {
		if (instance == null) {
			instance = new NotationSymbolRenderer();
		}
		return instance;
	}
}
