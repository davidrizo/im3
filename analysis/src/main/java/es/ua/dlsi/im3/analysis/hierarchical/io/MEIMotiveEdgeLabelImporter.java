package es.ua.dlsi.im3.analysis.hierarchical.io;


import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotive;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.io.ImportException;

/**
 * @deprecacted ¿Lo usamos?
 * @author drizo
 *
 */
public class MEIMotiveEdgeLabelImporter implements IMEIComponentImporter<Tree<AttributesLabel>, MelodicMotive> {

	@Override
	public MelodicMotive importFrom(Tree<AttributesLabel> source, MEIHierarchicalAnalysesModernImporter importer) {
		
		return null;
	}

}
