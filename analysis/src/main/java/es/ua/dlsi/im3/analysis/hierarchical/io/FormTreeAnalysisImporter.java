package es.ua.dlsi.im3.analysis.hierarchical.io;


import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysisTreeNodeLabel;
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
                    importDivision(result, result.getTree(), child, importer);
				}
			}
			return result;
		} catch (Exception e) {
			throw new ImportException(e);
		}
	}


    /**
     * It can be a section, subsection, a period, a phrase, or whatever
     * @param result
     * @param child
     * @param importer
     * @throws IM3Exception
     * @throws TreeException
     */
	private void importDivision(FormAnalysis result, Tree<FormAnalysisTreeNodeLabel> parent, Tree<AttributesLabel> child,
			MEIHierarchicalAnalysesModernImporter importer) throws IM3Exception, TreeException {

	    // locate the label element and create a tree for it
        Tree<FormAnalysisTreeNodeLabel> newDivisionTree = null;
        for (int i=0; newDivisionTree == null && i<child.getNumChildren(); i++) {
            Tree<AttributesLabel> labelChild = child.getChild(i);

            if ("label".equals(labelChild.getLabel().getTag())) {
                AttributesLabel attrLabel = labelChild.getLabel();
                String name = attrLabel.getAttribute("name");
                String measureid = attrLabel.getAttribute("measureid");
                String tstamp = attrLabel.getAttribute("tstamp");
                String color = attrLabel.getOptionalAttribute("color");
                Time quarters = importer.decodeTStamp(measureid, tstamp);

                String description = null;
                for (Tree<AttributesLabel> dchild : labelChild.getChildren()) {
                    if (dchild.getLabel().getTag().equals("description")) {
                        if (description != null) {
                            throw new ImportException("Several descriptions for melodic node label");
                        }
                        description = dchild.getLabel().getTextContent();
                    } else {
                        throw new ImportException("Unknown tag for section label: '" + child.getLabel().getTag() + "'");
                    }
                }
                newDivisionTree = result.addDivision(parent, name, quarters, description, color);
            }
        }

        // for all children but previously used <label>
        for (int i=0; i<child.getNumChildren(); i++) {
            Tree<AttributesLabel> nonLabelChild = child.getChild(i);
            if (!"label".equals(nonLabelChild.getLabel().getTag())) {
                importDivision(result, newDivisionTree, nonLabelChild, importer);
            }
        }
		
		/*ScoreAnalysisHook<FiguresModern> scoreAnalysisHookStart = importer.getScoreSong().getAnalysisStaff().findAnalysisHookWithOnset((long) (quarters * AbstractSong.DEFAULT_RESOLUTION));
		SectionLabel sectionLabel = new SectionLabel(name, scoreAnalysisHookStart);
		Tree<SectionLabel> sectionTree = new Tree<SectionLabel>(sectionLabel);
		return sectionTree;*/
	}

}
