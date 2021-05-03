package scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Program {


    // https://stackoverflow.com/questions/616484/how-to-use-concurrentlinkedqueue/616505
    public static void main(String[] args) throws InterruptedException, SQLException {
        final String START_URL = "https://www.touro.edu/";

        Connection conn = null;
        DatabasePull dbPull = null;

        try {
            // db parameters
            String url = "jdbc:sqlite:testing.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            dbPull = new DatabasePull(conn);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        LinkedBlockingQueue<Document> downloadedPages = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<String> urlsToDownload = new LinkedBlockingQueue<>();

        Set<String> paintedUrls = dbPull.getPaintedUrls();

        if(paintedUrls == null)
            paintedUrls = new HashSet<>();

        Set<String> syncedPaintedUrls = Collections.synchronizedSet(paintedUrls);   // all actions should be done through this one.

        Set<Document> inProgessScrapeHtml = new HashSet<>();
        Set<Document> syncedinProgessScrapeHtml = Collections.synchronizedSet(inProgessScrapeHtml);

        Set<String> inProgessGetHtml = new HashSet<>();
        Set<String> syncedinProgessGetHtml = Collections.synchronizedSet(inProgessGetHtml);

        urlsToDownload.offer(START_URL);
        syncedPaintedUrls.add(START_URL);

        GetHtml getHtml = new GetHtml(urlsToDownload, downloadedPages, syncedinProgessScrapeHtml, syncedinProgessGetHtml);
        new Thread(getHtml).start();

        // This will not find anything and then just break out. find a better way.
        // to make sure that the scraper will start.
        Thread.sleep(1000);

        CurrentPageResult mostCurrentPageResult = new CurrentPageResult();
        DatabasePush databasePush = new DatabasePush(conn);

        ScrapeHtml scrapeHtml = new ScrapeHtml(urlsToDownload, downloadedPages, syncedPaintedUrls, mostCurrentPageResult, syncedinProgessScrapeHtml, syncedinProgessGetHtml, databasePush);
        Thread thread1 = new Thread(scrapeHtml);
        Thread thread2 = new Thread(scrapeHtml);
        thread1.start();
        thread2.start();


        while(!mostCurrentPageResult.isFinalPage()) {

            synchronized(mostCurrentPageResult) {
                if (mostCurrentPageResult.isChanged()) {

                    // delete the previous results and display the most current ones
                    // we can clear the terminal and then print our new results (option 1)
                    for(int i = 0; i < 100; i++)
                        System.out.println();

                    System.out.println("URL of Webpage: ");
                    System.out.println(mostCurrentPageResult.getLocation());

                    synchronized (mostCurrentPageResult) {
                        mostCurrentPageResult.setChanged(false);
                    }
                }
            }
        }
    }
}
