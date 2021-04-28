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

        LinkedBlockingQueue<Document> downloadedPages = dbPull.getDownloadedPages();

        if(downloadedPages == null)
            downloadedPages = new LinkedBlockingQueue<>();


        LinkedBlockingQueue<String> urlsToDownload = dbPull.getUrlsToDownload();

        if(urlsToDownload == null)
            urlsToDownload = new LinkedBlockingQueue<>();


        Set<String> paintedUrls = dbPull.getPaintedUrls();

        if(paintedUrls == null)
            paintedUrls = new HashSet<>();

        Set<String> syncedPaintedUrls = Collections.synchronizedSet(paintedUrls);   // all actions should be done through this one.


        Set<Document> inProgessScrapeHtml = new HashSet<>();
        Set<Document> syncedinProgessScrapeHtml = Collections.synchronizedSet(inProgessScrapeHtml);

        Set<String> inProgessGetHtml = new HashSet<>();
        Set<String> syncedinProgessGetHtml = Collections.synchronizedSet(inProgessGetHtml);

        // Before doing anything, we need to load all data from the database,
        // if it already has data, we need to load it, otherwise just work on the current data
        // and inset data, when necessary, into the database so if we interrupt execution, we can
        // immediately resume progress from the most recent checkpoint.

        // We need to identify what data we will store in the database (so what data we need to retrieve, and
        // what data we need to update when we change it.

        urlsToDownload.offer(START_URL);
        syncedPaintedUrls.add(START_URL);

        GetHtml getHtml = new GetHtml(urlsToDownload, downloadedPages, syncedinProgessScrapeHtml, syncedinProgessGetHtml);
        new Thread(getHtml).start();

        // We need to color/paint the root
        syncedPaintedUrls.add("https://www.touro.edu/");

        // This will not find anything and then just break out. find a better way.
        // to make sure that the scraper will start.
        Thread.sleep(1000);

        CurrentPageResult mostCurrentPageResult = new CurrentPageResult();

        ScrapeHtml scrapeHtml = new ScrapeHtml(urlsToDownload, downloadedPages, syncedPaintedUrls, mostCurrentPageResult, syncedinProgessScrapeHtml, syncedinProgessGetHtml);
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

        // final page work

    }
}
