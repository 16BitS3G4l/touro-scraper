package scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class DatabasePull {
    private Connection connection = null;

    public DatabasePull(Connection connection) {
        this.connection = connection;
    }

    public LinkedBlockingQueue getDownloadedPages() throws SQLException {
        String query = "SELECT * FROM downloadedPages;";

        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery(query);

        if(results == null)
            return null;

        LinkedBlockingQueue<Document> downloadedPages = new LinkedBlockingQueue<>();

        while(results.next()) {
                String location = results.getString("URL");
                String html = results.getString("HTML");

                Document document = Jsoup.parse(html, location);
                downloadedPages.add(document);
        }

        return downloadedPages;
    }

    public LinkedBlockingQueue getUrlsToDownload() throws SQLException {
        String query = "SELECT * FROM urlsToDownload;";

        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery(query);

        if(results == null)
            return null;

        LinkedBlockingQueue<String> urlsToDownload = new LinkedBlockingQueue<>();

        while(results.next()) {
            urlsToDownload.add(results.getString("URL"));
        }

        return urlsToDownload;
    }

    public Set<String> getPaintedUrls() throws SQLException {
        String query = "SELECT * FROM paintedUrls;";

        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery(query);

        if(results == null)
            return null;

        Set<String> paintedUrls = new HashSet<>();

        while(results.next()) {
            paintedUrls.add(results.getString("URL"));
        }

        return paintedUrls;
    }
}
