/*
 * Copyright (C) 2014 David Rizo Valero
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

package es.ua.dlsi.im3.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//TODO ¿Esto debería estar en otro sitio junto a la font ttf?
/**
 *
 * @author drizo
 */
public class JSONGlyphNamesReader {
    private static final Object CODEPOINT = "codepoint";
    JSONParser parser;
	JSONObject jsonObject;
	private static final String UNICODE_PREFIX = "\\u";

	public JSONGlyphNamesReader(String internalFileName) throws ImportException {
		try {
			InputStream stream = this.getClass().getResourceAsStream(internalFileName);
			if (stream == null) {
				throw new IOException("Cannot locate '" + internalFileName + "'");
			}
            init(stream);
			jsonObject = (JSONObject) parser.parse(new InputStreamReader(stream));
		} catch (IOException | ParseException ex) {
			Logger.getLogger(JSONGlyphNamesReader.class.getName()).log(Level.SEVERE, null, ex);
			throw new ImportException(ex);
		}
	}

	public JSONGlyphNamesReader(InputStream inputStream) throws ImportException {
	    init(inputStream);
	}

    private void init(InputStream inputStream) throws ImportException {
        try {
            parser = new JSONParser();
            jsonObject = (JSONObject) parser.parse(new InputStreamReader(inputStream));
        } catch (IOException | ParseException ex) {
            Logger.getLogger(JSONGlyphNamesReader.class.getName()).log(Level.SEVERE, null, ex);
            throw new ImportException(ex);
        }

    }

    public JSONGlyphNamesReader(File file) throws ImportException {
		try {
			InputStream stream = new FileInputStream(file);
			if (stream == null) {
				throw new IOException("Cannot load '" + file.getAbsolutePath() + "'");
			}
			init(stream);
		} catch (IOException ex) {
			Logger.getLogger(JSONGlyphNamesReader.class.getName()).log(Level.SEVERE, null, ex);
			throw new ImportException(ex);
		}
	}

	/**
	 * 
	 * @param input
	 *            e.g. U+E06D
	 * @return \uE06D
	 */
	private static final String getJavaUnicodeString(String input) {
		/*
		 * StringBuilder sb = new StringBuilder(); sb.append(UNICODE_PREFIX);
		 * sb.append(input.substring(2)); return sb.toString();
		 */
		String cc2 = input.substring(2);
		return String.valueOf(Character.toChars(Integer.parseInt(cc2, 16)));
	}

	/**
	 * 
	 * @param glyphName
	 * @return Java Unicode string
	 */
	public String getCodepoint(String glyphName) throws IM3Exception {
		Object o = jsonObject.get(glyphName);
		if (o == null) {
			throw new IM3Exception("Cannot find glyph " + glyphName);
		}
		JSONObject jsono = (JSONObject) o;
		Object result = jsono.get("codepoint");
		if (result == null) {
			throw new IM3Exception("codepoint element not found inside " + o);
		}
		return getJavaUnicodeString((String) result);
	}
	
	/**
	 * It returns a table with all values from json file: key=codepoint ("U+E050"), value=glyphname ("gClef")
	 * @return
	 * @throws IM3Exception
	 */
	public HashMap<String, String> readCodepointToGlyphMap() throws IM3Exception {
		HashMap<String, String> result = new HashMap<>();
		Set<Map.Entry<?,?>> entrySet = jsonObject.entrySet();
		for (Map.Entry<?,?> entry: entrySet) {
		    if (entry.getValue() instanceof JSONObject) {
                JSONObject jsono = (JSONObject) entry.getValue();
                Object cp = jsono.get(CODEPOINT);
                if (cp != null) {
                    //TODO '{"octaveLineThickness":0.16,"dashedBarlineGapLength":0.25,"beamSpacing":0.25,"thickBarlineThickness":0.5,"beamThickness":0.5,"bracketThickness":0.5,"repeatBarlineDotSeparation":0.16,"repeatEndingLineThickness":0.16,"thinBarlineThickness":0.16,"stemThickness":0.12,"staffLineThickness":0.13,"tieMidpointThickness":0.22,"textEnclosureThickness":0.16,"tupletBracketThickness":0.16,"legerLineThickness":0.16,"dashedBarlineDashLength":0.5,"subBracketThickness":0.16,"arrowShaftThickness":0.16,"barlineSeparation":0.4,"slurEndpointThickness":0.1,"pedalLineThickness":0.16,"slurMidpointThickness":0.22,"hairpinThickness":0.16,"dashedBarlineThickness":0.16,"lyricLineThickness":0.16,"legerLineExtension":0.4,"tieEndpointThickness":0.1}'
                    String unicode = (String) cp;
                    result.put(unicode, entry.getKey().toString());
                }
            }
		}
		return result;
	}
}