public class Driver {

	/**
	 * Parse command line arguments
	 * 
	 * Create InvertedIndex to store data
	 * 
	 * Create FileTraverser object and traverse directories, filling in index
	 * 
	 * Create WorkQueue for FileTraverser to use
	 * 
	 * Create QuerySearcher and perform query for each query in specified file
	 * which will write results to results.txt
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// case: wrong number of args -> automatically incorrect input
		if (args.length != 2) {
			System.out.println("Incorrect number of parameters\n");
			usage();
			return;
		}

		String url = null;

		for (int i = 0; i < args.length; i += 2) {

			if (args[i].equals("-w")) {
				url = args[i + 1];
			}
		}

		System.out.println("Building index... ");
		if (HtmlParser.isValidLink(url)) {
			System.out.println("Starting web crawler with url: " + url);
			new RunnableWebCrawler(url).run();
		} else {
			System.out.println("Bad url: " + url);
			return;
		}

		try {
			System.out.println("Starting Web Server");
			new WebServer().start();
		} catch (Exception e) {
		}

		WorkQueue.getInstance().shutdownQueue();

	}

	public static void usage() {
		System.out.println("\nUSAGE: -w url -q /path/to/query/file");
	}

}