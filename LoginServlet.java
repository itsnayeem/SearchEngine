import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Display login box
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String logout = request.getParameter("logout");
		String error = request.getParameter("error");

		PrintWriter out = response.getWriter();

		if (logout != null && logout.equals("logout")) {
			response.addCookie(new Cookie("logged_in", "0"));
			response.addCookie(new Cookie("username", ""));
		}

		printLogin(out, error);

	}

	/**
	 * Check to see if login information is correct. If it is, redirect to
	 * search page. If not, allow user to try again.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		PrintWriter out = response.getWriter();

		if (username != null && password != null
				&& UserModel.authUser(username, password)) {
			response.addCookie(new Cookie("logged_in", "true"));
			response.addCookie(new Cookie("username", username));
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
					+ "<html><head><title></title>"
					+ "<meta http-equiv=\"REFRESH\" content=\"0;url=/\">"
					+ "</head><body></body></html>");
		} else {
			printLogin(out, "Invalid Username and/or Password");
		}

	}

	public void printLogin(PrintWriter out, String error) {

		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Search Engine</title>"
				+ "<style lang=\"text/css\">"
				+ "body { text-align: center;}"
				+ "#content {width: 600px;margin: 0 auto;}"
				+ "#box {position: absolute;top: 25%;left: 30%;width: 40%;}"
				+ "#s {width: 90%;}"
				+ "img {border: 0;}"
				+ "</style></head>"
				+ "<body><div id=\"content\"><div id=\"box\">"
				+ "<a href=\"index.html\"><img src=\"http://www2.cs.usfca.edu/~snelrahman/searchsite/logo.png\" "
				+ "alt=\"logo\" /></a>"
				+ " <form action=\"/login\" method=\"post\">"
				+ "<input name=\"username\" id=\"username\" type=\"text\" value=\"username\""
				+ "onclick=\"if (this.value=='username') this.value=''\""
				+ "onblur=\"if (this.value=='') this.value='username')\"/><br />"
				+ "<input name=\"password\" id=\"password\" type=\"password\" /><br />"
				+ "<input type=\"submit\" value=\"Login\" /></form><br />"
				+ "Please Login to use our services!<br />"
				+ "Don't have an account? <a href=\"/register\">Register Here.</a><p>");

		if (error != null)
			out.println(error);
		out.println("</p></div></div></body></html>");

	}
}
