package es.ua.dlsi.im3.core.score;


public class HierarchicalIDGenerator {
	protected int nextStaffOrder = 0;
	protected int nextVerticalDivisionIdentifier = 0;
	protected int topOrder = 0;

	public String nextStaffGroupHierarchicalOrder(StaffGroup currentPartGroup) {
		String hierarchicalOrder = null;
		if (currentPartGroup != null) {
			hierarchicalOrder = currentPartGroup.getHierarchicalOrder() + "."
					+ (currentPartGroup.getChildren().size() + 1);
		} else {
			hierarchicalOrder = Integer.toString(++topOrder);
		}
		return hierarchicalOrder;
	}

	public String nextStaffHierarchicalOrder(StaffGroup staffGroup) {
		String hierarchicalOrder;
		if (staffGroup != null) {
			hierarchicalOrder = staffGroup.getHierarchicalOrder() + "." + (staffGroup.getChildren().size() + 1);
		} else {
			hierarchicalOrder = Integer.toString(++nextStaffOrder);
		}
		return hierarchicalOrder;
	}
	
	public int getNextVerticalDivisionIdentifier() {
		return ++nextVerticalDivisionIdentifier;
	}

}
