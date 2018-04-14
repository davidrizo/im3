package es.ua.dlsi.im3.analysis.hierarchical.io;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.analysis.hierarchical.Analysis;
import es.ua.dlsi.im3.analysis.hierarchical.FreeAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.RootLabel;
import es.ua.dlsi.im3.analysis.hierarchical.forms.SectionLabel;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotive;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotivesAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MotiveNodeLabel;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MotivesEdgeLabel;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;

public class IOFactory {
	private static IOFactory instance = null;
	HashMap<Class<?>, IMEIComponentExporter<?>> exporters;
	/**
	 * key = type
	 */
	HashMap<String, IMEIComponentImporter<?, ?>> importers;
	
	HashMap<String, Class<? extends Analysis>> analysisCreators;
	
	private IOFactory() {		
		exporters = new HashMap<>();
		importers = new HashMap<>();
		analysisCreators = new HashMap<>();
		//TODO Se podría hacer en otro sitio
		registerExporter(FormAnalysis.class, new MEITreeAnalysisModernExporter("forms"));
		registerExporter(RootLabel.class, new MEIFormsRootLabelExporter());
		registerExporter(SectionLabel.class, new MEIFormsSectionLabelExporter());
		//registerExporter(LeafLabel.class, new MEIFormsLeafLabelExporter());
		registerAnalysisCreator(FreeAnalysis.TYPE, FreeAnalysis.class);
		registerImporter("forms", new FormTreeAnalysisImporter());
		
		registerExporter(MelodicMotivesAnalysis.class, new MEIGraphAnalysisModernExporter("motives"));
		registerExporter(MotiveNodeLabel.class, new MEIMotiveNodeLabelExporter());
		registerExporter(MotivesEdgeLabel.class, new MEIMotiveEdgeLabelExporter());
		registerExporter(MelodicMotive.class, new MEIMelodicMotiveExporter());
		registerImporter("motives", new MotivesGraphAnalysisImporter());
		registerImporter("melodic", new MEIMelodicMotiveImporter());
		
	}
	
	public static IOFactory getInstance() {
		synchronized (IOFactory.class) {
			if (instance == null) {
				instance = new IOFactory();
			}
		}
		return instance;
	}
	
	public void registerExporter(Class<?> clazz, IMEIComponentExporter<?> exporter) {
		exporters.put(clazz, exporter);
	}
	public void registerImporter(String type, IMEIComponentImporter<?, ?> importer) {
		importers.put(type, importer);
	}
	public void registerAnalysisCreator(String type, Class<? extends Analysis> analysisClass) {
		analysisCreators.put(type, analysisClass);
	}

	public Analysis createAnalysis(String type) throws ImportException {
		Class<? extends Analysis> analysisClass = analysisCreators.get(type);
		if (analysisClass == null) {
			throw new ImportException("Cannot find a class for analysis type '" + type + "', valid types are: " + analysisCreators.keySet());
		}
		try {
			return analysisClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ImportException("Cannot instantiate analysis class", e);
		}
		
	}
	public IMEIComponentExporter getExporter(Class<?> clazz) throws ExportException {
		IMEIComponentExporter result = exporters.get(clazz);
		if (result == null) {
			// find parent
			Class<?> baseClass = clazz.getSuperclass();
			if (baseClass != null) {
				try {
					result = getExporter(baseClass);
				} catch (Exception e) {
					// avoid throwing errors at base classes
				}
			}
			if (result == null) {
				throw new ExportException("Cannot find MEI component exporter for class " + clazz + " or any of its ancestors");
			}
		}
		return result;
	}
	
	public IMEIComponentImporter getImporter(String type) throws ImportException {
		IMEIComponentImporter result = importers.get(type);
		if (result == null) {
			throw new ImportException("Cannot find component importer for type " + type);
		}
		return result;
	}	
}
