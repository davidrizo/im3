package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotive;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.SimpleChord;
import es.ua.dlsi.im3.core.score.SimpleNote;
import es.ua.dlsi.im3.core.score.SimpleRest;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;

import java.util.ArrayList;

public class MEIMelodicMotiveExporter implements IMEIComponentExporter<MelodicMotive> {

	@Override
	public void process(MEIHierarchicalAnalysesModernExporter meiExporter, StringBuilder sb, int tabs,
			MelodicMotive motive) throws ExportException {
		ArrayList<String> params = new ArrayList<>();
		params.add("type");
		params.add("melodic");
		if (motive.getName() != null) {
			params.add("name");
			params.add(motive.getName());
		}
		if (motive.getHexaColor() != null) {
			params.add("color");
			params.add(motive.getHexaColor());
		}
		
		//XMLExporterHelper.start(sb, tabs, "label", "type", "melodic");
		XMLExporterHelper.start(sb, tabs, "label", params);
		if (motive.getDescription() != null) {
			XMLExporterHelper.startEndTextContent(sb, tabs+1, "description", motive.getDescription());
		}
		for (AtomFigure sds: motive.getAtomFigures()) {
			String tag;
			if (sds.getAtom() instanceof SimpleNote) {
				tag = "note";
			} else if (sds.getAtom() instanceof SimpleRest) {
				tag = "rest";
			} else if (sds.getAtom() instanceof SimpleChord) {
				tag = "chord";
			} else {
				throw new ExportException("Unhandled type: " + sds.getClass().getName());
			}
			if (sds.getAtom().__getID() == null) {
			    throw new ExportException("Atom " + sds.getAtom() + " has not an ID");
            }
			XMLExporterHelper.startEnd(sb, tabs+1, tag, "sameas", "#" + sds.getAtom().__getID());
		}
		
		
		XMLExporterHelper.end(sb, tabs, "label");
	}

}
