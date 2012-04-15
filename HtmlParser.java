import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains methods used to do various tasks on html files
 */
public class HtmlParser {

	private HtmlParser() {
	};

	/**
	 * Removes all html, javascript and css from a string containing html
	 * 
	 * @param rawdata
	 *            - html string
	 * @return string with html removed
	 */
	public static String removeTags(String rawdata) {
		// remove header information
		rawdata = rawdata.replaceAll("^.*?<", "<");

		// remove javascript and css
		rawdata = rawdata.replaceAll("<(style|script).*?>.*?</(style|script)>",
				" ");

		// remove html tags
		rawdata = rawdata.replaceAll("<.*?>", " ");

		// remove html special characters
		rawdata = rawdata.replaceAll("&.*?;", " ");

		return rawdata;
	}

	/**
	 * Extracts all valid links from string containing html
	 * 
	 * @param rawdata
	 *            - html string
	 * @return ArrayList<String> containing all the valid links
	 */
	public static ArrayList<String> extractLinks(String rawdata) {
		ArrayList<String> retval = new ArrayList<String>();
		String link;

		/*
		 * <[aA] - starts with <a tag
		 * 
		 * \\s - required space
		 * 
		 * [^>]* - all non-end bracket characters
		 * 
		 * href - until href attribute
		 * 
		 * \\s* - possible spaces
		 * 
		 * = - = character
		 * 
		 * \\s* - possible spaces
		 * 
		 * [\"\']?	- either " or ' start quote
		 * 
		 * ([^\"\' ]*) - the value of href tag
		 * 
		 * [\"\']?	- either " or ' end tag
		 * 
		 * [^>]* - all characters before end tag
		 * 
		 * > - end tag
		 */
		Pattern p = Pattern
				.compile("<[aA]\\s[^>]*[hH][rR][eE][fF]\\s*=\\s*[\"\']?([^\"\' ]*)[\"\']?[^>]*>");
		Matcher m = p.matcher(rawdata);

		while (m.find()) {
			link = m.group(1);
			if (isValidLink(link))
				retval.add(link);

		}

		return retval;
	}

	/**
	 * Parse page for page title
	 * 
	 * @param rawdata
	 * @return title of page
	 */
	public static String getPageTitle(String rawdata) {
		String retval = null;
		Pattern p = Pattern.compile("<title.*?>(.*?)</title>");
		Matcher m = p.matcher(rawdata);

		if (m.find()) {
			retval = m.group(1);
		}
		return retval;
	}

	/**
	 * Verify whether a paticual url string is valid
	 * 
	 * @param s
	 * @return
	 * @throws MalformedURLException
	 */
	public static boolean isValidLink(String s) {
		if (s.trim().equals("")) // if empty link
			return false;

		URL url = null;
		try {
			url = new URL(s);
		} catch (MalformedURLException e) {
			return false;
		}

		String protocol = url.getProtocol();

		if (!protocol.equals("") // if not empty
				&& !protocol.equals("http")) // if not http
			return false;
		String file = url.getFile();
		file = file.replaceAll("/", ""); // get rid of slashes

		if (file.trim().equals("") // if only a domain
				|| !file.contains(".") // and doesn't contain a .
				|| file.endsWith(".htm") // or ends in .htm
				|| file.endsWith(".html")) // or ends in .html
			return true;

		return false;
	}
}
