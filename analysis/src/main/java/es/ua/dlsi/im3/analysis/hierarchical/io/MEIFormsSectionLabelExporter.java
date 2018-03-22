package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.analysis.hierarchical.forms.SectionLabel;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.Measure;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;

import java.util.ArrayList;

public class MEIFormsSectionLabelExporter implements IMEIComponentExporter<SectionLabel> {

	@Override
	public void process(MEIHierarchicalAnalysesModernExporter meiExporter, StringBuilder sb, int tabs, SectionLabel sectionLabel) throws ExportException {
		String tstamp;
		Measure bar = null;
		try {
			Time time = sectionLabel.getScoreAnalysisHookStart().getTime();
			bar = meiExporter.getSong().getMeasureActiveAtTime(time);
			tstamp = MEISongExporter.generateTStamp(bar, time);
		} catch (IM3Exception e) {
			throw new ExportException(e);
		}
		
		ArrayList<String> params = new ArrayList<>();
		params.add("subtype");
		params.add("section");
		params.add("name");
		params.add(sectionLabel.getStringLabel());
		params.add("measureid");
		params.add(bar.__getID());
		params.add("tstamp");
		params.add(tstamp);
		if (sectionLabel.getHexaColor() != null) {
			params.add("color");
			params.add(sectionLabel.getHexaColor());			
		}
		if (sectionLabel.getDescription() == null) {
			//XMLExporterHelper.startEnd(sb, tabs, "label", "subtype", "section", "name", sectionLabel.getStringLabel(), "measureid", "#" + bar.getID(), "tstamp", tstamp);
			XMLExporterHelper.startEnd(sb, tabs, "label", params);
		} else {
			XMLExporterHelper.start(sb, tabs, "label", params);
			//XMLExporterHelper.start(sb, tabs, "label", "subtype", "section", "name", sectionLabel.getStringLabel(), "measureid", "#" + bar.getID(), "tstamp", tstamp);
			XMLExporterHelper.startEndTextContent(sb, tabs+1, "description", sectionLabel.getDescription());
			XMLExporterHelper.end(sb, tabs, "label");
		}
	}

}
