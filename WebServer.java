import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.ServletHandler;

public class WebServer {

	private static Object messenger = new Object();

	/**
	 * Starts jetty web server.
	 * 
	 * Blocks until shutdown method is called by a servlet.
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {

		Server server = new Server();
		Connector connector = new SocketConnector();
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });

		ServletHandler handler = new ServletHandler();

		server.setHandler(handler);

		handler.addServletWithMapping("SearchServlet", "/");
		handler.addServletWithMapping("ResultsServlet", "/results");
		handler.addServletWithMapping("LoginServlet", "/login");
		handler.addServletWithMapping("RegisterServlet", "/register");
		handler.addServletWithMapping("AccountServlet", "/account");
		handler.addServletWithMapping("AdminServlet", "/admin");
		handler.addServletWithMapping("SaveLinkServlet", "/savelink");

		server.start();

		synchronized (messenger) {
			messenger.wait();
		}
		Thread.yield();
		connector.close();
		server.stop();
	}

	/**
	 * Call method from another thread to shut down server.
	 */
	public static void stop() {
		synchronized (messenger) {
			messenger.notify();
		}
	}
}
