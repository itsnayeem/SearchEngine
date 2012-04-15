import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Runnable object that will process web pages.
 * 
 * Process: 1. Download web page content 2. Extract all properly formed links
 * from page 3. For each link, create a new runnable object and put it into
 * WorkQueue 4. Remove html from pages 5. Index content and add to Inverted
 * Index
 */
public class RunnableWebCrawler implements Runnable {

	private static final int MAX_HOPS = 30;
	private static final HashMap<String, Boolean> paths = new HashMap<String, Boolean>();
	private static Integer count = 0;
	private String path;
	private boolean oneone;

	/**
	 * Constructor using HTTP/1.0
	 * 
	 * @param u
	 */
	public RunnableWebCrawler(String u) {
		path = u;
		oneone = false;
	}

	/**
	 * Alternate constructor if need to use HTTP/1.1
	 * 
	 * @param u
	 * @param one1
	 */
	public RunnableWebCrawler(String u, boolean one1) {
		path = u;
		oneone = one1;
	}

	/**
	 * Reset count before putting in new seed
	 */
	public static void resetCount() {
		count = 0;
	}

	/**
	 * 1. Open connection to url 2. Get data 3. Parse data 4. Insert data into
	 * inverted index
	 */
	public void run() {
		InvertedIndex indx = InvertedIndex.getInstance();
		WorkQueue wq = WorkQueue.getInstance();
		StringBuffer webContent = null;
		String line, language, urlpath;
		Socket s = null;
		PrintWriter output = null;
		BufferedReader input = null;

		path = path.replaceAll("/$", "");

		try {
			URL url = new URL(path);
			System.out.println("Resolving hostname to: "
					+ InetAddress.getByName(url.getHost()).getHostAddress());
			s = new Socket(InetAddress.getByName(url.getHost())
					.getHostAddress(), 80);

			output = new PrintWriter(
					new OutputStreamWriter(s.getOutputStream()));

			if (!url.getPath().trim().equals("")) {
				urlpath = url.getPath();
			} else {
				urlpath = "/";
			}

			if (oneone) {
				language = " HTTP/1.1\nHost: " + url.getHost() + "\n";
			} else {
				language = " HTTP/1.0\nHost: " + url.getHost() + "\n";
			}
			output.println("GET " + urlpath + language);
			output.flush();

			input = new BufferedReader(
					new InputStreamReader(s.getInputStream()));

			webContent = new StringBuffer();
			if ((line = input.readLine()) != null) {

				if (line.contains("HTTP")) {
					System.out.println("Response: " + line);
					if (line.contains("400") && !oneone) {
						wq.execute(new RunnableWebCrawler(path, true));
					} else if (!line.contains("200")) {
						synchronized (count) {
							count--;
						}
						return;
					}
				} else {
					webContent.append(line + " ");
				}
			}
			while ((line = input.readLine()) != null) {
				webContent.append(line + " ");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null)
				output.close();
			try {
				if (input != null)
					input.close();
				if (s != null)
					s.close();
			} catch (IOException e) {
			}
		}
		if (webContent == null) {
			return;
		}
		String rawContent = new String(webContent);
		if (rawContent.trim().equals("")) {
			return;
		}

		ArrayList<String> extractedLinks = HtmlParser.extractLinks(rawContent);
		synchronized (paths) {
			for (String link : extractedLinks) {
				link = link.trim();
				if (!paths.containsKey(link) && count <= MAX_HOPS) {
					paths.put(link, true);
					System.out.println("Adding to work queue: " + link);
					wq.execute(new RunnableWebCrawler(link));
					synchronized (count) {
						count++;
					}
				}
			}
		}

		String pageTitle = HtmlParser.getPageTitle(rawContent);
		String cleanedData = HtmlParser.removeTags(rawContent.toLowerCase());
		if (pageTitle == null) {
			pageTitle = path;
		}

		// use scanner's token-i-f-y to get each word
		Scanner sc = new Scanner(cleanedData);
		Integer wordNum = 0;
		String word;

		// loop through words
		while (sc.hasNext()) {
			word = sc.next();
			// get rid of non alpha_num chars
			word = word.replaceAll("[^a-zA-Z0-9]", "");
			// if the word is not empty (can be caused by deleting
			// punctuation)
			if (!word.trim().isEmpty())
				indx.addElement(word.toLowerCase(), path, wordNum, pageTitle);
			wordNum++;
		}
	}
}
