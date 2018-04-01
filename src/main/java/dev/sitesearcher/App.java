package dev.sitesearcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

/**
 * Website-Searcher application entry point
 * @author Jeffrey
 *
 */
public class App {

	public static final int MAX_CONSUMER_THREADS = 20;						// Default to 20 per requirements
	public static final String DEFAULT_URLS_FILE = "urls.txt";				// The input file contains the URLs
	public static final String REGEX = "\\b(today|outside|more)\\b";		// Your search term(s) here
	public static final String RESULTS_FILE = "results.txt";				// The output file contains match counts

	public static BlockingQueue<String> queue;
	public static Pattern searchPattern;
	protected static Vector<String> urls;
	private static long startTime;
	private static final Path resultsFilePath = Paths.get(RESULTS_FILE);

	public static void main(String[] args) {
		
		startTime = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(MAX_CONSUMER_THREADS);		
		System.out.println("------------------------------------------------------");
		System.out.println("Starting program with regex: " + REGEX);
		
		LoadResourceFile();			// Reads urls.txt records into a vector
		CompileSearchPattern();		// Compile our search regex pattern
		CreateQueue();				// Fill our BlockingQueue with the URLs
		RunScrapers(latch);			// Populate the queue and start the consumer threads!
		
		System.out.println("Threads are working, hold tight...");
		try { 
			latch.await();
		} catch (InterruptedException e) {
			System.err.println("[ERROR] App.main " + e.getMessage());
		}
		
		System.out.println("Finished in " + String.valueOf((System.currentTimeMillis() - startTime)/1000.0) + " seconds!");
		System.out.println("Results file is here: " + resultsFilePath.toAbsolutePath());
		System.out.println("------------------------------------------------------");
		WriteResult(System.lineSeparator() + System.lineSeparator());
	}
	
	protected static void CreateQueue() {
		System.out.println("Reading urls.txt data...");
		queue = new LinkedBlockingQueue<String>(urls.size()+1);
		queue.addAll(App.urls);
		queue.add("endQ");
	}
	
	protected static void RunScrapers(CountDownLatch latch) {
		System.out.println("Starting " + MAX_CONSUMER_THREADS + " worker threads...");
		for (int j = 0; j < MAX_CONSUMER_THREADS; j++) {
			new Thread(new Scraper(latch, "endQ")).start();
		}
	}
	
	public static synchronized void WriteResult(String line) {
		try (BufferedWriter writer = Files.newBufferedWriter(resultsFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
		    writer.write(line);
		    writer.newLine();
		    writer.flush();
		} catch (IOException e) {
			System.err.println("[ERROR] App.WriteResult " + e.getMessage());
		}
	}
	
	protected static void CompileSearchPattern() {
		searchPattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
	}

	protected static void LoadResourceFile() {
		ClassLoader mainClassLoader = Thread.currentThread().getContextClassLoader();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(mainClassLoader.getResourceAsStream(DEFAULT_URLS_FILE)))) {
			urls = new Vector<String>();
			String line = null;
			boolean isHeader = true;
			while ((line = reader.readLine()) != null) {
				if (!isHeader) {
					urls.addElement("https://www." + line.split(",")[1].replace("\"", ""));
				}
				isHeader = false;
			}
		} catch (IOException e) {
			System.err.println("[ERROR] App.LoadResourceFile " + e.getMessage());
		}
	}

}
