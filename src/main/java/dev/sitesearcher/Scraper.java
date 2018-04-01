package dev.sitesearcher;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;

/**
 * A single thread task that pulls URLs from the queue and 
 * applies simple regex to the response html for a count of matches.
 * @author Jeffrey
 *
 */
public class Scraper implements Runnable {
	
	private String endQ;
	private CountDownLatch latch;

	public Scraper(CountDownLatch latch, String endQ) {
		this.endQ = endQ;
		this.latch = latch;
	}

	public void run() {
		try {
			while (App.queue.peek() != endQ) {
				
				String _url = App.queue.take();
				HttpURLConnection connection = null;
				try {
					connection = (HttpURLConnection) new URL(_url).openConnection();
					connection.setConnectTimeout(3000);
			        connection.setReadTimeout(3000);
			        
					if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
						App.WriteResult("[ERROR] bad return code for [" + _url + "], " + connection.getResponseCode());
						continue;
					}

					String html = "";
					try(Scanner scanner = new Scanner(connection.getInputStream())) {
						scanner.useDelimiter("\\Z");
						html = scanner.next();
					}

					Matcher htmlMatcher = App.searchPattern.matcher(html);
					int matches = 0;
					while (htmlMatcher.find()) {
						matches++;
					}
					App.WriteResult("[INFO] "+Thread.currentThread().getName()+" {url: "+_url+", matches: "+matches+"}");
					
				} catch (ConnectException e) {
					App.WriteResult("[ERROR] connection exception for " + _url + ", " + e);
				} catch (Exception e) {
					App.WriteResult("[ERROR] unknown exception for " + _url + ", " + e);
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		latch.countDown();
	}
}
