import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 
 * @author snelrahman
 * 
 */
public class QuerySearcher {

	private HashMap<String, CalcResult> resultSet;
	private String qstring;

	public QuerySearcher() {
		resultSet = new HashMap<String, CalcResult>();
	}

	/**
	 * Takes a query string, parses it and saves to a usable format.
	 * 
	 * Each word in the query string will be searched in the index and a set of
	 * raw results is saved
	 * 
	 * The raw results are then changed into a more usable format where each
	 * document is mapped to a CalcResult object, to which the position and word
	 * pair is added. This will allow them to be processed easily. During this
	 * process, a rank is set based on frequency of a word and the position it
	 * is in the query string (first word has more weight)
	 * 
	 * After all the the positions are in the correct place, the CalcResult
	 * objects are told to further process the ranks.
	 * 
	 * @param s
	 *            query string
	 */
	public void query(String s) {
		InvertedIndex indx = InvertedIndex.getInstance();
		clearResults();
		s = s.replaceAll("[^a-zA-Z0-9 \t]", "");
		qstring = s;
		String[] words = s.split("[ \t]");

		ArrayList<String> wordList = new ArrayList<String>();
		for (String w : words) {
			wordList.add(w.toLowerCase());
		}

		HashMap<String, HashMap<String, ArrayList<Integer>>> rawResults;
		rawResults = new HashMap<String, HashMap<String, ArrayList<Integer>>>();

		for (String word : wordList) {
			if (indx.containsWord(word)) {
				rawResults.put(word, indx.getWord(word));
			}
		}

		Iterator<String> docIter = null;
		Iterator<Integer> posIter = null;
		String docName = null;
		Integer position = null;

		double rankModifier = wordList.size() * .25;

		for (String word : wordList) {
			if (rawResults.containsKey(word)) {
				docIter = rawResults.get(word).keySet().iterator();
				while (docIter.hasNext()) {
					docName = docIter.next();

					if (!resultSet.containsKey(docName)) {
						resultSet.put(docName, new CalcResult());
					}

					posIter = rawResults.get(word).get(docName).iterator();
					while (posIter.hasNext()) {
						position = posIter.next();
						resultSet.get(docName).addResult(position, word,
								rankModifier);
					}
				}
			}
			rankModifier -= .25;
		}

		docIter = resultSet.keySet().iterator();
		while (docIter.hasNext()) {
			docName = docIter.next();
			resultSet.get(docName).processRank(wordList);
		}
	}

	/**
	 * Reads a query file line by line, performs a query on each line of text,
	 * then outputs the results of all the queries to an output file
	 * 
	 * @param file
	 *            query file
	 * @param output
	 *            output file
	 */
	public void queryFile(File file, File output) {
		if (file.isFile()) {
			// only look at text files
			if (file.getName().endsWith(".txt")) {
				try {
					// use scanner's token-i-f-y to get each word
					Scanner sc = new Scanner(file);
					String s;

					// loop through words
					while (sc.hasNext()) {
						s = sc.nextLine();
						query(s);
						printResults(output, s);
					}

				} catch (FileNotFoundException e) {
					System.out.println("Problem opening file: "
							+ file.getName());
				}
			} else {
				System.out.println("Must be a text file: " + file.getPath());
			}
		} else {
			System.out.println("Not a file: " + file.getPath());
		}
	}

	/**
	 * Writes result output file. If there are multiple queries, each result
	 * will be appeneded to the previous.
	 * 
	 * @param file
	 *            output file
	 * @param words
	 *            query string from user to label each result set
	 */
	public void printResults(File file, String words) {

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(file, true));
			// write to file
			writer.print("Search Results for: " + words + "\n" + toString()
					+ "\n");
		} catch (FileNotFoundException e) {
			System.out.print("Could not create " + file
					+ " (Permission Denied)");
		} catch (IOException e) {
			System.out.print("Could not create " + file + " (Reason Unknown)");
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * Clears resultSet to be used for new query
	 */
	private void clearResults() {
		resultSet.clear();
	}

	/**
	 * Generate result html
	 * 
	 * @param format
	 * @return
	 */
	public String getHTML(String format) {
		ArrayList<CompResult> parsedResults = new ArrayList<CompResult>();

		StringBuffer retval = new StringBuffer(resultSet.size() * 80);

		Iterator<String> e = resultSet.keySet().iterator();
		String docName;
		CalcResult currResult;

		while (e.hasNext()) {
			docName = e.next();
			currResult = resultSet.get(docName);
			parsedResults.add(new CompResult(docName, currResult.getTier(),
					currResult.getRank(), currResult.getFirstPos()));
		}

		Collections.sort(parsedResults);

		for (CompResult r : parsedResults) {
			retval.append(format
					.replaceAll(
							"<doctitle>",
							InvertedIndex.getInstance()
									.getTitle(r.getDocName()))
					.replaceAll("<docname>", r.getDocName())
					.replaceAll(
							"<snippet>",
							InvertedIndex.getInstance().getSnippet(
									r.getDocName(), r.getFirstPos() - 10,
									r.getFirstPos() + 10, qstring)));
		}

		return new String(retval);
	}

	/**
	 * String representation of final results
	 */
	public String toString() {
		ArrayList<CompResult> parsedResults = new ArrayList<CompResult>();

		StringBuffer retval = new StringBuffer(resultSet.size() * 80);

		Iterator<String> e = resultSet.keySet().iterator();
		String docName;
		CalcResult currResult;

		while (e.hasNext()) {
			docName = e.next();
			currResult = resultSet.get(docName);
			parsedResults.add(new CompResult(docName, currResult.getTier(),
					currResult.getRank(), currResult.getFirstPos()));
		}

		Collections.sort(parsedResults);

		for (CompResult r : parsedResults) {
			retval.append(r.toString());
		}

		return new String(retval);
	}

}
