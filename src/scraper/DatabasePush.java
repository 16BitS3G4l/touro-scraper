package scraper;

import java.sql.*;
import java.util.ArrayList;

public class DatabasePush {

    private Connection connection;

    public DatabasePush(Connection connection) {
        this.connection = connection;
    }

    public void insertScrapedPage(String url, ArrayList<String> emails, ArrayList<String> dates, ArrayList<String> externalLinks, ArrayList<String> internalLinks, ArrayList<String> facebookLinks, ArrayList<String> phoneNumbers) throws SQLException {
        String query = "INSERT INTO ScrapedPage VALUES(?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setString(1, url);
        preparedStatement.setString(2, String.join(",", emails));
        preparedStatement.setString(3, String.join(",", dates));
        preparedStatement.setString(4, String.join(",", externalLinks));
        preparedStatement.setString(5, String.join(",", internalLinks));
        preparedStatement.setString(6, String.join(",", facebookLinks));
        preparedStatement.setString(7, String.join(",", phoneNumbers));

        preparedStatement.execute();
    }
}
