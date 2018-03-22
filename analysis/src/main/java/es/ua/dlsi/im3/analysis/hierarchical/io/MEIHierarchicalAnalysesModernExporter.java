package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.analysis.hierarchical.Analysis;
import es.ua.dlsi.im3.analysis.hierarchical.HierarchicalAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;

import java.util.ArrayList;
import java.util.List;

/**
 * It exports a hierarchical analysis of a ModernSong 
 * @author drizo
 *
 */
public class MEIHierarchicalAnalysesModernExporter extends MEISongExporter {

	private List<Analysis> analyses;
	public MEIHierarchicalAnalysesModernExporter(List<? extends Analysis> analyses) {
		this.analyses = new ArrayList<>();
		for (Analysis a: analyses) {
			this.analyses.add(a);
		}
	}

	public MEIHierarchicalAnalysesModernExporter(Analysis analysis) {
		this.analyses = new ArrayList<>();
		this.analyses.add(analysis);
	}
	
	@Override
	protected void preprocess() throws IM3Exception {
		super.preprocess();
	}

	/**
	 * Generate here the hierarchical analysis
	 * @throws ExportException 
	 */
	@Override
	protected void processBeforeMusic(int tabs) throws ExportException {
		super.processBeforeMusic(tabs);

		XMLExporterHelper.start(sb, tabs, "analyses");
		for (Analysis analysis : analyses) {
			ArrayList<String> params = new ArrayList<>();
			params.add("type");
			params.add(analysis.getType());
			if (analysis.getName() != null) {
				params.add("name");
				params.add(analysis.getName());
			}
			if (analysis.getAuthor() != null) {
				params.add("author");
				params.add(analysis.getAuthor());				
			}
			if (analysis.getDate() != null) {
				params.add("date");
				params.add(XMLExporterHelper.DATE_FORMAT.format(analysis.getDate()));				
			}
			XMLExporterHelper.start(sb, tabs+1, "analysis", params);
			for (HierarchicalAnalysis<?> hierarchicalAnalysis : analysis.getHierarchicalAnalyses()) {
				processAnalysis(tabs+2, hierarchicalAnalysis);
			}
			XMLExporterHelper.end(sb, tabs+1, "analysis");
		}

		XMLExporterHelper.end(sb, tabs, "analyses");		
	}

	protected void processAnalysis(int tabs, HierarchicalAnalysis<?> hierarchicalAnalysis) throws ExportException {
		IOFactory.getInstance().getExporter(hierarchicalAnalysis.getClass()).process(this, sb, tabs, hierarchicalAnalysis);
	}
}
