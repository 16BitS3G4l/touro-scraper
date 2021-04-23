package scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class GetHtml {
    final int DEFAULT_SLEEP_TIME = 10000;

    LinkedBlockingQueue<Document> downloadedPages;
    LinkedBlockingQueue<String> urlsToDownload;


    public GetHtml(LinkedBlockingQueue<String> urlsToDownload, LinkedBlockingQueue<Document> downloadedPages) {
        this.urlsToDownload = urlsToDownload;
        this.downloadedPages = downloadedPages;
    }

    public void downloadWebPages() {
        while (!urlsToDownload.isEmpty()) {
            try {
                // Note this will wait forever, if there is not element
                Instant start = Instant.now();
                String currentURL = urlsToDownload.poll(3, TimeUnit.SECONDS);
                Document doc = Jsoup.connect(currentURL).get();
                //System.out.println("Getting HTML for: " + currentURL);
                Instant finish = Instant.now();
                long sleepTime = getSleepTime(start, finish);
                downloadedPages.add(doc);
                //System.out.println("Sleeping for miliseconds: " + sleepTime);
                Thread.sleep(sleepTime);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private long getSleepTime(Instant start, Instant finish) {
        long secondsElapsed = Duration.between(start, finish).toMillis();
        return Math.max(secondsElapsed, DEFAULT_SLEEP_TIME);
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
