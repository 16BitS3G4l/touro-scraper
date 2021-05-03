package scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class GetHtml implements Runnable {
    final int DEFAULT_SLEEP_TIME = 10000;

    LinkedBlockingQueue<Document> downloadedPages;
    LinkedBlockingQueue<String> urlsToDownload;
    Set<Document> syncedinProgessScrapeHtml;
    Set<String> syncedinProgessGetHtml;

    public GetHtml(LinkedBlockingQueue<String> urlsToDownload, LinkedBlockingQueue<Document> downloadedPages,
                   Set<Document> syncedinProgessScrapeHtml, Set<String> syncedinProgessGetHtml) {
        this.urlsToDownload = urlsToDownload;
        this.downloadedPages = downloadedPages;
        this.syncedinProgessScrapeHtml = syncedinProgessScrapeHtml;
        this.syncedinProgessGetHtml = syncedinProgessGetHtml;
    }

    public void downloadWebPages() {
        while (!urlsToDownload.isEmpty() || !downloadedPages.isEmpty() || !syncedinProgessScrapeHtml.isEmpty() || !syncedinProgessGetHtml.isEmpty()) {
            try {
                Instant start = Instant.now();

                String currentURL = urlsToDownload.poll(3, TimeUnit.SECONDS);
                syncedinProgessGetHtml.add(currentURL);
                Document doc = Jsoup.connect(currentURL).get();

                Instant finish = Instant.now();

                downloadedPages.add(doc);

                long sleepTime = getSleepTime(start, finish);
                Thread.sleep(sleepTime);
                syncedinProgessGetHtml.remove(currentURL);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private long getSleepTime(Instant start, Instant finish) {
        long secondsElapsed = Duration.between(start, finish).toMillis();
        return Math.max(secondsElapsed, DEFAULT_SLEEP_TIME);
    }

    @Override
    public void run() {
        downloadWebPages();
    }


//    Elements links = doc.select("a[href]");
//                System.out.println(links);



// previous download time
//    sleep_time(total_time)
//    download webpage
//    end = time.now
//    total_time = end - start
//  pause_time = 2 * donwload_time or 10 seconds. whichever is greater.

}
