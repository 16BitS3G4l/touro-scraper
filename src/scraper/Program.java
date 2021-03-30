package scraper;

import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Program {

    // https://stackoverflow.com/questions/616484/how-to-use-concurrentlinkedqueue/616505
    public static void main(String[] args) throws InterruptedException {
        final String START_URL = "https://www.touro.edu/";
        LinkedBlockingQueue<Document> downloadedPages = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<String> urlsToDownload = new LinkedBlockingQueue<>();
        Set<String> paintedUrls = new HashSet<>();
        Set<String> syncedPaintedUrls = Collections.synchronizedSet(paintedUrls);   // all actions should be done through this one.
        urlsToDownload.offer(START_URL);
        syncedPaintedUrls.add(START_URL);

        new Thread() {
            @Override
            public void run() {
                GetHtml getHtml = new GetHtml(urlsToDownload, downloadedPages);
                getHtml.downloadWebPages();
            }
        }.start();

        // This will not find anything and then just break out. find a better way.
        // to make sure that the scraper will start.
        Thread.sleep(10000);
//        System.out.println(downloadedPages);
        ScrapeHtml scrapeHtml = new ScrapeHtml(urlsToDownload, downloadedPages, syncedPaintedUrls);
        Thread thread1 = new Thread(scrapeHtml);
        Thread thread2 = new Thread(scrapeHtml);
        thread1.start();
        thread2.start();
    }
}
