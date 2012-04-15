import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResultsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Perform query on query string and display results
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		QuerySearcher searcher = null;
		String s = null;
		String username = null;
		Cookie cookies[] = request.getCookies();

		PrintWriter out = response.getWriter();

		s = request.getParameter("s");

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("username"))
					username = cookie.getValue();
			}
		}

		if (s != null && !s.trim().equals("")) {
			searcher = new QuerySearcher();
			searcher.query(s);
			if (Integer.parseInt(request.getParameter("newsearch")) == 1)
				QueryHistoryModel.addQuery(username, s);
		}

		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head>"
				+ "<title>Shahoo! - Results for: "
				+ s
				+ "</title>"
				+ "<style lang=\"text/css\">"
				+ "body { text-align: center; padding: 0; margin: 0;}"
				+ "img {border: 0;}"
				+ "li {display: inline; margin: 0 10px;}"
				+ "#topmenu { text-align: right; background-color: #EEE; margin: 0; padding: 5px}"
				+ "#content { text-align: left; width: 1000px; margin: 0 auto;text-align: left; }"
				+ "#box { margin: 10px auto 20px; }"
				+ "#logo_small { display:inline; float: left; }"
				+ "#box_cont { text-align: center; float: left; width: 800px; padding-top: 10px; }"
				+ "#s { display: inline; width: 60%; }"
				+ "#query {width: 100%; text-align: left; background-color: #DDD; margin: 0; padding: 5px;}"
				+ ".clear { clear: both; margin: 18px; }"
				+ ".result { margin: 20px 10px; }"
				+ ".link { font-size: 18px; }"
				+ ".snippet { font-size: 12px; }"
				+ ".url { font-size: 14px; color: green; }"
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
				+ "<a href=\"index.html\"><img id=\"logo_small\" "
				+ "src=\"http://www2.cs.usfca.edu/~snelrahman/searchsite/logo_small.png\" alt=\"logo_small\"></a>"
				+ "<div id=\"box_cont\"><form action=\"/results\" method=\"get\">"
				+ "<input name=\"s\" id=\"s\" type=\"text\" value=\""
				+ s
				+ "\" />"
				+ "<input name=\"newsearch\" id=\"newsearch\" type=\"hidden\" value=\"1\" />"
				+ "<input type=\"submit\" value=\"search\"/>"
				+ "</div></form></div>"
				+ "<div class=\"clear\"></div>"
				+ "<div id=\"query\">Search results for: "
				+ s
				+ "</div>"
				+ "<div class=\"clear\"></div>");

		if (searcher != null) {
			out.println(searcher
					.getHTML("<div class=\"result\">"
							+ "<a href=\"/savelink?link=<docname>\" class=\"link\"><doctitle></a>"
							+ "<div class=\"snippet\"><snippet></div>"
							+ "<div class=\"url\"><docname></div></div>"));
		} else {
			out.println("<div id=\"result\">No results found</div>");
		}
		out.println("</div></body></html>");

	}
}
