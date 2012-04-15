/**
 * This class is a wrapper for the elements in the final display. In order to
 * sort the list, this class implements Comparable.
 * 
 * @author snelrahman
 * 
 */
public class CompResult implements Comparable<CompResult> {
	private String docName;
	private int tier;
	private double rank;
	private int firstPos;

	public CompResult(String d, int t, double r, int f) {
		docName = d;
		tier = t;
		rank = r;
		firstPos = f;
	}

	/**
	 * Implementation requirement for Comparable. The ranks will be first sorted
	 * on the tier. Within each tier of resuls, they will be sored by rank.
	 * 
	 * @param rhs
	 *            the object being compared to
	 */
	public int compareTo(CompResult rhs) {
		if (tier > rhs.getTier())
			return -1;
		else if (tier < rhs.getTier())
			return 1;
		else {
			if (rank > rhs.getRank())
				return -1;
			else if (rank < rhs.getRank())
				return 1;
		}
		return 0;
	}

	/**
	 * 
	 * @return tier
	 */
	public int getTier() {
		return tier;
	}

	/**
	 * 
	 * @return rank
	 */
	public double getRank() {
		return rank;
	}

	/**
	 * 
	 * @return docName
	 */
	public String getDocName() {
		return docName;
	}

	/**
	 * 
	 * @return firstPost
	 */
	public int getFirstPos() {
		return firstPos;
	}

	public String toString() {
		return new String("\t" + docName + "\n\t\tRank: " + (int) rank
				+ "\tNumber of consecutive words matched: " + tier + "\n");
	}
}
