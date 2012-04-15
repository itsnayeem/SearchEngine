import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * If user is not logged in, allow them to register
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		boolean loggedIn = false;
		Cookie cookies[] = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("logged_in")
						&& cookie.getValue().equals("true"))
					loggedIn = true;
			}
		}

		PrintWriter out = response.getWriter();

		if (loggedIn) {
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
					+ "<html><head><title></title>"
					+ "<meta http-equiv=\"REFRESH\" content=\"0;url=/\">"
					+ "</head><body></body></html>");
		} else {
			printLogin(out, "");
		}
	}

	/**
	 * Validate registration information and add user to database. Redirect to
	 * login.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		boolean loggedIn = false;
		Cookie cookies[] = request.getCookies();
		PrintWriter out = response.getWriter();
		String error = "";

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("logged_in")
						&& cookie.getValue().equals("true"))
					loggedIn = true;
			}
		}

		if (loggedIn) {
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
					+ "<html><head><title></title>"
					+ "<meta http-equiv=\"REFRESH\" content=\"0;url=/\">"
					+ "</head><body></body></html>");
		} else {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String confirmpass = request.getParameter("confirmpass");

			if (username == null || password == null || confirmpass == null) {
				error = "Username and/or password fields are empty";
				printLogin(out, error);
				return;
			} else if (!password.trim().equals(confirmpass.trim())) {
				error = "Passwords don't match";
				printLogin(out, error);
				return;
			}

			UserModel.addUser(username, password);
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
					+ "<html><head><title></title>"
					+ "<meta http-equiv=\"REFRESH\" content=\"0;url=/login\">"
					+ "</head><body></body></html>");
		}

	}

	public void printLogin(PrintWriter out, String error) {

		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
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
				+ " <form action=\"/register\" method=\"post\">"
				+ "<input name=\"username\" id=\"username\" type=\"text\" value=\"username\""
				+ "onclick=\"if (this.value=='username') this.value=''\""
				+ "onblur=\"if (this.value=='') this.value='username')\"/><br />"
				+ "Password:<br />"
				+ "<input name=\"password\" id=\"password\" type=\"password\" /><br />"
				+ "Confirm Password:<br />"
				+ "<input name=\"confirmpass\" id=\"confirmpass\" type=\"password\" /><br />"
				+ "<input type=\"submit\" value=\"Register\" /></form><br />"
				+ error
				+ "<br />Already a member? <a href=\"/login\">Login Here.</a> "
				+ "</div></div></body></html>");

	}
}
