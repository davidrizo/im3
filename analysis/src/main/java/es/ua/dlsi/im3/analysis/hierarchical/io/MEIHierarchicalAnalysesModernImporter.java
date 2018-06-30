package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.analysis.hierarchical.Analysis;
import es.ua.dlsi.im3.analysis.hierarchical.HierarchicalAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.adt.tree.TreeException;
import es.ua.dlsi.im3.core.io.IXMLSAXImporterExtension;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


/**
 * It parses the MEI file, obtains a generic tree representation from the XML (it follows the XML tree structure)
 * that is given to the specialized hierarchical analyses
 * that build specific objects
 * @author drizo
 *
 */
public class MEIHierarchicalAnalysesModernImporter implements IXMLSAXImporterExtension {
	private ArrayList<Analysis> analyses;
	/**
	 * Cannot convert into regular HierarchicalAnalysis until the song has been imported
	 */
	private ArrayList<ArrayList<GenericTreeAndGraphAnalysis>> genericTreeAndGraphAnalyses;

    /**
     * Tree XML structure
     */
	Tree<AttributesLabel> lastTree;
	private boolean inAnalysisElement;
	ScoreSong scoreSong;
	//TODO lastGraph
	private Analysis lastAnalysis;
	private ArrayList<GenericTreeAndGraphAnalysis> lastGenericTreeAndGraphAnalysis;
	private MEISongImporter meiImporter;
	
	public MEIHierarchicalAnalysesModernImporter() {
	}
	
	public void importSongAndAnalyses(File file) throws ImportException {
		meiImporter = new MEISongImporter();
		analyses = new ArrayList<>();
        lastTree = null;
        lastAnalysis = null;
        inAnalysisElement = false;
		genericTreeAndGraphAnalyses = new ArrayList<>();
		meiImporter.registerExtension(this);
		scoreSong = meiImporter.importSong(file);
		processHierarchicalAnalyses();
	}
	
	//public ArrayList<? extends HierarchicalAnalysis<?>> getHierachicalAnalyses() {
	//	return hierachicalAnalyses;
	//}
	

	public ScoreSong getScoreSong() {
		return scoreSong;
	}

	public ArrayList<Analysis> getAnalyses() {
		return analyses;
	}

	@Override
	public void handleOpenElement(String element, HashMap<String, String> attributesMap) throws ImportException {
		String type;
		
		switch (element) {
			case "analysis":
				type = getAttribute(attributesMap, "type");
				String name = getOptionalAttribute(attributesMap, "name");
				String author = getOptionalAttribute(attributesMap, "author");
				String date = getOptionalAttribute(attributesMap, "date");
				lastAnalysis = IOFactory.getInstance().createAnalysis(type);				
				analyses.add(lastAnalysis);
				lastGenericTreeAndGraphAnalysis = new ArrayList<>();
				genericTreeAndGraphAnalyses.add(lastGenericTreeAndGraphAnalysis);
				lastAnalysis.setAuthor(author);
				lastAnalysis.setName(name);
				if (date != null) {
					try {
						lastAnalysis.setDate(XMLExporterHelper.DATE_FORMAT.parse(date));
					} catch (ParseException e) {
						throw new ImportException(e);
					}
				}
				this.lastTree = null;
				this.inAnalysisElement = true;
				break;
				//TODO Más genérico
            case "graphical":
            case "colors":
                break;
            case "mapping":
                String nodeName = getAttribute(attributesMap, "name");
                String color = getAttribute(attributesMap, "color");
                if (lastAnalysis == null) {
                    throw new ImportException("Cannot import a graphical color mapping without an analysis");
                }
                lastAnalysis.getGraphical().addColorMapping(nodeName, color);
                break;
			default:
				if (this.inAnalysisElement) {
					AttributesLabel treeLabel = new AttributesLabel(element);
					Tree<AttributesLabel> tree = new Tree<>(treeLabel);
					if (lastTree != null) {
						try {
							lastTree.addChild(tree);
						} catch (TreeException e) {
							throw new ImportException(e);
						} 
					} else {
						type = getOptionalAttribute(attributesMap, "type");
						lastGenericTreeAndGraphAnalysis.add(new GenericTreeAndGraphAnalysis(type, tree));
					}
					lastTree = tree;
					for (Entry<String, String> entry: attributesMap.entrySet()) {
						treeLabel.addAttribute(entry.getKey(), entry.getValue());
					}
				}
				break;
		}
	}



	
	private String getOptionalAttribute(HashMap<String, String> attributesMap, String key) {
		String result = attributesMap.get(key);
		return result;
	}
	
	private String getAttribute(HashMap<String, String> attributesMap, String key) throws ImportException {
		String result = attributesMap.get(key);
		if (result == null) {
			throw new ImportException("Cannot find attribute " + key + " among attributes " + attributesMap);
		}
		return result;
	}

	@Override
	public void handleElementContent(ArrayList<String> elementStack, String currentElement, String content) throws ImportException {
		if (inAnalysisElement) {
            if (lastTree != null) {
				lastTree.getLabel().setTextContent(content);
			} else if (content != null && !content.isEmpty() && !content.equals("null")) {
				throw new ImportException("Non empty content: '" + content + "' for lastTree = null");
			}
		}
	}

	@Override
	public void handleCloseElement(String elementTag) {
		switch (elementTag) {
			case "analysis":
				inAnalysisElement = false;
				// break
			default:
				if (inAnalysisElement && lastTree != null) {
                    lastTree = lastTree.getParent();
				}
				break;			
			}
	}

	
	protected void processHierarchicalAnalyses() throws ImportException {
		if (analyses.size() != genericTreeAndGraphAnalyses.size()) {
			throw new ImportException("Analyses size (" + analyses.size() + ") != genericTreeAndGraphAnalyses ("+ genericTreeAndGraphAnalyses.size() + ")");
		}
		// now convert generic tree or graph to specific analysis types
		for (int i=0; i<analyses.size(); i++) {
			Analysis analysis = analyses.get(i);
			ArrayList<GenericTreeAndGraphAnalysis> genericTreeAndGraphAnalysis = genericTreeAndGraphAnalyses.get(i);

			for (GenericTreeAndGraphAnalysis g : genericTreeAndGraphAnalysis) {
				HierarchicalAnalysis<?> imported = (HierarchicalAnalysis<?>) IOFactory.getInstance().getImporter(g.getType()).importFrom(g.getDataStructure(), this);
				try {
					analysis.addAnalysis(imported);
				} catch (IM3Exception e) {
					throw new ImportException(e);
				}
			}
		}
	}

	/*public AMTimedElement findXMLID(String xmlid) throws ImportException {
		return meiImporter.findXMLID(xmlid);
	}

	public double decodeTStamp(AMMeasure measure, String tstamp) {
		return meiImporter.decodeTStamp(measure, tstamp);
	}*/
	
	public Object findXMLID(String xmlid) throws ImportException {
		return meiImporter.findXMLID(xmlid);
	}

	public Time decodeTStamp(String measureid, String tstamp) throws ImportException {
		return meiImporter.decodeTStamp(measureid, tstamp);
	}
	

}
