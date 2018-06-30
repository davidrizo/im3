package es.ua.dlsi.im3.core.score.io;

import java.util.HashMap;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.HierarchicalIDGenerator;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScorePart;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.StaffGroup;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.core.score.staves.PercussionStaff;



public class NotationBuilder {
	ScoreSong song;
	HierarchicalIDGenerator hierarchicalIDGenerator;
	NotationType notationType;
	HashMap<String, StaffGroup> systems;
	HashMap<String, Staff> staves;

	public NotationBuilder(NotationType notationType) {
		super();

		song = new ScoreSong();
		hierarchicalIDGenerator = new HierarchicalIDGenerator();
		systems = new HashMap<>();
		staves = new HashMap<>();
	}

	public ScoreSong getSong() {
		return song;
	}

	public StaffGroup addSystem() {
		StaffGroup result = new StaffGroup(song,
				hierarchicalIDGenerator.nextStaffGroupHierarchicalOrder(null), song.getStaffGroups().size());
		return result;
	}

	public StaffGroup getOrCreateSystem(String systemId) {
		StaffGroup s = systems.get(systemId);
		if (s == null) {
			s = new StaffGroup(song, hierarchicalIDGenerator.nextStaffGroupHierarchicalOrder(null),
					song.getStaffGroups().size());
			systems.put(systemId, s);
		}
		return s;
	}

	public Staff addStaff(StaffGroup staffSystem, String staffId, int lines, ScorePart part) throws IM3Exception {
		Staff s = staves.get(staffId);
		if (s == null) {
			if (lines == 5) {
				s = new Pentagram(song, hierarchicalIDGenerator.nextStaffHierarchicalOrder(staffSystem),
						song.getStaves().size());
			} else if (lines == 1) {
				s = new PercussionStaff(song,
						hierarchicalIDGenerator.nextStaffHierarchicalOrder(staffSystem), song.getStaves().size());
			} else {
				throw new IM3Exception("Unsupported number of lines (" + lines + " for staff");
			}
			staves.put(staffId, s);		
		} else {
			throw new IM3Exception("The staff with id " + staffId + " already existed");
		}
		return s;
	}

	public Staff getStaff(String staffId) throws IM3Exception {
		Staff s = staves.get(staffId);
		if (s == null) {
			throw new IM3Exception("Staff ID " + staffId + " not found");
		}
		return s;
	}
	/*FRACCIONES public void addClef(Staff staff, Clef clef) throws IM3Exception {
		clef.setTime(staff.getDuration());
		staff.addClef(clef);
	}

	//TODO De momento no inserto el meter en la song
	public void addTimeSignature(Staff staff, TimeSignature ts) throws IM3Exception {
		ts.setTime(staff.getDuration());
		staff.addTimeSignature(ts);
	}*/
}
