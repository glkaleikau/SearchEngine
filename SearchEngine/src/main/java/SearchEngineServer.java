import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Demonstrates how to create a simple message board using Jetty and servlets,
 * as well as how to initialize servlets when you need to call its constructor.
 */
public class SearchEngineServer {

	/** Initialize port to 8080 */
	public int PORT = 0;
	/**
	 * 
	 */
	private final ThreadSafeIndex index;

	/**
	 * Default constructor.
	 * 
	 * @param index thread safe index
	 * @param PORT  the port for the server
	 */
	// public SearchEngineServer(ThreadSafeIndex index) {
	// this.index = index;
	// }
	public SearchEngineServer(ThreadSafeIndex index, int PORT) {
		this.index = index;
		this.PORT = PORT;
	}

	/**
	 * Sets up a Jetty server with different servlet instances.
	 *
	 * 
	 * @throws Exception if unable to start and run server
	 */
	public void run() throws Exception {
		Server server = new Server(PORT);

		ServletHandler handler = new ServletHandler();

		// must use servlet holds when need to call a constructor
		handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet(index)), "/search");

		server.setHandler(handler);
		server.start();
		server.join();
	}
}
