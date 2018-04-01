package es.ua.dlsi.im3.analysis.hierarchical.io;


import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.adt.tree.TreeException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.Time;

public class FormTreeAnalysisImporter implements IMEIComponentImporter<Tree<AttributesLabel>, FormAnalysis> {
	@Override
	public FormAnalysis importFrom(Tree<AttributesLabel> source, MEIHierarchicalAnalysesModernImporter importer) throws ImportException {
		try {
			// the name will be given later by the general importer
			FormAnalysis result = new FormAnalysis(importer.getScoreSong());
			//RootLabel rootLabel = new RootLabel();
			for (int i=0; i<source.getNumChildren(); i++) {
				Tree<AttributesLabel> child = source.getChild(i);
				if ("eTree".equals(child.getLabel().getTag()) || "eLeaf".equals(child.getLabel().getTag())) {
					importSection(result, child, importer);
				}
			}
			return result;
		} catch (Exception e) {
			throw new ImportException(e);
		}
	}


	private void importSection(FormAnalysis result, Tree<AttributesLabel> child,
			MEIHierarchicalAnalysesModernImporter importer) throws IM3Exception, TreeException {
		
		if (child.getNumChildren() != 1) {
			throw new ImportException("Expected just a child named 'label' of child " + child);
		}
		
		Tree<AttributesLabel> labelChild = child.getChild(0);
		if (!"label".equals(labelChild.getLabel().getTag())) {
			throw new ImportException("Expected child named 'label' of child " + child);
		}
		
		AttributesLabel attrLabel = labelChild.getLabel();
		String name = attrLabel.getAttribute("name");
		String measureid = attrLabel.getAttribute("measureid");
		String tstamp = attrLabel.getAttribute("tstamp");
		String color = attrLabel.getOptionalAttribute("color");
		Time quarters = importer.decodeTStamp(measureid, tstamp);
		
		String description = null;
		for (Tree<AttributesLabel> dchild: labelChild.getChildren()) {
			if (dchild.getLabel().getTag().equals("description")) {
				if (description != null) {
					throw new ImportException("Several descriptions for melodic node label");
				}
				description = dchild.getLabel().getTextContent();
			} else {
				throw new ImportException("Unknown tag for section label: '" + child.getLabel().getTag() + "'");
			}			
		}
		
		
		result.addSection(name, quarters, description, color);
		
		/*ScoreAnalysisHook<FiguresModern> scoreAnalysisHookStart = importer.getScoreSong().getAnalysisStaff().findAnalysisHookWithOnset((long) (quarters * AbstractSong.DEFAULT_RESOLUTION));
		SectionLabel sectionLabel = new SectionLabel(name, scoreAnalysisHookStart);
		Tree<SectionLabel> sectionTree = new Tree<SectionLabel>(sectionLabel);
		return sectionTree;*/
	}

}
