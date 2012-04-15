import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * InvertedIndex - Data structure of word mapped to files to locations
 * 
 */
public class InvertedIndex {

	/**
	 * HashMap of words mapped to a HashMap of documents mapped to an ArrayList
	 * of positions of the word in given document
	 */
	private HashMap<String, HashMap<String, ArrayList<Integer>>> table;
	private HashMap<String, String> titles;
	private HashMap<String, HashMap<Integer, String>> words;
	private MultiReadSingleWriteLock lock = new MultiReadSingleWriteLock();
	private static InvertedIndex instance = null;

	private InvertedIndex() {
		table = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
		titles = new HashMap<String, String>();
		words = new HashMap<String, HashMap<Integer, String>>();
	}

	public static InvertedIndex getInstance() {
		if (instance == null) {
			synchronized (InvertedIndex.class) {
				if (instance == null) {
					instance = new InvertedIndex();
				}
			}
		}
		return instance;
	}

	/**
	 * Add element to third dimension of data structure, creating HashMaps and
	 * ArrayLists as needed
	 * 
	 * @param word
	 *            first level of structure
	 * @param docName
	 *            second level of structure
	 * @param wordNum
	 *            element to be added to third level ArrayList
	 */
	public void addElement(String word, String docName, Integer wordNum,
			String title) {

		lock.getWriteLock();

		HashMap<String, ArrayList<Integer>> wordRef;

		// if able to find row, save reference to hash map of data
		if ((wordRef = table.get(word)) == null) {
			// if not found, create row for word and save reference
			table.put(word, new HashMap<String, ArrayList<Integer>>());
			wordRef = table.get(word);
		}

		ArrayList<Integer> listRef;

		// if able to find document, save reference to list of data
		if ((listRef = wordRef.get(docName)) == null) {
			// if not found, create data set and save reference
			wordRef.put(docName, new ArrayList<Integer>());
			listRef = wordRef.get(docName);
		}

		// by here, there should already be a row for word and column for
		// document there is no way for there to be duplicate values (as of
		// now), so just add.
		listRef.add(wordNum);

		titles.put(docName, title);

		HashMap<Integer, String> docRef;
		if ((docRef = words.get(docName)) == null) {
			words.put(docName, new HashMap<Integer, String>());
			docRef = words.get(docName);
		}
		docRef.put(wordNum, word);

		lock.releaseWriteLock();
	}

	/**
	 * Return deep copy of second level map related to word.
	 * 
	 * @param word
	 *            Key to look up
	 * @return HashMap of documents
	 */
	public HashMap<String, ArrayList<Integer>> getWord(String word) {

		HashMap<String, ArrayList<Integer>> retval = new HashMap<String, ArrayList<Integer>>();

		lock.getReadLock();

		HashMap<String, ArrayList<Integer>> wordTable = table.get(word);

		Iterator<String> docIter = wordTable.keySet().iterator();
		String docName;
		ArrayList<Integer> positionList, templist;

		while (docIter.hasNext()) {
			docName = docIter.next();
			positionList = wordTable.get(docName);
			templist = new ArrayList<Integer>();
			for (Integer position : positionList) {
				templist.add(position.intValue());
			}
			retval.put(docName, templist);
		}
		lock.releaseReadLock();
		return retval;
	}

	/**
	 * Return page title from titles map
	 * 
	 * @param docName
	 * @return page title
	 */
	public String getTitle(String docName) {
		String retval = null;
		lock.getReadLock();

		if (titles.get(docName) != null)
			retval = titles.get(docName);

		lock.releaseReadLock();
		return retval;
	}

	/**
	 * Get snippet of code from document from star to end positions and
	 * highlight the matching words.
	 * 
	 * @param docName
	 * @param start
	 * @param end
	 * @param qstring
	 * @return
	 */
	public String getSnippet(String docName, int start, int end, String qstring) {
		StringBuffer retval = new StringBuffer();
		String word;
		lock.getReadLock();

		HashMap<Integer, String> docRef = words.get(docName);
		if (docRef == null) {
			lock.releaseReadLock();
			return "Page preview not found";
		}
		for (int i = start; i <= end; i++) {
			word = docRef.get(i);
			if (word != null && qstring.trim().contains(word.trim())) {
				word = "<b>" + word + "</b>";
			}
			retval.append(word + " ");
		}

		lock.releaseReadLock();
		return new String(retval);
	}

	/**
	 * Check to see if key exists in table
	 * 
	 * @param word
	 *            word to search
	 * @return if it exists
	 */
	public boolean containsWord(String word) {
		lock.getReadLock();
		boolean retval = table.containsKey(word);
		lock.releaseReadLock();
		return retval;
	}

	/**
	 * Write the data structure to a given file
	 * 
	 * @param filename
	 *            path to file
	 */
	public void printIndex(String filename) {

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(filename));
			writer.print(table.toString());
		} catch (FileNotFoundException e) {
			System.out.print("Could not create " + filename
					+ " (Permission Denied)");
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * String representation of data structure
	 */
	public String toString() {

		StringBuffer retval = new StringBuffer(table.size() * 80);
		retval.append("Contents of Index: \n");

		Iterator<String> e = table.keySet().iterator();
		Iterator<String> i;

		String word, docName;

		while (e.hasNext()) {
			word = e.next();
			retval.append("\n\t" + word);

			i = table.get(word).keySet().iterator();
			while (i.hasNext()) {
				docName = i.next();
				retval.append("\n\t\t" + docName + ": "
						+ table.get(word).get(docName));
			}
		}

		return new String(retval);
	}

}
