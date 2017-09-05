/*
 * Created on 22-ene-2004
 */
package es.ua.dlsi.im3.core.adt.tree;

import java.io.File;

//WISH Mejor exportar a un stream que a File
/**
 * @author david
 */
public interface ITreeExporter {
	/**
	 * Export a tree to an external file
	 * @param file Target file
	 * @param tree The tree to be exported
	 * @throws ImportException Exception importing file
	 */
	void exportTree(File file, ITree<?> tree) throws ExportTreeException; 
}
