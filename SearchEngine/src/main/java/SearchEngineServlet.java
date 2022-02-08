import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// https://commons.apache.org/proper/commons-lang/download_lang.cgi

/**
 * The servlet class responsible for setting up a simple message board.
 *
 * 
 */
public class SearchEngineServlet extends HttpServlet {

	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202040;

	/** The title to use for this webpage. */
	private static final String TITLE = "Genesis Search";

	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();

	/** The thread-safe data structure to use for storing messages. */
	private final Queue<String> messages;

	/** Template for HTML. **/
	private final String htmlTemplate;

	/**
	 * Thread safe index.
	 */
	private final ThreadSafeIndex index;

	/**
	 * The input line from the user.
	 */
	private static String word;

	/**
	 * Initializes this message board. Each message board has its own collection of
	 * messages.
	 * 
	 * @param index thread safe index
	 * @throws IOException if unable to read template
	 */
	public SearchEngineServlet(ThreadSafeIndex index) throws IOException {
		super();
		this.index = index;
		messages = new LinkedList<>();
		htmlTemplate = Files.readString(Path.of("html", "index.html"), StandardCharsets.UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		// used to substitute values in our templates
		Map<String, String> values = new HashMap<>();
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());

		// setup form
		values.put("method", "POST");
		values.put("action", request.getServletPath());

		// compile all of the messages together
		// keep in mind multiple threads may access this at once!
		values.put("messages", String.join("\n\n", messages));

		// generate html from template
		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(htmlTemplate);

		// output generated html
		PrintWriter out = response.getWriter();
		if (word != null) {
			String[] words = word.split(" ");
			Set<String> list = new HashSet<String>();
			for (String word : words) {
				System.out.println(word);
				list.add(word);
			}

			ArrayList<InvertedIndex.Result> results = index.partialSearch(list);
			for (InvertedIndex.Result result : results) {
				out.println(String.format("<p><a href=\"%s\">%s</a></p>", result.getTotal(), result.getLocation(),
						result.getLocation()));
				// out.println(result);

			}
		}
		out.println(html);
		out.flush();

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String search = request.getParameter("search");

		search = search == null ? "" : search;

		// avoid xss attacks using apache commons text
		// comment out if you don't have this library installed
		search = StringEscapeUtils.escapeHtml4(search);
		word = search;

		String formatted = String.format("<p>%s<br><font size=\"-2\">[ posted at %s ]</font></p>", search, getDate());

		// keep in mind multiple threads may access at once
		// but we are using a thread-safe data structure here to avoid any issues
		synchronized (messages) {
			messages.add(formatted);

			// only keep the latest 5 messages
			if (messages.size() > 5) {
				String first = messages.poll();
				log.info("Removing message: " + first);
			}
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}
