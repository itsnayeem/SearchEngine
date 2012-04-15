import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Diplay administration page allowing user to either crawl a new site or
	 * shutdown server.
	 * 
	 * If user shuts down server, redirect to google and shut down server.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String action = request.getParameter("action");
		PrintWriter out = response.getWriter();
		String message = "";
		Cookie cookies[] = request.getCookies();
		String username = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("username")) {
					username = cookie.getValue();
				}
			}
		}

		if (username.trim().equals("admin")) {
			if (action != null && action.equals("shutdown")) {
				out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
						+ "<html><head><title>Shutting down server...</title>"
						+ "<meta http-equiv=\"REFRESH\" content=\"5;url=http://www.google.com\">"
						+ "</head><body>Shutting down server...redirecting to google, "
						+ "so you can search for real stuff</body></html>");
				out.flush();
				WebServer.stop();
				return;
			}
			printAdmin(out, username, message);
		} else {
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
					+ "<html><head><title></title>"
					+ "<meta http-equiv=\"REFRESH\" content=\"0;url=/\">"
					+ "</head><body></body></html>");
		}
	}

	/**
	 * If user gives valid url, start crawling site and display page
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String url = request.getParameter("url");
		PrintWriter out = response.getWriter();
		String message = "";
		Cookie cookies[] = request.getCookies();
		String username = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("username")) {
					username = cookie.getValue();
				}
			}
		}

		if (username.trim().equals("admin")) {
			if (HtmlParser.isValidLink(url)) {
				message = "Starting web crawler with url: " + url;
				RunnableWebCrawler.resetCount();
				new RunnableWebCrawler(url).run();
			} else {
				message = "Bad url: " + url;
			}
			printAdmin(out, username, message);
		} else {
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
					+ "<html><head><title></title>"
					+ "<meta http-equiv=\"REFRESH\" content=\"0;url=/\">"
					+ "</head><body></body></html>");
		}
	}

	public void printAdmin(PrintWriter out, String username, String message) {
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head>"
				+ "<title>Shahoo! - My Shahoo!</title>"
				+ "<style lang=\"text/css\">"
				+ "body { text-align: center; padding: 0; margin: 0;}"
				+ "img {border: 0;}"
				+ "#topmenu li {display: inline; margin: 0 10px;}"
				+ "#topmenu { text-align: right; background-color: #EEE; margin: 0; padding: 5px}"
				+ "#content { text-align: left; width: 1000px; margin: 0 auto; text-align: left; }"
				+ "#box { margin: 10px auto 20px; }"
				+ "#logo_small { display:inline; float: left; }"
				+ "#box_cont { text-align: center; float: left; width: 800px; padding-top: 10px; }"
				+ "#s { display: inline; width: 60%; }"
				+ "#query {width: 100%; text-align: left; background-color: #DDD; margin: 0; padding: 5px;}"
				+ "#url {width: 350px; }"
				+ ".clear { clear: both; margin: 18px; }"
				+ ".column { width: 450px; float: left; }"
				+ "</style>"
				+ "</head>" + "<body>" + "<ul id=\"topmenu\"><li>Welcome <b>"
				+ username
				+ "</b>!</li>"
				+ "<li><a href=\"/account\">My Shahoo!</a></li>"
				+ "<li><a href=\"/login?logout=logout\">Logout</a></li>"
				+ "</ul>"
				+ "<div id=\"content\">"
				+ "<div id=\"box\">"
				+ "<a href=\"index.html\"><img id=\"logo_small\" "
				+ "src=\"http://www2.cs.usfca.edu/~snelrahman/searchsite/logo_small.png\" alt=\"logo_small\"></a>"
				+ "<div id=\"box_cont\"><form action=\"/results\" method=\"get\">"
				+ "<input name=\"s\" id=\"s\" type=\"text\"/>"
				+ "<input name=\"newsearch\" id=\"newsearch\" type=\"hidden\" value=\"1\" />"
				+ "<input type=\"submit\" value=\"search\"/>"
				+ "</div></form></div>"
				+ "<div class=\"clear\"></div>"
				+ "<div id=\"query\">Administration</div>"
				+ "<div class=\"clear\"></div>"
				+ "<div class=\"column\">"
				+ "<form action=\"/admin\" method=\"post\">"
				+ "Crawl New Site<br /> <br />"
				+ "URL:<br />"
				+ "<input name=\"url\" id=\"url\" type=\"text\" />"
				+ "<input type=\"submit\" value=\"Crawl URL\" />"
				+ "</form>"
				+ "</div>"
				+ "<div class=\"column\">Shutdown Server:"
				+ "<ul><li><a href=\"/admin?action=shutdown\">Shutdown Server</a></li>"
				+ "</ul>"
				+ "</div>"
				+ "<div class=\"clear\"></div>"
				+ message
				+ "</div></div></body></html>");

	}
}
