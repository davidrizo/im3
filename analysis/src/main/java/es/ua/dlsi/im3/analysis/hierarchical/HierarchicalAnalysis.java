package es.ua.dlsi.im3.analysis.hierarchical;

import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IDGenerator;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.IADT;
import es.ua.dlsi.im3.core.score.ScoreSong;

public class HierarchicalAnalysis<HierarchicalStructureType extends IADT> {
	protected HierarchicalStructureType hierarchicalStructure;
	protected ScoreSong song;
	protected long ID;
	
	public HierarchicalAnalysis(ScoreSong song) throws IM3Exception {
		ID = IDGenerator.getID();
		if (song == null) {
			throw new IM3Exception("null song parameter in hierarchical analysis constructor");
		}
		this.song = song;
		if (!song.hasAnalysisStaff()) {
			Logger.getLogger(HierarchicalAnalysis.class.getName()).info("Creating analysis staff");
			song.createAnalysisPartAndStaff();
			song.createAnalysisHooks(song.getAnalysisStaff());
		} 

	}

	public long getID() {
		return ID;
	}


	public ScoreSong getSong() {
		return song;
	}
	
	
}
