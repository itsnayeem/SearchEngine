import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccountServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Display backend for user account administration. Clear history or clear
	 * links on user request.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		boolean loggedIn = false;
		Cookie cookies[] = request.getCookies();
		String username = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("logged_in")
						&& cookie.getValue().equals("true"))
					loggedIn = true;
				else if (cookie.getName().equals("username")) {
					username = cookie.getValue();
				}
			}
		}

		PrintWriter out = response.getWriter();

		if (loggedIn) {
			String clearhist = request.getParameter("clearhist");
			if (clearhist != null && clearhist.equals("clearhist")) {
				QueryHistoryModel.clearQueries(username);
			}
			String clearlinks = request.getParameter("clearlinks");
			if (clearlinks != null && clearlinks.equals("clearlinks")) {
				LinkHistoryModel.clearLinks(username);
			}

			ArrayList<String> historyList = QueryHistoryModel
					.getQueries(username);
			StringBuffer history = new StringBuffer("");
			for (String i : historyList) {
				history.append("<li>" + i + "</li>");
			}

			ArrayList<String> linksList = LinkHistoryModel.getLinks(username);
			StringBuffer links = new StringBuffer("");
			for (String i : linksList) {
				links.append("<li>" + i + "</li>");
			}

			printAccount(out, username, "", new String(history), new String(
					links));
		} else {
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
					+ "<html><head><title></title>"
					+ "<meta http-equiv=\"REFRESH\" content=\"0;url=/login\">"
					+ "</head><body></body></html>");
		}
	}

	/**
	 * Change username and password for user on request and display page
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		boolean loggedIn = false;
		Cookie cookies[] = request.getCookies();
		PrintWriter out = response.getWriter();
		String error = "";
		String username = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("logged_in")
						&& cookie.getValue().equals("true"))
					loggedIn = true;
				else if (cookie.getName().equals("username")) {
					username = cookie.getValue();
				}
			}
		}

		if (loggedIn) {
			String oldpass = request.getParameter("oldpass");
			String newpass = request.getParameter("newpass");
			String confirmpass = request.getParameter("confirmpass");

			if (oldpass == null || newpass == null || confirmpass == null) {
				error = "Username and/or password fields are empty";
				printAccount(out, username, error, "", "");
				return;
			} else if (!newpass.trim().equals(confirmpass.trim())) {
				error = "Passwords don't match";
				printAccount(out, username, error, "", "");
				return;
			}

			if (UserModel.changePassword(username, oldpass, newpass)) {
				error = "Password successfully changed";
			} else {
				error = "Error changing password";
			}

			ArrayList<String> historyList = QueryHistoryModel
					.getQueries(username);
			StringBuffer history = new StringBuffer("");
			for (String i : historyList) {
				history.append("<li>" + i + "</li>");
			}

			ArrayList<String> linksList = LinkHistoryModel.getLinks(username);
			StringBuffer links = new StringBuffer("");
			for (String i : linksList) {
				links.append("<li>" + i + "</li>");
			}

			printAccount(out, username, error, new String(history), new String(
					links));

		} else {
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
					+ "<html><head><title></title>"
					+ "<meta http-equiv=\"REFRESH\" content=\"0;url=/login\">"
					+ "</head><body></body></html>");
		}

	}

	public void printAccount(PrintWriter out, String username, String error,
			String history, String links) {

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
				+ ".clear { clear: both; margin: 18px; }"
				+ ".column { width: 300px; float: left; }"
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
				+ "<div id=\"query\">Change Account Settings</div>"
				+ "<div class=\"clear\"></div>"
				+ "<div class=\"column\">"
				+ "<form action=\"/account\" method=\"post\">"
				+ "Change Password<br /> <br />"
				+ "Old Password:<br />"
				+ "<input name=\"oldpass\" id=\"oldpass\" type=\"password\" /><br />"
				+ "New Password:<br />"
				+ "<input name=\"newpass\" id=\"newpass\" type=\"password\" /><br />"
				+ "Confirm Password:<br />"
				+ "<input name=\"confirmpass\" id=\"confirmpass\" type=\"password\" /><br />"
				+ "<input type=\"submit\" value=\"Change Password\" /></form>"
				+ "</div>"
				+ "<div class=\"column\">Search history:"
				+ "<ul><li><a href=\"/account?clearhist=clearhist\">Clear History</a></li>"
				+ history
				+ "</ul>"
				+ "</div>"
				+ "<div class=\"column\">Page visit history:"
				+ "<ul><li><a href=\"/account?clearlinks=clearlinks\">Clear Links</a></li>"
				+ links
				+ "</ul>"
				+ "</div>"
				+ error
				+ "<div class=\"clear\"></div>" + "</div></div></body></html>");

	}
}
