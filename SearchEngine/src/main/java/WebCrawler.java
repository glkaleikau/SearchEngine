import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/***
 * The utility class for parsing links to html.**
 * 
 * @author genesiskaleikau
 *
 */
public class WebCrawler {
	/**
	 * The seed for the html.
	 */
	private final String seed;
	/**
	 * The max number of workers.
	 */
	private int max;
	/**
	 * The thread safe index.
	 */
	private final ThreadSafeIndex index;
	/**
	 * The set of URLs that have been visited already.
	 */
	private final Set<URL> urls;

	/**
	 * The work queue.
	 */
	private final WorkQueue workqueue;

	/**
	 * Constructor for WebCrawler.
	 *
	 * @param threadSafeIndex the thread safe index
	 * @param max             the max number of URL's to visit
	 * @param threads         the number of threads to run
	 * @param seed            the URL
	 */
	public WebCrawler(ThreadSafeIndex threadSafeIndex, int max, int threads, String seed) {
		this.index = threadSafeIndex;
		this.max = max;
		this.seed = seed;
		this.urls = new HashSet<URL>();
		this.workqueue = new WorkQueue(threads);

	}

	/**
	 * @param url the URL to visit
	 * @throws MalformedURLException if an error with the URL occurs
	 * @throws URISyntaxException    if an syntax error occurs
	 */
	public void crawlUrl(URL url) throws MalformedURLException, URISyntaxException {
		String html = HtmlFetcher.fetch(url, 3);
		if (html == null || max == 0 || urls.size() > max) {
			return;
		}
		html = HtmlCleaner.stripBlockElements(html);
		ArrayList<URL> urlList = LinkParser.getValidLinks(url, html);

		for (URL nextUrl : urlList) {
			synchronized (urls) {
				if (urls.size() >= max) {
					break;
				}
				if (!urls.contains(nextUrl) && urls.size() < max) {
					urls.add(nextUrl);
					workqueue.execute(new Task(nextUrl));
				}

			}
		}

		html = HtmlCleaner.stripTags(html);
		html = HtmlCleaner.stripEntities(html);

		InvertedIndex inverted = new InvertedIndex();
		int counter = 0;
		String location = url.toString();
		for (String word : TextFileStemmer.listStems(html)) {
			counter++;
			inverted.add(word, location, counter);

		}

		// System.out.println(inverted);

		index.addAll(inverted);

	}

	/**
	 * @throws MalformedURLException if an error with the URL occurs
	 * @throws URISyntaxException    if a syntax error occurs
	 */
	public void crawl() throws MalformedURLException, URISyntaxException {
		URL current = LinkParser.normalize(new URL(seed));
		urls.add(current);
		workqueue.execute(new Task(current));
		workqueue.join();

	}

	/**
	 * @author genesiskaleikau
	 *
	 */
	private class Task implements Runnable {

		/**
		 * The URL.
		 */
		URL url;

		/**
		 * Constructor for work threads.
		 *
		 * @param url the URL to crawl
		 */
		public Task(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			try {
				crawlUrl(url);

			} catch (MalformedURLException | URISyntaxException e) {
				e.printStackTrace();
			}

		}

	}

}