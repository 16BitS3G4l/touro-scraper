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

        // We need to color/paint the root
        syncedPaintedUrls.add("https://www.touro.edu/");

        // This will not find anything and then just break out. find a better way.
        // to make sure that the scraper will start.
        Thread.sleep(10000);
//        System.out.println(downloadedPages);

        CurrentPageResult mostCurrentPageResult = new CurrentPageResult();

        ScrapeHtml scrapeHtml = new ScrapeHtml(urlsToDownload, downloadedPages, syncedPaintedUrls, mostCurrentPageResult);
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
