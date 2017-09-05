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
package es.ua.dlsi.im3.core.adt.tree;

import es.ua.dlsi.im3.core.io.ImportException;

/**
 * Tree that contains labels of type string
 * @author drizo
 *
 */
public class StringTree extends Tree<StringLabel> {

	private static final String PARTENTHESIS_START = "(";
	public StringTree(StringLabel label) {
		super(label);
	}
	/**
	 * Parses functional strings, see Tree.toFunctionalString
	 * @param inputStr
	 * @return
	 * @throws ImportException 
	 * @throws TreeException 
	 */
	public static StringTree parseString(String inputStr) throws ImportException, TreeException {
		String input = inputStr.trim();
		if (input.trim().isEmpty()) {
			return null;
		} else {
			int parenthesisStartIndex = input.indexOf(PARTENTHESIS_START);
			int parenthesisEndIndex = input.lastIndexOf(')');
			
			if (parenthesisStartIndex != -1) {
				if (parenthesisEndIndex == -1) {
					throw new ImportException("Unbalanced parenthesis in input '" + input + "', missing )");
				}
				String rootLabel = input.substring(0, parenthesisStartIndex);
				StringTree root = new StringTree(new StringLabel(rootLabel));
				
				String childrenStr = input.substring(parenthesisStartIndex+1, parenthesisEndIndex);
				String [] cs = childrenStr.split(",");
				if (cs != null && cs.length>0) {
					for (String string : cs) {
						StringTree child = parseString(string);
						root.addChild(child);
					}
				}
				return root;
				
			} else {
				// this is a leaf
				if (parenthesisEndIndex != -1) {
					throw new ImportException("Unbalanced parenthesis in input '" + input + "', not expected )");
				}
				StringTree root = new StringTree(new StringLabel(input));
				return root;
			}
		}
	}
}
