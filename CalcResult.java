import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Result object used to perform rank and tier calculations
 * 
 * rank is calculated first by each occurence of all of the words inputted by
 * user. the position of the word will give it more or less weight. if words
 * occur consecutivey, they are bumped to a higher 'tier' of rank and are given
 * a higher priority.
 * 
 * @author snelrahman
 * 
 */
public class CalcResult {

	private double rank;
	private int tier;
	// list of positions mapped to words searched
	private HashMap<Integer, String> locByWord;
	private int firstPos = 0;

	public CalcResult() {
		rank = 0;
		tier = 1;
		locByWord = new HashMap<Integer, String>();
	}

	/**
	 * Add a result that matches the document which this object is paired with
	 * 
	 * @param loc
	 *            position
	 * @param word
	 *            word at that position
	 * @param rankModifier
	 *            if the word is toward the beginning of the query string it
	 *            will be weighted more
	 */
	public void addResult(Integer loc, String word, double rankModifier) {
		rank++;
		rank += rankModifier;
		if (loc < 2000.0) {
			rank += 10 * ((2000 - loc) / 2000.0);
		}
		locByWord.put(loc, word);
	}

	/**
	 * Call after all the positions of matching words have been put into the
	 * locByWord HashMap. First take each location in the HashMap to see how
	 * close it is to the beginning of the document.
	 * 
	 * If the average number of words in a page of a given document is 2000,
	 * then increase rank based on the position till 2000. position 1 will add
	 * an additional 10 points and position 1999 will add .1 points
	 * 
	 * Afterwards, call the setTeir method on each word in the list.
	 * 
	 * @param wordList
	 *            List of words inputted by user
	 */
	public void processRank(ArrayList<String> wordList) {
		Iterator<Integer> posIter;
		Integer position;

		for (int wordPos = 0; wordPos < wordList.size(); wordPos++) {

			posIter = locByWord.keySet().iterator();
			while (posIter.hasNext()) {

				position = posIter.next();
				if (firstPos == 0) {
					firstPos = position;
				}

				if (wordList.size() > 1) {
					setTier(wordList, wordPos, position,
					/*
					 * max number of recursive calls
					 * 
					 * if there are 4 total words (wordList.size)
					 * 
					 * the quick brown fox
					 * 
					 * and the current word is the second (quick)
					 * 
					 * there should only be a check for the last two words
					 * (brown, fox)
					 * 
					 * 4-1-1 = 2 -> 2 additional recursive calls.
					 */
					wordList.size() - wordPos - 1);
				}
			}
		}
	}

	/**
	 * Calls calcTier method and processes the results from there by setting the
	 * highest tier as the tier of this object.
	 * 
	 * @param wordList
	 *            List of words inputted by user, passed to calcTier
	 * @param wordPos
	 *            Index of first word in user inputted list that needs to be
	 *            check, passed to calcTier
	 * @param pos
	 *            The position of the word being looked at
	 * @param max
	 *            The maximum number of recursive calls
	 */
	private void setTier(ArrayList<String> wordList, int wordPos, int pos,
			int max) {
		int currTier = calcTier(wordList, wordPos, pos, max);
		if (currTier > tier) {
			tier = currTier;
		}
	}

	/**
	 * Recursive function to search the word in the current position with the
	 * next words in the query string till the end.
	 * 
	 * ex: query = "the quick brown fox"
	 * 
	 * calcTier gets word at position pos (quick).
	 * 
	 * if (quick) matches wordList[wordPos] (quick) so it recursively calls
	 * itself till the words in question are the last two in the string (brown,
	 * fox) else leave function.
	 * 
	 * the lowest call of the recursion will compare the last two words to see
	 * if they match the corresponding words in the user query string
	 * 
	 * ex: if a string in the file reads "the quick brown fox", the function
	 * will increase tier and return else do nothing and return.
	 * 
	 * upon return to higher call, check the two words before.. until it goes
	 * back to position of the original word.
	 * 
	 * @param wordList
	 *            query string
	 * @param wordPos
	 *            position of starting word
	 * @param pos
	 *            position on locByWord
	 * @param max
	 *            maximum number of recursions, see processRank for details
	 * @return the number of consecutive words so far from the end
	 */
	private int calcTier(ArrayList<String> wordList, int wordPos, int pos,
			int max) {

		// base case - end of query string or break in consecutive values
		if ((max == 0) || !locByWord.containsKey(pos))
			return 1;

		// number of consecutive words starting from end
		int currTier = calcTier(wordList, wordPos + 1, pos + 1, max - 1);

		// if the word in current position matches the current word in query
		if (locByWord.get(pos).equals(wordList.get(wordPos))
		// and there is a value in the next position
				&& locByWord.containsKey(pos + 1)
				// and the value in the next pos matches the next value in query
				&& locByWord.get(pos + 1).equals(wordList.get(wordPos + 1))) {
			return ++currTier;
		}
		return 1;
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
	 * @return tier
	 */
	public int getTier() {
		return tier;
	}

	/**
	 * 
	 * @return firstPos
	 */
	public int getFirstPos() {
		return firstPos;
	}

	public String toString() {
		return new String("Rank: " + (int) rank
				+ "\tNumber of consecutive words matched: " + tier + "\n");
	}

}
