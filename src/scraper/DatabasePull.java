package scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class DatabasePull {
    private Connection connection = null;

    public DatabasePull(Connection connection) {
        this.connection = connection;
    }

    public Set<String> getPaintedUrls() throws SQLException {
        String query = "SELECT URL FROM ScrapedPage;";

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
