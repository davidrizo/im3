package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotive;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.SingleFigureAtom;

import java.util.ArrayList;


public class MEIMelodicMotiveImporter implements IMEIComponentImporter<Tree<AttributesLabel>, MelodicMotive> {

	@Override
	public MelodicMotive importFrom(Tree<AttributesLabel> source, MEIHierarchicalAnalysesModernImporter importer) throws ImportException {
		ArrayList<AtomFigure> v = new ArrayList<>();
		String description = null;
		for (Tree<AttributesLabel> child: source.getChildren()) {
			//TODO habrán luego otra formas de referenciar notas, ahora sólo uso sameas
			if (child.getLabel().getTag().equals("note") 
					|| child.getLabel().getTag().equals("chord")
					|| child.getLabel().getTag().equals("rest")) {
				String sameas = child.getLabel().getAttribute("sameas");
				Object coreSymbolObj = importer.findXMLID(sameas);

				//TODO ahora estamos referenciando SingleFigureAtom sólo - faltan cabezas de nota....
				if (!(coreSymbolObj instanceof SingleFigureAtom)) {
				    throw new ImportException("Object with an ID '" + sameas + "' is not an AtomFigure, it is a " + coreSymbolObj.getClass());
                }
                AtomFigure coreSymbol = ((SingleFigureAtom) coreSymbolObj).getAtomFigure();
				//AMTimedElement timedElement = importer.findXMLID(sameas);
				//LayeredCoreSymbol coreSymbol = abstractModel2ModernSong.findCoreSymbol(timedElement);
				v.add(coreSymbol);
				// child.getLabel().getTag()
			} else if (child.getLabel().getTag().equals("description")) {
				if (description != null) {
					throw new ImportException("Several descriptions for melodic node label");
				}
				description = child.getLabel().getTextContent();
			} else {
				throw new ImportException("Unknown tag for melodic node label: '" + child.getLabel().getTag() + "'");
			}
		}
		String name = source.getLabel().getOptionalAttribute("name");
		String color = source.getLabel().getOptionalAttribute("color");
		MelodicMotive mm = new MelodicMotive();		
		mm.setName(name);
		mm.setDescription(description);
		mm.setAtomFigures(v);
		mm.setHexaColor(color);
		
		return mm;
	}

}
