import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SaveLinkServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * When user clicks link, they are directed here. The link is saved then the
	 * user is redirected to there.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		Cookie cookies[] = request.getCookies();
		String username = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("username"))
					username = cookie.getValue();
			}
		}

		String link = request.getParameter("link");

		LinkHistoryModel.addLink(username, link);
		PrintWriter out = response.getWriter();

		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
				+ "<html><head><title></title>"
				+ "<meta http-equiv=\"REFRESH\" content=\"0;url="
				+ link
				+ "\">" + "</head><body></body></html>");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);

	}
}
