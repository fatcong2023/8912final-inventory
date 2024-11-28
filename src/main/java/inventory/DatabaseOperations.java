package inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static Map<String, List<String>> getStatesAndCities(Logger logger) throws Exception {
        Map<String, List<String>> statesAndCities = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(CONNECTION_URL)) {
            String querySql = "SELECT distinct State, City FROM BloodInventory";
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String state = resultSet.getString("State");
                    String city = resultSet.getString("City");
                    statesAndCities.computeIfAbsent(state, k -> new ArrayList<>()).add(city);
                }
            }
        } catch (Exception e) {
            logger.severe("Database query error: " + e.getMessage());
            throw e;
        }
        return statesAndCities;
    }

    public static List<Map<String, Object>> getDataByStateAndCity(String state, String city, Logger logger) throws Exception {
        List<Map<String, Object>> dataList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(CONNECTION_URL)) {
            String querySql = "SELECT ABO, Rh, hundredccCount, State, City, BankNumber, Address, universalBankNumber FROM BloodInventory WHERE State = ? AND City = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySql)) {
                preparedStatement.setString(1, state);
                preparedStatement.setString(2, city);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("ABO", resultSet.getString("ABO"));
                        dataMap.put("Rh", resultSet.getString("Rh"));
                        dataMap.put("hundredccCount", resultSet.getLong("hundredccCount"));
                        dataMap.put("State", resultSet.getString("State"));
                        dataMap.put("City", resultSet.getString("City"));
                        dataMap.put("BankNumber", resultSet.getString("BankNumber"));
                        dataMap.put("Address", resultSet.getString("Address"));
                        dataMap.put("universalBankNumber", resultSet.getString("universalBankNumber"));
                        dataList.add(dataMap);
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("Database query error: " + e.getMessage());
            throw e;
        }
        return dataList;
    }

    public static void updateHundredccCount(String state, String city, String bankNumber, String ABO, String Rh, long hundredccCount, Logger logger) throws Exception {
        try (Connection connection = DriverManager.getConnection(CONNECTION_URL)) {
            String updateSql = "UPDATE BloodInventory SET hundredccCount = ? WHERE State = ? AND City = ? AND BankNumber = ? AND ABO = ? AND Rh = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
                preparedStatement.setLong(1, hundredccCount);
                preparedStatement.setString(2, state);
                preparedStatement.setString(3, city);
                preparedStatement.setString(4, bankNumber);
                preparedStatement.setString(5, ABO);
                preparedStatement.setString(6, Rh);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            logger.severe("Database update error: " + e.getMessage());
            throw e;
        }
    }
    
}