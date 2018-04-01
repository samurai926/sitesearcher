package dev.sitesearcher;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.sitesearcher.App;
import dev.sitesearcher.Scraper;

public class AppTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		App.LoadResourceFile();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateQueue() {
		App.CreateQueue();
		int result = App.queue.size();
		assertEquals(result, 501);
	}

	@Test
	public void testRunScrapers() {
		App.CompileSearchPattern();
		App.queue = new LinkedBlockingQueue<String>(2);
		App.queue.add("https://www.google.com/");
		App.queue.add("endQ");
		CountDownLatch latch = new CountDownLatch(1);
		new Thread(new Scraper(latch, "endQ")).start();
		try {
			latch.await();
			System.out.println("Check results.txt for test result.  Expecting:");
			System.out.println("[INFO] Thread-0 {url: https://www.google.com/, matches: 3}");
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testWriteResult_CREATE() {
		// TODO delete the file first
		App.WriteResult("test appending to output when results.txt does not exist");
	}

	@Test
	public void testWriteResult_APPEND() {
		// TODO ensure the file already exists
		App.WriteResult("test appending to output when results.txt DOES exist");
	}
	
	@Test
	public void testCompileSearchPattern() {
		App.CompileSearchPattern();
		assertSame(App.searchPattern.toString(), App.REGEX);
	}

	@Test
	public void testLoadResourceFile() {
		assertNotNull(App.urls);
	}

}
