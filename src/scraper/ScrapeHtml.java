package scraper;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ScrapeHtml implements Runnable {


    LinkedBlockingQueue<Document> downloadedPages;
    LinkedBlockingQueue<String> urlsToDownload;
    Set<String> paintedUrls = new HashSet<>();
    Set<String> syncedPaintedUrls;// all actions should be done through this one.
    private String[] internalSites = new String[] {"touro.edu", "whatElseGoesHere"};

    public ScrapeHtml(LinkedBlockingQueue<String> urlsToDownload, LinkedBlockingQueue<Document> downloadedPages, Set<String> syncedPaintedUrls) {
        this.urlsToDownload = urlsToDownload;
        this.downloadedPages = downloadedPages;
        this.syncedPaintedUrls = syncedPaintedUrls;
    }

    @Override
    public void run() {
        while (!downloadedPages.isEmpty() || !urlsToDownload.isEmpty()) {
            Document pageToParse = null;
            try {
                pageToParse = downloadedPages.poll(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Page to parse: ");
            // Do ALL the parsing here
            ArrayList<String> internalLinks = getInternalLinks(pageToParse);
            // addInternalLinksToQueue(internalLinks);
        }
    }

    private ArrayList<String> getInternalLinks(Document pageToParse) {
        if (pageToParse == null) return null;
        Elements links = pageToParse.select("a[href]");
        if (links.isEmpty())
            return null;
        ArrayList<String> internalUrls = new ArrayList<>();
        links.stream().map((link) -> link.attr("abs:href")).forEachOrdered((url) -> {
            url = cleanedURL(url);
            boolean add = syncedPaintedUrls.add(url);
            if (add && isValidInternalURL(url)) {
                System.out.println(url);
                urlsToDownload.add(url);
            }
        });
        return internalUrls;
    }

    private String cleanedURL(String url) {
        // Remove the anchor tag. to avoid scraping the same page twice
        url = url.split("#")[0];
        // Clean out emails. this sometimes can come in the mix somehow
        return url;
    }

    private boolean isValidInternalURL(String url) {
        for(String listItem : internalSites)
            if(url.contains(listItem) && !url.contains("@"))
                return true;
        return false;
    }


    // lock the queue
    // get the next html from the queue
    // release lock
    // parse the html
    // add internal urls to the queue.
    //      only if it is not already in the painted set..
    //      lock on when adding to the painted set


}
