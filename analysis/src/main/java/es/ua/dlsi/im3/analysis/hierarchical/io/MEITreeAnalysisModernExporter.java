package es.ua.dlsi.im3.analysis.hierarchical.io;



import es.ua.dlsi.im3.analysis.hierarchical.TreeAnalysis;
import es.ua.dlsi.im3.core.adt.tree.ITreeLabel;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;

public class MEITreeAnalysisModernExporter extends MEIHierarchicalAnalysisModernExporter<TreeAnalysis<?>> {
	public MEITreeAnalysisModernExporter(String meiType) {
		super(meiType);
	}

	@Override
	public void process(MEIHierarchicalAnalysesModernExporter meiExporter, StringBuilder sb, int tabs, TreeAnalysis<?> analysis) throws ExportException {
		processTree(meiExporter, sb, tabs, analysis.getTree(), getMeiType());
	}

	private void processTree(MEIHierarchicalAnalysesModernExporter meiExporter, StringBuilder sb, int tabs, Tree<?> tree, String type) throws ExportException {
		String tag;
		if (tree.isLeaf() && !tree.isRoot()) {
			tag = "eLeaf";
			XMLExporterHelper.start(sb, tabs, tag, "xml:id", "T" + tree.getID());
		} else {
			tag = "eTree";
			if (type != null) {
				XMLExporterHelper.start(sb, tabs, tag, "xml:id", "T" + tree.getID(), "type", type);
			} else {
				XMLExporterHelper.start(sb, tabs, tag, "xml:id", "T" + tree.getID());
			}
		}
		
		processLabel(meiExporter, sb, tabs+1, tree.getLabel());
		if (!tree.isLeaf()) {
			for (int i=0; i<tree.getNumChildren(); i++) {
				processTree(meiExporter, sb, tabs+1, tree.getChild(i), null);
			}
			XMLExporterHelper.end(sb, tabs, tag);
		} else {
			XMLExporterHelper.end(sb, tabs, tag);
		}
	}

	private void processLabel(MEIHierarchicalAnalysesModernExporter meiExporter, StringBuilder sb, int tabs, ITreeLabel label) throws ExportException {
		IOFactory.getInstance().getExporter(label.getClass()).process(meiExporter, sb, tabs, label);
	}

}
