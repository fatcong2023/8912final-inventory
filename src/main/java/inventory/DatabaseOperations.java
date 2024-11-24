package inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DatabaseOperations {
    private static final String CONNECTION_URL = "jdbc:sqlserver://8912finallab.database.windows.net:1433;database=xiao8915;user=xiao@8912finallab;password=Zz300312!;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQL Server JDBC driver", e);
        }
    }

    public static List<String> getDistinctStates(Logger logger) throws Exception {
        List<String> states = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(CONNECTION_URL)) {
            String querySql = "SELECT DISTINCT State FROM BloodInventory";
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    states.add(resultSet.getString("State"));
                }
            }
        } catch (Exception e) {
            logger.severe("Database query error: " + e.getMessage());
            throw e;
        }
        return states;
    }
}