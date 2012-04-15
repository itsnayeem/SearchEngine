import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Main search page located at http://base_url/
	 * 
	 * User must be logged in to view this page. If user is not logged in,
	 * redirect to login page.
	 * 
	 * Capture user query and send to result page via GET
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		boolean loggedIn = false;
		String username = null;
		Cookie cookies[] = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("logged_in")
						&& cookie.getValue().equals("true"))
					loggedIn = true;
				else if (cookie.getName().equals("username"))
					username = cookie.getValue();
			}
		}

		PrintWriter out = response.getWriter();

		if (loggedIn) {
			out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
					+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
					+ "<head>"
					+ "<title>Shahoo! - Search</title>"
					+ "<style lang=\"text/css\">"
					+ "body { text-align: center; padding: 0; margin: 0;}"
					+ "img {border: 0;}"
					+ "li {display: inline; margin: 0 10px;}"
					+ "#content {width: 600px;margin: 0 auto;}"
					+ "#box {position: absolute;top: 25%;left: 30%;width: 40%;}"
					+ "#s {width: 80%; margin: 0 3px;}"
					+ "#topmenu {text-align: right; background-color: #EEE; margin: 0; padding: 5px}"
					+ "</style>"
					+ "</head>"
					+ "<body>"
					+ "<ul id=\"topmenu\"><li>Welcome <b>"
					+ username
					+ "</b>!</li>"
					+ "<li><a href=\"/account\">My Shahoo!</a></li>"
					+ "<li><a href=\"/login?logout=logout\">Logout</a></li>"
					+ "</ul>"
					+ "<div id=\"content\">"
					+ "<div id=\"box\">"
					+ "<a href=\"index.html\"><img src=\"http://www2.cs.usfca.edu/~snelrahman/searchsite/logo.png\" "
					+ "alt=\"logo\" /></a>"
					+ "<form action=\"/results\" method=\"get\">"
					+ "<input name=\"s\" id=\"s\" type=\"text\" />"
					+ "<input name=\"newsearch\" id=\"newsearch\" type=\"hidden\" value=\"1\" />"
					+ "<input id=\"submit\" type=\"submit\" value=\"Search\"/>"
					+ "</form>" + "</div>" + "</body>" + "</html>");
		} else {
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
					+ "<html><head><title></title>"
					+ "<meta http-equiv=\"REFRESH\" content=\"0;url=/login\">"
					+ "</head><body></body></html>");
		}

	}
}
