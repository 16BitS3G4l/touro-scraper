package scraper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            //add internalLinks to urlsToDownload queue, iterate over list adn add to queue
            for (String url : internalLinks) {
                urlsToDownload.add(url);
            }
            // set internalLinks to currentPage's internalLinks in sync block
            
            //external urls
            ArrayList<String> externalLinks = getExternalLinks(pageToParse);
            // set externalLinks to currentPage's externalLinks in sync block

            ArrayList<String> phoneNumbers = getPhoneNumbers(pageToParse);
            //set phone numbers to currentPages phon number list in sync block

            ArrayList<String> emails = getEmails(pageToParse);      

            //parse for two non trivial
        }
    }

    private ArrayList<String> getEmails(Document pageToParse) {
        if(pageToParse == null){
            return null;
        }
        ArrayList<String> emailsAddresses = new ArrayList<>();

        String regex_num = "[a-zA-Z\\d.]+@[a-zA-Z\\d.]+\\w{3}";
        Pattern pattern = Pattern.compile(regex_num);

        // get phone numbers on page with regex
        Elements emails = pageToParse.getElementsMatchingOwnText(pattern);
        
        if(!emails.isEmpty()){
            for (Element e : emails) {
                Matcher matcher = pattern.matcher(e.text());
                while(matcher.find()) {
                    emailsAddresses.add(matcher.group(0));
                }
            }
        }
        return emailsAddresses;
    }

    private ArrayList<String> getPhoneNumbers(Document pageToParse) {
        if(pageToParse == null){
            return null;
        }
        ArrayList<String> phoneNumbers = new ArrayList<>();

        String regex_num = "\\(\\d{3}\\)(-| )\\d{3}(-| )\\d{3}";
        Pattern pattern = Pattern.compile(regex_num);

        // get phone numbers on page with regex
        Elements numbers = pageToParse.getElementsMatchingOwnText(pattern);
        
        if(!numbers.isEmpty()){
            for (Element num : numbers) {
                Matcher matcher = pattern.matcher(num.text());
                while(matcher.find()) {
                    phoneNumbers.add(matcher.group(0));
                }
            }
        }
        return phoneNumbers;
        
    }

    private ArrayList<String> getExternalLinks(Document pageToParse) {
        if(pageToParse == null){
            return null;
        }
        Elements links = pageToParse.select("a[href]");
        if(links.isEmpty()){
            return null;
        }
        ArrayList<String> externalUrls = new ArrayList<>();
        links.stream().map((link) -> link.attr("abs:href")).forEachOrdered((url) -> {
            url = cleanedURL(url);
            if(isValidExternalUrl(url)){
                //do we want to print this out? We will print out currentPage class
                // System.out.println(url);
                externalUrls.add(url);
            }
        });
        return externalUrls;
    }

    private boolean isValidExternalUrl(String url) {
        return url.matches("https://www.(?!touro.edu)[a-zA-Z0-9./]*");
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
                //confusing that this func doesnt just get links rather actually adds them to urlsToDownload queue
                internalUrls.add(url);
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
            // I think regex would be the better approach here
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


    // We need a synchronized class currentPage that will be overriden at the end of each thread. 
    // This will keep live results of what page we are up to and what internal links, external links, phone number, and emails
    // are on that page. This class will also have a current page count.


}
